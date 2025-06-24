package com.excsi.riftfixes;

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
        if(loadedMods.contains("aether"))
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
            mixins.add("late.MixinRemoveDing");
        }
        return mixins;
    }
}
