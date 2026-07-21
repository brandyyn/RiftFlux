package gravestone.models.block.memorials;

import cpw.mods.fml.common.registry.VillagerRegistry;
import gravestone.block.enums.EnumHangedMobs;
import gravestone.block.enums.EnumMemorials;
import gravestone.core.Resources;
import gravestone.models.block.ModelMemorial;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import org.lwjgl.opengl.GL11;

public class ModelBurningStake extends ModelMemorial {
   private ModelRenderer horisontalPlank;
   private ModelRenderer plank1;
   private ModelRenderer plank2;
   private ModelRenderer plank3;
   private ModelRenderer plank4;
   private ModelRenderer plank5;
   private ModelRenderer plank6;
   private ModelRenderer plank7;
   private ModelRenderer plank8;
   private ModelRenderer hay1;
   private ModelRenderer hay2;
   private ModelRenderer hay3;
   private ModelRenderer hay4;
   private ModelRenderer hay5;
   private ModelRenderer hay6;
   private ModelRenderer hay7;
   private ModelRenderer hay8;
   private static final ModelHangedBiped bipedModel = new ModelHangedBiped(false);
   private static final ModelHangedBiped zombieModel = new ModelHangedBiped(false, true);
   private static final ModelHangedSkeleton skeletonModel = new ModelHangedSkeleton(false);
   private static final ModelHangedSkeleton witherSkeletonModel = new ModelHangedSkeleton(false, true);
   private static final ModelHangedVillager villagerModel = new ModelHangedVillager(false);
   private static final ModelHangedZombieVillager zombieVillagerModel = new ModelHangedZombieVillager(false);
   private static final ModelHangedWitch witchModel = new ModelHangedWitch(false);

   public ModelBurningStake() {
      this.textureWidth = 64;
      this.textureHeight = 128;
      this.horisontalPlank = new ModelRenderer(this, 0, 0);
      this.horisontalPlank.addBox(0.0F, 0.0F, 0.0F, 4, 56, 4);
      this.horisontalPlank.setRotationPoint(-2.0F, -32.0F, -2.0F);
      this.horisontalPlank.setTextureSize(this.textureWidth, this.textureHeight);
      this.plank1 = new ModelRenderer(this, 17, 0);
      this.plank1.addBox(0.0F, 0.0F, 0.0F, 3, 20, 3);
      this.plank1.setRotationPoint(-1.5F, 10.0F, -2.0F);
      this.plank1.setTextureSize(this.textureWidth, this.textureHeight);
      this.setRotation(this.plank1, (-(float)Math.PI / 4F), 0.0F, 0.0F);
      this.plank2 = new ModelRenderer(this, 30, 0);
      this.plank2.addBox(0.0F, 0.0F, 0.0F, 3, 20, 3);
      this.plank2.setRotationPoint(-2.0F, 10.0F, 0.0F);
      this.plank2.setTextureSize(this.textureWidth, this.textureHeight);
      this.setRotation(this.plank2, (-(float)Math.PI / 4F), ((float)Math.PI / 4F), 0.0F);
      this.plank3 = new ModelRenderer(this, 43, 0);
      this.plank3.addBox(0.0F, 0.0F, 0.0F, 3, 20, 3);
      this.plank3.setRotationPoint(-2.0F, 10.0F, 1.5F);
      this.plank3.setTextureSize(this.textureWidth, this.textureHeight);
      this.setRotation(this.plank3, (-(float)Math.PI / 4F), 1.570796F, 0.0F);
      this.plank4 = new ModelRenderer(this, 17, 24);
      this.plank4.addBox(0.0F, 0.0F, 0.0F, 3, 20, 3);
      this.plank4.setRotationPoint(0.0F, 10.0F, 2.0F);
      this.plank4.setTextureSize(this.textureWidth, this.textureHeight);
      this.setRotation(this.plank4, (-(float)Math.PI / 4F), 2.356194F, 0.0F);
      this.plank5 = new ModelRenderer(this, 30, 24);
      this.plank5.addBox(0.0F, 0.0F, 0.0F, 3, 20, 3);
      this.plank5.setRotationPoint(1.5F, 10.0F, 2.0F);
      this.plank5.setTextureSize(this.textureWidth, this.textureHeight);
      this.setRotation(this.plank5, (-(float)Math.PI / 4F), 3.141593F, 0.0F);
      this.plank6 = new ModelRenderer(this, 43, 24);
      this.plank6.addBox(0.0F, 0.0F, 0.0F, 3, 20, 3);
      this.plank6.setRotationPoint(2.0F, 10.0F, 0.0F);
      this.plank6.setTextureSize(this.textureWidth, this.textureHeight);
      this.setRotation(this.plank6, (-(float)Math.PI / 4F), -2.356194F, 0.0F);
      this.plank7 = new ModelRenderer(this, 17, 48);
      this.plank7.addBox(0.0F, 0.0F, 0.0F, 3, 20, 3);
      this.plank7.setRotationPoint(2.0F, 10.0F, -1.5F);
      this.plank7.setTextureSize(this.textureWidth, this.textureHeight);
      this.setRotation(this.plank7, (-(float)Math.PI / 4F), -1.570796F, 0.0F);
      this.plank8 = new ModelRenderer(this, 30, 48);
      this.plank8.addBox(0.0F, 0.0F, 0.0F, 3, 20, 3);
      this.plank8.setRotationPoint(0.0F, 10.0F, -2.0F);
      this.plank8.setTextureSize(this.textureWidth, this.textureHeight);
      this.setRotation(this.plank8, (-(float)Math.PI / 4F), (-(float)Math.PI / 4F), 0.0F);
      this.hay1 = new ModelRenderer(this, 0, 72);
      this.hay1.addBox(0.0F, 0.0F, 0.0F, 5, 5, 5);
      this.hay1.setRotationPoint(-2.3F, 19.0F, -6.0F);
      this.hay1.setTextureSize(this.textureWidth, this.textureHeight);
      this.hay1.mirror = true;
      this.setRotation(this.hay1, (-(float)Math.PI / 4F), -0.2466181F, ((float)Math.PI / 4F));
      this.hay2 = new ModelRenderer(this, 0, 72);
      this.hay2.addBox(0.0F, 0.0F, 0.0F, 5, 5, 5);
      this.hay2.setRotationPoint(-6.0F, 19.0F, -2.6F);
      this.hay2.setTextureSize(this.textureWidth, this.textureHeight);
      this.hay2.mirror = true;
      this.setRotation(this.hay2, (-(float)Math.PI / 4F), 0.5827747F, ((float)Math.PI / 4F));
      this.hay3 = new ModelRenderer(this, 0, 72);
      this.hay3.addBox(0.0F, 0.0F, 0.0F, 5, 5, 5);
      this.hay3.setRotationPoint(-6.0F, 23.0F, -2.0F);
      this.hay3.setTextureSize(this.textureWidth, this.textureHeight);
      this.hay3.mirror = true;
      this.setRotation(this.hay3, ((float)Math.PI / 4F), -0.5871122F, ((float)Math.PI / 4F));
      this.hay4 = new ModelRenderer(this, 0, 72);
      this.hay4.addBox(0.0F, 0.0F, 0.0F, 5, 5, 5);
      this.hay4.setRotationPoint(-5.6F, 21.0F, 3.0F);
      this.hay4.setTextureSize(this.textureWidth, this.textureHeight);
      this.hay4.mirror = true;
      this.setRotation(this.hay4, ((float)Math.PI / 4F), 0.1350823F, ((float)Math.PI / 4F));
      this.hay5 = new ModelRenderer(this, 0, 72);
      this.hay5.addBox(0.0F, 0.0F, 0.0F, 5, 5, 5);
      this.hay5.setRotationPoint(1.7F, 23.0F, 3.0F);
      this.hay5.setTextureSize(this.textureWidth, this.textureHeight);
      this.hay5.mirror = true;
      this.setRotation(this.hay5, 0.5902105F, -0.3160182F, -0.5902105F);
      this.hay6 = new ModelRenderer(this, 0, 72);
      this.hay6.addBox(0.0F, 0.0F, 0.0F, 5, 5, 5);
      this.hay6.setRotationPoint(4.0F, 25.0F, 2.0F);
      this.hay5.setTextureSize(this.textureWidth, this.textureHeight);
      this.hay6.mirror = true;
      this.setRotation(this.hay6, 0.924818F, 0.7718693F, -0.6273891F);
      this.hay7 = new ModelRenderer(this, 0, 72);
      this.hay7.addBox(0.0F, 0.0F, 0.0F, 5, 5, 5);
      this.hay7.setRotationPoint(3.0F, 24.0F, -2.6F);
      this.hay5.setTextureSize(this.textureWidth, this.textureHeight);
      this.hay7.mirror = true;
      this.setRotation(this.hay7, 0.5902105F, 1.219252F, -0.6645677F);
      this.hay8 = new ModelRenderer(this, 0, 72);
      this.hay8.addBox(0.0F, 0.0F, 0.0F, 5, 5, 5);
      this.hay8.setRotationPoint(-2.0F, 21.0F, -6.6F);
      this.hay8.setTextureSize(this.textureWidth, this.textureHeight);
      this.hay8.mirror = true;
      this.setRotation(this.hay8, -0.924818F, -0.0716721F, -0.4786746F);
   }

   public void renderAll() {
      float f5 = 0.0625F;
      this.horisontalPlank.render(f5);
      this.plank1.render(f5);
      this.plank2.render(f5);
      this.plank3.render(f5);
      this.plank4.render(f5);
      this.plank5.render(f5);
      this.plank6.render(f5);
      this.plank7.render(f5);
      this.plank8.render(f5);
      this.hay1.render(f5);
      this.hay2.render(f5);
      this.hay3.render(f5);
      this.hay4.render(f5);
      this.hay5.render(f5);
      this.hay6.render(f5);
      this.hay7.render(f5);
      this.hay8.render(f5);
   }

   public void customRender(EnumMemorials memorialType, EnumHangedMobs mob, int villagerProfession) {
      this.renderAll();
      if (mob != EnumHangedMobs.NONE) {
         GL11.glPushMatrix();
         GL11.glTranslatef(0.0F, -0.5F, -0.25F);
         switch(mob) {
         case STEVE:
            Minecraft.getMinecraft().renderEngine.bindTexture(Resources.STEVE);
            bipedModel.renderAll();
            break;
         case VILLAGER:
            switch(villagerProfession) {
            case 0:
               Minecraft.getMinecraft().renderEngine.bindTexture(Resources.VILLAGER_FARMER);
               break;
            case 1:
               Minecraft.getMinecraft().renderEngine.bindTexture(Resources.VILLAGER_LIBRARIAN);
               break;
            case 2:
               Minecraft.getMinecraft().renderEngine.bindTexture(Resources.VILLAGER_PRIEST);
               break;
            case 3:
               Minecraft.getMinecraft().renderEngine.bindTexture(Resources.VILLAGER_SMITH);
               break;
            case 4:
               Minecraft.getMinecraft().renderEngine.bindTexture(Resources.VILLAGER_BUTCHER);
               break;
            default:
               Minecraft.getMinecraft().renderEngine.bindTexture(VillagerRegistry.getVillagerSkin(villagerProfession, Resources.VILLAGER));
            }

            villagerModel.renderAll();
            break;
         case ZOMBIE:
            Minecraft.getMinecraft().renderEngine.bindTexture(Resources.ZOMBIE);
            zombieModel.renderAll();
            break;
         case ZOMBIE_VILLAGER:
            Minecraft.getMinecraft().renderEngine.bindTexture(Resources.ZOMBIE_VILLAGER);
            zombieVillagerModel.renderAll();
            break;
         case SKELETON:
            Minecraft.getMinecraft().renderEngine.bindTexture(Resources.SKELETON);
            skeletonModel.renderAll();
            break;
         case WITHER_SKELETON:
            Minecraft.getMinecraft().renderEngine.bindTexture(Resources.WITHER_SKELETON);
            witherSkeletonModel.renderAll();
            break;
         case WITCH:
            Minecraft.getMinecraft().renderEngine.bindTexture(Resources.WITCH);
            witchModel.renderAll();
            break;
         case ZOMBIE_PIGMAN:
            Minecraft.getMinecraft().renderEngine.bindTexture(Resources.ZOMBIE_PIGMAN);
            zombieModel.renderAll();
         }

         GL11.glPopMatrix();
      }

   }
}
