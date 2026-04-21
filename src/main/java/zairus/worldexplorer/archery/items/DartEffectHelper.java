package zairus.worldexplorer.archery.items;

import com.voidsrift.riftflux.ModConfig;
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
                    effects.add(normalizeEffect(new PotionEffect(effectId, getConfiguredDurationTicks(), 0)));
                }
            }
            return effects;
        }
        NBTTagList effectList = tag.getTagList(EXTRA_EFFECTS_KEY, 10);
        for (int i = 0; i < effectList.tagCount(); i++) {
            PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT(effectList.getCompoundTagAt(i));
            if (effect != null && effect.getPotionID() > 0) {
                effects.add(normalizeEffect(effect));
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

            if (rawEffect.getPotionID() == Potion.poison.id) {
                if (poisonLevel < MAX_POISON_LEVEL) {
                    poisonLevel++;
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
            target.addPotionEffect(normalizeEffect(extraEffects.get(i)));
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
        return new PotionEffect(effect.getPotionID(), getConfiguredDurationTicks(), effect.getAmplifier(), effect.getIsAmbient());
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
