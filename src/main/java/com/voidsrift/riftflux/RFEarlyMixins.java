package com.voidsrift.riftflux;

import com.voidsrift.riftflux.core.BasicTransformer;
import com.voidsrift.riftflux.dualhotbar.DualHotbarTransformer;
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
        if (ModConfig.allowPlantsOnAnyBlock) {
            mixins.add("early.MixinBlockBush_AnySupport");
            mixins.add("early.MixinBlock_PlayerPlacedBushMarker");
            mixins.add("early.MixinBiomeGenBase_WorldGenContext");
        }
        if (ModConfig.strictMobSpawnsZeroBlockLight) {
            mixins.add("early.MixinEntityMob_ZeroBlockLightSpawn");
        }
        if (ModConfig.wrongUseSingleDurability) {
            mixins.add("early.MixinItemStack_WrongUseDurability");
        }
        if (ModConfig.invincibleOwnedMobs ) {
            mixins.add("early.MixinEntityLivingBase_PetInvincibility");
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
        if (ModConfig.disableSpecificPotions) {
            mixins.add("early.MixinEntityLivingBase_DisablePotions");
        }
        if (ModConfig.disableTintedSugarcane) {
            mixins.add("early.MixinBlockReed");
        }
        if (ModConfig.enableNetherrackTweak) {
            mixins.add("early.MixinBlock_NoNetherrackDropsOutsideNether");
        }
        if (ModConfig.enableDoorAirPlacement) {
            mixins.add("early.MixinBlockDoor_AirPlacement");
            mixins.add("early.MixinItemDoor_ReplaceTallGrass");
            mixins.add("early.MixinBlock_DoorShiftOnReplaceable");
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
        if (ModConfig.playerOnlyHurtSound && cpw.mods.fml.relauncher.FMLLaunchHandler.side() == cpw.mods.fml.relauncher.Side.CLIENT) {
            mixins.add("early.MixinEntityPlayer_CustomHurtSound");
        }

        if (cpw.mods.fml.relauncher.FMLLaunchHandler.side() == cpw.mods.fml.relauncher.Side.CLIENT) {
            if (ModConfig.enableHotbarSelectorTexture) {
                mixins.add("early.MixinGuiIngame_HotbarSelectorTexture");
            }
            boolean hasBackhand = loadedCoreMods.contains("xonin.backhand.coremod.BackhandLoadingPlugin")
                    || classExists("xonin.backhand.Backhand");
            if (hasBackhand) {
                mixins.add("early.backhand.MixinGuiInventory_BackhandSlot");
            }
        }
        mixins.add("early.baubles.MixinContainerPlayerExpanded_BackpackShiftClick");

        // vortex mixins (always enabled)
        mixins.add("early.vortex.MixinArmorProperties");
        mixins.add("early.vortex.MixinBlockLiquid");
        mixins.add("early.vortex.MixinContainer");
        mixins.add("early.vortex.MixinEnchantmentHelper");
        mixins.add("early.vortex.MixinEntityItemBackpackPickup");
        mixins.add("early.vortex.MixinEntityPlayer");
        mixins.add("early.vortex.MixinInventoryPlayerBackpackPickup");
        mixins.add("early.vortex.MixinItemStackCustomGlint");
        mixins.add("early.vortex.MixinItemStack_BackpackBreakDrop");
        mixins.add("early.vortex.MixinRenderItem");
        mixins.add("early.vortex.MixinWorld");
        if (cpw.mods.fml.relauncher.FMLLaunchHandler.side() == cpw.mods.fml.relauncher.Side.CLIENT) {
            mixins.add("early.vortex.MixinGuiContainer");
            mixins.add("early.vortex.MixinGuiInventory");
            mixins.add("early.vortex.MixinGuiScreen");
            mixins.add("early.vortex.MixinItemRenderer");
            mixins.add("early.vortex.MixinItemRendererOF");
            mixins.add("early.vortex.MixinMinecraft_ToolbeltFocus");
            mixins.add("early.vortex.MixinRendererLivingEntity");
        }

        return mixins;
    }

    private static boolean classExists(String name) {
        try {
            Class.forName(name, false, RFEarlyMixins.class.getClassLoader());
            return true;
        } catch (Throwable ignored) {
            return false;
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
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() { return null; }
}
