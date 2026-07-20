package com.voidsrift.riftflux.mixin.late.twilightforest;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.client.sky.BetaStarsRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "twilightforest.client.renderer.TFSkyRenderer", remap = false)
public abstract class MixinTFSkyRenderer_SuppressStars {
    @Unique
    private float riftflux$partialTicks;

    @Inject(method = "render", at = @At("HEAD"))
    private void riftflux$beginTwilightSkyPass(float partialTicks, WorldClient world, Minecraft mc, CallbackInfo ci) {
        riftflux$partialTicks = partialTicks;
        if (ModConfig.suppressTwilightForestStars && ModConfig.betaStarsEnabled) {
            BetaStarsRenderHelper.beginSkyRenderPass();
        }
    }

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glCallList(I)V",
                    ordinal = 1
            ),
            require = 1
    )
    private void riftflux$suppressTwilightForestStars(int displayList) {
        if (!ModConfig.suppressTwilightForestStars) {
            GL11.glCallList(displayList);
            return;
        }
        if (!ModConfig.betaStarsEnabled || BetaStarsRenderHelper.hasRenderedThisSkyPass()) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (!BetaStarsRenderHelper.shouldRenderBetaStars(mc, riftflux$partialTicks)) {
            return;
        }
        int starCount = BetaStarsRenderHelper.resolvePreferredStarCount();
        if (starCount <= 0) {
            BetaStarsRenderHelper.markRenderedThisSkyPass();
            return;
        }

        float starBrightness = 1.0F - mc.theWorld.getRainStrength(riftflux$partialTicks);
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT
                | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPushMatrix();
        if (!ModConfig.betaStarsSpinWithSunMoon) {
            GL11.glRotatef(-riftflux$getRealCelestialAngle(mc, riftflux$partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        }
        BetaStarsRenderHelper.renderBetaStars(starCount, starBrightness, riftflux$partialTicks);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
        BetaStarsRenderHelper.markRenderedThisSkyPass();
    }

    @Unique
    private static float riftflux$getRealCelestialAngle(Minecraft mc, float partialTicks) {
        int dayTime = (int) (mc.theWorld.getWorldTime() % 24000L);
        float angle = ((float) dayTime + partialTicks) / 24000.0F - 0.25F;
        if (angle < 0.0F) {
            angle += 1.0F;
        }
        if (angle > 1.0F) {
            angle -= 1.0F;
        }
        float linearAngle = angle;
        angle = 1.0F - (float) ((Math.cos((double) angle * Math.PI) + 1.0D) / 2.0D);
        return linearAngle + (angle - linearAngle) / 3.0F;
    }
}
