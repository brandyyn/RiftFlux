package gravestone.block.enums;

import gravestone.ModGraveStone;

public enum EnumTrap implements IBlockEnum {
   NIGHT_STONE("tile.trap.night.name"),
   THUNDER_STONE("tile.trap.thunder.name");

   private String name;

   private EnumTrap(String name) {
      this.name = name;
   }

   public String getName() {
      return ModGraveStone.proxy.getLocalizedString(this.name);
   }

   public static EnumTrap getById(byte id) {
      return id < values().length ? values()[id] : NIGHT_STONE;
   }
}
