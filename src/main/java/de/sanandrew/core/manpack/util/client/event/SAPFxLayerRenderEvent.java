/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.Event
 */
package de.sanandrew.core.manpack.util.client.event;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;

@SideOnly(value=Side.CLIENT)
public class SAPFxLayerRenderEvent
extends Event {
    public final int layerId;

    public SAPFxLayerRenderEvent(int layer) {
        this.layerId = layer;
    }

    public static class Post
    extends SAPFxLayerRenderEvent {
        public Post(int layer) {
            super(layer);
        }
    }

    public static class Pre
    extends SAPFxLayerRenderEvent {
        public final Tessellator tessellator;

        public Pre(int layer, Tessellator tess) {
            super(layer);
            this.tessellator = tess;
        }
    }
}

