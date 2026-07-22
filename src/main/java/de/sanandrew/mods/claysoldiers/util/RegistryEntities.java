/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.registry.RenderingRegistry
 *  cpw.mods.fml.common.registry.EntityRegistry
 */
package de.sanandrew.mods.claysoldiers.util;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.mods.claysoldiers.client.render.entity.RenderClayMan;
import de.sanandrew.mods.claysoldiers.client.render.entity.mount.RenderBunnyMount;
import de.sanandrew.mods.claysoldiers.client.render.entity.mount.RenderGeckoMount;
import de.sanandrew.mods.claysoldiers.client.render.entity.mount.RenderHorseMount;
import de.sanandrew.mods.claysoldiers.client.render.entity.mount.RenderPegasusMount;
import de.sanandrew.mods.claysoldiers.client.render.entity.mount.RenderTurtleMount;
import de.sanandrew.mods.claysoldiers.client.render.entity.projectile.RenderBlockProjectile;
import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.entity.mount.EntityBunnyMount;
import de.sanandrew.mods.claysoldiers.entity.mount.EntityGeckoMount;
import de.sanandrew.mods.claysoldiers.entity.mount.EntityHorseMount;
import de.sanandrew.mods.claysoldiers.entity.mount.EntityPegasusMount;
import de.sanandrew.mods.claysoldiers.entity.mount.EntityTurtleMount;
import de.sanandrew.mods.claysoldiers.entity.projectile.EntityEmeraldChunk;
import de.sanandrew.mods.claysoldiers.entity.projectile.EntityFirechargeChunk;
import de.sanandrew.mods.claysoldiers.entity.projectile.EntityGravelChunk;
import de.sanandrew.mods.claysoldiers.entity.projectile.EntitySnowChunk;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.init.Blocks;

public final class RegistryEntities {
    public static void registerEntities(Object mod) {
        int entityId = 0;
        EntityRegistry.registerModEntity(EntityClayMan.class, (String)"clayman", (int)entityId++, (Object)mod, (int)64, (int)1, (boolean)true);
        EntityRegistry.registerModEntity(EntityHorseMount.class, (String)"horsemount", (int)entityId++, (Object)mod, (int)64, (int)1, (boolean)true);
        EntityRegistry.registerModEntity(EntityGravelChunk.class, (String)"gravelchunk", (int)entityId++, (Object)mod, (int)64, (int)1, (boolean)true);
        EntityRegistry.registerModEntity(EntitySnowChunk.class, (String)"snowchunk", (int)entityId++, (Object)mod, (int)64, (int)1, (boolean)true);
        EntityRegistry.registerModEntity(EntityFirechargeChunk.class, (String)"firechunk", (int)entityId++, (Object)mod, (int)64, (int)1, (boolean)true);
        EntityRegistry.registerModEntity(EntityEmeraldChunk.class, (String)"emeraldchunk", (int)entityId++, (Object)mod, (int)64, (int)1, (boolean)true);
        EntityRegistry.registerModEntity(EntityPegasusMount.class, (String)"pegasusmount", (int)entityId++, (Object)mod, (int)64, (int)1, (boolean)true);
        EntityRegistry.registerModEntity(EntityTurtleMount.class, (String)"turtlemount", (int)entityId++, (Object)mod, (int)64, (int)1, (boolean)true);
        EntityRegistry.registerModEntity(EntityBunnyMount.class, (String)"bunnymount", (int)entityId++, (Object)mod, (int)64, (int)1, (boolean)true);
        EntityRegistry.registerModEntity(EntityGeckoMount.class, (String)"geckomount", (int)entityId++, (Object)mod, (int)64, (int)1, (boolean)true);
    }

    @SideOnly(value=Side.CLIENT)
    public static void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityClayMan.class, (Render)new RenderClayMan());
        RenderingRegistry.registerEntityRenderingHandler(EntityHorseMount.class, (Render)new RenderHorseMount());
        RenderingRegistry.registerEntityRenderingHandler(EntityGravelChunk.class, (Render)new RenderBlockProjectile(Blocks.gravel));
        RenderingRegistry.registerEntityRenderingHandler(EntitySnowChunk.class, (Render)new RenderBlockProjectile(Blocks.snow));
        RenderingRegistry.registerEntityRenderingHandler(EntityFirechargeChunk.class, (Render)new RenderBlockProjectile(Blocks.lava));
        RenderingRegistry.registerEntityRenderingHandler(EntityPegasusMount.class, (Render)new RenderPegasusMount());
        RenderingRegistry.registerEntityRenderingHandler(EntityTurtleMount.class, (Render)new RenderTurtleMount());
        RenderingRegistry.registerEntityRenderingHandler(EntityBunnyMount.class, (Render)new RenderBunnyMount());
        RenderingRegistry.registerEntityRenderingHandler(EntityGeckoMount.class, (Render)new RenderGeckoMount());
    }
}

