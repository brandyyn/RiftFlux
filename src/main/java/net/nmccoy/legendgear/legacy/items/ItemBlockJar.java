package net.nmccoy.legendgear.legacy.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.IIcon;
import net.nmccoy.legendgear.legacy.blocks.TileEntityJar;

public class ItemBlockJar extends ItemBlock {
    public ItemBlockJar(Block block) {
        super(block);
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        if (stack != null
                && stack.hasTagCompound()
                && stack.getTagCompound().hasKey(TileEntityJar.TAG_JAR_DATA, 10)) {
            return 1;
        }
        return super.getItemStackLimit(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSpriteNumber() {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        return this.field_150939_a.getIcon(2, damage);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        if (stack == null || !stack.hasTagCompound() || !stack.getTagCompound().hasKey(TileEntityJar.TAG_JAR_DATA, 10)) {
            return;
        }

        tooltip.add(TileEntityJar.describeJarData(stack.getTagCompound().getCompoundTag(TileEntityJar.TAG_JAR_DATA)));
    }
}
