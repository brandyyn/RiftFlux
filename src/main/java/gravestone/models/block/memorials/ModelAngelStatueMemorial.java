package gravestone.models.block.memorials;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.models.block.ModelMemorial;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class ModelAngelStatueMemorial extends ModelMemorial {
   private ModelRenderer Legs;
   private ModelRenderer Body;
   private ModelRenderer Head;
   private ModelRenderer RightArm;
   private ModelRenderer RightArm2;
   private ModelRenderer LeftArm;
   private ModelRenderer LeftArm2;
   private ModelRenderer RightWing;
   private ModelRenderer RightWing2;
   private ModelRenderer LeftWing;
   private ModelRenderer LeftWing2;
   private ModelBigPedestal pedestal;

   public ModelAngelStatueMemorial() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.Legs = new ModelRenderer(this, 0, 16);
      this.Legs.addBox(0.0F, 0.0F, 0.0F, 6, 11, 3);
      this.Legs.setRotationPoint(-3.0F, 13.0F, 0.0F);
      this.Legs.setTextureSize(64, 64);
      this.Legs.mirror = true;
      this.setRotation(this.Legs, 0.0F, 0.0F, 0.0F);
      this.Body = new ModelRenderer(this, 0, 0);
      this.Body.addBox(0.0F, 0.0F, 0.0F, 8, 12, 4);
      this.Body.setRotationPoint(-4.0F, 1.0F, -0.5F);
      this.Body.setTextureSize(64, 64);
      this.Body.mirror = true;
      this.setRotation(this.Body, 0.0F, 0.0F, 0.0F);
      this.Head = new ModelRenderer(this, 24, 0);
      this.Head.addBox(0.0F, 0.0F, 0.0F, 8, 8, 8);
      this.Head.setRotationPoint(-4.0F, -8.0F, 1.0F);
      this.Head.setTextureSize(64, 64);
      this.Head.mirror = true;
      this.setRotation(this.Head, -1.047198F, 0.0F, 0.0F);
      this.RightArm = new ModelRenderer(this, 18, 16);
      this.RightArm.addBox(0.0F, 0.0F, 0.0F, 3, 7, 3);
      this.RightArm.setRotationPoint(-6.5F, 1.5F, 1.0F);
      this.RightArm.setTextureSize(64, 64);
      this.RightArm.mirror = true;
      this.setRotation(this.RightArm, -0.837758F, -0.4014257F, 0.0F);
      this.RightArm2 = new ModelRenderer(this, 30, 16);
      this.RightArm2.addBox(0.0F, 0.0F, 0.0F, 3, 7, 3);
      this.RightArm2.setRotationPoint(-3.0F, 1.0F, -4.5F);
      this.RightArm2.setTextureSize(64, 64);
      this.RightArm2.mirror = true;
      this.setRotation(this.RightArm2, 0.122173F, -0.4363323F, 0.2268928F);
      this.LeftArm = new ModelRenderer(this, 18, 16);
      this.LeftArm.addBox(0.0F, 0.0F, 0.0F, 3, 7, 3);
      this.LeftArm.setRotationPoint(3.5F, 1.5F, 2.0F);
      this.LeftArm.setTextureSize(64, 64);
      this.LeftArm.mirror = true;
      this.setRotation(this.LeftArm, -0.837758F, 0.4014257F, 0.0F);
      this.LeftArm2 = new ModelRenderer(this, 30, 16);
      this.LeftArm2.addBox(0.0F, 0.0F, 0.0F, 3, 7, 3);
      this.LeftArm2.setRotationPoint(0.2F, 2.0F, -4.0F);
      this.LeftArm2.setTextureSize(64, 64);
      this.LeftArm2.mirror = true;
      this.setRotation(this.LeftArm2, 0.296706F, 0.4537856F, -0.1570796F);
      this.RightWing = new ModelRenderer(this, 28, 30);
      this.RightWing.addBox(0.0F, 0.0F, 0.0F, 0, 7, 7);
      this.RightWing.setRotationPoint(-1.0F, 2.0F, 2.5F);
      this.RightWing.setTextureSize(64, 64);
      this.RightWing.mirror = true;
      this.setRotation(this.RightWing, 0.6108652F, -0.5061455F, -0.0872665F);
      this.RightWing2 = new ModelRenderer(this, 0, 30);
      this.RightWing2.addBox(0.0F, 0.0F, 0.0F, 0, 14, 14);
      this.RightWing2.setRotationPoint(-4.0F, -2.0F, 7.3F);
      this.RightWing2.setTextureSize(64, 64);
      this.RightWing2.mirror = true;
      this.setRotation(this.RightWing2, -1.064651F, 0.2094395F, 0.3839724F);
      this.LeftWing = new ModelRenderer(this, 28, 30);
      this.LeftWing.addBox(0.0F, 0.0F, 0.0F, 0, 7, 7);
      this.LeftWing.setRotationPoint(1.0F, 2.0F, 2.5F);
      this.LeftWing.setTextureSize(64, 64);
      this.LeftWing.mirror = true;
      this.setRotation(this.LeftWing, 0.6108652F, 0.5061455F, 0.0872665F);
      this.LeftWing2 = new ModelRenderer(this, 0, 30);
      this.LeftWing2.addBox(0.0F, 0.0F, 0.0F, 0, 14, 14);
      this.LeftWing2.setRotationPoint(4.0F, -2.0F, 7.3F);
      this.LeftWing2.setTextureSize(64, 64);
      this.LeftWing2.mirror = true;
      this.setRotation(this.LeftWing2, -1.082104F, -0.1745329F, -0.3839724F);
      this.pedestal = new ModelBigPedestal();
   }

   public void renderAll() {
      float par7 = 0.0625F;
      ModelBigPedestal var10000 = this.pedestal;
      ModelBigPedestal.shiftModel();
      this.Legs.render(par7);
      this.Body.render(par7);
      this.Head.render(par7);
      this.RightArm.render(par7);
      this.RightArm2.render(par7);
      this.LeftArm.render(par7);
      this.LeftArm2.render(par7);
      this.RightWing.render(par7);
      this.RightWing2.render(par7);
      this.LeftWing.render(par7);
      this.LeftWing2.render(par7);
      this.pedestal.renderAll();
   }

   public void setPedestalTexture(ResourceLocation texture) {
      this.pedestal.setTexture(texture);
   }
}
