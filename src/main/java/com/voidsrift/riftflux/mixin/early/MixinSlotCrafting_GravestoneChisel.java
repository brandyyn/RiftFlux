package com.voidsrift.riftflux.mixin.early;

import gravestone.core.GSBlock;
import gravestone.core.recipe.GSChiselRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlotCrafting.class)
public abstract class MixinSlotCrafting_GravestoneChisel {
    @Unique
    private boolean riftflux$gravestoneRecipe;

    @Inject(method = "onPickupFromSlot", at = @At("HEAD"))
    private void riftflux$beginGravestoneCraft(EntityPlayer player, ItemStack output, CallbackInfo ci) {
        Item outputItem = output == null ? null : output.getItem();
        this.riftflux$gravestoneRecipe = outputItem == Item.getItemFromBlock(GSBlock.graveStone)
                || outputItem == Item.getItemFromBlock(GSBlock.memorial);
    }

    @Redirect(method = "onPickupFromSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;hasContainerItem(Lnet/minecraft/item/ItemStack;)Z", remap = false))
    private boolean riftflux$retainConfiguredChisel(Item item, ItemStack stack) {
        return this.riftflux$gravestoneRecipe && GSChiselRegistry.isConfiguredChisel(stack)
                || item.hasContainerItem(stack);
    }

    @Redirect(method = "onPickupFromSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getContainerItem(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", remap = false))
    private ItemStack riftflux$damageConfiguredChisel(Item item, ItemStack stack) {
        return this.riftflux$gravestoneRecipe && GSChiselRegistry.isConfiguredChisel(stack)
                ? GSChiselRegistry.getDamagedCraftingChisel(stack)
                : item.getContainerItem(stack);
    }

    @Redirect(method = "onPickupFromSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;doesContainerItemLeaveCraftingGrid(Lnet/minecraft/item/ItemStack;)Z", remap = false))
    private boolean riftflux$keepConfiguredChiselInGrid(Item item, ItemStack stack) {
        return this.riftflux$gravestoneRecipe && GSChiselRegistry.isConfiguredChisel(stack)
                ? false
                : item.doesContainerItemLeaveCraftingGrid(stack);
    }

    @Inject(method = "onPickupFromSlot", at = @At("RETURN"))
    private void riftflux$endGravestoneCraft(EntityPlayer player, ItemStack output, CallbackInfo ci) {
        this.riftflux$gravestoneRecipe = false;
    }
}
