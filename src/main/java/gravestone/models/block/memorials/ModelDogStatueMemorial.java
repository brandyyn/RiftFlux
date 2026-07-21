package gravestone.models.block.memorials;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.models.block.ModelMemorial;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class ModelDogStatueMemorial extends ModelMemorial {
   private ModelRenderer wolfHeadMain;
   private ModelRenderer wolfBody;
   private ModelRenderer wolfLeg1;
   private ModelRenderer wolfLeg2;
   private ModelRenderer wolfLeg3;
   private ModelRenderer wolfLeg4;
   private ModelRenderer wolfMane;
   private ModelRenderer wolfTail;
   private ModelBigPedestal pedestal;

   public ModelDogStatueMemorial() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      float f = 0.0F;
      float f1 = 13.5F;
      this.wolfHeadMain = new ModelRenderer(this, 0, 0);
      this.wolfHeadMain.addBox(-3.0F, -3.0F, -2.0F, 6, 6, 4, f);
      this.wolfHeadMain.setRotationPoint(-1.0F, f1, -7.0F);
      this.wolfBody = new ModelRenderer(this, 18, 14);
      this.wolfBody.addBox(-4.0F, -2.0F, -3.0F, 6, 9, 6, f);
      this.wolfBody.setRotationPoint(0.0F, 14.0F, 2.0F);
      this.wolfMane = new ModelRenderer(this, 21, 0);
      this.wolfMane.addBox(-4.0F, -3.0F, -3.0F, 8, 6, 7, f);
      this.wolfMane.setRotationPoint(-1.0F, 14.0F, 2.0F);
      this.wolfLeg1 = new ModelRenderer(this, 0, 18);
      this.wolfLeg1.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f);
      this.wolfLeg1.setRotationPoint(-2.5F, 16.0F, 7.0F);
      this.wolfLeg2 = new ModelRenderer(this, 0, 18);
      this.wolfLeg2.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f);
      this.wolfLeg2.setRotationPoint(0.5F, 16.0F, 7.0F);
      this.wolfLeg3 = new ModelRenderer(this, 0, 18);
      this.wolfLeg3.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f);
      this.wolfLeg3.setRotationPoint(-2.5F, 16.0F, -4.0F);
      this.wolfLeg4 = new ModelRenderer(this, 0, 18);
      this.wolfLeg4.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f);
      this.wolfLeg4.setRotationPoint(0.5F, 16.0F, -4.0F);
      this.wolfTail = new ModelRenderer(this, 9, 18);
      this.wolfTail.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f);
      this.wolfTail.setRotationPoint(-1.0F, 12.0F, 8.0F);
      this.wolfHeadMain.setTextureOffset(16, 14).addBox(-3.0F, -5.0F, 0.0F, 2, 2, 1, f);
      this.wolfHeadMain.setTextureOffset(16, 14).addBox(1.0F, -5.0F, 0.0F, 2, 2, 1, f);
      this.wolfHeadMain.setTextureOffset(0, 10).addBox(-1.5F, 0.0F, -5.0F, 3, 3, 4, f);
      this.pedestal = new ModelBigPedestal();
   }

   public void setRotationAngles(float par2, float par3, float par4, float par5) {
      this.wolfHeadMain.rotateAngleX = -0.08F;
      this.wolfMane.setRotationPoint(-1.0F, 16.0F, -3.0F);
      this.wolfMane.rotateAngleX = 1.2566371F;
      this.wolfMane.rotateAngleY = 0.0F;
      this.wolfBody.setRotationPoint(0.0F, 18.0F, 0.0F);
      this.wolfBody.rotateAngleX = ((float)Math.PI / 4F);
      this.wolfLeg1.setRotationPoint(-2.5F, 22.0F, 2.0F);
      this.wolfLeg1.rotateAngleX = ((float)Math.PI * 1.5F);
      this.wolfLeg2.setRotationPoint(0.5F, 22.0F, 2.0F);
      this.wolfLeg2.rotateAngleX = ((float)Math.PI * 1.5F);
      this.wolfLeg3.rotateAngleX = 5.811947F;
      this.wolfLeg3.setRotationPoint(-2.49F, 17.0F, -4.0F);
      this.wolfLeg4.rotateAngleX = 5.811947F;
      this.wolfLeg4.setRotationPoint(0.51F, 17.0F, -4.0F);
      this.wolfTail.setRotationPoint(-1.0F, 21.0F, 6.0F);
      this.wolfTail.rotateAngleX = 1.7278761F;
   }

   public void renderAll() {
      this.setRotationAngles(0.0625F, 0.0625F, 0.0625F, 0.0625F);
      float par7 = 0.0625F;
      ModelBigPedestal var10000 = this.pedestal;
      ModelBigPedestal.shiftModel();
      this.wolfHeadMain.renderWithRotation(par7);
      this.wolfBody.render(par7);
      this.wolfLeg1.render(par7);
      this.wolfLeg2.render(par7);
      this.wolfLeg3.render(par7);
      this.wolfLeg4.render(par7);
      this.wolfMane.render(par7);
      this.wolfTail.render(par7);
      this.pedestal.renderAll();
   }

   public void setPedestalTexture(ResourceLocation texture) {
      this.pedestal.setTexture(texture);
   }
}
