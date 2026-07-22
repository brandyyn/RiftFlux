package gravestone.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.voidsrift.riftflux.riftflux;
import com.voidsrift.riftflux.net.MsgGravestoneLocation;
import com.voidsrift.riftflux.net.RFNetwork;
import com.voidsrift.riftflux.vortex.item.ModItems;
import com.voidsrift.riftflux.vortex.lib.helper.EnchantHelper;
import gravestone.ModGraveStone;
import gravestone.block.enums.EnumGraves;
import gravestone.config.GraveStoneConfig;
import gravestone.core.GSTabs;
import gravestone.core.logger.GSLogger;
import gravestone.inventory.GraveInventory;
import gravestone.tileentity.DeathMessageInfo;
import gravestone.tileentity.TileEntityGSGraveStone;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.common.util.FakePlayer;

public class BlockGSGraveStone extends BlockContainer {
   public static final byte[] TAB_PLAYER_GRAVES = new byte[]{(byte)EnumGraves.WOODEN_VERTICAL_PLATE.ordinal(), (byte)EnumGraves.SANDSTONE_VERTICAL_PLATE.ordinal(), (byte)EnumGraves.STONE_VERTICAL_PLATE.ordinal(), (byte)EnumGraves.MOSSY_VERTICAL_PLATE.ordinal(), (byte)EnumGraves.IRON_VERTICAL_PLATE.ordinal(), (byte)EnumGraves.GOLDEN_VERTICAL_PLATE.ordinal(), (byte)EnumGraves.DIAMOND_VERTICAL_PLATE.ordinal(), (byte)EnumGraves.EMERALD_VERTICAL_PLATE.ordinal(), (byte)EnumGraves.LAPIS_VERTICAL_PLATE.ordinal(), (byte)EnumGraves.REDSTONE_VERTICAL_PLATE.ordinal(), (byte)EnumGraves.OBSIDIAN_VERTICAL_PLATE.ordinal(), (byte)EnumGraves.QUARTZ_VERTICAL_PLATE.ordinal(), (byte)EnumGraves.ICE_VERTICAL_PLATE.ordinal(), (byte)EnumGraves.WOODEN_CROSS.ordinal(), (byte)EnumGraves.SANDSTONE_CROSS.ordinal(), (byte)EnumGraves.STONE_CROSS.ordinal(), (byte)EnumGraves.MOSSY_CROSS.ordinal(), (byte)EnumGraves.IRON_CROSS.ordinal(), (byte)EnumGraves.GOLDEN_CROSS.ordinal(), (byte)EnumGraves.DIAMOND_CROSS.ordinal(), (byte)EnumGraves.EMERALD_CROSS.ordinal(), (byte)EnumGraves.LAPIS_CROSS.ordinal(), (byte)EnumGraves.REDSTONE_CROSS.ordinal(), (byte)EnumGraves.OBSIDIAN_CROSS.ordinal(), (byte)EnumGraves.QUARTZ_CROSS.ordinal(), (byte)EnumGraves.ICE_CROSS.ordinal(), (byte)EnumGraves.WOODEN_HORISONTAL_PLATE.ordinal(), (byte)EnumGraves.SANDSTONE_HORISONTAL_PLATE.ordinal(), (byte)EnumGraves.STONE_HORISONTAL_PLATE.ordinal(), (byte)EnumGraves.MOSSY_HORISONTAL_PLATE.ordinal(), (byte)EnumGraves.IRON_HORISONTAL_PLATE.ordinal(), (byte)EnumGraves.GOLDEN_HORISONTAL_PLATE.ordinal(), (byte)EnumGraves.DIAMOND_HORISONTAL_PLATE.ordinal(), (byte)EnumGraves.EMERALD_HORISONTAL_PLATE.ordinal(), (byte)EnumGraves.LAPIS_HORISONTAL_PLATE.ordinal(), (byte)EnumGraves.REDSTONE_HORISONTAL_PLATE.ordinal(), (byte)EnumGraves.OBSIDIAN_HORISONTAL_PLATE.ordinal(), (byte)EnumGraves.QUARTZ_HORISONTAL_PLATE.ordinal(), (byte)EnumGraves.ICE_HORISONTAL_PLATE.ordinal()};
   public static final byte[] TAB_PETS_GRAVES = new byte[]{(byte)EnumGraves.WOODEN_DOG_STATUE.ordinal(), (byte)EnumGraves.SANDSTONE_DOG_STATUE.ordinal(), (byte)EnumGraves.STONE_DOG_STATUE.ordinal(), (byte)EnumGraves.MOSSY_DOG_STATUE.ordinal(), (byte)EnumGraves.IRON_DOG_STATUE.ordinal(), (byte)EnumGraves.GOLDEN_DOG_STATUE.ordinal(), (byte)EnumGraves.DIAMOND_DOG_STATUE.ordinal(), (byte)EnumGraves.EMERALD_DOG_STATUE.ordinal(), (byte)EnumGraves.LAPIS_DOG_STATUE.ordinal(), (byte)EnumGraves.REDSTONE_DOG_STATUE.ordinal(), (byte)EnumGraves.OBSIDIAN_DOG_STATUE.ordinal(), (byte)EnumGraves.QUARTZ_DOG_STATUE.ordinal(), (byte)EnumGraves.ICE_DOG_STATUE.ordinal(), (byte)EnumGraves.WOODEN_CAT_STATUE.ordinal(), (byte)EnumGraves.SANDSTONE_CAT_STATUE.ordinal(), (byte)EnumGraves.STONE_CAT_STATUE.ordinal(), (byte)EnumGraves.MOSSY_CAT_STATUE.ordinal(), (byte)EnumGraves.IRON_CAT_STATUE.ordinal(), (byte)EnumGraves.GOLDEN_CAT_STATUE.ordinal(), (byte)EnumGraves.DIAMOND_CAT_STATUE.ordinal(), (byte)EnumGraves.EMERALD_CAT_STATUE.ordinal(), (byte)EnumGraves.LAPIS_CAT_STATUE.ordinal(), (byte)EnumGraves.REDSTONE_CAT_STATUE.ordinal(), (byte)EnumGraves.OBSIDIAN_CAT_STATUE.ordinal(), (byte)EnumGraves.QUARTZ_CAT_STATUE.ordinal(), (byte)EnumGraves.ICE_CAT_STATUE.ordinal(), (byte)EnumGraves.WOODEN_HORSE_STATUE.ordinal(), (byte)EnumGraves.SANDSTONE_HORSE_STATUE.ordinal(), (byte)EnumGraves.STONE_HORSE_STATUE.ordinal(), (byte)EnumGraves.MOSSY_HORSE_STATUE.ordinal(), (byte)EnumGraves.IRON_HORSE_STATUE.ordinal(), (byte)EnumGraves.GOLDEN_HORSE_STATUE.ordinal(), (byte)EnumGraves.DIAMOND_HORSE_STATUE.ordinal(), (byte)EnumGraves.EMERALD_HORSE_STATUE.ordinal(), (byte)EnumGraves.LAPIS_HORSE_STATUE.ordinal(), (byte)EnumGraves.REDSTONE_HORSE_STATUE.ordinal(), (byte)EnumGraves.OBSIDIAN_HORSE_STATUE.ordinal(), (byte)EnumGraves.QUARTZ_HORSE_STATUE.ordinal(), (byte)EnumGraves.ICE_HORSE_STATUE.ordinal()};
   private static final Random rand = new Random();
   private static final ThreadLocal<Boolean> PLAYER_HARDNESS_QUERY = new ThreadLocal<>();

   public BlockGSGraveStone() {
      super(Material.rock);
      this.isBlockContainer = true;
      this.setStepSound(Block.soundTypeStone);
      this.setBlockName("GraveStone");
      this.setHardness(0.5F);
      this.setResistance(5.0F);
      this.setCreativeTab(GSTabs.gravesTab);
      this.setTickRandomly(GraveStoneConfig.removeEmptyGraves);
      this.setBlockTextureName("stone");
   }

   public float getBlockHardness(World world, int x, int y, int z) {
      if (GraveStoneConfig.onlyPlayersCanBreakGraves && !Boolean.TRUE.equals(PLAYER_HARDNESS_QUERY.get())) {
         return -1.0F;
      }
      return super.getBlockHardness(world, x, y, z);
   }

   public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z) {
      if (!this.canPlayerBreakGrave(world, player, x, y, z)) {
         return 0.0F;
      }
      PLAYER_HARDNESS_QUERY.set(Boolean.TRUE);
      try {
         return super.getPlayerRelativeBlockHardness(player, world, x, y, z);
      } finally {
         PLAYER_HARDNESS_QUERY.remove();
      }
   }

   @Override
   public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
      return this.removeByAuthorizedPlayer(world, player, x, y, z);
   }

   @Override
   public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
      return this.removeByAuthorizedPlayer(world, player, x, y, z);
   }

   private boolean removeByAuthorizedPlayer(World world, EntityPlayer player, int x, int y, int z) {
      if (!this.canPlayerBreakGrave(world, player, x, y, z)) {
         return false;
      }
      GravestoneRemovalGuard.beginAuthorizedRemoval();
      try {
         return super.removedByPlayer(world, player, x, y, z);
      } finally {
         GravestoneRemovalGuard.endAuthorizedRemoval();
      }
   }

   private boolean canPlayerBreakGrave(World world, EntityPlayer player, int x, int y, int z) {
      if (player == null || GraveStoneConfig.onlyPlayersCanBreakGraves && player instanceof FakePlayer) {
         return false;
      }
      if (!GraveStoneConfig.onlyOwnerCanBreakGraves) {
         return true;
      }
      TileEntityGSGraveStone grave = (TileEntityGSGraveStone)world.getTileEntity(x, y, z);
      return grave == null || grave.isOwner(player);
   }

   public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
      GraveStoneHelper.replaceGround(world, x, y - 1, z);
      int direction = MathHelper.floor_float(player.rotationYaw);
      if (direction < 0) {
         direction += 360;
      }

      int metadata = GraveStoneHelper.getMetadataBasedOnRotation(direction);
      world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
      TileEntityGSGraveStone tileEntity = (TileEntityGSGraveStone)world.getTileEntity(x, y, z);
      if (tileEntity != null) {
         tileEntity.setPlayerPlaced(true);
      }
      if (tileEntity != null && itemStack.stackTagCompound != null) {
         if (itemStack.stackTagCompound.hasKey("GraveType")) {
            tileEntity.setGraveType(itemStack.stackTagCompound.getByte("GraveType"));
         } else {
            tileEntity.setGraveType((byte)0);
         }

         if (itemStack.stackTagCompound.hasKey("isLocalized") && itemStack.stackTagCompound.getBoolean("isLocalized")) {
            tileEntity.getDeathTextComponent().setLocalized();
            if (itemStack.stackTagCompound.hasKey("name") && itemStack.stackTagCompound.hasKey("KillerName")) {
               tileEntity.getDeathTextComponent().setName(itemStack.stackTagCompound.getString("name"));
               tileEntity.getDeathTextComponent().setKillerName(itemStack.stackTagCompound.getString("KillerName"));
            }
         }

         if (itemStack.stackTagCompound.hasKey("DeathText")) {
            tileEntity.getDeathTextComponent().setDeathText(itemStack.stackTagCompound.getString("DeathText"));
         }

         if (itemStack.stackTagCompound.hasKey("Age")) {
            tileEntity.setAge(itemStack.stackTagCompound.getInteger("Age"));
         }

         if (itemStack.stackTagCompound.hasKey("Sword")) {
            tileEntity.setSword(GraveStoneHelper.loadSwordInfo(itemStack.getTagCompound()));
         }

         if (itemStack.stackTagCompound.hasKey("Enchanted")) {
            tileEntity.setEnchanted(itemStack.stackTagCompound.getBoolean("Enchanted"));
         }

         if (itemStack.stackTagCompound.hasKey(EnchantHelper.CUSTOM_GLINT_TAG, 3)) {
            tileEntity.setCustomGlint(itemStack.stackTagCompound.getInteger(EnchantHelper.CUSTOM_GLINT_TAG));
         }

         if (itemStack.stackTagCompound.hasKey("GraveOwnerName") || itemStack.stackTagCompound.hasKey("GraveOwnerUUID")) {
            tileEntity.setOwner(itemStack.stackTagCompound.getString("GraveOwnerName"), itemStack.stackTagCompound.getString("GraveOwnerUUID"));
         }
      }

   }

   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      return GraveStoneHelper.canPlaceBlockAt(world, x, y - 1, z);
   }

   public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
      return null;
   }

   public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z) {
      int meta = access.getBlockMetadata(x, y, z);
      TileEntityGSGraveStone tileEntity = (TileEntityGSGraveStone)access.getTileEntity(x, y, z);
      EnumGraves graveType;
      if (tileEntity != null) {
         graveType = tileEntity.getGraveType();
      } else {
         graveType = EnumGraves.STONE_VERTICAL_PLATE;
      }

      switch(graveType) {
      case WOODEN_VERTICAL_PLATE:
      case SANDSTONE_VERTICAL_PLATE:
      case STONE_VERTICAL_PLATE:
      case MOSSY_VERTICAL_PLATE:
      case IRON_VERTICAL_PLATE:
      case GOLDEN_VERTICAL_PLATE:
      case DIAMOND_VERTICAL_PLATE:
      case EMERALD_VERTICAL_PLATE:
      case LAPIS_VERTICAL_PLATE:
      case REDSTONE_VERTICAL_PLATE:
      case OBSIDIAN_VERTICAL_PLATE:
      case QUARTZ_VERTICAL_PLATE:
      case ICE_VERTICAL_PLATE:
         switch(meta) {
         case 0:
            this.setBlockBounds(0.125F, 0.0F, 0.0625F, 0.875F, 0.9375F, 0.1875F);
            return;
         case 1:
            this.setBlockBounds(0.125F, 0.0F, 0.8125F, 0.875F, 0.9375F, 0.9375F);
            return;
         case 2:
            this.setBlockBounds(0.0625F, 0.0F, 0.125F, 0.1875F, 0.9375F, 0.875F);
            return;
         case 3:
            this.setBlockBounds(0.8125F, 0.0F, 0.125F, 0.9375F, 0.9375F, 0.875F);
            return;
         default:
            return;
         }
      case WOODEN_CROSS:
      case SANDSTONE_CROSS:
      case STONE_CROSS:
      case MOSSY_CROSS:
      case IRON_CROSS:
      case GOLDEN_CROSS:
      case DIAMOND_CROSS:
      case EMERALD_CROSS:
      case LAPIS_CROSS:
      case REDSTONE_CROSS:
      case OBSIDIAN_CROSS:
      case QUARTZ_CROSS:
      case ICE_CROSS:
         switch(meta) {
         case 0:
            this.setBlockBounds(0.125F, 0.0F, 0.0625F, 0.875F, 1.0F, 0.1875F);
            return;
         case 1:
            this.setBlockBounds(0.125F, 0.0F, 0.8125F, 0.875F, 1.0F, 0.9375F);
            return;
         case 2:
            this.setBlockBounds(0.0625F, 0.0F, 0.125F, 0.1875F, 1.0F, 0.875F);
            return;
         case 3:
            this.setBlockBounds(0.8125F, 0.0F, 0.125F, 0.9375F, 1.0F, 0.875F);
            return;
         default:
            return;
         }
      case WOODEN_HORISONTAL_PLATE:
      case SANDSTONE_HORISONTAL_PLATE:
      case STONE_HORISONTAL_PLATE:
      case MOSSY_HORISONTAL_PLATE:
      case IRON_HORISONTAL_PLATE:
      case GOLDEN_HORISONTAL_PLATE:
      case DIAMOND_HORISONTAL_PLATE:
      case EMERALD_HORISONTAL_PLATE:
      case LAPIS_HORISONTAL_PLATE:
      case REDSTONE_HORISONTAL_PLATE:
      case OBSIDIAN_HORISONTAL_PLATE:
      case QUARTZ_HORISONTAL_PLATE:
      case ICE_HORISONTAL_PLATE:
         switch(meta) {
         case 0:
            this.setBlockBounds(0.09375F, 0.0F, 0.0625F, 0.90625F, 0.0625F, 0.9375F);
            return;
         case 1:
            this.setBlockBounds(0.09375F, 0.0F, 0.0625F, 0.90625F, 0.0625F, 0.9375F);
            return;
         case 2:
            this.setBlockBounds(0.0625F, 0.0F, 0.09375F, 0.9375F, 0.0625F, 0.90625F);
            return;
         case 3:
            this.setBlockBounds(0.0625F, 0.0F, 0.09375F, 0.9375F, 0.0625F, 0.90625F);
            return;
         default:
            return;
         }
      case WOODEN_DOG_STATUE:
      case SANDSTONE_DOG_STATUE:
      case STONE_DOG_STATUE:
      case MOSSY_DOG_STATUE:
      case IRON_DOG_STATUE:
      case GOLDEN_DOG_STATUE:
      case DIAMOND_DOG_STATUE:
      case EMERALD_DOG_STATUE:
      case LAPIS_DOG_STATUE:
      case REDSTONE_DOG_STATUE:
      case OBSIDIAN_DOG_STATUE:
      case QUARTZ_DOG_STATUE:
      case ICE_DOG_STATUE:
         switch(meta) {
         case 0:
            this.setBlockBounds(0.35F, 0.0F, 0.3F, 0.6F, 0.5F, 0.9F);
            return;
         case 1:
            this.setBlockBounds(0.4F, 0.0F, 0.1F, 0.65F, 0.5F, 0.7F);
            return;
         case 2:
            this.setBlockBounds(0.3F, 0.0F, 0.4F, 0.9F, 0.5F, 0.65F);
            return;
         case 3:
            this.setBlockBounds(0.1F, 0.0F, 0.35F, 0.7F, 0.5F, 0.6F);
            return;
         default:
            return;
         }
      case WOODEN_CAT_STATUE:
      case SANDSTONE_CAT_STATUE:
      case STONE_CAT_STATUE:
      case MOSSY_CAT_STATUE:
      case IRON_CAT_STATUE:
      case GOLDEN_CAT_STATUE:
      case DIAMOND_CAT_STATUE:
      case EMERALD_CAT_STATUE:
      case LAPIS_CAT_STATUE:
      case REDSTONE_CAT_STATUE:
      case OBSIDIAN_CAT_STATUE:
      case QUARTZ_CAT_STATUE:
      case ICE_CAT_STATUE:
         switch(meta) {
         case 0:
            this.setBlockBounds(0.43F, 0.0F, 0.3F, 0.57F, 0.5F, 0.75F);
            return;
         case 1:
            this.setBlockBounds(0.43F, 0.0F, 0.7F, 0.57F, 0.5F, 0.25F);
            return;
         case 2:
            this.setBlockBounds(0.3F, 0.0F, 0.43F, 0.75F, 0.5F, 0.57F);
            return;
         case 3:
            this.setBlockBounds(0.7F, 0.0F, 0.43F, 0.25F, 0.5F, 0.57F);
            return;
         default:
            return;
         }
      case SWORD:
         switch(meta) {
         case 0:
         case 1:
            this.setBlockBounds(0.375F, 0.0F, 0.4375F, 0.625F, 0.9F, 0.5625F);
            return;
         case 2:
         case 3:
            this.setBlockBounds(0.4375F, 0.0F, 0.375F, 0.5625F, 0.9F, 0.625F);
            return;
         default:
            return;
         }
      case WOODEN_HORSE_STATUE:
      case SANDSTONE_HORSE_STATUE:
      case STONE_HORSE_STATUE:
      case MOSSY_HORSE_STATUE:
      case IRON_HORSE_STATUE:
      case GOLDEN_HORSE_STATUE:
      case DIAMOND_HORSE_STATUE:
      case EMERALD_HORSE_STATUE:
      case LAPIS_HORSE_STATUE:
      case REDSTONE_HORSE_STATUE:
      case OBSIDIAN_HORSE_STATUE:
      case QUARTZ_HORSE_STATUE:
      case ICE_HORSE_STATUE:
         switch(meta) {
         case 0:
         case 1:
            this.setBlockBounds(0.375F, 0.0F, 0.275F, 0.625F, 0.85F, 0.725F);
            break;
         case 2:
         case 3:
            this.setBlockBounds(0.275F, 0.0F, 0.375F, 0.725F, 0.85F, 0.625F);
         }
      }

   }

   public void onBlockHarvested(World world, int x, int y, int z, int metadata, EntityPlayer player) {
      if (!this.canPlayerBreakGrave(world, player, x, y, z)) {
         return;
      }
      player.addExhaustion(0.025F);
      GraveStoneHelper.spawnMob(world, x, y, z);
      TileEntityGSGraveStone tileEntity = (TileEntityGSGraveStone)world.getTileEntity(x, y, z);
      if (tileEntity != null) {
         if (tileEntity.hasFlower()) {
            tileEntity.dropFlower();
         }

         if (EnchantmentHelper.getSilkTouchModifier(player)) {
            this.dropBlock(world, x, y, z);
         } else {
            this.dropBlockWithoutInfo(world, x, y, z);
         }
      }

   }

   public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
      ArrayList<ItemStack> ret = new ArrayList<>();
      ret.add(this.getBlockItemStack(world, x, y, z));
      return ret;
   }

   public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int metadata) {
   }

   public boolean renderAsNormalBlock() {
      return false;
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public int getRenderType() {
      return GraveStoneConfig.graveRenderID;
   }

   public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta) {
      GraveStoneHelper.spawnMob(world, x, y, z);
   }

   public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
      return this.getExplosionResistance(par1Entity);
   }

   public float getExplosionResistance(Entity par1Entity) {
      return GraveStoneConfig.protectGravesFromExplosions ? 1.8E7F : super.getExplosionResistance(par1Entity);
   }

   public void onBlockExploded(World world, int x, int y, int z, Explosion explosion) {
      if (!GraveStoneConfig.protectGravesFromExplosions) {
         GravestoneRemovalGuard.beginAuthorizedRemoval();
         try {
            super.onBlockExploded(world, x, y, z, explosion);
         } finally {
            GravestoneRemovalGuard.endAuthorizedRemoval();
         }
      }
   }

   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
      TileEntityGSGraveStone te = (TileEntityGSGraveStone)world.getTileEntity(x, y, z);
      if (te != null) {
         if (player.inventory.getCurrentItem() != null) {
            ItemStack item = player.inventory.getCurrentItem();
            if (item.getItem() == ModItems.glintRune) {
               if (!world.isRemote) {
                  te.setCustomGlint(MathHelper.clamp_int(item.getItemDamage(), 0, 16));
                  world.markBlockForUpdate(x, y, z);
                  if (!player.capabilities.isCreativeMode) {
                     --item.stackSize;
                     if (item.stackSize <= 0) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
                     }
                     player.inventory.markDirty();
                  }
               }
               return true;
            }

            if (item.getItem() instanceof ItemSpade) {
               if (!world.isRemote) {
                  GSLogger.logInfoGrave(player.getCommandSenderName() + " loot grave at " + x + "/" + y + "/" + z);
                  player.openGui(riftflux.instance, 0, world, x, y, z);
               }

               return false;
            }

            if (te.hasFlower()) {
               if (item.getItem() instanceof ItemShears) {
                  if (!world.isRemote) {
                     te.dropFlower();
                  }

                  te.setFlower((ItemStack)null);
                  return false;
               }
            } else if (GraveStoneHelper.canFlowerBePlaced(world, x, y, z, item, te)) {
               ItemStack graveFlower = item.copy();
               graveFlower.stackSize = 1;
               te.setFlower(graveFlower);
               --player.inventory.getCurrentItem().stackSize;
               return true;
            }
         }

         if (world.isRemote) {
            String deathText = te.getDeathTextComponent().getDeathText();
            if (deathText.length() != 0) {
               if (te.getDeathTextComponent().isLocalized()) {
                  String name = ModGraveStone.proxy.getLocalizedEntityName(te.getDeathTextComponent().getName());
                  String killerName = ModGraveStone.proxy.getLocalizedEntityName(te.getDeathTextComponent().getKillerName());
                  if (killerName.length() == 0) {
                     player.addChatComponentMessage(new ChatComponentTranslation(deathText, name));
                  } else {
                     player.addChatComponentMessage(new ChatComponentTranslation(deathText, name, killerName));
                  }
               } else {
                  player.addChatComponentMessage(new ChatComponentTranslation(deathText));
               }

               if (te.getAge() > 0) {
                  StringBuilder ageStr = new StringBuilder();
                  ageStr.append(ModGraveStone.proxy.getLocalizedString("item.grave.age")).append(" ").append(te.getAge()).append(" ").append(ModGraveStone.proxy.getLocalizedString("item.grave.days"));
                  player.addChatComponentMessage(new ChatComponentTranslation(ageStr.toString()));
               }
            }
         }
      }

      return false;
   }

   public TileEntity createNewTileEntity(World world, int var2) {
      return new TileEntityGSGraveStone(world);
   }

   public void onBlockAdded(World world, int x, int y, int z) {
      super.onBlockAdded(world, x, y, z);
      GraveStoneHelper.replaceGround(world, x, y - 1, z);
   }

   public void breakBlock(World world, int x, int y, int z, Block block, int par6) {
      TileEntityGSGraveStone tileEntity = (TileEntityGSGraveStone)world.getTileEntity(x, y, z);
      if (tileEntity != null) {
         tileEntity.dropAllItems();
      }

      super.breakBlock(world, x, y, z, block, par6);
   }

   public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
      if (!world.isSideSolid(x, y - 1, z, ForgeDirection.DOWN, true)) {
         if (GraveStoneConfig.onlyPlayersCanBreakGraves || GraveStoneConfig.protectGravesFromExplosions) {
            return;
         }
         this.dropBlockWithoutInfo(world, x, y, z);
         GravestoneRemovalGuard.beginAuthorizedRemoval();
         try {
            world.setBlock(x, y, z, Blocks.air, 0, 2);
         } finally {
            GravestoneRemovalGuard.endAuthorizedRemoval();
         }
      }

   }

   public int damageDropped(int metadata) {
      return 0;
   }

   @SideOnly(Side.CLIENT)
   public void getSubBlocks(Item gravestone, CreativeTabs tabs, List list) {
      for(byte i = 0; i < TAB_PLAYER_GRAVES.length; ++i) {
         ItemStack stack = new ItemStack(gravestone, 1, 0);
         NBTTagCompound nbt = new NBTTagCompound();
         nbt.setByte("GraveType", TAB_PLAYER_GRAVES[i]);
         stack.setTagCompound(nbt);
         list.add(stack);
      }

      for(byte i = 0; i < TAB_PETS_GRAVES.length; ++i) {
         ItemStack stack = new ItemStack(gravestone, 1, 0);
         NBTTagCompound nbt = new NBTTagCompound();
         nbt.setByte("GraveType", TAB_PETS_GRAVES[i]);
         stack.setTagCompound(nbt);
         list.add(stack);
      }

      for(Item sword : GraveStoneHelper.swordsList) {
         list.add(GraveStoneHelper.getSwordAsGrave(gravestone, new ItemStack(sword, 1)));
      }

      for(Item sword : GraveStoneHelper.swordsList) {
         try {
            ItemStack swordStack = new ItemStack(sword, 1);
            EnchantmentHelper.addRandomEnchantment(new Random(), swordStack, 5);
            ItemStack graveStoneStack = GraveStoneHelper.getSwordAsGrave(gravestone, swordStack);
            list.add(graveStoneStack);
         } catch (IllegalArgumentException var8) {
            GSLogger.logError("Can't create enchanted sword gravestone");
            var8.printStackTrace();
         }
      }

   }

   private void dropBlock(World world, int x, int y, int z) {
      ItemStack itemStack = this.getBlockItemStack(world, x, y, z);
      if (itemStack != null) {
         this.dropBlockAsItem(world, x, y, z, itemStack);
      }

   }

   private void dropBlockWithoutInfo(World world, int x, int y, int z) {
      ItemStack itemStack = this.createStackedBlock(0);
      TileEntityGSGraveStone tileEntity = (TileEntityGSGraveStone)world.getTileEntity(x, y, z);
      if (tileEntity != null) {
         if (tileEntity.isSwordGrave()) {
            tileEntity.dropSword();
         } else if (itemStack != null) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setByte("GraveType", tileEntity.getGraveTypeNum());
            itemStack.setTagCompound(nbt);
            this.dropBlockAsItem(world, x, y, z, itemStack);
         }
      }

   }

   private ItemStack getBlockItemStack(World world, int x, int y, int z) {
      ItemStack itemStack = this.createStackedBlock(0);
      TileEntityGSGraveStone tileEntity = (TileEntityGSGraveStone)world.getTileEntity(x, y, z);
      if (tileEntity != null) {
         NBTTagCompound nbt = new NBTTagCompound();
         nbt.setByte("GraveType", tileEntity.getGraveTypeNum());
         if (tileEntity.getDeathTextComponent().isLocalized()) {
            nbt.setBoolean("isLocalized", true);
            nbt.setString("name", tileEntity.getDeathTextComponent().getName());
            nbt.setString("KillerName", tileEntity.getDeathTextComponent().getKillerName());
         }

         nbt.setString("DeathText", tileEntity.getDeathTextComponent().getDeathText());
         nbt.setInteger("Age", tileEntity.getAge());
         if (tileEntity.isSwordGrave()) {
            GraveStoneHelper.addSwordInfo(nbt, tileEntity.getSword());
         }

         nbt.setBoolean("Enchanted", tileEntity.isEnchanted());
         if (tileEntity.hasCustomGlint()) {
            nbt.setInteger(EnchantHelper.CUSTOM_GLINT_TAG, tileEntity.getCustomGlint());
         }
         if (tileEntity.hasOwner()) {
            nbt.setString("GraveOwnerName", tileEntity.getOwnerName());
            nbt.setString("GraveOwnerUUID", tileEntity.getOwnerUuid());
         }
         itemStack.setTagCompound(nbt);
      }

      return itemStack;
   }

   public void createOnDeath(Entity entity, World world, int x, int y, int z, DeathMessageInfo deathInfo, int direction, List<ItemStack> items, int age, BlockGSGraveStone.EnumGraveType entityType, DamageSource damageSource) {
      if (direction < 0) {
         direction += 360;
      }

      byte graveType = 0;
      ItemStack sword = null;
      if (GraveStoneConfig.generateSwordGraves && world.rand.nextInt(4) == 0 && entityType.equals(BlockGSGraveStone.EnumGraveType.PLAYER_GRAVES)) {
         sword = GraveStoneHelper.oldCheckSword(items);
      }

      switch(entityType) {
      case PLAYER_GRAVES:
         graveType = GraveStoneHelper.getRandomGrave(GraveStoneHelper.getPlayerGraveForLevel(entity), rand);
         if (graveType == 0) {
            if (sword == null) {
               graveType = GraveStoneHelper.getRandomGrave(GraveStoneHelper.getPlayerGraveForDeath(damageSource, damageSource.damageType), rand);
               if (graveType == 0) {
                  graveType = GraveStoneHelper.getRandomGrave(GraveStoneHelper.getPlayerGraveTypes(world, x, z), rand);
               }
            } else {
               graveType = (byte)EnumGraves.SWORD.ordinal();
            }
         }
         break;
      case DOGS_GRAVES:
         graveType = GraveStoneHelper.getRandomGrave(GraveStoneHelper.getDogGraveForDeath(damageSource, damageSource.damageType), rand);
         if (graveType == 0) {
            graveType = GraveStoneHelper.getRandomGrave(GraveStoneHelper.getDogGraveTypes(world, x, z), rand);
         }
         break;
      case CATS_GRAVES:
         graveType = GraveStoneHelper.getRandomGrave(GraveStoneHelper.getCatGraveForDeath(damageSource, damageSource.damageType), rand);
         if (graveType == 0) {
            graveType = GraveStoneHelper.getRandomGrave(GraveStoneHelper.getCatGraveTypes(world, x, z), rand);
         }
         break;
      case HORSE_GRAVES:
         graveType = GraveStoneHelper.getRandomGrave(GraveStoneHelper.getHorseGraveForDeath(damageSource, damageSource.damageType), rand);
         if (graveType == 0) {
            graveType = GraveStoneHelper.getRandomGrave(GraveStoneHelper.getHorseGraveTypes(world, x, z), rand);
         }
      }

      boolean isMagic = GraveStoneHelper.isMagicDamage(damageSource, damageSource.damageType);
      int[] graveCoordinates = GraveStoneHelper.findPlaceForGrave(world, x, y, z);
      if (graveCoordinates != null) {
         x = graveCoordinates[0];
         y = graveCoordinates[1];
         z = graveCoordinates[2];
         boolean blockPlaced = world.setBlock(x, y, z, this, GraveStoneHelper.getMetadataBasedOnRotation(direction), 2);
         TileEntityGSGraveStone tileEntity = blockPlaced ? (TileEntityGSGraveStone)world.getTileEntity(x, y, z) : null;
         if (tileEntity != null) {
            if (sword != null) {
               tileEntity.setSword(sword);
            }

            tileEntity.getDeathTextComponent().setLocalized();
            tileEntity.getDeathTextComponent().setName(deathInfo.getName());
            tileEntity.getDeathTextComponent().setDeathText(deathInfo.getDeathMessage());
            tileEntity.getDeathTextComponent().setKillerName(deathInfo.getKillerName());
            tileEntity.setItems(items);
            tileEntity.setGraveType(graveType);
            tileEntity.setAge(age);
            tileEntity.setEnchanted(isMagic);
            if (entity instanceof EntityPlayer) {
               tileEntity.setOwner((EntityPlayer)entity);
            }
            if (entity instanceof EntityPlayerMP && RFNetwork.CH != null) {
               RFNetwork.CH.sendTo(new MsgGravestoneLocation(x, y, z, world.provider.dimensionId), (EntityPlayerMP)entity);
            }
            GSLogger.logInfoGrave("Create " + deathInfo.getName() + "'s grave at " + x + "x" + y + "x" + z);
         } else {
            if (blockPlaced) {
               GravestoneRemovalGuard.beginAuthorizedRemoval();
               try {
                  world.setBlockToAir(x, y, z);
               } finally {
                  GravestoneRemovalGuard.endAuthorizedRemoval();
               }
            }
            graveCoordinates = null;
         }
      }

      if (graveCoordinates == null) {
         ItemStack itemStack = this.createStackedBlock(0);
         NBTTagCompound nbt = new NBTTagCompound();
         nbt.setByte("GraveType", graveType);
         nbt.setBoolean("isLocalized", true);
         nbt.setString("name", deathInfo.getName());
         nbt.setString("DeathText", deathInfo.getDeathMessage());
         nbt.setString("KillerName", deathInfo.getKillerNameForTE());
         nbt.setBoolean("Enchanted", isMagic);
      nbt.setInteger("Age", age);
      if (entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         nbt.setString("GraveOwnerName", player.getCommandSenderName());
         nbt.setString("GraveOwnerUUID", player.getUniqueID().toString());
      }
         if (graveType == EnumGraves.SWORD.ordinal() && sword != null) {
            GraveStoneHelper.addSwordInfo(nbt, sword);
         }

         itemStack.setTagCompound(nbt);
         int safeDropY = MathHelper.clamp_int(y, 1, 255);
         this.dropBlockAsItem(world, x, safeDropY, z, itemStack);
         if (items != null) {
            for(int i = 0; i < items.size(); ++i) {
               if (items.get(i) != null) {
                  GraveInventory.dropItem(items.get(i), world, x, safeDropY, z);
               }
            }
         }

         GSLogger.logInfoGrave("Can not create " + deathInfo.getName() + "'s grave at " + x + "x" + y + "x" + z);
      }

   }

   public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
      ItemStack itemStack = this.createStackedBlock(0);
      TileEntityGSGraveStone tileEntity = (TileEntityGSGraveStone)world.getTileEntity(x, y, z);
      if (tileEntity != null && itemStack != null) {
         NBTTagCompound nbt = new NBTTagCompound();
         nbt.setByte("GraveType", tileEntity.getGraveTypeNum());
         itemStack.setTagCompound(nbt);
         if (tileEntity.isSwordGrave()) {
            GraveStoneHelper.addSwordInfo(nbt, tileEntity.getSword());
         }
      }

      return itemStack;
   }

   public void updateTick(World world, int x, int y, int z, Random random) {
      if (GraveStoneConfig.removeEmptyGraves && !world.isRemote) {
         TileEntityGSGraveStone tileEntity = (TileEntityGSGraveStone)world.getTileEntity(x, y, z);
         if (tileEntity != null && !tileEntity.isSwordGrave() && tileEntity.isEmpty()) {
            if (GraveStoneConfig.showGravesRemovingMessages) {
               GSLogger.logInfoGrave("Remove empty grave at " + x + "/" + y + "/" + z);
            }

            world.removeTileEntity(x, y, z);
            GravestoneRemovalGuard.beginAuthorizedRemoval();
            try {
               world.setBlock(x, y, z, Blocks.air, 0, 2);
            } finally {
               GravestoneRemovalGuard.endAuthorizedRemoval();
            }
         }
      }

   }

   public static enum EnumGraveType {
      ALL_GRAVES,
      PLAYER_GRAVES,
      PETS_GRAVES,
      DOGS_GRAVES,
      CATS_GRAVES,
      HORSE_GRAVES;
   }
}
