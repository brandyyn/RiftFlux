package com.voidsrift.riftflux.wam.client.render;

import com.voidsrift.riftflux.wam.client.model.ModelFlowerMan;
import com.voidsrift.riftflux.wam.entity.EntityFlowerMan;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderFlowerMan extends RenderLiving {
    public RenderFlowerMan() {
        super(new ModelFlowerMan(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if (!(entity instanceof EntityFlowerMan)) {
            return WAMTextures.FLOWER_MAN;
        }

        switch (((EntityFlowerMan) entity).getColor()) {
            case 2:
                return WAMTextures.FLOWER_MAN_BLUE;
            case 3:
                return WAMTextures.FLOWER_MAN_ORANGE;
            case 4:
                return WAMTextures.FLOWER_MAN_YELLOW;
            case 5:
                return WAMTextures.FLOWER_MAN_RED;
            case 6:
                return WAMTextures.FLOWER_MAN_WHITE;
            case 7:
                return WAMTextures.FLOWER_MAN_LAVENDER;
            case 8:
                return WAMTextures.FLOWER_MAN_PURPLE;
            case 9:
                return WAMTextures.FLOWER_MAN_MAGENTA;
            case 10:
                return WAMTextures.FLOWER_MAN_BLUE_WHITE;
            default:
                return WAMTextures.FLOWER_MAN;
        }
    }
}
