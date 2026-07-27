package com.voidsrift.riftflux.axolotl;

import java.util.Random;

public enum AxolotlVariant {
    LUCY(0, "lucy"),
    WILD(1, "wild"),
    GOLD(2, "gold"),
    CYAN(3, "cyan"),
    BLUE(4, "blue"),
    LAVENDER(5, "lavender"),
    MOSS(6, "moss"),
    SLATE(7, "slate"),
    ROSE(8, "rose"),
    MINT(9, "mint"),
    AMBER(10, "amber");

    private static final AxolotlVariant[] BY_ID = values();

    private final int id;
    private final String textureKey;

    AxolotlVariant(int id, String textureKey) {
        this.id = id;
        this.textureKey = textureKey;
    }

    public int getId() {
        return this.id;
    }

    public String getTextureKey() {
        return this.textureKey;
    }

    public static AxolotlVariant byId(int id) {
        if (id < 0 || id >= BY_ID.length) {
            return LUCY;
        }
        return BY_ID[id];
    }

    public static AxolotlVariant getRandomVariant(Random rand) {
        return rand == null ? LUCY : BY_ID[rand.nextInt(BY_ID.length)];
    }
}
