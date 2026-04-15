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
    private boolean mountedJumpActive;
    private boolean doubleJumpSent;
    private int noChargeTicks;
    private int mountedJumpTicks;

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

        EntityNimatin nimatin = (EntityNimatin) mc.thePlayer.ridingEntity;
        boolean currentlyHeld = mc.gameSettings.keyBindJump.getIsKeyPressed();
        boolean grounded = isGrounded(nimatin);
        if (noChargeTicks > 0) {
            --noChargeTicks;
        }
        if (mountedJumpActive) {
            ++mountedJumpTicks;
            if ((mountedJumpTicks > 8 && (grounded || isSettled(nimatin))) || mountedJumpTicks > 120) {
                mountedJumpActive = false;
                doubleJumpSent = false;
                mountedJumpTicks = 0;
                noChargeTicks = 0;
            }
        }

        if (!jumpHeld && currentlyHeld) {
            if (mountedJumpActive) {
                jumpPowerCounter = -1;
                jumpPower = 0.0F;
                if (noChargeTicks <= 0 && !doubleJumpSent && RFNetwork.CH != null) {
                    RFNetwork.CH.sendToServer(MsgNimatinJump.doubleJump(nimatin.getEntityId()));
                    doubleJumpSent = true;
                }
            } else {
                jumpPowerCounter = 0;
                jumpPower = 0.0F;
            }
        }

        if (jumpHeld && !currentlyHeld) {
            if (!mountedJumpActive && jumpPower > 0.0F && RFNetwork.CH != null) {
                RFNetwork.CH.sendToServer(new MsgNimatinJump(nimatin.getEntityId(), (int) (jumpPower * 100.0F)));
                mountedJumpActive = true;
                doubleJumpSent = false;
                mountedJumpTicks = 0;
                noChargeTicks = 8;
            }
            jumpPowerCounter = -10;
            jumpPower = 0.0F;
        } else if (mountedJumpActive) {
            jumpPowerCounter = -1;
            jumpPower = 0.0F;
        } else if (currentlyHeld) {
            if (jumpPowerCounter < 0) {
                ++jumpPowerCounter;
                if (jumpPowerCounter == 0) {
                    jumpPower = 0.0F;
                }
            }
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
        return player != null && player.ridingEntity instanceof EntityNimatin;
    }

    private static boolean isGrounded(EntityNimatin nimatin) {
        return nimatin != null
                && nimatin.onGround
                && Math.abs(nimatin.motionY) < 0.12D
                && nimatin.fallDistance <= 0.0F;
    }

    private static boolean isSettled(EntityNimatin nimatin) {
        return nimatin != null
                && Math.abs(nimatin.motionY) < 0.08D
                && Math.abs(nimatin.posY - nimatin.prevPosY) < 0.03D;
    }

    private void resetCharge() {
        jumpHeld = false;
        jumpPowerCounter = -1;
        jumpPower = 0.0F;
        mountedJumpActive = false;
        doubleJumpSent = false;
        noChargeTicks = 0;
        mountedJumpTicks = 0;
    }
}
