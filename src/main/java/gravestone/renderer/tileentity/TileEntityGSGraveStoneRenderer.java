package gravestone.renderer.tileentity;

import com.google.common.collect.ImmutableMap;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.block.enums.EnumGraves;
import gravestone.config.GraveStoneConfig;
import gravestone.core.Resources;
import gravestone.models.block.ModelGraveStone;
import gravestone.models.block.graves.ModelCatStatueGraveStone;
import gravestone.models.block.graves.ModelCrossGraveStone;
import gravestone.models.block.graves.ModelDogStatueGraveStone;
import gravestone.models.block.graves.ModelHorisontalPlateGraveStone;
import gravestone.models.block.graves.ModelHorseGraveStone;
import gravestone.models.block.graves.ModelSwordGrave;
import gravestone.models.block.graves.ModelVerticalPlateGraveStone;
import gravestone.tileentity.TileEntityGSGraveStone;
import com.voidsrift.riftflux.vortex.lib.helper.EnchantHelper;
import java.util.Map;
import java.util.Random;
import java.util.IdentityHashMap;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

@SideOnly(Side.CLIENT)
public class TileEntityGSGraveStoneRenderer extends TileEntityGSRenderer {
   private static final ResourceLocation ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   public static ModelGraveStone verticalPlate = new ModelVerticalPlateGraveStone();
   public static ModelGraveStone cross = new ModelCrossGraveStone();
   public static ModelGraveStone horisontalPlate = new ModelHorisontalPlateGraveStone();
   public static ModelGraveStone dogStatue = new ModelDogStatueGraveStone();
   public static ModelGraveStone catStatue = new ModelCatStatueGraveStone();
   public static ModelGraveStone horseStatue = new ModelHorseGraveStone();
   public static ModelGraveStone swordGrave = new ModelSwordGrave();
   public static TileEntityGSGraveStoneRenderer instance;
   private static final Map<EnumGraves, ModelGraveStone> MODELS_MAP = ImmutableMap.<EnumGraves, ModelGraveStone>builder().put(EnumGraves.STONE_VERTICAL_PLATE, verticalPlate).put(EnumGraves.STONE_CROSS, cross).put(EnumGraves.STONE_HORISONTAL_PLATE, horisontalPlate).put(EnumGraves.STONE_DOG_STATUE, dogStatue).put(EnumGraves.STONE_CAT_STATUE, catStatue).put(EnumGraves.WOODEN_SWORD, swordGrave).put(EnumGraves.STONE_SWORD, swordGrave).put(EnumGraves.IRON_SWORD, swordGrave).put(EnumGraves.GOLDEN_SWORD, swordGrave).put(EnumGraves.DIAMOND_SWORD, swordGrave).put(EnumGraves.STONE_HORSE_STATUE, horseStatue).put(EnumGraves.WOODEN_VERTICAL_PLATE, verticalPlate).put(EnumGraves.SANDSTONE_VERTICAL_PLATE, verticalPlate).put(EnumGraves.IRON_VERTICAL_PLATE, verticalPlate).put(EnumGraves.GOLDEN_VERTICAL_PLATE, verticalPlate).put(EnumGraves.DIAMOND_VERTICAL_PLATE, verticalPlate).put(EnumGraves.EMERALD_VERTICAL_PLATE, verticalPlate).put(EnumGraves.LAPIS_VERTICAL_PLATE, verticalPlate).put(EnumGraves.REDSTONE_VERTICAL_PLATE, verticalPlate).put(EnumGraves.OBSIDIAN_VERTICAL_PLATE, verticalPlate).put(EnumGraves.QUARTZ_VERTICAL_PLATE, verticalPlate).put(EnumGraves.ICE_VERTICAL_PLATE, verticalPlate).put(EnumGraves.MOSSY_VERTICAL_PLATE, verticalPlate).put(EnumGraves.WOODEN_CROSS, cross).put(EnumGraves.SANDSTONE_CROSS, cross).put(EnumGraves.IRON_CROSS, cross).put(EnumGraves.GOLDEN_CROSS, cross).put(EnumGraves.DIAMOND_CROSS, cross).put(EnumGraves.EMERALD_CROSS, cross).put(EnumGraves.LAPIS_CROSS, cross).put(EnumGraves.REDSTONE_CROSS, cross).put(EnumGraves.OBSIDIAN_CROSS, cross).put(EnumGraves.QUARTZ_CROSS, cross).put(EnumGraves.ICE_CROSS, cross).put(EnumGraves.MOSSY_CROSS, cross).put(EnumGraves.WOODEN_HORISONTAL_PLATE, horisontalPlate).put(EnumGraves.SANDSTONE_HORISONTAL_PLATE, horisontalPlate).put(EnumGraves.IRON_HORISONTAL_PLATE, horisontalPlate).put(EnumGraves.GOLDEN_HORISONTAL_PLATE, horisontalPlate).put(EnumGraves.DIAMOND_HORISONTAL_PLATE, horisontalPlate).put(EnumGraves.EMERALD_HORISONTAL_PLATE, horisontalPlate).put(EnumGraves.LAPIS_HORISONTAL_PLATE, horisontalPlate).put(EnumGraves.REDSTONE_HORISONTAL_PLATE, horisontalPlate).put(EnumGraves.OBSIDIAN_HORISONTAL_PLATE, horisontalPlate).put(EnumGraves.QUARTZ_HORISONTAL_PLATE, horisontalPlate).put(EnumGraves.ICE_HORISONTAL_PLATE, horisontalPlate).put(EnumGraves.MOSSY_HORISONTAL_PLATE, horisontalPlate).put(EnumGraves.WOODEN_DOG_STATUE, dogStatue).put(EnumGraves.SANDSTONE_DOG_STATUE, dogStatue).put(EnumGraves.IRON_DOG_STATUE, dogStatue).put(EnumGraves.GOLDEN_DOG_STATUE, dogStatue).put(EnumGraves.DIAMOND_DOG_STATUE, dogStatue).put(EnumGraves.EMERALD_DOG_STATUE, dogStatue).put(EnumGraves.LAPIS_DOG_STATUE, dogStatue).put(EnumGraves.REDSTONE_DOG_STATUE, dogStatue).put(EnumGraves.OBSIDIAN_DOG_STATUE, dogStatue).put(EnumGraves.QUARTZ_DOG_STATUE, dogStatue).put(EnumGraves.ICE_DOG_STATUE, dogStatue).put(EnumGraves.MOSSY_DOG_STATUE, dogStatue).put(EnumGraves.WOODEN_CAT_STATUE, catStatue).put(EnumGraves.SANDSTONE_CAT_STATUE, catStatue).put(EnumGraves.IRON_CAT_STATUE, catStatue).put(EnumGraves.GOLDEN_CAT_STATUE, catStatue).put(EnumGraves.DIAMOND_CAT_STATUE, catStatue).put(EnumGraves.EMERALD_CAT_STATUE, catStatue).put(EnumGraves.LAPIS_CAT_STATUE, catStatue).put(EnumGraves.REDSTONE_CAT_STATUE, catStatue).put(EnumGraves.OBSIDIAN_CAT_STATUE, catStatue).put(EnumGraves.QUARTZ_CAT_STATUE, catStatue).put(EnumGraves.ICE_CAT_STATUE, catStatue).put(EnumGraves.MOSSY_CAT_STATUE, catStatue).put(EnumGraves.WOODEN_HORSE_STATUE, horseStatue).put(EnumGraves.SANDSTONE_HORSE_STATUE, horseStatue).put(EnumGraves.IRON_HORSE_STATUE, horseStatue).put(EnumGraves.GOLDEN_HORSE_STATUE, horseStatue).put(EnumGraves.DIAMOND_HORSE_STATUE, horseStatue).put(EnumGraves.EMERALD_HORSE_STATUE, horseStatue).put(EnumGraves.LAPIS_HORSE_STATUE, horseStatue).put(EnumGraves.REDSTONE_HORSE_STATUE, horseStatue).put(EnumGraves.OBSIDIAN_HORSE_STATUE, horseStatue).put(EnumGraves.QUARTZ_HORSE_STATUE, horseStatue).put(EnumGraves.ICE_HORSE_STATUE, horseStatue).put(EnumGraves.MOSSY_HORSE_STATUE, horseStatue).put(EnumGraves.SWORD, swordGrave).build();
   public static final Map<Item, ResourceLocation> swordsTextureMap = ImmutableMap.<Item, ResourceLocation>builder().put(Items.wooden_sword, Resources.GRAVE_WOODEN_SWORD).put(Items.stone_sword, Resources.GRAVE_STONE_SWORD).put(Items.iron_sword, Resources.GRAVE_IRON_SWORD).put(Items.golden_sword, Resources.GRAVE_GOLDEN_SWORD).put(Items.diamond_sword, Resources.GRAVE_DIAMOND_SWORD).build();
   public static final Map<ItemStack, Object[]> EntityItemMap = new ConcurrentHashMap<>(500);
   public static int renderFlower_calls = 0;
   public static Random random = new Random();
   public static RenderBlocks field_147909_c = new RenderBlocks();
   private static final Map<ModelGraveStone, Integer> STATIC_MODEL_LISTS = new IdentityHashMap<ModelGraveStone, Integer>();

   public TileEntityGSGraveStoneRenderer() {
      instance = this;
   }

   public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
      TileEntityGSGraveStone tileEntity = (TileEntityGSGraveStone)te;
      EnumGraves graveType = tileEntity.getGraveType();
      int meta = 0;
      if (tileEntity.getWorldObj() != null) {
         meta = tileEntity.getBlockMetadata();
      }

      if (graveType != EnumGraves.SWORD) {
         this.bindTextureByName(graveType.getTexture());
      }

      GL11.glPushMatrix();
      if (tileEntity.getWorldObj() == null && tileEntity.isSwordGrave()) {
         GL11.glTranslatef((float)x + 0.5F, (float)y + 2.0F, (float)z + 0.5F);
         GL11.glScalef(1.5F, -1.5F, -1.5F);
      } else {
         GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);
         GL11.glScalef(1.0F, -1.0F, -1.0F);
      }

      if (meta == 0) {
         GL11.glRotatef(0.0F, 0.0F, 1.0F, 0.0F);
      } else if (meta == 3) {
         GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
      } else if (meta == 2) {
         GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
      } else {
         GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
      }

      if (tileEntity.isSwordGrave()) {
         if (GraveStoneConfig.vanillaRendererForSwordsGraves) {
            this.renderSword(tileEntity);
         } else {
            this.bindTextureByName(swordsTextureMap.get(tileEntity.getSword().getItem()));
            if (tileEntity.isEnchanted()) {
               MODELS_MAP.get(graveType).renderEnchanted();
            } else {
               MODELS_MAP.get(graveType).renderAll();
            }
         }
      } else {
         if (tileEntity.isEnchanted()) {
            MODELS_MAP.get(graveType).renderEnchanted();
         } else {
            renderStaticModel(MODELS_MAP.get(graveType));
         }

         if (tileEntity.hasFlower() && GraveStoneConfig.renderGravesFlowers) {
            this.renderFlower(tileEntity);
         }
      }

      GL11.glPopMatrix();
   }

   private void renderSword(TileEntityGSGraveStone te) {
      ItemStack sword = te.getSword();
      ItemStack renderSword = sword;
      if (te.isEnchanted() && !renderSword.isItemEnchanted()) {
         renderSword = renderSword.copy();
         if (!renderSword.hasTagCompound()) {
            renderSword.setTagCompound(new NBTTagCompound());
         }

         renderSword.getTagCompound().setTag("ench", new NBTTagList());
      }

      net.minecraft.world.World renderWorld = te.getWorldObj() != null
              ? te.getWorldObj()
              : net.minecraft.client.Minecraft.getMinecraft().theWorld;
      GL11.glTranslatef(0.24F, 0.83F, 0.0F);
      GL11.glScalef(1.5F, -1.5F, -1.5F);
      GL11.glRotatef(135.0F, 0.0F, 0.0F, 1.0F);

      GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
      GL11.glEnable(GL11.GL_TEXTURE_2D);
      GL11.glEnable(GL11.GL_ALPHA_TEST);
      GL11.glEnable(GL11.GL_DEPTH_TEST);
      GL11.glDepthMask(true);
      GL11.glDepthFunc(GL11.GL_LEQUAL);
      GL11.glDisable(GL11.GL_BLEND);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      // Sword icons are thin, two-sided sprites. Fixed GUI lights make one face bright and
      // the opposite face dark regardless of the grave's world lighting. The lightmap still
      // supplies the local block/sky brightness while disabling directional GL lights keeps
      // both faces equally lit and also makes inventory rendering independent of GUI state.
      GL11.glDisable(GL11.GL_LIGHTING);

      IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(renderSword, IItemRenderer.ItemRenderType.ENTITY);
      if (renderSword.isItemEnchanted() && customRenderer == null) {
         this.renderVanillaEnchantedSword(renderSword);
      } else {
         this.renderSwordEntity(renderWorld, renderSword);
      }
      GL11.glPopAttrib();
   }

   private void renderVanillaEnchantedSword(ItemStack sword) {
      TextureManager textures = net.minecraft.client.Minecraft.getMinecraft().getTextureManager();
      textures.bindTexture(textures.getResourceLocation(sword.getItemSpriteNumber()));
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 0.1F, 0.0F);
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      GL11.glTranslatef(-0.5F, -0.25F, 0.0421875F);
      int passes = sword.getItem().requiresMultipleRenderPasses()
              ? sword.getItem().getRenderPasses(sword.getItemDamage()) : 1;

      for (int pass = 0; pass < passes; ++pass) {
         IIcon icon = sword.getItem().requiresMultipleRenderPasses()
                 ? sword.getItem().getIcon(sword, pass) : sword.getIconIndex();
         if (icon == null) {
            continue;
         }
         int color = sword.getItem().getColorFromItemStack(sword, pass);
         float red = (float)(color >> 16 & 255) / 255.0F;
         float green = (float)(color >> 8 & 255) / 255.0F;
         float blue = (float)(color & 255) / 255.0F;
         GL11.glColor4f(red, green, blue, 1.0F);
         ItemRenderer.renderItemIn2D(Tessellator.instance, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(),
                 icon.getIconWidth(), icon.getIconHeight(), 0.0625F);
      }

      GL11.glDepthFunc(GL11.GL_EQUAL);
      GL11.glDisable(GL11.GL_LIGHTING);
      textures.bindTexture(ITEM_GLINT);
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
      GlintColor glint = getSwordGlintColor(sword);
      if (glint.render) {
         GL11.glColorMask(true, true, true, false);
         if (glint.subtractive) {
            GL14.glBlendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);
         }
         GL11.glColor4f(glint.red, glint.green, glint.blue, 1.0F);
         GL11.glMatrixMode(GL11.GL_TEXTURE);
         renderSwordGlintPass(3000L, -50.0F, false);
         renderSwordGlintPass(4873L, 10.0F, true);
         GL11.glMatrixMode(GL11.GL_MODELVIEW);
         if (glint.subtractive) {
            GL14.glBlendEquation(GL14.GL_FUNC_ADD);
         }
         GL11.glColorMask(true, true, true, true);
      }
      GL11.glDisable(GL11.GL_BLEND);
      GL11.glEnable(GL11.GL_LIGHTING);
      GL11.glDepthFunc(GL11.GL_LEQUAL);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPopMatrix();
   }

   private static GlintColor getSwordGlintColor(ItemStack sword) {
      if (!sword.hasTagCompound() || !sword.getTagCompound().hasKey(EnchantHelper.CUSTOM_GLINT_TAG)) {
         return new GlintColor(true, false, 0.38F, 0.19F, 0.608F);
      }
      int value = sword.getTagCompound().getInteger(EnchantHelper.CUSTOM_GLINT_TAG);
      switch (value) {
         case 7:
            return new GlintColor(true, true, 0.36F, 0.36F, 0.36F);
         case 8:
            return new GlintColor(true, false, 0.36F, 0.36F, 0.36F);
         case 11:
            return new GlintColor(true, true, 0.72F, 0.39F, 0.02F);
         case 12:
            return new GlintColor(true, true, 0.35F, 0.48F, 0.57F);
         case 13:
            return new GlintColor(true, true, 0.54F, 0.22F, 0.57F);
         case 15:
            return new GlintColor(true, true, 0.52F, 0.52F, 0.52F);
         case 16:
            return new GlintColor(false, false, 0.0F, 0.0F, 0.0F);
         default:
            int color = value >= 0 && value <= 15 ? ItemDye.field_150922_c[15 - value] : value;
            float[] colors = EnchantHelper.generateColorsForGlint(color);
            return new GlintColor(true, false, colors[0], colors[1], colors[2]);
      }
   }

   private static final class GlintColor {
      private final boolean render;
      private final boolean subtractive;
      private final float red;
      private final float green;
      private final float blue;

      private GlintColor(boolean render, boolean subtractive, float red, float green, float blue) {
         this.render = render;
         this.subtractive = subtractive;
         this.red = red;
         this.green = green;
         this.blue = blue;
      }
   }

   private static void renderSwordGlintPass(long period, float rotation, boolean reverse) {
      GL11.glPushMatrix();
      GL11.glScalef(0.125F, 0.125F, 0.125F);
      float offset = (float)(net.minecraft.client.Minecraft.getSystemTime() % period) / (float)period * 8.0F;
      GL11.glTranslatef(reverse ? -offset : offset, 0.0F, 0.0F);
      GL11.glRotatef(rotation, 0.0F, 0.0F, 1.0F);
      ItemRenderer.renderItemIn2D(Tessellator.instance, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, 0.0625F);
      GL11.glPopMatrix();
   }

   private void renderSwordEntity(net.minecraft.world.World world, ItemStack sword) {
      EntityItem entityitem = new EntityItem(world, 0.0D, 0.0D, 0.0D, sword);
      entityitem.hoverStart = 0.0F;
      this.renderItem(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
   }

   private static void renderStaticModel(ModelGraveStone model) {
      Integer displayList = STATIC_MODEL_LISTS.get(model);
      if (displayList == null) {
         // ModelRenderer lazily creates its own lists. Warm those before compiling our aggregate list.
         model.renderAll();
         displayList = GLAllocation.generateDisplayLists(1);
         GL11.glNewList(displayList, GL11.GL_COMPILE);
         model.renderAll();
         GL11.glEndList();
         STATIC_MODEL_LISTS.put(model, displayList);
         return;
      }
      GL11.glCallList(displayList);
   }

   private void renderFlower(TileEntityGSGraveStone te) {
      ItemStack flowerStack = te.getFlower();
      Block flower = Block.getBlockFromItem(flowerStack.getItem());
      if (flower == null || flower == net.minecraft.init.Blocks.air) {
         return;
      }
      IIcon icon = flower.getIcon(0, flowerStack.getItemDamage());
      if (icon == null) {
         return;
      }

      this.bindTextureByName(TextureMap.locationBlocksTexture);
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 1.45F, -0.1F);
      GL11.glScalef(0.5F, -0.5F, -0.5F);
      int color = flower.getRenderColor(flowerStack.getItemDamage());
      float red = (float)(color >> 16 & 255) / 255.0F;
      float green = (float)(color >> 8 & 255) / 255.0F;
      float blue = (float)(color & 255) / 255.0F;
      GL11.glColor4f(red, green, blue, 1.0F);
      boolean lightingEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING);
      GL11.glDisable(GL11.GL_LIGHTING);
      Tessellator tessellator = Tessellator.instance;
      tessellator.startDrawingQuads();
      int brightness = te.getWorldObj() == null ? 15728880
              : flower.getMixedBrightnessForBlock(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
      tessellator.setBrightness(brightness);
      tessellator.setColorOpaque_F(red, green, blue);
      field_147909_c.drawCrossedSquares(icon, -0.5D, 0.0D, -0.5D, 1.0F);
      tessellator.draw();
      if (lightingEnabled) {
         GL11.glEnable(GL11.GL_LIGHTING);
      }
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPopMatrix();
   }

   public void renderItem(Entity p_147939_1_, double p_147939_2_, double p_147939_4_, double p_147939_6_, float p_147939_8_, float p_147939_9_) {
      Render render = null;

      try {
         render = RenderManager.instance.getEntityClassRenderObject(p_147939_1_.getClass());
         if (render != null && !render.isStaticEntity()) {
            render.doRender(p_147939_1_, p_147939_2_, p_147939_4_, p_147939_6_, p_147939_8_, p_147939_9_);
         }
      } catch (Throwable var12) {
         FMLLog.log(Level.WARN, var12, "GraveStone stacktrace: %s", var12);
      }

   }

   public void doRender(Object[] objects, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      EntityItem p_76986_1_ = (EntityItem)objects[0];
      boolean[] renderValues = (boolean[])objects[1];
      ItemStack itemstack = p_76986_1_.getEntityItem();
      if (itemstack.getItem() != null) {
         TextureManager re = RenderManager.instance.renderEngine;
         int gISN = itemstack.getItemSpriteNumber();
         re.bindTexture(re.getResourceLocation(gISN));
         TextureUtil.func_152777_a(false, false, 1.0F);
         random.setSeed(187L);
         GL11.glPushMatrix();
         float f2 = 0.0F;
         float f3 = 6.1520865E9F;
         byte b0 = 1;
         GL11.glTranslatef((float)p_76986_2_, f2, (float)p_76986_6_);
         GL11.glEnable(32826);
         if (!ForgeHooksClient.renderEntityItem(p_76986_1_, itemstack, f2, f3, random, re, field_147909_c, b0)) {
            if (itemstack.getItem().requiresMultipleRenderPasses()) {
               if (renderValues[0]) {
                  GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
                  GL11.glTranslatef(0.0F, -0.05F, 0.0F);
               } else {
                  GL11.glScalef(0.5F, 0.5F, 0.5F);
               }

               for(int j = 0; j < itemstack.getItem().getRenderPasses(itemstack.getItemDamage()); ++j) {
                  random.setSeed(187L);
                  IIcon iicon1 = itemstack.getItem().getIcon(itemstack, j);
                  if (renderValues[1]) {
                     int k = itemstack.getItem().getColorFromItemStack(itemstack, j);
                     float f5 = (float)(k >> 16 & 255) / 255.0F;
                     float f6 = (float)(k >> 8 & 255) / 255.0F;
                     float f7 = (float)(k & 255) / 255.0F;
                     GL11.glColor4f(f5, f6, f7, 1.0F);
                     this.renderDroppedItem(iicon1, f5, f6, f7);
                  } else {
                     this.renderDroppedItem(iicon1, 1.0F, 1.0F, 1.0F);
                  }
               }
            } else {
               if (renderValues[0]) {
                  GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
                  GL11.glTranslatef(0.0F, -0.05F, 0.0F);
               } else {
                  GL11.glScalef(0.5F, 0.5F, 0.5F);
               }

               IIcon iicon = itemstack.getIconIndex();
               if (renderValues[1]) {
                  int i = itemstack.getItem().getColorFromItemStack(itemstack, 0);
                  float f4 = (float)(i >> 16 & 255) / 255.0F;
                  float f5 = (float)(i >> 8 & 255) / 255.0F;
                  float f6 = (float)(i & 255) / 255.0F;
                  this.renderDroppedItem(iicon, f4, f5, f6);
               } else {
                  this.renderDroppedItem(iicon, 1.0F, 1.0F, 1.0F);
               }
            }
         }

         GL11.glDisable(32826);
         GL11.glPopMatrix();
         re.bindTexture(re.getResourceLocation(gISN));
         TextureUtil.func_147945_b();
      }

   }

   public void renderDroppedItem(IIcon p_77020_2_, float p_77020_5_, float p_77020_6_, float p_77020_7_) {
      Tessellator tessellator = Tessellator.instance;
      float f14 = p_77020_2_.getMinU();
      float f15 = p_77020_2_.getMaxU();
      float f4 = p_77020_2_.getMinV();
      float f5 = p_77020_2_.getMaxV();
      GL11.glPushMatrix();
      GL11.glColor4f(p_77020_5_, p_77020_6_, p_77020_7_, 1.0F);
      tessellator.startDrawingQuads();
      tessellator.setNormal(0.0F, 1.0F, 0.0F);
      tessellator.addVertexWithUV(-0.5D, -0.25D, 0.0D, (double)f14, (double)f5);
      tessellator.addVertexWithUV(0.5D, -0.25D, 0.0D, (double)f15, (double)f5);
      tessellator.addVertexWithUV(0.5D, 0.75D, 0.0D, (double)f15, (double)f4);
      tessellator.addVertexWithUV(-0.5D, 0.75D, 0.0D, (double)f14, (double)f4);
      tessellator.draw();
      GL11.glPopMatrix();
   }
}
