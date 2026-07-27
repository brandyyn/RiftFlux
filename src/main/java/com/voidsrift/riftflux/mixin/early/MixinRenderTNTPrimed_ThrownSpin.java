package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.entity.PrimedTntCarry;
import net.minecraft.client.renderer.entity.RenderTNTPrimed;
import net.minecraft.entity.item.EntityTNTPrimed;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderTNTPrimed.class)
public abstract class MixinRenderTNTPrimed_ThrownSpin {

    @Inject(
            method = "doRender(Lnet/minecraft/entity/item/EntityTNTPrimed;DDDFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glTranslatef(FFF)V",
                    shift = At.Shift.AFTER
            )
    )
    private void riftflux$spinThrownTnt(
            EntityTNTPrimed tnt,
            double x,
            double y,
            double z,
            float yaw,
            float partialTicks,
            CallbackInfo ci
    ) {
        if (ModConfig.spinThrownPrimedTnt
                && PrimedTntCarry.isThrown(tnt)
                && !tnt.onGround
                && !tnt.isCollidedVertically) {
            float spin = ((float) tnt.ticksExisted + partialTicks)
                    * ModConfig.thrownPrimedTntSpinSpeed;
            GL11.glRotatef(spin, 0.0F, 1.0F, 0.0F);
        }
    }
}
