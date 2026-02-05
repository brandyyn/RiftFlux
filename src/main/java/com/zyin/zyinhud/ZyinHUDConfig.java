package com.zyin.zyinhud;

import com.voidsrift.riftflux.ModConfig;
import com.zyin.zyinhud.mods.ItemSelector;
import com.zyin.zyinhud.mods.QuickDeposit;
import net.minecraftforge.common.config.Configuration;

public class ZyinHUDConfig {
    public static final String CATEGORY_QUICKDEPOSIT = "quickdeposit";
    public static final String CATEGORY_ITEMSELECTOR = "itemselector";

    private ZyinHUDConfig() {
    }

    public static void LoadConfigSettings() {
        syncFromModConfig();
    }

    public static void SaveConfigSettings() {
        saveToModConfig();
    }

    public static void syncFromModConfig() {
        QuickDeposit.Enabled = ModConfig.zyinQuickDepositEnabled;
        QuickDeposit.IgnoreItemsInHotbar = ModConfig.zyinQuickDepositIgnoreHotbar;
        QuickDeposit.CloseChestAfterDepositing = ModConfig.zyinQuickDepositCloseChest;
        QuickDeposit.BlacklistTorch = ModConfig.zyinQuickDepositBlacklistTorch;
        QuickDeposit.BlacklistWeapons = ModConfig.zyinQuickDepositBlacklistWeapons;
        QuickDeposit.BlacklistArrow = ModConfig.zyinQuickDepositBlacklistArrow;
        QuickDeposit.BlacklistEnderPearl = ModConfig.zyinQuickDepositBlacklistEnderPearl;
        QuickDeposit.BlacklistFood = ModConfig.zyinQuickDepositBlacklistFood;
        QuickDeposit.BlacklistWaterBucket = ModConfig.zyinQuickDepositBlacklistWaterBucket;
        QuickDeposit.BlacklistClockCompass = ModConfig.zyinQuickDepositBlacklistClockCompass;

        ItemSelector.Enabled = ModConfig.zyinItemSelectorEnabled;
        ItemSelector.SetTimeout(ModConfig.zyinItemSelectorTimeout);
        ItemSelector.Mode = ItemSelector.Modes.GetMode(ModConfig.zyinItemSelectorMode);
        ItemSelector.UseMouseSideButtons = ModConfig.zyinItemSelectorSideButtons;
        ItemSelector.HudYOffset = ModConfig.zyinItemSelectorHudOffsetY;
        ItemSelector.IncludeHotbar = ModConfig.zyinItemSelectorIncludeHotbar;
    }

    private static void saveToModConfig() {
        ModConfig.zyinQuickDepositEnabled = QuickDeposit.Enabled;
        ModConfig.zyinQuickDepositIgnoreHotbar = QuickDeposit.IgnoreItemsInHotbar;
        ModConfig.zyinQuickDepositCloseChest = QuickDeposit.CloseChestAfterDepositing;
        ModConfig.zyinQuickDepositBlacklistTorch = QuickDeposit.BlacklistTorch;
        ModConfig.zyinQuickDepositBlacklistWeapons = QuickDeposit.BlacklistWeapons;
        ModConfig.zyinQuickDepositBlacklistArrow = QuickDeposit.BlacklistArrow;
        ModConfig.zyinQuickDepositBlacklistEnderPearl = QuickDeposit.BlacklistEnderPearl;
        ModConfig.zyinQuickDepositBlacklistFood = QuickDeposit.BlacklistFood;
        ModConfig.zyinQuickDepositBlacklistWaterBucket = QuickDeposit.BlacklistWaterBucket;
        ModConfig.zyinQuickDepositBlacklistClockCompass = QuickDeposit.BlacklistClockCompass;

        ModConfig.zyinItemSelectorEnabled = ItemSelector.Enabled;
        ModConfig.zyinItemSelectorTimeout = ItemSelector.GetTimeout();
        ModConfig.zyinItemSelectorMode = ItemSelector.Mode.name();
        ModConfig.zyinItemSelectorSideButtons = ItemSelector.UseMouseSideButtons;
        ModConfig.zyinItemSelectorHudOffsetY = ItemSelector.HudYOffset;
        ModConfig.zyinItemSelectorIncludeHotbar = ItemSelector.IncludeHotbar;

        Configuration config = ModConfig.config;
        if (config == null) {
            return;
        }

        config.get(CATEGORY_QUICKDEPOSIT, "EnableQuickDeposit", true)
                .set(QuickDeposit.Enabled);
        config.get(CATEGORY_QUICKDEPOSIT, "IgnoreItemsInHotbar", false)
                .set(QuickDeposit.IgnoreItemsInHotbar);
        config.get(CATEGORY_QUICKDEPOSIT, "CloseChestAfterDepositing", false)
                .set(QuickDeposit.CloseChestAfterDepositing);
        config.get(CATEGORY_QUICKDEPOSIT, "BlacklistTorch", false)
                .set(QuickDeposit.BlacklistTorch);
        config.get(CATEGORY_QUICKDEPOSIT, "BlacklistWeapons", false)
                .set(QuickDeposit.BlacklistWeapons);
        config.get(CATEGORY_QUICKDEPOSIT, "BlacklistArrow", false)
                .set(QuickDeposit.BlacklistArrow);
        config.get(CATEGORY_QUICKDEPOSIT, "BlacklistEnderPearl", false)
                .set(QuickDeposit.BlacklistEnderPearl);
        config.get(CATEGORY_QUICKDEPOSIT, "BlacklistFood", false)
                .set(QuickDeposit.BlacklistFood);
        config.get(CATEGORY_QUICKDEPOSIT, "BlacklistWaterBucket", false)
                .set(QuickDeposit.BlacklistWaterBucket);
        config.get(CATEGORY_QUICKDEPOSIT, "BlacklistClockCompass", false)
                .set(QuickDeposit.BlacklistClockCompass);

        config.get(CATEGORY_ITEMSELECTOR, "EnableItemSelector", true)
                .set(ItemSelector.Enabled);
        config.get(CATEGORY_ITEMSELECTOR, "ItemSelectorTimeout", ItemSelector.defaultTimeout)
                .set(ItemSelector.GetTimeout());
        config.get(CATEGORY_ITEMSELECTOR, "ItemSelectorMode", "ALL")
                .set(ItemSelector.Mode.name());
        config.get(CATEGORY_ITEMSELECTOR, "ItemSelectorSideButtons", false)
                .set(ItemSelector.UseMouseSideButtons);
        config.get(CATEGORY_ITEMSELECTOR, "ItemSelectorHudOffsetY", 0)
                .set(ItemSelector.HudYOffset);
        config.get(CATEGORY_ITEMSELECTOR, "ItemSelectorIncludeHotbar", true)
                .set(ItemSelector.IncludeHotbar);

        config.save();
    }
}
