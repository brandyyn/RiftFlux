/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.IModGuiFactory
 *  cpw.mods.fml.client.IModGuiFactory$RuntimeOptionCategoryElement
 *  cpw.mods.fml.client.IModGuiFactory$RuntimeOptionGuiHandler
 *  cpw.mods.fml.client.registry.RenderingRegistry
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.gameevent.TickEvent$RenderTickEvent
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.item.Item
 *  net.minecraftforge.client.IItemRenderer
 *  net.minecraftforge.client.MinecraftForgeClient
 *  net.minecraftforge.client.event.FOVUpdateEvent
 *  net.minecraftforge.client.event.RenderPlayerEvent$Pre
 */
package iDiamondhunter.morebows;

import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import iDiamondhunter.morebows.MoreBows;
import iDiamondhunter.morebows.a;
import iDiamondhunter.morebows.b;
import iDiamondhunter.morebows.c;
import iDiamondhunter.morebows.e;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.item.Item;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;

public final class Client
extends MoreBows
implements IModGuiFactory {
    public static float a = 0.0f;

    @SubscribeEvent
    public final void a(RenderPlayerEvent.Pre pre) {
        if (pre.entityPlayer.getItemInUse() != null && pre.entityPlayer.getItemInUse().getItem() instanceof b) {
            pre.renderer.modelBipedMain.aimedBow = true;
            pre.renderer.modelArmor.aimedBow = true;
            pre.renderer.modelArmorChestplate.aimedBow = true;
        }
    }

    @SubscribeEvent
    public final void a(TickEvent.RenderTickEvent renderTickEvent) {
        a = renderTickEvent.renderTickTime;
    }

    @SubscribeEvent
    public final void a(FOVUpdateEvent fOVUpdateEvent) {
        if (fOVUpdateEvent.entity.getItemInUse() != null && fOVUpdateEvent.entity.getItemInUse().getItem() instanceof b) {
            float f2 = (float)(72000 - fOVUpdateEvent.entity.getItemInUseCount()) / ((float)((b)fOVUpdateEvent.entity.getItemInUse().getItem()).var_byte_arr_a[0] * 1.1f);
            if (f2 > 1.0f) {
                f2 = 1.0f;
            } else {
                float f3 = f2;
                f2 = f3 * f3;
            }
            fOVUpdateEvent.newfov *= 1.0f - f2 * 0.15f;
        }
    }

    public final IModGuiFactory.RuntimeOptionGuiHandler getHandlerFor(IModGuiFactory.RuntimeOptionCategoryElement runtimeOptionCategoryElement) {
        return null;
    }

    public final void initialize(Minecraft minecraft) {
    }

    public final Class mainConfigGuiClass() {
        return a.class;
    }

    protected final void a() {
        super.a();
        RenderingRegistry.registerEntityRenderingHandler(e.class, (Render)new c());
        MinecraftForgeClient.registerItemRenderer((Item)MoreBows.var_net_minecraft_item_Item_a, (IItemRenderer)new c());
        MinecraftForgeClient.registerItemRenderer((Item)MoreBows.e, (IItemRenderer)new c());
        MinecraftForgeClient.registerItemRenderer((Item)MoreBows.var_net_minecraft_item_Item_b, (IItemRenderer)new c());
        MinecraftForgeClient.registerItemRenderer((Item)MoreBows.h, (IItemRenderer)new c());
        MinecraftForgeClient.registerItemRenderer((Item)MoreBows.f, (IItemRenderer)new c());
        MinecraftForgeClient.registerItemRenderer((Item)MoreBows.g, (IItemRenderer)new c());
        MinecraftForgeClient.registerItemRenderer((Item)MoreBows.var_net_minecraft_item_Item_c, (IItemRenderer)new c());
        MinecraftForgeClient.registerItemRenderer((Item)MoreBows.d, (IItemRenderer)new c());
    }

    public final Set runtimeGuiCategories() {
        return null;
    }
}
