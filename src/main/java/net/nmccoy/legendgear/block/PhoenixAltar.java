/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.BlockContainer
 *  net.minecraft.block.material.Material
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.IIcon
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.block.TileEntityAltar;

public class PhoenixAltar
extends BlockContainer {
    @SideOnly(value=Side.CLIENT)
    private IIcon topIcon;
    @SideOnly(value=Side.CLIENT)
    private IIcon bottomIcon;

    public PhoenixAltar() {
        super(Material.rock);
        this.setHarvestLevel("pickaxe", 3);
        this.setHardness(50.0f);
        this.setResistance(2000.0f);
        this.setStepSound(soundTypePiston);
        this.setBlockName("starAltar");
        this.setCreativeTab(LegendGear2.legendgearTab);
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.75f, 1.0f);
        this.setLightOpacity(0);
        this.setLightLevel(0.6f);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister regi) {
        this.blockIcon = regi.registerIcon(this.firstAvailableBlockTexture(
                "legendgear:phoenix_altar_side",
                "legendgear:phoenixAltarSide",
                "riftflux:phoenix_altar_side",
                "riftflux:phoenixAltarSide"
        ));
        this.topIcon = regi.registerIcon(this.firstAvailableBlockTexture(
                "legendgear:phoenix_altar_top",
                "legendgear:phoenixAltarTop",
                "riftflux:phoenix_altar_top",
                "riftflux:phoenixAltarTop"
        ));
        this.bottomIcon = regi.registerIcon("obsidian");
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

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return side == 0 ? this.bottomIcon : (side == 1 ? this.topIcon : this.blockIcon);
    }

    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityAltar();
    }
}
