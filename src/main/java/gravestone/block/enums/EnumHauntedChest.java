package gravestone.block.enums;

import gravestone.ModGraveStone;

public enum EnumHauntedChest implements IBlockEnum {
   BATS_CHEST("block.haunted_chest.bats_chest"),
   SKELETON_CHEST("block.haunted_chest.skeleton_chest");

   private String name;

   private EnumHauntedChest(String name) {
      this.name = name;
   }

   public String getName() {
      return ModGraveStone.proxy.getLocalizedString(this.name);
   }

   public static EnumHauntedChest getById(byte id) {
      return id < values().length ? values()[id] : BATS_CHEST;
   }
}
