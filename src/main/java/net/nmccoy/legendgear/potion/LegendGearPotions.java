package net.nmccoy.legendgear.potion;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.nmccoy.legendgear.LegendGear2;

public final class LegendGearPotions {
    public static final ResourceLocation icon = new ResourceLocation("legendgear", "textures/gui/potion.png");

    private LegendGearPotions() {
    }

    public static void init() {
        if (LegendGear2.manaRegenPotion != null && LegendGear2.jumpPenaltyPotion != null) {
            return;
        }

        if (LegendGear2.manaRegenPotion == null) {
            int selectedId = findFreePotionId(LegendGear2.CONFIG_MANA_REGEN_POTION_ID);
            if (selectedId < 0) {
                FMLLog.severe("[RiftFlux] No free potion ID available for LegendGear mana regeneration.");
            } else {
                if (selectedId != LegendGear2.CONFIG_MANA_REGEN_POTION_ID) {
                    FMLLog.warning(
                            "[RiftFlux] LegendGear manaRegenPotionId %d is unavailable; using %d instead.",
                            LegendGear2.CONFIG_MANA_REGEN_POTION_ID,
                            selectedId
                    );
                }

                LegendGear2.manaRegenPotion = new PotionManaRegen(selectedId, false, 0x49A9FF).setPotionName("potion.legendgearManaRegen");
            }
        }

        if (LegendGear2.jumpPenaltyPotion == null) {
            int preferredId = LegendGear2.CONFIG_GROUNDED_POTION_ID;
            int selectedId = findFreePotionId(preferredId);
            if (selectedId < 0) {
                FMLLog.severe("[RiftFlux] No free potion ID available for LegendGear jump penalty effect.");
            } else {
                if (selectedId != preferredId) {
                    FMLLog.warning(
                            "[RiftFlux] LegendGear jump penalty potion slot %d is unavailable; using %d instead.",
                            preferredId,
                            selectedId
                    );
                }

                LegendGear2.jumpPenaltyPotion = new PotionJumpPenalty(selectedId, true, 0x6E97C8).setPotionName("potion.legendgearJumpPenalty");
            }
        }
    }

    private static int findFreePotionId(int configuredId) {
        if (isPotionSlotFree(configuredId)) {
            return configuredId;
        }

        int firstScan = configuredId < 0 ? 0 : Math.min(Potion.potionTypes.length, configuredId + 1);
        for (int i = firstScan; i < Potion.potionTypes.length; ++i) {
            if (Potion.potionTypes[i] == null) {
                return i;
            }
        }

        int wrapLimit = configuredId < 0 ? 0 : Math.min(configuredId, Potion.potionTypes.length);
        for (int i = 0; i < wrapLimit; ++i) {
            if (Potion.potionTypes[i] == null) {
                return i;
            }
        }

        return -1;
    }

    private static boolean isPotionSlotFree(int potionId) {
        return potionId >= 0 && potionId < Potion.potionTypes.length && Potion.potionTypes[potionId] == null;
    }
}
