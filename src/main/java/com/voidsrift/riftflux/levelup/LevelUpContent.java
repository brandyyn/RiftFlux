package com.voidsrift.riftflux.levelup;

import assets.levelup.LevelUp;
import assets.levelup.SkillClientProxy;
import assets.levelup.SkillProxy;
import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

import java.util.Locale;

public final class LevelUpContent {
    private static LevelUp module;
    private static boolean preInited;
    private static boolean initialized;
    private static boolean activated;
    private static boolean dungeonLootRegistered;

    private LevelUpContent() {
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (preInited) {
            return;
        }
        preInited = true;

        if (!ModConfig.enableLevelUpModule) {
            return;
        }

        if (Loader.isModLoaded(LevelUp.ID)) {
            activated = true;
            return;
        }

        ensureProxy();
        if (LevelUp.instance == null) {
            LevelUp.instance = new LevelUp();
        }
        module = LevelUp.instance;
        module.preInit(event);
        activated = true;
    }

    public static void init(FMLInitializationEvent event) {
        if (!activated || initialized) {
            return;
        }
        initialized = true;
        if (module != null) {
            module.init(event);
        }
        registerUnlearningBookDungeonLoot();
    }

    public static boolean isEnabled() {
        return activated;
    }

    public static Item resolveLegacyItemAlias(String fullName) {
        if (!activated || fullName == null) {
            return null;
        }

        String lower = fullName.toLowerCase(Locale.ROOT);
        if ("levelup:xptalisman".equals(lower) || "levelup:talisman of wonder".equals(lower)) {
            if (!ModConfig.levelUpRegisterTalismanOfWonder) {
                return null;
            }
            return GameRegistry.findItem(LevelUp.ID, "xpTalisman");
        }
        if ("levelup:respecbook".equals(lower) || "levelup:book of unlearning".equals(lower)) {
            return GameRegistry.findItem(LevelUp.ID, "respecBook");
        }
        return null;
    }

    private static void ensureProxy() {
        if (LevelUp.proxy != null) {
            return;
        }
        Side side = FMLCommonHandler.instance().getSide();
        if (side == Side.CLIENT) {
            LevelUp.proxy = new SkillClientProxy();
        } else {
            LevelUp.proxy = new SkillProxy();
        }
    }

    private static void registerUnlearningBookDungeonLoot() {
        if (dungeonLootRegistered) {
            return;
        }
        dungeonLootRegistered = true;

        if (!ModConfig.levelUpEnableUnlearningBook) {
            return;
        }

        int weight = Math.max(0, ModConfig.levelUpUnlearningBookDungeonLootWeight);
        if (weight <= 0) {
            return;
        }

        Item unlearningBook = GameRegistry.findItem(LevelUp.ID, "respecBook");
        if (unlearningBook == null) {
            return;
        }

        ChestGenHooks.addItem(
                ChestGenHooks.DUNGEON_CHEST,
                new WeightedRandomChestContent(
                        new ItemStack(unlearningBook, 1, ModConfig.levelUpUnlearningBookResetClass ? 1 : 0),
                        1,
                        1,
                        weight
                )
        );
    }
}
