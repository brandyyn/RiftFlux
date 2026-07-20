package com.voidsrift.riftflux.offlawn;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

public final class OffLawnPotions {
    public static final ResourceLocation HAPPY_ICON =
            new ResourceLocation("riftflux", "textures/gui/happy_potion.png");
    public static Potion happy;

    private OffLawnPotions() {
    }

    public static void init() {
        if (happy != null) {
            return;
        }
        int configuredId = ModConfig.offLawnBrightSunflowerHappyPotionId;
        int selectedId = findFreePotionId(configuredId);
        if (selectedId < 0) {
            FMLLog.severe("[RiftFlux] No free potion ID available for the Bright Sunflower Happy! effect.");
            return;
        }
        if (selectedId != configuredId) {
            FMLLog.warning(
                    "[RiftFlux] BrightSunflowerHappyPotionId %d is unavailable; using %d instead.",
                    configuredId,
                    selectedId
            );
        }
        happy = new PotionHappy(selectedId).setPotionName("potion.riftflux.happy");
    }

    private static int findFreePotionId(int configuredId) {
        if (isPotionSlotFree(configuredId)) {
            return configuredId;
        }
        int firstScan = configuredId < 0 ? 0 : Math.min(Potion.potionTypes.length, configuredId + 1);
        for (int id = firstScan; id < Potion.potionTypes.length; ++id) {
            if (Potion.potionTypes[id] == null) {
                return id;
            }
        }
        int wrapLimit = configuredId < 0 ? 0 : Math.min(configuredId, Potion.potionTypes.length);
        for (int id = 0; id < wrapLimit; ++id) {
            if (Potion.potionTypes[id] == null) {
                return id;
            }
        }
        return -1;
    }

    private static boolean isPotionSlotFree(int id) {
        return id >= 0 && id < Potion.potionTypes.length && Potion.potionTypes[id] == null;
    }
}
