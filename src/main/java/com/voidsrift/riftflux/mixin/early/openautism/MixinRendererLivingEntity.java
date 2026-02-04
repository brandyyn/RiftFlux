package com.voidsrift.riftflux.mixin.early.vortex;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import com.voidsrift.riftflux.vortex.lib.helper.EnchantHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Surrogate;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({RendererLivingEntity.class})
public abstract class MixinRendererLivingEntity {
   private static boolean hasCustomGlint;
   private static int customGlint;
   private static float[] customColors = new float[3];
   private static boolean doSubtract;
   private static boolean doNull;

   @Inject(
      method = {"doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V"},
      at = {@At(
   value = "INVOKE",
   target = "net/minecraft/client/renderer/entity/RendererLivingEntity.bindTexture(Lnet/minecraft/util/ResourceLocation;)V"
)},
      locals = LocalCapture.CAPTURE_FAILSOFT
   )
   private void onDoRender(EntityLivingBase par1Entity, double par2, double par4, double par6, float par8, float par9, CallbackInfo ci, boolean isShaders, float var10, float var11, float var13, float var26, float var14, float var15, float var16, int var17, int var18, float var19) {
      this.onDoRender(par1Entity, par2, par4, par6, par8, par9, ci, var10, var11, var13, var26, var14, var15, var16, var18, var19, var17);
   }

   @Surrogate
   private void onDoRender(EntityLivingBase p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_, CallbackInfo ci, float f2, float f3, float f4, float f13, float f5, float f6, float f7, int j, float f8, int i) {
      ItemStack itemStack = p_76986_1_.getEquipmentInSlot(4 - i);
      hasCustomGlint = false;
      doSubtract = false;
      doNull = false;
      if (itemStack != null && itemStack.hasTagCompound()) {
         hasCustomGlint = itemStack.getTagCompound().hasKey("customGlint");
         if (hasCustomGlint) {
            customGlint = itemStack.getTagCompound().getInteger("customGlint");
            switch(customGlint) {
            case 7:
               customColors[0] = 0.36F;
               customColors[1] = 0.36F;
               customColors[2] = 0.36F;
               doSubtract = true;
               break;
            case 8:
               customColors[0] = 0.36F;
               customColors[1] = 0.36F;
               customColors[2] = 0.36F;
               break;
            case 9:
            case 10:
            case 14:
            default:
               if (customGlint >= 0 && customGlint <= 15) {
                  customColors = EnchantHelper.generateColorsForGlint(ItemDye.field_150922_c[15 - customGlint]);
               } else {
                  customColors = EnchantHelper.generateColorsForGlint(customGlint);
               }
               break;
            case 11:
               customColors[0] = 0.72F;
               customColors[1] = 0.39F;
               customColors[2] = 0.02F;
               doSubtract = true;
               break;
            case 12:
               customColors[0] = 0.35F;
               customColors[1] = 0.48F;
               customColors[2] = 0.57F;
               doSubtract = true;
               break;
            case 13:
               customColors[0] = 0.54F;
               customColors[1] = 0.22F;
               customColors[2] = 0.57F;
               doSubtract = true;
               break;
            case 15:
               customColors[0] = 0.52F;
               customColors[1] = 0.52F;
               customColors[2] = 0.52F;
               doSubtract = true;
               break;
            case 16:
               customColors[0] = 0.0F;
               customColors[1] = 0.0F;
               customColors[2] = 0.0F;
               doNull = true;
            }
         }
      }

   }

   @Surrogate
   private void onDoRender(EntityLivingBase par1Entity, double par2, double par4, double par6, float par8, float par9, CallbackInfo ci, float f2, float f3, float f13, float f4, float f5, float f6, float f7, int i, int j, float f8) {
      this.onDoRender(par1Entity, par2, par4, par6, par8, par9, ci, f2, f3, f4, f13, f5, f6, f7, j, f8, i);
   }

   @Redirect(
      method = {"doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V"},
      at = @At(
   value = "INVOKE",
   target = "net/minecraft/client/model/ModelBase.render(Lnet/minecraft/entity/Entity;FFFFFF)V",
   ordinal = 2
)
   )
   private void onRender(ModelBase modelBase, Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
      if (hasCustomGlint) {
         if (!doNull) {
            if (doSubtract) {
               GL14.glBlendEquation(32779);
            }

            GL11.glColor4f(customColors[0], customColors[1], customColors[2], 1.0F);
            modelBase.render(p_78088_1_, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_);
            if (doSubtract) {
               GL14.glBlendEquation(32774);
            }
         }
      } else {
         modelBase.render(p_78088_1_, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_);
      }

   }
}
