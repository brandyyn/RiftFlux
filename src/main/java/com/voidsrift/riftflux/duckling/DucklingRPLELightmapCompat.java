package com.voidsrift.riftflux.duckling;

import com.falsepattern.rple.api.client.CookieMonster;
import com.falsepattern.rple.api.client.RPLETessBrightnessUtil;

final class DucklingRPLELightmapCompat implements DucklingLightmapCompat {
    @Override
    public long lastLightMapRGB64() {
        return RPLETessBrightnessUtil.lastLightMapRGB64();
    }

    @Override
    public boolean restoreLightMapTextureCoords(long rgb64) {
        RPLETessBrightnessUtil.setLightMapTextureCoordsRGB64(rgb64);
        return true;
    }

    @Override
    public boolean setLightMapTextureCoords(float brightnessX, float brightnessY) {
        int packedBrightness = ((int) brightnessY << 16) | ((int) brightnessX & 65535);
        RPLETessBrightnessUtil.setLightMapTextureCoordsRGB64(CookieMonster.RGB64FromCookie(packedBrightness));
        return true;
    }
}
