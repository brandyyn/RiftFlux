package com.voidsrift.riftflux.jukebox;

public interface JukeboxLoopState {
    long riftflux$getNextLoopTick();

    void riftflux$setNextLoopTick(long tick);

    boolean riftflux$wasPowered();

    void riftflux$setWasPowered(boolean powered);
}
