/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package de.sanandrew.mods.claysoldiers.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.mods.claysoldiers.client.particle.ParticleNexusFX;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public final class ParticleRenderDispatcher {
    public static void dispatch() {
        Tessellator tessellator = Tessellator.instance;
        GL11.glDepthMask((boolean)false);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)1);
        GL11.glAlphaFunc((int)516, (float)0.003921569f);
        ParticleNexusFX.dispatchQueuedRenders(tessellator);
        GL11.glAlphaFunc((int)516, (float)0.1f);
        GL11.glDisable((int)3042);
        GL11.glDepthMask((boolean)true);
    }
}

