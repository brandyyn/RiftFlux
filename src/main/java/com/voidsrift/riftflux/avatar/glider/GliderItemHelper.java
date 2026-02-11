package com.voidsrift.riftflux.avatar.glider;

import com.voidsrift.riftflux.compat.BackhandCompat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public final class GliderItemHelper {
    private GliderItemHelper() {
    }

    public static boolean isGlider(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemGlider;
    }

    public static ItemStack getOffhandGlider(EntityPlayer player) {
        if (player == null || !BackhandCompat.isAvailable()) {
            return null;
        }
        ItemStack offhand = BackhandCompat.getOffhandItem(player);
        return isGlider(offhand) ? offhand : null;
    }

    public static ItemStack getGliderStack(EntityPlayer player) {
        if (player == null) {
            return null;
        }
        ItemStack main = player.getHeldItem();
        if (isGlider(main)) {
            return main;
        }
        return getOffhandGlider(player);
    }

    public static boolean isHoldingGlider(EntityPlayer player) {
        return getGliderStack(player) != null;
    }
}
