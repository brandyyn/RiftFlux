package com.voidsrift.riftflux.client;

import com.voidsrift.riftflux.Constants;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.mixin.accessor.MinecraftAccessor;
import com.voidsrift.riftflux.pets.PetKnockdown;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public final class PetKnockdownDizzyStarRenderer {
    private static final PetKnockdownDizzyStarRenderer INSTANCE = new PetKnockdownDizzyStarRenderer();
    private static final ResourceLocation STAR_TEXTURE =
            new ResourceLocation(Constants.MODID, "textures/entity/pet_knockdown_star.png");
    private static final int STAR_COUNT = 4;
    private static final double MAX_DISTANCE = 64.0D;
    private static final int FULL_BRIGHT = 0xF000F0;
    private static boolean bootstrapped;

    private PetKnockdownDizzyStarRenderer() {
    }

    public static void bootstrap() {
        if (bootstrapped) {
            return;
        }
        bootstrapped = true;
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void onRenderLivingPost(RenderLivingEvent.Post event) {
        if (!ModConfig.showPetKnockdownDizzyStars || !(event.entity instanceof EntityLivingBase)) {
            return;
        }

        EntityLivingBase entity = (EntityLivingBase) event.entity;
        if (!PetKnockdown.isKnockedDown(entity)) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null) {
            return;
        }
        Entity view = mc.renderViewEntity != null ? mc.renderViewEntity : mc.thePlayer;
        if (entity.getDistanceSqToEntity(view) > MAX_DISTANCE * MAX_DISTANCE) {
            return;
        }

        Timer timer = ((MinecraftAccessor) mc).riftflux$getTimer();
        float partialTicks = timer == null ? 0.0F : timer.renderPartialTicks;
        renderStars(entity, event.x, event.y, event.z, partialTicks);
    }

    private static void renderStars(
            EntityLivingBase entity,
            double x,
            double y,
            double z,
            float partialTicks
    ) {
        Minecraft mc = Minecraft.getMinecraft();
        RenderManager renderManager = RenderManager.instance;
        float time = (float) entity.ticksExisted + partialTicks;
        double radius = Math.max(0.55D, Math.min(1.5D, (double) entity.width * 0.75D));
        double centerY = y
                + (double) entity.height
                + (double) ModConfig.petKnockdownDizzyStarHeightOffset;
        float starSize = Math.max(0.24F, Math.min(0.45F, entity.width * 0.28F));

        int previousActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        float previousBrightnessX = OpenGlHelper.lastBrightnessX;
        float previousBrightnessY = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        mc.getTextureManager().bindTexture(STAR_TEXTURE);
        OpenGlHelper.setLightmapTextureCoords(
                OpenGlHelper.lightmapTexUnit,
                FULL_BRIGHT % 65536,
                FULL_BRIGHT / 65536
        );

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            for (int i = 0; i < STAR_COUNT; ++i) {
                float orbitDegrees = time * 7.0F + (360.0F / (float) STAR_COUNT) * (float) i;
                double orbitRadians = Math.toRadians((double) orbitDegrees);
                double starX = x + Math.cos(orbitRadians) * radius;
                double starZ = z + Math.sin(orbitRadians) * radius;
                double starY = centerY + Math.sin(orbitRadians * 2.0D) * 0.10D;
                float spinDegrees = ModConfig.rotatePetKnockdownDizzyStars
                        ? time * 13.0F + (float) i * 37.0F
                        : 0.0F;
                renderStar(
                        renderManager,
                        starX,
                        starY,
                        starZ,
                        starSize,
                        spinDegrees
                );
            }
        } finally {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopAttrib();
            OpenGlHelper.setLightmapTextureCoords(
                    OpenGlHelper.lightmapTexUnit,
                    previousBrightnessX,
                    previousBrightnessY
            );
            OpenGlHelper.setActiveTexture(previousActiveTexture);
        }
    }

    private static void renderStar(
            RenderManager renderManager,
            double x,
            double y,
            double z,
            float size,
            float spinDegrees
    ) {
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(x, y, z);
            GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(spinDegrees, 0.0F, 0.0F, 1.0F);

            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(-size, size, 0.0D, 0.0D, 0.0D);
            tessellator.addVertexWithUV(size, size, 0.0D, 1.0D, 0.0D);
            tessellator.addVertexWithUV(size, -size, 0.0D, 1.0D, 1.0D);
            tessellator.addVertexWithUV(-size, -size, 0.0D, 0.0D, 1.0D);
            tessellator.draw();
        } finally {
            GL11.glPopMatrix();
        }
    }
}
