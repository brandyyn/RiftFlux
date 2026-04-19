/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.event.FMLInitializationEvent
 *  cpw.mods.fml.common.event.FMLPostInitializationEvent
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 */
package zairus.worldexplorer.core;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import zairus.worldexplorer.core.CommonProxy;
import zairus.worldexplorer.core.WEKeyBindings;
import zairus.worldexplorer.core.WorldExplorer;
import zairus.worldexplorer.core.block.WorldExplorerBlocks;
import zairus.worldexplorer.core.client.IPlayerRenderer;
import zairus.worldexplorer.core.gui.GuiScreenJournal;

public class ClientProxy
extends CommonProxy {
    public static Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        WorldExplorerBlocks.registerRenderIds();
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        for (int i = 0; i < WorldExplorer.getRegisteredMods().size(); ++i) {
            WorldExplorer.getRegisteredMods().get(i).getRenderManager().registerRenderers();
        }
        WEKeyBindings.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }

    @Override
    public void displayBookJournalGUI(EntityPlayer player, ItemStack stack) {
        super.displayBookJournalGUI(player, stack);
        mc.displayGuiScreen((GuiScreen)new GuiScreenJournal(player, stack));
    }

    @Override
    public void registerPlayerRenderer(IPlayerRenderer ... renderers) {
        for (int i = 0; i < renderers.length; ++i) {
            this.playerRenderers.add(renderers[i]);
        }
    }
}

