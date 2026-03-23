package tk.nukeduck.hearts.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockStarLantern
extends BlockLantern {
    public BlockStarLantern(Material material) {
        super(material);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister icon) {
        this.blockIcon = icon.registerIcon("hearts:star_lantern");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityStarLantern();
    }
}
