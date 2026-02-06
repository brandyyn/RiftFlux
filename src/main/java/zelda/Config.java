package zelda;

import com.voidsrift.riftflux.ModConfig;

public class Config {
    public static boolean HEARTS_ENABLED = true;
    public static boolean DISABLE_REGEN = true;
    public static int HEARTPIECE_RARITY = 10;
    public static int STARTING_HEARTS = 3;
    public static int MAXIMUM_HEARTS = 20;
    public static int MOB_DROP = 3;
    public static int BLOCK_DROP = 20;

    private Config() {
    }

    public static void syncFromModConfig() {
        HEARTS_ENABLED = ModConfig.zeldaHeartsEnabled;
        DISABLE_REGEN = ModConfig.zeldaDisableRegen;
        HEARTPIECE_RARITY = ModConfig.zeldaHeartPieceRarity;
        STARTING_HEARTS = ModConfig.zeldaStartingHearts;
        MAXIMUM_HEARTS = ModConfig.zeldaMaximumHearts;
        MOB_DROP = ModConfig.zeldaMobDrop;
        BLOCK_DROP = ModConfig.zeldaBlockDrop;
    }
}
