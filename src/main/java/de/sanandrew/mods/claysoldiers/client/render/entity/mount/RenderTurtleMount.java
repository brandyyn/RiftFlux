/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.client.render.entity.mount;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.mods.claysoldiers.client.model.mount.ModelTurtleMount;
import de.sanandrew.mods.claysoldiers.entity.mount.EntityTurtleMount;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SideOnly(value=Side.CLIENT)
public class RenderTurtleMount
extends RenderLiving {
    public RenderTurtleMount() {
        super(new ModelTurtleMount(), 0.3f);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return ((EntityTurtleMount)entity).getTurtleTexture();
    }
}

