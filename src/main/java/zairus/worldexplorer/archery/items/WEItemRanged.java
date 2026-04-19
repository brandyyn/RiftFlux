/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package zairus.worldexplorer.archery.items;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import zairus.worldexplorer.core.items.WEItem;

public class WEItemRanged
extends WEItem {
    protected List<Item> allowedAmmo = new ArrayList<Item>();

    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        return stack;
    }

    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.bow;
    }

    public int getItemEnchantability() {
        return 1;
    }

    @Override
    public boolean updatesFOV() {
        return true;
    }

    @Override
    public float getFOVValue() {
        return 0.4f;
    }

    @Override
    public float getFOVSpeedFactor() {
        return 350.0f;
    }

    protected void addAllowedAmmo(Item ... ammo) {
        for (int i = 0; i < ammo.length; ++i) {
            this.allowedAmmo.add(ammo[i]);
        }
    }

    public List<Item> getAllowedAmmo() {
        return this.allowedAmmo;
    }

    public static ItemStack getAmmo(ItemStack stack, EntityPlayer player) {
        ItemStack ammo = null;
        for (int i = 0; i < 36; ++i) {
            if (player.inventory.getStackInSlot(i) == null || !((WEItemRanged)stack.getItem()).getAllowedAmmo().contains(player.inventory.getStackInSlot(i).getItem())) continue;
            ammo = player.inventory.getStackInSlot(i);
            break;
        }
        return ammo;
    }
}

