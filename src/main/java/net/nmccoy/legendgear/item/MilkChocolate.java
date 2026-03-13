/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemFood
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.nmccoy.legendgear.LegendGear2;

public class MilkChocolate
extends ItemFood {
    public MilkChocolate() {
        super(2, 0.0f, false);
        this.setMaxStackSize(16);
        this.setCreativeTab(LegendGear2.legendgearTab);
        this.setUnlocalizedName("milkChocolate");
        this.setTextureName("legendgear:itemMilkChocolate");
        this.setAlwaysEdible();
    }

    public void addRecipes() {
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)this), (Object[])new Object[]{Items.milk_bucket, Items.sugar, new ItemStack(Items.dye, 1, 3)});
    }

    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            player.curePotionEffects(new ItemStack(Items.milk_bucket));
        }
        return super.onEaten(stack, world, player);
    }
}

