package com.voidsrift.riftflux.mixin.early;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.lwjgl.opengl.GL11;

@Mixin(value = GuiIngameForge.class, remap = false)
public abstract class MixinGuiIngame_HotbarSelectorTexture {
    private static final ResourceLocation RIFT_SELECTOR =
            new ResourceLocation("riftflux:textures/gui/riftselector.png");
    private static final ResourceLocation WIDGETS =
            new ResourceLocation("textures/gui/widgets.png");
    private static final int SELECTOR_Y_OFFSET = 1;

    @Unique
    private int riftflux$selectorX;
    @Unique
    private int riftflux$selectorY;
    @Unique
    private boolean riftflux$hasSelector;

    @Redirect(
            method = "renderHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/client/GuiIngameForge;func_73729_b(IIIIII)V"
            )
    )
    private void riftflux$drawHotbarSelector(GuiIngameForge instance, int x, int y, int u, int v, int w, int h) {
        if (u == 0 && v == 22 && w == 24 && h == 22) {
            riftflux$selectorX = x;
            riftflux$selectorY = y;
            riftflux$hasSelector = true;
            return;
        }
        instance.drawTexturedModalRect(x, y, u, v, w, h);
    }

    @Inject(method = "renderHotbar", at = @At("RETURN"))
    private void riftflux$renderSelectorOnTop(int width, int height, float partialTicks, CallbackInfo ci) {
        if (!riftflux$hasSelector) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        mc.getTextureManager().bindTexture(RIFT_SELECTOR);
        drawSelectorScaled(riftflux$selectorX, riftflux$selectorY + SELECTOR_Y_OFFSET);
        mc.getTextureManager().bindTexture(WIDGETS);
        riftflux$hasSelector = false;
    }

    private void drawSelectorScaled(int x, int y) {
        drawCustomSizedTexture(x, y, 0f, 0f, 24, 24, 24, 22, 24f, 24f);
    }

    private void drawCustomSizedTexture(int x, int y, float u, float v, int regionWidth, int regionHeight,
                                        int drawWidth, int drawHeight, float textureWidth, float textureHeight) {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + drawHeight, 0.0D, (u) * f, (v + regionHeight) * f1);
        tessellator.addVertexWithUV(x + drawWidth, y + drawHeight, 0.0D, (u + regionWidth) * f, (v + regionHeight) * f1);
        tessellator.addVertexWithUV(x + drawWidth, y, 0.0D, (u + regionWidth) * f, (v) * f1);
        tessellator.addVertexWithUV(x, y, 0.0D, (u) * f, (v) * f1);
        tessellator.draw();
    }
}
