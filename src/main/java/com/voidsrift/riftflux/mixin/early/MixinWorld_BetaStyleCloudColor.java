package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.sky.SunriseSkyTintHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class MixinWorld_BetaStyleCloudColor {

    @Inject(method = "getCloudColour(F)Lnet/minecraft/util/Vec3;", at = @At("RETURN"), cancellable = true)
    private void riftflux$tintCloudsForBetaFog(float partialTicks, CallbackInfoReturnable<Vec3> cir) {
        if (!((Object) this instanceof WorldClient)) {
            return;
        }

        WorldClient world = (WorldClient) (Object) this;
        if (!SunriseSkyTintHelper.shouldUseBetaStyleBiomeFog(world)) {
            return;
        }

        Vec3 cloud = cir.getReturnValue();
        if (cloud == null) {
            return;
        }

        float[] tint = SunriseSkyTintHelper.resolveBetaStyleCloudColor(
                world,
                Minecraft.getMinecraft(),
                partialTicks,
                (float) cloud.xCoord,
                (float) cloud.yCoord,
                (float) cloud.zCoord
        );
        if (tint != null && tint.length >= 3) {
            cir.setReturnValue(Vec3.createVectorHelper(tint[0], tint[1], tint[2]));
        }
    }
}
