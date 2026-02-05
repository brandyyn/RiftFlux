package com.voidsrift.riftflux.mixin.early.baubles;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.expanded.IBaubleExpanded;
import baubles.common.container.ContainerPlayerExpanded;
import com.voidsrift.riftflux.vortex.item.ItemBackpack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mixin(value = ContainerPlayerExpanded.class, remap = false)
public abstract class MixinContainerPlayerExpanded_BackpackShiftClick {

    @Unique
    private static final Item rf$backpackProxy = new ItemBackpackBaubleProxy();

    @ModifyVariable(
            method = {"transferStackInSlot", "func_82846_b"},
            at = @At(value = "STORE"),
            ordinal = 0
    )
    private Item rf$swapBackpackItem(Item item) {
        return item instanceof ItemBackpack ? rf$backpackProxy : item;
    }

    private static final class ItemBackpackBaubleProxy extends Item implements IBaubleExpanded {
        @Override
        public String[] getBaubleTypes(ItemStack stack) {
            return new String[]{"backpack"};
        }

        @Override
        public BaubleType getBaubleType(ItemStack stack) {
            return BaubleType.UNIVERSAL;
        }

        @Override
        public void onWornTick(ItemStack stack, net.minecraft.entity.EntityLivingBase player) {
        }

        @Override
        public void onEquipped(ItemStack stack, net.minecraft.entity.EntityLivingBase player) {
        }

        @Override
        public void onUnequipped(ItemStack stack, net.minecraft.entity.EntityLivingBase player) {
        }

        @Override
        public boolean canEquip(ItemStack stack, net.minecraft.entity.EntityLivingBase player) {
            return true;
        }

        @Override
        public boolean canUnequip(ItemStack stack, net.minecraft.entity.EntityLivingBase player) {
            return true;
        }

        @Override
        public void onPlayerLoad(ItemStack stack, net.minecraft.entity.EntityLivingBase player) {
        }
    }
}
