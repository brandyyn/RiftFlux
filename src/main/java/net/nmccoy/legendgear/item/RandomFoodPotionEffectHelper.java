package net.nmccoy.legendgear.item;

import com.voidsrift.riftflux.util.ConfiguredPotionEffectHelper;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.List;

public final class RandomFoodPotionEffectHelper {
    private RandomFoodPotionEffectHelper() {
    }

    public static PotionEffect getRandomConfiguredEffect(World world, String[] entries) {
        if (world == null) {
            return null;
        }
        List<PotionEffect> effects = buildConfiguredEffects(entries);
        if (effects.isEmpty()) {
            return null;
        }
        return effects.get(world.rand.nextInt(effects.size()));
    }

    static List<PotionEffect> buildConfiguredEffects(String[] entries) {
        return ConfiguredPotionEffectHelper.parseEffects(entries);
    }
}
