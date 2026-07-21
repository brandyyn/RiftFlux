package gravestone.structures.catacombs;

import gravestone.config.GraveStoneConfig;
import gravestone.core.GSBlock;
import gravestone.structures.catacombs.components.CatacombsBaseComponent;
import gravestone.structures.catacombs.components.Stairs;
import gravestone.structures.catacombs.components.Treasury;
import gravestone.structures.catacombs.components.WitherHall;
import gravestone.structures.ObjectsGenerationHelper;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.gen.structure.StructureComponent.BlockSelector;

public class CatacombsLevel {
   private static final BlockSelector catacombsStoneBlocks = new CatacombsStonesBlocks();
   private static final BlockSelector catacombsBoneBlocks = new CatacombsBoneBlocks();
   private final int level;
   private Random random;
   private World world;
   private int totalComponentsCount;
   private int componentsCount;
   public static final int DEFAULT_MIN_ROOMS_COUNT_AT_1_LEVEL = 30;
   public static final int DEFAULT_MAX_ROOMS_COUNT_AT_1_LEVEL = 60;
   public static final int DEFAULT_MIN_ROOMS_COUNT_AT_2_LEVEL = 60;
   public static final int DEFAULT_MAX_ROOMS_COUNT_AT_2_LEVEL = 120;
   public static final int DEFAULT_MIN_ROOMS_COUNT_AT_3_LEVEL = 90;
   public static final int DEFAULT_MAX_ROOMS_COUNT_AT_3_LEVEL = 180;
   public static final int DEFAULT_MIN_ROOMS_COUNT_AT_4_LEVEL = 160;
   public static final int DEFAULT_MAX_ROOMS_COUNT_AT_4_LEVEL = 320;
   private LinkedList<CatacombsBaseComponent> levelComponents = new LinkedList<>();
   private LinkedList<CatacombsBaseComponent> endComponents = new LinkedList<>();
   private List<TileEntityChest> generatedChests;

   public CatacombsLevel(LinkedList<CatacombsBaseComponent> startComponents, int level, World world, Random random) {
      this.levelComponents = startComponents;
      this.random = random;
      this.world = world;
      this.level = level;
      switch(level) {
      case 1:
         this.totalComponentsCount = GraveStoneConfig.catacombsMinRoomsCountAt1Level + random.nextInt(GraveStoneConfig.catacombsMaxRoomsCountAt1Level - GraveStoneConfig.catacombsMinRoomsCountAt1Level);
         break;
      case 2:
         this.totalComponentsCount = GraveStoneConfig.catacombsMinRoomsCountAt2Level + random.nextInt(GraveStoneConfig.catacombsMaxRoomsCountAt2Level - GraveStoneConfig.catacombsMinRoomsCountAt2Level);
         break;
      case 3:
         this.totalComponentsCount = GraveStoneConfig.catacombsMinRoomsCountAt3Level + random.nextInt(GraveStoneConfig.catacombsMaxRoomsCountAt3Level - GraveStoneConfig.catacombsMinRoomsCountAt3Level);
         break;
      case 4:
         this.totalComponentsCount = GraveStoneConfig.catacombsMinRoomsCountAt4Level + random.nextInt(GraveStoneConfig.catacombsMaxRoomsCountAt4Level - GraveStoneConfig.catacombsMinRoomsCountAt4Level);
      }

      this.componentsCount = 0;
      this.prepareLevel(this.levelComponents);
      ObjectsGenerationHelper.beginChestCollection();
      try {
         this.generateLevel();
      } finally {
         this.generatedChests = ObjectsGenerationHelper.endChestCollection();
      }
   }

   public static BlockSelector getCatacombsStones(int level) {
      return level < 3 ? catacombsStoneBlocks : catacombsBoneBlocks;
   }

   public static Block getCatacombsStairsId(int level) {
      return (Block)(level < 3 ? Blocks.stone_brick_stairs : GSBlock.boneStairs);
   }

   public final void prepareLevel(LinkedList<CatacombsBaseComponent> currentComponents) {
      LinkedList<CatacombsBaseComponent> newComponents = new LinkedList<>();
      CatacombsBaseComponent[] components = new CatacombsBaseComponent[0];
      components = currentComponents.toArray(components);
      int resultComponentsCount = 0;
      if (this.totalComponentsCount > this.componentsCount) {
         for(int i = 0; i < components.length; ++i) {
            CatacombsBaseComponent component = components[i];
            if (component.canGoOnlyTop()) {
               if (component.goTop) {
                  resultComponentsCount += this.addComponent(newComponents, component, component.getDirection(), CatacombsLevel.COMPONENT_SIDE.TOP);
               }
            } else if (component.goTop) {
               resultComponentsCount = resultComponentsCount + this.addComponent(newComponents, component, component.getDirection(), CatacombsLevel.COMPONENT_SIDE.TOP);
               resultComponentsCount = resultComponentsCount + this.addComponent(newComponents, component, CatacombsBaseComponent.getLeftDirection(component.getDirection()), CatacombsLevel.COMPONENT_SIDE.LEFT);
               resultComponentsCount = resultComponentsCount + this.addComponent(newComponents, component, CatacombsBaseComponent.getRightDirection(component.getDirection()), CatacombsLevel.COMPONENT_SIDE.RIGHT);
            }
         }
      } else if (this.endComponents.isEmpty()) {
         this.createEnd(currentComponents, this.level);
      }

      this.componentsCount += resultComponentsCount;
      if (resultComponentsCount != 0) {
         this.prepareLevel(newComponents);
      }

   }

   private int addComponent(LinkedList<CatacombsBaseComponent> newComponents, CatacombsBaseComponent component, int direction, CatacombsLevel.COMPONENT_SIDE componentSide) {
      CatacombsBaseComponent newComponent = this.tryCreateComponent(component, direction, componentSide);
      if (newComponent != null) {
         newComponents.add(newComponent);
         return 1;
      } else {
         return 0;
      }
   }

   private void createEnd(LinkedList<CatacombsBaseComponent> currentComponents, int level) {
      LinkedList<CatacombsBaseComponent> components = currentComponents;
      int ends = 1 + this.random.nextInt(currentComponents.size() - 1);
      int endsCount = 0;
      Class componentClass;
      if (level == 4) {
         componentClass = WitherHall.class;
         ends = 1;
      } else {
         componentClass = Stairs.class;
      }

      for(int i = 0; i < ends && (endsCount < ends || components.size() > 0); ++i) {
         int j = this.random.nextInt(components.size());
         CatacombsBaseComponent component = components.get(j);
         CatacombsBaseComponent newComponent = this.tryCreateComponent(component, componentClass, component.getDirection(), level, CatacombsLevel.COMPONENT_SIDE.TOP);
         if (newComponent == null) {
            newComponent = this.tryCreateComponent(component, componentClass, CatacombsBaseComponent.getLeftDirection(component.getDirection()), level, CatacombsLevel.COMPONENT_SIDE.LEFT);
            if (newComponent == null) {
               newComponent = this.tryCreateComponent(component, componentClass, CatacombsBaseComponent.getLeftDirection(component.getDirection()), level, CatacombsLevel.COMPONENT_SIDE.LEFT);
               if (newComponent != null) {
                  this.levelComponents.add(newComponent);
                  this.endComponents.add(newComponent);
                  ++endsCount;
                  components.remove(j);
               } else {
                  components.remove(j);
               }
            } else {
               this.levelComponents.add(newComponent);
               this.endComponents.add(newComponent);
               ++endsCount;
               components.remove(j);
            }
         } else {
            this.levelComponents.add(newComponent);
            this.endComponents.add(newComponent);
            ++endsCount;
            components.remove(j);
         }
      }

      if (endsCount == 0) {
         CatacombsBaseComponent component = currentComponents.get(this.random.nextInt(components.size()));
         CatacombsBaseComponent newComponent = CatacombsComponentsFactory.createComponent(component, this.random, component.getDirection(), level, componentClass, CatacombsLevel.COMPONENT_SIDE.TOP);
         this.levelComponents.add(newComponent);
         this.endComponents.add(newComponent);
      }

   }

   private CatacombsBaseComponent tryCreateComponent(CatacombsBaseComponent component, Class componentClass, int direction, int level, CatacombsLevel.COMPONENT_SIDE componentSide) {
      if (this.componentsCount < 30 && componentClass == Treasury.class) {
         componentClass = CatacombsComponentsFactory.getCorridorType(this.random);
      }

      CatacombsBaseComponent newComponent = CatacombsComponentsFactory.createComponent(component, this.random, direction, level, componentClass, componentSide);
      if (this.canBePlaced(newComponent)) {
         this.levelComponents.add(newComponent);
         return newComponent;
      } else {
         return null;
      }
   }

   private CatacombsBaseComponent tryCreateComponent(CatacombsBaseComponent component, int direction, CatacombsLevel.COMPONENT_SIDE componentSide) {
      return this.tryCreateComponent(component, CatacombsComponentsFactory.getNextComponent(component.getClass(), componentSide, this.random, this.level), direction, this.level, componentSide);
   }

   private boolean canBePlaced(CatacombsBaseComponent component) {
      for(CatacombsBaseComponent xz : this.levelComponents) {
         if (component.canBePlacedHere(xz.getBoundingBox())) {
            return false;
         }
      }

      return true;
   }

   public final void generateLevel() {
      for(CatacombsBaseComponent component : this.levelComponents) {
         component.addComponentParts(this.world, this.random);
      }

   }

   public LinkedList<CatacombsBaseComponent> getEndParts() {
      return this.endComponents;
   }

   public LinkedList<CatacombsBaseComponent> getLevelComponents() {
      return this.levelComponents;
   }

   public List<TileEntityChest> getGeneratedChests() {
      return this.generatedChests;
   }

   protected static enum COMPONENT_SIDE {
      TOP,
      LEFT,
      RIGHT;
   }
}
