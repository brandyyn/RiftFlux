package com.voidsrift.riftflux.mixin.early.legendgear;

import com.voidsrift.riftflux.mixinhooks.EntityLivingBaseJumpAccessor;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase_StarbeamJumpAccessor implements EntityLivingBaseJumpAccessor {
    @Override
    @Accessor("isJumping")
    public abstract boolean riftflux$isJumping();
}
