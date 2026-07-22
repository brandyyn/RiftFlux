package gravestone.renderer.item;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.registry.VillagerRegistry;
import gravestone.core.Resources;
import gravestone.item.corpse.CatCorpseHelper;
import gravestone.item.corpse.HorseCorpseHelper;
import gravestone.item.corpse.VillagerCorpseHelper;
import gravestone.item.enums.EnumCorpse;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.model.ModelOcelot;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import org.lwjgl.opengl.GL11;

public class ItemGSCorpseRenderer implements IItemRenderer {
   private static final Map horsesTexturesMap = Maps.newHashMap();
   private static final ModelVillager villagerModel = new ModelVillager(0.0F);
   private static final ModelWolf dogModel = new ModelWolf();
   private static final ModelOcelot catModel = new ModelOcelot();
   private static final ModelHorse horseModel = new ModelHorse();
   private static EntityVillager villager;
   private static EntityWolf dog;
   private static EntityOcelot cat;
   private static EntityHorse horse;

   public boolean handleRenderType(ItemStack item, ItemRenderType type) {
      return true;
   }

   public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
      return true;
   }

   public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
      GL11.glPushMatrix();
      GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
      byte corpseType = (byte)item.getItemDamage();
      float xz = 0.0625F;
      switch(EnumCorpse.getById(corpseType)) {
      case VILLAGER:
         GL11.glTranslatef(0.0F, -0.5F, 0.0F);
         int profession = VillagerCorpseHelper.getVillagerType(item.getTagCompound());
         switch(profession) {
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
            Minecraft.getMinecraft().renderEngine.bindTexture(VillagerRegistry.getVillagerSkin(profession, Resources.VILLAGER));
         }

         if (villager == null) {
            villager = new EntityVillager(Minecraft.getMinecraft().theWorld);
         }

         villager.setProfession(profession);
         villagerModel.setLivingAnimations(villager, 0.0F, 0.0F, 0.0F);
         villagerModel.render(villager, xz, xz, xz, xz, xz, xz);
         break;
      case DOG:
         GL11.glTranslatef(0.0F, -1.0F, 0.0F);
         Minecraft.getMinecraft().renderEngine.bindTexture(Resources.WOLF);
         if (dog == null) {
            dog = new EntityWolf(Minecraft.getMinecraft().theWorld);
         }

         dogModel.setLivingAnimations(dog, 0.0F, 0.0F, 0.0F);
         dogModel.render(dog, xz, xz, xz, xz, xz, xz);
         break;
      case CAT:
         GL11.glTranslatef(0.0F, -1.0F, 0.0F);
         int catType = CatCorpseHelper.getCatType(item.getTagCompound());
         switch(catType) {
         case 0:
         default:
            Minecraft.getMinecraft().renderEngine.bindTexture(Resources.OCELOT);
            break;
         case 1:
            Minecraft.getMinecraft().renderEngine.bindTexture(Resources.BLACK_CAT);
            break;
         case 2:
            Minecraft.getMinecraft().renderEngine.bindTexture(Resources.RED_CAT);
            break;
         case 3:
            Minecraft.getMinecraft().renderEngine.bindTexture(Resources.SIAMESE_CAT);
         }

         if (cat == null) {
            cat = new EntityOcelot(Minecraft.getMinecraft().theWorld);
         }

         cat.setTameSkin(catType);
         catModel.setLivingAnimations(cat, 0.0F, 0.0F, 0.0F);
         catModel.render(cat, xz, xz, xz, xz, xz, xz);
         break;
      case HORSE:
         GL11.glTranslatef(0.0F, -0.6F, 0.0F);
         if (horse == null) {
            horse = new EntityHorse(Minecraft.getMinecraft().theWorld);
         }

         horse.setHorseType(HorseCorpseHelper.getHorseType(item.getTagCompound()));
         horse.setHorseVariant(HorseCorpseHelper.getHorseVariant(item.getTagCompound()));
         switch(HorseCorpseHelper.getHorseType(item.getTagCompound())) {
         case 0:
            String horseTexturePath = horse.getHorseTexture();
            ResourceLocation horseResourceLocation = (ResourceLocation)horsesTexturesMap.get(horseTexturePath);
            if (horseResourceLocation == null) {
               horseResourceLocation = new ResourceLocation(horseTexturePath);
               Minecraft.getMinecraft().getTextureManager().loadTexture(horseResourceLocation, new LayeredTexture(horse.getVariantTexturePaths()));
               horsesTexturesMap.put(horseTexturePath, horseResourceLocation);
            }

            Minecraft.getMinecraft().renderEngine.bindTexture(horseResourceLocation);
            break;
         case 1:
            Minecraft.getMinecraft().renderEngine.bindTexture(Resources.DONKEY);
            break;
         case 2:
            Minecraft.getMinecraft().renderEngine.bindTexture(Resources.MULE);
            break;
         case 3:
            Minecraft.getMinecraft().renderEngine.bindTexture(Resources.ZOMBIE_HORSE);
            break;
         case 4:
            Minecraft.getMinecraft().renderEngine.bindTexture(Resources.SKELETON_HORSE);
         }

         horseModel.setLivingAnimations(horse, 0.0F, 0.0F, 0.0F);
         horseModel.render(horse, xz, xz, xz, xz, xz, xz);
      }

      GL11.glPopMatrix();
   }
}
