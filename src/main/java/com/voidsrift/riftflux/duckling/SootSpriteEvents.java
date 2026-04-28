package com.voidsrift.riftflux.duckling;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.init.Blocks;
import net.minecraftforge.event.world.BlockEvent;

public class SootSpriteEvents {
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!ModConfig.enableDucklingModule
                || event == null
                || event.world == null
                || event.world.isRemote
                || event.block != Blocks.coal_ore
                || ModConfig.ghibliSootSpriteCoalOreSpawnChancePercent <= 0.0F
                || event.world.rand.nextFloat() * 100.0F >= ModConfig.ghibliSootSpriteCoalOreSpawnChancePercent) {
            return;
        }

        int minSpawns = Math.max(0, Math.min(ModConfig.ghibliSootSpriteCoalOreMinSpawns, ModConfig.ghibliSootSpriteCoalOreMaxSpawns));
        int maxSpawns = Math.max(minSpawns, Math.max(ModConfig.ghibliSootSpriteCoalOreMinSpawns, ModConfig.ghibliSootSpriteCoalOreMaxSpawns));
        if (maxSpawns <= 0) {
            return;
        }

        int count = minSpawns + event.world.rand.nextInt(maxSpawns - minSpawns + 1);
        for (int i = 0; i < count; i++) {
            EntitySootSprite sprite = new EntitySootSprite(event.world);
            double offsetX = count == 1 ? 0.5D : 0.25D + event.world.rand.nextDouble() * 0.5D;
            double offsetZ = count == 1 ? 0.5D : 0.25D + event.world.rand.nextDouble() * 0.5D;
            sprite.setLocationAndAngles(event.x + offsetX, event.y, event.z + offsetZ, event.world.rand.nextFloat() * 360.0F, 0.0F);
            event.world.spawnEntityInWorld(sprite);
        }
    }
}
