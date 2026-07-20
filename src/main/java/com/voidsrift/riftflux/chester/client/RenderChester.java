package com.voidsrift.riftflux.chester.client;

import com.voidsrift.riftflux.chester.EntityChester;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderChester extends RenderLiving {
    private static final ResourceLocation NORMAL_TEXTURE =
            new ResourceLocation("chester:textures/models/chester.png");
    private static final ResourceLocation SHADOW_TEXTURE =
            new ResourceLocation("chester:textures/models/chester2.png");

    public RenderChester(ModelBase model, float shadowSize) {
        super(model, shadowSize);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return ((EntityChester) entity).getChesterType() == EntityChester.TYPE_SHADOW
                ? SHADOW_TEXTURE
                : NORMAL_TEXTURE;
    }
}
