/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package zairus.worldexplorer.core.helpers;

import java.util.HashMap;
import org.lwjgl.opengl.GL11;

public class ColorHelper {
    private static HashMap<String, HashMap<Integer, Integer>> cachedStartEndBitMaps = new HashMap();

    public static void glSetColor(int color) {
        ColorHelper.glSetColor(color, 1.0f);
    }

    public static void glSetColor(int color, float alpha) {
        GL11.glColor4f((float)((float)ColorHelper.getRGB('r', color) / 255.0f), (float)((float)ColorHelper.getRGB('g', color) / 255.0f), (float)((float)ColorHelper.getRGB('b', color) / 255.0f), (float)alpha);
    }

    private static int getRGB(char rgb, int color) {
        switch (rgb) {
            case 'r': {
                return ColorHelper.getInteger(color, 16, 23);
            }
            case 'g': {
                return ColorHelper.getInteger(color, 8, 15);
            }
            case 'b': {
                return ColorHelper.getInteger(color, 0, 7);
            }
        }
        return 0;
    }

    private static int getInteger(int source, int start, int end) {
        return ColorHelper.getInteger(source, ColorHelper.getBitMap(start, end));
    }

    private static int getInteger(int source, HashMap<Integer, Integer> bitMap) {
        int output = 0;
        for (int bitLocation : bitMap.keySet()) {
            boolean bit = (source & 1 << bitLocation) != 0;
            if (!bit) continue;
            output += 1 << bitMap.get(bitLocation);
        }
        return output;
    }

    private static HashMap<Integer, Integer> getBitMap(int start, int end) {
        HashMap<Integer, Integer> bitMap = null;
        String cacheKey = start + "," + end;
        if (cachedStartEndBitMaps.containsKey(cacheKey)) {
            bitMap = cachedStartEndBitMaps.get(cacheKey);
        } else {
            bitMap = new HashMap();
            for (int i = start; i <= end; ++i) {
                bitMap.put(i, i - start);
            }
            cachedStartEndBitMaps.put(cacheKey, bitMap);
        }
        return bitMap;
    }
}

