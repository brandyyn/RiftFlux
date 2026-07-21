package gravestone.structures;

import com.voidsrift.riftflux.chester.ChesterContent;
import gravestone.config.GraveStoneConfig;
import gravestone.block.BlockGSSpawner;
import gravestone.block.enums.EnumHauntedChest;
import gravestone.core.GSBlock;
import gravestone.tileentity.TileEntityGSHauntedChest;
import gravestone.structures.catacombs.CatacombsLevel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;

public class ObjectsGenerationHelper {
   private static final int[] POTIONS = new int[]{32764, 32692};
   private static final ThreadLocal<List<TileEntityChest>> GENERATED_CHEST_COLLECTOR = new ThreadLocal<List<TileEntityChest>>();
   private static final WeightedRandomChestContent[] NETHER_CHEST_CONTENT = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 5), new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 5), new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 15), new WeightedRandomChestContent(Items.golden_sword, 0, 1, 1, 5), new WeightedRandomChestContent(Items.golden_chestplate, 0, 1, 1, 5), new WeightedRandomChestContent(Items.flint_and_steel, 0, 1, 1, 5), new WeightedRandomChestContent(Items.nether_wart, 0, 3, 7, 5), new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 10), new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 8), new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 5), new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 3)};

   private ObjectsGenerationHelper() {
   }

   public static void generateChest(ComponentGraveStone component, World world, Random random, int xCoord, int yCoord, int zCoord, boolean defaultChest, ObjectsGenerationHelper.EnumChestTypes chestType) {
      if (chestType.equals(ObjectsGenerationHelper.EnumChestTypes.ALL_CHESTS) && random.nextInt(7) == 0) {
         generateHauntedChest(component, world, random, xCoord, yCoord, zCoord);
      } else {
         generateVanillaChest(component, world, random, xCoord, yCoord, zCoord, defaultChest, chestType);
      }

   }

   public static void generateVanillaChest(ComponentGraveStone component, World world, Random random, int xCoord, int yCoord, int zCoord, boolean defaultChest, ObjectsGenerationHelper.EnumChestTypes chestType) {
      component.getYWithOffset(yCoord);
      component.getXWithOffset(xCoord, zCoord);
      component.getZWithOffset(xCoord, zCoord);
      ChestGenHooks chest = getChest(random, chestType);
      WeightedRandomChestContent[] items;
      int count;
      if (chest == null) {
         items = NETHER_CHEST_CONTENT;
         count = 2 + random.nextInt(4);
      } else {
         items = chest.getItems(random);
         count = chest.getCount(random);
      }

      if (defaultChest) {
         component.generateStructureChestContents(world, component.getBoundingBox(), random, xCoord, yCoord, zCoord, items, count);
         TileEntity tileEntity = world.getTileEntity(component.getXWithOffset(xCoord, zCoord), component.getYWithOffset(yCoord), component.getZWithOffset(xCoord, zCoord));
         if (tileEntity instanceof TileEntityChest) {
            recordGeneratedChest((TileEntityChest)tileEntity);
         }
      } else {
         generateTrappedChestContents(component, world, random, xCoord, yCoord, zCoord, items, count);
      }

   }

   public static void generateHauntedChest(ComponentGraveStone component, World world, Random random, int xCoord, int yCoord, int zCoord) {
      int x = component.getXWithOffset(xCoord, zCoord);
      int y = component.getYWithOffset(yCoord);
      int z = component.getZWithOffset(xCoord, zCoord);
      world.setBlock(x, y, z, GSBlock.hauntedChest, 0, 2);
      TileEntityGSHauntedChest te = (TileEntityGSHauntedChest)world.getTileEntity(x, y, z);
      if (te != null) {
         te.setChestType(EnumHauntedChest.getById((byte)random.nextInt(EnumHauntedChest.values().length)));
      }

   }

   public static void generateTrappedChestContents(ComponentGraveStone component, World world, Random random, int xCoord, int yCoord, int zCoord, WeightedRandomChestContent[] chestContent, int count) {
      int x = component.getXWithOffset(xCoord, zCoord);
      int y = component.getYWithOffset(yCoord);
      int z = component.getZWithOffset(xCoord, zCoord);
      world.setBlock(x, y, z, Blocks.trapped_chest, 0, 2);
      TileEntityChest tileentitychest = (TileEntityChest)world.getTileEntity(x, y, z);
      if (tileentitychest != null) {
         WeightedRandomChestContent.generateChestContents(random, chestContent, tileentitychest, count);
         recordGeneratedChest(tileentitychest);
      }

   }

   public static void beginChestCollection() {
      GENERATED_CHEST_COLLECTOR.set(new ArrayList<TileEntityChest>());
   }

   public static List<TileEntityChest> endChestCollection() {
      List<TileEntityChest> chests = GENERATED_CHEST_COLLECTOR.get();
      GENERATED_CHEST_COLLECTOR.remove();
      return chests == null ? new ArrayList<TileEntityChest>() : chests;
   }

   private static void recordGeneratedChest(TileEntityChest chest) {
      List<TileEntityChest> collector = GENERATED_CHEST_COLLECTOR.get();
      if (collector != null && chest != null && !collector.contains(chest)) {
         collector.add(chest);
      }
   }

   public static TileEntityChest generateLowestLevelEyeboneChest(Random random, List<TileEntityChest> levelChests) {
      if (!GraveStoneConfig.generateEyeboneChestInLowestCatacombs || ChesterContent.eyeBone == null) {
         return null;
      }
      return addEyeboneToExistingChest(random, levelChests, null);
   }

   public static void generateRandomLevelEyeboneChest(World world, Random random, CatacombsLevel[] levels, TileEntityChest excludedChest) {
      if (!GraveStoneConfig.generateEyeboneChestInRandomCatacombsLayer || ChesterContent.eyeBone == null || levels == null || levels.length == 0) {
         return;
      }

      List<CatacombsLevel> levelOrder = new ArrayList<CatacombsLevel>();
      Collections.addAll(levelOrder, levels);
      Collections.shuffle(levelOrder, random);
      for (CatacombsLevel level : levelOrder) {
         if (addEyeboneToExistingChest(random, level.getGeneratedChests(), excludedChest) != null) {
            return;
         }
      }
   }

   private static TileEntityChest addEyeboneToExistingChest(Random random, List<TileEntityChest> levelChests, TileEntityChest excludedChest) {
      if (levelChests == null || levelChests.isEmpty()) {
         return null;
      }

      List<TileEntityChest> chests = new ArrayList<TileEntityChest>(levelChests);
      Collections.shuffle(chests, random);
      for (TileEntityChest chest : chests) {
         if (chest == excludedChest || chest.isInvalid()) {
            continue;
         }
         List<Integer> emptySlots = new ArrayList<Integer>();
         for (int slot = 0; slot < chest.getSizeInventory(); ++slot) {
            if (chest.getStackInSlot(slot) == null) {
               emptySlots.add(slot);
            }
         }
         if (!emptySlots.isEmpty()) {
            chest.setInventorySlotContents(emptySlots.get(random.nextInt(emptySlots.size())), new ItemStack(ChesterContent.eyeBone));
            chest.markDirty();
            return chest;
         }
      }
      return null;
   }

   private static ChestGenHooks getChest(Random random, ObjectsGenerationHelper.EnumChestTypes chestType) {
      switch(chestType) {
      case VALUABLE_CHESTS:
         switch(random.nextInt(7)) {
         case 0:
         default:
            return ChestGenHooks.getInfo("strongholdCorridor");
         case 1:
            return ChestGenHooks.getInfo("dungeonChest");
         case 2:
            return ChestGenHooks.getInfo("mineshaftCorridor");
         case 3:
            return ChestGenHooks.getInfo("pyramidDesertyChest");
         case 4:
            return ChestGenHooks.getInfo("strongholdCrossing");
         case 5:
            return ChestGenHooks.getInfo("strongholdLibrary");
         case 6:
            return null;
         }
      case ALL_CHESTS:
      default:
         switch(random.nextInt(9)) {
         case 0:
         default:
            return ChestGenHooks.getInfo("strongholdCorridor");
         case 1:
            return ChestGenHooks.getInfo("dungeonChest");
         case 2:
            return ChestGenHooks.getInfo("mineshaftCorridor");
         case 3:
            return ChestGenHooks.getInfo("pyramidDesertyChest");
         case 4:
            return ChestGenHooks.getInfo("pyramidJungleChest");
         case 5:
            return ChestGenHooks.getInfo("strongholdCrossing");
         case 6:
            return ChestGenHooks.getInfo("strongholdLibrary");
         case 7:
            return ChestGenHooks.getInfo("villageBlacksmith");
         case 8:
            return null;
         }
      }
   }

   public static void generateSpawner(ComponentGraveStone component, World world, Random random, int xCoord, int yCoord, int zCoord) {
      int y = component.getYWithOffset(yCoord);
      int x = component.getXWithOffset(xCoord, zCoord);
      int z = component.getZWithOffset(xCoord, zCoord);
      world.setBlock(x, y, z, GSBlock.spawner, BlockGSSpawner.MOB_SPAWNERS.get(random.nextInt(BlockGSSpawner.MOB_SPAWNERS.size())), 2);
   }

   public static void generateMinecraftSpawner(ComponentGraveStone component, World world, int xCoord, int yCoord, int zCoord, String mobNmae) {
      int y = component.getYWithOffset(yCoord);
      int x = component.getXWithOffset(xCoord, zCoord);
      int z = component.getZWithOffset(xCoord, zCoord);
      world.setBlock(x, y, z, Blocks.mob_spawner);
      TileEntityMobSpawner tileEntity = (TileEntityMobSpawner)world.getTileEntity(x, y, z);
      if (tileEntity != null) {
         tileEntity.func_145881_a().setEntityName(mobNmae);
      }

   }

   public static void generateDispenser(World world, ComponentGraveStone component, Random random, int xCoord, int yCoord, int zCoord, int direction) {
      int x = component.getXWithOffset(xCoord, zCoord);
      int y = component.getYWithOffset(yCoord);
      int z = component.getZWithOffset(xCoord, zCoord);
      world.setBlock(x, y, z, Blocks.dispenser);
      setDispenserMeta(world, x, y, z, direction);
      TileEntityDispenser dispenser = (TileEntityDispenser)world.getTileEntity(x, y, z);
      if (dispenser != null) {
         generateDispenserContents(random, dispenser);
      }

   }

   public static void generateDispenserContents(Random random, TileEntityDispenser dispenserEntity) {
      for(int i = 0; i < 9; ++i) {
         ItemStack stack = new ItemStack(Items.potionitem, getRandomCount(random), POTIONS[random.nextInt(POTIONS.length)]);
         dispenserEntity.setInventorySlotContents(i, stack);
      }

   }

   private static int getRandomCount(Random random) {
      return 5 + random.nextInt(5);
   }

   public static void setDispenserMeta(World world, int x, int y, int z, int direction) {
      switch(direction) {
      case 0:
         world.setBlockMetadataWithNotify(x, y, z, 2, 2);
         break;
      case 1:
         world.setBlockMetadataWithNotify(x, y, z, 5, 2);
         break;
      case 2:
         world.setBlockMetadataWithNotify(x, y, z, 3, 2);
         break;
      case 3:
         world.setBlockMetadataWithNotify(x, y, z, 4, 2);
      }

   }

   public static enum EnumChestTypes {
      ALL_CHESTS,
      VALUABLE_CHESTS;
   }
}
