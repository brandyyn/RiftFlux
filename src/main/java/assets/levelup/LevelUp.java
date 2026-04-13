/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.common.Mod
 *  cpw.mods.fml.common.Mod$EventHandler
 *  cpw.mods.fml.common.Mod$Instance
 *  cpw.mods.fml.common.SidedProxy
 *  cpw.mods.fml.common.event.FMLInitializationEvent
 *  cpw.mods.fml.common.event.FMLMissingMappingsEvent
 *  cpw.mods.fml.common.event.FMLMissingMappingsEvent$MissingMapping
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  cpw.mods.fml.common.network.FMLEventChannel
 *  cpw.mods.fml.common.network.NetworkRegistry
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.block.Block
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.IRecipe
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.config.Configuration
 *  net.minecraftforge.common.config.Property
 *  net.minecraftforge.oredict.ShapedOreRecipe
 *  net.minecraftforge.oredict.ShapelessOreRecipe
 */
package assets.levelup;

import assets.levelup.BowEventHandler;
import assets.levelup.ClassBonus;
import assets.levelup.FMLEventHandler;
import assets.levelup.FightEventHandler;
import assets.levelup.ItemRespecBook;
import assets.levelup.PlayerEventHandler;
import assets.levelup.PlayerExtendedProperties;
import assets.levelup.SkillPacketHandler;
import assets.levelup.SkillProxy;
import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public final class LevelUp {
    public static final String ID = "levelup";
    public static LevelUp instance;
    public static SkillProxy proxy;
    private Property[] clientProperties;
    private Property[] serverProperties;
    private static Item xpTalisman;
    private static Item respecBook;
    private static Map<Item, Integer> towItems;
    private static List[] tiers;
    private static Configuration config;
    public static boolean allowHUD;
    public static boolean renderTopLeft;
    public static boolean renderExpBar;
    public static boolean changeFOV;
    private static boolean bonusMiningXP;
    private static boolean bonusCraftingXP;
    private static boolean bonusFightingXP;
    private static boolean oreMiningXP;
    public static FMLEventChannel initChannel;
    public static FMLEventChannel skillChannel;
    public static FMLEventChannel classChannel;
    public static FMLEventChannel configChannel;

    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register((Object)BowEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register((Object)FightEventHandler.INSTANCE);
        SkillPacketHandler sk = new SkillPacketHandler();
        initChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(SkillPacketHandler.CHAN[0]);
        initChannel.register((Object)sk);
        classChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(SkillPacketHandler.CHAN[1]);
        classChannel.register((Object)sk);
        skillChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(SkillPacketHandler.CHAN[2]);
        skillChannel.register((Object)sk);
        configChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(SkillPacketHandler.CHAN[3]);
        configChannel.register((Object)sk);
        proxy.registerGui();
    }

    public void preInit(FMLPreInitializationEvent event) {
        config = ModConfig.config;
        config.addCustomCategoryComment("LevelUp.HUD", "Entirely client side. No need to sync.");
        this.initClientProperties();
        config.addCustomCategoryComment("LevelUp.Items", "Integrated LevelUp item settings.");
        config.addCustomCategoryComment("LevelUp.Cheats", "Integrated LevelUp gameplay settings. These are synced to clients on dedicated servers.");
        config.addCustomCategoryComment("LevelUp.BlackList", "Integrated LevelUp blacklist settings.");
        this.initServerProperties();
        boolean talismanEnabled = ModConfig.levelUpRegisterTalismanOfWonder;
        boolean bookEnabled = ModConfig.levelUpEnableUnlearningBook;
        boolean legacyRecipes = ModConfig.levelUpEnableLegacyRecipes;
        this.useServerProperties();
        List<String> blackList = Arrays.asList(ModConfig.levelUpFarmingBlacklist);
        FMLEventHandler.INSTANCE.addCropsToBlackList(blackList);
        if (config.hasChanged()) {
            config.save();
        }
        if (talismanEnabled) {
            towItems = new HashMap<Item, Integer>();
            towItems.put(Item.getItemFromBlock((Block)Blocks.log), 2);
            towItems.put(Items.coal, 2);
            towItems.put(Items.brick, 4);
            towItems.put(Items.book, 4);
            towItems.put(Item.getItemFromBlock((Block)Blocks.iron_ore), 8);
            towItems.put(Items.dye, 8);
            towItems.put(Items.redstone, 8);
            towItems.put(Items.bread, 10);
            towItems.put(Items.melon, 10);
            towItems.put(Item.getItemFromBlock((Block)Blocks.pumpkin), 10);
            towItems.put(Items.cooked_porkchop, 12);
            towItems.put(Items.cooked_beef, 12);
            towItems.put(Items.cooked_chicken, 12);
            towItems.put(Items.cooked_fished, 12);
            towItems.put(Items.iron_ingot, 16);
            towItems.put(Item.getItemFromBlock((Block)Blocks.gold_ore), 20);
            towItems.put(Items.gold_ingot, 24);
            towItems.put(Items.diamond, 40);
            xpTalisman = new Item().setUnlocalizedName("xpTalisman").setTextureName("levelup:XPTalisman").setCreativeTab(CreativeTabs.tabTools);
            GameRegistry.registerItem((Item)xpTalisman, (String)"xpTalisman");
            GameRegistry.addRecipe((IRecipe)new ShapedOreRecipe(xpTalisman, new Object[]{"GG ", " R ", " GG", Character.valueOf('G'), "ingotGold", Character.valueOf('R'), "dustRedstone"}));
            GameRegistry.addShapelessRecipe((ItemStack)new ItemStack(xpTalisman), (Object[])new Object[]{xpTalisman, Items.coal});
            GameRegistry.addRecipe((IRecipe)new ShapelessOreRecipe(xpTalisman, new Object[]{xpTalisman, "oreGold"}));
            GameRegistry.addRecipe((IRecipe)new ShapelessOreRecipe(xpTalisman, new Object[]{xpTalisman, "oreIron"}));
            GameRegistry.addRecipe((IRecipe)new ShapelessOreRecipe(xpTalisman, new Object[]{xpTalisman, "gemDiamond"}));
            GameRegistry.addRecipe((IRecipe)new ShapelessOreRecipe(xpTalisman, new Object[]{xpTalisman, "logWood"}));
            GameRegistry.addShapelessRecipe((ItemStack)new ItemStack(xpTalisman), (Object[])new Object[]{xpTalisman, Items.brick});
            GameRegistry.addShapelessRecipe((ItemStack)new ItemStack(xpTalisman), (Object[])new Object[]{xpTalisman, Items.book});
            GameRegistry.addRecipe((IRecipe)new ShapelessOreRecipe(xpTalisman, new Object[]{xpTalisman, "gemLapis"}));
            GameRegistry.addRecipe((IRecipe)new ShapelessOreRecipe(xpTalisman, new Object[]{xpTalisman, "dustRedstone"}));
            GameRegistry.addShapelessRecipe((ItemStack)new ItemStack(xpTalisman), (Object[])new Object[]{xpTalisman, Items.bread});
            GameRegistry.addShapelessRecipe((ItemStack)new ItemStack(xpTalisman), (Object[])new Object[]{xpTalisman, Items.melon});
            GameRegistry.addShapelessRecipe((ItemStack)new ItemStack(xpTalisman), (Object[])new Object[]{xpTalisman, Items.cooked_porkchop});
            GameRegistry.addShapelessRecipe((ItemStack)new ItemStack(xpTalisman), (Object[])new Object[]{xpTalisman, Items.cooked_beef});
            GameRegistry.addShapelessRecipe((ItemStack)new ItemStack(xpTalisman), (Object[])new Object[]{xpTalisman, Items.cooked_chicken});
            GameRegistry.addShapelessRecipe((ItemStack)new ItemStack(xpTalisman), (Object[])new Object[]{xpTalisman, Items.cooked_fished});
            GameRegistry.addRecipe((IRecipe)new ShapelessOreRecipe(xpTalisman, new Object[]{xpTalisman, "ingotIron"}));
            GameRegistry.addRecipe((IRecipe)new ShapelessOreRecipe(xpTalisman, new Object[]{xpTalisman, "ingotGold"}));
            GameRegistry.addShapelessRecipe((ItemStack)new ItemStack(xpTalisman), (Object[])new Object[]{xpTalisman, Blocks.pumpkin});
        } else {
            xpTalisman = null;
            towItems = null;
        }
        if (bookEnabled) {
            respecBook = new ItemRespecBook().setUnlocalizedName("respecBook").setTextureName("levelup:RespecBook").setCreativeTab(CreativeTabs.tabTools);
            GameRegistry.registerItem((Item)respecBook, (String)"respecBook");
            ItemStack output = new ItemStack(respecBook);
            if (ModConfig.levelUpUnlearningBookResetClass) {
                output.setItemDamage(1);
            }
            GameRegistry.addRecipe((ItemStack)output, (Object[])new Object[]{"OEO", "DBD", "ODO", Character.valueOf('O'), Blocks.obsidian, Character.valueOf('D'), new ItemStack(Items.dye), Character.valueOf('E'), Items.ender_pearl, Character.valueOf('B'), Items.book});
        }
        if (legacyRecipes) {
            GameRegistry.addShapelessRecipe((ItemStack)new ItemStack(Items.pumpkin_seeds, 4), (Object[])new Object[]{Blocks.pumpkin});
            GameRegistry.addRecipe((ItemStack)new ItemStack(Blocks.gravel, 4), (Object[])new Object[]{"##", "##", Character.valueOf('#'), Items.flint});
        }
        FMLCommonHandler.instance().bus().register((Object)FMLEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register((Object)new PlayerEventHandler());
    }

    private void initClientProperties() {
        this.clientProperties = new Property[]{
                config.get("LevelUp.HUD", "AllowHud", ModConfig.levelUpAllowHud, "If anything from the LevelUp HUD should be rendered on screen at all.").setRequiresMcRestart(true),
                config.get("LevelUp.HUD", "RenderHudOnTopLeft", ModConfig.levelUpRenderHudTopLeft),
                config.get("LevelUp.HUD", "RenderHudOnExpBar", ModConfig.levelUpRenderHudExpBar),
                config.get("LevelUp.FOV", "ChangeFovWithSpeed", ModConfig.levelUpChangeFovWithSpeed, "Should FOV change based on player speed from athletics / sneak skills.")
        };
        allowHUD = this.clientProperties[0].getBoolean();
        renderTopLeft = this.clientProperties[1].getBoolean();
        renderExpBar = this.clientProperties[2].getBoolean();
        changeFOV = this.clientProperties[3].getBoolean();
        ModConfig.levelUpAllowHud = allowHUD;
        ModConfig.levelUpRenderHudTopLeft = renderTopLeft;
        ModConfig.levelUpRenderHudExpBar = renderExpBar;
        ModConfig.levelUpChangeFovWithSpeed = changeFOV;
    }

    private void initServerProperties() {
        String cat = "Cheats";
        String limitedBonus = "This is a bonus related to a few classes";
        this.serverProperties = new Property[]{
                config.get("LevelUp.Cheats", "MaxPointsPerSkill", ModConfig.levelUpMaxPointsPerSkill, "Minimum is 1"),
                config.get("LevelUp.Cheats", "BonusPointsForClasses", ModConfig.levelUpBonusPointsForClasses, "Points given when choosing a class, allocated automatically.\n Minimum is 0, Maximum is max points per skill times 2"),
                config.get("LevelUp.Cheats", "XpGainPerLevel", ModConfig.levelUpXpGainPerLevel, "Minimum is 0"),
                config.get("LevelUp.Cheats", "SkillPointsLostOnDeathPercent", ModConfig.levelUpSkillPointsLostOnDeathPercent, "How much skill points are lost on death, in percent.").setMinValue(0).setMaxValue(100),
                config.get("LevelUp.Cheats", "UseOldSpeedDirtAndGravelDigging", ModConfig.levelUpUseOldSpeedDirtAndGravelDigging),
                config.get("LevelUp.Cheats", "UseOldSpeedRedstoneBreaking", ModConfig.levelUpUseOldSpeedRedstoneBreaking, "Makes the redstone ore mining efficient"),
                config.get("LevelUp.Cheats", "ResetPlayerClassOnDeath", ModConfig.levelUpResetPlayerClassOnDeath, "Does the player lose the class they chose on death?"),
                config.get("LevelUp.Cheats", "PreventDuplicatedOresPlacing", ModConfig.levelUpPreventDuplicatedOresPlacing, "Some skills duplicate ores; this prevents infinite duplication by placing them back down."),
                config.get("LevelUp.Cheats", "AddBonusXpOnCraft", ModConfig.levelUpAddBonusXpOnCraft, limitedBonus),
                config.get("LevelUp.Cheats", "AddBonusXpOnMining", ModConfig.levelUpAddBonusXpOnMining, limitedBonus),
                config.get("LevelUp.Cheats", "AddXpOnCraftingSomeItems", ModConfig.levelUpAddXpOnCraftingSomeItems, "This is a global bonus, limited to a few craftable items"),
                config.get("LevelUp.Cheats", "AddXpOnMiningSomeOre", ModConfig.levelUpAddXpOnMiningSomeOre, "This is a global bonus, limited to a few ores"),
                config.get("LevelUp.Cheats", "AddBonusXpOnFighting", ModConfig.levelUpAddBonusXpOnFighting, limitedBonus)
        };
    }

    public void useServerProperties() {
        ClassBonus.setSkillMax(this.serverProperties[0].getInt());
        ClassBonus.setBonusPoints(this.serverProperties[1].getInt());
        double opt = this.serverProperties[2].getDouble();
        if (opt >= 0.0) {
            PlayerEventHandler.xpPerLevel = opt <= (double)ClassBonus.getMaxSkillPoints() ? opt : (double)ClassBonus.getMaxSkillPoints();
        }
        PlayerEventHandler.resetSkillOnDeath = (float)this.serverProperties[3].getInt() / 100.0f;
        PlayerEventHandler.oldSpeedDigging = this.serverProperties[4].getBoolean();
        PlayerEventHandler.oldSpeedRedstone = this.serverProperties[5].getBoolean();
        PlayerEventHandler.resetClassOnDeath = this.serverProperties[6].getBoolean();
        PlayerEventHandler.noPlaceDuplicate = this.serverProperties[7].getBoolean();
        bonusCraftingXP = this.serverProperties[8].getBoolean();
        bonusMiningXP = this.serverProperties[9].getBoolean();
        oreMiningXP = this.serverProperties[11].getBoolean();
        bonusFightingXP = this.serverProperties[12].getBoolean();
        ModConfig.levelUpMaxPointsPerSkill = ClassBonus.getMaxSkillPoints();
        ModConfig.levelUpBonusPointsForClasses = ClassBonus.getBonusPoints();
        ModConfig.levelUpXpGainPerLevel = PlayerEventHandler.xpPerLevel;
        ModConfig.levelUpSkillPointsLostOnDeathPercent = this.serverProperties[3].getInt();
        ModConfig.levelUpUseOldSpeedDirtAndGravelDigging = PlayerEventHandler.oldSpeedDigging;
        ModConfig.levelUpUseOldSpeedRedstoneBreaking = PlayerEventHandler.oldSpeedRedstone;
        ModConfig.levelUpResetPlayerClassOnDeath = PlayerEventHandler.resetClassOnDeath;
        ModConfig.levelUpPreventDuplicatedOresPlacing = PlayerEventHandler.noPlaceDuplicate;
        ModConfig.levelUpAddBonusXpOnCraft = bonusCraftingXP;
        ModConfig.levelUpAddBonusXpOnMining = bonusMiningXP;
        ModConfig.levelUpAddXpOnCraftingSomeItems = this.serverProperties[10].getBoolean();
        ModConfig.levelUpAddXpOnMiningSomeOre = oreMiningXP;
        ModConfig.levelUpAddBonusXpOnFighting = bonusFightingXP;
        if (this.serverProperties[10].getBoolean()) {
            List<Item> ingrTier1 = Arrays.asList(Items.stick, Items.leather, Item.getItemFromBlock((Block)Blocks.stone));
            List<Item> ingrTier2 = Arrays.asList(Items.iron_ingot, Items.gold_ingot, Items.paper, Items.slime_ball);
            List<Item> ingrTier3 = Arrays.asList(Items.redstone, Items.glowstone_dust, Items.ender_pearl);
            List<Item> ingrTier4 = Arrays.asList(Items.diamond);
            tiers = new List[]{ingrTier1, ingrTier2, ingrTier3, ingrTier4};
        }
    }

    public Property[] getServerProperties() {
        return this.serverProperties;
    }

    public boolean[] getClientProperties() {
        boolean[] result = new boolean[this.clientProperties.length];
        for (int i = 0; i < this.clientProperties.length; ++i) {
            result[i] = this.clientProperties[i].getBoolean();
        }
        return result;
    }

    public void refreshValues(boolean[] values) {
        if (values.length == this.clientProperties.length) {
            allowHUD = values[0];
            renderTopLeft = values[1];
            renderExpBar = values[2];
            changeFOV = values[3];
            ModConfig.levelUpAllowHud = allowHUD;
            ModConfig.levelUpRenderHudTopLeft = renderTopLeft;
            ModConfig.levelUpRenderHudExpBar = renderExpBar;
            ModConfig.levelUpChangeFovWithSpeed = changeFOV;
            for (int i = 0; i < values.length; ++i) {
                this.clientProperties[i].set(values[i]);
            }
            config.save();
        }
    }

    public static Item resolveLegacyItemAlias(String fullName) {
        if (fullName == null) {
            return null;
        }
        if ("levelup:Talisman of Wonder".equals(fullName) || "levelup:xpTalisman".equals(fullName)) {
            return xpTalisman;
        }
        if ("levelup:Book of Unlearning".equals(fullName) || "levelup:respecBook".equals(fullName)) {
            return respecBook;
        }
        return null;
    }

    public static void giveBonusFightingXP(EntityPlayer player) {
        byte pClass;
        if (bonusFightingXP && ((pClass = PlayerExtendedProperties.getPlayerClass(player)) == 2 || pClass == 5 || pClass == 8 || pClass == 11)) {
            player.addExperience(2);
        }
    }

    public static void giveBonusCraftingXP(EntityPlayer player) {
        byte pClass;
        if (bonusCraftingXP && ((pClass = PlayerExtendedProperties.getPlayerClass(player)) == 3 || pClass == 6 || pClass == 9 || pClass == 12)) {
            LevelUp.runBonusCounting(player, 1);
        }
    }

    public static void giveBonusMiningXP(EntityPlayer player) {
        byte pClass;
        if (bonusMiningXP && ((pClass = PlayerExtendedProperties.getPlayerClass(player)) == 1 || pClass == 4 || pClass == 7 || pClass == 10)) {
            LevelUp.runBonusCounting(player, 0);
        }
    }

    private static void runBonusCounting(EntityPlayer player, int type) {
        Map<String, int[]> counters = PlayerExtendedProperties.getCounterMap(player);
        int[] bonus = counters.get(PlayerExtendedProperties.counters[2]);
        if (bonus == null || bonus.length == 0) {
            bonus = new int[]{0, 0, 0};
        }
        if (bonus[type] < 4) {
            int n = type;
            bonus[n] = bonus[n] + 1;
        } else {
            bonus[type] = 0;
            player.addExperience(2);
        }
        counters.put(PlayerExtendedProperties.counters[2], bonus);
    }

    public static void giveCraftingXP(EntityPlayer player, ItemStack itemstack) {
        if (tiers != null) {
            for (int i = 0; i < tiers.length; ++i) {
                if (!tiers[i].contains(itemstack.getItem())) continue;
                LevelUp.incrementCraftCounter(player, i);
            }
        }
    }

    private static void incrementCraftCounter(EntityPlayer player, int i) {
        Map<String, int[]> counters = PlayerExtendedProperties.getCounterMap(player);
        int[] craft = counters.get(PlayerExtendedProperties.counters[1]);
        if (craft.length <= i) {
            int[] craftnew = new int[i + 1];
            System.arraycopy(craft, 0, craftnew, 0, craft.length);
            counters.put(PlayerExtendedProperties.counters[1], craftnew);
            craft = craftnew;
        }
        int n = i;
        craft[n] = craft[n] + 1;
        boolean flag = false;
        for (float f = (float)Math.pow(2.0, 3 - i); f <= (float)craft[i]; f += 0.5f) {
            player.addExperience(1);
            flag = true;
        }
        if (flag) {
            craft[i] = 0;
        }
        counters.put(PlayerExtendedProperties.counters[1], craft);
    }

    public static void incrementOreCounter(EntityPlayer player, int i) {
        if (oreMiningXP) {
            Map<String, int[]> counters = PlayerExtendedProperties.getCounterMap(player);
            int[] ore = counters.get(PlayerExtendedProperties.counters[0]);
            if (ore.length <= i) {
                int[] orenew = new int[i + 1];
                System.arraycopy(ore, 0, orenew, 0, ore.length);
                counters.put(PlayerExtendedProperties.counters[0], orenew);
                ore = orenew;
            }
            int n = i;
            ore[n] = ore[n] + 1;
            boolean flag = false;
            for (float f = (float)Math.pow(2.0, 3 - i) / 2.0f; f <= (float)ore[i]; f += 0.5f) {
                player.addExperience(1);
                flag = true;
            }
            if (flag) {
                ore[i] = 0;
            }
            counters.put(PlayerExtendedProperties.counters[0], ore);
        }
        LevelUp.giveBonusMiningXP(player);
    }

    public static boolean isTalismanRecipe(IInventory iinventory) {
        if (xpTalisman != null) {
            for (int i = 0; i < iinventory.getSizeInventory(); ++i) {
                if (iinventory.getStackInSlot(i) == null || iinventory.getStackInSlot(i).getItem() != xpTalisman) continue;
                return true;
            }
        }
        return false;
    }

    public static void takenFromCrafting(EntityPlayer player, ItemStack itemstack, IInventory iinventory) {
        if (LevelUp.isTalismanRecipe(iinventory)) {
            for (int i = 0; i < iinventory.getSizeInventory(); ++i) {
                ItemStack itemstack1 = iinventory.getStackInSlot(i);
                if (itemstack1 == null || !towItems.containsKey(itemstack1.getItem())) continue;
                player.addExperience((int)Math.floor((double)(itemstack1.stackSize * towItems.get(itemstack1.getItem())) / 4.0));
                iinventory.getStackInSlot((int)i).stackSize = 0;
            }
        } else {
            for (int j = 0; j < iinventory.getSizeInventory(); ++j) {
                ItemStack itemstack2 = iinventory.getStackInSlot(j);
                if (itemstack2 == null || LevelUp.isUncraftable(itemstack.getItem())) continue;
                LevelUp.giveCraftingXP(player, itemstack2);
                LevelUp.giveBonusCraftingXP(player);
            }
        }
    }

    public static boolean isUncraftable(Item item) {
        return item == Item.getItemFromBlock((Block)Blocks.hay_block) || item == Item.getItemFromBlock((Block)Blocks.gold_block) || item == Item.getItemFromBlock((Block)Blocks.iron_block) || item == Item.getItemFromBlock((Block)Blocks.diamond_block);
    }

    static {
        allowHUD = true;
        renderTopLeft = true;
        renderExpBar = true;
        changeFOV = true;
        bonusMiningXP = true;
        bonusCraftingXP = true;
        bonusFightingXP = true;
        oreMiningXP = true;
    }
}
