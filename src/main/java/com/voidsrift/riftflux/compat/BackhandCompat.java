package com.voidsrift.riftflux.compat;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import java.lang.reflect.Method;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBoat;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemCarrotOnAStick;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemFireball;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSign;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.World;
import xonin.backhand.api.core.IBackhandPlayer;
import xonin.backhand.api.core.BackhandUtils;

public final class BackhandCompat {
    private static Boolean classPresentCache;
    private static final ThreadLocal<Boolean> EXECUTING_OFFHAND_ACTION = new ThreadLocal<Boolean>();

    private BackhandCompat() {
    }

    public static boolean isAvailable() {
        if (Loader.isModLoaded("backhand")) {
            return true;
        }
        if (classPresentCache != null) {
            return classPresentCache.booleanValue();
        }
        boolean present;
        try {
            Class.forName("xonin.backhand.api.core.BackhandUtils", false, BackhandCompat.class.getClassLoader());
            present = true;
        } catch (Throwable ignored) {
            present = false;
        }
        classPresentCache = Boolean.valueOf(present);
        return present;
    }

    public static ItemStack getOffhandItem(EntityPlayer player) {
        if (!isAvailable() || player == null) {
            return null;
        }
        ItemStack offhand = getOffhandItemInternal(player);
        if (offhand != null) {
            return offhand;
        }
        int offhandSlot = getOffhandSlot(player);
        if (player.inventory == null
                || player.inventory.mainInventory == null
                || offhandSlot < 0
                || offhandSlot >= player.inventory.mainInventory.length) {
            return null;
        }
        return player.inventory.mainInventory[offhandSlot];
    }

    public static boolean isUsingOffhand(EntityPlayer player) {
        if (!isAvailable() || player == null) {
            return false;
        }
        return isUsingOffhandInternal(player);
    }

    public static ItemStack getMainhandItem(EntityPlayer player) {
        if (player == null) {
            return null;
        }
        if (!isAvailable()) {
            return player.getHeldItem();
        }
        return getMainhandItemInternal(player);
    }

    public static int getOffhandSlot(EntityPlayer player) {
        if (!isAvailable() || player == null) {
            return -1;
        }
        return getOffhandSlotInternal(player);
    }

    public static boolean isMainhandUsingItem(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        if (!player.isUsingItem()) {
            return false;
        }
        if (isAvailable() && isUsingOffhand(player)) {
            return false;
        }
        if (player instanceof IBackhandPlayer) {
            if (((IBackhandPlayer) player).isOffhandItemInUse()) {
                return false;
            }
        }
        ItemStack inUse = player.getItemInUse();
        if (inUse == null) {
            return false;
        }
        ItemStack mainhand = getMainhandItem(player);
        if (mainhand == null) {
            return true;
        }
        return inUse == mainhand
                || (inUse.getItem() == mainhand.getItem() && inUse.getItemDamage() == mainhand.getItemDamage());
    }

    public static boolean isOffhandActive(EntityPlayer player) {
        if (!isAvailable() || player == null) {
            return false;
        }
        return isUsingOffhand(player);
    }

    public static boolean isOffhandItemInUse(EntityPlayer player) {
        if (!isAvailable() || player == null) {
            return false;
        }
        return isOffhandItemInUseInternal(player);
    }

    public static boolean isExecutingOffhandAction() {
        return Boolean.TRUE.equals(EXECUTING_OFFHAND_ACTION.get());
    }

    public static void beginOffhandAction() {
        EXECUTING_OFFHAND_ACTION.set(Boolean.TRUE);
    }

    public static void endOffhandAction() {
        EXECUTING_OFFHAND_ACTION.remove();
    }

    @SafeVarargs
    public static void addOffhandPriorityItems(Class<? extends Item>... itemClasses) {
        if (!isAvailable() || itemClasses == null || itemClasses.length == 0) {
            return;
        }
        addOffhandPriorityItemsInternal(itemClasses);
    }

    public static void setOffhandItemInUse(EntityPlayer player, boolean inUse) {
        if (!isAvailable() || player == null) {
            return;
        }
        setOffhandItemInUseInternal(player, inUse);
    }

    public static boolean isOffhandStack(EntityPlayer player, ItemStack stack) {
        if (!isAvailable() || player == null || stack == null) {
            return false;
        }
        ItemStack offhand = getOffhandItem(player);
        if (offhand == null) {
            return false;
        }
        if (offhand == stack) {
            return true;
        }
        if (!ItemStack.areItemStacksEqual(stack, offhand)) {
            return false;
        }
        if (isUsingOffhand(player) || isOffhandItemInUse(player)) {
            return true;
        }
        ItemStack mainhand = getMainhandItem(player);
        if (mainhand == null) {
            return true;
        }
        if (!ItemStack.areItemStacksEqual(stack, mainhand)) {
            return true;
        }
        return false;
    }

    public static boolean mainhandConsumesRightClick(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        if (item == null) {
            return false;
        }
        if (item instanceof ItemSword) {
            return false;
        }

        EnumAction action = item.getItemUseAction(stack);
        if (action != EnumAction.none && action != EnumAction.block) {
            return true;
        }
        if (item.getMaxItemUseDuration(stack) > 0 && action != EnumAction.block) {
            return true;
        }

        return item instanceof ItemBlock
                || item instanceof ItemBow
                || item instanceof ItemFood
                || item instanceof ItemPotion
                || item instanceof ItemBucket
                || item instanceof ItemMonsterPlacer
                || item instanceof ItemFlintAndSteel
                || item instanceof ItemFireball
                || item instanceof ItemBoat
                || item instanceof ItemMinecart
                || item instanceof ItemEnderPearl
                || item instanceof ItemSnowball
                || item instanceof ItemEgg
                || item instanceof ItemExpBottle
                || item instanceof ItemFishingRod
                || item instanceof ItemLead
                || item instanceof ItemCarrotOnAStick
                || item instanceof ItemSign
                || overridesRightClick(item);
    }

    private static boolean overridesRightClick(Item item) {
        return overridesRightClickMethod(item, "onItemRightClick")
                || overridesRightClickMethod(item, "func_77659_a");
    }

    private static boolean overridesRightClickMethod(Item item, String methodName) {
        try {
            Method method = item.getClass().getMethod(methodName, ItemStack.class, World.class, EntityPlayer.class);
            return method.getDeclaringClass() != Item.class;
        } catch (Throwable ignored) {
            return false;
        }
    }

    @Optional.Method(modid = "backhand")
    private static ItemStack getOffhandItemInternal(EntityPlayer player) {
        return BackhandUtils.getOffhandItem(player);
    }

    @Optional.Method(modid = "backhand")
    private static int getOffhandSlotInternal(EntityPlayer player) {
        return BackhandUtils.getOffhandSlot(player);
    }

    @Optional.Method(modid = "backhand")
    private static boolean isUsingOffhandInternal(EntityPlayer player) {
        return BackhandUtils.isUsingOffhand(player);
    }

    @Optional.Method(modid = "backhand")
    private static ItemStack getMainhandItemInternal(EntityPlayer player) {
        if (player instanceof IBackhandPlayer) {
            return ((IBackhandPlayer) player).getMainhandItem();
        }
        return player.getHeldItem();
    }

    @Optional.Method(modid = "backhand")
    private static boolean isOffhandItemInUseInternal(EntityPlayer player) {
        if (player instanceof IBackhandPlayer) {
            return ((IBackhandPlayer) player).isOffhandItemInUse();
        }
        return false;
    }

    @SafeVarargs
    @Optional.Method(modid = "backhand")
    private static void addOffhandPriorityItemsInternal(Class<? extends Item>... itemClasses) {
        BackhandUtils.addOffhandPriorityItem(itemClasses);
    }

    @Optional.Method(modid = "backhand")
    private static void setOffhandItemInUseInternal(EntityPlayer player, boolean inUse) {
        if (player instanceof IBackhandPlayer) {
            ((IBackhandPlayer) player).setOffhandItemInUse(inUse);
        }
    }
}
