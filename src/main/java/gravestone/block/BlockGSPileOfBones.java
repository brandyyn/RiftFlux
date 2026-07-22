package gravestone.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.block.enums.EnumPileOfBones;
import gravestone.config.GraveStoneConfig;
import gravestone.core.GSBlock;
import gravestone.core.GSTabs;
import gravestone.tileentity.TileEntityGSPileOfBones;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockGSPileOfBones extends BlockContainer {
   public BlockGSPileOfBones() {
      super(Material.circuits);
      this.setStepSound(Block.soundTypeStone);
      this.setBlockName("pile of bones");
      this.setHardness(0.1F);
      this.setResistance(0.0F);
      this.setBlockTextureName("snow");
      this.setCreativeTab(GSTabs.otherItemsTab);
      this.setBlockBounds(0.1F, 0.0F, 0.1F, 0.9F, 0.2F, 0.9F);
   }

   public TileEntity createNewTileEntity(World world, int p_149915_2_) {
      return new TileEntityGSPileOfBones();
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
      return GraveStoneConfig.pileOfBonesRenderID;
   }

   protected boolean canSilkHarvest() {
      return true;
   }

   public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
      return Items.bone;
   }

   public int quantityDropped(Random random) {
      int minDrops = GraveStoneConfig.pileOfBonesMinDrops;
      int maxDrops = GraveStoneConfig.pileOfBonesMaxDrops;
      return minDrops + random.nextInt(maxDrops - minDrops + 1);
   }

   public int damageDropped(int damage) {
      return damage;
   }

   protected ItemStack createStackedBlock(int meta) {
      return new ItemStack(GSBlock.pileOfBones, 1, meta);
   }

   public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
      world.setBlockMetadataWithNotify(x, y, z, stack.getItemDamage(), 2);
      TileEntityGSPileOfBones te = (TileEntityGSPileOfBones)world.getTileEntity(x, y, z);
      if (te != null) {
         te.setDirection((byte)(MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3));
      }

   }

   @SideOnly(Side.CLIENT)
   public void getSubBlocks(Item item, CreativeTabs tab, List list) {
      for(byte i = 0; i < EnumPileOfBones.values().length; ++i) {
         list.add(new ItemStack(item, 1, i));
      }

   }

   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      return World.doesBlockHaveSolidTopSurface(world, x, y - 1, z);
   }

   public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
      if (!this.canPlaceBlockAt(world, x, y, z)) {
         this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
         world.setBlockToAir(x, y, z);
      }

   }
}
