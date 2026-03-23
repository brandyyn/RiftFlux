package com.voidsrift.riftflux.specialarmor;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;

public final class SpecialArmorContent {
    public static Item slimeHelmet;
    public static Item doubleJumpBoots;
    public static Item skates;
    public static Item heavyBoots;

    private static boolean preInited;
    private static boolean initialized;
    private static boolean clientInited;

    private SpecialArmorContent() {
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (preInited) {
            return;
        }
        preInited = true;

        slimeHelmet = new ItemSpecialArmorPiece(ItemArmor.ArmorMaterial.IRON, 2, 0, "slimehelmet", "slimehelmet", "specialarmor2_1.png", "tooltip.riftflux.specialarmor.slimehelmet");
        doubleJumpBoots = new ItemSpecialArmorPiece(ItemArmor.ArmorMaterial.CHAIN, 6, 3, "doublejumpboots", "doublejumpboots", "specialarmor6_1.png", "tooltip.riftflux.specialarmor.doublejumpboots");
        skates = new ItemSpecialArmorPiece(ItemArmor.ArmorMaterial.IRON, 5, 3, "skates", "skates", "specialarmor5_1.png", "tooltip.riftflux.specialarmor.skates");
        heavyBoots = new ItemSpecialArmorPiece(ItemArmor.ArmorMaterial.IRON, 0, 3, "heavyboots", "heavyboots", "specialarmor_1.png", "tooltip.riftflux.specialarmor.heavyboots");

        GameRegistry.registerItem(slimeHelmet, "slime_helmet");
        GameRegistry.registerItem(doubleJumpBoots, "double_jump_boots");
        GameRegistry.registerItem(skates, "skates");
        GameRegistry.registerItem(heavyBoots, "heavy_boots");
    }

    public static void init(FMLInitializationEvent event) {
        if (initialized) {
            return;
        }
        initialized = true;

        addDungeonLootIfEnabled(ModConfig.specialArmorLootSlimeHelmet, slimeHelmet);
        addDungeonLootIfEnabled(ModConfig.specialArmorLootDoubleJumpBoots, doubleJumpBoots);
        addDungeonLootIfEnabled(ModConfig.specialArmorLootSkates, skates);
        addDungeonLootIfEnabled(ModConfig.specialArmorLootHeavyBoots, heavyBoots);
    }

    public static void initClient() {
        if (clientInited) {
            return;
        }
        clientInited = true;
        FMLCommonHandler.instance().bus().register(new SpecialArmorClientEvents());
    }

    private static void addDungeonLootIfEnabled(boolean enabled, Item item) {
        if (!enabled || item == null) {
            return;
        }
        ChestGenHooks.addItem(
                ChestGenHooks.DUNGEON_CHEST,
                new WeightedRandomChestContent(new ItemStack(item), 1, 1, 8)
        );
    }
}
