package com.voidsrift.riftflux.palaria.client;

import com.voidsrift.riftflux.asgardshield.AsgardShieldHud;
import com.voidsrift.riftflux.dualhotbar.RenderHandler;
import com.voidsrift.riftflux.net.MsgNimatinJump;
import com.voidsrift.riftflux.net.RFNetwork;
import com.voidsrift.riftflux.palaria.entity.EntityNimatin;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;

public final class NimatinJumpHud extends Gui {
    private static final NimatinJumpHud INSTANCE = new NimatinJumpHud();
    private static final ResourceLocation GUI_ICONS = new ResourceLocation("textures/gui/icons.png");

    private boolean jumpHeld;
    private int jumpPowerCounter = -1;
    private float jumpPower;

    private NimatinJumpHud() {
    }

    public static void bootstrap() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        FMLCommonHandler.instance().bus().register(INSTANCE);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null || mc.gameSettings == null) {
            resetCharge();
            return;
        }

        if (!isDrivingNimatin(mc.thePlayer)) {
            resetCharge();
            return;
        }

        boolean currentlyHeld = mc.gameSettings.keyBindJump.getIsKeyPressed();
        if (jumpPowerCounter < 0) {
            ++jumpPowerCounter;
            if (jumpPowerCounter == 0) {
                jumpPower = 0.0F;
            }
        }

        if (jumpHeld && !currentlyHeld) {
            if (jumpPower > 0.0F && RFNetwork.CH != null) {
                RFNetwork.CH.sendToServer(new MsgNimatinJump((int) (jumpPower * 100.0F)));
            }
            jumpPowerCounter = -10;
            jumpPower = 0.0F;
        } else if (!jumpHeld && currentlyHeld) {
            jumpPowerCounter = 0;
            jumpPower = 0.0F;
        } else if (currentlyHeld) {
            ++jumpPowerCounter;
            if (jumpPowerCounter < 10) {
                jumpPower = (float) jumpPowerCounter * 0.1F;
            } else {
                jumpPower = 0.8F + 2.0F / (float) (jumpPowerCounter - 9) * 0.1F;
            }
            if (jumpPower > 1.0F) {
                jumpPower = 1.0F;
            }
        }

        jumpHeld = currentlyHeld;
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
        if (!isDrivingNimatin(mc.thePlayer)) {
            return;
        }

        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int width = res.getScaledWidth();
        int x = width / 2 - 91;
        int tooltipTopY = RenderHandler.getHeldItemTooltipTopY(mc, mc.thePlayer);
        int y = tooltipTopY == Integer.MIN_VALUE
                ? res.getScaledHeight() - 67 - AsgardShieldHud.getDualHotbarShiftY()
                : tooltipTopY - 9;
        int fill = (int) (jumpPower * 183.0F);

        mc.getTextureManager().bindTexture(GUI_ICONS);
        drawTexturedModalRect(x, y, 0, 84, 182, 5);
        if (fill > 0) {
            drawTexturedModalRect(x, y, 0, 89, fill, 5);
        }
    }

    private static boolean isDrivingNimatin(EntityPlayer player) {
        return player != null && player.ridingEntity instanceof EntityNimatin && player.ridingEntity.riddenByEntity == player;
    }

    private void resetCharge() {
        jumpHeld = false;
        jumpPowerCounter = -1;
        jumpPower = 0.0F;
    }
}
