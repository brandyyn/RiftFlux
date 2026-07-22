/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.client.event.RenderWorldLastEvent
 */
package de.sanandrew.mods.claysoldiers.client.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.mods.claysoldiers.client.particle.ParticleRenderDispatcher;
import net.minecraftforge.client.event.RenderWorldLastEvent;

@SideOnly(value=Side.CLIENT)
public class WorldOnLastRenderHandler {
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        ParticleRenderDispatcher.dispatch();
    }
}

