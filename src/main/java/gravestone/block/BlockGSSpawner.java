package gravestone.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.block.enums.EnumSpawner;
import gravestone.config.GraveStoneConfig;
import gravestone.core.GSBlock;
import gravestone.core.GSTabs;
import gravestone.core.Resources;
import gravestone.particle.EntityGreenFlameFX;
import gravestone.tileentity.TileEntityGSSpawner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockGSSpawner extends BlockMobSpawner {
   public static final List<Byte> MOB_SPAWNERS = new ArrayList<>(Arrays.asList((byte)EnumSpawner.SKELETON_SPAWNER.ordinal(), (byte)EnumSpawner.ZOMBIE_SPAWNER.ordinal()));
   public static final List<Byte> BOSS_SPAWNERS = new ArrayList<>(Arrays.asList((byte)EnumSpawner.WITHER_SPAWNER.ordinal()));

   public BlockGSSpawner() {
      this.setBlockName("Spawner");
      this.setHardness(5.0F);
      this.setLightLevel(0.45F);
      this.setStepSound(Block.soundTypeMetal);
      this.disableStats();
      this.setCreativeTab(GSTabs.otherItemsTab);
      this.setBlockTextureName(Resources.PENTAGRAM_ICO);
      this.setBlockBounds(-0.5F, 0.0F, -0.5F, 1.5F, 0.05F, 1.5F);
      this.setHarvestLevel("pickaxe", 1);
   }

   public TileEntity createNewTileEntity(World world, int var2) {
      return new TileEntityGSSpawner();
   }

   public int getRenderType() {
      return GraveStoneConfig.spawnerRenderID;
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
      return null;
   }

   public boolean renderAsNormalBlock() {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(World world, int x, int y, int z, Random random) {
      double xPos = (double)((float)x + 0.5F);
      double yPos = (double)y + 0.85D;
      double zPos = (double)((float)z + 0.5F);
      double dRotation = Math.toRadians(72.0D);
      double rotation = Math.toRadians(-36.0D);
      double d = 1.07D;

      for(int i = 0; i < 5; ++i) {
         double dx = -Math.sin(rotation) * d;
         double dz = Math.cos(rotation) * d;
         world.spawnParticle("smoke", xPos + dx, yPos, zPos + dz, 0.0D, 0.0D, 0.0D);
         EntityFX entityfx = new EntityGreenFlameFX(world, xPos + dx, yPos, zPos + dz, 0.0D, 0.0D, 0.0D);
         Minecraft.getMinecraft().effectRenderer.addEffect(entityfx);
         rotation += dRotation;
      }

   }

   public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
      ArrayList<ItemStack> ret = new ArrayList<>();
      ret.add(new ItemStack(this.getItemDropped(metadata, world.rand, fortune), this.quantityDropped(world.rand), this.getItemMeta(metadata)));

      for(int i = 0; i < 5; ++i) {
         if (fortune > 0 && world.rand.nextInt(100) < 5 * fortune || world.rand.nextInt(100) < 5 * fortune) {
            ret.add(this.getCustomItemsDropped(metadata));
         }
      }

      return ret;
   }

   public ItemStack getCustomItemsDropped(int meta) {
      switch(meta) {
      case 0:
      default:
         return new ItemStack(GSBlock.skullCandle, 1, 1);
      case 1:
         return new ItemStack(GSBlock.skullCandle, 1, 0);
      case 2:
         return new ItemStack(GSBlock.skullCandle, 1, 2);
      }
   }

   public int getItemMeta(int metadata) {
      return 15;
   }

   public Item getItemDropped(int meta, Random random, int fortune) {
      return Items.dye;
   }

   public int quantityDropped(Random random) {
      return 3;
   }

   @SideOnly(Side.CLIENT)
   public void getSubBlocks(Item item, CreativeTabs tab, List list) {
      for(byte meta : MOB_SPAWNERS) {
         list.add(new ItemStack(item, 1, meta));
      }

      for(byte meta : BOSS_SPAWNERS) {
         list.add(new ItemStack(item, 1, meta));
      }

   }
}
