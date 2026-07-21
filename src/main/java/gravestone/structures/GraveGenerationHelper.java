package gravestone.structures;

import com.voidsrift.riftflux.chester.ChesterContent;
import gravestone.block.GraveStoneHelper;
import gravestone.config.GraveStoneConfig;
import gravestone.core.GSBlock;
import gravestone.tileentity.TileEntityGSGraveStone;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GraveGenerationHelper {
   private static final ThreadLocal<List<TileEntityGSGraveStone>> GENERATED_GRAVE_COLLECTOR = new ThreadLocal<List<TileEntityGSGraveStone>>();

   private GraveGenerationHelper() {
   }

   public static void placeGrave(ComponentGraveStone component, World world, Random random, int x, int y, int z, int graveMeta, byte graveType, Item sword, boolean allLoot) {
      component.placeBlockAtCurrentPosition(world, GSBlock.graveStone, graveMeta, x, y, z, component.getBoundingBox());
      TileEntityGSGraveStone tileEntity = (TileEntityGSGraveStone)world.getTileEntity(component.getXWithOffset(x, z), component.getYWithOffset(y), component.getZWithOffset(x, z));
      if (tileEntity != null) {
         if (GraveStoneHelper.isSwordGrave(graveType)) {
            tileEntity.setSword(new ItemStack(sword));
         }

         tileEntity.setGraveType(graveType);
         tileEntity.setGraveContent(random, GraveStoneHelper.isPetGrave(graveType), allLoot);
         List<TileEntityGSGraveStone> collector = GENERATED_GRAVE_COLLECTOR.get();
         if (collector != null) {
            collector.add(tileEntity);
         }
      }

   }

   public static void fillGraves(ComponentGraveStone component, World world, Random random, int xStart, int yStart, int zStart, int xEnd, int yEnd, int zEnd, int graveMeta, byte graveType, Item sword, boolean allLoot) {
      for(int y = yStart; y <= yEnd; ++y) {
         for(int x = xStart; x <= xEnd; ++x) {
            for(int z = zStart; z <= zEnd; ++z) {
               placeGrave(component, world, random, x, y, z, graveMeta, graveType, sword, allLoot);
            }
         }
      }

   }

   public static void beginGraveCollection() {
      GENERATED_GRAVE_COLLECTOR.set(new ArrayList<TileEntityGSGraveStone>());
   }

   public static List<TileEntityGSGraveStone> endGraveCollection() {
      List<TileEntityGSGraveStone> graves = GENERATED_GRAVE_COLLECTOR.get();
      GENERATED_GRAVE_COLLECTOR.remove();
      return graves == null ? new ArrayList<TileEntityGSGraveStone>() : graves;
   }

   public static void addEyeboneLoot(List<TileEntityGSGraveStone> graves, Random random, boolean enabled) {
      if (!enabled || ChesterContent.eyeBone == null || graves == null || graves.isEmpty()) {
         return;
      }

      TileEntityGSGraveStone grave = graves.get(random.nextInt(graves.size()));
      grave.getInventory().addInventoryContent(new ItemStack(ChesterContent.eyeBone));
      grave.markDirty();
   }

   public static boolean canPlaceGrave(World world, int x, int minY, int z, int maxY) {
      for(int y = maxY; y >= minY - 1; --y) {
         Block block = world.getBlock(x, y, z);
         if (block != null) {
            if (block.equals(Blocks.water) || block.equals(Blocks.lava)) {
               return false;
            }

            if (GraveStoneHelper.canPlaceBlockAt(world, block, x, y, z)) {
               return true;
            }
         }
      }

      return false;
   }
}
