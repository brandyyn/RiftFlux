/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.ItemStack
 */
package net.nmccoy.legendgear.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.nmccoy.legendgear.item.LGItem;

import java.util.List;

public class PhoenixFeather
extends LGItem {
    public PhoenixFeather() {
        this.setUnlocalizedName("phoenixFeather");
        this.tabs.add(CreativeTabs.tabMaterials);
        this.setTextureName("legendgear:itemPhoenixFeather");
    }

    public EnumRarity getRarity(ItemStack p_77613_1_) {
        return EnumRarity.uncommon;
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        list.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("tooltip.legendgear.phoenix_feather.revive"));
        list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("tooltip.legendgear.phoenix_feather.consume"));
    }

    @Override
    public boolean hasEffect(ItemStack stack, int pass) {
        return stack.getItemDamage() == 0;
    }

    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        EntityLivingBase elb;
        if (entity instanceof EntityLivingBase && (elb = (EntityLivingBase)entity).isEntityUndead()) {
            elb.setFire(30);
        }
        return false;
    }
}
