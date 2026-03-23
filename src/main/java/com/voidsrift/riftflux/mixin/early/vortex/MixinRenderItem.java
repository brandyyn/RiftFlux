package com.voidsrift.riftflux.mixin.early.vortex;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import com.voidsrift.riftflux.vortex.lib.helper.EnchantHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({RenderItem.class})
public abstract class MixinRenderItem {
   private static boolean hasCustomGlintGuiStack;
   private static int customGlintGuiStack;
   private static float[] customColorsGuiStack = new float[3];
   private static boolean doNullGuiStack;
   private static boolean doSubtractGuiStack;
   private static boolean hasCustomGlintDroppedStack;
   private static int customGlintDroppedStack;
   private static float[] customColorsDroppedStack = new float[3];
   private static boolean doSubtractDroppedStack;
   private static boolean doNullDroppedStack;

   @Inject(
      method = {"renderItemIntoGUI(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/item/ItemStack;IIZ)V"},
      at = {@At("HEAD")},
      remap = false
   )
   public void onRenderItemIntoGUI(FontRenderer fontRenderer, TextureManager textureManager, ItemStack itemStack, int p_77015_4_, int p_77015_5_, boolean renderEffect, CallbackInfo ci) {
      hasCustomGlintGuiStack = false;
      doSubtractGuiStack = false;
      doNullGuiStack = false;
      if (itemStack != null && renderEffect && itemStack.hasTagCompound()) {
         hasCustomGlintGuiStack = itemStack.getTagCompound().hasKey("customGlint");
         if (hasCustomGlintGuiStack) {
            customGlintGuiStack = itemStack.getTagCompound().getInteger("customGlint");
            switch(customGlintGuiStack) {
            case 7:
               customColorsGuiStack[0] = 0.36F;
               customColorsGuiStack[1] = 0.36F;
               customColorsGuiStack[2] = 0.36F;
               doSubtractGuiStack = true;
               break;
            case 8:
               customColorsGuiStack[0] = 0.36F;
               customColorsGuiStack[1] = 0.36F;
               customColorsGuiStack[2] = 0.36F;
               break;
            case 9:
            case 10:
            case 14:
            default:
               if (customGlintGuiStack >= 0 && customGlintGuiStack <= 15) {
                  customColorsGuiStack = EnchantHelper.generateColorsForGlint(ItemDye.field_150922_c[15 - customGlintGuiStack]);
               } else {
                  customColorsGuiStack = EnchantHelper.generateColorsForGlint(customGlintGuiStack);
               }
               break;
            case 11:
               customColorsGuiStack[0] = 0.72F;
               customColorsGuiStack[1] = 0.39F;
               customColorsGuiStack[2] = 0.02F;
               doSubtractGuiStack = true;
               break;
            case 12:
               customColorsGuiStack[0] = 0.35F;
               customColorsGuiStack[1] = 0.48F;
               customColorsGuiStack[2] = 0.57F;
               doSubtractGuiStack = true;
               break;
            case 13:
               customColorsGuiStack[0] = 0.54F;
               customColorsGuiStack[1] = 0.22F;
               customColorsGuiStack[2] = 0.57F;
               doSubtractGuiStack = true;
               break;
            case 15:
               customColorsGuiStack[0] = 0.52F;
               customColorsGuiStack[1] = 0.52F;
               customColorsGuiStack[2] = 0.52F;
               doSubtractGuiStack = true;
               break;
            case 16:
               customColorsDroppedStack[0] = 0.0F;
               customColorsDroppedStack[1] = 0.0F;
               customColorsDroppedStack[2] = 0.0F;
               doNullGuiStack = true;
            }
         }
      }

   }

   @Inject(
      method = {"renderEffect"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   public void onRenderEffect(TextureManager manager, int x, int y, CallbackInfo ci) {
      if (doNullGuiStack) {
         ci.cancel();
      }

   }

   @Redirect(
      method = {"renderGlint"},
      at = @At(
   value = "INVOKE",
   target = "net/minecraft/client/renderer/Tessellator.draw()I"
)
   )
   public int onDraw(Tessellator tessellator) {
      if (hasCustomGlintGuiStack) {
         beginCustomGlintPass(doSubtractGuiStack);
         GL11.glColor4f(customColorsGuiStack[0], customColorsGuiStack[1], customColorsGuiStack[2], 1.0F);
         int result = tessellator.draw();
         endCustomGlintPass(doSubtractGuiStack);
         return result;
      } else {
         return tessellator.draw();
      }
   }

   @Inject(
      method = {"renderDroppedItem(Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/util/IIcon;IFFFFI)V"},
      at = {@At(
   value = "INVOKE",
   target = "org/lwjgl/opengl/GL11.glTranslatef(FFF)V",
   ordinal = 0
)},
      locals = LocalCapture.CAPTURE_FAILSOFT,
      remap = false
   )
   public void onRenderDroppedItem(EntityItem p_77020_1_, IIcon p_77020_2_, int p_77020_3_, float p_77020_4_, float p_77020_5_, float p_77020_6_, float p_77020_7_, int pass, CallbackInfo ci, Tessellator tessellator, float f14, float f15, float f4, float f5, float f6, float f7, float f8, float f10, float f9, ItemStack itemStack) {
      hasCustomGlintDroppedStack = false;
      doSubtractDroppedStack = false;
      doNullDroppedStack = false;
      if (itemStack != null && itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("customGlint")) {
         hasCustomGlintDroppedStack = itemStack.getTagCompound().hasKey("customGlint");
         if (hasCustomGlintDroppedStack) {
            customGlintDroppedStack = itemStack.getTagCompound().getInteger("customGlint");
            switch(customGlintDroppedStack) {
            case 7:
               customColorsDroppedStack[0] = 0.36F;
               customColorsDroppedStack[1] = 0.36F;
               customColorsDroppedStack[2] = 0.36F;
               doSubtractDroppedStack = true;
               break;
            case 8:
               customColorsDroppedStack[0] = 0.36F;
               customColorsDroppedStack[1] = 0.36F;
               customColorsDroppedStack[2] = 0.36F;
               break;
            case 9:
            case 10:
            case 14:
            default:
               if (customGlintDroppedStack >= 0 && customGlintDroppedStack <= 15) {
                  customColorsDroppedStack = EnchantHelper.generateColorsForGlint(ItemDye.field_150922_c[15 - customGlintDroppedStack]);
               } else {
                  customColorsDroppedStack = EnchantHelper.generateColorsForGlint(customGlintDroppedStack);
               }
               break;
            case 11:
               customColorsDroppedStack[0] = 0.72F;
               customColorsDroppedStack[1] = 0.39F;
               customColorsDroppedStack[2] = 0.02F;
               doSubtractDroppedStack = true;
               break;
            case 12:
               customColorsDroppedStack[0] = 0.35F;
               customColorsDroppedStack[1] = 0.48F;
               customColorsDroppedStack[2] = 0.57F;
               doSubtractDroppedStack = true;
               break;
            case 13:
               customColorsDroppedStack[0] = 0.54F;
               customColorsDroppedStack[1] = 0.22F;
               customColorsDroppedStack[2] = 0.57F;
               doSubtractDroppedStack = true;
               break;
            case 15:
               customColorsDroppedStack[0] = 0.52F;
               customColorsDroppedStack[1] = 0.52F;
               customColorsDroppedStack[2] = 0.52F;
               doSubtractDroppedStack = true;
               break;
            case 16:
               customColorsDroppedStack[0] = 0.0F;
               customColorsDroppedStack[1] = 0.0F;
               customColorsDroppedStack[2] = 0.0F;
               doNullDroppedStack = true;
            }
         }
      }

   }

   @Redirect(
      slice = @Slice(
   from = @At(
   value = "INVOKE",
   target = "net/minecraft/item/ItemStack.hasEffect(I)Z"
)
),
      method = {"renderDroppedItem(Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/util/IIcon;IFFFFI)V"},
      at = @At(
   value = "INVOKE",
   target = "net/minecraft/client/renderer/ItemRenderer.renderItemIn2D(Lnet/minecraft/client/renderer/Tessellator;FFFFIIF)V"
)
   )
   public void onRenderItemIn2D(Tessellator p_78439_0_, float p_78439_1_, float p_78439_2_, float p_78439_3_, float p_78439_4_, int p_78439_5_, int p_78439_6_, float p_78439_7_) {
      if (hasCustomGlintDroppedStack) {
         if (!doNullDroppedStack) {
            beginCustomGlintPass(doSubtractDroppedStack);
            GL11.glColor4f(customColorsDroppedStack[0], customColorsDroppedStack[1], customColorsDroppedStack[2], 1.0F);
            ItemRenderer.renderItemIn2D(p_78439_0_, p_78439_1_, p_78439_2_, p_78439_3_, p_78439_4_, p_78439_5_, p_78439_6_, p_78439_7_);
            endCustomGlintPass(doSubtractDroppedStack);
         }
      } else {
         ItemRenderer.renderItemIn2D(p_78439_0_, p_78439_1_, p_78439_2_, p_78439_3_, p_78439_4_, p_78439_5_, p_78439_6_, p_78439_7_);
      }

   }

   private static void beginCustomGlintPass(boolean subtractive) {
      GL11.glColorMask(true, true, true, false);
      if (subtractive) {
         GL14.glBlendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);
      }
   }

   private static void endCustomGlintPass(boolean subtractive) {
      if (subtractive) {
         GL14.glBlendEquation(GL14.GL_FUNC_ADD);
      }
      GL11.glColorMask(true, true, true, true);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
