package com.voidsrift.riftflux.wam;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.wam.entity.EntityEnderTroll;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.List;

public class WAMEventHandler {
    private static final double TROLL_TARGET_RANGE = 24.0D;

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event == null || event.entityLiving == null || event.entityLiving.worldObj == null || event.entityLiving.worldObj.isRemote) {
            return;
        }
        if (!ModConfig.enableWitchesAndMoreModule || !ModConfig.enableEnderTrollMob) {
            return;
        }

        EntityLivingBase livingBase = event.entityLiving;
        if (!(livingBase instanceof EntityLiving)) {
            return;
        }
        EntityLiving living = (EntityLiving) livingBase;
        if (living.ticksExisted % 20 != 0) {
            return;
        }

        if (living instanceof EntityEnderTroll) {
            EntityEnderTroll troll = (EntityEnderTroll) living;
            EntityEnderman enderman = findNearest(EntityEnderman.class, troll, TROLL_TARGET_RANGE);
            if (enderman != null) {
                setMutualAggro(troll, enderman);
            }
            return;
        }

        if (living instanceof EntityEnderman) {
            EntityEnderman enderman = (EntityEnderman) living;
            EntityEnderTroll troll = findNearest(EntityEnderTroll.class, enderman, TROLL_TARGET_RANGE);
            if (troll != null) {
                setMutualAggro(troll, enderman);
            }
        }
    }

    private static void setMutualAggro(EntityEnderTroll troll, EntityEnderman enderman) {
        if (troll == null || enderman == null || !troll.isEntityAlive() || !enderman.isEntityAlive()) {
            return;
        }

        troll.setAttackTarget(enderman);
        troll.setRevengeTarget(enderman);
        troll.getNavigator().tryMoveToEntityLiving(enderman, 1.0D);
        enderman.setAttackTarget(troll);
        enderman.setRevengeTarget(troll);
        enderman.setScreaming(true);
        enderman.getNavigator().tryMoveToEntityLiving(troll, 1.0D);
    }

    private static <T extends EntityLiving> T findNearest(Class<T> type, EntityLiving source, double range) {
        AxisAlignedBB bounds = source.boundingBox.expand(range, 8.0D, range);
        @SuppressWarnings("unchecked")
        List<T> found = source.worldObj.getEntitiesWithinAABB(type, bounds);
        T best = null;
        double bestDistance = Double.MAX_VALUE;

        for (T candidate : found) {
            if (candidate == null || candidate == source || !candidate.isEntityAlive()) {
                continue;
            }
            double distance = source.getDistanceSqToEntity(candidate);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = candidate;
            }
        }

        return best;
    }
}
