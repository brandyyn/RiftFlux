/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.gameevent.TickEvent$ClientTickEvent
 *  cpw.mods.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.client.event.RenderWorldLastEvent
 */
package de.sanandrew.core.manpack.mod.client.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.mod.client.particle.SAPEffectRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;

@SideOnly(value=Side.CLIENT)
public class EventWorldRenderLast {
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.entityRenderer.debugViewDirection == 0) {
            mc.entityRenderer.enableLightmap(event.partialTicks);
            SAPEffectRenderer.INSTANCE.renderParticles(Minecraft.getMinecraft().renderViewEntity, event.partialTicks, false);
            SAPEffectRenderer.INSTANCE.renderParticles(Minecraft.getMinecraft().renderViewEntity, event.partialTicks, true);
            mc.entityRenderer.disableLightmap(event.partialTicks);
        }
    }

    @SubscribeEvent
    public void onClientPostTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !Minecraft.getMinecraft().isGamePaused()) {
            SAPEffectRenderer.INSTANCE.updateEffects();
        }
    }
}

