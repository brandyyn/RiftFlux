/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package de.sanandrew.mods.claysoldiers.client.render.entity.mount;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.mods.claysoldiers.client.model.mount.ModelHorseMount;
import de.sanandrew.mods.claysoldiers.entity.mount.EntityHorseMount;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class RenderHorseMount
extends RenderLiving {
    public RenderHorseMount() {
        super(new ModelHorseMount(), 0.2f);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase livingBase, float partTicks) {
        GL11.glTranslatef((float)0.0f, (float)0.36f, (float)0.0f);
        GL11.glScalef((float)0.5f, (float)0.5f, (float)0.5f);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return ((EntityHorseMount)entity).getHorseTexture();
    }
}

