package gravestone.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.entity.monster.EntityUndeadCat;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderUndeadCat extends RenderLiving {
   public RenderUndeadCat(ModelBase model, float par2) {
      super(model, par2);
   }

   public void renderLivingUndeadCat(EntityUndeadCat undeadCat, double par2, double par4, double par6, float par8, float par9) {
      super.doRender((EntityLiving)undeadCat, par2, par4, par6, par8, par9);
   }

   protected void preRenderUndeadCat(EntityUndeadCat undeadCat, float par2) {
      super.preRenderCallback(undeadCat, par2);
      GL11.glScalef(0.8F, 0.8F, 0.8F);
   }

   protected void preRenderCallback(EntityLiving entityLiving, float par2) {
      this.preRenderUndeadCat((EntityUndeadCat)entityLiving, par2);
   }

   public void doRender(EntityLiving entityLiving, double par2, double par4, double par6, float par8, float par9) {
      this.renderLivingUndeadCat((EntityUndeadCat)entityLiving, par2, par4, par6, par8, par9);
   }

   public void doRender(Entity entity, double par2, double par4, double par6, float par8, float par9) {
      this.renderLivingUndeadCat((EntityUndeadCat)entity, par2, par4, par6, par8, par9);
   }

   protected ResourceLocation getEntityTexture(Entity entity) {
      return ((EntityUndeadCat)entity).getTexture();
   }
}
