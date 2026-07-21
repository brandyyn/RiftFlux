package gravestone.block.enums;

public enum EnumHangedMobs {
   NONE,
   STEVE,
   VILLAGER,
   ZOMBIE,
   ZOMBIE_VILLAGER,
   SKELETON,
   WITHER_SKELETON,
   WITCH,
   ZOMBIE_PIGMAN;

   public static EnumHangedMobs getByID(int id) {
      return id < values().length ? values()[id] : NONE;
   }
}
