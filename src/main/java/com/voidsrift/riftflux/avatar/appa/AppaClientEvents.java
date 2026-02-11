package com.voidsrift.riftflux.avatar.appa;

import com.voidsrift.riftflux.net.MsgAppaControl;
import com.voidsrift.riftflux.net.RFNetwork;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class AppaClientEvents {
    private boolean lastUp;
    private boolean lastDown;
    private int lastEntityId = -1;

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
        if (!(player.ridingEntity instanceof EntityBison)) {
            if (lastEntityId != -1) {
                RFNetwork.CH.sendToServer(new MsgAppaControl(lastEntityId, false, false));
                lastEntityId = -1;
                lastUp = false;
                lastDown = false;
            }
            return;
        }

        int entityId = player.ridingEntity.getEntityId();
        boolean up = mc.gameSettings.keyBindJump.getIsKeyPressed();
        boolean down = mc.gameSettings.keyBindSprint.getIsKeyPressed();
        if (entityId != lastEntityId || up != lastUp || down != lastDown) {
            RFNetwork.CH.sendToServer(new MsgAppaControl(entityId, up, down));
            lastEntityId = entityId;
            lastUp = up;
            lastDown = down;
        }
    }
}
