package gravestone.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.block.enums.EnumHauntedChest;
import gravestone.core.GSTabs;
import gravestone.tileentity.TileEntityGSHauntedChest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class BlockGSHauntedChest extends BlockContainer {
   public BlockGSHauntedChest() {
      super(Material.wood);
      this.setStepSound(Block.soundTypeWood);
      this.setBlockName("HauntedChest");
      this.setHardness(2.5F);
      this.setCreativeTab(GSTabs.otherItemsTab);
      this.setBlockTextureName("planks_oak");
      this.setHarvestLevel("axe", 0);
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean renderAsNormalBlock() {
      return false;
   }

   public int getRenderType() {
      return 22;
   }

   public void setBlockBoundsBasedOnState(IBlockAccess access, int par2, int par3, int par4) {
      this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
   }

   public Item getItemDropped(int par1, Random random, int par3) {
      return null;
   }

   public int damageDropped(int metadata) {
      return 0;
   }

   public boolean canSilkHarvest() {
      return true;
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
      if (player instanceof FakePlayer) {
         return false;
      } else {
         TileEntityGSHauntedChest te = (TileEntityGSHauntedChest)world.getTileEntity(x, y, z);
         if (te != null) {
            te.openChest();
         }

         return true;
      }
   }

   public TileEntity createNewTileEntity(World world, int var2) {
      return new TileEntityGSHauntedChest();
   }

   public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack stack) {
      int direction = MathHelper.floor_double((double)(entityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
      int metadata;
      switch(direction) {
      case 0:
         metadata = 2;
         break;
      case 1:
         metadata = 5;
         break;
      case 2:
         metadata = 3;
         break;
      case 3:
      default:
         metadata = 4;
      }

      world.setBlockMetadataWithNotify(x, y, z, metadata, 3);
      TileEntityGSHauntedChest tileEntity = (TileEntityGSHauntedChest)world.getTileEntity(x, y, z);
      if (tileEntity != null && stack.stackTagCompound != null) {
         tileEntity.setChestType(EnumHauntedChest.getById(stack.stackTagCompound.getByte("ChestType")));
      }

   }

   public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int metadata) {
   }

   public void onBlockHarvested(World world, int x, int y, int z, int metadata, EntityPlayer player) {
      player.addExhaustion(0.025F);
      ItemStack itemStack;
      if (EnchantmentHelper.getSilkTouchModifier(player)) {
         itemStack = this.getBlockItemStack(world, x, y, z);
      } else {
         itemStack = new ItemStack(Blocks.chest, 1, 0);
      }

      if (itemStack != null) {
         this.dropBlockAsItem(world, x, y, z, itemStack);
      }

   }

   public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
      ArrayList<ItemStack> ret = new ArrayList<>();
      ret.add(new ItemStack(Blocks.chest, 1, 0));
      return ret;
   }

   private ItemStack getBlockItemStack(World world, int x, int y, int z) {
      ItemStack itemStack = this.createStackedBlock(0);
      TileEntityGSHauntedChest tileEntity = (TileEntityGSHauntedChest)world.getTileEntity(x, y, z);
      if (tileEntity != null) {
         NBTTagCompound nbt = new NBTTagCompound();
         nbt.setByte("ChestType", (byte)tileEntity.getChestType().ordinal());
         itemStack.setTagCompound(nbt);
      }

      return itemStack;
   }

   public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
      return this.getBlockItemStack(world, x, y, z);
   }

   @SideOnly(Side.CLIENT)
   public void getSubBlocks(Item item, CreativeTabs tabs, List list) {
      for(byte i = 0; i < EnumHauntedChest.values().length; ++i) {
         ItemStack stack = new ItemStack(item, 1, 0);
         NBTTagCompound nbt = new NBTTagCompound();
         nbt.setByte("ChestType", i);
         stack.setTagCompound(nbt);
         list.add(stack);
      }

   }
}
