package com.voidsrift.riftflux.mixin.early.angelica;

import com.voidsrift.riftflux.client.sky.CloudTimeFadeHelper;
import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import net.minecraft.client.Minecraft;
import org.embeddedt.embeddium.impl.gl.shader.GlProgram;
import org.embeddedt.embeddium.impl.gl.shader.GlShader;
import org.embeddedt.embeddium.impl.gl.shader.ShaderConstants;
import org.embeddedt.embeddium.impl.gl.shader.ShaderType;
import org.embeddedt.embeddium.impl.render.shader.ShaderLoader;
import org.lwjgl.opengl.GL20;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.gtnewhorizons.angelica.render.CloudRenderer", remap = false)
public abstract class MixinAngelicaCloudRenderer_CloudTimeFade {

    @Unique
    private int riftflux$cloudOpacityProgramHandle = -1;

    @Unique
    private int riftflux$cloudOpacityUniformLocation = -1;

    @Inject(method = "render(IF)Z", at = @At("HEAD"), cancellable = true)
    private void riftflux$skipFullyTransparentClouds(
            int cloudTicks,
            float partialTicks,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (IsometricPhotoModeController.instance().isActive()) {
            cir.setReturnValue(false);
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null
                && CloudTimeFadeHelper.getOpacity(mc.theWorld, partialTicks) <= 0.0001F) {
            cir.setReturnValue(false);
        }
    }

    @Redirect(
            method = "initProgram()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/embeddedt/embeddium/impl/render/shader/ShaderLoader;loadShader(Lorg/embeddedt/embeddium/impl/gl/shader/ShaderType;Ljava/lang/String;Lorg/embeddedt/embeddium/impl/gl/shader/ShaderConstants;)Lorg/embeddedt/embeddium/impl/gl/shader/GlShader;"
            )
    )
    private GlShader riftflux$loadCloudFadeShader(
            ShaderType type,
            String name,
            ShaderConstants constants
    ) {
        return ShaderLoader.loadShader(
                type,
                type == ShaderType.FRAGMENT ? "riftflux:cloud_fade.frag" : name,
                constants
        );
    }

    @Redirect(
            method = "render(IF)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/embeddedt/embeddium/impl/gl/shader/GlProgram;bind()V"
            )
    )
    private void riftflux$bindCloudOpacity(
            GlProgram<?> program,
            int cloudTicks,
            float partialTicks
    ) {
        program.bind();
        Minecraft mc = Minecraft.getMinecraft();
        float opacity = mc == null ? 1.0F : CloudTimeFadeHelper.getOpacity(mc.theWorld, partialTicks);
        if (this.riftflux$cloudOpacityProgramHandle != program.handle()) {
            this.riftflux$cloudOpacityProgramHandle = program.handle();
            this.riftflux$cloudOpacityUniformLocation =
                    GL20.glGetUniformLocation(this.riftflux$cloudOpacityProgramHandle, "u_Opacity");
        }
        if (this.riftflux$cloudOpacityUniformLocation >= 0) {
            GL20.glUniform1f(this.riftflux$cloudOpacityUniformLocation, opacity);
        }
    }
}
