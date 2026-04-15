package com.voidsrift.riftflux.inventorypets;

import com.voidsrift.riftflux.asgardshield.AsgardShieldState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemInventoryShieldPet extends ItemInventoryPet {
    private final boolean fluxVariant;

    public ItemInventoryShieldPet(String unlocalizedName, String displayName, String texturePath, boolean fluxVariant) {
        super(unlocalizedName, displayName, texturePath);
        this.fluxVariant = fluxVariant;
        setMaxDamage(fluxVariant ? 378 : 250);
    }

    public boolean isFluxVariant() {
        return fluxVariant;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.block;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player == null || AsgardShieldState.isGuardBroken(player)) {
            return stack;
        }
        player.setItemInUse(stack, getMaxItemUseDuration(stack));
        return stack;
    }

    public float getDamageMultiplier() {
        return fluxVariant ? 0.80F : 0.85F;
    }

    public String getSoundProfile() {
        return "metal";
    }
}
