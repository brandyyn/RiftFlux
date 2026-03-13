package inurosen.healaltar.common.core;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import com.voidsrift.riftflux.ModConfig;
import inurosen.healaltar.common.entity.ExtendedPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class EventHandler {
    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayer && ExtendedPlayer.get((EntityPlayer)event.entity) == null) {
            ExtendedPlayer.register((EntityPlayer)event.entity);
        }
    }

    @SubscribeEvent
    public void onLivingDeathEvent(LivingDeathEvent event) {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer) {
            ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer)event.entity);
            if (props != null) {
                props.setSoulHearts(0.0f);
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurtEvent(LivingHurtEvent event) {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer) {
            float damage = event.ammount;
            ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer)event.entity);
            if (props == null) {
                return;
            }
            float soulHearts = props.getSoulHearts();

            if (damage <= 0.0f || soulHearts <= 0.0f) {
                return;
            }

            if (ModConfig.soulHeartsConsumeOneHeartPerHit) {
                float heartCost = 2.0f;
                float remaining = soulHearts - heartCost;
                if (remaining >= 0.0f) {
                    props.setSoulHearts(remaining);
                    event.ammount = 0.0f;
                } else {
                    float ratio = soulHearts / heartCost;
                    props.setSoulHearts(0.0f);
                    event.ammount = damage * (1.0f - Math.max(0.0f, Math.min(1.0f, ratio)));
                }
            } else {
                float soulDamage = damage * ModConfig.soulHeartsDamageMultiplier;
                float damageDiff = soulHearts - soulDamage;
                if (damageDiff < 0.0f) {
                    props.setSoulHearts(0.0f);
                    event.ammount = -1.0f * damageDiff / ModConfig.soulHeartsDamageMultiplier;
                } else {
                    props.setSoulHearts(damageDiff);
                    event.ammount = 0.0f;
                }
            }
        }
    }
}
