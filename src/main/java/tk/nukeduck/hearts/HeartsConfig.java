/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.config.Configuration
 */
package tk.nukeduck.hearts;

import com.voidsrift.riftflux.ModConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.config.Configuration;

public class HeartsConfig {
    public Configuration config;
    private int maxHearts;
    private int heartsPerCrystal;
    private int miningLevel;
    private int genHeight;
    private int genCount;
    private float keptRate;
    private boolean oldModel;
    private boolean heartLanternAuraEnabled;
    private int heartLanternAuraDurationSeconds;
    private float heartLanternAuraRadius;
    private String[] heartLanternAuraEffects = new String[0];
    private boolean starLanternAuraEnabled;
    private int starLanternAuraDurationSeconds;
    private float starLanternAuraRadius;
    private String[] starLanternAuraEffects = new String[0];

    public HeartsConfig(File file) {
        this.config = new Configuration(file);
    }

    public HeartsConfig load() {
        if (ModConfig.config != null) {
            this.maxHearts = ModConfig.heartCrystalMaxHearts;
            this.heartsPerCrystal = Math.max(1, ModConfig.heartCrystalHeartsPerCrystal);
            this.miningLevel = Math.max(0, ModConfig.heartCrystalMiningLevel);
            this.genHeight = ModConfig.heartCrystalGenHeight;
            this.genCount = ModConfig.heartCrystalGenCount;
            this.oldModel = ModConfig.heartCrystalOldModel;
            this.heartLanternAuraEnabled = ModConfig.heartLanternAuraEnabled;
            this.heartLanternAuraDurationSeconds = Math.max(1, ModConfig.heartLanternAuraDurationSeconds);
            this.heartLanternAuraRadius = Math.max(0.0f, ModConfig.heartLanternAuraRadius);
            this.heartLanternAuraEffects = ModConfig.heartLanternAuraEffects;
            this.starLanternAuraEnabled = ModConfig.starLanternAuraEnabled;
            this.starLanternAuraDurationSeconds = Math.max(1, ModConfig.starLanternAuraDurationSeconds);
            this.starLanternAuraRadius = Math.max(0.0f, ModConfig.starLanternAuraRadius);
            this.starLanternAuraEffects = ModConfig.starLanternAuraEffects;
            return this;
        }

        this.config.load();
        this.maxHearts = this.config.getInt("maxHearts", "general", 10, 0, Integer.MAX_VALUE, "Sets how many extra hearts a player can gain");
        this.heartsPerCrystal = this.config.getInt("heartsPerCrystal", "general", 2, 1, 20, "Sets how many hearts one consumed heart crystal grants");
        this.miningLevel = this.config.getInt("miningLevel", "general", 2, 0, 10, "Sets required pickaxe harvest level for heart crystal blocks. 2 = iron.");
        this.genHeight = this.config.getInt("genHeight", "general", 20, 1, 256, "Sets the height of heart crystal generation, from 0 (inclusive) to X (exclusive)");
        this.genCount = this.config.getInt("genCount", "general", 4, 0, 1000, "Sets the chances of heart crystals spawning. Set to 0 for no spawning");
        this.keptRate = this.config.getFloat("keptRate", "general", 0.0f, 0.0f, 1.0f, "Sets the chance of retaining individual hearts upon death. 0.0F to always drop hearts, 1.0F to always keep them.");
        this.oldModel = this.config.getBoolean("oldModel", "general", false, "Enable this to use the old 3D model for heart crystals.");
        this.heartLanternAuraEnabled = this.config.getBoolean("heartLanternAuraEnabled", "general", false, "If true, heart lanterns apply configurable potion effects in a radius.");
        this.heartLanternAuraDurationSeconds = this.config.getInt("heartLanternAuraDurationSeconds", "general", 4, 1, 60, "How long heart lantern aura potion effects last, in seconds.");
        this.heartLanternAuraRadius = this.config.getFloat("heartLanternAuraRadius", "general", 6.0f, 0.0f, 64.0f, "Radius around a heart lantern that receives the configured aura effects.");
        this.heartLanternAuraEffects = this.config.getStringList("heartLanternAuraEffects", "general", new String[]{"regeneration,0"}, "Format per entry: potionNameOrId,amplifier");
        this.starLanternAuraEnabled = this.config.getBoolean("starLanternAuraEnabled", "general", true, "If true, star lanterns apply configurable potion effects in a radius.");
        this.starLanternAuraDurationSeconds = this.config.getInt("starLanternAuraDurationSeconds", "general", 4, 1, 60, "How long star lantern aura potion effects last, in seconds.");
        this.starLanternAuraRadius = this.config.getFloat("starLanternAuraRadius", "general", 6.0f, 0.0f, 64.0f, "Radius around a star lantern that receives the configured aura effects.");
        this.starLanternAuraEffects = this.config.getStringList("starLanternAuraEffects", "general", new String[]{"legendgearManaRegen,0"}, "Format per entry: potionNameOrId,amplifier");
        this.config.save();
        return this;
    }

    public int getMaxHearts() {
        return this.maxHearts;
    }

    public int getHeartsPerCrystal() {
        return this.heartsPerCrystal;
    }

    public int getMiningLevel() {
        return this.miningLevel;
    }

    public int getGenHeight() {
        return this.genHeight;
    }

    public int getGenCount() {
        return this.genCount;
    }

    public float getKeptRate() {
        return this.keptRate;
    }

    public boolean getOldModel() {
        return this.oldModel;
    }

    public boolean isHeartLanternAuraEnabled() {
        return this.heartLanternAuraEnabled;
    }

    public float getHeartLanternAuraRadius() {
        return this.heartLanternAuraRadius;
    }

    public int getHeartLanternAuraDurationSeconds() {
        return this.heartLanternAuraDurationSeconds;
    }

    public List<LanternAuraEffect> getHeartLanternAuraEffects() {
        return this.parseLanternAuraEffects(this.heartLanternAuraEffects);
    }

    public boolean isStarLanternAuraEnabled() {
        return this.starLanternAuraEnabled;
    }

    public float getStarLanternAuraRadius() {
        return this.starLanternAuraRadius;
    }

    public int getStarLanternAuraDurationSeconds() {
        return this.starLanternAuraDurationSeconds;
    }

    public List<LanternAuraEffect> getStarLanternAuraEffects() {
        return this.parseLanternAuraEffects(this.starLanternAuraEffects);
    }

    private List<LanternAuraEffect> parseLanternAuraEffects(String[] entries) {
        if (entries == null || entries.length == 0) {
            return Collections.emptyList();
        }
        ArrayList<LanternAuraEffect> parsed = new ArrayList<LanternAuraEffect>();
        for (String entry : entries) {
            if (entry == null) {
                continue;
            }
            String raw = entry.trim();
            if (raw.isEmpty()) {
                continue;
            }
            String[] parts = raw.split("\\s*,\\s*");
            if (parts.length < 2) {
                continue;
            }
            int potionId = this.parsePotionId(parts[0]);
            if (potionId < 0 || potionId >= Potion.potionTypes.length || Potion.potionTypes[potionId] == null) {
                continue;
            }
            int amplifier = this.parseIntSafe(parts[1], 0);
            if (amplifier < 0) {
                amplifier = 0;
            }
            parsed.add(new LanternAuraEffect(potionId, amplifier));
        }
        if (parsed.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(parsed);
    }

    private int parsePotionId(String token) {
        if (token == null) {
            return -1;
        }
        String trimmed = token.trim();
        if (trimmed.isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(trimmed);
        }
        catch (NumberFormatException ignored) {
        }
        String normalized = trimmed.toLowerCase(Locale.ROOT);
        if (normalized.startsWith("potion.")) {
            normalized = normalized.substring("potion.".length());
        }
        if (normalized.equals("speed")) {
            normalized = "movespeed";
        }
        if (normalized.equals("slowness")) {
            normalized = "moveslowdown";
        }
        if (normalized.equals("haste")) {
            normalized = "digspeed";
        }
        if (normalized.equals("miningfatigue")) {
            normalized = "digslowdown";
        }
        if (normalized.equals("strength")) {
            normalized = "damageboost";
        }
        if (normalized.equals("regen")) {
            normalized = "regeneration";
        }
        if (normalized.equals("manaregen")) {
            normalized = "legendgearmanaregen";
        }
        for (Potion potion : Potion.potionTypes) {
            if (potion == null) {
                continue;
            }
            String name = potion.getName();
            if (name == null) {
                continue;
            }
            String simple = name.toLowerCase(Locale.ROOT);
            if (simple.startsWith("potion.")) {
                simple = simple.substring("potion.".length());
            }
            if (simple.equals(normalized)) {
                return potion.id;
            }
        }
        return -1;
    }

    private int parseIntSafe(String token, int fallback) {
        if (token == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(token.trim());
        }
        catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    public static final class LanternAuraEffect {
        private final int potionId;
        private final int amplifier;

        public LanternAuraEffect(int potionId, int amplifier) {
            this.potionId = potionId;
            this.amplifier = amplifier;
        }

        public int getPotionId() {
            return this.potionId;
        }

        public int getAmplifier() {
            return this.amplifier;
        }
    }
}
