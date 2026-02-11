package com.voidsrift.riftflux.avatar.appa;

import com.voidsrift.riftflux.avatar.util.FileLocation;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

public class RenderBison extends RenderLiving {
    private static final ResourceLocation res = new ResourceLocation(FileLocation.ENTITYTEXTURE + "Bison2.png");

    public RenderBison(ModelBase par1ModelBase, float par2) {
        super(par1ModelBase, par2);
    }

    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        this.doRender((EntityLiving) par1Entity, par2, par4, par6, par8, par9);
        if (((EntityBison) par1Entity).getName() != null && !((EntityBison) par1Entity).getName().isEmpty()) {
            this.func_147906_a(par1Entity, ((EntityBison) par1Entity).getName(), par2,
                    par4 + par1Entity.getMountedYOffset(), par6, 25);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        if (entity instanceof EntityBison) {
            return res;
        }
        return null;
    }
}
