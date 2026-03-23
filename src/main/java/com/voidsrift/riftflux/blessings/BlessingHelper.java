package com.voidsrift.riftflux.blessings;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import com.voidsrift.riftflux.ModConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class BlessingHelper {
    public static final String NBT_BLESSING = "Blessing";
    public static final String NBT_BLESSING_ACTIVE = "BlessingActive";
    public static final String NBT_BLESSING_COOLDOWN = "BlessingCooldown";
    public static final String NBT_BLESSING_COUNTER = "BlessingCounter";
    public static final String NBT_BLESSING_TIMER = "BlessingTimer";
    public static final String NBT_NINJA_INVIS_COOLDOWN = "BlessingNinjaInvisCooldown";
    public static final String NBT_NINJA_DAMAGE_BOOST_TICK = "BlessingNinjaDamageBoostTick";
    public static final String NBT_BLESSING_PILLAR_X = "BlessingPillarX";
    public static final String NBT_BLESSING_PILLAR_Y = "BlessingPillarY";
    public static final String NBT_BLESSING_PILLAR_Z = "BlessingPillarZ";
    public static final String NBT_BLESSING_PILLAR_DIM = "BlessingPillarDim";

    public static final String[] BLESSINGS = new String[] {
            "Miner",
            "Lumberjack",
            "Warrior",
            "Hunter",
            "Swamp",
            "Thief",
            "Gambler",
            "Drunk",
            "Ninja",
            "Mechanic",
            "Alchemist",
            "Scout",
            "Guardian",
            "Vampire",
            "Inferno",
            "Diver",
            "Berserker",
            "Rogue",
            "Paratrooper",
            "Porcupine"
    };

    public static final String[] DESCRIPTIONS = new String[] {
            "+25% mining speed with pickaxes, but non-pickaxe blocks are 40% slower",
            "+25% breaking speed with axes, and +35% damage with axes",
            "+20% melee damage",
            "+20% projectile damage",
            "Attacks will slow enemies",
            "Mobs may drop additional items (5% chance each by default)",
            "Take 20% extra damage, but gain 60% more EXP from orbs",
            "Deal 50% more damage, but gain a negative potion effect when you take damage",
            "While sneaking you are invisible and attacks on full-health enemies do double damage. Invisibility breaks on attack and has a 30s combat cooldown",
            "Take 25% less explosive damage, and spike traps and caltrops don't affect you",
            "Drinking potions grants an extra random positive effect",
            "Move 20% faster, but take double fall damage",
            "Take 20% less damage from all sources",
            "Heal 7% of damage dealt to enemies and, in direct sunlight, you take 20% more damage and deal 20% less damage",
            "You don't take fire damage, and do +35% damage while on fire",
            "You can breathe underwater",
            "Kills are counted until 10 - Key to toggle berserk mode. While active you deal 33% more damage, move 33% faster, take 33% less damage, and gain extra health - You lose 1 kill every 2 seconds, regain by bloodshed",
            "The lower your health, the higher your damage, to a maximum of +100%",
            "You don't take fall damage",
            "Melee attackers receive damage"
    };

    private BlessingHelper() {
    }

    public static String getBlessing(EntityPlayer player) {
        if (player == null) {
            return null;
        }
        NBTTagCompound tag = player.getEntityData();
        if (!tag.hasKey(NBT_BLESSING)) {
            NBTTagCompound persisted = getPersisted(player, false);
            if (persisted != null && persisted.hasKey(NBT_BLESSING)) {
                String persistedBlessing = persisted.getString(NBT_BLESSING);
                if (persistedBlessing != null && !persistedBlessing.isEmpty()) {
                    tag.setString(NBT_BLESSING, persistedBlessing);
                    copyPersistedSource(persisted, tag);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
        String blessing = tag.getString(NBT_BLESSING);
        if ("Loner".equals(blessing)) {
            blessing = "Rogue";
            tag.setString(NBT_BLESSING, blessing);
        }
        return blessing;
    }

    public static String getPersistedBlessing(EntityPlayer player) {
        if (player == null) {
            return null;
        }
        NBTTagCompound persisted = getPersisted(player, false);
        if (persisted == null || !persisted.hasKey(NBT_BLESSING)) {
            return null;
        }
        String blessing = persisted.getString(NBT_BLESSING);
        if (blessing == null || blessing.isEmpty()) {
            return null;
        }
        if ("Loner".equals(blessing)) {
            blessing = "Rogue";
        }
        return blessing;
    }

    public static boolean isBlessingEnabled(String blessing) {
        return ModConfig.isBlessingEnabled(blessing);
    }

    public static boolean hasBlessing(EntityPlayer player) {
        return getBlessing(player) != null;
    }

    public static void setBlessing(EntityPlayer player, String blessing) {
        if (player == null || blessing == null) {
            return;
        }
        if ("Loner".equals(blessing)) {
            blessing = "Rogue";
        }
        player.getEntityData().setString(NBT_BLESSING, blessing);
        NBTTagCompound persisted = getPersisted(player, true);
        if (persisted != null) {
            persisted.setString(NBT_BLESSING, blessing);
        }
    }

    public static void clearBlessing(EntityPlayer player) {
        if (player == null) {
            return;
        }
        player.getEntityData().removeTag(NBT_BLESSING);
        NBTTagCompound persisted = getPersisted(player, false);
        if (persisted != null) {
            persisted.removeTag(NBT_BLESSING);
        }
    }

    public static void resetBlessingState(EntityPlayer player) {
        if (player == null) {
            return;
        }
        setActive(player, false);
        setCooldown(player, 0);
        setCounter(player, 0);
        setTimer(player, 0);
    }

    public static void setBlessingSource(EntityPlayer player, int x, int y, int z, int dim) {
        if (player == null) {
            return;
        }
        NBTTagCompound tag = player.getEntityData();
        tag.setInteger(NBT_BLESSING_PILLAR_X, x);
        tag.setInteger(NBT_BLESSING_PILLAR_Y, y);
        tag.setInteger(NBT_BLESSING_PILLAR_Z, z);
        tag.setInteger(NBT_BLESSING_PILLAR_DIM, dim);
        NBTTagCompound persisted = getPersisted(player, true);
        if (persisted != null) {
            persisted.setInteger(NBT_BLESSING_PILLAR_X, x);
            persisted.setInteger(NBT_BLESSING_PILLAR_Y, y);
            persisted.setInteger(NBT_BLESSING_PILLAR_Z, z);
            persisted.setInteger(NBT_BLESSING_PILLAR_DIM, dim);
        }
    }

    public static void clearBlessingSource(EntityPlayer player) {
        if (player == null) {
            return;
        }
        NBTTagCompound tag = player.getEntityData();
        tag.removeTag(NBT_BLESSING_PILLAR_X);
        tag.removeTag(NBT_BLESSING_PILLAR_Y);
        tag.removeTag(NBT_BLESSING_PILLAR_Z);
        tag.removeTag(NBT_BLESSING_PILLAR_DIM);
        NBTTagCompound persisted = getPersisted(player, false);
        if (persisted != null) {
            persisted.removeTag(NBT_BLESSING_PILLAR_X);
            persisted.removeTag(NBT_BLESSING_PILLAR_Y);
            persisted.removeTag(NBT_BLESSING_PILLAR_Z);
            persisted.removeTag(NBT_BLESSING_PILLAR_DIM);
        }
    }

    public static boolean hasBlessingSource(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        NBTTagCompound tag = player.getEntityData();
        return tag.hasKey(NBT_BLESSING_PILLAR_X)
                && tag.hasKey(NBT_BLESSING_PILLAR_Y)
                && tag.hasKey(NBT_BLESSING_PILLAR_Z)
                && tag.hasKey(NBT_BLESSING_PILLAR_DIM);
    }

    public static boolean isBlessingSource(EntityPlayer player, int x, int y, int z, int dim) {
        if (!hasBlessingSource(player)) {
            return false;
        }
        NBTTagCompound tag = player.getEntityData();
        return tag.getInteger(NBT_BLESSING_PILLAR_X) == x
                && tag.getInteger(NBT_BLESSING_PILLAR_Y) == y
                && tag.getInteger(NBT_BLESSING_PILLAR_Z) == z
                && tag.getInteger(NBT_BLESSING_PILLAR_DIM) == dim;
    }

    public static boolean isActive(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        return player.getEntityData().getBoolean(NBT_BLESSING_ACTIVE);
    }

    public static void setActive(EntityPlayer player, boolean active) {
        if (player == null) {
            return;
        }
        player.getEntityData().setBoolean(NBT_BLESSING_ACTIVE, active);
    }

    public static int getCooldown(EntityPlayer player) {
        if (player == null) {
            return 0;
        }
        return player.getEntityData().getInteger(NBT_BLESSING_COOLDOWN);
    }

    public static void setCooldown(EntityPlayer player, int ticks) {
        if (player == null) {
            return;
        }
        player.getEntityData().setInteger(NBT_BLESSING_COOLDOWN, Math.max(0, ticks));
    }

    public static int getCounter(EntityPlayer player) {
        if (player == null) {
            return 0;
        }
        return player.getEntityData().getInteger(NBT_BLESSING_COUNTER);
    }

    public static void setCounter(EntityPlayer player, int counter) {
        if (player == null) {
            return;
        }
        player.getEntityData().setInteger(NBT_BLESSING_COUNTER, Math.max(0, counter));
    }

    public static int getTimer(EntityPlayer player) {
        if (player == null) {
            return 0;
        }
        return player.getEntityData().getInteger(NBT_BLESSING_TIMER);
    }

    public static void setTimer(EntityPlayer player, int timer) {
        if (player == null) {
            return;
        }
        player.getEntityData().setInteger(NBT_BLESSING_TIMER, Math.max(0, timer));
    }

    public static void ensureBlessingState(EntityPlayer player) {
        if (player == null) {
            return;
        }
        NBTTagCompound tag = player.getEntityData();
        if (!tag.hasKey(NBT_BLESSING_ACTIVE)) {
            tag.setBoolean(NBT_BLESSING_ACTIVE, false);
        }
        if (!tag.hasKey(NBT_BLESSING_COOLDOWN)) {
            tag.setInteger(NBT_BLESSING_COOLDOWN, 0);
        }
        if (!tag.hasKey(NBT_BLESSING_COUNTER)) {
            tag.setInteger(NBT_BLESSING_COUNTER, 0);
        }
        if (!tag.hasKey(NBT_BLESSING_TIMER)) {
            tag.setInteger(NBT_BLESSING_TIMER, 0);
        }
        if (!tag.hasKey(NBT_NINJA_INVIS_COOLDOWN)) {
            tag.setInteger(NBT_NINJA_INVIS_COOLDOWN, 0);
        }
    }

    public static void ensurePersistedBlessing(EntityPlayer player) {
        if (player == null) {
            return;
        }
        NBTTagCompound tag = player.getEntityData();
        if (!tag.hasKey(NBT_BLESSING)) {
            return;
        }
        String blessing = tag.getString(NBT_BLESSING);
        if (blessing == null || blessing.isEmpty()) {
            return;
        }
        NBTTagCompound persisted = getPersisted(player, true);
        if (!persisted.hasKey(NBT_BLESSING)) {
            persisted.setString(NBT_BLESSING, blessing);
        }
        if (tag.hasKey(NBT_BLESSING_PILLAR_X)) {
            persisted.setInteger(NBT_BLESSING_PILLAR_X, tag.getInteger(NBT_BLESSING_PILLAR_X));
        }
        if (tag.hasKey(NBT_BLESSING_PILLAR_Y)) {
            persisted.setInteger(NBT_BLESSING_PILLAR_Y, tag.getInteger(NBT_BLESSING_PILLAR_Y));
        }
        if (tag.hasKey(NBT_BLESSING_PILLAR_Z)) {
            persisted.setInteger(NBT_BLESSING_PILLAR_Z, tag.getInteger(NBT_BLESSING_PILLAR_Z));
        }
        if (tag.hasKey(NBT_BLESSING_PILLAR_DIM)) {
            persisted.setInteger(NBT_BLESSING_PILLAR_DIM, tag.getInteger(NBT_BLESSING_PILLAR_DIM));
        }
    }

    private static NBTTagCompound getPersisted(EntityPlayer player, boolean create) {
        if (player == null) {
            return null;
        }
        NBTTagCompound data = player.getEntityData();
        if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            if (!create) {
                return null;
            }
            data.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        }
        return data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
    }

    private static void copyPersistedSource(NBTTagCompound persisted, NBTTagCompound target) {
        if (persisted.hasKey(NBT_BLESSING_PILLAR_X)) {
            target.setInteger(NBT_BLESSING_PILLAR_X, persisted.getInteger(NBT_BLESSING_PILLAR_X));
        }
        if (persisted.hasKey(NBT_BLESSING_PILLAR_Y)) {
            target.setInteger(NBT_BLESSING_PILLAR_Y, persisted.getInteger(NBT_BLESSING_PILLAR_Y));
        }
        if (persisted.hasKey(NBT_BLESSING_PILLAR_Z)) {
            target.setInteger(NBT_BLESSING_PILLAR_Z, persisted.getInteger(NBT_BLESSING_PILLAR_Z));
        }
        if (persisted.hasKey(NBT_BLESSING_PILLAR_DIM)) {
            target.setInteger(NBT_BLESSING_PILLAR_DIM, persisted.getInteger(NBT_BLESSING_PILLAR_DIM));
        }
    }

    public static String getRandomBlessing(Random rand, boolean allowInferno) {
        if (rand == null) {
            rand = new Random();
        }
        if (BLESSINGS.length == 0) {
            return null;
        }
        List<String> allowed = new ArrayList<String>();
        for (String blessing : BLESSINGS) {
            if (!allowInferno && "Inferno".equals(blessing)) {
                continue;
            }
            if (!isBlessingEnabled(blessing)) {
                continue;
            }
            allowed.add(blessing);
        }
        if (allowed.isEmpty()) {
            return null;
        }
        return allowed.get(rand.nextInt(allowed.size()));
    }

    public static int getBlessingIndex(String blessing) {
        if (blessing == null) {
            return -1;
        }
        for (int i = 0; i < BLESSINGS.length; i++) {
            if (blessing.equals(BLESSINGS[i])) {
                return i;
            }
        }
        return -1;
    }

    public static String getNameKey(String blessing) {
        String suffix = toBlessingKeySuffix(blessing);
        if (suffix.isEmpty()) {
            return "";
        }
        return "blessing.riftflux.name." + suffix;
    }

    public static String getDescriptionKey(String blessing) {
        String suffix = toBlessingKeySuffix(blessing);
        if (suffix.isEmpty()) {
            return "";
        }
        return "blessing.riftflux.desc." + suffix;
    }

    public static String getDisplayName(String blessing) {
        if (blessing == null || blessing.isEmpty()) {
            return "";
        }
        String key = getNameKey(blessing);
        if (key.isEmpty()) {
            return blessing;
        }
        String translated = StatCollector.translateToLocal(key);
        if (translated == null || translated.isEmpty() || key.equals(translated)) {
            return blessing;
        }
        return translated;
    }

    public static String getLocalizedTitle(String blessing) {
        if (blessing == null || blessing.isEmpty()) {
            return "";
        }
        String localizedName = getDisplayName(blessing);
        String key = "blessing.riftflux.title";
        String translated = StatCollector.translateToLocalFormatted(key, localizedName);
        if (translated == null || translated.isEmpty() || key.equals(translated)) {
            return "Blessing of the " + localizedName;
        }
        return translated;
    }

    public static String getDescription(String blessing) {
        if ("Vampire".equals(blessing)) {
            String key = getDescriptionKey(blessing);
            String translated = StatCollector.translateToLocalFormatted(key, formatPercent(ModConfig.blessingVampireHealPercent));
            if (translated != null && !translated.isEmpty() && !key.equals(translated)) {
                return translated;
            }
        }
        String key = getDescriptionKey(blessing);
        if (!key.isEmpty()) {
            String translated = StatCollector.translateToLocal(key);
            if (translated != null && !translated.isEmpty() && !key.equals(translated)) {
                return translated;
            }
        }
        int idx = getBlessingIndex(blessing);
        if (idx < 0 || idx >= DESCRIPTIONS.length) {
            return "";
        }
        return DESCRIPTIONS[idx];
    }

    private static String formatPercent(float value) {
        if (value == (long) value) {
            return String.format(Locale.ROOT, "%d", (long) value);
        }
        String out = String.format(Locale.ROOT, "%.2f", value);
        while (out.endsWith("0")) {
            out = out.substring(0, out.length() - 1);
        }
        if (out.endsWith(".")) {
            out = out.substring(0, out.length() - 1);
        }
        return out;
    }

    private static String toBlessingKeySuffix(String blessing) {
        if (blessing == null) {
            return "";
        }
        String normalized = blessing.trim().toLowerCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            return "";
        }
        StringBuilder out = new StringBuilder(normalized.length());
        for (int i = 0; i < normalized.length(); i++) {
            char c = normalized.charAt(i);
            if (c >= 'a' && c <= 'z') {
                out.append(c);
            } else if (c >= '0' && c <= '9') {
                out.append(c);
            } else {
                out.append('_');
            }
        }
        return out.toString();
    }
}
