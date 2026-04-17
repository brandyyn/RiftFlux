package com.voidsrift.riftflux.pumpkinpastures;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.compat.BackhandCompat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.nmccoy.legendgear.entity.EntitySpellEffect;
import net.nmccoy.legendgear.item.spell.StaffFire;

public class ItemPumpkinShovel extends StaffFire {
    public ItemPumpkinShovel() {
        setUnlocalizedName("pumpkin_shovel");
        setTextureName("riftflux:pumpkinpastures/pumpkin_shovel");
        setCreativeTab(CreativeTabs.tabCombat);
        setMaxDamage(Math.max(0, ModConfig.pumpkinPasturesEnderflameStaffDurability));
        this.baseArcanePower = Math.max(0.0F, ModConfig.pumpkinPasturesEnderflameStaffSpellDamage);
        this.baseStaminaCost = Math.max(0.0F, ModConfig.pumpkinPasturesEnderflameStaffManaCost);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        if (!ModConfig.pumpkinPasturesEnderflameStaffCastsSpell) {
            return EnumAction.block;
        }
        return super.getItemUseAction(stack);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        if (!ModConfig.pumpkinPasturesEnderflameStaffCastsSpell) {
            return 72000;
        }
        return super.getMaxItemUseDuration(stack);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!ModConfig.pumpkinPasturesEnderflameStaffCastsSpell) {
            if (BackhandCompat.isAvailable() && BackhandCompat.isOffhandStack(player, stack)) {
                BackhandCompat.setOffhandItemInUse(player, true);
            }
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
            return stack;
        }
        return super.onItemRightClick(stack, world, player);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int count) {
        if (!ModConfig.pumpkinPasturesEnderflameStaffCastsSpell) {
            if (BackhandCompat.isAvailable() && BackhandCompat.isOffhandStack(player, stack)) {
                BackhandCompat.setOffhandItemInUse(player, false);
            }
            return;
        }
        super.onPlayerStoppedUsing(stack, world, player, count);
    }

    @Override
    public void GenerateSpell(EntityPlayer caster, EntitySpellEffect.SpellType id, Vec3 location, double radius, double power, boolean critical) {
        if (caster == null || caster.worldObj == null || caster.worldObj.isRemote) {
            return;
        }
        int fireSeconds = Math.max(0, (int) Math.ceil(Math.max(0.0F, ModConfig.pumpkinPasturesEnderflameStaffFireSeconds)));
        EntitySpellEffect effect = new EntitySpellEffect(caster.worldObj, id, caster, location, radius, power, critical)
                .withOverrideFireSeconds(fireSeconds);
        caster.worldObj.spawnEntityInWorld(effect);
    }

    @Override
    public float getManaCost() {
        return Math.max(0.0F, ModConfig.pumpkinPasturesEnderflameStaffManaCost);
    }

    @Override
    protected double getBasePower(ItemStack stack) {
        return Math.max(0.0F, ModConfig.pumpkinPasturesEnderflameStaffSpellDamage);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return net.minecraft.util.EnumChatFormatting.GOLD + super.getItemStackDisplayName(stack);
    }
}
