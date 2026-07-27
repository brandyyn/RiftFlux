package com.voidsrift.riftflux.mixin.early;

import com.gtnewhorizons.angelica.glsm.GLStateManager;
import invmod.common.entity.EntityIMLiving;
import invmod.common.entity.EntityIMWolf;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RendererLivingEntity.class)
public abstract class MixinRendererLivingEntity_InvasionNormals {

    @Unique
    private int riftflux$invasionNormalizeDepth;

    @Unique
    private boolean riftflux$restoreNormalize;

    @Inject(
            method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V",
            at = @At("HEAD")
    )
    private void riftflux$enableNormalizedInvasionNormals(
            EntityLivingBase entity,
            double x,
            double y,
            double z,
            float yaw,
            float partialTicks,
            CallbackInfo ci
    ) {
        if (!riftflux$isInvasionMob(entity)) {
            return;
        }

        if (this.riftflux$invasionNormalizeDepth++ == 0) {
            this.riftflux$restoreNormalize = !GLStateManager.glIsEnabled(GL11.GL_NORMALIZE);
            if (this.riftflux$restoreNormalize) {
                GLStateManager.glEnable(GL11.GL_NORMALIZE);
            }
        }
    }

    @Inject(
            method = "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V",
            at = @At("RETURN")
    )
    private void riftflux$restoreNormalizedInvasionNormals(
            EntityLivingBase entity,
            double x,
            double y,
            double z,
            float yaw,
            float partialTicks,
            CallbackInfo ci
    ) {
        if (!riftflux$isInvasionMob(entity)) {
            return;
        }

        if (--this.riftflux$invasionNormalizeDepth == 0 && this.riftflux$restoreNormalize) {
            GLStateManager.glDisable(GL11.GL_NORMALIZE);
        }
    }

    @Unique
    private static boolean riftflux$isInvasionMob(EntityLivingBase entity) {
        return entity instanceof EntityIMLiving || entity instanceof EntityIMWolf;
    }
}
