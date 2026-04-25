package zairus.worldexplorer.archery.items;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.ConfigResolver;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;

public final class DartEffectHelper {
    private static final String EXTRA_EFFECTS_KEY = "riftflux_dart_extra_effects";
    private static final String POISON_LEVEL_KEY = "riftflux_dart_poison_level";
    private static final int MAX_POISON_LEVEL = 3;

    private DartEffectHelper() {
    }

    public static boolean hasExtraEffects(ItemStack stack) {
        return !getExtraEffects(stack).isEmpty();
    }

    public static boolean hasInfusion(ItemStack stack) {
        return getPoisonLevel(stack) > 1 || hasExtraEffects(stack);
    }

    public static int getExtraEffectCount(ItemStack stack) {
        return getExtraEffects(stack).size();
    }

    public static int getPoisonLevel(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return 1;
        }
        return clampPoisonLevel(stack.getTagCompound().getInteger(POISON_LEVEL_KEY));
    }

    public static List<PotionEffect> getExtraEffects(ItemStack stack) {
        List<PotionEffect> effects = new ArrayList<PotionEffect>();
        if (stack == null || !stack.hasTagCompound()) {
            return effects;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (!tag.hasKey(EXTRA_EFFECTS_KEY, 9)) {
            if (tag.hasKey(Dart.KEY_EFFECTID, 99)) {
                int effectId = tag.getInteger(Dart.KEY_EFFECTID);
                if (effectId > 0 && effectId != Potion.poison.id) {
                    PotionEffect effect = normalizeEffect(new PotionEffect(effectId, getConfiguredDurationTicks(), 0));
                    if (isEffectAllowed(effect)) {
                        effects.add(effect);
                    }
                }
            }
            return effects;
        }
        NBTTagList effectList = tag.getTagList(EXTRA_EFFECTS_KEY, 10);
        for (int i = 0; i < effectList.tagCount(); i++) {
            PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT(effectList.getCompoundTagAt(i));
            if (effect != null && effect.getPotionID() > 0) {
                PotionEffect normalized = normalizeEffect(effect);
                if (isEffectAllowed(normalized)) {
                    effects.add(normalized);
                }
            }
        }
        return effects;
    }

    public static ItemStack applyPotion(ItemStack dartStack, ItemStack potionStack) {
        if (dartStack == null || dartStack.getItem() == null || potionStack == null || potionStack.getItem() != Items.potionitem) {
            return null;
        }
        List<PotionEffect> potionEffects = Items.potionitem.getEffects(potionStack);
        if (potionEffects == null || potionEffects.isEmpty()) {
            return null;
        }

        LinkedHashMap<Integer, PotionEffect> merged = new LinkedHashMap<Integer, PotionEffect>();
        List<PotionEffect> existingEffects = getExtraEffects(dartStack);
        for (int i = 0; i < existingEffects.size(); i++) {
            PotionEffect effect = existingEffects.get(i);
            merged.put(effect.getPotionID(), copyEffect(effect));
        }

        int poisonLevel = getPoisonLevel(dartStack);
        boolean changed = false;
        int maxExtraEffects = getMaxExtraEffects();
        for (int i = 0; i < potionEffects.size(); i++) {
            PotionEffect rawEffect = potionEffects.get(i);
            if (rawEffect == null || rawEffect.getPotionID() <= 0) {
                continue;
            }
            if (!isEffectAllowed(rawEffect)) {
                continue;
            }

            if (rawEffect.getPotionID() == Potion.poison.id) {
                int incomingPoisonLevel = clampPoisonLevel(rawEffect.getAmplifier() + 1);
                if (incomingPoisonLevel > poisonLevel) {
                    poisonLevel = incomingPoisonLevel;
                    changed = true;
                }
                continue;
            }

            if (merged.size() >= maxExtraEffects && !merged.containsKey(rawEffect.getPotionID())) {
                continue;
            }

            PotionEffect incoming = normalizeEffect(rawEffect);
            PotionEffect existing = merged.get(incoming.getPotionID());
            if (existing != null) {
                PotionEffect stronger = choosePreferredEffect(existing, incoming);
                if (!isSameEffect(existing, stronger)) {
                    merged.put(incoming.getPotionID(), stronger);
                    changed = true;
                }
                continue;
            }

            merged.put(incoming.getPotionID(), incoming);
            changed = true;
        }

        if (!changed) {
            return null;
        }

        ItemStack output = dartStack.copy();
        NBTTagCompound outputTag = output.getTagCompound();
        if (outputTag == null) {
            outputTag = new NBTTagCompound();
            output.setTagCompound(outputTag);
        }
        if (poisonLevel > 1) {
            outputTag.setInteger(POISON_LEVEL_KEY, poisonLevel);
        } else {
            outputTag.removeTag(POISON_LEVEL_KEY);
        }

        NBTTagList effectList = new NBTTagList();
        for (Map.Entry<Integer, PotionEffect> entry : merged.entrySet()) {
            effectList.appendTag(entry.getValue().writeCustomPotionEffectToNBT(new NBTTagCompound()));
        }
        if (effectList.tagCount() > 0) {
            outputTag.setTag(EXTRA_EFFECTS_KEY, effectList);
        } else {
            outputTag.removeTag(EXTRA_EFFECTS_KEY);
        }
        outputTag.removeTag(Dart.KEY_HASEFFECT);
        outputTag.removeTag(Dart.KEY_EFFECTID);
        outputTag.removeTag(Dart.KEY_EFFECTNAME);
        if (outputTag.hasNoTags()) {
            output.setTagCompound(null);
        }
        return output;
    }

    public static void applyEffects(EntityLivingBase target, Entity shooter, ItemStack dartStack) {
        if (target == null) {
            return;
        }
        if (target.isPotionActive(Potion.poison)) {
            target.removePotionEffect(Potion.poison.id);
        }
        target.addPotionEffect(new PotionEffect(Potion.poison.id, getConfiguredDurationTicks(), getPoisonLevel(dartStack) - 1));
        List<PotionEffect> extraEffects = getExtraEffects(dartStack);
        for (int i = 0; i < extraEffects.size(); i++) {
            PotionEffect effect = normalizeEffect(extraEffects.get(i));
            Potion potion = getPotion(effect.getPotionID());
            if (potion == null) {
                continue;
            }
            if (potion.isInstant()) {
                potion.affectEntity(shooter instanceof EntityLivingBase ? (EntityLivingBase) shooter : null, target, effect.getAmplifier(), 1.0D);
            } else {
                target.addPotionEffect(effect);
            }
        }
    }

    public static String getTooltipText(ItemStack stack) {
        StringBuilder builder = new StringBuilder("Effects: Poison");
        int poisonLevel = getPoisonLevel(stack);
        if (poisonLevel > 1) {
            builder.append(" ").append(toRoman(poisonLevel));
        }
        List<PotionEffect> extraEffects = getExtraEffects(stack);
        for (int i = 0; i < extraEffects.size(); i++) {
            builder.append(", ").append(getEffectDisplayName(extraEffects.get(i)));
            int level = extraEffects.get(i).getAmplifier() + 1;
            if (level > 1) {
                builder.append(" ").append(toRoman(level));
            }
        }
        return builder.toString();
    }

    private static PotionEffect choosePreferredEffect(PotionEffect existing, PotionEffect incoming) {
        if (incoming.getAmplifier() > existing.getAmplifier()) {
            return incoming;
        }
        return existing;
    }

    private static boolean isSameEffect(PotionEffect left, PotionEffect right) {
        return left.getPotionID() == right.getPotionID()
                && left.getAmplifier() == right.getAmplifier()
                && left.getDuration() == right.getDuration();
    }

    private static PotionEffect copyEffect(PotionEffect effect) {
        return new PotionEffect(effect.getPotionID(), effect.getDuration(), effect.getAmplifier(), effect.getIsAmbient());
    }

    private static PotionEffect normalizeEffect(PotionEffect effect) {
        Potion potion = getPotion(effect.getPotionID());
        int duration = potion != null && potion.isInstant() ? 1 : getConfiguredDurationTicks();
        return new PotionEffect(effect.getPotionID(), duration, clampAmplifier(effect.getAmplifier()), effect.getIsAmbient());
    }

    private static String getEffectDisplayName(PotionEffect effect) {
        Potion potion = effect == null || effect.getPotionID() <= 0 ? null : Potion.potionTypes[effect.getPotionID()];
        if (potion == null) {
            return "Unknown";
        }
        return StatCollector.translateToLocal(potion.getName());
    }

    private static int getMaxExtraEffects() {
        return Math.max(0, ModConfig.riftExplorerDartMaxPotionModifiers);
    }

    private static boolean isEffectAllowed(PotionEffect effect) {
        if (effect == null) {
            return false;
        }
        Potion potion = getPotion(effect.getPotionID());
        if (potion == null) {
            return false;
        }
        String[] filter = ModConfig.riftExplorerDartPotionEffectFilter;
        boolean whitelistMode = ModConfig.riftExplorerDartPotionEffectFilterWhitelistMode;
        if (filter == null || filter.length == 0) {
            return !whitelistMode;
        }

        boolean matched = false;
        for (int i = 0; i < filter.length; i++) {
            if (matchesConfiguredPotion(effect.getPotionID(), potion, filter[i])) {
                matched = true;
                break;
            }
        }
        return whitelistMode ? matched : !matched;
    }

    private static boolean matchesConfiguredPotion(int potionId, Potion potion, String entry) {
        if (entry == null) {
            return false;
        }
        String trimmed = entry.trim();
        if (trimmed.isEmpty()) {
            return false;
        }
        if (trimmed.startsWith("id:")) {
            return potionId == ConfigResolver.parseIntSafe(trimmed.substring(3).trim(), Integer.MIN_VALUE);
        }
        if (ConfigResolver.isInteger(trimmed)) {
            return potionId == ConfigResolver.parseIntSafe(trimmed, Integer.MIN_VALUE);
        }
        if (trimmed.startsWith("name:")) {
            trimmed = trimmed.substring(5).trim();
        }

        String normalizedEntry = normalizePotionToken(trimmed);
        if (normalizedEntry.isEmpty()) {
            return false;
        }

        if (normalizedEntry.equals(normalizePotionToken(potion.getName()))) {
            return true;
        }
        return normalizedEntry.equals(normalizePotionToken(StatCollector.translateToLocal(potion.getName())));
    }

    private static String normalizePotionToken(String value) {
        String normalized = ConfigResolver.normalizeToken(value);
        if (normalized.startsWith("potion")) {
            normalized = normalized.substring("potion".length());
        }
        return normalized;
    }

    private static Potion getPotion(int potionId) {
        return potionId > 0 && potionId < Potion.potionTypes.length ? Potion.potionTypes[potionId] : null;
    }

    private static int clampAmplifier(int amplifier) {
        if (amplifier < 0) {
            return 0;
        }
        int levelCap = ModConfig.riftExplorerDartPotionLevelCap;
        if (levelCap <= 0) {
            return amplifier;
        }
        return Math.min(amplifier, levelCap - 1);
    }

    private static int getConfiguredDurationTicks() {
        return Math.max(1, Math.round(Math.max(0.1f, ModConfig.riftExplorerDartPotionDurationSeconds) * 20.0f));
    }

    private static int clampPoisonLevel(int level) {
        if (level <= 1) {
            return 1;
        }
        return Math.min(MAX_POISON_LEVEL, level);
    }

    private static String toRoman(int level) {
        switch (level) {
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            default:
                return Integer.toString(level);
        }
    }
}
