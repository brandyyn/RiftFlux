package com.voidsrift.riftflux.vortex.item;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.nmccoy.legendgear.PlayerStarstatsExtension;
import net.nmccoy.legendgear.item.RandomFoodPotionEffectHelper;

import java.util.List;
import java.util.Locale;

public class ItemPoptart extends ItemFood {
    public ItemPoptart(int foodValue, float saturation) {
        super(Math.max(0, foodValue), Math.max(0.0F, saturation), false);
        this.setTextureName("riftflux:poptart");
        this.setCreativeTab(CreativeTabs.tabFood);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);

        if (world != null && !world.isRemote && player != null) {
            PotionEffect effect = RandomFoodPotionEffectHelper.getRandomConfiguredEffect(world, ModConfig.legendGearSweetSnackPotionEffects);
            if (effect != null) {
                player.addPotionEffect(effect);
            }
        }

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

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        float restore = Math.max(0.0F, ModConfig.poptartLegendGearManaRestore);
        if (restore > 0.0F && ModConfig.enableLegendGearModule) {
            list.add(EnumChatFormatting.AQUA + "Restores " + formatManaValue(restore) + " Mana");
        }
    }

    private static String formatManaValue(float value) {
        String formatted = String.format(Locale.ROOT, "%.2f", Math.max(0.0F, value));
        int end = formatted.length();
        while (end > 0 && formatted.charAt(end - 1) == '0') {
            end--;
        }
        if (end > 0 && formatted.charAt(end - 1) == '.') {
            end--;
        }
        return end > 0 ? formatted.substring(0, end) : "0";
    }
}
