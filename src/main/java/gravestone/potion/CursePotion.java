package gravestone.potion;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.core.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;

public class CursePotion extends Potion {
   public CursePotion(int id, boolean isBadEffect, int liquidColor) {
      super(id, isBadEffect, liquidColor);
      this.setIconIndex(0, 0);
   }

   public void performEffect(EntityLivingBase entity, int p_76394_2_) {
   }

   public String getName() {
      return "Curse";
   }

   @SideOnly(Side.CLIENT)
   public int getStatusIconIndex() {
      Minecraft.getMinecraft().renderEngine.bindTexture(Resources.POTIONS);
      return super.getStatusIconIndex();
   }
}
