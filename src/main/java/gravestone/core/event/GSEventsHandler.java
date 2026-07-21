package gravestone.core.event;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import gravestone.block.BlockGSGraveStone;
import gravestone.block.GraveStoneHelper;
import gravestone.config.GraveStoneConfig;
import gravestone.core.GSMobSpawn;
import gravestone.core.GraveStoneDeathInventory;
import gravestone.core.MobHandler;
import gravestone.core.compatibility.GSCompatibilityWitchery;
import gravestone.core.logger.GravesLogger;
import gravestone.entity.monster.EntitySkullCrawler;
import gravestone.entity.monster.EntityWitherSkullCrawler;
import gravestone.entity.monster.EntityZombieSkullCrawler;
import gravestone.item.corpse.CorpseHelper;
import gravestone.item.enums.EnumCorpse;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent.Load;

public class GSEventsHandler {
   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onEntityLivingDeath(LivingDeathEvent event) {
      if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
         if (GraveStoneConfig.enablePlayerDeathGraves && event.entityLiving instanceof EntityPlayer
               && !GSCompatibilityWitchery.isVampire((EntityPlayer)event.entity, event.source)) {
            GraveStoneDeathInventory.captureConfiguredItems((EntityPlayer)event.entityLiving);
         }

         if (!GraveStoneConfig.generateGravesInLava && event.source.damageType.equals("lava")) {
            return;
         }

         if (GraveStoneConfig.enablePlayerDeathGraves && GraveStoneConfig.generatePlayerGraves && event.entityLiving instanceof EntityPlayer && !GSCompatibilityWitchery.isVampire((EntityPlayer)event.entity, event.source)) {
            GraveStoneHelper.createPlayerGrave((EntityPlayer)event.entity, event, MobHandler.getAndRemoveSpawnTime(event.entity));
         } else {
            if (GraveStoneConfig.generateVillagerGraves && event.entity instanceof EntityVillager) {
               GraveStoneHelper.createGrave(event.entity, event, CorpseHelper.getCorpse(event.entity, EnumCorpse.VILLAGER), BlockGSGraveStone.EnumGraveType.PLAYER_GRAVES, true, MobHandler.getAndRemoveSpawnTime(event.entity));
               return;
            }

            if (GraveStoneConfig.generatePetGraves && event.entity instanceof EntityTameable) {
               GraveStoneHelper.createPetGrave(event.entity, event, MobHandler.getAndRemoveSpawnTime(event.entity));
               return;
            }

            if (GraveStoneConfig.generatePetGraves && event.entity instanceof EntityHorse) {
               GraveStoneHelper.createHorseGrave((EntityHorse)event.entity, event, MobHandler.getAndRemoveSpawnTime(event.entity));
               return;
            }
         }

         if (GraveStoneConfig.spawnSkullCrawlersAtMobsDeath) {
            if (event.entity instanceof EntitySkeleton) {
               if (GSMobSpawn.isWitherSkeleton((EntitySkeleton)event.entity)) {
                  GSMobSpawn.spawnCrawler(event.entity, new EntityWitherSkullCrawler(event.entity.worldObj));
               } else {
                  GSMobSpawn.spawnCrawler(event.entity, new EntitySkullCrawler(event.entity.worldObj));
               }

               return;
            }

            if (event.entity instanceof EntityZombie) {
               GSMobSpawn.spawnCrawler(event.entity, new EntityZombieSkullCrawler(event.entity.worldObj));
            }
         }
      }

   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   public void onPlayerClone(PlayerEvent.Clone event) {
      if (event.wasDeath && FMLCommonHandler.instance().getEffectiveSide().isServer()) {
         GraveStoneDeathInventory.restoreConfiguredItems(event.original, event.entityPlayer);
      }
   }

   @SubscribeEvent
   public void entityJoinWorldEvent(EntityJoinWorldEvent event) {
      if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
         Entity entity = event.entity;
         if (entity instanceof EntityVillager || entity instanceof EntityWolf || entity instanceof EntityOcelot || entity instanceof EntityHorse) {
            MobHandler.setMobSpawnTime(event.entity);
         }

      }
   }

   @SubscribeEvent
   public void worldLoading(Load event) {
      if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
         MobHandler.loadMobsSpawnTime(event.world);
         GravesLogger.setWorldDirectory(event.world.getSaveHandler().getWorldDirectory());
      }
   }
}
