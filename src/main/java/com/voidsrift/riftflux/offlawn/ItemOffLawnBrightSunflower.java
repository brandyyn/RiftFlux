package com.voidsrift.riftflux.offlawn;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemOffLawnBrightSunflower extends ItemBlock {
    @SideOnly(Side.CLIENT)
    private IIcon brightSunflowerIcon;

    public ItemOffLawnBrightSunflower(Block block) {
        super(block);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return this.field_150939_a.getUnlocalizedName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        this.brightSunflowerIcon = register.registerIcon("riftflux:offlawn/bright_sunflower");
        this.itemIcon = this.brightSunflowerIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return this.brightSunflowerIcon != null ? this.brightSunflowerIcon : super.getIconFromDamage(meta);
    }

    @Override
    public int getSpriteNumber() {
        return 1;
    }
}
