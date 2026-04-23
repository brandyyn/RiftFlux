package net.nmccoy.legendgear.legacy.enchantments;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.nmccoy.legendgear.legacy.LegendGear;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EnchantmentSoulTether extends Enchantment {
    private static final String PLAYER_PERSISTED_TAG = "PlayerPersisted";
    private static final String SOULBOUND_ITEMS_TAG = "soulboundItems";

    public EnchantmentSoulTether(int id, int rarity) {
        super(id, rarity, EnumEnchantmentType.weapon);
        this.setName("soulTether");
    }

    @Override
    public int getMinEnchantability(int level) {
        return 20;
    }

    @Override
    public int getMaxEnchantability(int level) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        return stack.isItemStackDamageable() || super.canApply(stack);
    }

    @SubscribeEvent
    public void onPlayerDeathDrops(LivingDropsEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer) || LegendGear.enchantmentSoulTether == null) {
            return;
        }

        List<EntityItem> tetheredDrops = new ArrayList<EntityItem>();
        Iterator<EntityItem> iterator = event.drops.iterator();
        while (iterator.hasNext()) {
            EntityItem entityItem = iterator.next();
            ItemStack stack = entityItem.getEntityItem();
            if (stack == null
                    || EnchantmentHelper.getEnchantmentLevel(LegendGear.enchantmentSoulTether.effectId, stack) <= 0) {
                continue;
            }

            iterator.remove();
            tetheredDrops.add(entityItem);
        }

        if (tetheredDrops.isEmpty()) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.entityLiving;
        NBTTagCompound persisted = player.getEntityData().getCompoundTag(PLAYER_PERSISTED_TAG);
        NBTTagList items = new NBTTagList();
        for (EntityItem entityItem : tetheredDrops) {
            ItemStack stack = entityItem.getEntityItem();
            if (stack == null) {
                continue;
            }
            items.appendTag(stack.writeToNBT(new NBTTagCompound()));
        }
        persisted.setTag(SOULBOUND_ITEMS_TAG, items);
        player.getEntityData().setTag(PLAYER_PERSISTED_TAG, persisted);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        EntityPlayer player = event.player;
        if (player == null || player.worldObj == null || player.worldObj.isRemote) {
            return;
        }

        NBTTagCompound playerData = player.getEntityData();
        if (!playerData.hasKey(PLAYER_PERSISTED_TAG)) {
            return;
        }

        NBTTagCompound persisted = playerData.getCompoundTag(PLAYER_PERSISTED_TAG);
        if (!persisted.hasKey(SOULBOUND_ITEMS_TAG)) {
            return;
        }

        NBTTagList items = persisted.getTagList(SOULBOUND_ITEMS_TAG, 10);
        for (int i = 0; i < items.tagCount(); i++) {
            ItemStack stack = ItemStack.loadItemStackFromNBT(items.getCompoundTagAt(i));
            if (stack != null) {
                player.inventory.addItemStackToInventory(stack);
            }
        }
        persisted.removeTag(SOULBOUND_ITEMS_TAG);
        playerData.setTag(PLAYER_PERSISTED_TAG, persisted);
    }
}
