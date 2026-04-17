package com.voidsrift.riftflux.pumpkinpastures.client.render;

import com.voidsrift.riftflux.Constants;
import com.voidsrift.riftflux.pumpkinpastures.client.model.ModelPumpkinCreeper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderPumpkinCreeper extends RenderLiving {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(Constants.MODID, "textures/entity/pumpkinpastures/pumpkin_creeper.png");

    public RenderPumpkinCreeper() {
        super(new ModelPumpkinCreeper(), 0.6F);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTick) {
        GL11.glScalef(1.05F, 1.05F, 1.05F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TEXTURE;
    }
}
