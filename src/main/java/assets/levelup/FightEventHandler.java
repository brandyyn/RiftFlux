/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.monster.EntityMob
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.ChatComponentTranslation
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EntityDamageSourceIndirect
 *  net.minecraft.util.IChatComponent
 *  net.minecraft.util.MathHelper
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent
 */
package assets.levelup;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;

import java.util.Locale;

public final class FightEventHandler {
    public static final FightEventHandler INSTANCE = new FightEventHandler();

    private FightEventHandler() {
    }

    @SubscribeEvent
    public void onHurting(LivingHurtEvent event) {
        int j;
        DamageSource damagesource = event.source;
        float i = event.ammount;
        if (damagesource.getEntity() instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)damagesource.getEntity();
            boolean rangedSneakAttackBonusEnabled = ModConfig.levelUpEnableRangedSneakAttackBonusDamage;
            boolean meleeSneakAttackBonusEnabled = ModConfig.levelUpEnableMeleeSneakAttackBonusDamage;
            float rangedSneakAttackDamageMultiplier = Math.max(0.0f, ModConfig.levelUpRangedSneakAttackDamageMultiplier);
            float meleeSneakAttackDamageMultiplier = Math.max(0.0f, ModConfig.levelUpMeleeSneakAttackDamageMultiplier);
            if (damagesource instanceof EntityDamageSourceIndirect) {
                if (!damagesource.damageType.equals("arrow")) {
                    i *= 1.0F
                            + (float)BowEventHandler.getArcherSkill(entityplayer)
                            * ModConfig.levelUpArcheryProjectileDamagePercentPerPoint
                            / 100.0F;
                }
                if (rangedSneakAttackBonusEnabled
                        && rangedSneakAttackDamageMultiplier > 0.0f
                        && FightEventHandler.getDistance(event.entityLiving, (EntityLivingBase)entityplayer) < 256.0f
                        && entityplayer.isSneaking()
                        && !FightEventHandler.canSeePlayer(event.entityLiving)
                        && !FightEventHandler.entityIsFacing(event.entityLiving, (EntityLivingBase)entityplayer)) {
                    i *= rangedSneakAttackDamageMultiplier;
                    entityplayer.addChatComponentMessage(
                            (IChatComponent) new ChatComponentTranslation(
                                    "sneak.attack",
                                    new Object[]{FightEventHandler.formatMultiplier(rangedSneakAttackDamageMultiplier)}
                            )
                    );
                }
            } else {
                if (entityplayer.getCurrentEquippedItem() != null) {
                    j = this.getSwordSkill(entityplayer);
                    if (LevelUpTuning.rollPerPoint(
                            entityplayer.getRNG(),
                            j,
                            ModConfig.levelUpSwordCritChancePerPointPercent
                    )) {
                        i *= ModConfig.levelUpSwordCritDamageMultiplier;
                    }
                    i *= 1.0F
                            + (float)LevelUpTuning.steps(j, ModConfig.levelUpSwordDamagePointsPerStep)
                            * ModConfig.levelUpSwordDamagePercentPerStep
                            / 100.0F;
                }
                if (meleeSneakAttackBonusEnabled
                        && meleeSneakAttackDamageMultiplier > 0.0f
                        && entityplayer.isSneaking()
                        && !FightEventHandler.canSeePlayer(event.entityLiving)
                        && !FightEventHandler.entityIsFacing(event.entityLiving, (EntityLivingBase)entityplayer)) {
                    i *= meleeSneakAttackDamageMultiplier;
                    entityplayer.addChatComponentMessage(
                            (IChatComponent) new ChatComponentTranslation(
                                    "sneak.attack",
                                    new Object[]{FightEventHandler.formatMultiplier(meleeSneakAttackDamageMultiplier)}
                            )
                    );
                }
            }
        }
        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.entityLiving;
            j = this.getDefenseSkill(player);
            if (!damagesource.isUnblockable()) {
                float reduction = (float)LevelUpTuning.steps(
                        j,
                        ModConfig.levelUpDefenseReductionPointsPerStep
                ) * ModConfig.levelUpDefenseReductionPercentPerStep / 100.0F;
                i *= Math.max(0.0F, 1.0F - reduction);
            }
            if (player.isBlocking()
                    && LevelUpTuning.rollPerPoint(
                            player.getRNG(),
                            j,
                            ModConfig.levelUpDefenseSuperBlockChancePerPointPercent
                    )) {
                i *= ModConfig.levelUpDefenseSuperBlockDamageMultiplier;
            }
        }
        event.ammount = i;
    }

    @SubscribeEvent
    public void onTargetSet(LivingSetAttackTargetEvent event) {
        if (event.target instanceof EntityPlayer && event.entityLiving instanceof EntityMob && event.target.isSneaking() && !FightEventHandler.entityHasVisionOf(event.entityLiving, (EntityPlayer)event.target) && event.entityLiving.func_142015_aE() != event.entityLiving.ticksExisted) {
            ((EntityMob)event.entityLiving).setAttackTarget(null);
        }
    }

    private int getDefenseSkill(EntityPlayer player) {
        return PlayerExtendedProperties.getSkillFromIndex(player, 2);
    }

    private int getSwordSkill(EntityPlayer player) {
        return PlayerExtendedProperties.getSkillFromIndex(player, 1);
    }

    public static boolean canSeePlayer(EntityLivingBase entityLiving) {
        EntityPlayer entityplayer = entityLiving.worldObj.getClosestVulnerablePlayerToEntity((Entity)entityLiving, 16.0);
        return entityplayer != null && entityLiving.canEntityBeSeen((Entity)entityplayer) && (!entityplayer.isSneaking() || FightEventHandler.entityHasVisionOf(entityLiving, entityplayer));
    }

    public static float getDistance(EntityLivingBase entityLiving, EntityLivingBase entityliving1) {
        return MathHelper.floor_double_long((double)((entityliving1.posX - entityLiving.posX) * (entityliving1.posX - entityLiving.posX) + (entityliving1.posZ - entityLiving.posZ) * (entityliving1.posZ - entityLiving.posZ)));
    }

    public static float getPointDistance(double d, double d1, double d2, double d3) {
        return MathHelper.floor_double_long((double)((d2 - d) * (d2 - d) + (d3 - d1) * (d3 - d1)));
    }

    public static boolean compareAngles(float f, float f1, float f2) {
        if (MathHelper.abs((float)(f - f1)) < f2) {
            return true;
        }
        if (f + f2 >= 360.0f && f + f2 - 360.0f > f1) {
            return true;
        }
        return f1 + f2 >= 360.0f && f1 + f2 - 360.0f > f;
    }

    public static boolean entityHasVisionOf(EntityLivingBase entityLiving, EntityPlayer player) {
        if (entityLiving == null || player == null) {
            return false;
        }
        float baseSightRange = ModConfig.levelUpSneakingBaseMobSightRange;
        float sightDistanceSquared = baseSightRange * baseSightRange
                - (float)LevelUpTuning.steps(
                        PlayerExtendedProperties.from(player).getSkillFromIndex("Sneaking"),
                        ModConfig.levelUpSneakingSightReductionPointsPerStep
                ) * ModConfig.levelUpSneakingSightRangeReductionPerStep;
        if (FightEventHandler.getDistance(entityLiving, (EntityLivingBase)player) > Math.max(0.0F, sightDistanceSquared)) {
            return false;
        }
        return entityLiving.canEntityBeSeen((Entity)player) && FightEventHandler.entityIsFacing((EntityLivingBase)player, entityLiving);
    }

    public static boolean entityIsFacing(EntityLivingBase entityLiving, EntityLivingBase entityliving1) {
        if (entityLiving == null || entityliving1 == null) {
            return false;
        }
        float f = -((float)(entityliving1.posX - entityLiving.posX));
        float f1 = (float)(entityliving1.posZ - entityLiving.posZ);
        float f2 = entityLiving.rotationYaw;
        if (f2 < 0.0f) {
            float f3 = ((float)MathHelper.floor_float((float)(MathHelper.abs((float)f2) / 360.0f)) + 1.0f) * 360.0f;
            f2 = f3 + f2;
        } else {
            while (f2 > 360.0f) {
                f2 -= 360.0f;
            }
        }
        float f4 = (float)(Math.atan2(f, f1) * 180.0 / Math.PI);
        if (f < 0.0f) {
            f4 = 360.0f + f4;
        }
        return FightEventHandler.compareAngles(f2, f4, 22.5f);
    }

    private static String formatMultiplier(float value) {
        String formatted = String.format(Locale.ROOT, "%.2f", Math.max(0.0f, value));
        int end = formatted.length();
        while (end > 0 && formatted.charAt(end - 1) == '0') {
            end--;
        }
        if (end > 0 && formatted.charAt(end - 1) == '.') {
            end--;
        }
        return end > 0 ? formatted.substring(0, end) : "0";
    }
}
