package gravestone.core;

import cpw.mods.fml.common.registry.GameRegistry;
import gravestone.tileentity.TileEntityGSAltar;
import gravestone.tileentity.TileEntityGSCandle;
import gravestone.tileentity.TileEntityGSGraveStone;
import gravestone.tileentity.TileEntityGSHauntedChest;
import gravestone.tileentity.TileEntityGSMemorial;
import gravestone.tileentity.TileEntityGSPileOfBones;
import gravestone.tileentity.TileEntityGSSkullCandle;
import gravestone.tileentity.TileEntityGSSpawner;

public class GSTileEntity {
   private GSTileEntity() {
   }

   public static void registration() {
      GameRegistry.registerTileEntity(TileEntityGSGraveStone.class, "GraveStoneTE");
      GameRegistry.registerTileEntity(TileEntityGSMemorial.class, "Memorial");
      GameRegistry.registerTileEntity(TileEntityGSSpawner.class, "GS Spawner");
      GameRegistry.registerTileEntity(TileEntityGSHauntedChest.class, "GSHaunted Chest");
      GameRegistry.registerTileEntity(TileEntityGSCandle.class, "GSTECandle");
      GameRegistry.registerTileEntity(TileEntityGSSkullCandle.class, "GSSkull Candle");
      GameRegistry.registerTileEntity(TileEntityGSPileOfBones.class, "GSTEPileOfBones");
      GameRegistry.registerTileEntity(TileEntityGSAltar.class, "GSAltarTE");
   }
}
