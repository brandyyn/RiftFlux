package com.voidsrift.riftflux.duckling;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ModelSootSprite extends AnimatedGeoModel<EntitySootSprite> {
    private static final ResourceLocation MODEL = new ResourceLocation(DucklingContent.MODID, "geo/soot_sprite.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(DucklingContent.MODID, "textures/entity/soot_sprite.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation(DucklingContent.MODID, "animations/soot_sprite.animation.json");

    @Override
    public ResourceLocation getAnimationFileLocation(EntitySootSprite sprite) {
        return ANIMATION;
    }

    @Override
    public ResourceLocation getModelLocation(EntitySootSprite sprite) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureLocation(EntitySootSprite sprite) {
        return TEXTURE;
    }
}
