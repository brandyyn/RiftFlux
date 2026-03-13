package com.voidsrift.riftflux.combat.torohealth.client.event;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.combat.torohealth.client.particle.DamageParticles;
import com.voidsrift.riftflux.combat.torohealth.mixins.EntityLivingBaseExt;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent;

public class ToroHealthEventHandler {

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!ModConfig.enableToroHealthModule || !ModConfig.toroHealthShowDamageParticles) {
            return;
        }

        final EntityLivingBase entity = event.entityLiving;
        if (!entity.worldObj.isRemote) {
            return;
        }
        if (!(entity instanceof EntityLivingBaseExt)) {
            return;
        }

        final EntityLivingBaseExt ext = (EntityLivingBaseExt) entity;
        final int prevHp = MathHelper.floor_float(ext.riftflux$getToroHealthPrevHealth());
        final int hp = MathHelper.floor_float(entity.getHealth());

        // Avoid treating initial spawn sync as a giant heal.
        if (hp > prevHp + 2 && entity.ticksExisted < 5) {
            ext.riftflux$setToroHealthPrevHealth(hp);
            return;
        }

        if (hp == prevHp) {
            return;
        }

        // Damage popoffs are server-synced; healing can be derived client-side.
        if (hp > prevHp) {
            DamageParticles.spawnDamageParticle(entity, prevHp - hp);
        }

        ext.riftflux$setToroHealthPrevHealth(hp);
    }
}
