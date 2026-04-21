package com.voidsrift.riftflux.blessings;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;

import java.util.Set;

public final class BlessingCombatHelper {
    private BlessingCombatHelper() {
    }

    public static float getDirectMeleeDamageMultiplier(EntityPlayer player, Entity target) {
        if (!(target instanceof EntityLivingBase)) {
            return 1.0F;
        }
        EntityLivingBase victim = (EntityLivingBase) target;
        String blessing = getEnabledBlessing(player);
        if (blessing == null) {
            return 1.0F;
        }

        if ("Ninja".equals(blessing) && shouldApplyNinjaFirstStrike(player, victim)) {
            return 2.0F;
        }
        if ("Lumberjack".equals(blessing) && isAxeLike(player.getHeldItem())) {
            return 1.35F;
        }
        return getCommonDamageMultiplier(player, blessing);
    }

    public static float getIndirectDamageMultiplier(EntityPlayer player, EntityLivingBase victim, DamageSource source) {
        String blessing = getEnabledBlessing(player);
        if (blessing == null) {
            return 1.0F;
        }
        if ("Hunter".equals(blessing) && source != null && source.isProjectile()) {
            return 1.2F;
        }
        return getCommonDamageMultiplier(player, blessing);
    }

    public static boolean shouldApplyNinjaFirstStrike(EntityPlayer player, EntityLivingBase victim) {
        return player != null
                && victim != null
                && player.isSneaking()
                && player.isPotionActive(Potion.invisibility)
                && victim.getHealth() >= victim.getMaxHealth();
    }

    public static boolean isDirectPlayerMeleeSource(EntityPlayer player, DamageSource source) {
        return player != null
                && source != null
                && !source.isProjectile()
                && source.getEntity() == player
                && "player".equals(source.damageType);
    }

    private static String getEnabledBlessing(EntityPlayer player) {
        if (player == null || !ModConfig.blessingsEnabled) {
            return null;
        }
        String blessing = BlessingHelper.getBlessing(player);
        return BlessingHelper.isBlessingEnabled(blessing) ? blessing : null;
    }

    private static float getCommonDamageMultiplier(EntityPlayer player, String blessing) {
        if (player == null || blessing == null) {
            return 1.0F;
        }
        if ("Warrior".equals(blessing)) {
            return 1.2F;
        }
        if ("Berserker".equals(blessing) && BlessingHelper.isActive(player)) {
            return 1.33F;
        }
        if ("Inferno".equals(blessing) && player.isBurning()) {
            return 1.35F;
        }
        if ("Drunk".equals(blessing)) {
            return 1.5F;
        }
        if ("Rogue".equals(blessing)) {
            float max = player.getMaxHealth();
            if (max > 0.0F) {
                return 1.0F + Math.min(1.0F, Math.max(0.0F, (max - player.getHealth()) / max));
            }
        }
        if ("Vampire".equals(blessing) && isInDirectSun(player)) {
            return 0.8F;
        }
        return 1.0F;
    }

    private static boolean isAxeLike(ItemStack held) {
        if (held == null || held.getItem() == null) {
            return false;
        }
        Item item = held.getItem();
        if (item instanceof ItemAxe) {
            return true;
        }
        try {
            Set toolClasses = item.getToolClasses(held);
            return toolClasses != null && toolClasses.contains("axe");
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean isInDirectSun(EntityPlayer player) {
        if (player == null || player.worldObj == null || !player.worldObj.isDaytime()) {
            return false;
        }
        int x = (int) Math.floor(player.posX);
        int y = (int) Math.floor(player.posY);
        int z = (int) Math.floor(player.posZ);
        return player.worldObj.canBlockSeeTheSky(x, y, z);
    }
}
