package com.voidsrift.riftflux.duckling;

import java.util.Calendar;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ModelQuackling extends AnimatedGeoModel<EntityQuackling> {
    private static final ResourceLocation MODEL = new ResourceLocation(DucklingContent.MODID, "geo/quackling.geo.json");
    private static final ResourceLocation CHRISTMAS_MODEL = new ResourceLocation(DucklingContent.MODID, "geo/quackling_christmas.geo.json");
    private static final ResourceLocation ANIMATION = new ResourceLocation(DucklingContent.MODID, "animations/quackling.animation.json");
    private static final ResourceLocation NORMAL = new ResourceLocation(DucklingContent.MODID, "textures/entity/quackling.png");
    private static final ResourceLocation DRIPPED = new ResourceLocation(DucklingContent.MODID, "textures/entity/dripped_out_quackling.png");
    private static final ResourceLocation CHRISTMAS = new ResourceLocation(DucklingContent.MODID, "textures/entity/christmas_quackling.png");
    private static final ResourceLocation MAID = new ResourceLocation(DucklingContent.MODID, "textures/entity/maid_quackling.png");

    @Override
    public ResourceLocation getAnimationFileLocation(EntityQuackling quackling) {
        return ANIMATION;
    }

    @Override
    public ResourceLocation getModelLocation(EntityQuackling quackling) {
        return this.isChristmasQuackling(quackling) ? CHRISTMAS_MODEL : MODEL;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityQuackling quackling) {
        if (this.isMaid(quackling)) {
            return MAID;
        }
        if (quackling.isDripped()) {
            return DRIPPED;
        }
        return this.isDecember() ? CHRISTMAS : NORMAL;
    }

    private boolean isChristmasQuackling(EntityQuackling quackling) {
        return !this.isMaid(quackling) && !quackling.isDripped() && this.isDecember();
    }

    private boolean isMaid(EntityQuackling quackling) {
        String name = quackling.getCommandSenderName();
        return "haley".equalsIgnoreCase(name) || "maid".equalsIgnoreCase(name);
    }

    private boolean isDecember() {
        return Calendar.getInstance().get(Calendar.MONTH) == Calendar.DECEMBER;
    }
}
