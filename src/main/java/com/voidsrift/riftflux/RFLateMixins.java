package com.voidsrift.riftflux;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

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
        }
        if(loadedMods.contains("DragonAPI")) {
            mixins.add("late.FixNullCrash");
            mixins.add("late.MixinMTInteractionManager");
        }
        if(loadedMods.contains("Hats")) {
            mixins.add("late.MixinHatsEventHandler");
        }
        if(loadedMods.contains("chocolateQuest")) {
            mixins.add("late.MixinGameRegistry_CatchCQDivZero");
        }
        if(loadedMods.contains("LambdaLib")) {
            mixins.add("late.MixinRenderImagPhaseLiquid_Optimize");
            mixins.add("late.MixinPhaseLiquidGenerator_ChunkAligned");
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

        return mixins;
    }
}
