package gravestone.block.enums;

import gravestone.ModGraveStone;

public enum EnumSkullCandle implements IBlockEnum {
   SKELETON_SKULL("block.skull_candle.skeleton.name"),
   WITHER_SKULL("block.skull_candle.wither_skeleton.name"),
   ZOMBIE_SKULL("block.skull_candle.zombie.name");

   private String name;

   private EnumSkullCandle(String name) {
      this.name = name;
   }

   public String getName() {
      return ModGraveStone.proxy.getLocalizedString(this.name);
   }

   public static EnumSkullCandle getByID(int id) {
      return id < values().length ? values()[id] : SKELETON_SKULL;
   }
}
