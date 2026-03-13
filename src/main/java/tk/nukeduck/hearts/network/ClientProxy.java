/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.registry.ClientRegistry
 *  cpw.mods.fml.client.registry.RenderingRegistry
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 */
package tk.nukeduck.hearts.network;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import tk.nukeduck.hearts.block.TileEntityHeartCrystal;
import tk.nukeduck.hearts.block.TileEntityHeartLantern;
import tk.nukeduck.hearts.network.IProxy;
import tk.nukeduck.hearts.renderer.LanternRenderer;
import tk.nukeduck.hearts.renderer.HeartCrystalRenderer;

public class ClientProxy
implements IProxy {
    public static int renderId;

    @Override
    public void renderInit() {
        renderId = RenderingRegistry.getNextAvailableRenderId();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHeartLantern.class, (TileEntitySpecialRenderer)new LanternRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHeartCrystal.class, (TileEntitySpecialRenderer)new HeartCrystalRenderer());
    }
}
