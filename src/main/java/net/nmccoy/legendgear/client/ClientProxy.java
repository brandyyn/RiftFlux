/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.FMLClientHandler
 *  cpw.mods.fml.client.registry.ClientRegistry
 *  cpw.mods.fml.client.registry.RenderingRegistry
 *  net.minecraft.client.particle.EntityFX
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityXPOrb
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 */
package net.nmccoy.legendgear.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.nmccoy.legendgear.CommonProxy;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.block.TileEntityRitual;
import net.nmccoy.legendgear.block.TileEntityPlacedStar;
import net.nmccoy.legendgear.block.TileEntityStarstone;
import net.nmccoy.legendgear.block.TileEntityStarwell;
import net.nmccoy.legendgear.entity.EntityFallingStar;
import net.nmccoy.legendgear.entity.EntityHeart;
import net.nmccoy.legendgear.entity.EntityMagicBoomerang;
import net.nmccoy.legendgear.entity.EntityPing;
import net.nmccoy.legendgear.entity.EntitySpellEffect;
import net.nmccoy.legendgear.entity.EntityThrownOrb;
import net.nmccoy.legendgear.entity.SpellDecorator;
import net.nmccoy.legendgear.fx.EntityRippleFX;
import net.nmccoy.legendgear.fx.EntitySparkleFX;
import net.nmccoy.legendgear.render.GuiManaBar;
import net.nmccoy.legendgear.render.RenderBoomerang;
import net.nmccoy.legendgear.render.RenderFallingStar;
import net.nmccoy.legendgear.render.RenderHeart;
import net.nmccoy.legendgear.render.RenderPing;
import net.nmccoy.legendgear.render.RenderPrismaticXP;
import net.nmccoy.legendgear.render.RenderSpellDecoration;
import net.nmccoy.legendgear.render.RenderSpellReticle;
import net.nmccoy.legendgear.render.RenderThrownOrb;
import net.nmccoy.legendgear.render.TileEntityPlacedStarRender;
import net.nmccoy.legendgear.render.TileEntityRitualRender;
import net.nmccoy.legendgear.render.TileEntityStarstoneRender;
import net.nmccoy.legendgear.render.TileEntityStarwellRender;

public class ClientProxy
extends CommonProxy {
    public static int starglassRenderID;

    @Override
    public void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityFallingStar.class, (Render)new RenderFallingStar());
        RenderingRegistry.registerEntityRenderingHandler(SpellDecorator.class, (Render)new RenderSpellDecoration());
        RenderingRegistry.registerEntityRenderingHandler(EntityThrownOrb.class, (Render)new RenderThrownOrb(LegendGear2.emptyOrb));
        RenderingRegistry.registerEntityRenderingHandler(EntityHeart.class, (Render)new RenderHeart());
        RenderingRegistry.registerEntityRenderingHandler(EntityPing.class, (Render)new RenderPing());
        MinecraftForge.EVENT_BUS.register((Object)new RenderSpellReticle());
        MinecraftForge.EVENT_BUS.register((Object)new GuiManaBar());
        if (LegendGear2.CONFIG_FANCY_XP) {
            RenderingRegistry.registerEntityRenderingHandler(EntityXPOrb.class, (Render)new RenderPrismaticXP());
        }
        RenderingRegistry.registerEntityRenderingHandler(EntityMagicBoomerang.class, (Render)new RenderBoomerang());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStarstone.class, (TileEntitySpecialRenderer)new TileEntityStarstoneRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPlacedStar.class, (TileEntitySpecialRenderer)new TileEntityPlacedStarRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStarwell.class, (TileEntitySpecialRenderer)new TileEntityStarwellRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRitual.class, (TileEntitySpecialRenderer)new TileEntityRitualRender());
        DashRingClientHandler.bootstrap();
    }

    @Override
    public void addSparkleParticle(World world, double x, double y, double z, double vx, double vy, double vz, float scale) {
        if (world.isRemote) {
            EntitySparkleFX emrfx = new EntitySparkleFX(world, x, y, z, vx, vy, vz, scale);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect((EntityFX)emrfx);
        }
    }

    @Override
    public void addRippleParticle(World world, double x, double y, double z, double vx, double vy, double vz, float scale) {
        if (world.isRemote) {
            EntityRippleFX emrfx = new EntityRippleFX(world, x, y, z, vx, vy, vz, scale);
            FMLClientHandler.instance().getClient().effectRenderer.addEffect((EntityFX)emrfx);
        }
    }

    @Override
    public void decorateSpell(EntitySpellEffect spell) {
        if (spell.worldObj.isRemote) {
            spell.worldObj.spawnEntityInWorld((Entity)new SpellDecorator(spell));
        }
    }
}
