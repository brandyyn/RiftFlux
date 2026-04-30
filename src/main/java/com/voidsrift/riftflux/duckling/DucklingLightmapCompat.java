package com.voidsrift.riftflux.duckling;

interface DucklingLightmapCompat {
    long lastLightMapRGB64();

    boolean restoreLightMapTextureCoords(long rgb64);

    boolean setLightMapTextureCoords(float brightnessX, float brightnessY);
}
