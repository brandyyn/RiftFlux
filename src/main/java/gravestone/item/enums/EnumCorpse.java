package gravestone.item.enums;

import gravestone.ModGraveStone;
import gravestone.block.enums.IBlockEnum;

public enum EnumCorpse implements IBlockEnum {
   VILLAGER("item.corpse.villager"),
   DOG("item.corpse.dog"),
   CAT("item.corpse.cat"),
   HORSE("item.corpse.horse");

   private String name;

   private EnumCorpse(String name) {
      this.name = name;
   }

   public String getName() {
      return ModGraveStone.proxy.getLocalizedString(this.name);
   }

   public static EnumCorpse getById(byte id) {
      return id < values().length ? values()[id] : VILLAGER;
   }
}
