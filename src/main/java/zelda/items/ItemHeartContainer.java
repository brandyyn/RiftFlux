/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ChatComponentText
 *  net.minecraft.util.IChatComponent
 *  net.minecraft.world.World
 */
package zelda.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import zelda.Config;
import zelda.ExtendedPlayerProperties;

public class ItemHeartContainer
extends Item {
    public ItemHeartContainer() {
        this.setMaxStackSize(1);
    }

    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        --stack.stackSize;
        world.playSoundAtEntity((Entity)player, "random.levelup", 0.5f, world.rand.nextFloat() * 0.1f + 0.9f);
        ExtendedPlayerProperties.get(player).addHeart();
        return stack;
    }

    public int getMaxItemUseDuration(ItemStack stack) {
        return 16;
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.block;
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (ExtendedPlayerProperties.get(player).getMaxHearts() < (double)Config.MAXIMUM_HEARTS) {
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        } else if (world.isRemote) {
            player.addChatComponentMessage((IChatComponent)new ChatComponentText("You are at the maximum heart capacity."));
        }
        return stack;
    }
}

