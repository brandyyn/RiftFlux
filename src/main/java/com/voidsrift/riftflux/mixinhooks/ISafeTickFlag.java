package com.voidsrift.riftflux.mixinhooks;

/** Attached to every Entity via mixin so we can throttle ticking after an NPE. */
public interface ISafeTickFlag {
    long  rf$getSkipUntil();
    void  rf$setSkipUntil(long t);
    int   rf$getErrorCount();
    void  rf$setErrorCount(int c);
    void  rf$incErrorCount();
    void  rf$clearErrors();
}
