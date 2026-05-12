package com.voidsrift.riftflux.jukebox;

public interface JukeboxLoopState {
    long riftflux$getNextLoopTick();

    void riftflux$setNextLoopTick(long tick);

    long riftflux$getScheduledRecordKey();

    void riftflux$setScheduledRecordKey(long key);

    boolean riftflux$wasPowered();

    void riftflux$setWasPowered(boolean powered);
}
