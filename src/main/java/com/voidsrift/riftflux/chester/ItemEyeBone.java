package com.voidsrift.riftflux.chester;

import baubles.api.BaubleType;
import baubles.api.expanded.BaubleItemHelper;
import baubles.api.expanded.IBaubleExpanded;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import makamys.satchels.compat.BaublesCompat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemEyeBone extends Item implements IBaubleExpanded {
    public ItemEyeBone() {
        setMaxStackSize(1);
        setFull3D();
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        return getBaubleTypes(stack).length > 0
                ? BaubleItemHelper.onBaubleRightClick(stack, world, player)
                : stack;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held) {
        if (entity instanceof EntityPlayer && !world.isRemote && entity.ticksExisted % 10 == slot % 10) {
            ChesterBinding.ensureChester(stack, (EntityPlayer) entity);
        }
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        entityItem.age = 0;
        if (!entityItem.worldObj.isRemote && entityItem.ticksExisted % 10 == 0) {
            ChesterBinding.updateGroundAnchor(
                    entityItem.getEntityItem(),
                    entityItem.worldObj,
                    entityItem.posX,
                    entityItem.posY,
                    entityItem.posZ
            );
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        String boundName = ChesterBinding.getBoundName(stack);
        if (boundName != null && !boundName.isEmpty()) {
            tooltip.add(StatCollector.translateToLocalFormatted("tooltip.chester.bound", boundName));
        }
        String[] baubleTypes = getBaubleTypes(stack);
        if (baubleTypes.length > 0) {
            BaubleItemHelper.addSlotInformation(tooltip, baubleTypes);
        }
    }

    @Override
    public String[] getBaubleTypes(ItemStack stack) {
        return BaublesCompat.getTypes(BaublesCompat.ITEM_EYEBONE, ChesterContent.BAUBLE_TYPE);
    }

    @Override
    public BaubleType getBaubleType(ItemStack stack) {
        return BaubleType.UNIVERSAL;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase wearer) {
        if (wearer instanceof EntityPlayer && !wearer.worldObj.isRemote && wearer.ticksExisted % 10 == 0) {
            ChesterBinding.ensureChester(stack, (EntityPlayer) wearer);
        }
    }

    @Override
    public void onEquipped(ItemStack stack, EntityLivingBase wearer) {
    }

    @Override
    public void onUnequipped(ItemStack stack, EntityLivingBase wearer) {
    }

    @Override
    public boolean canEquip(ItemStack stack, EntityLivingBase wearer) {
        return getBaubleTypes(stack).length > 0;
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityLivingBase wearer) {
        return true;
    }
}
