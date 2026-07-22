/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.client.event.TextureStitchEvent$Pre
 */
package de.sanandrew.mods.claysoldiers.client.util;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;

@SideOnly(value=Side.CLIENT)
public final class Textures {
    public static IIcon s_shieldIcon;
    public static IIcon s_shieldStudIcon;
    public static IIcon s_starfruitShieldIcon;
    public static IIcon s_starfruitShieldStudIcon;
    public static final ResourceLocation CLAYMAN_LEATHER_ARMOR;
    public static final ResourceLocation CLAYMAN_PADDING;
    public static final ResourceLocation CLAYMAN_GUNPOWDER;
    public static final ResourceLocation CLAYMAN_MAGMACREAM;
    public static final ResourceLocation CLAYMAN_SLIMEFEET;
    public static final ResourceLocation CLAYMAN_CROWN;
    public static final ResourceLocation CLAYMAN_LILYPANTS;
    public static final ResourceLocation CLAYMAN_GOGGLES;
    public static final ResourceLocation CLAYMAN_CAPE_BLANK;
    public static final ResourceLocation CLAYMAN_CAPE_DIAMOND;
    public static final ResourceLocation CLAYMAN_GOLD_HOODIE;
    public static final ResourceLocation NEXUS_TEXTURE;
    public static final ResourceLocation NEXUS_GLOWING;
    public static final ResourceLocation NEXUS_PARTICLE;

    @SubscribeEvent
    public void onTextureStitchPre(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() == 1) {
            s_shieldIcon = event.map.registerIcon("claysoldiers:shield");
            s_shieldStudIcon = event.map.registerIcon("claysoldiers:shield_studs");
            s_starfruitShieldIcon = event.map.registerIcon("claysoldiers:starfruit");
            s_starfruitShieldStudIcon = event.map.registerIcon("claysoldiers:starfruit_studs");
        }
    }

    static {
        CLAYMAN_LEATHER_ARMOR = new ResourceLocation("claysoldiers", "textures/entity/wearables/leather.png");
        CLAYMAN_PADDING = new ResourceLocation("claysoldiers", "textures/entity/wearables/padding.png");
        CLAYMAN_GUNPOWDER = new ResourceLocation("claysoldiers", "textures/entity/wearables/gunpowder.png");
        CLAYMAN_MAGMACREAM = new ResourceLocation("claysoldiers", "textures/entity/wearables/magmacream.png");
        CLAYMAN_SLIMEFEET = new ResourceLocation("claysoldiers", "textures/entity/wearables/slimefeet.png");
        CLAYMAN_CROWN = new ResourceLocation("claysoldiers", "textures/entity/wearables/crown.png");
        CLAYMAN_LILYPANTS = new ResourceLocation("claysoldiers", "textures/entity/wearables/lilypants.png");
        CLAYMAN_GOGGLES = new ResourceLocation("claysoldiers", "textures/entity/wearables/goggles.png");
        CLAYMAN_CAPE_BLANK = new ResourceLocation("claysoldiers", "textures/entity/wearables/cape_blank.png");
        CLAYMAN_CAPE_DIAMOND = new ResourceLocation("claysoldiers", "textures/entity/wearables/cape_diamond.png");
        CLAYMAN_GOLD_HOODIE = new ResourceLocation("claysoldiers", "textures/entity/wearables/gold_hoodie.png");
        NEXUS_TEXTURE = new ResourceLocation("claysoldiers", "textures/entity/nexus/normal.png");
        NEXUS_GLOWING = new ResourceLocation("claysoldiers", "textures/entity/nexus/glow_map.png");
        NEXUS_PARTICLE = new ResourceLocation("claysoldiers", "textures/entity/nexus/particle.png");
    }
}

