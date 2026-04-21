package com.voidsrift.riftflux.duckling;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemDuckEgg extends Item {
    public ItemDuckEgg() {
        this.setMaxStackSize(16);
        this.setCreativeTab(CreativeTabs.tabMaterials);
        this.setTextureName(DucklingContent.MODID + ":duck_egg");
        this.setUnlocalizedName("duck_egg");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }

        world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!world.isRemote) {
            world.spawnEntityInWorld(new EntityDuckEgg(world, player));
        }

        return stack;
    }
}
