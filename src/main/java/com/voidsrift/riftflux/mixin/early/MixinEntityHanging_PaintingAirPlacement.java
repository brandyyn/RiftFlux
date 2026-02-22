package com.voidsrift.riftflux.mixin.early;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityPainting;
import com.voidsrift.riftflux.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;

@Mixin(EntityHanging.class)
public abstract class MixinEntityHanging_PaintingAirPlacement {

    @Inject(method = "onValidSurface", at = @At("HEAD"), cancellable = true)
    private void riftflux$allowPaintingWithoutBacking(CallbackInfoReturnable<Boolean> cir) {
        EntityHanging self = (EntityHanging)(Object)this;
        if (!(self instanceof EntityPainting)) {
            return;
        }
        if (!ModConfig.enablePaintingAirPlacement) {
            return;
        }

        if (!self.worldObj.getCollidingBoundingBoxes(self, self.boundingBox).isEmpty()) {
            cir.setReturnValue(false);
            return;
        }

        List list = self.worldObj.getEntitiesWithinAABBExcludingEntity(self, self.boundingBox);
        Iterator iterator = list.iterator();
        Entity entity;

        do {
            if (!iterator.hasNext()) {
                cir.setReturnValue(true);
                return;
            }
            entity = (Entity)iterator.next();
        } while (!(entity instanceof EntityHanging));

        cir.setReturnValue(false);
    }
}
