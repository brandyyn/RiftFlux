package com.voidsrift.riftflux.avatar.glider;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;

import java.util.IdentityHashMap;
import java.util.Map;

public class GliderPlayerRenderHandler {
    private final Map<EntityPlayer, float[]> limbSwingBackup = new IdentityHashMap<EntityPlayer, float[]>();

    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.entityPlayer;
        ItemStack held = player.getHeldItem();
        boolean holdingGlider = held != null && held.getItem() instanceof ItemGlider;
        if (!holdingGlider) {
            return;
        }
        String playerName = player.getDisplayName();
        boolean shouldGlide = !player.onGround && !player.isInWater()
                && GliderState.isPlayerGliding(playerName);
        if (!shouldGlide) {
            return;
        }
        if (limbSwingBackup.containsKey(player)) {
            return;
        }
        limbSwingBackup.put(player, new float[]{
                player.limbSwing,
                player.prevLimbSwingAmount,
                player.limbSwingAmount
        });
        player.limbSwing = 0.0f;
        player.prevLimbSwingAmount = 0.0f;
        player.limbSwingAmount = 0.0f;
        float yaw = player.prevRenderYawOffset
                + (player.renderYawOffset - player.prevRenderYawOffset) * event.partialRenderTick
                + 90.0f;
        double yawRad = Math.toRadians(yaw);
        float axisX = (float) Math.sin(yawRad);
        float axisZ = (float) -Math.cos(yawRad);
        GL11.glPushMatrix();
        GL11.glRotatef(75.0f, axisX, 0.0f, axisZ);
    }

    @SubscribeEvent
    public void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        float[] backup = limbSwingBackup.remove(event.entityPlayer);
        if (backup == null) {
            return;
        }
        event.entityPlayer.limbSwing = backup[0];
        event.entityPlayer.prevLimbSwingAmount = backup[1];
        event.entityPlayer.limbSwingAmount = backup[2];
        GL11.glPopMatrix();
    }
}
