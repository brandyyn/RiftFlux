package com.voidsrift.riftflux.mixin.early.legendgear;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.dualhotbar.RenderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.GuiIngameForge;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GuiIngameForge.class, remap = false)
public abstract class MixinGuiIngame_StarbeamMountPrompt {
    @Redirect(
            method = "renderRecordOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glTranslatef(FFF)V"
            ),
            require = 0
    )
    private void riftflux$moveMountPrompt(float x, float y, float z) {
        GL11.glTranslatef(x, this.riftflux$getPromptTranslateY(y), z);
    }

    @Unique
    private float riftflux$getPromptTranslateY(float defaultY) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null) {
            return defaultY;
        }

        EntityPlayer player = mc.thePlayer;
        if (!RenderHandler.isMountOnboardOverlayVisible(mc, player)) {
            return defaultY;
        }

        return RenderHandler.getCenteredOverlayTranslateY(mc, player, defaultY);
    }

    @Redirect(
            method = "renderRecordOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/FontRenderer;func_78276_b(Ljava/lang/String;III)I"
            ),
            require = 0
    )
    private int riftflux$skipVanillaMountPrompt(FontRenderer fontRenderer, String text, int x, int y, int color) {
        Minecraft mc = Minecraft.getMinecraft();
        if (ModConfig.dualHotbarUseCustomMountOnboardPrompt
                && mc != null
                && mc.thePlayer != null
                && RenderHandler.isMountOnboardOverlayVisible(mc, mc.thePlayer)) {
            return 0;
        }
        return fontRenderer.drawString(text, x, y, color);
    }
}
