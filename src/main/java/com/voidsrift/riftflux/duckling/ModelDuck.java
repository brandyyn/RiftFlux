package com.voidsrift.riftflux.duckling;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ModelDuck extends AnimatedGeoModel<EntityDuck> {
    private static final ResourceLocation MODEL = new ResourceLocation(DucklingContent.MODID, "geo/duck.geo.json");
    private static final ResourceLocation ANIMATION = new ResourceLocation(DucklingContent.MODID, "animations/duck.animation.json");

    @Override
    public ResourceLocation getAnimationFileLocation(EntityDuck duck) {
        return ANIMATION;
    }

    @Override
    public ResourceLocation getModelLocation(EntityDuck duck) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityDuck duck) {
        return duck.getTextureVariant().getTexture();
    }
}
