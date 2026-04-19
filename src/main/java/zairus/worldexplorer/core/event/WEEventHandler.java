/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.gameevent.PlayerEvent$ItemCraftedEvent
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.client.event.FOVUpdateEvent
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$ElementType
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$Pre
 *  net.minecraftforge.client.event.RenderPlayerEvent$Post
 *  net.minecraftforge.event.entity.player.EntityItemPickupEvent
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent
 */
package zairus.worldexplorer.core.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import zairus.worldexplorer.core.WorldExplorer;
import zairus.worldexplorer.core.client.IPlayerRenderer;
import zairus.worldexplorer.core.items.WEItem;
import zairus.worldexplorer.core.items.WorldExplorerItems;
import zairus.worldexplorer.core.player.CorePlayerManager;

public class WEEventHandler {
    @SubscribeEvent
    public void itemCrafted(PlayerEvent.ItemCraftedEvent event) {
        CorePlayerManager.unlockItem(event.player, event.crafting.getItem());
        if (event.crafting.getItem() == WorldExplorerItems.journal) {
            // empty if block
        }
    }

    @SubscribeEvent
    @SideOnly(value=Side.CLIENT)
    public void onFOVUpdate(FOVUpdateEvent event) {
        WEItem item;
        if (!event.entity.isUsingItem()) {
            return;
        }
        if (event.entity.getItemInUse().getItem() instanceof WEItem && (item = (WEItem)event.entity.getItemInUse().getItem()).updatesFOV()) {
            event.newfov = event.fov / (event.fov + item.getFOVValue() * this.getItemInUsePercentaje(event.entity, item.getFOVSpeedFactor()));
        }
    }

    @SideOnly(value=Side.CLIENT)
    private float getItemInUsePercentaje(EntityPlayerSP player, float speedFactor) {
        float maxUse = player.getItemInUse().getItem().getMaxItemUseDuration(player.getItemInUse());
        float curUse = player.getItemInUseCount();
        float percent = 0.0f;
        percent *= speedFactor * ((percent = maxUse - curUse) > 100.0f ? 1.0f : percent / 100.0f);
        percent /= maxUse;
        percent *= 1.0f + percent;
        return percent > 1.0f ? 1.0f : percent;
    }

    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event) {
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
    }

    @SubscribeEvent
    @SideOnly(value=Side.CLIENT)
    public void onRender(RenderPlayerEvent.Post event) {
        List<IPlayerRenderer> renderers = WorldExplorer.proxy.getPlayerRenderers();
        for (int i = 0; i < renderers.size(); ++i) {
            renderers.get(i).render(event.entityPlayer);
        }
    }

    @SubscribeEvent
    @SideOnly(value=Side.CLIENT)
    public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        WEItem item;
        ItemStack usingStack = Minecraft.getMinecraft().thePlayer.getItemInUse();
        if (usingStack != null && usingStack.getItem() instanceof WEItem && (item = (WEItem)usingStack.getItem()).getUseOverlay() != null && event.type == RenderGameOverlayEvent.ElementType.ALL) {
            item.getUseOverlay().draw();
        }
    }
}

