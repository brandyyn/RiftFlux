package gravestone.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockGSInvisibleWall extends Block {
   public BlockGSInvisibleWall() {
      super(Material.rock);
      this.setBlockName("GSInvisibleWall");
      this.setResistance(100500.0F);
      this.setStepSound(Block.soundTypeStone);
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   }

   public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
      return AxisAlignedBB.getBoundingBox((double)x, (double)y, (double)z, (double)(x + 1), (double)(y + 1), (double)(z + 1));
   }

   public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune) {
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
      return false;
   }

   public float getExplosionResistance(Entity entity) {
      return 1.8E7F;
   }

   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      return false;
   }

   public int getRenderType() {
      return -1;
   }
}
