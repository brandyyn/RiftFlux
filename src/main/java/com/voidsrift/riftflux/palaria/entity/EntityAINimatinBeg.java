package com.voidsrift.riftflux.palaria.entity;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.palaria.PalariaMobDrops;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityAINimatinBeg extends EntityAIBase {
    private final EntityNimatin nimatin;
    private final World world;
    private final float minPlayerDistance;
    private final double speed;
    private final boolean temptMode;
    private EntityPlayer player;
    private int timeoutCounter;

    public EntityAINimatinBeg(EntityNimatin nimatin, float minPlayerDistance) {
        this(nimatin, minPlayerDistance, 0.0D, false);
    }

    public EntityAINimatinBeg(EntityNimatin nimatin, float minPlayerDistance, double speed, boolean temptMode) {
        this.nimatin = nimatin;
        this.world = nimatin.worldObj;
        this.minPlayerDistance = minPlayerDistance;
        this.speed = speed;
        this.temptMode = temptMode;
        setMutexBits(temptMode ? 3 : 2);
    }

    @Override
    public boolean shouldExecute() {
        if (temptMode && (!ModConfig.palariaNimatinTameable || nimatin.isTamed() || nimatin.isAngry())) {
            return false;
        }
        player = world.getClosestPlayerToEntity(nimatin, minPlayerDistance);
        return player != null && (temptMode ? hasTameItem(player) : hasBegItem(player));
    }

    @Override
    public boolean continueExecuting() {
        return player != null
                && player.isEntityAlive()
                && (!temptMode || (!nimatin.isTamed() && !nimatin.isAngry()))
                && nimatin.getDistanceSqToEntity(player) <= minPlayerDistance * minPlayerDistance
                && (temptMode || timeoutCounter > 0)
                && (temptMode ? hasTameItem(player) : hasBegItem(player));
    }

    @Override
    public void startExecuting() {
        nimatin.setBegging(true);
        timeoutCounter = temptMode ? 0 : 40 + nimatin.getRNG().nextInt(40);
    }

    @Override
    public void resetTask() {
        if (temptMode) {
            nimatin.getNavigator().clearPathEntity();
        }
        nimatin.setBegging(false);
        player = null;
    }

    @Override
    public void updateTask() {
        nimatin.getLookHelper().setLookPosition(player.posX, player.posY + (double) player.getEyeHeight(), player.posZ, 10.0F, nimatin.getVerticalFaceSpeed());
        if (temptMode) {
            if (nimatin.getDistanceSqToEntity(player) > 6.25D) {
                nimatin.getNavigator().tryMoveToEntityLiving(player, speed);
            } else {
                nimatin.getNavigator().clearPathEntity();
            }
        } else {
            --timeoutCounter;
        }
    }

    private boolean hasBegItem(EntityPlayer player) {
        ItemStack held = player.inventory.getCurrentItem();
        if (held == null) {
            return false;
        }
        return (ModConfig.palariaNimatinTameable && PalariaMobDrops.matchesConfiguredItem(held, ModConfig.palariaNimatinTameItems))
                || held.getItem() instanceof ItemFood && ((ItemFood) held.getItem()).isWolfsFavoriteMeat();
    }

    private boolean hasTameItem(EntityPlayer player) {
        ItemStack held = player.inventory.getCurrentItem();
        return held != null && PalariaMobDrops.matchesConfiguredItem(held, ModConfig.palariaNimatinTameItems);
    }
}
