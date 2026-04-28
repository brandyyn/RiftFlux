package com.voidsrift.riftflux.pumpkinpastures;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class ItemEnderflamePickaxe extends ItemPickaxe {
    public ItemEnderflamePickaxe(ToolMaterial material) {
        super(material);
        setUnlocalizedName("enderflame_pickaxe");
        setTextureName("riftflux:pumpkinpastures/enderflame_pickaxe");
        setCreativeTab(CreativeTabs.tabTools);
        setMaxDamage(Math.max(0, ModConfig.pumpkinPasturesEnderflamePickaxeDurability));
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
