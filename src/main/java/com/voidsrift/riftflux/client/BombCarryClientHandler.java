package com.voidsrift.riftflux.client;

import com.voidsrift.riftflux.net.MsgBombCarryUse;
import com.voidsrift.riftflux.net.RFNetwork;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;
import net.nmccoy.legendgear.legacy.entities.EntityBomb;
import org.lwjgl.input.Mouse;

public class BombCarryClientHandler {
    private static boolean bootstrapped;
    private boolean wasUsePressed;

    public static void bootstrap() {
        if (bootstrapped) {
            return;
        }
        bootstrapped = true;
        FMLCommonHandler.instance().bus().register(new BombCarryClientHandler());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null || mc.currentScreen != null) {
            this.wasUsePressed = false;
            return;
        }

        boolean usePressed = Mouse.isButtonDown(1);
        if (usePressed
                && !this.wasUsePressed
                && mc.thePlayer.riddenByEntity instanceof EntityBomb
                && RFNetwork.CH != null) {
            MovingObjectPosition hit = mc.objectMouseOver;
            if (hit == null || hit.typeOfHit == MovingObjectPosition.MovingObjectType.MISS) {
                RFNetwork.CH.sendToServer(new MsgBombCarryUse(mc.thePlayer.riddenByEntity.getEntityId()));
            }
        }

        this.wasUsePressed = usePressed;
    }
}
