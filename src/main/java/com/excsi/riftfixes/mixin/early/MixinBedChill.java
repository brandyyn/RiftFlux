package com.excsi.riftfixes.mixin.early;

import com.excsi.riftfixes.ModConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;

/**
 * Bed “chill mode” (single toggle: ModConfig.enableBedChill):
 *  - Sleep any time (ignore day)
 *  - Ignore “monsters nearby”
 *  - Ignore proximity to bed
 *  - Never skip night
 *  - No fade-to-black
 *  - Stay in bed during day (don’t get kicked out)
 */
public final class MixinBedChill {

    /* 1) SERVER: never skip the night */
    @Mixin(WorldServer.class)
    public static abstract class NoSleepSkip {
        @Inject(method = "areAllPlayersAsleep()Z", at = @At("HEAD"), cancellable = true)
        private void riftfixes$neverSkip(CallbackInfoReturnable<Boolean> cir) {
            if (ModConfig.enableBedChill) {
                cir.setReturnValue(false);
            }
        }
    }

    /* 2) SERVER: allow bed anytime, ignore mobs & proximity, and don't wake up due to day */
    @Mixin(EntityPlayer.class)
    public static abstract class AnyTimeSleep {

        /** Treat world as “not daytime” inside sleepInBedAt so bed is allowed. */
        @Redirect(
                method = "sleepInBedAt(III)Lnet/minecraft/entity/player/EntityPlayer$EnumStatus;",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isDaytime()Z")
        )
        private boolean riftfixes$allowDaytimeSleep(World world) {
            if (ModConfig.enableBedChill) return false;
            return world.isDaytime();
        }

        /** Return empty list for the nearby-monsters query. */
        @Redirect(
                method = "sleepInBedAt(III)Lnet/minecraft/entity/player/EntityPlayer$EnumStatus;",
                at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/world/World;getEntitiesWithinAABB(Ljava/lang/Class;Lnet/minecraft/util/AxisAlignedBB;)Ljava/util/List;")
        )
        private List riftfixes$noMonstersNearby(World world, Class cls, AxisAlignedBB box) {
            if (ModConfig.enableBedChill) return Collections.emptyList();
            return world.getEntitiesWithinAABB(cls, box);
        }

        /** Neutralize the distance-to-bed check by zeroing Math.abs(...) in this method. */
        @Redirect(
                method = "sleepInBedAt(III)Lnet/minecraft/entity/player/EntityPlayer$EnumStatus;",
                at = @At(value = "INVOKE", target = "Ljava/lang/Math;abs(D)D")
        )
        private double riftfixes$noDistanceLimit(double value) {
            if (ModConfig.enableBedChill) return 0D;
            return Math.abs(value);
        }

        /** While sleeping, treat day as night in onUpdate so the server doesn't wake you up. */
        @Redirect(
                method = "onUpdate()V",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isDaytime()Z")
        )
        private boolean riftfixes$stayInBedDuringDay(World world) {
            if (ModConfig.enableBedChill) {
                EntityPlayer self = (EntityPlayer)(Object)this;
                if (self.isPlayerSleeping()) return false; // “not daytime” -> don’t auto-wake
            }
            return world.isDaytime();
        }
    }

    /* 3) CLIENT: remove the fade-to-black while lying in bed */
    @Pseudo
    @Mixin(EntityPlayer.class)
    public static abstract class NoSleepFade {
        @Inject(method = "getSleepTimer()I", at = @At("HEAD"), cancellable = true)
        private void riftfixes$noFade(CallbackInfoReturnable<Integer> cir) {
            if (!ModConfig.enableBedChill) return;
            EntityPlayer self = (EntityPlayer) (Object) this;
            if (!self.worldObj.isRemote) return; // client only
            cir.setReturnValue(0);
        }
    }
}
