package com.voidsrift.riftflux.duckling;

import java.util.Locale;
import java.util.Random;
import net.minecraft.util.ResourceLocation;

public enum DuckVariant {
    AGENTD(0, "agent_d"),
    MALLARD(1, "mallard"),
    PEKIN(2, "duck");

    private final int id;
    private final ResourceLocation texture;

    DuckVariant(int id, String textureName) {
        this.id = id;
        this.texture = new ResourceLocation(DucklingContent.MODID, "textures/entity/" + textureName + ".png");
    }

    public int getId() {
        return id;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public static DuckVariant byId(int id) {
        DuckVariant[] values = values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].id == id) {
                return values[i];
            }
        }
        return PEKIN;
    }

    public static DuckVariant byName(String name) {
        if (name == null || name.length() == 0) {
            return PEKIN;
        }
        try {
            return valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return PEKIN;
        }
    }

    public static DuckVariant randomNatural(Random random) {
        return random != null && random.nextBoolean() ? MALLARD : PEKIN;
    }
}
