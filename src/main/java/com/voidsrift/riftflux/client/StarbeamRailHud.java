package com.voidsrift.riftflux.client;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.dualhotbar.RenderHandler;
import com.voidsrift.riftflux.net.MsgStarbeamRailJump;
import com.voidsrift.riftflux.net.RFNetwork;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.nmccoy.legendgear.legacy.entities.EntityGrindStar;
import org.lwjgl.opengl.GL11;

public final class StarbeamRailHud extends Gui {
    private static final StarbeamRailHud INSTANCE = new StarbeamRailHud();
    private static boolean bootstrapped;
    private static boolean lastJumpPressed;
    private static final String JUMP_PROMPT = "Jump to launch!";
    private static final int LINE_HEIGHT = 10;
    private static final int JUMP_DISMOUNT_GRACE_TICKS = 5;

    private StarbeamRailHud() {
    }

    public static void bootstrap() {
        if (bootstrapped) {
            return;
        }
        bootstrapped = true;
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        FMLCommonHandler.instance().bus().register(INSTANCE);
    }

    @SubscribeEvent
    public void onHud(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null || mc.gameSettings == null || mc.gameSettings.hideGUI) {
            return;
        }

        EntityPlayer player = mc.thePlayer;
        boolean mountPromptVisible = RenderHandler.isMountOnboardOverlayVisible(mc, player);
        boolean showJumpPrompt = player.ridingEntity instanceof EntityGrindStar && mountPromptVisible;
        boolean showCustomMountPrompt = ModConfig.dualHotbarUseCustomMountOnboardPrompt
                && mountPromptVisible
                && player.ridingEntity != null;
        if (!showJumpPrompt && !showCustomMountPrompt) {
            return;
        }

        int opacity = this.getOverlayOpacity(RenderHandler.getOverlayMessageTicks(mc), event.partialTicks);
        if (opacity <= 0) {
            return;
        }

        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int topY = RenderHandler.getCenteredOverlayTextTopY(mc, player);
        if (topY == Integer.MIN_VALUE) {
            topY = res.getScaledHeight() - 72;
        }
        int jumpY = showCustomMountPrompt ? topY - LINE_HEIGHT - 2 : topY;
        int promptY = topY;

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        if (showJumpPrompt) {
            this.drawCentered(mc, res, JUMP_PROMPT, jumpY, (opacity << 24) | 0xFFFFFF);
        }
        if (showCustomMountPrompt) {
            this.drawCentered(mc, res, this.getMountPrompt(mc), promptY, (opacity << 24) | 0xFFFFFF);
        }
        GL11.glPopAttrib();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null || mc.gameSettings == null) {
            lastJumpPressed = false;
            return;
        }

        boolean jumpPressed = mc.currentScreen == null && mc.gameSettings.keyBindJump.getIsKeyPressed();
        if (mc.thePlayer.ridingEntity instanceof EntityGrindStar
                && jumpPressed
                && !lastJumpPressed
                && RFNetwork.CH != null) {
            this.startStarbeamLaunchParticles(mc.thePlayer);
            RFNetwork.CH.sendToServer(new MsgStarbeamRailJump(mc.thePlayer.ridingEntity.getEntityId()));
        }

        lastJumpPressed = jumpPressed;
    }

    private void startStarbeamLaunchParticles(EntityPlayer player) {
        if (player.ridingEntity == null || player.ridingEntity.ticksExisted <= JUMP_DISMOUNT_GRACE_TICKS) {
            return;
        }

        player.getEntityData().setBoolean(EntityGrindStar.STARBEAM_LAUNCH_PARTICLES, true);
        player.getEntityData().setInteger(EntityGrindStar.STARBEAM_LAUNCH_PARTICLE_START, player.ticksExisted);
    }

    private String getMountPrompt(Minecraft mc) {
        return I18n.format(
                "mount.onboard",
                GameSettings.getKeyDisplayString(mc.gameSettings.keyBindSneak.getKeyCode())
        );
    }

    private int getOverlayOpacity(int remainingTicks, float partialTicks) {
        if (remainingTicks <= 0) {
            return 0;
        }
        float ticks = (float) remainingTicks - partialTicks;
        int opacity = (int) (ticks * 256.0F / 20.0F);
        if (opacity > 255) {
            return 255;
        }
        return Math.max(opacity, 0);
    }

    private void drawCentered(Minecraft mc, ScaledResolution res, String text, int y, int color) {
        int x = (res.getScaledWidth() - mc.fontRenderer.getStringWidth(text)) / 2;
        mc.fontRenderer.drawStringWithShadow(text, x, y, color);
    }
}
