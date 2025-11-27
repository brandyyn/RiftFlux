package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

/**
 * Restricts hostile mob spawns:
 * - Only allow spawns when BLOCK light level is exactly 0.
 * - Ignore skylight for the spawn light check itself so mobs can still spawn
 *   on the surface at night.
 * - Preserve vanilla "no spawns in bright daytime sky" behaviour.
 *
 * Affects all mobs extending EntityMob that use isValidLightLevel() in getCanSpawnHere().
 */
@Mixin(EntityMob.class)
public abstract class MixinEntityMob_ZeroBlockLightSpawn {

    @Inject(method = "isValidLightLevel", at = @At("HEAD"), cancellable = true)
    private void riftflux$requireZeroBlockLight(CallbackInfoReturnable<Boolean> cir) {
        // Config gate: if feature is off, let vanilla behaviour run.
        if (!ModConfig.strictMobSpawnsZeroBlockLight) {
            return;
        }

        EntityMob self = (EntityMob) (Object) this;
        World world = self.worldObj;
        Random rand = world.rand; // use world's Random instead of self.rand

        int x = MathHelper.floor_double(self.posX);
        int y = MathHelper.floor_double(self.boundingBox.minY);
        int z = MathHelper.floor_double(self.posZ);

        // --- Keep vanilla "no spawns in bright daytime sky" behaviour ---
        if (world.isDaytime()) {
            int skyLight = world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z);
            if (skyLight > rand.nextInt(32)) {
                // Too bright from sky during the day -> no spawn
                cir.setReturnValue(false);
                return;
            }
        }

        // --- Our rule: only allow spawns if BLOCK light is exactly 0 ---
        int blockLight = world.getSavedLightValue(EnumSkyBlock.Block, x, y, z);

        // Ignore skylight completely for the actual spawn check:
        // surface at night (no torches) -> blockLight == 0 -> allowed
        // any torches/glowstone/etc. -> blockLight > 0 -> denied
        cir.setReturnValue(blockLight == 0);
    }
}
