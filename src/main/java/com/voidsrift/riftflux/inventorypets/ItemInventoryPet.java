package com.voidsrift.riftflux.inventorypets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemInventoryPet extends Item {
    private final String displayName;
    private final String texturePath;

    public ItemInventoryPet(String unlocalizedName, String displayName, String texturePath) {
        this.displayName = displayName;
        this.texturePath = texturePath;
        this.setUnlocalizedName(unlocalizedName);
        this.setTextureName(texturePath);
        this.setMaxStackSize(1);
        this.setCreativeTab(null);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return this.displayName;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        this.itemIcon = register.registerIcon(this.texturePath);
    }
}
