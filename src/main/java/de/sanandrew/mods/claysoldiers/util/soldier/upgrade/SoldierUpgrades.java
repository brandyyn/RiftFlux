/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  cpw.mods.fml.common.FMLLog
 *  org.apache.logging.log4j.Level
 */
package de.sanandrew.mods.claysoldiers.util.soldier.upgrade;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.FMLLog;
import de.sanandrew.core.manpack.util.javatuples.Pair;
import de.sanandrew.mods.claysoldiers.util.RegistryItems;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.ASoldierUpgrade;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.behavior.UpgradeFermSpiderEye;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.behavior.UpgradeNetherwart;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.behavior.UpgradeWheat;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.core.UpgradeBrick;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.core.UpgradeCactus;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.core.UpgradeIronIngot;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.core.UpgradeNetherBrick;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.core.UpgradeString;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.lefthand.UpgradeBowl;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.lefthand.UpgradeEmerald;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.lefthand.UpgradeFirecharge;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.lefthand.UpgradeGravel;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.lefthand.UpgradeNetherQuartz;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.lefthand.UpgradeShearBladeLeft;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.lefthand.UpgradeSnow;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeBlazePowder;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeBrownMushroom;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeClay;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeDiamond;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeDiamondBlock;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeEgg;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeEnderpearl;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeFeather;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeFireworkStar;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeFood;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeGhastTear;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeGlass;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeGlowstone;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeGoldNugget;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeGunpowder;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeHelperGlass;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeHelperShearBlade;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeHelperWool;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeLeather;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeLilyPads;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeMagmacream;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeMobHead;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradePaper;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeRedMushroom;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeRedstone;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeSlimeball;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeStoneButton;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeSugar;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeWheatSeeds;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeWoodButton;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.enchantment.UpgradeCoal;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.enchantment.UpgradeFlint;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.enchantment.UpgradeGoldIngot;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.enchantment.UpgradeIronBlock;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.enchantment.UpgradeSugarCane;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.enchantment.UpgradeWool;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.righthand.UpgradeArrow;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.righthand.UpgradeBlazeRod;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.righthand.UpgradeBone;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.righthand.UpgradeGoldMelon;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.righthand.UpgradeShearBladeRight;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.righthand.UpgradeStick;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Level;

public final class SoldierUpgrades {
    public static final String UPG_EGG = "egg";
    public static final String UPG_BONE = "bone";
    public static final String UPG_BOWL = "bowl";
    public static final String UPG_CLAY = "clay";
    public static final String UPG_COAL = "coal";
    public static final String UPG_FOOD = "food";
    public static final String UPG_SNOW = "snow";
    public static final String UPG_WOOL = "wool";
    public static final String UPG_ARROW = "arrow";
    public static final String UPG_BRICK = "brick";
    public static final String UPG_FLINT = "flint";
    public static final String UPG_GLASS = "glass";
    public static final String UPG_PAPER = "paper";
    public static final String UPG_STICK = "stick";
    public static final String UPG_SUGAR = "sugar";
    public static final String UPG_WHEAT = "wheat";
    public static final String UPG_CACTUS = "string";
    public static final String UPG_GRAVEL = "gravel";
    public static final String UPG_STRING = "string";
    public static final String UPG_EMERALD = "emerald";
    public static final String UPG_FEATHER = "feather";
    public static final String UPG_LEATHER = "leather";
    public static final String UPG_BLAZEROD = "blazerod";
    public static final String UPG_LILYPADS = "lilypads";
    public static final String UPG_MOB_HEAD = "skull";
    public static final String UPG_REDSTONE = "redstone";
    public static final String UPG_GLOWSTONE = "glowstone";
    public static final String UPG_GOLDMELON = "goldmelon";
    public static final String UPG_GUNPOWDER = "gunpowder";
    public static final String UPG_SHEARLEFT = "shear_l";
    public static final String UPG_SUGARCANE = "sugarcane";
    public static final String UPG_ENDERPEARL = "enderpearl";
    public static final String UPG_FIRECHARGE = "firecharge";
    public static final String UPG_GOLD_INGOT = "gold_ingot";
    public static final String UPG_IRON_BLOCK = "iron_block";
    public static final String UPG_IRON_INGOT = "iron_ingot";
    public static final String UPG_MAGMACREAM = "magmacream";
    public static final String UPG_NETHERWART = "netherwart";
    public static final String UPG_SHEARRIGHT = "shear_r";
    public static final String UPG_SLIMEBALLS = "slimeball";
    public static final String UPG_WOODBUTTON = "woodbutton";
    public static final String UPG_GOLD_NUGGET = "gold_nugget";
    public static final String UPG_STONEBUTTON = "stonebutton";
    public static final String UPG_WHEAT_SEEDS = "wheat_seeds";
    public static final String UPG_BLAZE_POWDER = "blaze_powder";
    public static final String UPG_MUSHROOM_RED = "red_mushroom";
    public static final String UPG_NETHER_BRICK = "nether_brick";
    public static final String UPG_FERMSPIDEREYE = "spidereye_ferm";
    public static final String UPG_FIREWORK_STAR = "firework";
    public static final String UPG_NETHER_QUARTZ = "nether_quartz";
    public static final String UPG_MUSHROOM_BROWN = "brown_mushroom";
    public static final String UPG_DIAMOND_ITEM = "diamond";
    public static final String UPG_DIAMOND_BLOCK = "diamond_block";
    public static final String UPG_GHAST_TEAR = "ghasttear";
    private static final Map<String, ASoldierUpgrade> NAME_TO_UPGRADE_MAP_ = Maps.newHashMap();
    private static final Map<ASoldierUpgrade, String> UPGRADE_TO_NAME_MAP_ = Maps.newHashMap();
    private static final Map<Pair<Item, Integer>, ASoldierUpgrade> ITEM_TO_UPGRADE_MAP_ = Maps.newHashMap();
    private static final Map<ASoldierUpgrade, Byte> UPGRADE_TO_RENDER_ID_MAP_ = Maps.newHashMap();
    private static final Map<Byte, ASoldierUpgrade> RENDER_ID_TO_UPGRADE_MAP_ = Maps.newHashMap();
    private static byte currRenderId = 0;

    public static void registerUpgrade(String name, ItemStack item, ASoldierUpgrade instance) {
        SoldierUpgrades.registerUpgrade(name, item, instance, -1);
    }

    public static void registerUpgrade(String name, ItemStack item, ASoldierUpgrade instance, int clientRenderId) {
        SoldierUpgrades.registerUpgrade(name, new ItemStack[]{item}, instance, clientRenderId);
    }

    public static void registerUpgrade(String name, ItemStack[] items, ASoldierUpgrade instance) {
        SoldierUpgrades.registerUpgrade(name, items, instance, -1);
    }

    public static void registerUpgrade(String name, ItemStack[] items, ASoldierUpgrade instance, int clientRenderId) {
        NAME_TO_UPGRADE_MAP_.put(name, instance);
        UPGRADE_TO_NAME_MAP_.put(instance, name);
        for (ItemStack upgradeItem : items) {
            if (upgradeItem == null) continue;
            ITEM_TO_UPGRADE_MAP_.put(Pair.with(upgradeItem.getItem(), upgradeItem.getItemDamage()), instance);
        }
        if (clientRenderId >= 0) {
            if (clientRenderId > 127) {
                FMLLog.log((String)"ClaySoldiers", (Level)Level.WARN, (String)"The Upgrade \"%s\" cannot be bound to the render ID! The render ID is greater than 127!", (Object[])new Object[]{name});
            } else if (RENDER_ID_TO_UPGRADE_MAP_.containsKey((byte)clientRenderId)) {
                FMLLog.log((String)"ClaySoldiers", (Level)Level.WARN, (String)"The Upgrade \"%s\" cannot be bound to the render ID! The render ID is already registered!", (Object[])new Object[]{name});
            } else {
                UPGRADE_TO_RENDER_ID_MAP_.put(instance, (byte)clientRenderId);
                RENDER_ID_TO_UPGRADE_MAP_.put((byte)clientRenderId, instance);
            }
        }
    }

    public static ASoldierUpgrade getUpgrade(String name) {
        return NAME_TO_UPGRADE_MAP_.get(name);
    }

    public static String getName(ASoldierUpgrade upgrade) {
        return UPGRADE_TO_NAME_MAP_.get(upgrade);
    }

    public static ASoldierUpgrade getUpgrade(ItemStack item) {
        if (item != null) {
            Pair<Item, Integer> itemData = Pair.with(item.getItem(), (int)Short.MAX_VALUE);
            if (ITEM_TO_UPGRADE_MAP_.containsKey(itemData)) {
                return ITEM_TO_UPGRADE_MAP_.get(itemData);
            }
            itemData = Pair.with(item.getItem(), item.getItemDamage());
            if (ITEM_TO_UPGRADE_MAP_.containsKey(itemData)) {
                return ITEM_TO_UPGRADE_MAP_.get(itemData);
            }
        }
        return null;
    }

    public static byte getRenderId(ASoldierUpgrade upgrade) {
        if (UPGRADE_TO_RENDER_ID_MAP_.containsKey(upgrade)) {
            return UPGRADE_TO_RENDER_ID_MAP_.get(upgrade);
        }
        return -1;
    }

    public static ASoldierUpgrade getUpgrade(int renderId) {
        return RENDER_ID_TO_UPGRADE_MAP_.get((byte)renderId);
    }

    public static Set<Byte> getRegisteredRenderIds() {
        return RENDER_ID_TO_UPGRADE_MAP_.keySet();
    }

    public static byte getNewRenderId() {
        if (currRenderId == 127) {
            throw new RenderIdException();
        }
        byte by = currRenderId;
        currRenderId = (byte)(by + 1);
        return by;
    }

    public static void initialize() {
        SoldierUpgrades.registerUpgrade(UPG_SHEARLEFT, (ItemStack)null, (ASoldierUpgrade)new UpgradeShearBladeLeft(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_SHEARRIGHT, (ItemStack)null, (ASoldierUpgrade)new UpgradeShearBladeRight(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_GLASS, (ItemStack)null, (ASoldierUpgrade)new UpgradeGlass(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_WOOL, (ItemStack)null, (ASoldierUpgrade)new UpgradeWool(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_STICK, new ItemStack(Items.stick), (ASoldierUpgrade)new UpgradeStick(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_BLAZEROD, new ItemStack(Items.blaze_rod), (ASoldierUpgrade)new UpgradeBlazeRod(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_LEATHER, new ItemStack(Items.leather), (ASoldierUpgrade)new UpgradeLeather(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_EGG, new ItemStack(Items.egg), (ASoldierUpgrade)new UpgradeEgg(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_WOODBUTTON, new ItemStack(Blocks.wooden_button, 1, Short.MAX_VALUE), (ASoldierUpgrade)new UpgradeWoodButton(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_STONEBUTTON, new ItemStack(Blocks.stone_button, 1, Short.MAX_VALUE), (ASoldierUpgrade)new UpgradeStoneButton(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_IRON_INGOT, new ItemStack(Items.iron_ingot), (ASoldierUpgrade)new UpgradeIronIngot(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_BRICK, new ItemStack(Items.brick), (ASoldierUpgrade)new UpgradeBrick(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_GRAVEL, new ItemStack(Blocks.gravel), (ASoldierUpgrade)new UpgradeGravel(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_FIRECHARGE, new ItemStack(Items.fire_charge), (ASoldierUpgrade)new UpgradeFirecharge(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_BOWL, new ItemStack(Items.bowl), (ASoldierUpgrade)new UpgradeBowl(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_GOLD_NUGGET, new ItemStack(Items.gold_nugget), (ASoldierUpgrade)new UpgradeGoldNugget(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_LILYPADS, new ItemStack(Blocks.waterlily), (ASoldierUpgrade)new UpgradeLilyPads(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_GOLDMELON, new ItemStack(Items.speckled_melon), (ASoldierUpgrade)new UpgradeGoldMelon(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_FLINT, new ItemStack(Items.flint), (ASoldierUpgrade)new UpgradeFlint(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_FEATHER, new ItemStack(Items.feather), (ASoldierUpgrade)new UpgradeFeather(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_IRON_BLOCK, new ItemStack(Blocks.iron_block), (ASoldierUpgrade)new UpgradeIronBlock(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_BONE, new ItemStack(Items.bone), (ASoldierUpgrade)new UpgradeBone(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_NETHER_QUARTZ, new ItemStack(Items.quartz), (ASoldierUpgrade)new UpgradeNetherQuartz(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_ENDERPEARL, new ItemStack(Items.ender_pearl), (ASoldierUpgrade)new UpgradeEnderpearl(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_MAGMACREAM, new ItemStack(Items.magma_cream), (ASoldierUpgrade)new UpgradeMagmacream(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_MOB_HEAD, new ItemStack(Items.skull, 1, Short.MAX_VALUE), (ASoldierUpgrade)new UpgradeMobHead(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_FIREWORK_STAR, new ItemStack(Items.firework_charge, 1, Short.MAX_VALUE), (ASoldierUpgrade)new UpgradeFireworkStar(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_GOLD_INGOT, new ItemStack(Items.gold_ingot), (ASoldierUpgrade)new UpgradeGoldIngot(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_DIAMOND_ITEM, new ItemStack(Items.diamond), (ASoldierUpgrade)new UpgradeDiamond(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_DIAMOND_BLOCK, new ItemStack(Blocks.diamond_block), (ASoldierUpgrade)new UpgradeDiamondBlock(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_COAL, new ItemStack(Items.coal, 1, Short.MAX_VALUE), (ASoldierUpgrade)new UpgradeCoal());
        SoldierUpgrades.registerUpgrade(UPG_WHEAT, new ItemStack(Items.wheat), (ASoldierUpgrade)new UpgradeWheat());
        SoldierUpgrades.registerUpgrade(UPG_NETHERWART, new ItemStack(Items.nether_wart), (ASoldierUpgrade)new UpgradeNetherwart());
        SoldierUpgrades.registerUpgrade(UPG_FERMSPIDEREYE, new ItemStack(Items.fermented_spider_eye), (ASoldierUpgrade)new UpgradeFermSpiderEye());
        SoldierUpgrades.registerUpgrade(UPG_SUGAR, new ItemStack(Items.sugar), (ASoldierUpgrade)new UpgradeSugar());
        SoldierUpgrades.registerUpgrade(UPG_SLIMEBALLS, new ItemStack(Items.slime_ball), (ASoldierUpgrade)new UpgradeSlimeball());
        SoldierUpgrades.registerUpgrade("string", new ItemStack(Items.string), (ASoldierUpgrade)new UpgradeString());
        SoldierUpgrades.registerUpgrade("string", new ItemStack(Blocks.cactus), (ASoldierUpgrade)new UpgradeCactus());
        SoldierUpgrades.registerUpgrade(UPG_CLAY, new ItemStack(Items.clay_ball), (ASoldierUpgrade)new UpgradeClay());
        SoldierUpgrades.registerUpgrade(UPG_SUGARCANE, new ItemStack(Items.reeds), (ASoldierUpgrade)new UpgradeSugarCane());
        SoldierUpgrades.registerUpgrade(UPG_ARROW, new ItemStack(Items.arrow), (ASoldierUpgrade)new UpgradeArrow());
        SoldierUpgrades.registerUpgrade(UPG_WHEAT_SEEDS, new ItemStack(Items.wheat_seeds), (ASoldierUpgrade)new UpgradeWheatSeeds());
        SoldierUpgrades.registerUpgrade(UPG_MUSHROOM_RED, new ItemStack(Blocks.red_mushroom), (ASoldierUpgrade)new UpgradeRedMushroom());
        SoldierUpgrades.registerUpgrade(UPG_MUSHROOM_BROWN, new ItemStack(Blocks.brown_mushroom), (ASoldierUpgrade)new UpgradeBrownMushroom());
        SoldierUpgrades.registerUpgrade(UPG_BLAZE_POWDER, new ItemStack(Items.blaze_powder), (ASoldierUpgrade)new UpgradeBlazePowder());
        SoldierUpgrades.registerUpgrade("wool_helper", new ItemStack(Blocks.wool, 1, Short.MAX_VALUE), (ASoldierUpgrade)new UpgradeHelperWool());
        SoldierUpgrades.registerUpgrade(UPG_GHAST_TEAR, new ItemStack(Items.ghast_tear), (ASoldierUpgrade)new UpgradeGhastTear());
        SoldierUpgrades.registerUpgrade(UPG_GLOWSTONE, new ItemStack[]{new ItemStack(Items.glowstone_dust), new ItemStack(Blocks.glowstone)}, (ASoldierUpgrade)new UpgradeGlowstone(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_GUNPOWDER, new ItemStack[]{new ItemStack(Items.gunpowder), new ItemStack(Blocks.tnt)}, (ASoldierUpgrade)new UpgradeGunpowder(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_SNOW, new ItemStack[]{new ItemStack(Blocks.snow), new ItemStack(Blocks.snow_layer), new ItemStack(Items.snowball)}, (ASoldierUpgrade)new UpgradeSnow(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_EMERALD, new ItemStack[]{new ItemStack(Blocks.emerald_block), new ItemStack(Items.emerald)}, (ASoldierUpgrade)new UpgradeEmerald(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade(UPG_PAPER, new ItemStack[]{new ItemStack(Items.paper), new ItemStack(Items.book)}, (ASoldierUpgrade)new UpgradePaper(), (int)SoldierUpgrades.getNewRenderId());
        SoldierUpgrades.registerUpgrade("shear_helper", new ItemStack[]{new ItemStack(RegistryItems.shearBlade), new ItemStack(Items.shears)}, (ASoldierUpgrade)new UpgradeHelperShearBlade());
        SoldierUpgrades.registerUpgrade(UPG_NETHER_BRICK, new ItemStack[]{new ItemStack(Blocks.nether_brick), new ItemStack(Items.netherbrick)}, (ASoldierUpgrade)new UpgradeNetherBrick());
        SoldierUpgrades.registerUpgrade("glass_helper", new ItemStack[]{new ItemStack(Blocks.glass), new ItemStack(Blocks.glass_pane), new ItemStack(Blocks.stained_glass, 1, Short.MAX_VALUE), new ItemStack(Blocks.stained_glass_pane, 1, Short.MAX_VALUE), new ItemStack(Items.glass_bottle)}, (ASoldierUpgrade)new UpgradeHelperGlass());
        SoldierUpgrades.registerUpgrade(UPG_FOOD, SoldierUpgrades.getFoodItems(), (ASoldierUpgrade)new UpgradeFood());
        SoldierUpgrades.registerUpgrade(UPG_REDSTONE, new ItemStack[]{new ItemStack(Items.redstone), new ItemStack(Blocks.redstone_block)}, (ASoldierUpgrade)new UpgradeRedstone());
    }

    public static void logUpgradeCount() {
        FMLLog.log((String)"ClaySoldiers", (Level)Level.DEBUG, (String)"There are %d soldier upgrades registered. %d of them use client renderers!", (Object[])new Object[]{NAME_TO_UPGRADE_MAP_.size(), currRenderId + 1});
    }

    private static ItemStack[] getFoodItems() {
        ArrayList<ItemStack> stackList = new ArrayList<ItemStack>();
        Iterator<Item> iter = Item.itemRegistry.iterator();
        while (iter.hasNext()) {
            Item elem = iter.next();
            if (elem instanceof ItemFood && !UpgradeFood.isFoodExcluded((ItemFood)elem)) {
                stackList.add(new ItemStack(elem, 1, (int)Short.MAX_VALUE));
            }
        }
        return stackList.toArray(new ItemStack[stackList.size()]);
    }

    public static class RenderIdException
    extends RuntimeException {
        public RenderIdException() {
            super("There are no more render IDs for soldier upgrade available!");
        }
    }
}
