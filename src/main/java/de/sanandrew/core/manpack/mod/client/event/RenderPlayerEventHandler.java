/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.client.event.RenderHandEvent
 *  net.minecraftforge.client.event.RenderLivingEvent$Pre
 *  net.minecraftforge.client.event.RenderPlayerEvent$Pre
 *  org.lwjgl.opengl.GL11
 */
package de.sanandrew.core.manpack.mod.client.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.mod.client.render.RenderSanPlayer;
import de.sanandrew.core.manpack.util.ReflectionNames;
import de.sanandrew.core.manpack.util.SAPReflectionHelper;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class RenderPlayerEventHandler {
    private static final String[] SANPLAYER_NAMES_UUID = new String[]{"SanAndreasP", "044d980d-5c2a-4030-95cf-cbfde69ea3cb"};
    private RenderSanPlayer sanPlayerModel = null;
    private float playerPartTicks = 0.0f;

    private void lazyLoad() {
        if (this.sanPlayerModel == null) {
            this.sanPlayerModel = new RenderSanPlayer();
            this.sanPlayerModel.setRenderManager(RenderManager.instance);
            if (Minecraft.getMinecraft().getResourceManager() instanceof SimpleReloadableResourceManager) {
                ((SimpleReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this.sanPlayerModel);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerRender(RenderPlayerEvent.Pre event) {
        this.lazyLoad();
        if (SAPUtils.isPlayerNameOrUuidEqual(event.entityPlayer, SANPLAYER_NAMES_UUID)) {
            this.playerPartTicks = event.partialRenderTick;
        }
    }

    @SubscribeEvent
    public void onLivingRender(RenderLivingEvent.Pre event) {
        this.lazyLoad();
        if (event.entity instanceof EntityPlayer && event.renderer != this.sanPlayerModel && SAPUtils.isPlayerNameOrUuidEqual((EntityPlayer)event.entity, SANPLAYER_NAMES_UUID)) {
            this.sanPlayerModel.doRender(event.entity, event.x, event.y + (double)event.entity.yOffset, event.z, 0.0f, this.playerPartTicks);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onHandRender(RenderHandEvent event) {
        this.lazyLoad();
        GL11.glPushMatrix();
        Minecraft mc = Minecraft.getMinecraft();
        if (SAPUtils.isPlayerNameOrUuidEqual(mc.thePlayer, SANPLAYER_NAMES_UUID)) {
            event.setCanceled(true);
            GL11.glClear((int)256);
            RenderPlayer rend = (RenderPlayer)RenderManager.instance.getEntityRenderObject(mc.thePlayer);
            RenderManager.instance.entityRenderMap.put(mc.thePlayer.getClass(), this.sanPlayerModel);
            SAPReflectionHelper.invokeCachedMethod(EntityRenderer.class, mc.entityRenderer, ReflectionNames.RENDER_HAND.mcpName, ReflectionNames.RENDER_HAND.srgName, new Class[]{Float.TYPE, Integer.TYPE}, new Object[]{Float.valueOf(event.partialTicks), event.renderPass});
            RenderManager.instance.entityRenderMap.put(mc.thePlayer.getClass(), rend);
        }
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glPopMatrix();
    }
}

