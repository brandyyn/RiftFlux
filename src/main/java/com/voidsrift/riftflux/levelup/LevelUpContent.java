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
import net.minecraft.item.Item;

import java.util.Locale;

public final class LevelUpContent {
    private static LevelUp module;
    private static boolean preInited;
    private static boolean initialized;
    private static boolean activated;

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
        if (!activated || initialized || module == null) {
            return;
        }
        initialized = true;
        module.init(event);
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
}
