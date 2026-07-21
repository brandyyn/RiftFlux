package gravestone.models.block.memorials;

import cpw.mods.fml.common.registry.VillagerRegistry;
import gravestone.block.enums.EnumHangedMobs;
import gravestone.block.enums.EnumMemorials;
import gravestone.core.Resources;
import gravestone.models.block.ModelMemorial;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import org.lwjgl.opengl.GL11;

public class ModelStocks extends ModelMemorial {
   private ModelRenderer horisontalPlank;
   private ModelRenderer verticalPlank1;
   private ModelRenderer verticalPlank2;
   private ModelRenderer headHole;
   private ModelRenderer armHole1;
   private ModelRenderer armHole2;
   private static final ModelHangedBiped bipedModel = new ModelHangedBiped(true);
   private static final ModelHangedBiped zombieModel = new ModelHangedBiped(true, true);
   private static final ModelHangedSkeleton skeletonModel = new ModelHangedSkeleton(true);
   private static final ModelHangedSkeleton witherSkeletonModel = new ModelHangedSkeleton(true, true);
   private static final ModelHangedVillager villagerModel = new ModelHangedVillager(true);
   private static final ModelHangedZombieVillager zombieVillagerModel = new ModelHangedZombieVillager(true);
   private static final ModelHangedWitch witchModel = new ModelHangedWitch(true);

   public ModelStocks() {
      this.textureWidth = 128;
      this.textureHeight = 64;
      this.horisontalPlank = new ModelRenderer(this, 0, 0);
      this.horisontalPlank.addBox(0.0F, 0.0F, 0.0F, 32, 12, 1);
      this.horisontalPlank.setRotationPoint(-16.0F, -2.0F, 0.0F);
      this.horisontalPlank.setTextureSize(128, this.textureHeight);
      this.verticalPlank1 = new ModelRenderer(this, 0, 13);
      this.verticalPlank1.addBox(0.0F, 0.0F, 0.0F, 3, 28, 2);
      this.verticalPlank1.setRotationPoint(-19.0F, -4.0F, -0.5F);
      this.verticalPlank1.setTextureSize(128, this.textureHeight);
      this.verticalPlank2 = new ModelRenderer(this, 11, 13);
      this.verticalPlank2.addBox(0.0F, 0.0F, 0.0F, 3, 28, 2);
      this.verticalPlank2.setRotationPoint(16.0F, -4.0F, -0.5F);
      this.verticalPlank2.setTextureSize(128, this.textureHeight);
      this.headHole = new ModelRenderer(this, 22, 13);
      this.headHole.addBox(0.0F, 0.0F, 0.0F, 6, 6, 1);
      this.headHole.setRotationPoint(-3.0F, 1.0F, 0.0F);
      this.headHole.setTextureSize(128, this.textureHeight);
      this.armHole1 = new ModelRenderer(this, 37, 13);
      this.armHole1.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1);
      this.armHole1.setRotationPoint(-11.0F, 3.0F, 0.0F);
      this.armHole1.setTextureSize(128, this.textureHeight);
      this.armHole2 = new ModelRenderer(this, 44, 13);
      this.armHole2.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1);
      this.armHole2.setRotationPoint(9.0F, 3.0F, 0.0F);
      this.armHole2.setTextureSize(128, this.textureHeight);
   }

   public void renderAll() {
      this.horisontalPlank.render(0.0625F);
      this.verticalPlank1.render(0.0625F);
      this.verticalPlank2.render(0.0625F);
      this.headHole.render(0.0625F);
      this.armHole1.render(0.0625F);
      this.armHole2.render(0.0625F);
   }

   public void customRender(EnumMemorials memorialType, EnumHangedMobs mob, int villagerProfession) {
      this.renderAll();
      if (mob != EnumHangedMobs.NONE) {
         GL11.glPushMatrix();
         GL11.glTranslatef(0.0F, 0.35F, 0.125F);
         GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
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
