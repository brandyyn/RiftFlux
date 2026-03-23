package com.voidsrift.riftflux.axolotl;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderAxolotl extends RenderLiving {
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
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if (entity instanceof EntityAxolotl) {
            return TEXTURES[((EntityAxolotl) entity).getVariant().getId()];
        }
        return TEXTURES[0];
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTickTime) {
        if (entity instanceof EntityAxolotl && ((EntityAxolotl) entity).isPlayingDead()) {
            GL11.glTranslatef(0.0F, 0.05F, 0.0F);
        }
    }
}
