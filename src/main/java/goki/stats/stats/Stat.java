/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.common.ForgeHooks
 *  net.minecraftforge.common.config.Configuration
 */
package goki.stats.stats;

import goki.stats.lib.Helper;
import goki.stats.stats.IStat;
import goki.stats.stats.StatAthleticism;
import goki.stats.stats.StatBowmanship;
import goki.stats.stats.StatChopping;
import goki.stats.stats.StatClimbing;
import goki.stats.stats.StatDigging;
import goki.stats.stats.StatFeatherFall;
import goki.stats.stats.StatFurnaceFinesse;
import goki.stats.stats.StatLeaperH;
import goki.stats.stats.StatLeaperV;
import goki.stats.stats.StatMining;
import goki.stats.stats.StatMiningMagician;
import goki.stats.stats.StatProtection;
import goki.stats.stats.StatReaper;
import goki.stats.stats.StatSteadyGuard;
import goki.stats.stats.StatStealth;
import goki.stats.stats.StatSwordsmanship;
import goki.stats.stats.StatTempering;
import goki.stats.stats.StatToughSkin;
import goki.stats.stats.StatTreasureFinder;
import goki.stats.stats.StatTrimming;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.config.Configuration;

public class Stat
implements IStat {
    public static boolean loseStatsOnDeath = true;
    public static float globalCostMultiplier = 1.0f;
    public static float globalLimitMultiplier = 2.5f;
    public static float globalBonusMultiplier = 1.0f;
    public static ArrayList<Stat> stats = new ArrayList(32);
    public static int totalStats = 0;
    public static final StatMining STAT_MINING = new StatMining(0, "grpg_Mining", "Mining Proficiency", 10);
    public static final StatDigging STAT_DIGGING = new StatDigging(1, "grpg_Digging", "Digging Proficiency", 10);
    public static final StatChopping STAT_CHOPPING = new StatChopping(2, "grpg_Chopping", "Chopping Proficiency", 10);
    public static final StatTrimming STAT_TRIMMING = new StatTrimming(3, "grpg_Trimming", "Trimming Proficiency", 10);
    public static final StatProtection STAT_PROTECTION = new StatProtection(4, "grpg_Protection", "Protection", 10);
    public static final StatTempering STAT_TEMPERING = new StatTempering(5, "grpg_Tempering", "Tempering", 10);
    public static final StatToughSkin STAT_TOUGH_SKIN = new StatToughSkin(6, "grpg_ToughSkin", "Tough Skin", 10);
    public static final StatFeatherFall STAT_FEATHER_FALL = new StatFeatherFall(7, "grpg_FeatherFall", "Feather Fall", 10);
    public static final StatLeaperH STAT_LEAPERH = new StatLeaperH(8, "grpg_LeaperH", "LeaperH", 10);
    public static final StatLeaperV STAT_LEAPERV = new StatLeaperV(9, "grpg_LeaperV", "LeaperV", 10);
    public static final StatAthleticism STAT_ATHLETICISM = new StatAthleticism(10, "grpg_Athleticism", "Athleticism", 10);
    public static final StatClimbing STAT_CLIMBING = new StatClimbing(11, "grpg_Climbing", "Climbing", 10);
    public static final StatSwordsmanship STAT_SWORDSMANSHIP = new StatSwordsmanship(13, "grpg_Swordsmanship", "Swordsmanship", 10);
    public static final StatBowmanship STAT_BOWMANSHIP = new StatBowmanship(14, "grpg_Bowmanship", "Bowmanship", 10);
    public static final StatReaper STAT_REAPER = new StatReaper(15, "grpg_Reaper", "Reaper", 10);
    public static final StatTreasureFinder STAT_TREASURE_FINDER = new StatTreasureFinder(16, "grpg_Treasure_Finder", "Treasure Finder", 3);
    public static final StatFurnaceFinesse STAT_FURNACE_FINESSE = new StatFurnaceFinesse(17, "grpg_Furnace_Finesse", "Furnace Finesse", 10);
    public static final StatSteadyGuard STAT_STEADY_GUARD = new StatSteadyGuard(18, "grpg_Steady_Guard", "Steady Guard", 10);
    public static final StatStealth STAT_STEALTH = new StatStealth(19, "grpg_Stealth", "Stealth", 10);
    public static final StatMiningMagician STAT_MINING_MAGICIAN = new StatMiningMagician(20, "grpg_Mining_Magician", "Mining Magician", 10);
    public int imageID;
    private int limit;
    public String key;
    public String name;
    public float costMultiplier = 1.0f;
    public float limitMultiplier = 1.0f;
    public float bonusMultiplier = 1.0f;
    public boolean enabled = true;

    public Stat() {
        this.imageID = -1;
        this.limit = 0;
        this.key = "Dummy";
        this.name = "Dummy Stat";
    }

    public Stat(int id, String key, String name, int limit) {
        this.imageID = id;
        this.limit = limit;
        this.key = key;
        this.name = name;
        stats.add(this);
        ++totalStats;
    }

    public static void loadOptions(Configuration config) {
        globalCostMultiplier = (float)config.get("Global Modifiers", "Cost Muliplier", 1.0, "A flat multiplier on the cost to upgrade all stats.").getDouble(1.0);
        globalLimitMultiplier = (float)config.get("Global Modifiers", "Limit Muliplier", 2.5, "A flat multiplier on the level limit of all stats.").getDouble(1.0);
        globalBonusMultiplier = (float)config.get("Global Modifiers", "Bonus Muliplier", 1.0, "A flat multiplier on the bonus all stats gives.").getDouble(1.0);
        loseStatsOnDeath = config.get("Options", "Death Loss", true, "Lose stats on death?").getBoolean(true);
    }

    public static void saveGlobalMultipliers(Configuration config) {
        config.get("Global Modifiers", "Cost Muliplier", 1.0, "A flat multiplier on the cost to upgrade all stats.").set((double)globalCostMultiplier);
        config.get("Global Modifiers", "Limit Muliplier", 2.5, "A flat multiplier on the level limit of all stats.").set((double)globalLimitMultiplier);
        config.get("Global Modifiers", "Bonus Muliplier", 1.0, "A flat multiplier on the bonus all stats gives.").set((double)globalBonusMultiplier);
        config.get("Options", "Death Loss", true, "Lose stats on death?").set(loseStatsOnDeath);
    }

    @Override
    public float getBonus(int level) {
        return 0.0f;
    }

    @Override
    public float getBonus(EntityPlayer player) {
        return this.getBonus(Helper.getPlayerStatLevel(player, this));
    }

    @Override
    public int isAffectedByStat(Object object) {
        return 0;
    }

    @Override
    public String getSimpleDescriptionString() {
        return null;
    }

    @Override
    public String getAppliedDescriptionString(EntityPlayer player) {
        return null;
    }

    public static Stat getStat(int n) {
        return stats.get(n);
    }

    @Override
    public int getCost(int level) {
        return (int)((Math.pow(level, 1.6) + 6.0 + (double)level) * (double)globalCostMultiplier);
    }

    public static float getFinalBonus(float currentBonus) {
        return currentBonus * globalBonusMultiplier;
    }

    @Override
    public float getSecondaryBonus(int level) {
        return 0.0f;
    }

    @Override
    public float getSecondaryBonus(EntityPlayer player) {
        return this.getSecondaryBonus(Helper.getPlayerStatLevel(player, this));
    }

    protected int getPlayerStatLevel(EntityPlayer player) {
        return Helper.getPlayerStatLevel(player, this);
    }

    public void loadFromConfigurationFile(Configuration config) {
    }

    public void fromConfigurationString(String configString) {
    }

    public void saveToConfigurationFile(Configuration config) {
    }

    public String toConfigurationString() {
        return "!";
    }

    public static void loadAllStatsFromConfiguration(Configuration config) {
        for (int i = 0; i < totalStats; ++i) {
            stats.get(i).loadFromConfigurationFile(config);
        }
    }

    public static void saveAllStatsToConfiguration(Configuration config) {
        for (int i = 0; i < totalStats; ++i) {
            stats.get(i).saveToConfigurationFile(config);
        }
    }

    @Override
    public float getAppliedBonus(EntityPlayer player, Object object) {
        return this.getBonus(player) * (float)this.isAffectedByStat(object);
    }

    @Override
    public int isAffectedByStat(Object object1, Object object2) {
        return this.isAffectedByStat(object1);
    }

    @Override
    public int isAffectedByStat(Object object1, Object object2, Object object3) {
        int metadata;
        Block block;
        ItemStack stack;
        if (object1 instanceof ItemStack && object2 instanceof Block && object3 instanceof Integer && ForgeHooks.isToolEffective((ItemStack)(stack = (ItemStack)object1), (Block)(block = (Block)object2), (int)(metadata = ((Integer)object3).intValue()))) {
            return 1;
        }
        return this.isAffectedByStat(object1);
    }

    @Override
    public int getLimit() {
        if (globalLimitMultiplier <= 0.0f) {
            return 127;
        }
        return (int)((float)this.limit * globalLimitMultiplier);
    }
}

