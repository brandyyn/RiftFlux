package com.voidsrift.riftflux.palaria;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.palaria.client.model.ModelCowasaurus;
import com.voidsrift.riftflux.palaria.client.model.ModelCreeptile;
import com.voidsrift.riftflux.palaria.client.model.ModelEnderWalker;
import com.voidsrift.riftflux.palaria.client.model.ModelNimatin;
import com.voidsrift.riftflux.palaria.client.model.ModelRaptorChicken;
import com.voidsrift.riftflux.palaria.client.render.RenderCreeptile;
import com.voidsrift.riftflux.palaria.client.render.RenderNimatin;
import com.voidsrift.riftflux.palaria.client.render.RenderPalariaMob;
import com.voidsrift.riftflux.palaria.client.render.RenderPalariaSeat;
import com.voidsrift.riftflux.palaria.client.render.RenderRaptorChicken;
import com.voidsrift.riftflux.palaria.entity.EntityCowasaurus;
import com.voidsrift.riftflux.palaria.entity.EntityCreeptile;
import com.voidsrift.riftflux.palaria.entity.EntityEnderRaptorChicken;
import com.voidsrift.riftflux.palaria.entity.EntityEnderWalker;
import com.voidsrift.riftflux.palaria.entity.EntityMagmaRaptorChicken;
import com.voidsrift.riftflux.palaria.entity.EntityNimatin;
import com.voidsrift.riftflux.palaria.entity.EntityNimatinSeat;
import com.voidsrift.riftflux.palaria.entity.EntityRaptorChicken;
import com.voidsrift.riftflux.riftflux;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

public final class PalariaMobContent {
    public static Item raptorClaw;
    public static Item creeptileEye;
    public static Item endermiteShard;

    private static boolean preInited;
    private static boolean initialized;
    private static boolean clientInited;

    private PalariaMobContent() {
    }

    public static boolean isEnabled() {
        return ModConfig.enablePalariaModule;
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (preInited || !isEnabled()) {
            return;
        }
        preInited = true;

        registerDropItems();
        int id = 260;
        if (ModConfig.enablePalariaCreeptile) {
            EntityRegistry.registerModEntity(EntityCreeptile.class, "Creeptile", id++, riftflux.instance, 80, 3, true);
            registerEntityEgg(EntityCreeptile.class, "RiftFluxCreeptile", 0x23DD15, 0x000000);
            registerSpawnEgg("creeptile_spawn_egg", "creeptile", 0x23DD15, 0x000000);
        }
        if (ModConfig.enablePalariaRaptorChicken) {
            EntityRegistry.registerModEntity(EntityRaptorChicken.class, "RaptorChicken", id++, riftflux.instance, 100, 3, true);
            registerEntityEgg(EntityRaptorChicken.class, "RiftFluxRaptorChicken", 0xCFCFB8, 0xD11515);
            registerSpawnEgg("raptor_chicken_spawn_egg", "raptor_chicken", 0xCFCFB8, 0xD11515);
        }
        if (ModConfig.enablePalariaCowasaurus) {
            EntityRegistry.registerModEntity(EntityCowasaurus.class, "Cowasaurus", id++, riftflux.instance, 100, 3, true);
            registerEntityEgg(EntityCowasaurus.class, "RiftFluxCowasaurus", 0x4F3D2B, 0x8C8282);
            registerSpawnEgg("cowasaurus_spawn_egg", "cowasaurus", 0x4F3D2B, 0x8C8282);
        }
        if (ModConfig.enablePalariaEnderWalker) {
            EntityRegistry.registerModEntity(EntityEnderWalker.class, "EnderWalker", id++, riftflux.instance, 100, 3, true);
            registerEntityEgg(EntityEnderWalker.class, "RiftFluxEnderWalker", 0x000000, 0xA00000);
            registerSpawnEgg("ender_walker_spawn_egg", "ender_walker", 0x000000, 0xA00000);
        }
        if (ModConfig.enablePalariaNimatin) {
            EntityRegistry.registerModEntity(EntityNimatin.class, "Nimatin", id++, riftflux.instance, 100, 3, true);
            EntityRegistry.registerModEntity(EntityNimatinSeat.class, "NimatinSeat", id++, riftflux.instance, 64, 1, false);
            registerEntityEgg(EntityNimatin.class, "RiftFluxNimatin", 0xD1AD00, 0x6E6E6E);
            registerSpawnEgg("nimatin_spawn_egg", "nimatin", 0xD1AD00, 0x6E6E6E);
        }
        if (ModConfig.enablePalariaEnderRaptorChicken) {
            EntityRegistry.registerModEntity(EntityEnderRaptorChicken.class, "EnderRaptorChicken", id++, riftflux.instance, 100, 3, true);
            registerEntityEgg(EntityEnderRaptorChicken.class, "RiftFluxEnderRaptorChicken", 0x5B1A8E, 0x000000);
            registerSpawnEgg("ender_raptor_chicken_spawn_egg", "ender_raptor_chicken", 0x5B1A8E, 0x000000);
        }
        if (ModConfig.enablePalariaMagmaRaptorChicken) {
            EntityRegistry.registerModEntity(EntityMagmaRaptorChicken.class, "MagmaRaptorChicken", id, riftflux.instance, 100, 3, true);
            registerEntityEgg(EntityMagmaRaptorChicken.class, "RiftFluxMagmaRaptorChicken", 0xFFD000, 0xD60000);
            registerSpawnEgg("magma_raptor_chicken_spawn_egg", "magma_raptor_chicken", 0xFFD000, 0xD60000);
        }
    }

    public static void init(FMLInitializationEvent event) {
        if (initialized || !isEnabled()) {
            return;
        }
        initialized = true;

        if (ModConfig.enablePalariaCreeptile && ModConfig.palariaCreeptileSpawnWeight > 0) {
            EntityRegistry.addSpawn(EntityCreeptile.class, ModConfig.palariaCreeptileSpawnWeight, 2, 3, EnumCreatureType.monster, PalariaBiomeHelper.overworldMonsterBiomes(false));
        }
        if (ModConfig.enablePalariaRaptorChicken && ModConfig.palariaRaptorChickenSpawnWeight > 0) {
            EntityRegistry.addSpawn(EntityRaptorChicken.class, ModConfig.palariaRaptorChickenSpawnWeight, 4, 6, EnumCreatureType.monster, PalariaBiomeHelper.overworldMonsterBiomes(false));
        }
        if (ModConfig.enablePalariaCowasaurus && ModConfig.palariaCowasaurusSpawnWeight > 0) {
            EntityRegistry.addSpawn(EntityCowasaurus.class, ModConfig.palariaCowasaurusSpawnWeight, 2, 3, EnumCreatureType.monster, PalariaBiomeHelper.overworldMonsterBiomes(false));
        }
        if (ModConfig.enablePalariaEnderWalker && ModConfig.palariaEnderWalkerSpawnWeight > 0) {
            EntityRegistry.addSpawn(EntityEnderWalker.class, ModConfig.palariaEnderWalkerSpawnWeight, 2, 5, EnumCreatureType.monster, PalariaBiomeHelper.overworldMonsterBiomes(true));
        }
        if (ModConfig.enablePalariaNimatin && ModConfig.palariaNimatinSpawnWeight > 0) {
            EntityRegistry.addSpawn(EntityNimatin.class, ModConfig.palariaNimatinSpawnWeight, 1, 1, EnumCreatureType.creature, PalariaBiomeHelper.nimatinBiomes());
        }
        if (ModConfig.enablePalariaEnderRaptorChicken && ModConfig.palariaEnderRaptorChickenSpawnWeight > 0) {
            EntityRegistry.addSpawn(EntityEnderRaptorChicken.class, ModConfig.palariaEnderRaptorChickenSpawnWeight, 2, 5, EnumCreatureType.monster, PalariaBiomeHelper.endBiomes());
        }
        if (ModConfig.enablePalariaMagmaRaptorChicken && ModConfig.palariaMagmaRaptorChickenSpawnWeight > 0) {
            EntityRegistry.addSpawn(EntityMagmaRaptorChicken.class, ModConfig.palariaMagmaRaptorChickenSpawnWeight, 2, 5, EnumCreatureType.monster, PalariaBiomeHelper.netherBiomes());
        }
        if (ModConfig.enablePalariaNimatin) {
            MinecraftForge.EVENT_BUS.register(new NimatinSeatEvents());
        }
    }

    public static void initClient() {
        if (clientInited || !isEnabled()) {
            return;
        }
        clientInited = true;

        if (ModConfig.enablePalariaCowasaurus) {
            RenderingRegistry.registerEntityRenderingHandler(EntityCowasaurus.class,
                    new RenderPalariaMob(new ModelCowasaurus(), 1.0F, "textures/entity/palaria/cowasaurus.png"));
        }
        if (ModConfig.enablePalariaCreeptile) {
            RenderingRegistry.registerEntityRenderingHandler(EntityCreeptile.class,
                    new RenderCreeptile(new ModelCreeptile(), 0.5F));
        }
        if (ModConfig.enablePalariaRaptorChicken) {
            RenderingRegistry.registerEntityRenderingHandler(EntityRaptorChicken.class,
                    new RenderRaptorChicken(new ModelRaptorChicken(), 0.4F, "textures/entity/palaria/raptor_chicken.png"));
        }
        if (ModConfig.enablePalariaEnderWalker) {
            RenderingRegistry.registerEntityRenderingHandler(EntityEnderWalker.class,
                    new RenderPalariaMob(new ModelEnderWalker(), 0.5F, "textures/entity/palaria/ender_walker.png"));
        }
        if (ModConfig.enablePalariaNimatin) {
            RenderingRegistry.registerEntityRenderingHandler(EntityNimatin.class,
                    new RenderNimatin(new ModelNimatin(), 0.8F));
            RenderingRegistry.registerEntityRenderingHandler(EntityNimatinSeat.class, new RenderPalariaSeat());
        }
        if (ModConfig.enablePalariaEnderRaptorChicken) {
            RenderingRegistry.registerEntityRenderingHandler(EntityEnderRaptorChicken.class,
                    new RenderRaptorChicken(new ModelRaptorChicken(), 0.4F, "textures/entity/palaria/ender_raptor_chicken.png"));
        }
        if (ModConfig.enablePalariaMagmaRaptorChicken) {
            RenderingRegistry.registerEntityRenderingHandler(EntityMagmaRaptorChicken.class,
                    new RenderRaptorChicken(new ModelRaptorChicken(), 0.4F, "textures/entity/palaria/magma_raptor_chicken.png"));
        }
    }

    private static void registerDropItems() {
        raptorClaw = registerMaterial("raptor_claw", "raptorclaw");
        creeptileEye = registerMaterial("creeptile_eye", "creeptileeye");
        endermiteShard = registerMaterial("endermite_shard", "endermiteshard");
    }

    private static Item registerMaterial(String registryName, String textureName) {
        Item item = new ItemPalariaMaterial(textureName).setUnlocalizedName(registryName);
        GameRegistry.registerItem(item, registryName);
        return item;
    }

    private static void registerSpawnEgg(String registryName, String mobKey, int primaryColor, int secondaryColor) {
        GameRegistry.registerItem(new ItemPalariaSpawnEgg(mobKey, primaryColor, secondaryColor).setUnlocalizedName(registryName), registryName);
    }

    private static void registerEntityEgg(Class<? extends Entity> entityClass, String entityName, int primaryColor, int secondaryColor) {
        int entityId = EntityRegistry.findGlobalUniqueEntityId();
        EntityRegistry.registerGlobalEntityID(entityClass, entityName, entityId);
        EntityList.entityEggs.put(entityId, new EntityList.EntityEggInfo(entityId, primaryColor, secondaryColor));
    }
}
