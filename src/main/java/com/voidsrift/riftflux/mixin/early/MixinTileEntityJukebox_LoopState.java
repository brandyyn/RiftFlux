package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.jukebox.JukeboxLoopHelper;
import com.voidsrift.riftflux.jukebox.JukeboxLoopState;
import net.minecraft.block.BlockJukebox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockJukebox.TileEntityJukebox.class)
public abstract class MixinTileEntityJukebox_LoopState implements JukeboxLoopState {
    @Unique
    private static final String RIFTFLUX_NEXT_LOOP_TICK = "RiftFluxNextLoopTick";

    @Unique
    private static final String RIFTFLUX_WAS_POWERED = "RiftFluxWasPowered";

    @Unique
    private long riftflux$nextLoopTick = JukeboxLoopHelper.NO_LOOP_TICK;

    @Unique
    private boolean riftflux$wasPowered;

    @Inject(method = "func_145857_a", at = @At("TAIL"))
    private void riftflux$onRecordChanged(ItemStack stack, CallbackInfo ci) {
        BlockJukebox.TileEntityJukebox self = (BlockJukebox.TileEntityJukebox) (Object) this;
        if (stack == null) {
            JukeboxLoopHelper.clearLoop(self);
        } else {
            JukeboxLoopHelper.scheduleLoop(self, stack);
            JukeboxLoopHelper.syncInitialPower(self);
        }
    }

    @Inject(method = "updateEntity", at = @At("HEAD"))
    private void riftflux$tickLoop(CallbackInfo ci) {
        JukeboxLoopHelper.tick((BlockJukebox.TileEntityJukebox) (Object) this);
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"))
    private void riftflux$readLoopState(NBTTagCompound tag, CallbackInfo ci) {
        if (tag.hasKey(RIFTFLUX_NEXT_LOOP_TICK)) {
            this.riftflux$nextLoopTick = tag.getLong(RIFTFLUX_NEXT_LOOP_TICK);
        }
        this.riftflux$wasPowered = tag.getBoolean(RIFTFLUX_WAS_POWERED);
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"))
    private void riftflux$writeLoopState(NBTTagCompound tag, CallbackInfo ci) {
        tag.setLong(RIFTFLUX_NEXT_LOOP_TICK, this.riftflux$nextLoopTick);
        tag.setBoolean(RIFTFLUX_WAS_POWERED, this.riftflux$wasPowered);
    }

    @Override
    public long riftflux$getNextLoopTick() {
        return this.riftflux$nextLoopTick;
    }

    @Override
    public void riftflux$setNextLoopTick(long tick) {
        this.riftflux$nextLoopTick = tick;
    }

    @Override
    public boolean riftflux$wasPowered() {
        return this.riftflux$wasPowered;
    }

    @Override
    public void riftflux$setWasPowered(boolean powered) {
        this.riftflux$wasPowered = powered;
    }
}
