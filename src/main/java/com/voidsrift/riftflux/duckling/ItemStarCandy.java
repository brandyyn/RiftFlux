package com.voidsrift.riftflux.duckling;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.nmccoy.legendgear.item.RandomFoodPotionEffectHelper;

public class ItemStarCandy extends ItemFood {
    public ItemStarCandy() {
        super(2, 0.3F, false);
        this.setAlwaysEdible();
        this.setTextureName(DucklingContent.MODID + ":star_candy");
        this.setUnlocalizedName("star_candy");
        this.setCreativeTab(CreativeTabs.tabFood);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);
        if (world == null || world.isRemote || player == null) {
            return;
        }

        PotionEffect effect = RandomFoodPotionEffectHelper.getRandomConfiguredEffect(world, ModConfig.legendGearSweetSnackPotionEffects);
        if (effect != null) {
            player.addPotionEffect(effect);
        }
    }
}
