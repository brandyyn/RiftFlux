/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.registry.RenderingRegistry
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.client.renderer.entity.RenderSkeleton
 *  net.minecraft.item.Item
 *  net.minecraftforge.client.IItemRenderer
 *  net.minecraftforge.client.MinecraftForgeClient
 */
package zairus.worldexplorer.archery.client.renderer;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.item.Item;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import zairus.worldexplorer.archery.client.renderer.PlayerQuiverRenderer;
import zairus.worldexplorer.archery.client.renderer.entity.RenderEntityBoomerang;
import zairus.worldexplorer.archery.client.renderer.entity.RenderEntityDart;
import zairus.worldexplorer.archery.client.renderer.entity.RenderEntityPebble;
import zairus.worldexplorer.archery.client.renderer.entity.RenderEntitySpecialArrow;
import zairus.worldexplorer.archery.client.renderer.item.ItemBlowPipeRenderer;
import zairus.worldexplorer.archery.client.renderer.item.ItemBoomerangRenderer;
import zairus.worldexplorer.archery.client.renderer.item.ItemBowRenderer;
import zairus.worldexplorer.archery.entity.EntityBoomerang;
import zairus.worldexplorer.archery.entity.EntityDart;
import zairus.worldexplorer.archery.entity.EntityPebble;
import zairus.worldexplorer.archery.entity.EntitySpecialArrow;
import zairus.worldexplorer.archery.entity.monster.EntitySkeletonExplorer;
import zairus.worldexplorer.archery.items.WEArcheryItems;
import zairus.worldexplorer.core.IWEAddonRenderManager;
import zairus.worldexplorer.core.WorldExplorer;

public class ArcheryRenderManager
implements IWEAddonRenderManager {
    @Override
    public void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityPebble.class, (Render)new RenderEntityPebble());
        RenderingRegistry.registerEntityRenderingHandler(EntityBoomerang.class, (Render)new RenderEntityBoomerang());
        RenderingRegistry.registerEntityRenderingHandler(EntityDart.class, (Render)new RenderEntityDart());
        RenderingRegistry.registerEntityRenderingHandler(EntitySpecialArrow.class, (Render)new RenderEntitySpecialArrow());
        RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonExplorer.class, (Render)new RenderSkeleton());
        MinecraftForgeClient.registerItemRenderer((Item)WEArcheryItems.longbow, (IItemRenderer)new ItemBowRenderer());
        MinecraftForgeClient.registerItemRenderer((Item)WEArcheryItems.boomerang, (IItemRenderer)new ItemBoomerangRenderer());
        MinecraftForgeClient.registerItemRenderer((Item)WEArcheryItems.blowpipe, (IItemRenderer)new ItemBlowPipeRenderer());
        WorldExplorer.proxy.registerPlayerRenderer(new PlayerQuiverRenderer());
    }
}
