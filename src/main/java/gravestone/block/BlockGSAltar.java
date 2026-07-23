package gravestone.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.voidsrift.riftflux.riftflux;
import gravestone.core.GSTabs;
import gravestone.core.GSGuiHandler;
import gravestone.core.Resources;
import gravestone.tileentity.TileEntityGSAltar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockGSAltar extends BlockContainer {
   @SideOnly(Side.CLIENT)
   private IIcon topTexture;
   @SideOnly(Side.CLIENT)
   private IIcon bottomTexture;

   public BlockGSAltar() {
      super(Material.rock);
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
      this.setLightOpacity(0);
      this.setBlockName("Altar");
      this.setCreativeTab(GSTabs.otherItemsTab);
      this.setHarvestLevel("pickaxe", 2);
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
      TileEntity tileEntity = world.getTileEntity(x, y, z);
      if (!(tileEntity instanceof TileEntityGSAltar) || player.isSneaking()) {
         return false;
      }

      if (!world.isRemote) {
         player.openGui(riftflux.instance, GSGuiHandler.ALTAR_GUI_ID, world, x, y, z);
      }
      return true;
   }

   public boolean renderAsNormalBlock() {
      return false;
   }

   public boolean isOpaqueCube() {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public IIcon getIcon(int side, int metadata) {
      return side == 0 ? this.bottomTexture : (side == 1 ? this.topTexture : this.blockIcon);
   }

   @SideOnly(Side.CLIENT)
   public void registerBlockIcons(IIconRegister register) {
      this.blockIcon = register.registerIcon(Resources.ALTAR_SIDE);
      this.topTexture = register.registerIcon(Resources.ALTAR_TOP);
      this.bottomTexture = register.registerIcon(Resources.BONE_BLOCK);
   }

   public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
      return new TileEntityGSAltar();
   }

   public void breakBlock(World world, int x, int y, int z, Block block, int par6) {
      TileEntityGSAltar tileEntity = (TileEntityGSAltar)world.getTileEntity(x, y, z);
      if (tileEntity != null) {
         tileEntity.dropCorpse();
      }

      super.breakBlock(world, x, y, z, block, par6);
   }
}
