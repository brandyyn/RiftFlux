/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 */
package goki.stats.lib;

import goki.stats.stats.Stat;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

public class Helper {
    public static NBTTagCompound getPlayerPersistentNBT(EntityPlayer player) {
        NBTTagCompound nbt = player.getEntityData().getCompoundTag("PlayerPersisted");
        if (!nbt.hasKey("gokiStats_Stats")) {
            nbt.setTag("gokiStats_Stats", (NBTBase)new NBTTagCompound());
            player.getEntityData().setTag("PlayerPersisted", (NBTBase)nbt);
        }
        return nbt;
    }

    public static int getPlayerStatLevel(EntityPlayer player, Stat stat) {
        NBTTagCompound nbt = Helper.getPlayerPersistentNBT(player);
        if (nbt.hasKey("gokiStats_Stats")) {
            return ((NBTTagCompound)nbt.getTag("gokiStats_Stats")).getByte(stat.key);
        }
        return 0;
    }

    public static void setPlayerStatLevel(EntityPlayer player, Stat stat, int level) {
        NBTTagCompound nbt = Helper.getPlayerPersistentNBT(player);
        if (nbt.hasKey("gokiStats_Stats")) {
            ((NBTTagCompound)nbt.getTag("gokiStats_Stats")).setByte(stat.key, (byte)level);
        }
    }

    public static float trimDecimals(float in, int decimals) {
        float f = (float)((double)in * Math.pow(10.0, decimals));
        int i = (int)f;
        return (float)i / (float)Math.pow(10.0, decimals);
    }

    public static void setPlayersExpTo(EntityPlayer player, int total) {
        player.experienceLevel = Helper.getLevelFromXPValue(total);
        player.experience = Helper.getCurrentFromXPValue(total);
    }

    public static int getXPTotal(int xpLevel, float current) {
        return (int)((float)Helper.getXPValueFromLevel(xpLevel) + (float)Helper.getXPValueToNextLevel(xpLevel) * current);
    }

    public static int getXPTotal(EntityPlayer player) {
        return (int)((float)Helper.getXPValueFromLevel(player.experienceLevel) + (float)Helper.getXPValueToNextLevel(player.experienceLevel) * player.experience);
    }

    public static int getLevelFromXPValue(int value) {
        int level = 0;
        level = value >= Helper.getXPValueFromLevel(30) ? (int)(0.07142857142857142 * (Math.sqrt(56.0 * (double)value - 32511.0) + 303.0)) : (value >= Helper.getXPValueFromLevel(15) ? (int)(0.16666666666666666 * (Math.sqrt(24.0 * (double)value - 5159.0) + 59.0)) : (int)((double)value / 17.0));
        return level;
    }

    public static float getCurrentFromXPValue(int value) {
        if (value == 0) {
            return 0.0f;
        }
        int level = Helper.getLevelFromXPValue(value);
        int needed = Helper.getXPValueFromLevel(level);
        int next = Helper.getXPValueToNextLevel(level);
        int difference = value - needed;
        float current = (float)difference / (float)next;
        return current;
    }

    public static int getXPValueFromLevel(int xpLevel) {
        int val = 0;
        val = xpLevel >= 30 ? (int)(3.5 * Math.pow(xpLevel, 2.0) - 151.5 * (double)xpLevel + 2220.0) : (xpLevel >= 15 ? (int)(1.5 * Math.pow(xpLevel, 2.0) - 29.5 * (double)xpLevel + 360.0) : 17 * xpLevel);
        return val;
    }

    public static int getXPValueToNextLevel(int xpLevel) {
        int val = 0;
        val = xpLevel >= 30 ? 7 * xpLevel - 148 : (xpLevel >= 15 ? 3 * xpLevel - 28 : 17);
        return val;
    }

    public static float getDamageDealt(EntityPlayer player, Entity target, DamageSource source) {
        float damage = (float)player.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
        float bonusDamage = 0.0f;
        boolean targetIsLiving = target instanceof EntityLivingBase;
        boolean critical = false;
        if (targetIsLiving) {
            bonusDamage = EnchantmentHelper.getEnchantmentModifierLiving((EntityLivingBase)player, (EntityLivingBase)((EntityLivingBase)target));
        }
        if (damage > 0.0f || bonusDamage > 0.0f) {
            boolean bl = critical = player.fallDistance > 0.0f && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(Potion.blindness) && player.ridingEntity == null && targetIsLiving;
            if (critical && damage > 0.0f) {
                damage *= 1.5f;
            }
            damage += bonusDamage;
        }
        return damage;
    }

    public static float getFallResistance(EntityLivingBase entity) {
        float resistance = 3.0f;
        PotionEffect potioneffect = entity.getActivePotionEffect(Potion.jump);
        float bonus = potioneffect != null ? (float)(potioneffect.getAmplifier() + 1) : 0.0f;
        return resistance + bonus;
    }
}

