package com.voidsrift.riftflux.inventorypets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class ItemBananaPet extends ItemInventoryPet {
    public ItemBananaPet() {
        super("petBanana", "Banana", "riftflux:inventorypets/banana_pet2");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (stack == null) {
            return null;
        }
        if (!world.isRemote) {
            ItemStack thrown = stack.copy();
            thrown.stackSize = 1;
            EntityBananaBoomerang entity = new EntityBananaBoomerang(world, player, thrown, player.inventory.currentItem);
            world.spawnEntityInWorld((Entity) entity);
            world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        }
        stack.stackSize = 0;
        return stack;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        list.add("Throwable");
    }
}
