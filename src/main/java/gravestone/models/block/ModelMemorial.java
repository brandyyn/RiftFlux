package gravestone.models.block;

import gravestone.block.enums.EnumHangedMobs;
import gravestone.block.enums.EnumMemorials;
import net.minecraft.util.ResourceLocation;

public abstract class ModelMemorial extends ModelGraveStone {
   public void setPedestalTexture(ResourceLocation texture) {
   }

   public void customRender(EnumMemorials memorialType, boolean enchanted) {
      if (enchanted) {
         this.renderEnchanted();
      } else {
         this.renderAll();
      }

   }

   public void customRender(EnumMemorials memorialType, EnumHangedMobs mob, int villagerProfession) {
   }
}
