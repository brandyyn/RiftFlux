package com.voidsrift.riftflux;

import com.voidsrift.riftflux.core.BasicTransformer;
import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RFEarlyMixins implements IFMLLoadingPlugin, IEarlyMixinLoader {

    public RFEarlyMixins() {
        ModConfig.init(new File("config/riftflux.cfg"));
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
        if (ModConfig.enableSafeEntityTick) {
            mixins.add("early.MixinEntitySafeTick");
            mixins.add("early.MixinWorldSafeEntityTick");
        }
        if (ModConfig.reworkVillageGolems) {
            mixins.add("early.MixinVillageGolemBlock");
            mixins.add("early.MixinStructureStartVillageSpawn");
        }
        if (ModConfig.disableSleepRainClear) {
            mixins.add("early.DisableSleepRainClear");
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
        if (ModConfig.disableBonemeal) {
            mixins.add("early.MixinItemDye_DisableBonemeal");
        }
        if (ModConfig.enableMeleeDamageTooltip) {
            mixins.add("early.MixinTooltip");
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
        }
        if (ModConfig.enableNewBlockHighlight) {
            mixins.add("early.MixinBlockHighlight");
        }
        return mixins;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{BasicTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass() { return null; }

    @Override
    public String getSetupClass() { return null; }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() { return null; }
}
