/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.ChatComponentText
 *  net.minecraft.util.EnumChatFormatting
 *  net.minecraft.util.IChatComponent
 */
package net.nmccoy.legendgear;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.item.MagicRing;
import net.nmccoy.legendgear.ritual.Boon;
import net.nmccoy.legendgear.ritual.BoonTable;
import net.nmccoy.legendgear.ritual.EnchantBoon;

public class StarSpirit {
    public static Map<Object, Integer> phoenixOpinions;
    public static final int NONE = -1;
    public static final int PHOENIX = 0;
    public static final int ENDCHILD = 1;
    public static final int NETHERLORD = 2;
    public static final int APPRECIATE = 10;
    public static final int LIKE = 30;
    public static final int LOVE = 100;
    public static final int FAVORITE = 300;
    public static final int DONATION_COOLDOWN = 300;
    public static final int MIN_VALUE_ACCEPTED = 0;
    public static final float STALENESS_SCALING = 0.0f;
    public static final float FAVOR_ROLL_FLOOR_RATIO = 0.25f;
    public static final float REQUEST_FLOOR_RATIO = 1.0f;
    public static final float INTERVENTION_CAP = 0.25f;
    public static final float FAVOR_INTERVENTION_REFRESH_RATIO = 3.0f;
    public static final int INTERVENTION_COOLDOWN = 900;
    public static final int COST_PHOENIX_FEATHER = 300;
    public static final int COST_SOUNDTRACK = 0;
    public static final int COST_FIRE_ASPECT = 150;
    public static final int COST_FLAME = 150;
    public static final int COST_FIRE_PROTECTION = 150;
    public static final int COST_PHOENIX_RING = 500;
    public static final int COST_FIRE_POTION = 300;
    public static final int COST_SUNFIRE_DIAMOND = 3000;
    public static final int COST_REVIVE_INTERVENTION = 300;
    public static final int COST_HEAL_INTERVENTION = 100;
    public static final int COST_FEED_INTERVENTION = 100;
    public static final int COST_SMITE_INTERVENTION = 200;
    public static final int COST_FIRE_INTERVENTION = 200;
    public static final int OBSERVE_SLAY_UNDEAD = 10;
    public static final int OBSERVE_SLAY_MONSTER_NAKED = 10;
    public static BoonTable phoenixPrizes;
    public int type;

    public static void populateOpinions() {
        phoenixOpinions = new HashMap<Object, Integer>();
        phoenixOpinions.put(Items.bone, 3);
        phoenixOpinions.put(Items.rotten_flesh, 3);
        phoenixOpinions.put(Items.gold_nugget, 3);
        phoenixOpinions.put(Items.gold_ingot, 30);
        phoenixOpinions.put(Blocks.gold_block, 300);
        phoenixOpinions.put(Items.ghast_tear, 100);
        phoenixOpinions.put(new ItemStack((Block)Blocks.tallgrass, 0, 1), 3);
        phoenixOpinions.put(new ItemStack((Item)LegendGear2.starDust, 1, 0), 30);
        phoenixOpinions.put(new ItemStack((Item)LegendGear2.starDust, 1, 1), 100);
        phoenixOpinions.put(new ItemStack((Item)LegendGear2.starDust, 1, 2), 1000);
        phoenixOpinions.put(new ItemStack((Item)LegendGear2.starDust, 1, 3), 45);
        phoenixOpinions.put(new ItemStack((Item)LegendGear2.starDust, 1, 4), 150);
        phoenixOpinions.put(new ItemStack((Item)LegendGear2.starDust, 1, 5), 1500);
        phoenixOpinions.put((Object)LegendGear2.starstoneBlock, 1000);
        phoenixOpinions.put((Object)LegendGear2.infusedStarstoneBlock, 1500);
        phoenixPrizes.add((Object)LegendGear2.phoenixFeather, 300, Items.feather);
        phoenixPrizes.add(new ItemStack((Item)LegendGear2.phoenixFeather, 3), 900, null);
        phoenixPrizes.add(new ItemStack((Item)LegendGear2.phoenixFeather, 5), 1500, null);
        phoenixPrizes.add(LegendGear2.sunfireDiamond, 3000, Items.diamond);
        phoenixPrizes.addHidden(new EnchantBoon(Enchantment.fireAspect, 1), 150, ItemSword.class);
        phoenixPrizes.addHidden(new EnchantBoon(Enchantment.flame, 1), 150, Items.bow);
        phoenixPrizes.addHidden(new EnchantBoon(Enchantment.fireProtection, 1), 150, ItemArmor.class);
        phoenixPrizes.addHidden(new ItemStack((Item)LegendGear2.magicRing, 1, MagicRing.RingType.PHOENIX_RING.ordinal()), 500, new ItemStack((Item)LegendGear2.magicRing, 1, MagicRing.RingType.PLAIN_RING.ordinal()));
        phoenixPrizes.addHidden(new ItemStack((Item)Items.potionitem, 1, 8227), 300, new ItemStack((Item)Items.potionitem, 1, 0));
        phoenixPrizes.addHidden(new ItemStack((Item)LegendGear2.spiritEmblem, 1, 1), 0, new ItemStack((Item)LegendGear2.spiritEmblem, 1, 0));
    }

    public static void observeActivity(int spirit, EntityPlayer player, int favor) {
        StarSpirit.adjustFavor(spirit, player, favor);
    }

    public static List<ItemStack> handleItemRequest(int spirit, EntityPlayer player, Object key, int count) {
        ItemStack prize;
        int total;
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        BoonTable boons = null;
        if (spirit == 0) {
            boons = phoenixPrizes;
        }
        if (boons == null) {
            return null;
        }
        Boon boon = boons.matchRequest(key);
        if (boon == null) {
            return null;
        }
        ItemStack rewardStack = null;
        if (boon.reward instanceof ItemStack) {
            rewardStack = (ItemStack)boon.reward;
        }
        if (boon.reward instanceof Item) {
            rewardStack = new ItemStack((Item)boon.reward);
        }
        if (boon.reward instanceof EnchantBoon) {
            EnchantBoon ench = (EnchantBoon)boon.reward;
            if (!(key instanceof ItemStack)) {
                return null;
            }
            ItemStack stack = (ItemStack)key;
            if (!ench.enchantment.canApply(stack)) {
                return null;
            }
            for (Object o : EnchantmentHelper.getEnchantments((ItemStack)stack).keySet()) {
                int id = (Integer)o;
                if (ench.enchantment.canApplyTogether(Enchantment.enchantmentsList[id])) continue;
                return null;
            }
            rewardStack = stack.copy();
            rewardStack.addEnchantment(ench.enchantment, ench.level);
        }
        if (rewardStack == null) {
            return null;
        }
        int cost = count * boon.cost;
        if (!StarSpirit.rollFavor(spirit, player, cost, 1.0f, 1.0f)) {
            return null;
        }
        int limit = rewardStack.getItem().getItemStackLimit(rewardStack);
        for (total = count * rewardStack.stackSize; total > limit; total -= limit) {
            prize = rewardStack.copy();
            prize.stackSize = limit;
            items.add(prize);
        }
        if (total > 0) {
            prize = rewardStack.copy();
            prize.stackSize = total;
            items.add(prize);
        }
        if (items.size() > 0) {
            StarSpirit.adjustFavor(spirit, player, -cost);
            return items;
        }
        return null;
    }

    public static ItemStack gratitude(int spirit, EntityPlayer player, int offered) {
        int budget = offered;
        int bonus = Math.min(budget, StarSpirit.getSpiritFavor(spirit, player) / 10);
        budget += bonus;
        BoonTable prizeTable = null;
        if (spirit == 0) {
            prizeTable = phoenixPrizes;
        }
        if (prizeTable == null) {
            return null;
        }
        int spending = 0;
        ItemStack prize = null;
        for (Boon boon : prizeTable.boons) {
            int price;
            int roll;
            if (!boon.allowGratitude || (roll = player.worldObj.rand.nextInt(budget + 1)) <= (price = boon.cost) || price <= spending) continue;
            spending = price;
            prize = boon.getRewardItem(1);
        }
        if (prize != null) {
            return prize;
        }
        return null;
    }

    public static int getOpinion(int spirit, Object offering) {
        Map<Object, Integer> opinions = new HashMap<Object, Integer>();
        if (spirit == 0) {
            opinions = phoenixOpinions;
        }
        Item offerItem = null;
        Block offerBlock = null;
        ItemStack offerStack = null;
        int offerCount = 1;
        int value = -1;
        if (offering instanceof ItemStack) {
            ItemStack stack = (ItemStack)offering;
            offerItem = stack.getItem();
            offerStack = stack;
            if (offerItem instanceof ItemBlock) {
                offerBlock = Block.getBlockFromItem((Item)offerItem);
            }
            offerCount = stack.stackSize;
        }
        if (offering instanceof Block) {
            offerBlock = (Block)offering;
        }
        double quantityScale = offerCount;
        if (offerBlock != null && opinions.containsKey(offerBlock)) {
            value = (Integer)opinions.get(offerBlock);
        } else if (offerItem != null && opinions.containsKey(offerItem)) {
            value = (Integer)opinions.get(offerItem);
        } else if (offerStack != null) {
            for (Object key : opinions.keySet()) {
                ItemStack keyStack;
                if (!(key instanceof ItemStack) || (keyStack = (ItemStack)key).getItem() != offerStack.getItem() || keyStack.getItemDamage() != offerStack.getItemDamage()) continue;
                value = (Integer)opinions.get(key);
                break;
            }
        }
        if (value == -1) {
            return -1;
        }
        return (int)((double)value * quantityScale);
    }

    public static void randomTell(int spirit, EntityPlayer player, String ... args) {
        if (args.length == 0) {
            return;
        }
        if (player.worldObj.isRemote) {
            return;
        }
        int which = player.worldObj.rand.nextInt(args.length);
        String message = "";
        if (spirit == 0) {
            message = message + EnumChatFormatting.GOLD + EnumChatFormatting.ITALIC;
        }
        if (spirit == 1) {
            message = message + EnumChatFormatting.LIGHT_PURPLE + EnumChatFormatting.ITALIC;
        }
        if (spirit == 2) {
            message = message + EnumChatFormatting.RED + EnumChatFormatting.ITALIC;
        }
        message = message + args[which];
        player.addChatMessage((IChatComponent)new ChatComponentText(message));
    }

    public static boolean attemptIntervention(int spirit, EntityPlayer player, int cost) {
        return StarSpirit.attemptIntervention(spirit, player, cost, 1.0f);
    }

    public static boolean attemptIntervention(int spirit, EntityPlayer player, int cost, float scale) {
        int favor;
        int spendableFavor;
        NBTTagCompound favorTag = StarSpirit.getPlayerFavorTag(player, spirit);
        int interventionTime = favorTag.getInteger("interventionTime");
        int nowTime = (int)(player.worldObj.getTotalWorldTime() / 20L);
        int interventionAgo = nowTime - interventionTime;
        float interventionRatio = interventionAgo >= 900 ? 1.0f : 1.0f * (float)interventionAgo / 900.0f;
        if ((interventionRatio = interventionRatio * 1.03f - 0.03f) < 0.0f) {
            interventionRatio = 0.0f;
        }
        if (player.worldObj.rand.nextInt((spendableFavor = (int)((float)(favor = StarSpirit.getSpiritFavor(spirit, player)) * interventionRatio * scale)) + 1) >= cost && (float)cost <= (float)favor * 0.25f) {
            StarSpirit.adjustFavor(spirit, player, -cost);
            favorTag.setInteger("interventionTime", nowTime);
            if (spirit == 0) {
                player.addChatMessage((IChatComponent)new ChatComponentText("" + EnumChatFormatting.GOLD + EnumChatFormatting.BOLD + "The Phoenix lends her aid!"));
            }
            return true;
        }
        return false;
    }

    public static int getRecentDonationCharge(int spirit, EntityPlayer player) {
        NBTTagCompound favorTag = StarSpirit.getPlayerFavorTag(player, spirit);
        int donationTime = favorTag.getInteger("lastDonationWorldSeconds");
        int nowTime = (int)(player.worldObj.getTotalWorldTime() / 20L);
        int donationCharge = favorTag.getInteger("recentDonationCharge");
        int donationAgo = nowTime - donationTime;
        donationCharge = donationAgo >= 300 ? 0 : (int)((double)donationCharge * (1.0 - 1.0 * (double)donationAgo / 300.0));
        return donationCharge;
    }

    public static NBTTagCompound getPlayerFavorTag(EntityPlayer player, int spirit) {
        NBTTagCompound persist;
        String PNT = "PlayerPersisted";
        NBTTagCompound data = player.getEntityData();
        if (!data.hasKey(PNT)) {
            data.setTag(PNT, (NBTBase)new NBTTagCompound());
        }
        if (!(persist = player.getEntityData().getCompoundTag(PNT)).hasKey("SpiritFavor" + spirit)) {
            persist.setTag("SpiritFavor" + spirit, (NBTBase)new NBTTagCompound());
        }
        NBTTagCompound favorTag = persist.getCompoundTag("SpiritFavor" + spirit);
        return favorTag;
    }

    public static void adjustFavor(int spirit, EntityPlayer player, int amount) {
        NBTTagCompound favorTag = StarSpirit.getPlayerFavorTag(player, spirit);
        int currentFavor = favorTag.getInteger("favor");
        favorTag.setInteger("favor", currentFavor += amount);
        int interventionTime = favorTag.getInteger("interventionTime");
        if (amount > 0) {
            favorTag.setInteger("interventionTime", interventionTime -= (int)((float)amount * 3.0f));
        }
    }

    public static int getSpiritFavor(int spirit, EntityPlayer player) {
        NBTTagCompound favorTag = StarSpirit.getPlayerFavorTag(player, spirit);
        return favorTag.getInteger("favor");
    }

    public static boolean rollFavor(int spirit, EntityPlayer player, int targetNumber, float cap) {
        return StarSpirit.rollFavor(spirit, player, targetNumber, cap, 0.25f);
    }

    public static boolean rollFavor(int spirit, EntityPlayer player, int targetNumber, float cap, float floorRatio) {
        int totalFavor = StarSpirit.getSpiritFavor(spirit, player);
        int floor = (int)((float)totalFavor * floorRatio);
        int budget = (int)((float)totalFavor * cap);
        if (budget < targetNumber) {
            return false;
        }
        int roll = player.worldObj.rand.nextInt(totalFavor - floor + 1) + floor;
        return roll >= targetNumber;
    }

    public static boolean requestBoon(int spirit, EntityPlayer player, int price, float cap, float floor) {
        boolean success;
        if (StarSpirit.rollFavor(spirit, player, price, cap, floor)) {
            StarSpirit.adjustFavor(spirit, player, -price);
            success = true;
        } else {
            success = false;
        }
        int favor = StarSpirit.getSpiritFavor(spirit, player);
        int budget = (int)((float)favor * cap);
        System.out.println("requested boon of value " + price + " with favor " + favor + " (budget " + budget + "), success = " + success);
        return success;
    }

    public static boolean isCarryingEmblem(int spirit, EntityPlayer player) {
        InventoryPlayer inv = player.inventory;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack == null || stack.getItem() != LegendGear2.spiritEmblem) continue;
            int damage = stack.getItemDamage();
            if (spirit != 0 || damage != 1 && damage != 2) continue;
            return true;
        }
        return false;
    }

    public static int handleOffering(int spirit, EntityPlayer player, Object offering, float multiplier) {
        NBTTagCompound favorTag = StarSpirit.getPlayerFavorTag(player, spirit);
        int donationCharge = StarSpirit.getRecentDonationCharge(spirit, player);
        int offeringValue = StarSpirit.netOfferingValue(spirit, player, offering);
        if ((offeringValue = (int)((float)offeringValue * multiplier)) == -1) {
            return -1;
        }
        int currentFavor = favorTag.getInteger("favor");
        favorTag.setInteger("recentDonationCharge", donationCharge += offeringValue);
        favorTag.setInteger("lastDonationWorldSeconds", (int)(player.worldObj.getTotalWorldTime() / 20L));
        favorTag.setInteger("favor", currentFavor += offeringValue);
        System.out.println("Donation of " + offeringValue + ", total favor " + currentFavor);
        return offeringValue;
    }

    public static int netOfferingValue(int spirit, EntityPlayer player, Object offering) {
        int donationCharge = StarSpirit.getRecentDonationCharge(spirit, player);
        int value = StarSpirit.getOpinion(spirit, offering);
        if (value == -1) {
            return -1;
        }
        value = donationCharge >= value ? (int)((float)value * 0.0f) : (int)((float)(value - donationCharge) + (float)donationCharge * 0.0f);
        return value;
    }

    static {
        phoenixPrizes = new BoonTable();
    }
}
