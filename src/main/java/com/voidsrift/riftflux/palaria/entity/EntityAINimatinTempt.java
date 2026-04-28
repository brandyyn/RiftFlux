package com.voidsrift.riftflux.palaria.entity;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.palaria.PalariaMobDrops;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityAINimatinTempt extends EntityAIBase {
    private final EntityNimatin nimatin;
    private final World world;
    private final double speed;
    private final float range;
    private EntityPlayer player;

    public EntityAINimatinTempt(EntityNimatin nimatin, double speed, float range) {
        this.nimatin = nimatin;
        this.world = nimatin.worldObj;
        this.speed = speed;
        this.range = range;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (!ModConfig.palariaNimatinTameable || nimatin.isTamed() || nimatin.isAngry()) {
            return false;
        }
        player = world.getClosestPlayerToEntity(nimatin, range);
        return player != null && hasTameItem(player);
    }

    @Override
    public boolean continueExecuting() {
        return player != null
                && player.isEntityAlive()
                && !nimatin.isTamed()
                && !nimatin.isAngry()
                && nimatin.getDistanceSqToEntity(player) <= range * range
                && hasTameItem(player);
    }

    @Override
    public void startExecuting() {
        nimatin.setBegging(true);
    }

    @Override
    public void resetTask() {
        nimatin.getNavigator().clearPathEntity();
        nimatin.setBegging(false);
        player = null;
    }

    @Override
    public void updateTask() {
        nimatin.getLookHelper().setLookPosition(player.posX, player.posY + (double) player.getEyeHeight(), player.posZ, 10.0F, nimatin.getVerticalFaceSpeed());
        if (nimatin.getDistanceSqToEntity(player) > 6.25D) {
            nimatin.getNavigator().tryMoveToEntityLiving(player, speed);
        } else {
            nimatin.getNavigator().clearPathEntity();
        }
    }

    private boolean hasTameItem(EntityPlayer player) {
        ItemStack held = player.inventory.getCurrentItem();
        return held != null && PalariaMobDrops.matchesConfiguredItem(held, ModConfig.palariaNimatinTameItems);
    }
}
