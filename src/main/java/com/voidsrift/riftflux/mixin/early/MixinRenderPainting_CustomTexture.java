package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.painting.CustomPaintingRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderPainting;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderPainting.class)
public abstract class MixinRenderPainting_CustomTexture {

    private static final ResourceLocation VANILLA_PAINTINGS =
            new ResourceLocation("textures/painting/paintings_kristoffer_zetterstrand.png");

    @Shadow
    private void func_77008_a(EntityPainting painting, float offsetX, float offsetY) {
    }

    @Inject(
            method = "getEntityTexture(Lnet/minecraft/entity/item/EntityPainting;)Lnet/minecraft/util/ResourceLocation;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void riftflux$useCustomTexture(EntityPainting painting, CallbackInfoReturnable<ResourceLocation> cir) {
        if (painting == null || painting.art == null) {
            return;
        }

        ResourceLocation customTexture = CustomPaintingRegistry.getCustomTexture(painting.art);
        if (customTexture != null) {
            cir.setReturnValue(customTexture);
        }
    }

    @Inject(
            method = "func_77010_a(Lnet/minecraft/entity/item/EntityPainting;IIII)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void riftflux$renderCustomWithVanillaBack(EntityPainting painting,
                                                      int sizeX,
                                                      int sizeY,
                                                      int offsetX,
                                                      int offsetY,
                                                      CallbackInfo ci) {
        if (painting == null || painting.art == null) {
            return;
        }

        ResourceLocation customTexture = CustomPaintingRegistry.getCustomTexture(painting.art);
        if (customTexture == null) {
            return;
        }

        boolean fullTexture = CustomPaintingRegistry.usesFullTexture(painting.art);
        int textureWidth = fullTexture ? sizeX : 256;
        int textureHeight = fullTexture ? sizeY : 256;
        int baseOffsetX = fullTexture ? 0 : offsetX;
        int baseOffsetY = fullTexture ? 0 : offsetY;
        float invTextureWidth = 1.0F / (float) textureWidth;
        float invTextureHeight = 1.0F / (float) textureHeight;
        float borderU = invTextureWidth;
        float borderV = invTextureHeight;

        float f = (float)(-sizeX) / 2.0F;
        float f1 = (float)(-sizeY) / 2.0F;
        float f2 = 0.5F;
        float f3 = 0.75F;
        float f4 = 0.8125F;
        float f5 = 0.0F;
        float f6 = 0.0625F;
        float f7 = 0.75F;
        float f8 = 0.8125F;
        float f9 = 0.001953125F;
        float f10 = 0.001953125F;
        float f11 = 0.7519531F;
        float f12 = 0.7519531F;
        float f13 = 0.0F;
        float f14 = 0.0625F;

        for (int i1 = 0; i1 < sizeX / 16; ++i1) {
            for (int j1 = 0; j1 < sizeY / 16; ++j1) {
                float f15 = f + (float)((i1 + 1) * 16);
                float f16 = f + (float)(i1 * 16);
                float f17 = f1 + (float)((j1 + 1) * 16);
                float f18 = f1 + (float)(j1 * 16);
                this.func_77008_a(painting, (f15 + f16) / 2.0F, (f17 + f18) / 2.0F);
                float f19 = (float)(baseOffsetX + sizeX - i1 * 16) * invTextureWidth;
                float f20 = (float)(baseOffsetX + sizeX - (i1 + 1) * 16) * invTextureWidth;
                float f21 = (float)(baseOffsetY + sizeY - j1 * 16) * invTextureHeight;
                float f22 = (float)(baseOffsetY + sizeY - (j1 + 1) * 16) * invTextureHeight;

                Minecraft.getMinecraft().getTextureManager().bindTexture(customTexture);
                Tessellator tessellator = Tessellator.instance;
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 0.0F, -1.0F);
                tessellator.addVertexWithUV((double)f15, (double)f18, (double)(-f2), (double)f20, (double)f21);
                tessellator.addVertexWithUV((double)f16, (double)f18, (double)(-f2), (double)f19, (double)f21);
                tessellator.addVertexWithUV((double)f16, (double)f17, (double)(-f2), (double)f19, (double)f22);
                tessellator.addVertexWithUV((double)f15, (double)f17, (double)(-f2), (double)f20, (double)f22);
                tessellator.draw();

                Minecraft.getMinecraft().getTextureManager().bindTexture(VANILLA_PAINTINGS);
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 0.0F, 1.0F);
                tessellator.addVertexWithUV((double)f15, (double)f17, (double)f2, (double)f3, (double)f5);
                tessellator.addVertexWithUV((double)f16, (double)f17, (double)f2, (double)f4, (double)f5);
                tessellator.addVertexWithUV((double)f16, (double)f18, (double)f2, (double)f4, (double)f6);
                tessellator.addVertexWithUV((double)f15, (double)f18, (double)f2, (double)f3, (double)f6);
                tessellator.draw();

                float uLeft = f20;
                float uRight = f19;
                float vTop = f22;
                float vBottom = f21;

                float artU0 = (float) baseOffsetX * invTextureWidth;
                float artU1 = (float) (baseOffsetX + sizeX) * invTextureWidth;
                float artV0 = (float) baseOffsetY * invTextureHeight;
                float artV1 = (float) (baseOffsetY + sizeY) * invTextureHeight;

                float uLeftEdge0 = artU1 - borderU;
                float uLeftEdge1 = artU1;
                float uRightEdge0 = artU0;
                float uRightEdge1 = artU0 + borderU;
                float vTopEdge0 = artV0;
                float vTopEdge1 = artV0 + borderV;
                float vBottomEdge0 = artV1 - borderV;
                float vBottomEdge1 = artV1;

                Minecraft.getMinecraft().getTextureManager().bindTexture(customTexture);
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 1.0F, 0.0F);
                tessellator.addVertexWithUV((double)f15, (double)f17, (double)(-f2), (double)uLeft, (double)vTopEdge0);
                tessellator.addVertexWithUV((double)f16, (double)f17, (double)(-f2), (double)uRight, (double)vTopEdge0);
                tessellator.addVertexWithUV((double)f16, (double)f17, (double)f2, (double)uRight, (double)vTopEdge1);
                tessellator.addVertexWithUV((double)f15, (double)f17, (double)f2, (double)uLeft, (double)vTopEdge1);
                tessellator.setNormal(0.0F, -1.0F, 0.0F);
                tessellator.addVertexWithUV((double)f15, (double)f18, (double)f2, (double)uLeft, (double)vBottomEdge0);
                tessellator.addVertexWithUV((double)f16, (double)f18, (double)f2, (double)uRight, (double)vBottomEdge0);
                tessellator.addVertexWithUV((double)f16, (double)f18, (double)(-f2), (double)uRight, (double)vBottomEdge1);
                tessellator.addVertexWithUV((double)f15, (double)f18, (double)(-f2), (double)uLeft, (double)vBottomEdge1);
                tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                tessellator.addVertexWithUV((double)f15, (double)f17, (double)f2, (double)uRightEdge1, (double)vTop);
                tessellator.addVertexWithUV((double)f15, (double)f18, (double)f2, (double)uRightEdge1, (double)vBottom);
                tessellator.addVertexWithUV((double)f15, (double)f18, (double)(-f2), (double)uRightEdge0, (double)vBottom);
                tessellator.addVertexWithUV((double)f15, (double)f17, (double)(-f2), (double)uRightEdge0, (double)vTop);
                tessellator.setNormal(1.0F, 0.0F, 0.0F);
                tessellator.addVertexWithUV((double)f16, (double)f17, (double)(-f2), (double)uLeftEdge0, (double)vTop);
                tessellator.addVertexWithUV((double)f16, (double)f18, (double)(-f2), (double)uLeftEdge0, (double)vBottom);
                tessellator.addVertexWithUV((double)f16, (double)f18, (double)f2, (double)uLeftEdge1, (double)vBottom);
                tessellator.addVertexWithUV((double)f16, (double)f17, (double)f2, (double)uLeftEdge1, (double)vTop);
                tessellator.draw();
            }
        }

        ci.cancel();
    }
}
