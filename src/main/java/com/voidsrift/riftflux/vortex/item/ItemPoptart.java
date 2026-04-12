package com.voidsrift.riftflux.vortex.item;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.nmccoy.legendgear.PlayerStarstatsExtension;

public class ItemPoptart extends ItemFood {
    public ItemPoptart(int foodValue, float saturation) {
        super(Math.max(0, foodValue), Math.max(0.0F, saturation), false);
        this.setTextureName("riftflux:poptart");
        this.setCreativeTab(CreativeTabs.tabFood);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);

        if (world == null || world.isRemote || player == null || !ModConfig.enableLegendGearModule) {
            return;
        }

        PlayerStarstatsExtension stats = PlayerStarstatsExtension.get(player);
        if (stats == null) {
            return;
        }

        float restore = Math.max(0.0F, ModConfig.poptartLegendGearManaRestore);
        if (restore <= 0.0F) {
            return;
        }

        stats.setMana(Math.max(0.0F, stats.getMana() - restore));
    }
}
