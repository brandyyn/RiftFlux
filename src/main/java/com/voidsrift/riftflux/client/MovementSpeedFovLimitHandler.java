package com.voidsrift.riftflux.client;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.FOVUpdateEvent;

@SideOnly(Side.CLIENT)
public class MovementSpeedFovLimitHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onFovUpdate(FOVUpdateEvent event) {
        if (event == null || event.entity == null || ModConfig.movementSpeedFovFactorMax <= 0.0F) {
            return;
        }

        float speedFov = this.getMovementSpeedFov(event.entity);
        if (speedFov <= 0.0F) {
            return;
        }

        float clampedSpeedFov = MathHelper.clamp_float(speedFov, 0.0F, ModConfig.movementSpeedFovFactorMax);
        if (clampedSpeedFov == speedFov) {
            return;
        }

        float nonSpeedMultiplier = event.newfov / speedFov;
        if (Float.isNaN(nonSpeedMultiplier) || Float.isInfinite(nonSpeedMultiplier) || nonSpeedMultiplier <= 0.0F) {
            nonSpeedMultiplier = 1.0F;
        }

        event.newfov = clampedSpeedFov * nonSpeedMultiplier;
    }

    private float getMovementSpeedFov(EntityPlayerSP player) {
        float fov = 1.0F;
        if (player.capabilities.isFlying) {
            fov *= 1.1F;
        }

        IAttributeInstance attribute = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        float walkSpeed = player.capabilities.getWalkSpeed();
        if (attribute == null || walkSpeed == 0.0F) {
            return 1.0F;
        }

        fov = (float) ((double) fov * ((attribute.getAttributeValue() / (double) walkSpeed + 1.0D) / 2.0D));
        if (Float.isNaN(fov) || Float.isInfinite(fov)) {
            return 1.0F;
        }

        return fov;
    }
}
