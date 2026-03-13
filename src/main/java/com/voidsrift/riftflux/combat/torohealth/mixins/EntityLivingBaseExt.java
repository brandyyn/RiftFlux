package com.voidsrift.riftflux.combat.torohealth.mixins;

public interface EntityLivingBaseExt {

    int riftflux$getToroHealthPrevHealth();

    void riftflux$setToroHealthPrevHealth(int value);

    int riftflux$getToroHealthLastDamageParticleTick();

    void riftflux$setToroHealthLastDamageParticleTick(int value);
}
