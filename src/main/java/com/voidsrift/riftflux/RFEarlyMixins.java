package com.voidsrift.riftflux;

import com.voidsrift.riftflux.core.BasicTransformer;
import com.voidsrift.riftflux.dualhotbar.DualHotbarTransformer;
import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraft.launchwrapper.Launch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@IFMLLoadingPlugin.SortingIndex(1200)
public class RFEarlyMixins implements IFMLLoadingPlugin, IEarlyMixinLoader {
    private static final String ANGELICA_INCOMPATIBLE_TWEAKER =
            "com.gtnewhorizons.angelica.loading.fml.tweakers.IncompatibleModsDisablerTweaker";
    private static final String ANGELICA_FOG_SERVICE =
            "com.gtnewhorizons.angelica.glsm.AngelicaFogService";
    private static final String ANGELICA_RENDER_SECTION_MANAGER =
            "com.gtnewhorizons.angelica.rendering.celeritas.AngelicaRenderSectionManager";
    private static final String ANGELICA_CHUNK_RENDERER =
            "com.gtnewhorizons.angelica.rendering.celeritas.AngelicaChunkRenderer";
    private static final String ANGELICA_CELERITAS_WORLD_RENDERER =
            "com.gtnewhorizons.angelica.rendering.celeritas.CeleritasWorldRenderer";
    private static final String ANGELICA_WORLD_SLICE =
            "com.gtnewhorizons.angelica.rendering.celeritas.world.WorldSlice";
    private static final String MCPATCHER_SKY_RENDERER =
            "com.prupe.mcpatcher.sky.SkyRenderer";
    private static final String FLUIDLOGGED_FL_UTIL =
            "mega.fluidlogged.internal.FLUtil";
    private static final String BEDDIUM_FOG_STATE_SERVICE =
            "com.ventooth.beddium.modules.TerrainRendering.fog.FogStateService";
    private static final String BEDDIUM_RENDER_SECTION_MANAGER =
            "com.ventooth.beddium.modules.TerrainRendering.ArchaicRenderSectionManager";
    private static final String BEDDIUM_CELERITAS_WORLD_RENDERER =
            "com.ventooth.beddium.modules.TerrainRendering.CeleritasWorldRenderer";
    private static final String BEDDIUM_SWANSONG_CHUNK_RENDERER =
            "com.ventooth.beddium.modules.TerrainRendering.render.SwanSongChunkRenderer";
    private static final String BEDDIUM_SIMPLE_CHUNK_BUILDER_MESHING_TASK =
            "com.ventooth.beddium.api.task.SimpleChunkBuilderMeshingTask";

    public RFEarlyMixins() {
        ModConfig.init(new File("config/riftflux.cfg"));
        removeAngelicaIncompatibleTweakerIfNeeded();
    }

    @Override
    public String getMixinConfig() {
        return "mixins." + Constants.MODID + ".early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        List<String> mixins = new ArrayList<String>();
        if (ModConfig.shearsDamageOnAnyBlock) {
            mixins.add("early.MixinItemShears_DamageAnyBlock");
        }
        if (ModConfig.woolRequireShears) {
            mixins.add("early.MixinBlock_WoolShearsOnly");
        }
        if (ModConfig.enableStackOverflowGuard) {
            mixins.add("early.MixinCrashReportCategory_NoRecurse");
            mixins.add("early.MixinWorld_GetBlockDepthLimit");
        }
        if (ModConfig.disableFalseCrashImprover) {
            mixins.add("early.MixinCrashReport_NoFalseCrashImprover");
        }
        if (ModConfig.enableSafeEntityTick) {
            mixins.add("early.MixinEntitySafeTick");
            mixins.add("early.MixinWorldSafeEntityTick");
        }
        mixins.add("early.MixinFMLProxyPacket_NullDispatcherReject");
        if (ModConfig.reworkVillageGolems) {
            mixins.add("early.MixinVillageGolemBlock");
            mixins.add("early.MixinStructureStartVillageSpawn");
        }
        if (ModConfig.disableWitchingGadgetsVillageHouseGeneration
                && hasClass("witchinggadgets.WitchingGadgets")) {
            mixins.add("early.witchinggadgets.MixinWitchingGadgets_DisableVillageHouse");
        }
        if (ModConfig.disableSleepRainClear) {
            mixins.add("early.DisableSleepRainClear");
        }
        if (ModConfig.jukeboxAutoLoopEnabled || ModConfig.jukeboxRedstoneRestartEnabled) {
            mixins.add("early.MixinTileEntityJukebox_LoopState");
        }
        if (ModConfig.jukeboxAutoLoopEnabled) {
            mixins.add("early.MixinBlockJukebox_LoopTick");
        }
        if (ModConfig.jukeboxRedstoneRestartEnabled) {
            mixins.add("early.MixinWorld_JukeboxRedstoneRestart");
        }
        if (ModConfig.pulseLockedHoppers) {
            mixins.add("early.MixinTileEntityHopper_PulseLocked");
            mixins.add("early.MixinBlockHopper_PulseLocked");
        }
        if (ModConfig.enableFullExplosionDrops) {
            mixins.add("early.MixinTNT");
        }
        if (ModConfig.enableFullExplosionDrops) {
            mixins.add("early.MixinRenderGlobal_ItemRenderDist");
        }
        if (ModConfig.protectItemsFromExplosions) {
            mixins.add("early.MixinExplosionKeepItems");
        }
        if (ModConfig.explosionsIgnoreThinPlantsForExposure) {
            mixins.add("early.MixinWorld_ExplosionDensityIgnoresPlants");
        }
        if (ModConfig.disableBonemeal) {
            mixins.add("early.MixinItemDye_DisableBonemeal");
        }
        if (ModConfig.allowPlantsOnAnyBlock || ModConfig.directionalCrossedPlantRenderingByPlacement) {
            mixins.add("early.MixinBlockDoublePlant_AnySupport");
        }
        if (ModConfig.allowPlantsOnAnyBlock) {
            mixins.add("early.MixinBlockBush_AnySupport");
            mixins.add("early.MixinBiomeGenBase_WorldGenContext");
        }
        if (ModConfig.allowPumpkinsOnAnyBlock) {
            mixins.add("early.MixinBlockPumpkin_AnySupport");
        }
        if (ModConfig.allowPlantsOnAnyBlock || ModConfig.directionalCrossedPlantRenderingByPlacement) {
            mixins.add("early.MixinBlock_PlayerPlacedBushMarker");
            mixins.add("early.MixinItemBlock_PlayerPlacedBushMarker");
        }
        if (ModConfig.allowTorchesOnAnyBlock) {
            mixins.add("early.MixinBlockTorch_AnySupport");
        }
        if (ModConfig.deathRespawnDelaySeconds > 0) {
            mixins.add("early.MixinNetHandlerPlayServer_RespawnDelay");
        }
        if (ModConfig.strictMobSpawnsZeroBlockLight) {
            mixins.add("early.MixinEntityMob_ZeroBlockLightSpawn");
        }
        mixins.add("early.MixinWorldProvider_InitialMoonPhase");
        mixins.add("early.MixinWorldServer_ConfiguredMobDimensions");
        if (ModConfig.useSpawnTypeForMobCap) {
            mixins.add("early.MixinEntity_SpawnTypeMobCap");
        }
        if (ModConfig.wrongUseSingleDurability) {
            mixins.add("early.MixinItemStack_WrongUseDurability");
        }
        if (ModConfig.invincibleOwnedMobs ) {
            mixins.add("early.MixinEntityLivingBase_PetInvincibility");
        }
        if (ModConfig.preventLeadsBreaking) {
            mixins.add("early.MixinEntityLiving_NoLeadBreaking");
        }
        if (ModConfig.preventLeashedMobFallDamage) {
            mixins.add("early.MixinEntityLivingBase_LeashedFallDamage");
        }
        if (ModConfig.unsilenceCoveredNoteBlocks) {
            mixins.add("early.MixinTileEntityNote_UncoveredSound");
        }
        mixins.add("early.MixinEntityArrow_NoRandomSpread");
        if (ModConfig.enableThornsArmorTweaks) {
            mixins.add("early.MixinEnchantmentThorns_NoExtraDurability");
            mixins.add("early.MixinEnchantment_ThornsAnyArmorTable");
        }
        mixins.add("early.MixinChunk_TeleportOwnedPetsOnUnload");
        if (ModConfig.enableMeleeDamageTooltip) {
            mixins.add("early.MixinTooltip");
        }
        if (ModConfig.chromatiCraftNetherBedrockBreakableLikeObsidian) {
            mixins.add("early.MixinBlock_NetherBedrockHardness");
        }
        if (ModConfig.preventBlockBreakingResetOnHeldItemChange) {
            mixins.add("early.MixinItemInWorldManager_LegacyBreakProgress");
        }
        if (cpw.mods.fml.relauncher.FMLLaunchHandler.side() == cpw.mods.fml.relauncher.Side.CLIENT) {
            if (ModConfig.fixUnderwaterMobDarkening) {
                mixins.add("early.MixinEntity_RiftFluxUnderwaterBrightness");
            }
            if (ModConfig.enablePostProcessing) {
                mixins.add("early.MixinEntityRenderer_PostProcessBeforeHud");
            }
            if (hasClass("me.kimovoid.tweakimo.Tweakimo")) {
                mixins.add("early.tweakimo.MixinGuiIngameForge_HideMountPromptOverlay");
            }
            if (hasClass("Reika.DragonAPI.Instantiable.Event.Client.RenderBlockAtPosEvent")
                    && ModConfig.optimizeDragonAPIBlockRenderFastPaths) {
                mixins.add("early.dragonapi.MixinRenderBlockAtPosEvent_FastPath");
            }
            if (hasClass("Reika.DragonAPI.Instantiable.Event.Client.EntityRenderingLoopEvent")
                    && ModConfig.optimizeDragonAPIEntityRenderLoopFastPaths) {
                mixins.add("early.dragonapi.MixinEntityRenderingLoopEvent_FastPath");
            }
            if (hasClass("Reika.DragonAPI.Extras.ThrottleableEffectRenderer")
                    && ModConfig.optimizeDragonAPIParticleRenderFastPaths) {
                mixins.add("early.dragonapi.MixinThrottleableEffectRenderer_VanillaParticleLoop");
            }
            if (hasClass("Reika.ChromatiCraft.ChromaClientEventController")
                    && ModConfig.optimizeChromatiCraftRenderEventFastPaths) {
                mixins.add("early.chromaticraft.MixinChromaClientEventController_OptimizeClientRenderHooks");
            }
        }
        if (ModConfig.disableChromatiCraftItemFabricator
                && hasClass("Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable")
                && hasClass("Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles.FabricatorRecipe")) {
            mixins.add("early.chromaticraft.MixinRecipesCastingTable_DisableFabricatorRecipe");
            if (cpw.mods.fml.relauncher.FMLLaunchHandler.side() == cpw.mods.fml.relauncher.Side.CLIENT
                    && hasClass("codechicken.nei.api.API")
                    && hasClass("Reika.ChromatiCraft.ModInterface.NEI.NEIChromaConfig")) {
                mixins.add("early.chromaticraft.MixinNEIChromaConfig_DisableFabricatorHandler");
            }
        }
        if (ModConfig.optimizeChromatiCraftCliffsChunkGeneration) {
            if (hasClass("Reika.ChromatiCraft.World.IWG.GlowingCliffsAuxGenerator")) {
                mixins.add("early.chromaticraft.MixinGlowingCliffsAuxGenerator_SkipIrrelevantChunks");
            }
            if (hasClass("Reika.ChromatiCraft.World.IWG.CaveIndicatorGenerator")) {
                mixins.add("early.chromaticraft.MixinCaveIndicatorGenerator_SkipIrrelevantChunks");
            }
        }
        if (hasClass("Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield")
                && ModConfig.chromatiCraftNetherStructureShieldBreakableLikeObsidian) {
            mixins.add("early.chromaticraft.MixinBlockStructureShield_NetherBreakable");
            mixins.add("early.chromaticraft.MixinBlock_ChromatiCraftShieldBreakSpeed");
        }
        if (hasClass("Reika.ChromatiCraft.World.Nether.NetherStructures")) {
            mixins.add("early.chromaticraft.MixinNetherStructures_Configurable");
        }
        if (hasClass("Reika.ChromatiCraft.World.Nether.NetherStructureGenerator")) {
            mixins.add("early.chromaticraft.MixinNetherStructureGenerator_HolesConfig");
        }
        if (hasClass("Reika.ChromatiCraft.World.Nether.LavaRiverGenerator")) {
            mixins.add("early.chromaticraft.MixinLavaRiverGenerator_Configurable");
        }
        if (ModConfig.configurableWaterLakeYLevels) {
            mixins.add("early.MixinChunkProviderGenerate_ConfigurableWaterLakeY");
        }
        if (ModConfig.enableWheatfieldBiome) {
            mixins.add("early.MixinWorldGenLakes_SkipWheatfield");
            mixins.add("early.MixinMapGenStructure_NoWheatfieldStructures");
        }
        if (ModConfig.enableArmorMixin) {
            mixins.add("early.MixinArmorProperties");
        }
        if (ModConfig.changeArmorBarAmount) {
            mixins.add("early.MixinForgeHooks");
        }
        if (ModConfig.enableBedChill) {
            mixins.add("early.MixinBedChill$NoSleepSkip");
            mixins.add("early.MixinBedChill$AnyTimeSleep");
            if (cpw.mods.fml.relauncher.FMLLaunchHandler.side() == cpw.mods.fml.relauncher.Side.CLIENT) {
                mixins.add("early.MixinBedChill$NoSleepFade");
            }
        }
        if (ModConfig.enableItemPickupStar) {
            mixins.add("early.MixinItemPickupStar");           // client draw/hover-clear
            mixins.add("early.MixinInventoryPlayerPickupTag"); // server tag-on-pickup
            mixins.add("early.MixinItemStackTagEqual");
            mixins.add("early.MixinInventoryPlayerMergeIgnoreStarTags");
            mixins.add("early.MixinInventoryPlayerPickupTagStorePartial");
            mixins.add("early.MixinEntityPlayer_ClearPickupStarOnDrop");
        }
        // Satchels
        mixins.add("early.satchels.MixinEntityPlayer");
        mixins.add("early.satchels.MixinInventoryPlayer");
        mixins.add("early.satchels.MixinItemInWorldManager");
        if (cpw.mods.fml.relauncher.FMLLaunchHandler.side() == cpw.mods.fml.relauncher.Side.CLIENT) {
            mixins.add("early.satchels.MixinPlayerControllerMP");
        }
        if (ModConfig.enableNewBlockHighlight) {
            mixins.add("early.MixinBlockHighlight");
        }
        if (ModConfig.enableFistDamageBoost || ModConfig.enableStickDamageBonus) {
            mixins.add("early.MixinEntityPlayer_FistStickDamage");
        }
        if (ModConfig.enableHeartCrystalModule) {
            mixins.add("early.heartcrystal.MixinEntityPlayer_HeartCrystalEatParticles");
        }
        if (ModConfig.disableSpecificPotions) {
            mixins.add("early.MixinEntityLivingBase_DisablePotions");
        }
        if (ModConfig.disableTintedSugarcane
                || ModConfig.allowSugarcaneOnAnyBlock
                || ModConfig.allowSugarcaneInWater
                || ModConfig.sugarcaneMaxHeight != 3
                || ModConfig.sugarcaneMaxHeightAboveTopWaterBlock != 3
                || ModConfig.allowHangingSugarcane
                || ModConfig.hangingSugarcaneGrowsWithWaterAboveSupport
                || ModConfig.sugarcaneGrowsWhenSupportHasBlockBelow) {
            mixins.add("early.MixinBlockReed");
        }
        if (ModConfig.allowSugarcaneOnAnyBlock
                || ModConfig.allowSugarcaneInWater
                || ModConfig.sugarcaneGeneratesOnRiverFloors
                || ModConfig.allowHangingSugarcane
                || ModConfig.sugarcaneGrowsWhenSupportHasBlockBelow) {
            mixins.add("early.MixinWorldGenReed_VanillaRules");
        }
        if (ModConfig.enableNetherrackTweak) {
            mixins.add("early.MixinBlock_NoNetherrackDropsOutsideNether");
        }
        if (ModConfig.enableDoorAirPlacement) {
            mixins.add("early.MixinBlockDoor_AirPlacement");
            mixins.add("early.MixinItemDoor_ReplaceTallGrass");
            mixins.add("early.MixinBlock_DoorShiftOnReplaceable");
        }
        if (ModConfig.protectCircuitryFromWater) {
            mixins.add("early.MixinBlockDynamicLiquid_ProtectCircuitryFromWater");
            mixins.add("early.MixinItemBucket_ProtectCircuitryFromWater");
        }
        if (ModConfig.preventWaterGrassDecay) {
            mixins.add("early.MixinBlockGrass_NoWaterDecay");
        }
        if (ModConfig.enablePodzolDirtTexture && cpw.mods.fml.relauncher.FMLLaunchHandler.side() == cpw.mods.fml.relauncher.Side.CLIENT) {
            mixins.add("early.MixinBlockDirt_PodzolTextures");
            mixins.add("early.MixinBlockMycelium_BottomDirtTexture");
        }
        if (ModConfig.enableHangingLadders) {
            mixins.add("early.MixinEntityLivingBase_NoLadderWalkSlowdown");
        }
        if (ModConfig.legacyBoatBuoyancy || ModConfig.boatsFallBreakDistance > 0.0F) {
            mixins.add("early.MixinEntityBoat_WaterClimb");
        }
        mixins.add("early.MixinEntity_NoUnderwaterLivingDismount");
        if (ModConfig.playerOnlyHurtSound) {
            mixins.add("early.MixinEntityLivingBase_CustomPlayerHurtSoundServer");
            if (cpw.mods.fml.relauncher.FMLLaunchHandler.side() == cpw.mods.fml.relauncher.Side.CLIENT) {
                mixins.add("early.MixinEntityLivingBase_CustomPlayerHurtSound");
            }
        }
        if (ModConfig.enableCustomPaintings) {
            mixins.add("early.MixinEntityPainting_ExtraArt");
            mixins.add("early.MixinS10PacketSpawnPainting_TitleLength");
        }
        if (ModConfig.enablePaintingAirPlacement) {
            mixins.add("early.MixinEntityHanging_PaintingAirPlacement");
        }
        if (ModConfig.enablePaintingSelection) {
            mixins.add("early.MixinItemHangingEntity_PaintingSelection");
        }
        if (ModConfig.enableFenceTextureModule) {
            mixins.add("early.MixinBlockFence_InfdevPlusConnection");
        }
        if (ModConfig.disableFencePumpkinConnections) {
            mixins.add("early.MixinBlockFence_NoPumpkinConnections");
        }
        if (ModConfig.enableJackOLanternHelmet) {
            mixins.add("early.MixinItem_JackOLanternHelmet");
            mixins.add("early.MixinEntityLiving_JackOLanternArmorPosition");
            mixins.add("early.MixinEntityEnderman_JackOLanternNoAggro");
        }
        if (ModConfig.enableRiftExplorerModule) {
            mixins.add("early.MixinEntityLiving_NoRiftChestPickup");
            mixins.add("early.MixinEntity_RiftChestRandomMobState");
        }
        if (ModConfig.legendGearEnableLegacyLegendGear) {
            mixins.add("early.legendgear.MixinEntityLivingBase_StarbeamJumpAccessor");
            mixins.add("early.legendgear.MixinBlockRedstoneComparator_LegacyPedestal");
            if (cpw.mods.fml.relauncher.FMLLaunchHandler.side() == cpw.mods.fml.relauncher.Side.CLIENT) {
                mixins.add("early.legendgear.MixinGuiIngame_StarbeamMountPrompt");
            }
        }
        if (cpw.mods.fml.relauncher.FMLLaunchHandler.side() == cpw.mods.fml.relauncher.Side.CLIENT) {
            mixins.add("accessor.GuiChatAccessor");
            mixins.add("accessor.GuiNewChatAccessor");
            mixins.add("accessor.GuiScreenAccessor");
            mixins.add("accessor.GuiContainerAccessor");
            mixins.add("accessor.GuiYesNoAccessor");
            mixins.add("accessor.ModelBoxAccessor");
            mixins.add("accessor.PlayerControllerMPAccessor");
            mixins.add("accessor.ChunkCacheAccessor");
            if (hasClass(ANGELICA_WORLD_SLICE)) {
                mixins.add("accessor.angelica.WorldSliceAccessor");
            }
            if (ModConfig.enableFenceTextureModule) {
                mixins.add("early.MixinBlockFence_InfdevPlusTexture");
            }
            mixins.add("early.MixinCreativeTabs_DisabledModuleItems");
            if (ModConfig.directionalCrossedPlantRenderingByPlacement) {
                mixins.add("early.MixinRenderBlocks_DirectionalCrossedPlants");
            }
            if (ModConfig.allowSugarcaneInWater || ModConfig.fixGeoStrataCrystalSpikeWaterlogging) {
                mixins.add("early.MixinChunkCache_WaterloggedFluidAccess");
                if (hasClass(ANGELICA_WORLD_SLICE)) {
                    mixins.add("early.angelica.MixinWorldSlice_WaterloggedFluidAccess");
                }
                if (hasClass(FLUIDLOGGED_FL_UTIL)) {
                    mixins.add("early.fluidlogged.MixinFLUtil_WaterloggedLookup");
                }
                mixins.add("early.MixinRenderBlocks_WaterloggedFluidLookup");
            }
            if (ModConfig.allowSugarcaneInWater) {
                mixins.add("early.MixinBlock_SugarcaneWaterRenderPass");
                mixins.add("early.MixinWorldRenderer_SugarcaneWaterRenderPass");
                mixins.add("early.MixinRenderBlocks_SugarcaneWaterlogging");
            }
            if (ModConfig.allowSugarcaneInWater || ModConfig.fixGeoStrataCrystalSpikeWaterlogging) {
                mixins.add("early.MixinWorld_SupportedFluidLookup");
            }
            mixins.add("early.MixinRenderBlocks_TorchAllFaces");
            if (ModConfig.betterTorchTexture) {
                mixins.add("early.MixinBlock_TorchTextureOverride");
            }
            if (ModConfig.enableJackOLanternHelmet) {
                mixins.add("early.MixinGuiIngame_JackOLanternBlur");
            }
            mixins.add("early.MixinRenderGlobal_SunriseTint");
            mixins.add("early.MixinEntityRenderer_BlackNightFog");
            mixins.add("early.MixinEntityRenderer_BetaStyleFogDistance");
            mixins.add("early.MixinWorld_BetaStyleCloudColor");
            if (hasClass(ANGELICA_FOG_SERVICE)) {
                mixins.add("early.angelica.MixinAngelicaFogService_BetaStyleFog");
            }
            if (hasClass(BEDDIUM_FOG_STATE_SERVICE)) {
                mixins.add("early.beddium.MixinBeddiumFogStateService_BetaStyleFog");
            }
            if (ModConfig.saveWorldBeforeWindowClose) {
                mixins.add("early.MixinMinecraft_SaveBeforeWindowClose");
            }
            if (ModConfig.preventBlockBreakingResetOnHeldItemChange) {
                mixins.add("early.MixinPlayerControllerMP_NoBreakResetOnHeldItemChange");
            }
            if (ModConfig.asyncWorldSelection) {
                mixins.add("early.MixinGuiSelectWorld_AsyncLoad");
            }
            if (ModConfig.enableIsometricPhotoMode) {
                mixins.add("early.MixinEntityRenderer_IsometricPhotoMode");
                mixins.add("early.MixinEntityPlayerSP_IsometricPhotoMode");
                mixins.add("early.MixinMinecraft_IsometricPhotoMode");
                mixins.add("early.MixinMovementInputFromOptions_IsometricPhotoMode");
                mixins.add("early.MixinRenderGlobal_IsometricPhotoMode");
                if (!hasCeleritasStack()) {
                    mixins.add("early.MixinRenderGlobal_VanillaPhotoModeFallback");
                }
                mixins.add("early.MixinEffectRenderer_IsometricPhotoMode");
                mixins.add("early.MixinBlock_PhotoMode");
                mixins.add("early.MixinBlock_NetherHideBlocks_PhotoMode");
                mixins.add("early.MixinWorldRenderer_PhotoModeBlockFaceCulling");
                mixins.add("early.MixinRenderBlocks_PhotoModeAllFacesBrightness");
                mixins.add("early.MixinRenderBlocks_NetherHideBlocks_PhotoMode");
                if (hasClass(ANGELICA_FOG_SERVICE)) {
                    mixins.add("early.angelica.MixinAngelicaFogService_PhotoMode");
                }
                if (hasClass(BEDDIUM_FOG_STATE_SERVICE)) {
                    mixins.add("early.beddium.MixinBeddiumFogStateService_PhotoMode");
                }
                if (hasClass(ANGELICA_RENDER_SECTION_MANAGER)) {
                    mixins.add("early.angelica.MixinAngelicaRenderSectionManager_PhotoMode");
                }
                if (hasClass(BEDDIUM_RENDER_SECTION_MANAGER)) {
                    mixins.add("early.beddium.MixinBeddiumArchaicRenderSectionManager_PhotoMode");
                }
                if (hasClass("org.embeddedt.embeddium.impl.render.chunk.RenderSectionManager")) {
                    mixins.add("early.angelica.MixinEmbeddiumRenderSectionManager_PhotoMode");
                }
                if (hasClass(ANGELICA_CHUNK_RENDERER)) {
                    mixins.add("early.angelica.MixinAngelicaChunkRenderer_PhotoMode");
                }
                if (hasClass(ANGELICA_CELERITAS_WORLD_RENDERER)) {
                    mixins.add("early.angelica.MixinAngelicaCeleritasWorldRenderer_PhotoMode");
                }
                if (hasClass(BEDDIUM_CELERITAS_WORLD_RENDERER)) {
                    mixins.add("early.beddium.MixinBeddiumCeleritasWorldRenderer_PhotoMode");
                }
                if (hasClass("org.embeddedt.embeddium.impl.render.chunk.DefaultChunkRenderer")) {
                    mixins.add("early.angelica.MixinEmbeddiumDefaultChunkRenderer_PhotoMode");
                }
                if (hasClass(BEDDIUM_SWANSONG_CHUNK_RENDERER)) {
                    mixins.add("early.beddium.MixinBeddiumSwanSongChunkRenderer_PhotoMode");
                }
                if (hasClass(BEDDIUM_SIMPLE_CHUNK_BUILDER_MESHING_TASK)) {
                    mixins.add("early.beddium.MixinBeddiumSimpleChunkBuilderMeshingTask_PhotoModeEdges");
                }
                if (hasClass("org.embeddedt.embeddium.impl.render.viewport.frustum.SimpleFrustum")) {
                    mixins.add("early.angelica.MixinEmbeddiumSimpleFrustum_PhotoMode");
                }
                if (hasClass("org.embeddedt.embeddium.impl.render.chunk.occlusion.OcclusionCuller")) {
                    mixins.add("early.angelica.MixinEmbeddiumOcclusionCuller_PhotoMode");
                }
                if (hasClass("org.embeddedt.embeddium.impl.render.terrain.SimpleWorldRenderer")) {
                    mixins.add("early.angelica.MixinEmbeddiumSimpleWorldRenderer_PhotoMode");
                }
                if (hasClass("org.embeddedt.embeddium.impl.render.chunk.ShaderChunkRenderer")) {
                    mixins.add("early.angelica.MixinEmbeddiumShaderChunkRenderer_PhotoMode");
                }
                if (hasClass("thaumcraft.client.fx.ParticleEngine")) {
                    mixins.add("early.thaumcraft.MixinThaumcraftParticleEngine_PhotoMode");
                }
            }
            mixins.add("early.MixinEntityRenderer_SunriseTint");
            mixins.add("early.MixinEntityRenderer_VoidFogHeight");
            mixins.add("early.MixinWorldClient_VoidParticleHeight");
            if (ModConfig.enableToroHealthModule && ModConfig.toroHealthShowDamageParticles) {
                mixins.add("early.torohealth.MixinEntityLivingBase_ToroHealth");
            }
            if (ModConfig.enableCustomPaintings) {
                mixins.add("early.MixinRenderPainting_CustomTexture");
            }
            if (ModConfig.enablePaintingSelection) {
                mixins.add("early.MixinPlayerControllerMP_PaintingSelectorOpen");
            }
            if (ModConfig.enableHotbarSelectorTexture) {
                mixins.add("early.MixinGuiIngame_HotbarSelectorTexture");
            }
            if (ModConfig.disablePumpkinOverlay) {
                mixins.add("early.MixinGuiIngame_NoPumpkinOverlay");
            }
            if (ModConfig.centerCrosshair) {
                mixins.add("early.MixinGuiIngame_CenteredCrosshair");
            }
            if (ModConfig.disableUnderwaterOverlay) {
                mixins.add("early.MixinItemRenderer_NoUnderwaterOverlay");
            }
            if (ModConfig.enableAsgardShieldModule) {
                mixins.add("early.asgardshield.MixinGuiIngameForge_AsgardAirOffset");
            }
            if (ModConfig.allowChatOnDeathScreen) {
                mixins.add("early.MixinGuiGameOver_AllowChat");
                mixins.add("early.vortex.MixinGuiRuneGameOver_AllowChat");
            }
            if (ModConfig.allowChatOnPauseScreen) {
                mixins.add("early.MixinGuiScreen_AllowPauseMenuChat");
            }
            if (ModConfig.enableChatSelectionCopy) {
                mixins.add("early.MixinGuiChat_ChatSelection");
            }
            if (ModConfig.enableTerraModule) {
                mixins.add("early.MixinGuiIngame_EyeBossBarColor");
            }
            if (ModConfig.enableCelestialEventTextures || ModConfig.enablePostProcessing) {
                mixins.add("early.MixinRenderGlobal_CelestialEventTextures");
            }
            if (ModConfig.betaStarsEnabled) {
                mixins.add("early.MixinRenderGlobal_BetaStars");
                if (hasClass("jss.notfine.render.RenderStars")) {
                    mixins.add("early.notfine.MixinRenderStars_BetaStars");
                }
                mixins.add("early.mcpatcherforge.MixinRenderGlobal_BetaStarsSkyPass");
                mixins.add("early.mcpatcherforge.MixinSkyRendererLayer_BetaStars");
            }
            if (ModConfig.enablePostProcessing) {
                mixins.add("early.MixinRenderGlobal_PostProcessSkyBloom");
                if (hasClass(MCPATCHER_SKY_RENDERER)) {
                    mixins.add("early.mcpatcherforge.MixinSkyRenderer_PostProcessCelestialExposure");
                }
            }
            if (ModConfig.enablePlacedItem) {
                mixins.add("early.MixinWorld_NoPlacedItemParticles");
                mixins.add("early.MixinWorldClient_NoPlacedItemParticles");
                mixins.add("early.MixinEntityLivingBase_NoPlacedItemRunParticles");
                mixins.add("early.MixinEffectRenderer_PlacedItemDestroyParticles");
            }
            mixins.add("early.MixinNetHandlerPlayClient_WindowItemsClamp");
            mixins.add("early.MixinEffectRenderer_GeoStrataDecoGenParticles");
            boolean hasBackhand = loadedCoreMods.contains("xonin.backhand.coremod.BackhandLoadingPlugin")
                    || hasBackhandClass();
            if (hasBackhand) {
                mixins.add("early.backhand.MixinGuiInventory_BackhandSlot");
                mixins.add("early.backhand.MixinGuiInventoryBackpack_BackhandSlot");
                mixins.add("early.backhand.MixinGuiSatchelsInventory_BackhandSlot");
                mixins.add("early.backhand.MixinContainerPlayer_BackhandSlot");
                mixins.add("early.backhand.MixinBackhandUtils_BlockUtilityOffhand");
                if (ModConfig.enableIsometricPhotoMode) {
                    mixins.add("early.backhand.MixinSyncedKeybind_BackhandPhotoMode");
                }
            }
            if (hasLegendGearClass()) {
                mixins.add("early.legendgear.MixinGuiManaBar");
            }
            if (ModConfig.dualHotbarEnable && ModConfig.dualHotbarEnablePickBlockImplementation) {
                mixins.add("early.dualhotbar.MixinMinecraft_PickBlock");
            }
        }
        if (ModConfig.dualHotbarEnable && ModConfig.dualHotbarEnablePickBlockImplementation) {
            mixins.add("early.dualhotbar.MixinForgeHooks_PickBlock");
        }
        if (ModConfig.dualHotbarEnable) {
            mixins.add("early.dualhotbar.MixinNetHandlerPlayServer_HeldItemChange");
        }
        if (ModConfig.enableBetaLeavesLook) {
            mixins.add("early.MixinBlock_LeafLightOpacity");
        }
        mixins.add("early.baubles.MixinContainerPlayerExpanded_BackpackShiftClick");

        // vortex mixins (always enabled)
        mixins.add("early.vortex.MixinArmorProperties");
        mixins.add("early.vortex.MixinBlockLiquid");
        mixins.add("early.vortex.MixinChestGenHooksRandomGlint");
        mixins.add("early.vortex.MixinContainer");
        mixins.add("early.vortex.MixinEnchantmentHelper");
        mixins.add("early.vortex.MixinEntityItemBackpackPickup");
        mixins.add("early.vortex.MixinEntityPlayer");
        mixins.add("early.vortex.MixinItemEnchantedBookRandomGlint");
        mixins.add("early.vortex.MixinInventoryPlayerBackpackPickup");
        mixins.add("early.vortex.MixinItemStackCustomGlint");
        mixins.add("early.vortex.MixinItemStackRandomGlint");
        mixins.add("early.vortex.MixinItemStack_BackpackBreakDrop");
        mixins.add("early.vortex.MixinRenderItem");
        if (cpw.mods.fml.relauncher.FMLLaunchHandler.side() == cpw.mods.fml.relauncher.Side.CLIENT) {
            mixins.add("early.vortex.MixinGuiContainer");
            mixins.add("early.vortex.MixinGuiInventory");
            mixins.add("early.vortex.MixinGuiScreen");
            mixins.add("early.vortex.MixinItemRenderer");
            mixins.add("early.vortex.MixinModelRendererArmoredArmsGlint");
            if (hasClass("ItemRendererOF")) {
                mixins.add("early.vortex.MixinItemRendererOF");
            }
            mixins.add("early.vortex.MixinMinecraft_ToolbeltFocus");
            mixins.add("early.vortex.MixinRendererLivingEntity");
        }

        return mixins;
    }

    private static boolean hasBackhandClass() {
        try {
            Class.forName("xonin.backhand.Backhand", false, RFEarlyMixins.class.getClassLoader());
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean hasLegendGearClass() {
        if (!ModConfig.enableLegendGearModule) {
            return false;
        }
        try {
            ClassLoader loader = RFEarlyMixins.class.getClassLoader();
            return loader.getResource("net/nmccoy/legendgear/render/GuiManaBar.class") != null;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean hasClass(String className) {
        if (className == null || className.isEmpty()) {
            return false;
        }
        try {
            ClassLoader loader = RFEarlyMixins.class.getClassLoader();
            String resourceName = className.replace('.', '/') + ".class";
            return loader.getResource(resourceName) != null;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean hasOptimizationsAndTweaks() {
        return hasClass("fr.iamacat.optimizationsandtweaks.OptimizationsAndTweaks")
                || hasClass("fr.iamacat.optimizationsandtweaks.OptimizationsAndTweaksMod");
    }

    private static boolean hasCeleritasStack() {
        return hasClass(ANGELICA_CELERITAS_WORLD_RENDERER) || hasClass(BEDDIUM_CELERITAS_WORLD_RENDERER);
    }

    @SuppressWarnings("rawtypes")
    private static void removeAngelicaIncompatibleTweakerIfNeeded() {
        if (!hasOptimizationsAndTweaks()) {
            return;
        }
        try {
            Object tweakClasses = Launch.blackboard.get("TweakClasses");
            removeTweakerEntries(tweakClasses);
            Object loadedTweakers = Launch.blackboard.get("Tweaks");
            removeTweakerEntries(loadedTweakers);
        } catch (Throwable ignored) {
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void removeTweakerEntries(Object listObj) {
        if (!(listObj instanceof List)) {
            return;
        }
        List list = (List) listObj;
        for (int i = list.size() - 1; i >= 0; i--) {
            Object entry = list.get(i);
            if (entry == null) {
                continue;
            }
            String className = entry instanceof String ? (String) entry : entry.getClass().getName();
            if (ANGELICA_INCOMPATIBLE_TWEAKER.equals(className)) {
                list.remove(i);
            }
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{
                BasicTransformer.class.getName(),
                DualHotbarTransformer.class.getName()
        };
    }

    @Override
    public String getModContainerClass() { return null; }

    @Override
    public String getSetupClass() { return null; }

    @Override
    public void injectData(Map<String, Object> data) {
        removeAngelicaIncompatibleTweakerIfNeeded();
    }

    @Override
    public String getAccessTransformerClass() { return null; }
}
