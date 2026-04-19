/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.IWorldGenerator
 *  cpw.mods.fml.common.event.FMLInitializationEvent
 *  cpw.mods.fml.common.event.FMLPostInitializationEvent
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 */
package zairus.worldexplorer.core;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import zairus.worldexplorer.core.WorldExplorer;
import zairus.worldexplorer.core.block.WorldExplorerBlocks;
import zairus.worldexplorer.core.client.IPlayerRenderer;
import zairus.worldexplorer.core.world.gen.feature.WorldGenBurial;

public class CommonProxy {
    protected List<IPlayerRenderer> playerRenderers = new ArrayList<IPlayerRenderer>();

    public void preInit(FMLPreInitializationEvent e) {
        WorldExplorerBlocks.init();
        for (int i = 0; i < WorldExplorer.getRegisteredMods().size(); ++i) {
            WorldExplorer.getRegisteredMods().get(i).getEntityManager().registerEntities();
        }
    }

    public void init(FMLInitializationEvent e) {
        for (int i = 0; i < WorldExplorer.getRegisteredMods().size(); ++i) {
            WorldExplorer.getRegisteredMods().get(i).getMonsterManager().registerMobs();
        }
        GameRegistry.registerWorldGenerator((IWorldGenerator)new WorldGenBurial(), (int)5);
    }

    public void postInit(FMLPostInitializationEvent e) {
    }

    public void displayBookJournalGUI(EntityPlayer player, ItemStack stack) {
    }

    public void registerPlayerRenderer(IPlayerRenderer ... renderers) {
    }

    public List<IPlayerRenderer> getPlayerRenderers() {
        return this.playerRenderers;
    }
}

