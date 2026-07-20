package com.voidsrift.riftflux.mixin.early;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.GuiIngameForge;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GuiIngameForge.class, remap = false)
public abstract class MixinGuiIngame_CenteredCrosshair {
    private static final int CROSSHAIR_SIZE = 16;
    private static final int CROSSHAIR_CENTER_OFFSET = 7;
    private static final float TEXTURE_SIZE = 256.0F;

    @Redirect(
            method = "renderCrosshairs",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/client/GuiIngameForge;func_73729_b(IIIIII)V"
            )
    )
    private void riftflux$drawCenteredCrosshair(GuiIngameForge instance, int x, int y, int u, int v,
                                                 int width, int height) {
        if (u != 0 || v != 0 || width != 16 || height != 16) {
            instance.drawTexturedModalRect(x, y, u, v, width, height);
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        ScaledResolution resolution = new ScaledResolution(
                minecraft, minecraft.displayWidth, minecraft.displayHeight
        );
        double centeredX = resolution.getScaledWidth_double() / 2.0D - CROSSHAIR_CENTER_OFFSET;
        double centeredY = resolution.getScaledHeight_double() / 2.0D - CROSSHAIR_CENTER_OFFSET;

        Tessellator tessellator = Tessellator.instance;
        GL11.glPushMatrix();
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(
                centeredX, centeredY + CROSSHAIR_SIZE, 0.0D,
                0.0D, height / TEXTURE_SIZE
        );
        tessellator.addVertexWithUV(
                centeredX + CROSSHAIR_SIZE, centeredY + CROSSHAIR_SIZE, 0.0D,
                width / TEXTURE_SIZE, height / TEXTURE_SIZE
        );
        tessellator.addVertexWithUV(
                centeredX + CROSSHAIR_SIZE, centeredY, 0.0D,
                width / TEXTURE_SIZE, 0.0D
        );
        tessellator.addVertexWithUV(
                centeredX, centeredY, 0.0D,
                0.0D, 0.0D
        );
        tessellator.draw();
        GL11.glPopMatrix();
    }
}
