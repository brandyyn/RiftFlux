package gravestone.models.block;

import com.voidsrift.riftflux.vortex.lib.helper.EnchantHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.core.Resources;
import gravestone.renderer.GSGlintAnimation;
import gravestone.renderer.tileentity.TileEntityGSGraveStoneRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

@SideOnly(Side.CLIENT)
public abstract class ModelGraveStone extends ModelBase {
   public abstract void renderAll();

   protected void setRotation(ModelRenderer model, float x, float y, float z) {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
   }

   public void customRender(boolean enchanted) {
      if (enchanted) {
         this.renderEnchanted();
      } else {
         this.renderAll();
      }

   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
   }

   public void renderEnchanted() {
      this.renderAll();
      this.renderEnchantment();
   }

   public void renderEnchanted(int customGlint) {
      this.renderAll();
      EnchantHelper.GlintColor color = EnchantHelper.resolveGlintColor(customGlint);
      if (color.render) {
         this.renderEnchantment(color);
      }
   }

   protected void renderEnchantment() {
      this.renderEnchantment((EnchantHelper.GlintColor)null);
   }

   private void renderEnchantment(EnchantHelper.GlintColor color) {
      double elapsedSeconds = GSGlintAnimation.getElapsedSeconds();
      TileEntityGSGraveStoneRenderer.instance.bindTextureByName(Resources.SWORD_AURA);
      GL11.glEnable(3042);
      GL11.glDepthMask(true);
      GL11.glBlendFunc(768, 1);
      if (color != null && color.subtractive) {
         GL14.glBlendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);
      }
      GL11.glMatrixMode(5890);

      for(int var21 = 0; var21 < 3; ++var21) {
         GL11.glDisable(2896);
         float var22 = 0.76F;
         if (color == null) {
            GL11.glColor4f(0.5F * var22, 0.25F * var22, 0.8F * var22, 1.0F);
         } else {
            GL11.glColor4f(color.red, color.green, color.blue, 1.0F);
         }
         GL11.glBlendFunc(768, 1);
         GL11.glMatrixMode(5890);
         GL11.glLoadIdentity();
         float var23 = (float)(elapsedSeconds * (0.24D + (double)var21 * 0.36D));
         float var24 = 0.33333334F;
         GL11.glScalef(var24, var24, var24);
         GL11.glRotatef(30.0F - (float)var21 * 60.0F, 0.0F, 0.0F, 1.0F);
         GL11.glTranslatef(0.0F, var23, 0.0F);
         GL11.glMatrixMode(5888);
         this.renderAll();
      }

      GL11.glMatrixMode(5890);
      GL11.glDepthMask(true);
      GL11.glLoadIdentity();
      GL11.glMatrixMode(5888);
      if (color != null && color.subtractive) {
         GL14.glBlendEquation(GL14.GL_FUNC_ADD);
      }
      GL11.glEnable(2896);
      GL11.glDisable(3042);
      GL11.glDepthFunc(515);
   }
}
