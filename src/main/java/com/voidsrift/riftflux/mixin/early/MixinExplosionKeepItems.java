package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Prevents dropped items from being deleted by any explosion.
 * We redirect the specific call to Entity.attackEntityFrom(...) inside Explosion#doExplosionA.
 *
 * Vanilla 1.7.10 targets:
 *  - Lnet/minecraft/world/Explosion;doExplosionA()V
 *  - INVOKE Lnet/minecraft/entity/Entity;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z
 *
 * Controlled by ModConfig.protectItemsFromExplosions (default: true).
 */
@Mixin(Explosion.class)
public abstract class MixinExplosionKeepItems {

    @Redirect(
            method = "doExplosionA",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z"
            )
    )
    private boolean riftflux$skipItemExplosionDamage(Entity self, DamageSource source, float amount) {
        // If enabled, block explosion "damage" to dropped items (which would otherwise kill them).
        if (ModConfig.protectItemsFromExplosions && self instanceof EntityItem) {
            return false; // not damaged => not deleted
        }
        // Otherwise behave normally
        return self.attackEntityFrom(source, amount);
    }
}
