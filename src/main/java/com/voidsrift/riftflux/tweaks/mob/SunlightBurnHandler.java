package com.voidsrift.riftflux.tweaks.mob;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent;

public class SunlightBurnHandler {

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event == null || event.entityLiving == null || event.entityLiving.worldObj == null || event.entityLiving.worldObj.isRemote) {
            return;
        }

        if (event.entityLiving instanceof EntityZombie) {
            EntityZombie zombie = (EntityZombie) event.entityLiving;
            if (zombie.isChild()) {
                return;
            }
            applySunlightBurn(zombie);
            return;
        }

        if (event.entityLiving instanceof EntitySkeleton) {
            applySunlightBurn(event.entityLiving);
        }
    }

    private void applySunlightBurn(net.minecraft.entity.EntityLivingBase living) {
        if (!living.worldObj.isDaytime()) {
            return;
        }

        float brightness = living.getBrightness(1.0F);
        if (brightness <= 0.5F) {
            return;
        }

        int x = MathHelper.floor_double(living.posX);
        int y = MathHelper.floor_double(living.posY);
        int z = MathHelper.floor_double(living.posZ);
        if (!living.worldObj.canBlockSeeTheSky(x, y, z)) {
            return;
        }

        ItemStack helmet = living.getEquipmentInSlot(4);
        if (helmet != null) {
            return;
        }

        living.setFire(8);
    }
}
