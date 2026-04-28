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
 *  net.minecraft.stats.StatBase
 *  net.minecraft.util.ChatComponentText
 *  net.minecraft.util.EnumChatFormatting
 *  net.minecraft.util.IChatComponent
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.item;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.nmccoy.legendgear.LegendGear2;

public class FortuneCookie
extends ItemFood {
    public FortuneCookie() {
        super(1, 0.0f, false);
        this.setMaxStackSize(16);
        this.setCreativeTab(LegendGear2.legendgearTab);
        this.setUnlocalizedName("fortuneCookie");
        this.setTextureName("legendgear:fortuneCookie");
        this.setAlwaysEdible();
    }

    public void addRecipes() {
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)this), (Object[])new Object[]{Items.paper, Items.wheat, Items.sugar});
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            String fortune = LegendGear2.fortunes.randomFortune();
            player.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.ITALIC + fortune));
            player.addStat((StatBase)LegendGear2.achievementFortunate, 1);
            PotionEffect effect = this.getRandomConfiguredEffect(world);
            if (effect != null) {
                player.addPotionEffect(effect);
            }
        }
        return super.onEaten(stack, world, player);
    }

    private PotionEffect getRandomConfiguredEffect(World world) {
        return RandomFoodPotionEffectHelper.getRandomConfiguredEffect(world, ModConfig.legendGearFortuneCookiePotionEffects);
    }
}
