package com.voidsrift.riftflux.combat.torohealth.server;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.combat.torohealth.net.MsgToroHealthDamage;
import com.voidsrift.riftflux.net.RFNetwork;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class DamageSyncEventHandler {

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!ModConfig.enableToroHealthModule || !ModConfig.toroHealthShowDamageParticles) {
            return;
        }

        final EntityLivingBase entity = event.entityLiving;
        if (entity == null || entity.worldObj == null || entity.worldObj.isRemote) {
            return;
        }

        final float amount = event.ammount; // 1.7.10 field name
        if (amount <= 0.0F) {
            return;
        }
        if (RFNetwork.CH == null) {
            return;
        }

        final int damage = MathHelper.ceiling_float_int(amount);
        RFNetwork.CH.sendToAll(new MsgToroHealthDamage(entity.getEntityId(), damage));
    }
}
