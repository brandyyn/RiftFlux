package gravestone.core;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import gravestone.ModGraveStone;
import gravestone.config.GraveStoneConfig;
import gravestone.core.logger.GSLogger;
import gravestone.structures.GraveStoneWorldGenerator;
import gravestone.structures.village.ComponentGSVillageCemetery;
import gravestone.structures.village.ComponentGSVillageMemorial;
import gravestone.structures.village.ComponentGSVillageUndertaker;
import gravestone.structures.village.VillageHandlerGSCemetery;
import gravestone.structures.village.VillageHandlerGSMemorial;
import gravestone.structures.village.VillageHandlerGSUndertaker;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.structure.MapGenStructureIO;

public class GSStructures {
   public static final Block[] VALUEBLE_BLOCKS = new Block[]{Blocks.gold_block, Blocks.lapis_block, Blocks.redstone_block, Blocks.gold_block, Blocks.lapis_block, Blocks.redstone_block, Blocks.diamond_block, Blocks.emerald_block};
   private static GSStructures instance;

   private GSStructures() {
      this.generateStructures();
   }

   public static GSStructures getInstance() {
      return instance == null ? new GSStructures() : instance;
   }

   public static void preInit() {
      if (GraveStoneConfig.generateCemeteries) {
         try {
            MapGenStructureIO.func_143031_a(ComponentGSVillageCemetery.class, "GSVillageCemetery");
         } catch (Throwable var3) {
            GSLogger.logError("Can not register ComponentGSVillageCemetery");
            var3.printStackTrace();
         }
      }

      if (GraveStoneConfig.generateVillageMemorials) {
         try {
            MapGenStructureIO.func_143031_a(ComponentGSVillageMemorial.class, "GSVillageMemorial");
         } catch (Throwable var2) {
            GSLogger.logError("Can not register ComponentGSVillageMemorial");
            var2.printStackTrace();
         }
      }

      if (GraveStoneConfig.generateUndertaker) {
         try {
            MapGenStructureIO.func_143031_a(ComponentGSVillageUndertaker.class, "GSUndertakerHouse");
         } catch (Throwable var1) {
            GSLogger.logError("Can not register ComponentGSVillageUndertaker");
            var1.printStackTrace();
         }
      }

   }

   private void generateStructures() {
      if (GraveStoneConfig.generateCemeteries) {
         VillageHandlerGSCemetery villageCemeteryHandler = new VillageHandlerGSCemetery();
         VillagerRegistry.instance().registerVillageCreationHandler(villageCemeteryHandler);
      }

      if (GraveStoneConfig.generateVillageMemorials) {
         VillageHandlerGSMemorial villageMemorialHandler = new VillageHandlerGSMemorial();
         VillagerRegistry.instance().registerVillageCreationHandler(villageMemorialHandler);
      }

      if (GraveStoneConfig.generateUndertaker) {
         VillageHandlerGSUndertaker villageUndertakerHandler = new VillageHandlerGSUndertaker();
         VillagerRegistry.instance().registerVillageCreationHandler(villageUndertakerHandler);
         VillagerRegistry.instance().registerVillagerId(385);
         ModGraveStone.proxy.registerVillagers();
         VillagerRegistry.instance().registerVillageTradeHandler(385, villageUndertakerHandler);
      }

      GameRegistry.registerWorldGenerator(new GraveStoneWorldGenerator(), 50);
   }
}
