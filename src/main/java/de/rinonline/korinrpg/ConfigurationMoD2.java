/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 */
package de.rinonline.korinrpg;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.FMLCommonHandler;
import de.rinonline.korinrpg.Springmain;

public class ConfigurationMoD2 {
    public static double transparency;
    public static double MaxSprintime;
    public static double rechargeSprintime;
    public static double OverchargeSprintime;
    public static double DefaultTrans;
    public static boolean EnableEnchantment;
    public static int EnchantmentMaxStaminaId;
    public static int EnchantmentStaminaRegenId;
    public static int EnchantmentOverloadreductionId;
    public static double EnchantmentMaxStamina;
    public static double EnchantmentStaminaRegen;
    public static double EnchantmentOverloadreduction;
    public static boolean EnablePotionEffects;
    public static double PotionEffectregenerationspeed;
    public static String PotionEffectregenerationspeedID;
    public static double PotionEffectreduceregen;
    public static String PotionEffectreduceregenID;
    public static double PotionEffectreducemaxtime;
    public static String PotionEffectreducemaxtimeID;
    public static double PotionEffectexpandmaxtime;
    public static String PotionEffectexpandmaxtimeID;
    public static int BarX;
    public static int BarY;
    public static int BarType;

    public static void loadConfig() {
        FMLCommonHandler.instance().bus().register((Object)Springmain.instance);
        MaxSprintime = ModConfig.dssMaxSprintingTimeSeconds;
        OverchargeSprintime = ModConfig.dssOverchargeRegenTimeSeconds;
        EnableEnchantment = ModConfig.dssEnableEnchantments;
        EnchantmentMaxStaminaId = ModConfig.dssEnchantmentMaxStaminaId;
        EnchantmentStaminaRegenId = ModConfig.dssEnchantmentStaminaRegenId;
        EnchantmentOverloadreductionId = ModConfig.dssEnchantmentOverloadReductionId;
        EnchantmentMaxStamina = ModConfig.dssEnchantmentMaxStaminaSeconds;
        EnchantmentStaminaRegen = ModConfig.dssEnchantmentStaminaRegenSeconds;
        EnchantmentOverloadreduction = ModConfig.dssEnchantmentOverloadReductionSeconds;
        EnablePotionEffects = ModConfig.dssEnablePotionEffects;
        PotionEffectregenerationspeedID = ModConfig.dssPotionRegenSpeedIds;
        PotionEffectregenerationspeed = ModConfig.dssPotionRegenSpeedMultiplier;
        PotionEffectreduceregenID = ModConfig.dssPotionReduceRegenIds;
        PotionEffectreduceregen = ModConfig.dssPotionReduceRegenMultiplier;
        PotionEffectreducemaxtimeID = ModConfig.dssPotionReduceMaxStaminaIds;
        PotionEffectreducemaxtime = ModConfig.dssPotionReduceMaxStaminaMultiplier;
        PotionEffectexpandmaxtimeID = ModConfig.dssPotionExpandMaxStaminaIds;
        PotionEffectexpandmaxtime = ModConfig.dssPotionExpandMaxStaminaMultiplier;
        BarType = ModConfig.dssBarSize;
        BarX = ModConfig.dssBarOffsetX;
        BarY = ModConfig.dssBarOffsetY;
        transparency = ModConfig.dssBarTransparencyPercent;
    }
}
