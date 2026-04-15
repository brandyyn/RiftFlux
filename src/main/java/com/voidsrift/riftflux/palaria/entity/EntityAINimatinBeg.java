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
    private EntityPlayer player;
    private int timeoutCounter;

    public EntityAINimatinBeg(EntityNimatin nimatin, float minPlayerDistance) {
        this.nimatin = nimatin;
        this.world = nimatin.worldObj;
        this.minPlayerDistance = minPlayerDistance;
        setMutexBits(2);
    }

    @Override
    public boolean shouldExecute() {
        player = world.getClosestPlayerToEntity(nimatin, minPlayerDistance);
        return player != null && hasBegItem(player);
    }

    @Override
    public boolean continueExecuting() {
        return player != null
                && player.isEntityAlive()
                && nimatin.getDistanceSqToEntity(player) <= minPlayerDistance * minPlayerDistance
                && timeoutCounter > 0
                && hasBegItem(player);
    }

    @Override
    public void startExecuting() {
        nimatin.setBegging(true);
        timeoutCounter = 40 + nimatin.getRNG().nextInt(40);
    }

    @Override
    public void resetTask() {
        nimatin.setBegging(false);
        player = null;
    }

    @Override
    public void updateTask() {
        nimatin.getLookHelper().setLookPosition(player.posX, player.posY + (double) player.getEyeHeight(), player.posZ, 10.0F, nimatin.getVerticalFaceSpeed());
        --timeoutCounter;
    }

    private boolean hasBegItem(EntityPlayer player) {
        ItemStack held = player.inventory.getCurrentItem();
        if (held == null) {
            return false;
        }
        return (ModConfig.palariaNimatinTameable && !nimatin.isTamed() && PalariaMobDrops.matchesConfiguredItem(held, ModConfig.palariaNimatinTameItems))
                || held.getItem() instanceof ItemFood && ((ItemFood) held.getItem()).isWolfsFavoriteMeat();
    }
}
