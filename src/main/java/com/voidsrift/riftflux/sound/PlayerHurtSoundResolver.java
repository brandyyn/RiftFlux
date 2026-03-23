package com.voidsrift.riftflux.sound;

import com.voidsrift.riftflux.Constants;
import com.voidsrift.riftflux.ModConfig;

public final class PlayerHurtSoundResolver {
    private PlayerHurtSoundResolver() {
    }

    public static String resolvePlayerHurtSoundKey() {
        return resolvePlayerHurtSoundKey(true);
    }

    public static String resolvePlayerHurtSoundKey(boolean allowOof) {
        if (!allowOof) {
            return Constants.MODID + ":player_hurt";
        }
        double chance = ModConfig.playerOnlyHurtSoundOofChance;
        if (chance > 0.0D && Math.random() < chance) {
            return Constants.MODID + ":oof";
        }
        return Constants.MODID + ":player_hurt";
    }
}
