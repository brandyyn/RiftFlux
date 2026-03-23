package com.voidsrift.riftflux.wam.client.render;

import com.voidsrift.riftflux.Constants;
import net.minecraft.util.ResourceLocation;

final class WAMTextures {
    public static final ResourceLocation CYCLOPS = texture("Cyclops.png");
    public static final ResourceLocation ENDER_TROLL = texture("EnderTroll.png");
    public static final ResourceLocation ENDER_TROLL_EYES = texture("EnderTrollEyes.png");
    public static final ResourceLocation FLOWER_MAN = texture("FlowerMan.png");
    public static final ResourceLocation FLOWER_MAN_BLUE = texture("FlowerManBlue.png");
    public static final ResourceLocation FLOWER_MAN_BLUE_WHITE = texture("FlowerManBlueWhite.png");
    public static final ResourceLocation FLOWER_MAN_LAVENDER = texture("FlowerManLavender.png");
    public static final ResourceLocation FLOWER_MAN_MAGENTA = texture("FlowerManMagenta.png");
    public static final ResourceLocation FLOWER_MAN_ORANGE = texture("FlowerManOrange.png");
    public static final ResourceLocation FLOWER_MAN_PURPLE = texture("FlowerManPurple.png");
    public static final ResourceLocation FLOWER_MAN_RED = texture("FlowerManRed.png");
    public static final ResourceLocation FLOWER_MAN_WHITE = texture("FlowerManWhite.png");
    public static final ResourceLocation FLOWER_MAN_YELLOW = texture("FlowerManYellow.png");
    public static final ResourceLocation JAXX = texture("Jaxx.png");
    public static final ResourceLocation JAXX_EYES = texture("Jaxxeyes.png");
    public static final ResourceLocation BLACK_WIDOW = texture("MedianWidow.png");
    public static final ResourceLocation BLACK_WIDOW_EYES = texture("MedianWidowEyes.png");

    private WAMTextures() {
    }

    private static ResourceLocation texture(String name) {
        return new ResourceLocation(Constants.MODID, "textures/models/wam/" + name);
    }
}
