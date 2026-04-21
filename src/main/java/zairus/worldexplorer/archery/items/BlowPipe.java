/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.Event
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.player.ArrowLooseEvent
 */
package zairus.worldexplorer.archery.items;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import zairus.worldexplorer.archery.entity.EntityDart;
import zairus.worldexplorer.archery.items.WEArcheryItems;
import zairus.worldexplorer.archery.items.WEItemRanged;

public class BlowPipe
extends WEItemRanged {
    public BlowPipe() {
        this.setUnlocalizedName("blowpipe");
        this.setTextureName("worldexplorer:blowpipe");
        this.setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabCombat);
        this.setFull3D();
        this.setMaxDamage(Math.max(0, ModConfig.riftExplorerBlowpipeDurability));
        this.maxStackSize = 1;
        this.addAllowedAmmo(WEArcheryItems.dart);
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.capabilities.isCreativeMode || this.findPreferredAmmo(player) != null) {
            world.playSoundAtEntity((Entity)player, "worldexplorer:blowpipe_breathe1", 1.0f, 1.0f / (itemRand.nextFloat() * 0.4f + 1.2f) + 0.5f);
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        }
        return stack;
    }

    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int useCount) {
        int j = this.getMaxItemUseDuration(stack) - useCount;
        ArrowLooseEvent event = new ArrowLooseEvent(player, stack, j);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (event.isCanceled()) {
            return;
        }
        j = event.charge;
        boolean flag = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel((int)Enchantment.infinity.effectId, (ItemStack)stack) > 0;
        ItemStack ammo = this.findPreferredAmmo(player);
        if (player.capabilities.isCreativeMode && ammo == null) {
            ammo = new ItemStack((Item)WEArcheryItems.dart, 1);
        }
        if (!flag && ammo == null) {
            return;
        }
        if (ammo == null) {
            return;
        }
        float f = (float)j / 7.0f;
        if ((double)(f = (f * f + f * 2.0f) / 3.0f) < 0.1) {
            return;
        }
        if (f > 1.0f) {
            f = 1.0f;
        }
        EntityDart dart = new EntityDart(world, (EntityLivingBase)player, f * 1.7f, ammo);
        stack.damageItem(1, (EntityLivingBase)player);
        if (!world.isRemote) {
            world.playSoundAtEntity((Entity)player, "worldexplorer:blowpipe_blow" + (itemRand.nextInt(3) + 1), 1.0f, 1.0f / (itemRand.nextFloat() * 0.4f + 1.2f) + f * 0.5f);
        }
        if (flag) {
            dart.canBePickedUp = 2;
        }
        if (!flag) {
            consumeSpecificAmmo(player.inventory, ammo);
        }
        if (!world.isRemote) {
            world.spawnEntityInWorld((Entity)dart);
        }
    }

    private ItemStack findPreferredAmmo(EntityPlayer player) {
        if (player == null || player.inventory == null) {
            return null;
        }
        ItemStack plainDart = null;
        for (int i = 0; i < 36; i++) {
            ItemStack slot = player.inventory.getStackInSlot(i);
            if (slot == null || slot.getItem() != WEArcheryItems.dart) {
                continue;
            }
            if (DartEffectHelper.hasInfusion(slot)) {
                return slot;
            }
            if (plainDart == null) {
                plainDart = slot;
            }
        }
        return plainDart;
    }

    private static void consumeSpecificAmmo(InventoryPlayer inventory, ItemStack ammo) {
        if (inventory == null || ammo == null || ammo.getItem() == null) {
            return;
        }
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack slot = inventory.getStackInSlot(i);
            if (slot == null || slot.getItem() != ammo.getItem()) {
                continue;
            }
            if (slot.getHasSubtypes() && slot.getItemDamage() != ammo.getItemDamage()) {
                continue;
            }
            if (!ItemStack.areItemStackTagsEqual(slot, ammo)) {
                continue;
            }
            inventory.decrStackSize(i, 1);
            return;
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack duration) {
        return 3000;
    }
}
