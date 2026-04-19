/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraftforge.client.IItemRenderer
 *  net.minecraftforge.client.MinecraftForgeClient
 */
package zairus.worldexplorer.equipment.client.renderer;

import net.minecraft.item.Item;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import zairus.worldexplorer.core.IWEAddonRenderManager;
import zairus.worldexplorer.equipment.client.render.item.ItemSpyGlassRenderer;
import zairus.worldexplorer.equipment.items.WEEquipmentItems;

public class EquipmentRenderManager
implements IWEAddonRenderManager {
    @Override
    public void registerRenderers() {
        MinecraftForgeClient.registerItemRenderer((Item)WEEquipmentItems.spyglass, (IItemRenderer)new ItemSpyGlassRenderer());
    }
}

