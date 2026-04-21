package zairus.worldexplorer.core.block;

import com.voidsrift.riftflux.riftflux;
import com.voidsrift.riftflux.riftexplorer.RiftExplorerGuiIds;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import zairus.worldexplorer.core.WorldExplorer;
import zairus.worldexplorer.core.tileentity.TileEntityDesk;

public class BlockStudyDesk extends BlockContainer {
    private IIcon blockIconTop;
    private IIcon blockIconFront;
    private IIcon blockIconBack;
    private int renderType = 0;

    protected BlockStudyDesk(String unlocalizedName) {
        super(Material.wood);
        this.setBlockName(unlocalizedName);
        this.setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabDecorations);
        this.setBlockTextureName("worldexplorer:studydesk");
        this.setBlockName("studydesk");
        this.setStepSound(soundTypeWood);
        this.setHardness(1.5F);
        this.setResistance(5.0F);
        this.setHarvestLevel("axe", 0);
        this.setBlockBounds(0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
    }

    public void setRenderId(int id) {
        this.renderType = id;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 1) {
            return this.blockIconTop;
        }
        if (side == 0) {
            return Blocks.planks.getBlockTextureFromSide(side);
        }
        if (side == 4) {
            return this.blockIconBack;
        }
        if (side == 2) {
            return this.blockIconFront;
        }
        return this.blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.blockIcon = iconRegister.registerIcon(this.getTextureName() + "_side");
        this.blockIconTop = iconRegister.registerIcon(this.getTextureName() + "_top");
        this.blockIconFront = iconRegister.registerIcon(this.getTextureName() + "_front");
        this.blockIconBack = iconRegister.registerIcon(this.getTextureName() + "_back");
    }

    @Override
    public boolean onBlockActivated(
            World world,
            int x,
            int y,
            int z,
            EntityPlayer player,
            int side,
            float hitX,
            float hitY,
            float hitZ
    ) {
        if (world.isRemote) {
            return true;
        }
        player.openGui(riftflux.instance, RiftExplorerGuiIds.STUDY_DESK, world, x, y, z);
        return true;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return this.renderType;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean canProvidePower() {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityDesk();
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        setDefaultDirection(world, x, y, z);
    }

    private void setDefaultDirection(World world, int x, int y, int z) {
        if (!world.isRemote) {
            Block north = world.getBlock(x, y, z - 1);
            Block south = world.getBlock(x, y, z + 1);
            Block west = world.getBlock(x - 1, y, z);
            Block east = world.getBlock(x + 1, y, z);
            int meta = 3;
            if (north.isOpaqueCube() && !south.isOpaqueCube()) {
                meta = 3;
            }
            if (south.isOpaqueCube() && !north.isOpaqueCube()) {
                meta = 2;
            }
            if (west.isOpaqueCube() && !east.isOpaqueCube()) {
                meta = 5;
            }
            if (east.isOpaqueCube() && !west.isOpaqueCube()) {
                meta = 4;
            }
            world.setBlockMetadataWithNotify(x, y, z, meta, 3);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        int meta = 0;
        int direction = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        if (direction == 0) {
            meta = 2;
        }
        if (direction == 1) {
            meta = 5;
        }
        if (direction == 2) {
            meta = 3;
        }
        if (direction == 3) {
            meta = 4;
        }
        world.setBlockMetadataWithNotify(x, y, z, meta, 3);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntityDesk tileEntityDesk = (TileEntityDesk) world.getTileEntity(x, y, z);
        if (tileEntityDesk != null) {
            for (int i = 0; i < tileEntityDesk.getSizeInventory(); ++i) {
                if (tileEntityDesk.getStackInSlot(i) != null) {
                    world.spawnEntityInWorld(new EntityItem(world, x, y, z, tileEntityDesk.getStackInSlot(i)));
                }
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }
}
