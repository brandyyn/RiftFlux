package gravestone.models.block.memorials;

import cpw.mods.fml.common.registry.VillagerRegistry;
import gravestone.block.enums.EnumHangedMobs;
import gravestone.block.enums.EnumMemorials;
import gravestone.core.Resources;
import gravestone.models.block.ModelMemorial;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import org.lwjgl.opengl.GL11;

public class ModelGibbet extends ModelMemorial {
   private ModelRenderer horisontalPlank;
   private ModelRenderer verticalPlank;
   private ModelRenderer plank1;
   private ModelRenderer plank2;
   private ModelRenderer plank3;
   private ModelRenderer plank4;
   private ModelRenderer rope;
   private ModelRenderer rope2;
   private ModelRenderer knot;
   private ModelRenderer loop1;
   private ModelRenderer loop2;
   private ModelRenderer loop3;
   private ModelRenderer loop4;
   private ModelRenderer loop5;
   private static final ModelHangedBiped bipedModel = new ModelHangedBiped(false);
   private static final ModelHangedBiped zombieModel = new ModelHangedBiped(false, true);
   private static final ModelHangedSkeleton skeletonModel = new ModelHangedSkeleton(false);
   private static final ModelHangedSkeleton witherSkeletonModel = new ModelHangedSkeleton(false, true);
   private static final ModelHangedVillager villagerModel = new ModelHangedVillager(false);
   private static final ModelHangedZombieVillager zombieVillagerModel = new ModelHangedZombieVillager(false);
   private static final ModelHangedWitch witchModel = new ModelHangedWitch(false);

   public ModelGibbet() {
      this.textureWidth = 64;
      this.textureHeight = 128;
      this.horisontalPlank = new ModelRenderer(this, 0, 0);
      this.horisontalPlank.addBox(0.0F, 0.0F, 0.0F, 4, 56, 4);
      this.horisontalPlank.setRotationPoint(-2.0F, -32.0F, 3.0F);
      this.horisontalPlank.setTextureSize(this.textureWidth, this.textureHeight);
      this.verticalPlank = new ModelRenderer(this, 16, 0);
      this.verticalPlank.addBox(0.0F, 0.0F, 0.0F, 4, 4, 20);
      this.verticalPlank.setRotationPoint(-2.0F, -32.0F, -17.0F);
      this.verticalPlank.setTextureSize(this.textureWidth, this.textureHeight);
      this.plank1 = new ModelRenderer(this, 31, 24);
      this.plank1.addBox(0.0F, 0.0F, 0.0F, 4, 15, 3);
      this.plank1.setRotationPoint(-2.0F, 13.3F, 3.5F);
      this.plank1.setTextureSize(this.textureWidth, this.textureHeight);
      this.setRotation(this.plank1, 0.0F, 0.0F, ((float)Math.PI / 4F));
      this.plank2 = new ModelRenderer(this, 16, 24);
      this.plank2.addBox(0.0F, 0.0F, 0.0F, 3, 15, 4);
      this.plank2.setRotationPoint(-1.5F, 13.3F, 3.0F);
      this.plank2.setTextureSize(this.textureWidth, this.textureHeight);
      this.setRotation(this.plank2, (-(float)Math.PI / 4F), 0.0F, 0.0F);
      this.plank3 = new ModelRenderer(this, 31, 42);
      this.plank3.addBox(0.0F, 0.0F, 0.0F, 4, 15, 3);
      this.plank3.setRotationPoint(-1.0F, 16.0F, 3.5F);
      this.plank3.setTextureSize(this.textureWidth, this.textureHeight);
      this.setRotation(this.plank3, 0.0F, 0.0F, (-(float)Math.PI / 4F));
      this.plank4 = new ModelRenderer(this, 16, 43);
      this.plank4.addBox(0.0F, 0.0F, 0.0F, 3, 15, 4);
      this.plank4.setRotationPoint(-1.5F, -20.3F, 6.0F);
      this.plank4.setTextureSize(this.textureWidth, this.textureHeight);
      this.setRotation(this.plank4, -2.356194F, 0.0F, 0.0F);
      this.rope = new ModelRenderer(this, 0, 62);
      this.rope.addBox(0.0F, 0.0F, 0.0F, 5, 5, 3);
      this.rope.setRotationPoint(-2.5F, -32.5F, -16.0F);
      this.rope.setTextureSize(this.textureWidth, this.textureHeight);
      this.rope2 = new ModelRenderer(this, 0, 71);
      this.rope2.addBox(0.0F, 0.0F, 0.0F, 1, 16, 1);
      this.rope2.setRotationPoint(-0.5F, -28.0F, -15.0F);
      this.rope2.setTextureSize(this.textureWidth, this.textureHeight);
      this.knot = new ModelRenderer(this, 5, 71);
      this.knot.addBox(0.0F, 0.0F, 0.0F, 2, 6, 2);
      this.knot.setRotationPoint(-1.4F, -18.0F, -14.5F);
      this.knot.setTextureSize(this.textureWidth, this.textureHeight);
      this.setRotation(this.knot, 0.0F, ((float)Math.PI / 4F), 0.0F);
      this.loop1 = new ModelRenderer(this, 17, 65);
      this.loop1.addBox(0.0F, 0.0F, 0.0F, 1, 4, 1);
      this.loop1.setRotationPoint(-0.8F, -12.3F, -15.0F);
      this.loop1.setTextureSize(this.textureWidth, this.textureHeight);
      this.setRotation(this.loop1, 0.0F, 0.0F, 0.1745329F);
      this.loop2 = new ModelRenderer(this, 22, 65);
      this.loop2.addBox(0.0F, 0.0F, 0.0F, 1, 4, 1);
      this.loop2.setRotationPoint(-0.2F, -12.1F, -15.0F);
      this.loop2.setTextureSize(this.textureWidth, this.textureHeight);
      this.setRotation(this.loop2, 0.0F, 0.0F, -0.1745329F);
      this.loop3 = new ModelRenderer(this, 27, 65);
      this.loop3.addBox(0.0F, 0.0F, 0.0F, 2, 1, 1);
      this.loop3.setRotationPoint(-0.65F, -8.85F, -15.0F);
      this.loop3.setTextureSize(this.textureWidth, this.textureHeight);
      this.setRotation(this.loop3, 0.0F, 0.0F, 1.082104F);
      this.loop4 = new ModelRenderer(this, 27, 68);
      this.loop4.addBox(0.0F, 0.0F, 0.0F, 2, 1, 1);
      this.loop4.setRotationPoint(1.5F, -8.4F, -15.0F);
      this.loop4.setTextureSize(this.textureWidth, this.textureHeight);
      this.setRotation(this.loop4, 0.0F, 0.0F, 2.094395F);
      this.loop5 = new ModelRenderer(this, 34, 65);
      this.loop5.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1);
      this.loop5.setRotationPoint(-0.5F, -7.6F, -15.0F);
      this.loop5.setTextureSize(this.textureWidth, this.textureHeight);
   }

   public void renderAllWithoutLoop() {
      this.horisontalPlank.render(0.0625F);
      this.verticalPlank.render(0.0625F);
      this.plank1.render(0.0625F);
      this.plank2.render(0.0625F);
      this.plank3.render(0.0625F);
      this.plank4.render(0.0625F);
      this.rope.render(0.0625F);
      this.rope2.render(0.0625F);
      this.knot.render(0.0625F);
   }

   public void renderLoop() {
      this.loop1.render(0.0625F);
      this.loop2.render(0.0625F);
      this.loop3.render(0.0625F);
      this.loop4.render(0.0625F);
      this.loop5.render(0.0625F);
   }

   public void renderAll() {
      this.renderAllWithoutLoop();
      this.renderLoop();
   }

   public void customRender(EnumMemorials memorialType, EnumHangedMobs mob, int villagerProfession) {
      if (mob == EnumHangedMobs.NONE) {
         this.renderAll();
      } else {
         this.renderAllWithoutLoop();
         GL11.glPushMatrix();
         GL11.glTranslatef(0.0F, -0.5F, -1.1F);
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
            GL11.glTranslatef(0.0F, 0.0F, 0.1F);
            Minecraft.getMinecraft().renderEngine.bindTexture(Resources.SKELETON);
            skeletonModel.renderAll();
            break;
         case WITHER_SKELETON:
            GL11.glTranslatef(0.0F, 0.0F, 0.1F);
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
