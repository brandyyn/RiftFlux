package net.nmccoy.legendgear.client;

import com.voidsrift.riftflux.net.MsgDashRingJump;
import com.voidsrift.riftflux.net.RFNetwork;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.PlayerStarstatsExtension;
import net.nmccoy.legendgear.item.MagicRing;

public class DashRingClientHandler {
    private static final String DASH_CLIENT_WAS_GROUNDED_KEY = "riftfluxDashClientWasGrounded";
    private static boolean bootstrapped;

    public static void bootstrap() {
        if (bootstrapped) {
            return;
        }
        bootstrapped = true;
        FMLCommonHandler.instance().bus().register(new DashRingClientHandler());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null) {
            return;
        }

        EntityPlayer player = mc.thePlayer;
        boolean jumpPressed = mc.currentScreen == null && mc.gameSettings.keyBindJump.getIsKeyPressed();
        boolean physicallyGrounded = player.onGround || player.isOnLadder() || player.isInWater();
        boolean wasGrounded = player.getEntityData().getBoolean(DASH_CLIENT_WAS_GROUNDED_KEY);

        if (physicallyGrounded) {
            MagicRing.setDashAirJumps(player, 0);
        }

        if (!MagicRing.PlayerWears(player, MagicRing.RingType.SPEED_RING)) {
            MagicRing.setDashJumpPressed(player, jumpPressed);
            player.getEntityData().setBoolean(DASH_CLIENT_WAS_GROUNDED_KEY, physicallyGrounded);
            return;
        }

        boolean requiresSprinting = LegendGear2.CONFIG_DASH_RING_AIR_JUMPS_REQUIRE_SPRINTING;
        if (!physicallyGrounded
                && !wasGrounded
                && jumpPressed
                && PlayerStarstatsExtension.availableMana(player) > 0.0F
                && (!requiresSprinting || player.isSprinting())
                && !MagicRing.wasDashJumpPressed(player)) {
            boolean preserveSprintState = player.isSprinting();
            double clientMotionX = player.motionX;
            double clientMotionZ = player.motionZ;
            if (LegendGear2.CONFIG_DASH_RING_USE_ORIGINAL_BEHAVIOR) {
                MagicRing.performDashJump(player, preserveSprintState);
                if (RFNetwork.CH != null) {
                    RFNetwork.CH.sendToServer(new MsgDashRingJump(preserveSprintState, clientMotionX, clientMotionZ));
                }
            } else if (LegendGear2.CONFIG_DASH_RING_MAX_AIR_JUMPS > MagicRing.getDashAirJumps(player)) {
                MagicRing.performDashJump(player, preserveSprintState);
                MagicRing.setDashAirJumps(player, MagicRing.getDashAirJumps(player) + 1);
                if (RFNetwork.CH != null) {
                    RFNetwork.CH.sendToServer(new MsgDashRingJump(preserveSprintState, clientMotionX, clientMotionZ));
                }
            }
        }

        MagicRing.setDashJumpPressed(player, jumpPressed);
        player.getEntityData().setBoolean(DASH_CLIENT_WAS_GROUNDED_KEY, physicallyGrounded);
    }
}
