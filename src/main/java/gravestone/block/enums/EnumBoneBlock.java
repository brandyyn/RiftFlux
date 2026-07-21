package gravestone.block.enums;

import gravestone.ModGraveStone;

public enum EnumBoneBlock implements IBlockEnum {
   BONE_BLOCK("tile.bone_block.name"),
   SKULL_BONE_BLOCK("tile.bone_block.skull_name"),
   CRAWLER_BONE_BLOCK("tile.bone_block.crawler_name"),
   CRAWLER_SKULL_BONE_BLOCK("tile.bone_block.crawler_skull_name");

   private String name;

   private EnumBoneBlock(String name) {
      this.name = name;
   }

   public String getName() {
      return ModGraveStone.proxy.getLocalizedString(this.name);
   }

   public static EnumBoneBlock getById(byte id) {
      return id < values().length ? values()[id] : BONE_BLOCK;
   }
}
