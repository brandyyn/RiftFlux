package inurosen.healaltar.common.core;

import inurosen.healaltar.common.gui.GuiSoulHearts;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy
extends CommonProxy {
    @Override
    public void registerRenderers() {
        MinecraftForge.EVENT_BUS.register((Object)new GuiSoulHearts(Minecraft.getMinecraft()));
    }
}
