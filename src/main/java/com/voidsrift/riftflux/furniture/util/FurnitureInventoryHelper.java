package com.voidsrift.riftflux.furniture.util;

import java.util.Random;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public final class FurnitureInventoryHelper {
    private FurnitureInventoryHelper() {
    }

    public static void dropInventory(World world, int x, int y, int z, IInventory inventory, Random random) {
        if (world == null || world.isRemote || inventory == null || random == null) {
            return;
        }
        for (int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack == null) {
                continue;
            }

            float offsetX = random.nextFloat() * 0.8F + 0.1F;
            float offsetY = random.nextFloat() * 0.8F + 0.1F;
            float offsetZ = random.nextFloat() * 0.8F + 0.1F;

            while (stack.stackSize > 0) {
                int amount = random.nextInt(21) + 10;
                if (amount > stack.stackSize) {
                    amount = stack.stackSize;
                }
                stack.stackSize -= amount;

                EntityItem item = new EntityItem(
                        world,
                        x + offsetX,
                        y + offsetY,
                        z + offsetZ,
                        new ItemStack(stack.getItem(), amount, stack.getItemDamage())
                );
                float motionScale = 0.05F;
                item.motionX = random.nextGaussian() * motionScale;
                item.motionY = random.nextGaussian() * motionScale + 0.2F;
                item.motionZ = random.nextGaussian() * motionScale;
                if (stack.hasTagCompound()) {
                    item.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
                }
                world.spawnEntityInWorld(item);
            }
            inventory.setInventorySlotContents(slot, null);
        }
    }
}
