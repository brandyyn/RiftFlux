package com.voidsrift.riftflux;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.Side;

@LateMixin
public class RFLateMixins implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins." + Constants.MODID + ".late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        List<String> mixins = new ArrayList<>();
        if(loadedMods.contains("aether") && (ModConfig.DisableAether2Portal))
           mixins.add("late.MixinAetherPortal");
        if(loadedMods.contains("ChromatiCraft")) {
            mixins.add("late.MixinChromaOptions");
            if(ModConfig.enableChromatiCraftMixin)
                mixins.add("late.MixinProgressOverlayRenderer");
        }
        if(loadedMods.contains("GeoStrata") && loadedMods.contains("DragonAPI")) {
            mixins.add("late.MixinGeoOptions");
            mixins.add("late.MixinRetroGenController");
            mixins.add("late.geostrata.MixinBlockDecoGen_CrystalSpikeOrientation");
            mixins.add("late.geostrata.MixinMetadataItemBlock_DecoGenItems");
            if (FMLLaunchHandler.side() == Side.CLIENT) {
                if (ModConfig.stabilizeGeoStrataDecoGenShapes) {
                    mixins.add("late.geostrata.MixinDecoGenRenderer_DeterministicShapes");
                }
                mixins.add("late.geostrata.MixinDecoGenRenderer_CrystalSpikeWater");
                mixins.add("late.geostrata.MixinGeoClient_DecoGenItemRenderer");
                mixins.add("late.geostrata.MixinBlockDecoGen_ParticleIcons");
            }
        }
        if(loadedMods.contains("DragonAPI")) {
            mixins.add("late.FixNullCrash");
            mixins.add("late.MixinMTInteractionManager");
        }
        if(loadedMods.contains("Hats")) {
            mixins.add("late.MixinHatsEventHandler");
            if (FMLLaunchHandler.side() == Side.CLIENT) {
                mixins.add("late.MixinHatsKeybind");
            }
        }
        if (loadedMods.contains("divinerpg")) {
            mixins.add("late.divinerpg.MixinArcanaTickHandler_FullOnLogin");
            if (FMLLaunchHandler.side() == Side.CLIENT) {
                mixins.add("late.divinerpg.MixinArcanaRenderer_HideFullArcana");
                if (ModConfig.divineRpgDisableHaliteExtraArmorPieceRender) {
                    mixins.add("late.divinerpg.MixinGUIOverlay_DisableHaliteArmorPiece");
                }
            }
        }
        if (loadedMods.contains("musicchoices") && FMLLaunchHandler.side() == Side.CLIENT) {
            mixins.add("late.musicchoices.MixinMusicChoicesEventHandler_NullSound");
        }
        if(loadedMods.contains("chocolateQuest")) {
            if (ModConfig.fixChocolateQuestDivideByZero) {
                mixins.add("late.chocolatequest.MixinWorldGeneratorNew_CatchBuilderArithmetic");
            }
            if (FMLLaunchHandler.side() == Side.CLIENT && ModConfig.hideChocolateQuestGeneratingStructureOverlay) {
                mixins.add("late.chocolatequest.MixinGuiInGameStats_HideStructureOverlay");
            }
        }
        if (loadedMods.contains("battlegear2")) {
            mixins.add("late.battlegear.MixinBattlegearClientEventsBridge");
        }
        if (hasAnyMod(loadedMods, "Botania", "botania") && FMLLaunchHandler.side() == Side.CLIENT) {
            mixins.add("late.botania.MixinHUDHandler_ManaBarOffset");
        }
        if(loadedMods.contains("LambdaLib")) {
            mixins.add("late.MixinRenderImagPhaseLiquid_Optimize");
            mixins.add("late.MixinPhaseLiquidGenerator_ChunkAligned");
        }

        if (loadedMods.contains("netherlicious")) {
            mixins.add("late.netherlicious.MixinNetherWorldProvider_CustomHeight");
            mixins.add("late.netherlicious.MixinMaxHeightNetherChunkProvider_CustomHeight");
        }

        // --- Unconfigurable late mixins (disable third-party network checks / log spam) ---
        if (loadedMods.contains("iChunUtil")) {
            mixins.add("late.ichun.ichunutil.MixinThreadGetPatrons");
            mixins.add("late.ichun.ichunutil.MixinModVersionChecker");
        }

        if (loadedMods.contains("XaeroMinimap") || loadedMods.contains("XaeroWorldMap")) {
            mixins.add("late.xaero.patreon.MixinPatreon7");
        }
        if (loadedMods.contains("XaeroMinimap")) {
            mixins.add("late.xaero.minimap.MixinInternet");
        }
        if (loadedMods.contains("XaeroWorldMap")) {
            mixins.add("late.xaero.worldmap.MixinInternet");
        }

        if (loadedMods.contains("ExtraUtilities")) {
            mixins.add("late.extrautilities.MixinEnderConstructorRecipesHandler");
        }

        if (loadedMods.contains("NotEnoughItems") && FMLLaunchHandler.side() == Side.CLIENT) {
            mixins.add("late.nei.MixinShapedRecipeHandler_DartInfusionOutputCycle");
            mixins.add("late.nei.MixinTemplateRecipeHandler_CycleDartResult");
        }

        if (loadedMods.contains("modtweaker2") && loadedMods.contains("TConstruct")) {
            mixins.add("late.modtweaker.MixinSmelteryMeltingRecipe_SafeRenderer");
        }

        if (ModConfig.blockEtFuturumElytraWhileAvatarGliding
                && hasAnyMod(loadedMods, "etfuturum", "EtFuturum", "etfuturumrequiem", "EtFuturumRequiem")) {
            mixins.add("late.etfuturum.MixinStartElytraFlyingHandler_NoAvatarGlider");
            if (hasAnyMod(loadedMods, "OpenBlocks", "openblocks")) {
                mixins.add("late.openblocks.MixinItemHangGlider_NoElytra");
            }
        }

        if (loadedMods.contains("ThermalDynamics") && ModConfig.disableThermalDynamicsFacades) {
            mixins.add("late.thermaldynamics.MixinCoverHelper_NoFacades");
            mixins.add("late.thermaldynamics.MixinItemCover_NoFacades");
            mixins.add("late.thermaldynamics.MixinTileTDBase_NoFacades");
            if (FMLLaunchHandler.side() == Side.CLIENT) {
                mixins.add("late.thermaldynamics.MixinTDCreativeTabCovers_NoFacades");
            }
        }

        if (ModConfig.enableFenceTextureModule
                && FMLLaunchHandler.side() == Side.CLIENT
                && hasAnyMod(loadedMods, "Forestry", "forestry")) {
            mixins.add("late.forestry.MixinRenderFenceItem_FenceTextures");
        }

        // vortex mixins
        if (hasAnyMod(loadedMods, "armoredarms", "ArmoredArms") && FMLLaunchHandler.side() == Side.CLIENT) {
            mixins.add("late.vortex.MixinArmoredArmsItemGlint");
            mixins.add("late.vortex.MixinArmoredArmsArmorGlintTexture");
            mixins.add("late.vortex.MixinArmoredArmsArmorModelManager");
        }
        if (loadedMods.contains("Thaumcraft")) {
            mixins.add("late.vortex.MixinInfusionEnchantmentRecipe");
            mixins.add("late.thaumcraft.MixinThaumcraft_WarpSyncSafeLogin");
            mixins.add("late.thaumcraft.MixinResearchManager_LogEarlyWarpResearch");
            if (ModConfig.thaumcraftOnlyWarpResearchRequiresMinigame) {
                mixins.add("late.thaumcraft.AccessorPacketPlayerCompleteToServer");
                mixins.add("late.thaumcraft.MixinPacketPlayerCompleteToServer_WarpOnlyResearchNotes");
                if (FMLLaunchHandler.side() == Side.CLIENT) {
                    mixins.add("late.thaumcraft.MixinGuiResearchBrowser_WarpOnlyResearchText");
                }
            }
        }

        // AlternativeChunkloading compatibility fixes port
        if (hasAnyMod(loadedMods, "erebus", "Erebus")) {
            mixins.add("late.erebus.MixinSpawnerErebus");
        }

        if (hasAnyMod(loadedMods, "DimDoors", "dimdoors", "DimensionalDoors", "dimensionaldoors")) {
            mixins.add("late.dimdoors.legacy.MixinChunkBlockSetter");
            mixins.add("late.dimdoors.legacy.MixinDungeonSchematic");
            mixins.add("late.dimdoors.legacy.MixinWorldBlockSettings");
            mixins.add("late.dimdoors.legacy.MixinPocketBuilder");

            mixins.add("late.dimdoors.modern.MixinChunkBlockSetter");
            mixins.add("late.dimdoors.modern.MixinDungeonSchematic");
            mixins.add("late.dimdoors.modern.MixinWorldBlockSettings");
            mixins.add("late.dimdoors.modern.MixinPocketBuilder");
        }

        if (loadedMods.contains("Satisforestry")
                && (ModConfig.satisforestryLizardDoggoAllowNametagRename
                || ModConfig.satisforestryLizardDoggoDisableRandomItemFinding)) {
            mixins.add("late.satisforestry.MixinEntityLizardDoggo");
        }

        return mixins;
    }

    private static boolean hasAnyMod(Set<String> loadedMods, String... modIds) {
        for (String modId : modIds) {
            if (loadedMods.contains(modId)) {
                return true;
            }
        }
        return false;
    }
}
