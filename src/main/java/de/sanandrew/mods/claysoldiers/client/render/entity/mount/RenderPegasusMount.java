/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package de.sanandrew.mods.claysoldiers.client.render.entity.mount;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.mods.claysoldiers.client.model.mount.ModelPegasusMount;
import de.sanandrew.mods.claysoldiers.entity.mount.EntityPegasusMount;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class RenderPegasusMount
extends RenderLiving {
    public RenderPegasusMount() {
        super(new ModelPegasusMount(), 0.2f);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase livingBase, float partTicks) {
        GL11.glTranslatef((float)0.0f, (float)0.36f, (float)0.0f);
        GL11.glScalef((float)0.5f, (float)0.5f, (float)0.5f);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return ((EntityPegasusMount)entity).getHorseTexture();
    }
}

