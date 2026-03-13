/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package tk.nukeduck.hearts.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tk.nukeduck.hearts.HeartCrystal;

public class ItemHeartCrystal
extends ItemBlock {
    public ItemHeartCrystal(Block block) {
        super(block);
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon("hearts:heart_crystal");
    }

    @SideOnly(value=Side.CLIENT)
    public boolean hasEffect(ItemStack par1ItemStack, int pass) {
        return true;
    }

    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        --stack.stackSize;
        world.playSoundAtEntity((Entity)player, "random.burp", 0.5f, world.rand.nextFloat() * 0.1f + 0.9f);
        HeartCrystal.events.onEat(player);
        return stack;
    }

    public boolean hasSpace(EntityPlayer player) {
        AttributeModifier modifier = HeartCrystal.events.getBoost(player);
        double currentBonus = modifier == null ? 0.0 : modifier.getAmount();
        double maxBonus = (double)Math.max(0, HeartCrystal.config.getMaxHearts()) * 2.0;
        double perCrystalBonus = (double)Math.max(1, HeartCrystal.config.getHeartsPerCrystal()) * 2.0;
        return currentBonus + perCrystalBonus <= maxBonus + 1.0E-6;
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.canEat(true) && this.hasSpace(player)) {
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        }
        return stack;
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.eat;
    }

    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }
}
