package com.zyin.zyinhud;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import org.lwjgl.input.Keyboard;

import com.zyin.zyinhud.mods.ItemSelector;
import com.zyin.zyinhud.mods.QuickDeposit;

/**
 * This class is responsible for interacting with the configuration file.
 */
public class ZyinHUDConfig
{
    public static final String CATEGORY_QUICKDEPOSIT = "quickdeposit";
    public static final String CATEGORY_ITEMSELECTOR = "itemselector";

    public static Configuration config = null;
    
    /**
     * Loads every value from the configuration file.
     * @param configFile
     */
    public static void LoadConfigSettings(File configFile)
    {
    	ReadConfigSettings(configFile, true);
    }
    
    /**
     * Saves every value back to the config file.
     */
    public static void SaveConfigSettings()
    {
    	ReadConfigSettings(null, false);
    }
    
    /**
     * Creates the config file if it doesn't already exist.
     * It loads/saves config values from/to the config file.
     * @param configFile Standard Forge configuration file
     * @param loadSettings set to true to load the settings from the config file, 
     * or false to save the settings to the config file
     */
    private static void ReadConfigSettings(File configFile, boolean loadSettings)
    {
    	//NOTE: doing config.save() multiple times will bug out and add additional quotes to
    	//categories with more than 1 word
    	if(loadSettings)
    	{
            config = new Configuration(configFile);
            config.load();
    	}
        
        Property p;
        config.addCustomCategoryComment(CATEGORY_QUICKDEPOSIT, "Quick Stack allows you to inteligently deposit every item in your inventory quickly into a chest.");
        config.addCustomCategoryComment(CATEGORY_ITEMSELECTOR, "Item Selector allows you to conveniently swap your currently selected hotbar item with something in your inventory.");


        
        //CATEGORY_QUICKDEPOSIT
        p = config.get(CATEGORY_QUICKDEPOSIT, "EnableQuickDeposit", true);
        p.comment = "Enables Quick Deposit.";
        if(loadSettings)
        	QuickDeposit.Enabled = p.getBoolean(true);
        else
        	p.set(QuickDeposit.Enabled);

        p = config.get(CATEGORY_QUICKDEPOSIT, "IgnoreItemsInHotbar", false);
        p.comment = "Determines if items in your hotbar will be deposited into chests when '"
        			+ Keyboard.getKeyName(ZyinHUDKeyHandlers.KEY_BINDINGS[0].getKeyCode()) + "' is pressed.";
        if(loadSettings)
        	QuickDeposit.IgnoreItemsInHotbar = p.getBoolean(false);
        else
        	p.set(QuickDeposit.IgnoreItemsInHotbar);

        p = config.get(CATEGORY_QUICKDEPOSIT, "CloseChestAfterDepositing", false);
        p.comment = "Closes the chest GUI after you deposit your items in it. Allows quick and easy depositing of all your items into multiple chests.";
        if(loadSettings)
        	QuickDeposit.CloseChestAfterDepositing = p.getBoolean(false);
        else
        	p.set(QuickDeposit.CloseChestAfterDepositing);

        p = config.get(CATEGORY_QUICKDEPOSIT, "BlacklistTorch", false);
        p.comment = "Stop Quick Deposit from putting torches in chests?";
        if(loadSettings)
        	QuickDeposit.BlacklistTorch = p.getBoolean(false);
        else
        	p.set(QuickDeposit.BlacklistTorch);

        p = config.get(CATEGORY_QUICKDEPOSIT, "BlacklistWeapons", false);
        p.comment = "Stop Quick Deposit from putting swords and bows in chests?";
        if(loadSettings)
        	QuickDeposit.BlacklistWeapons = p.getBoolean(false);
        else
        	p.set(QuickDeposit.BlacklistWeapons);

        p = config.get(CATEGORY_QUICKDEPOSIT, "BlacklistArrow", false);
        p.comment = "Stop Quick Deposit from putting arrows in chests?";
        if(loadSettings)
        	QuickDeposit.BlacklistArrow = p.getBoolean(false);
        else
        	p.set(QuickDeposit.BlacklistArrow);

        p = config.get(CATEGORY_QUICKDEPOSIT, "BlacklistEnderPearl", false);
        p.comment = "Stop Quick Deposit from putting ender pearls in chests?";
        if(loadSettings)
        	QuickDeposit.BlacklistEnderPearl = p.getBoolean(false);
        else
        	p.set(QuickDeposit.BlacklistEnderPearl);

        p = config.get(CATEGORY_QUICKDEPOSIT, "BlacklistFood", false);
        p.comment = "Stop Quick Deposit from putting food in chests?";
        if(loadSettings)
        	QuickDeposit.BlacklistFood = p.getBoolean(false);
        else
        	p.set(QuickDeposit.BlacklistFood);

        p = config.get(CATEGORY_QUICKDEPOSIT, "BlacklistWaterBucket", false);
        p.comment = "Stop Quick Deposit from putting water buckets in chests?";
        if(loadSettings)
        	QuickDeposit.BlacklistWaterBucket = p.getBoolean(false);
        else
        	p.set(QuickDeposit.BlacklistWaterBucket);
        	
        //CATEGORY_ITEMSELECTOR
        p = config.get(CATEGORY_ITEMSELECTOR, "EnableItemSelector", true);
        p.comment = "Enables/Disable using mouse wheel scrolling whilst holding "
                + Keyboard.getKeyName( ZyinHUDKeyHandlers.KEY_BINDINGS[1].getKeyCode() ) + " to swap the selected item with an inventory item.";
        if(loadSettings)
          ItemSelector.Enabled = p.getBoolean(true);
        else
          p.set(ItemSelector.Enabled);

        p = config.get(CATEGORY_ITEMSELECTOR, "ItemSelectorTimeout", ItemSelector.defaultTimeout);
        p.comment = "Specifies how many ticks until the item selector confirms your choice and performs the item swap.";
        if(loadSettings)
          ItemSelector.SetTimeout(p.getInt(ItemSelector.defaultTimeout));
        else
          p.set(ItemSelector.GetTimeout());
        
        p = config.get(CATEGORY_ITEMSELECTOR, "ItemSelectorMode", "ALL");
        p.comment = "Sets the Item Selector mode.";
        if(loadSettings)
        	ItemSelector.Mode = ItemSelector.Modes.GetMode(p.getString());
        else
        	p.set(ItemSelector.Mode.name());

        p = config.get(CATEGORY_ITEMSELECTOR, "ItemSelectorSideButtons", false);
        p.comment = "Enable/disable use of side buttons for item selection.";
        if(loadSettings)
            ItemSelector.UseMouseSideButtons = p.getBoolean(false);
        else
            p.set(ItemSelector.UseMouseSideButtons);
        

        config.save();
    }
    
}
