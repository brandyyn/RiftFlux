package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.mixinhooks.ISafeTickFlag;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public abstract class MixinEntitySafeTick implements ISafeTickFlag {

    @Unique private long rf$skipUntilTick = 0L;
    @Unique private int  rf$errorCount    = 0;

    @Override public long rf$getSkipUntil()       { return rf$skipUntilTick; }
    @Override public void rf$setSkipUntil(long t) { rf$skipUntilTick = t; }
    @Override public int  rf$getErrorCount()      { return rf$errorCount; }
    @Override public void rf$setErrorCount(int c) { rf$errorCount = c; }
    @Override public void rf$incErrorCount()      { rf$errorCount++; }
    @Override public void rf$clearErrors()        { rf$errorCount = 0; }
}
