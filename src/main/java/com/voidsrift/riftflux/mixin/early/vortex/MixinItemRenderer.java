package com.voidsrift.riftflux.mixin.early.vortex;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import com.voidsrift.riftflux.vortex.lib.helper.EnchantHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemRenderer.class})
public abstract class MixinItemRenderer {
   private static boolean hasCustomGlint;
   private static int customGlint;
   private static float[] customColors = new float[3];
   private static boolean doSubtract;
   private static boolean doNull;

   @Inject(
      method = {"renderItem(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;ILnet/minecraftforge/client/IItemRenderer$ItemRenderType;)V"},
      at = {@At("HEAD")},
      remap = false
   )
   public void onRenderItem(EntityLivingBase p_78443_1_, ItemStack p_78443_2_, int p_78443_3_, ItemRenderType type, CallbackInfo ci) {
      hasCustomGlint = false;
      doSubtract = false;
      doNull = false;
      if (p_78443_2_ != null && p_78443_2_.hasTagCompound() && p_78443_2_.getTagCompound().hasKey("customGlint")) {
         hasCustomGlint = p_78443_2_.getTagCompound().hasKey("customGlint");
         if (hasCustomGlint) {
            customGlint = p_78443_2_.getTagCompound().getInteger("customGlint");
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

   @Redirect(
      slice = @Slice(
   from = @At(
   value = "INVOKE",
   target = "net/minecraft/item/ItemStack.hasEffect(I)Z"
)
),
      method = {"renderItem(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;ILnet/minecraftforge/client/IItemRenderer$ItemRenderType;)V"},
      at = @At(
   value = "INVOKE",
   target = "net/minecraft/client/renderer/ItemRenderer.renderItemIn2D(Lnet/minecraft/client/renderer/Tessellator;FFFFIIF)V"
)
   )
   public void onRenderItemIn2D(Tessellator p_78439_0_, float p_78439_1_, float p_78439_2_, float p_78439_3_, float p_78439_4_, int p_78439_5_, int p_78439_6_, float p_78439_7_) {
      if (hasCustomGlint) {
         if (!doNull) {
            beginCustomGlintPass(doSubtract);
            GL11.glColor4f(customColors[0], customColors[1], customColors[2], 1.0F);
            ItemRenderer.renderItemIn2D(p_78439_0_, p_78439_1_, p_78439_2_, p_78439_3_, p_78439_4_, p_78439_5_, p_78439_6_, p_78439_7_);
            endCustomGlintPass(doSubtract);
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
