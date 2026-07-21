package gravestone.block.enums;

import gravestone.ModGraveStone;

public enum EnumSpawner implements IBlockEnum {
   WITHER_SPAWNER("block.spawner.wither"),
   SKELETON_SPAWNER("block.spawner.skeleton"),
   ZOMBIE_SPAWNER("block.spawner.zombie");

   private String name;

   private EnumSpawner(String name) {
      this.name = name;
   }

   public String getName() {
      return ModGraveStone.proxy.getLocalizedString(this.name);
   }

   public static EnumSpawner getById(byte id) {
      return id < values().length ? values()[id] : ZOMBIE_SPAWNER;
   }
}
