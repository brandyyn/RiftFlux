package com.voidsrift.riftflux.avatar.glider;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.compat.BackhandCompat;
import com.voidsrift.riftflux.compat.EtFuturumElytraCompat;
import com.voidsrift.riftflux.terramine.ItemIceRod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemGlider extends Item {
    private static final Map<String, Long> LAST_TOGGLE_TICK = new ConcurrentHashMap<String, Long>();
    private final int color;

    public ItemGlider(int color) {
        this.color = color;
        this.setCreativeTab(CreativeTabs.tabTransport);
        this.setTextureName(ItemGlider.getOpenTexture(this.color));
        this.setMaxStackSize(ModConfig.gliderStackable ? 64 : 1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player == null) {
            return stack;
        }
        if (shouldIgnoreOffhandActivation(stack, player)) {
            return stack;
        }
        String playerName = player.getDisplayName();
        if (playerName == null) {
            return stack;
        }
        long tick = player.worldObj != null ? player.worldObj.getTotalWorldTime() : 0L;
        Long last = LAST_TOGGLE_TICK.get(playerName);
        if (last != null && last.longValue() == tick) {
            return stack;
        }
        LAST_TOGGLE_TICK.put(playerName, tick);
        boolean enablingGlider = !GliderState.isPlayerGliding(playerName);
        if (enablingGlider
                && ModConfig.blockEtFuturumElytraWhileAvatarGliding
                && EtFuturumElytraCompat.isElytraFlying(player)) {
            EtFuturumElytraCompat.clearElytraFlight(player);
        }
        if (enablingGlider) {
            GliderState.addGlidingPlayerName(playerName);
        } else {
            GliderState.removeGlidingPlayerName(playerName);
        }
        return stack;
    }

    public static String getOpenTexture(int color) {
        switch (color) {
            case 0:
                return "kieranvs_avatar:WhiteStaffIcon";
            case 1:
                return "kieranvs_avatar:OrangeStaffIcon";
            case 2:
                return "kieranvs_avatar:MagentaStaffIcon";
            case 3:
                return "kieranvs_avatar:LightBlueStaffIcon";
            case 4:
                return "kieranvs_avatar:YellowStaffIcon";
            case 5:
                return "kieranvs_avatar:LightGreenStaffIcon";
            case 6:
                return "kieranvs_avatar:PinkStaffIcon";
            case 7:
                return "kieranvs_avatar:GreyStaffIcon";
            case 8:
                return "kieranvs_avatar:LightGreyStaffIcon";
            case 9:
                return "kieranvs_avatar:CyanStaffIcon";
            case 10:
                return "kieranvs_avatar:PurpleStaffIcon";
            case 11:
                return "kieranvs_avatar:BlueStaffIcon";
            case 12:
                return "kieranvs_avatar:BrownStaffIcon";
            case 13:
                return "kieranvs_avatar:GreenStaffIcon";
            case 14:
                return "kieranvs_avatar:RedStaffIcon";
            case 15:
                return "kieranvs_avatar:BlackStaffIcon";
            default:
                return "kieranvs_avatar:OrangeStaffIcon";
        }
    }

    public int getColor() {
        return this.color;
    }

    public static void clearLastToggle(String playerName) {
        if (playerName != null) {
            LAST_TOGGLE_TICK.remove(playerName);
        }
    }

    private static boolean shouldIgnoreOffhandActivation(ItemStack stack, EntityPlayer player) {
        if (!BackhandCompat.isAvailable() || stack == null || player == null) {
            return false;
        }
        if (!BackhandCompat.isOffhandStack(player, stack)) {
            return false;
        }
        return shouldBlockOffhandByMainhand(player);
    }

    public static boolean canUseFromOffhand(ItemStack stack, EntityPlayer player) {
        if (!BackhandCompat.isAvailable() || player == null || stack == null) {
            return true;
        }
        if (!BackhandCompat.isOffhandStack(player, stack)) {
            return true;
        }
        return !shouldBlockOffhandByMainhand(player);
    }

    private static boolean shouldBlockOffhandByMainhand(EntityPlayer player) {
        if (BackhandCompat.isMainhandUsingItem(player)) {
            return true;
        }
        ItemStack mainhand = BackhandCompat.getMainhandItem(player);
        return isIceRodOrGlider(mainhand) || BackhandCompat.mainhandConsumesRightClick(mainhand);
    }

    private static boolean isIceRodOrGlider(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return false;
        }
        Item item = stack.getItem();
        return item instanceof ItemGlider || item instanceof ItemIceRod;
    }

}
