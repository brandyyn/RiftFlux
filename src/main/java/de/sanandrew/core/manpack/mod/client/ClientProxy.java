/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.registry.ClientRegistry
 *  cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler
 *  cpw.mods.fml.client.registry.RenderingRegistry
 *  cpw.mods.fml.common.FMLCommonHandler
 *  net.minecraftforge.common.MinecraftForge
 */
package de.sanandrew.core.manpack.mod.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.mod.CommonProxy;
import de.sanandrew.core.manpack.network.ClientPacketHandler;
import de.sanandrew.core.manpack.network.NetworkManager;
import de.sanandrew.core.manpack.network.PacketProcessor;

@SideOnly(value=Side.CLIENT)
public class ClientProxy
extends CommonProxy {
    @Override
    public void registerPacketHandler(String modId, String modChannel, PacketProcessor packetProcessor) {
        super.registerPacketHandler(modId, modChannel, packetProcessor);
        NetworkManager.getPacketChannel(modId).register((Object)new ClientPacketHandler(modId, modChannel));
    }
}
