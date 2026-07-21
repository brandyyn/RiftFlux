package gravestone.models.block.memorials;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.core.Resources;
import gravestone.models.block.ModelMemorial;
import gravestone.renderer.tileentity.TileEntityGSMemorialRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ModelCreeperStatueMemorial extends ModelMemorial {
   public ModelRenderer head;
   public ModelRenderer field_78133_b;
   public ModelRenderer body;
   public ModelRenderer leg1;
   public ModelRenderer leg2;
   public ModelRenderer leg3;
   public ModelRenderer leg4;
   ModelBigPedestal pedestal;

   public ModelCreeperStatueMemorial() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      float par1 = 0.0F;
      byte b0 = 4;
      this.head = new ModelRenderer(this, 0, 0);
      this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, par1);
      this.head.setRotationPoint(0.0F, (float)b0, 0.0F);
      this.field_78133_b = new ModelRenderer(this, 32, 0);
      this.field_78133_b.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, par1 + 0.5F);
      this.field_78133_b.setRotationPoint(0.0F, (float)b0, 0.0F);
      this.body = new ModelRenderer(this, 16, 16);
      this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, par1);
      this.body.setRotationPoint(0.0F, (float)b0, 0.0F);
      this.leg1 = new ModelRenderer(this, 0, 16);
      this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, par1);
      this.leg1.setRotationPoint(-2.0F, (float)(12 + b0), 4.0F);
      this.leg2 = new ModelRenderer(this, 0, 16);
      this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, par1);
      this.leg2.setRotationPoint(2.0F, (float)(12 + b0), 4.0F);
      this.leg3 = new ModelRenderer(this, 0, 16);
      this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, par1);
      this.leg3.setRotationPoint(-2.0F, (float)(12 + b0), -4.0F);
      this.leg4 = new ModelRenderer(this, 0, 16);
      this.leg4.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, par1);
      this.leg4.setRotationPoint(2.0F, (float)(12 + b0), -4.0F);
      this.pedestal = new ModelBigPedestal();
   }

   public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6) {
      this.head.rotateAngleY = par4 / (180F / (float)Math.PI);
      this.head.rotateAngleX = par5 / (180F / (float)Math.PI);
      this.leg1.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
      this.leg2.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float)Math.PI) * 1.4F * par2;
      this.leg3.rotateAngleX = MathHelper.cos(par1 * 0.6662F + (float)Math.PI) * 1.4F * par2;
      this.leg4.rotateAngleX = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
   }

   public void renderAll() {
      this.setRotationAngles(0.0625F, 0.0625F, 0.0625F, 0.0625F, 0.0625F, 0.0625F);
      GL11.glTranslated(0.0D, -0.8D, 0.0D);
      this.renderCreeper();
      GL11.glTranslated(0.0D, -0.19D, 0.0D);
      this.pedestal.renderAll();
   }

   public void customRender(boolean enchanted) {
      if (enchanted) {
         this.renderEnchanted();
      } else {
         this.renderAll();
      }

      this.renderCreeperCharging();
   }

   private void renderCreeper() {
      float par7 = 0.0625F;
      this.head.render(par7);
      this.body.render(par7);
      this.leg1.render(par7);
      this.leg2.render(par7);
      this.leg3.render(par7);
      this.leg4.render(par7);
   }

   private void renderCreeperCharging() {
      float tickModifier = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 48.0F;
      float scale = 1.2F;
      float f4 = 0.5F;
      GL11.glTranslated(0.0D, -0.5D, 0.0D);
      GL11.glScalef(scale, scale, scale);
      TileEntityGSMemorialRenderer.instance.bindTextureByName(Resources.CREEPER_AURA);
      GL11.glEnable(3042);
      GL11.glDepthMask(true);
      GL11.glBlendFunc(768, 1);
      GL11.glMatrixMode(5890);

      for(int var21 = 0; var21 < 3; ++var21) {
         GL11.glLoadIdentity();
         float var23 = tickModifier * (0.001F + (float)var21 * 0.0015F) * 15.0F;
         GL11.glTranslatef(0.0F, var23, 0.0F);
         this.renderCreeper();
      }

      GL11.glMatrixMode(5890);
      GL11.glLoadIdentity();
      GL11.glMatrixMode(5888);
      GL11.glEnable(2896);
      GL11.glDisable(3042);
      GL11.glDepthFunc(515);
      GL11.glBlendFunc(1, 1);
      GL11.glTranslated(0.0D, -0.19D, 0.0D);
   }

   public void setPedestalTexture(ResourceLocation texture) {
      this.pedestal.setTexture(texture);
   }
}
