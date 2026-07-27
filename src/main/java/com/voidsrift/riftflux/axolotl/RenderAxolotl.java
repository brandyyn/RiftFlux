package com.voidsrift.riftflux.axolotl;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderAxolotl extends RenderLiving {
    private static final ResourceLocation PERRY_TEXTURE =
            new ResourceLocation("riftflux:textures/entities/axolotl/axolotl_perry.png");
    private static final ResourceLocation PERRY_HAT_TEXTURE =
            new ResourceLocation("ghibli:textures/entity/agent_d.png");
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
            new ResourceLocation("riftflux:textures/entities/axolotl/axolotl_lucy.png"),
            new ResourceLocation("riftflux:textures/entities/axolotl/axolotl_wild.png"),
            new ResourceLocation("riftflux:textures/entities/axolotl/axolotl_gold.png"),
            new ResourceLocation("riftflux:textures/entities/axolotl/axolotl_cyan.png"),
            new ResourceLocation("riftflux:textures/entities/axolotl/axolotl_blue.png"),
            new ResourceLocation("riftflux:textures/entities/axolotl/axolotl_lavender.png"),
            new ResourceLocation("riftflux:textures/entities/axolotl/axolotl_moss.png"),
            new ResourceLocation("riftflux:textures/entities/axolotl/axolotl_slate.png"),
            new ResourceLocation("riftflux:textures/entities/axolotl/axolotl_rose.png"),
            new ResourceLocation("riftflux:textures/entities/axolotl/axolotl_mint.png"),
            new ResourceLocation("riftflux:textures/entities/axolotl/axolotl_amber.png")
    };

    public RenderAxolotl() {
        super(new ModelAxolotl(), 0.3F);
        this.setRenderPassModel(new ModelAxolotl(true));
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if (entity instanceof EntityAxolotl) {
            EntityAxolotl axolotl = (EntityAxolotl) entity;
            return axolotl.hasPerrySkin() ? PERRY_TEXTURE : TEXTURES[axolotl.getVariant().getId()];
        }
        return TEXTURES[0];
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTickTime) {
        if (pass == 0 && entity instanceof EntityAxolotl && ((EntityAxolotl) entity).hasPerrySkin()) {
            this.bindTexture(PERRY_HAT_TEXTURE);
            return 1;
        }
        return -1;
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTickTime) {
        if (entity instanceof EntityAxolotl && ((EntityAxolotl) entity).isPlayingDead()) {
            GL11.glTranslatef(0.0F, 0.05F, 0.0F);
        }
    }
}
