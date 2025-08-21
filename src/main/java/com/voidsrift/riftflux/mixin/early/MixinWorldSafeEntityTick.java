package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.mixinhooks.ISafeTickFlag;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(World.class)
public abstract class MixinWorldSafeEntityTick {

    @Shadow public abstract long getTotalWorldTime();

    /**
     * Wrap the per-entity tick:
     * World.updateEntityWithOptionalForce(Entity, boolean) -> Entity.onUpdate()
     */
    @Redirect(
            method = "updateEntityWithOptionalForce(Lnet/minecraft/entity/Entity;Z)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onUpdate()V")
    )
    private void rf$wrapEntityOnUpdate(Entity entity) {
        if (!ModConfig.enableSafeEntityTick) {
            entity.onUpdate();
            return;
        }

        final long now = this.getTotalWorldTime();
        final ISafeTickFlag flag = (ISafeTickFlag) entity;

        // Still cooling down from a previous fault? Skip quietly.
        if (flag.rf$getSkipUntil() > now) return;

        try {
            entity.onUpdate();
            // success: clear error state
            flag.rf$setSkipUntil(0L);
            flag.rf$clearErrors();
        } catch (NullPointerException npe) {
            flag.rf$incErrorCount();
            final int errs = flag.rf$getErrorCount();

            final long cooloff = now + ModConfig.safeEntityTickSkipTicks;
            flag.rf$setSkipUntil(cooloff);

            if (ModConfig.safeEntityTickLog) {
                // Avoid EntityList dependency; log class name instead
                FMLLog.severe(
                        "[RiftFlux] Suppressed NPE while ticking %s @ [%.1f, %.1f, %.1f]; errors=%d, skipping until t=%d",
                        entity.getClass().getName(),
                        entity.posX, entity.posY, entity.posZ,
                        errs, cooloff
                );
            }

            if (ModConfig.safeEntityTickMaxErrorsBeforeRemove > 0 &&
                    errs >= ModConfig.safeEntityTickMaxErrorsBeforeRemove) {
                if (ModConfig.safeEntityTickLog) {
                    FMLLog.severe(
                            "[RiftFlux] Removing %s after %d consecutive NPEs.",
                            entity.getClass().getName(), errs
                    );
                }
                entity.setDead();
            }
        }
    }
}
