/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.io.IOException;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.nmccoy.legendgear.LegendGear2;

public class StruckGroundBlock
extends Block {
    public IIcon sandIcon;
    public IIcon dirtIcon;
    public IIcon sandTopIcon;
    public IIcon dirtTopIcon;

    public StruckGroundBlock() {
        super(Material.sand);
        this.setBlockName("struckGround");
        this.setHardness(0.5f);
        this.setStepSound(soundTypeSand);
        this.setCreativeTab(LegendGear2.legendgearTab);
    }

    public int damageDropped(int dam) {
        return dam;
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister reg) {
        this.sandIcon = reg.registerIcon("sand");
        this.sandTopIcon = reg.registerIcon(this.firstAvailableBlockTexture(
                "legendgear:struck_sand",
                "legendgear:struckSand",
                "riftflux:struck_sand",
                "riftflux:struckSand"
        ));
        this.dirtIcon = reg.registerIcon("dirt");
        this.dirtTopIcon = reg.registerIcon(this.firstAvailableBlockTexture(
                "legendgear:struck_dirt",
                "legendgear:struckDirt",
                "riftflux:struck_dirt",
                "riftflux:struckDirt"
        ));
    }

    @SideOnly(value=Side.CLIENT)
    private String firstAvailableBlockTexture(String ... keys) {
        for (String key : keys) {
            if (this.blockTextureExists(key)) {
                return key;
            }
        }
        return keys[0];
    }

    @SideOnly(value=Side.CLIENT)
    private boolean blockTextureExists(String key) {
        int split = key.indexOf(':');
        String domain = split >= 0 ? key.substring(0, split) : "minecraft";
        String path = split >= 0 ? key.substring(split + 1) : key;
        ResourceLocation texture = new ResourceLocation(domain, "textures/blocks/" + path + ".png");
        try {
            Minecraft.getMinecraft().getResourceManager().getResource(texture);
            return true;
        }
        catch (IOException ignored) {
            return false;
        }
    }

    public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List list) {
        list.add(new ItemStack(Item.getItemFromBlock((Block)this), 1, 0));
        list.add(new ItemStack(Item.getItemFromBlock((Block)this), 1, 1));
    }

    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        if (metadata == 0) {
            drops.add(new ItemStack((Block)Blocks.sand));
        } else {
            drops.add(new ItemStack(Blocks.dirt));
        }
        return drops;
    }

    public IIcon getIcon(int side, int meta) {
        if (meta == 0) {
            if (side == 1) {
                return this.sandTopIcon;
            }
            return this.sandIcon;
        }
        if (side == 1) {
            return this.dirtTopIcon;
        }
        return this.dirtIcon;
    }
}
