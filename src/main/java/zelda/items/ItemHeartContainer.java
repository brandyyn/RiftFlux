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
        if (world.isRemote) {
            return stack;
        }
        ExtendedPlayerProperties props = ExtendedPlayerProperties.get(player);
        if (props == null) {
            ExtendedPlayerProperties.register(player);
            props = ExtendedPlayerProperties.get(player);
        }
        if (props != null) {
            props.addHeart();
        }
        if (player.capabilities.isCreativeMode) {
            return stack;
        }
        --stack.stackSize;
        world.playSoundAtEntity((Entity)player, "random.levelup", 0.5f, world.rand.nextFloat() * 0.1f + 0.9f);
        if (stack.stackSize <= 0) {
            return null;
        }
        return stack;
    }

    public int getMaxItemUseDuration(ItemStack stack) {
        return 0;
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.none;
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        return this.tryConsume(stack, world, player);
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
                             int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        ItemStack result = this.tryConsume(stack, world, player);
        return result != stack || result == null;
    }

    private ItemStack tryConsume(ItemStack stack, World world, EntityPlayer player) {
        int maxHearts = Math.max(Config.STARTING_HEARTS, Config.MAXIMUM_HEARTS);
        ExtendedPlayerProperties props = ExtendedPlayerProperties.get(player);
        if (props == null) {
            ExtendedPlayerProperties.register(player);
            props = ExtendedPlayerProperties.get(player);
        }
        if (props != null && props.getMaxHearts() < (double)maxHearts) {
            if (!world.isRemote) {
                ItemStack result = this.onEaten(stack, world, player);
                player.inventory.markDirty();
                return result;
            }
            return stack;
        }
        if (world.isRemote) {
            player.addChatComponentMessage((IChatComponent)new ChatComponentText("You are at the maximum heart capacity."));
        }
        return stack;
    }
}
