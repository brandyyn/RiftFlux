package com.excsi.riftfixes;

import com.excsi.riftfixes.core.BasicTransformer;
import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RFEarlyMixins implements IFMLLoadingPlugin, IEarlyMixinLoader {

    public RFEarlyMixins() {
        ModConfig.init(new File("config/RiftFixes.cfg"));
    }

    @Override
    public String getMixinConfig() {
        return "mixins." + Constants.MODID + ".early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        List<String> mixins = new ArrayList<>();
        if(ModConfig.enableChestLaunch)
            mixins.add("early.MixinBlockChestLaunch");
        if(ModConfig.enableFullExplosionDrops)
            mixins.add("early.MixinTNT");
        if(ModConfig.enableMeleeDamageTooltip)
            mixins.add("early.MixinTooltip");
        if(ModConfig.enableArmorMixin)
            mixins.add("early.MixinArmorProperties");
        if(ModConfig.changeArmorBarAmount)
            mixins.add("early.MixinForgeHooks");
        return mixins;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{BasicTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
