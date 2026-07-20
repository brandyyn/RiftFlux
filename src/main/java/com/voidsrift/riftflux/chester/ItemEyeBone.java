package com.voidsrift.riftflux.chester;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class ItemEyeBone extends Item {
    public ItemEyeBone() {
        setMaxStackSize(1);
        setHasSubtypes(true);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (stack.getItemDamage() != 0) {
            if (world.isRemote) {
                player.addChatComponentMessage(new ChatComponentText(
                        "This bone already has summoned Chester"
                ));
            }
            return stack;
        }

        if (!world.isRemote) {
            EntityChester chester = new EntityChester(world);
            chester.setLocationAndAngles(
                    player.posX,
                    player.posY + 1.0D,
                    player.posZ,
                    player.rotationYaw,
                    0.0F
            );
            chester.setTamed(true);
            chester.func_152115_b(player.getUniqueID().toString());
            chester.setHealth(chester.getMaxHealth());
            world.spawnEntityInWorld(chester);
            world.setEntityState(chester, (byte) 7);
            stack.setItemDamage(1);
        }
        return stack;
    }
}
