package gravestone.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.config.GraveStoneConfig;
import gravestone.core.GSTabs;
import gravestone.core.TimeHelper;
import gravestone.particle.EntityGreenFlameFX;
import gravestone.tileentity.TileEntityGSCandle;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockGSCandle extends BlockContainer {
   public BlockGSCandle() {
      super(Material.carpet);
      this.setStepSound(Block.soundTypeCloth);
      this.setBlockName("candle");
      this.setHardness(0.0F);
      this.setLightLevel(1.0F);
      this.setResistance(0.0F);
      this.setBlockTextureName("snow");
      this.setCreativeTab(GSTabs.otherItemsTab);
      this.setBlockBounds(0.4F, 0.0F, 0.4F, 0.6F, 0.6F, 0.6F);
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(World world, int x, int y, int z, Random random) {
      double xPos = (double)x + 0.5D;
      double yPos = (double)y + 0.5D;
      double zPos = (double)z + 0.5D;
      long dayTime = TimeHelper.getDayTime(world);
      if (dayTime >= 13000L && dayTime <= 23000L) {
         EntityFX entityfx = new EntityGreenFlameFX(world, xPos, yPos, zPos, 0.0D, 0.0D, 0.0D);
         Minecraft.getMinecraft().effectRenderer.addEffect(entityfx);
      } else {
         world.spawnParticle("flame", xPos, yPos, zPos, 0.0D, 0.0D, 0.0D);
      }

      world.spawnParticle("smoke", xPos, yPos, zPos, 0.0D, 0.0D, 0.0D);
   }

   public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
      return null;
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean renderAsNormalBlock() {
      return false;
   }

   public int getRenderType() {
      return GraveStoneConfig.candleRenderID;
   }

   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      return this.canPlaceCandleOn(world, x, y - 1, z);
   }

   public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
      if (!this.canPlaceCandleOn(world, x, y - 1, z)) {
         this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
         world.setBlockToAir(x, y, z);
      }

   }

   private boolean canPlaceCandleOn(World world, int x, int y, int z) {
      if (World.doesBlockHaveSolidTopSurface(world, x, y, z)) {
         return true;
      } else {
         Block block = world.getBlock(x, y, z);
         return block != null && block.canPlaceTorchOnTop(world, x, y, z);
      }
   }

   public TileEntity createNewTileEntity(World world, int var2) {
      return new TileEntityGSCandle();
   }
}
