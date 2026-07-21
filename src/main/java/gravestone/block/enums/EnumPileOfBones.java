package gravestone.block.enums;

import gravestone.ModGraveStone;

public enum EnumPileOfBones implements IBlockEnum {
   PILE_OF_BONES("block.pile_of_bones.name"),
   PILE_OF_BONES_WITH_SKULL("block.pile_of_bones_with_skull.name");

   private String name;

   private EnumPileOfBones(String name) {
      this.name = name;
   }

   public String getName() {
      return ModGraveStone.proxy.getLocalizedString(this.name);
   }

   public static EnumPileOfBones getByID(int id) {
      return id < values().length ? values()[id] : PILE_OF_BONES;
   }
}
