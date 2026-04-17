package com.voidsrift.riftflux.pumpkinpastures;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class ItemPumpkinPickaxe extends ItemPickaxe {
    public ItemPumpkinPickaxe(ToolMaterial material) {
        super(material);
        setUnlocalizedName("pumpkin_pickaxe");
        setTextureName("riftflux:pumpkinpastures/pumpkin_pickaxe");
        setCreativeTab(CreativeTabs.tabTools);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        boolean result = super.hitEntity(stack, target, attacker);
        if (target != null) {
            target.setFire(8);
        }
        return result;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return EnumChatFormatting.GOLD + super.getItemStackDisplayName(stack);
    }
}
