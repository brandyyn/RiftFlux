package com.voidsrift.riftflux.combat.torohealth.client.particle;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class DamageParticles extends EntityFX {

    private static final float GRAVITY = 0.1F;
    private static final int LIFESPAN = 12;
    private static final Minecraft MC = Minecraft.getMinecraft();

    private final String text;
    private final int color;
    private float prevParticleScale;
    private boolean grow = true;

    private DamageParticles(int damage, World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.particleTextureJitterX = 0.0F;
        this.particleTextureJitterY = 0.0F;
        this.particleGravity = GRAVITY;
        this.particleScale = Math.max(0.1F, ModConfig.toroHealthParticleSize);
        this.prevParticleScale = this.particleScale;
        this.particleMaxAge = LIFESPAN;
        this.text = Integer.toString(Math.abs(damage));
        this.color = damage < 0 ? ModConfig.toroHealthHealColor : ModConfig.toroHealthDamageColor;
    }

    public static void spawnDamageParticle(EntityLivingBase entity, int damage) {
        if (!ModConfig.enableToroHealthModule || !ModConfig.toroHealthShowDamageParticles) {
            return;
        }
        if (IsometricPhotoModeController.instance().isActive()) {
            return;
        }
        if (entity == null || entity.worldObj == null) {
            return;
        }
        // Don't show damage/heal popoffs for the local player (self).
        if (entity == MC.thePlayer) {
            return;
        }

        final double motionX = entity.worldObj.rand.nextGaussian() * 0.02D;
        final double motionY = 0.5D;
        final double motionZ = entity.worldObj.rand.nextGaussian() * 0.02D;

        final DamageParticles indicator = new DamageParticles(
                damage,
                entity.worldObj,
                entity.posX,
                entity.posY + entity.height,
                entity.posZ,
                motionX,
                motionY,
                motionZ
        );
        MC.effectRenderer.addEffect(indicator);
    }

    /**
     * Returns true if there is an opaque/solid block between the camera and this particle.
     * Transparent blocks (like glass) do not occlude.
     */
    private boolean isOccludedBySolidBlocks(float partialTicks) {
        if (MC == null || MC.theWorld == null) {
            return false;
        }
        final Entity view = MC.renderViewEntity;
        if (view == null) {
            return false;
        }

        final double camX = view.prevPosX + (view.posX - view.prevPosX) * (double) partialTicks;
        final double camY = view.prevPosY + (view.posY - view.prevPosY) * (double) partialTicks + (double) view.getEyeHeight();
        final double camZ = view.prevPosZ + (view.posZ - view.prevPosZ) * (double) partialTicks;

        final double partX = this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks;
        final double partY = this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks;
        final double partZ = this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks;

        return hasOpaqueSolidBlockBetween(camX, camY, camZ, partX, partY, partZ);
    }

    private boolean hasOpaqueSolidBlockBetween(double x1, double y1, double z1, double x2, double y2, double z2) {
        final double dx = x2 - x1;
        final double dy = y2 - y1;
        final double dz = z2 - z1;
        final double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist <= 0.0001D) {
            return false;
        }

        final double step = 0.2D;
        final int steps = Math.max(1, (int) Math.ceil(dist / step));

        for (int i = 0; i <= steps; i++) {
            final double t = (double) i / (double) steps;
            final double sx = x1 + dx * t;
            final double sy = y1 + dy * t;
            final double sz = z1 + dz * t;

            final int bx = (int) Math.floor(sx);
            final int by = (int) Math.floor(sy);
            final int bz = (int) Math.floor(sz);

            final Block block = MC.theWorld.getBlock(bx, by, bz);
            if (block == null) {
                continue;
            }

            if (!MC.theWorld.isAirBlock(bx, by, bz)
                    && block.isOpaqueCube()
                    && block.getMaterial() != null
                    && block.getMaterial().isSolid()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void renderParticle(
            Tessellator tessellator,
            float partialTicks,
            float rotationX,
            float rotationZ,
            float rotationYZ,
            float rotationXY,
            float rotationXZ
    ) {
        if (!ModConfig.toroHealthShowThroughWalls && this.isOccludedBySolidBlocks(partialTicks)) {
            return;
        }

        final float relativeX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        final float relativeY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        final float relativeZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);

        final float rotationYaw;
        final float rotationPitch;
        if (MC.gameSettings.thirdPersonView != 2) {
            rotationYaw = MC.thePlayer.rotationYaw;
            rotationPitch = MC.thePlayer.rotationPitch;
        } else {
            rotationYaw = MC.thePlayer.rotationYaw + 180.0F;
            rotationPitch = -MC.thePlayer.rotationPitch;
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(relativeX, relativeY, relativeZ);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-rotationYaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(rotationPitch, 1.0F, 0.0F, 0.0F);

        final double scale = (this.prevParticleScale + (this.particleScale - this.prevParticleScale) * partialTicks) * 0.008D;
        GL11.glScaled(-scale, -scale, scale);
        GL11.glDisable(GL11.GL_LIGHTING);

        final int prevBrightness = this.getBrightnessForRender(partialTicks);
        final int prevX = prevBrightness % 65536;
        final int prevY = prevBrightness / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        final int halfWidth = MC.fontRenderer.getStringWidth(this.text) / 2;
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        MC.fontRenderer.drawStringWithShadow(this.text, -halfWidth, 0, this.color);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) prevX, (float) prevY);

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.prevParticleScale = this.particleScale;
        if (this.grow) {
            this.particleScale *= 1.1664F;
            if (this.particleScale > Math.max(0.1F, ModConfig.toroHealthParticleSize) * 3.0D) {
                this.grow = false;
            }
        } else {
            this.particleScale *= 0.8573F;
        }
    }

    @Override
    public int getFXLayer() {
        return 3;
    }
}
