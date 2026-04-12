package com.voidsrift.riftflux.mixin.early.thaumcraft;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;

@Pseudo
@Mixin(targets = "thaumcraft.client.fx.ParticleEngine", remap = false)
public abstract class MixinThaumcraftParticleEngine_PhotoMode {

    private static final ResourceLocation RIFTFLUX_THAUMCRAFT_PARTICLE_TEXTURE_1 =
            new ResourceLocation("thaumcraft", "textures/misc/particles.png");
    private static final ResourceLocation RIFTFLUX_THAUMCRAFT_PARTICLE_TEXTURE_2 =
            new ResourceLocation("thaumcraft", "textures/misc/particles2.png");

    @Shadow
    private HashMap<Integer, ArrayList<EntityFX>>[] particles;

    @Inject(method = "onRenderWorldLast", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$renderThaumcraftParticlesFromPhotoCamera(RenderWorldLastEvent event, CallbackInfo ci) {
        if (!IsometricPhotoModeController.instance().isActive()) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.theWorld == null) {
            return;
        }

        Entity viewEntity = this.riftflux$getViewEntity(mc.thePlayer);
        if (viewEntity == null) {
            return;
        }

        float frame = event.partialTicks;
        int dimension = mc.theWorld.provider.dimensionId;
        TextureManager renderer = mc.renderEngine;

        renderer.bindTexture(RIFTFLUX_THAUMCRAFT_PARTICLE_TEXTURE_1);
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
        boolean reboundSecondTexture = false;

        for (int layer = 0; layer < 4; layer++) {
            if (this.particles[layer] == null) {
                continue;
            }

            ArrayList<EntityFX> parts = this.particles[layer].get(dimension);
            if (parts == null || parts.isEmpty()) {
                continue;
            }

            if (!reboundSecondTexture && layer >= 2) {
                renderer.bindTexture(RIFTFLUX_THAUMCRAFT_PARTICLE_TEXTURE_2);
                reboundSecondTexture = true;
            }

            GL11.glPushMatrix();
            switch (layer) {
                case 0:
                case 2:
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                    break;
                case 1:
                case 3:
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    break;
                default:
                    break;
            }

            float rotationX = ActiveRenderInfo.rotationX;
            float rotationZ = ActiveRenderInfo.rotationZ;
            float rotationYZ = ActiveRenderInfo.rotationYZ;
            float rotationXY = ActiveRenderInfo.rotationXY;
            float rotationXZ = ActiveRenderInfo.rotationXZ;

            EntityFX.interpPosX = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * (double) frame;
            EntityFX.interpPosY = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * (double) frame;
            EntityFX.interpPosZ = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * (double) frame;

            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            for (int particleIndex = 0; particleIndex < parts.size(); particleIndex++) {
                EntityFX particle = parts.get(particleIndex);
                if (particle == null) {
                    continue;
                }

                tessellator.setBrightness(particle.getBrightnessForRender(frame));
                particle.renderParticle(tessellator, frame, rotationX, rotationXZ, rotationZ, rotationYZ, rotationXY);
            }
            tessellator.draw();
            GL11.glPopMatrix();
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glPopMatrix();
        ci.cancel();
    }

    @Redirect(
            method = "onRenderWorldLast",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;lastTickPosX:D", opcode = 180),
            require = 0
    )
    private double riftflux$usePhotoCameraLastTickPosX(Entity entity) {
        return this.riftflux$getViewEntity(entity).lastTickPosX;
    }

    @Redirect(
            method = "onRenderWorldLast",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;posX:D", opcode = 180),
            require = 0
    )
    private double riftflux$usePhotoCameraPosX(Entity entity) {
        return this.riftflux$getViewEntity(entity).posX;
    }

    @Redirect(
            method = "onRenderWorldLast",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;lastTickPosY:D", opcode = 180),
            require = 0
    )
    private double riftflux$usePhotoCameraLastTickPosY(Entity entity) {
        return this.riftflux$getViewEntity(entity).lastTickPosY;
    }

    @Redirect(
            method = "onRenderWorldLast",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;posY:D", opcode = 180),
            require = 0
    )
    private double riftflux$usePhotoCameraPosY(Entity entity) {
        return this.riftflux$getViewEntity(entity).posY;
    }

    @Redirect(
            method = "onRenderWorldLast",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;lastTickPosZ:D", opcode = 180),
            require = 0
    )
    private double riftflux$usePhotoCameraLastTickPosZ(Entity entity) {
        return this.riftflux$getViewEntity(entity).lastTickPosZ;
    }

    @Redirect(
            method = "onRenderWorldLast",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;posZ:D", opcode = 180),
            require = 0
    )
    private double riftflux$usePhotoCameraPosZ(Entity entity) {
        return this.riftflux$getViewEntity(entity).posZ;
    }

    @Redirect(
            method = "onRenderWorldLast",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityClientPlayerMP;lastTickPosX:D", opcode = 180),
            require = 0
    )
    private double riftflux$usePhotoCameraLastTickPosXClientPlayer(EntityClientPlayerMP entity) {
        return this.riftflux$getViewEntity(entity).lastTickPosX;
    }

    @Redirect(
            method = "onRenderWorldLast",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityClientPlayerMP;posX:D", opcode = 180),
            require = 0
    )
    private double riftflux$usePhotoCameraPosXClientPlayer(EntityClientPlayerMP entity) {
        return this.riftflux$getViewEntity(entity).posX;
    }

    @Redirect(
            method = "onRenderWorldLast",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityClientPlayerMP;lastTickPosY:D", opcode = 180),
            require = 0
    )
    private double riftflux$usePhotoCameraLastTickPosYClientPlayer(EntityClientPlayerMP entity) {
        return this.riftflux$getViewEntity(entity).lastTickPosY;
    }

    @Redirect(
            method = "onRenderWorldLast",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityClientPlayerMP;posY:D", opcode = 180),
            require = 0
    )
    private double riftflux$usePhotoCameraPosYClientPlayer(EntityClientPlayerMP entity) {
        return this.riftflux$getViewEntity(entity).posY;
    }

    @Redirect(
            method = "onRenderWorldLast",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityClientPlayerMP;lastTickPosZ:D", opcode = 180),
            require = 0
    )
    private double riftflux$usePhotoCameraLastTickPosZClientPlayer(EntityClientPlayerMP entity) {
        return this.riftflux$getViewEntity(entity).lastTickPosZ;
    }

    @Redirect(
            method = "onRenderWorldLast",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityClientPlayerMP;posZ:D", opcode = 180),
            require = 0
    )
    private double riftflux$usePhotoCameraPosZClientPlayer(EntityClientPlayerMP entity) {
        return this.riftflux$getViewEntity(entity).posZ;
    }

    private Entity riftflux$getViewEntity(Entity fallback) {
        Entity viewEntity = Minecraft.getMinecraft().renderViewEntity;
        if (IsometricPhotoModeController.instance().isActive() && viewEntity != null) {
            return viewEntity;
        }

        return fallback;
    }

    private void riftflux$syncInterpWithPhotoCamera(float partialTicks) {
        if (!IsometricPhotoModeController.instance().isActive()) {
            return;
        }

        Entity viewEntity = Minecraft.getMinecraft().renderViewEntity;
        if (viewEntity == null) {
            return;
        }

        EntityFX.interpPosX = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * (double) partialTicks;
        EntityFX.interpPosY = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * (double) partialTicks;
        EntityFX.interpPosZ = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * (double) partialTicks;
    }
}
