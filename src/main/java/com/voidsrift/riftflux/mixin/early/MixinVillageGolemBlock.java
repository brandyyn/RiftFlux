package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.world.World;
import net.minecraft.village.Village;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Blocks villager-based iron golem spawning from Village#tick.
 * (Prevents golem farms.) Keeps non-golem spawns intact.
 */
@Mixin(Village.class)
public abstract class MixinVillageGolemBlock {

    @Redirect(
            method = "tick(I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntityInWorld(Lnet/minecraft/entity/Entity;)Z")
    )
    private boolean riftflux$blockFarmSpawns(World world, Entity e) {
        if (!ModConfig.reworkVillageGolems) {
            return world.spawnEntityInWorld(e);
        }
        if (e instanceof EntityIronGolem) {
            // Block vanilla villager-produced golems entirely
            return false;
        }
        return world.spawnEntityInWorld(e);
    }
}