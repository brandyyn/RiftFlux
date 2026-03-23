package com.voidsrift.riftflux.wheatfield;

import com.voidsrift.riftflux.Constants;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.IIcon;

public class ItemBlockWheatfieldBarley extends ItemBlock {
    @SideOnly(Side.CLIENT)
    private IIcon itemIcon;

    public ItemBlockWheatfieldBarley(Block block) {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon(Constants.MODID + ":wheatfield/item_barley");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        return itemIcon == null ? super.getIconFromDamage(damage) : itemIcon;
    }

    @Override
    public int getSpriteNumber() {
        return 1;
    }
}
