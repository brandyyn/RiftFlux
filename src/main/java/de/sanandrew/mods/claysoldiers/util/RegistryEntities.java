/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.registry.RenderingRegistry
 *  cpw.mods.fml.common.registry.EntityRegistry
 */
package de.sanandrew.mods.claysoldiers.util;

import cpw.mods.fml.client.registry.RenderingRegistry;
import com.voidsrift.riftflux.entity.RiftFluxEntityRegistry;
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
        RiftFluxEntityRegistry.registerModEntity(EntityClayMan.class, "clayman", mod, 64, 1, true);
        RiftFluxEntityRegistry.registerModEntity(EntityHorseMount.class, "horsemount", mod, 64, 1, true);
        RiftFluxEntityRegistry.registerModEntity(EntityGravelChunk.class, "gravelchunk", mod, 64, 1, true);
        RiftFluxEntityRegistry.registerModEntity(EntitySnowChunk.class, "snowchunk", mod, 64, 1, true);
        RiftFluxEntityRegistry.registerModEntity(EntityFirechargeChunk.class, "firechunk", mod, 64, 1, true);
        RiftFluxEntityRegistry.registerModEntity(EntityEmeraldChunk.class, "emeraldchunk", mod, 64, 1, true);
        RiftFluxEntityRegistry.registerModEntity(EntityPegasusMount.class, "pegasusmount", mod, 64, 1, true);
        RiftFluxEntityRegistry.registerModEntity(EntityTurtleMount.class, "turtlemount", mod, 64, 1, true);
        RiftFluxEntityRegistry.registerModEntity(EntityBunnyMount.class, "bunnymount", mod, 64, 1, true);
        RiftFluxEntityRegistry.registerModEntity(EntityGeckoMount.class, "geckomount", mod, 64, 1, true);
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
