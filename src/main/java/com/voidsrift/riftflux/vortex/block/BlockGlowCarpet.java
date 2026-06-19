package com.voidsrift.riftflux.vortex.block;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockGlowCarpet extends Block {

    private static final int TEXTURE_VARIANTS = 9;

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public BlockGlowCarpet() {
        super(Material.cloth);
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        if (!ModConfig.randomizeGlowCarpetTextureOnPlacement) {
            return 0;
        }
        return world.rand.nextInt(TEXTURE_VARIANTS);
    }

    @Override
    public int damageDropped(int meta) {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        int variant = meta >= 0 && meta < TEXTURE_VARIANTS ? meta : 0;
        return this.icons == null ? this.blockIcon : this.icons[variant];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.icons = new IIcon[TEXTURE_VARIANTS];
        this.icons[0] = iconRegister.registerIcon(this.getTextureName());
        for (int i = 1; i < TEXTURE_VARIANTS; ++i) {
            this.icons[i] = iconRegister.registerIcon(this.getTextureName() + i);
        }
        this.blockIcon = this.icons[0];
    }

    @Override
    public int getRenderType() {
        if (ModConfig.randomizeGlowCarpetRotation && ModBlocks.glowCarpetRenderId >= 0) {
            return ModBlocks.glowCarpetRenderId;
        }
        return super.getRenderType();
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return ModConfig.glowCarpetLightLevel;
    }

    @Override
    public int getLightValue() {
        return ModConfig.glowCarpetLightLevel;
    }
}