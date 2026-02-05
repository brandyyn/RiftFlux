/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.FMLClientHandler
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraftforge.common.MinecraftForge
 */
package zelda.proxy;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.MinecraftForge;
import zelda.GuiHUD;
import zelda.proxy.CommonProxy;

@SideOnly(value=Side.CLIENT)
public class ClientProxy
extends CommonProxy {
    @Override
    public void registerClientStuff() {
        MinecraftForge.EVENT_BUS.register((Object)new GuiHUD(FMLClientHandler.instance().getClient()));
    }
}

