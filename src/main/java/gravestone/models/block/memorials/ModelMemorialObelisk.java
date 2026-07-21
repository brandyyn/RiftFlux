package gravestone.models.block.memorials;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.models.block.ModelMemorial;
import net.minecraft.client.model.ModelRenderer;

@SideOnly(Side.CLIENT)
public class ModelMemorialObelisk extends ModelMemorial {
   ModelRenderer Plate1;
   ModelRenderer Plate2;
   ModelRenderer Pillar1;
   ModelRenderer Pillar2;
   ModelRenderer Pillar3;

   public ModelMemorialObelisk() {
      this.textureWidth = 256;
      this.textureHeight = 128;
      this.Plate1 = new ModelRenderer(this, 0, 0);
      this.Plate1.addBox(0.0F, 0.0F, 0.0F, 48, 8, 48);
      this.Plate1.setRotationPoint(-24.0F, 16.0F, -24.0F);
      this.Plate1.setTextureSize(256, 128);
      this.Plate1.mirror = true;
      this.setRotation(this.Plate1, 0.0F, 0.0F, 0.0F);
      this.Plate2 = new ModelRenderer(this, 0, 56);
      this.Plate2.addBox(0.0F, 0.0F, 0.0F, 32, 8, 32);
      this.Plate2.setRotationPoint(-16.0F, 8.0F, -16.0F);
      this.Plate2.setTextureSize(256, 128);
      this.Plate2.mirror = true;
      this.setRotation(this.Plate2, 0.0F, 0.0F, 0.0F);
      this.Pillar1 = new ModelRenderer(this, 128, 56);
      this.Pillar1.addBox(0.0F, 0.0F, 0.0F, 20, 20, 20);
      this.Pillar1.setRotationPoint(-10.0F, -12.0F, -10.0F);
      this.Pillar1.setTextureSize(256, 128);
      this.Pillar1.mirror = true;
      this.setRotation(this.Pillar1, 0.0F, 0.0F, 0.0F);
      this.Pillar2 = new ModelRenderer(this, 144, 0);
      this.Pillar2.addBox(0.0F, 0.0F, 0.0F, 16, 23, 16);
      this.Pillar2.setRotationPoint(-8.0F, -35.0F, -8.0F);
      this.Pillar2.setTextureSize(256, 128);
      this.Pillar2.mirror = true;
      this.setRotation(this.Pillar2, 0.0F, 0.0F, 0.0F);
      this.Pillar3 = new ModelRenderer(this, 0, 0);
      this.Pillar3.addBox(0.0F, 0.0F, 0.0F, 12, 27, 12);
      this.Pillar3.setRotationPoint(-6.0F, -62.0F, -6.0F);
      this.Pillar3.setTextureSize(256, 128);
      this.Pillar3.mirror = true;
      this.setRotation(this.Pillar3, 0.0F, 0.0F, 0.0F);
   }

   public void renderAll() {
      this.Plate1.render(0.0625F);
      this.Plate2.render(0.0625F);
      this.Pillar1.render(0.0625F);
      this.Pillar2.render(0.0625F);
      this.Pillar3.render(0.0625F);
   }
}
