/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.config.Configuration
 */
package tk.nukeduck.hearts;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.ConfiguredPotionEffectHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.potion.PotionEffect;
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
    private float heartLanternAuraRadius;
    private String[] heartLanternAuraEffects = new String[0];
    private boolean starLanternAuraEnabled;
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
            this.heartLanternAuraRadius = Math.max(0.0f, ModConfig.heartLanternAuraRadius);
            this.heartLanternAuraEffects = ModConfig.heartLanternAuraEffects;
            this.starLanternAuraEnabled = ModConfig.starLanternAuraEnabled;
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
        this.heartLanternAuraRadius = this.config.getFloat("heartLanternAuraRadius", "general", 6.0f, 0.0f, 64.0f, "Radius around a heart lantern that receives the configured aura effects.");
        this.heartLanternAuraEffects = this.config.getStringList("heartLanternAuraEffects", "general", new String[]{"regeneration,2,4"}, "Format per entry: potionNameOrId,level,durationSeconds");
        this.starLanternAuraEnabled = this.config.getBoolean("starLanternAuraEnabled", "general", true, "If true, star lanterns apply configurable potion effects in a radius.");
        this.starLanternAuraRadius = this.config.getFloat("starLanternAuraRadius", "general", 6.0f, 0.0f, 64.0f, "Radius around a star lantern that receives the configured aura effects.");
        this.starLanternAuraEffects = this.config.getStringList("starLanternAuraEffects", "general", new String[]{"legendgearManaRegen,1,4"}, "Format per entry: potionNameOrId,level,durationSeconds");
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

    public List<LanternAuraEffect> getHeartLanternAuraEffects() {
        return this.parseLanternAuraEffects(this.heartLanternAuraEffects);
    }

    public boolean isStarLanternAuraEnabled() {
        return this.starLanternAuraEnabled;
    }

    public float getStarLanternAuraRadius() {
        return this.starLanternAuraRadius;
    }

    public List<LanternAuraEffect> getStarLanternAuraEffects() {
        return this.parseLanternAuraEffects(this.starLanternAuraEffects);
    }

    private List<LanternAuraEffect> parseLanternAuraEffects(String[] entries) {
        ArrayList<LanternAuraEffect> parsed = new ArrayList<LanternAuraEffect>();
        for (PotionEffect effect : ConfiguredPotionEffectHelper.parseEffects(entries)) {
            parsed.add(new LanternAuraEffect(
                    effect.getPotionID(),
                    effect.getAmplifier(),
                    effect.getDuration()
            ));
        }
        if (parsed.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(parsed);
    }

    public static final class LanternAuraEffect {
        private final int potionId;
        private final int amplifier;
        private final int durationTicks;

        public LanternAuraEffect(int potionId, int amplifier, int durationTicks) {
            this.potionId = potionId;
            this.amplifier = amplifier;
            this.durationTicks = Math.max(1, durationTicks);
        }

        public int getPotionId() {
            return this.potionId;
        }

        public int getAmplifier() {
            return this.amplifier;
        }

        public int getDurationTicks() {
            return this.durationTicks;
        }
    }
}
