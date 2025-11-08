package com.voidsrift.riftflux.mixin.late;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import hats.common.Hats;
import hats.common.core.CommonProxy;
import hats.common.core.EventHandler;
import hats.common.core.HatHandler;
import hats.common.core.HatInfo;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

@Mixin(value = EventHandler.class, remap = false)
public abstract class MixinHatsEventHandler {

    /**
     * Overwrites Hats' onEntitySpawn to remove the MobSpawnerBaseLogic scan that can NPE.
     * Only behavioral change: mobs from spawners are no longer specially detected
     * and can get random hats like normal spawns.
     */
    @Overwrite
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        // Same early returns as original
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()
                || !(event.entity instanceof EntityLivingBase)
                || !HatHandler.canMobHat((EntityLivingBase) event.entity)) {
            return;
        }

        if (CommonProxy.tickHandlerServer.mobHats.containsKey(event.entity)) {
            return;
        }

        EntityLivingBase living = (EntityLivingBase) event.entity;

        // We no longer try to detect "from spawner" by poking MobSpawnerBaseLogic.
        // That code was causing the NPE when logic == null.
        boolean fromSpawner = false;

        HatInfo hatInfo;

        // === NBT handling with MCP names ===
        if (living.getEntityData().hasKey("Hats_hatInfo")) {
            // Already has hat info
            hatInfo = new HatInfo(living.getEntityData().getString("Hats_hatInfo"));
        } else {
            // Random hat assignment (unchanged logic, just clearer names)
            boolean shouldHaveRandomHat =
                    living.getRNG().nextFloat()
                            < (float) Hats.config.getInt("randomMobHat") / 100.0F
                            && !fromSpawner;

            if (shouldHaveRandomHat) {
                hatInfo = HatHandler.getRandomHatFromList(
                        HatHandler.getHatsWithWeightedContributors(),
                        Hats.config.getSessionInt("playerHatsMode") == 4
                                && Hats.config.getInt("hatRarity") == 1
                );
            } else {
                hatInfo = new HatInfo();
            }

            living.getEntityData().setString("Hats_hatInfo", hatInfo.hatName);
        }

        if (!hatInfo.hatName.isEmpty()) {
            CommonProxy.tickHandlerServer.mobHats.put(living, hatInfo.hatName);
        }
    }
}