package com.voidsrift.riftflux.client;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.CommonProxy;
import com.voidsrift.riftflux.combat.torohealth.client.particle.DamageParticles;
import com.voidsrift.riftflux.combat.torohealth.mixins.EntityLivingBaseExt;
import com.voidsrift.riftflux.chester.ChesterContent;
import com.voidsrift.riftflux.compat.hats.HatsKeybinds;
import com.voidsrift.riftflux.compat.chromaticraft.ChromatiCraftItemFabricatorTooltipHandler;
import com.voidsrift.riftflux.dualhotbar.DualHotbarClient;
import com.voidsrift.riftflux.duckling.DucklingContent;
import com.voidsrift.riftflux.fence.FenceOverrideClientState;
import com.voidsrift.riftflux.client.chatcopy.ChatSelectionManager;
import com.voidsrift.riftflux.client.chatbubbles.ChatBubblesClient;
import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeHandler;
import com.voidsrift.riftflux.client.inventory.InventoryShortcutHandler;
import com.voidsrift.riftflux.client.sky.CelestialFogEventClientState;
import com.voidsrift.riftflux.client.sky.FogDistanceGradientRenderer;
import com.voidsrift.riftflux.glowstonedust.GlowstoneDustContent;
import com.voidsrift.riftflux.inventorypets.InventoryPetsContent;
import com.voidsrift.riftflux.placeablegunpowder.PlaceableGunpowderContent;
import com.voidsrift.riftflux.client.worldtooltips.WorldTooltipClient;
import com.voidsrift.riftflux.terramine.EyeOfCthulhuMusicHandler;
import com.voidsrift.riftflux.terramine.IceRodPlacementPreviewRenderer;
import com.voidsrift.riftflux.terramine.TerrariaContent;
import com.voidsrift.riftflux.asgardshield.AsgardShieldContent;
import com.voidsrift.riftflux.painting.GuiPaintingSelector;
import com.voidsrift.riftflux.palaria.client.NimatinJumpHud;
import com.voidsrift.riftflux.palaria.PalariaMobContent;
import com.voidsrift.riftflux.axolotl.AxolotlContent;
import com.voidsrift.riftflux.blessings.BlessingContent;
import com.voidsrift.riftflux.furniture.FurnitureContent;
import com.voidsrift.riftflux.offlawn.OffLawnContent;
import com.voidsrift.riftflux.offlawn.client.OffLawnClientContent;
import com.voidsrift.riftflux.pumpkinpastures.PumpkinPasturesContent;
import com.voidsrift.riftflux.specialarmor.SpecialArmorContent;
import com.voidsrift.riftflux.wam.WAMContent;
import com.voidsrift.riftflux.util.RFPlantContext;
import com.voidsrift.riftflux.tweaks.ladder.client.DoubleSidedLadderRenderer;
import com.voidsrift.riftflux.tweaks.ladder.client.RFRenderIds;
import com.voidsrift.riftflux.wheatfield.WheatfieldContent;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
    private static boolean clientPreInitFeaturesInitialized;
    private static boolean clientFeaturesInitialized;

    @Override
    public boolean isJumpKeyDown() {
        // Client-side: read the jump keybinding directly (no reflection).
        return Minecraft.getMinecraft().gameSettings.keyBindJump.getIsKeyPressed();
    }

    @Override
    public void openPaintingSelectorScreen() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null) {
            return;
        }
        mc.displayGuiScreen(new GuiPaintingSelector());
    }

    @Override
    public void applyBlessingSync(String blessing, boolean hasSource, int x, int y, int z, int dim) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null) {
            return;
        }
        if (blessing != null && !blessing.isEmpty()) {
            com.voidsrift.riftflux.blessings.BlessingHelper.setBlessing(mc.thePlayer, blessing);
        } else {
            com.voidsrift.riftflux.blessings.BlessingHelper.clearBlessing(mc.thePlayer);
        }
        if (hasSource) {
            mc.thePlayer.getEntityData().setInteger(com.voidsrift.riftflux.blessings.BlessingHelper.NBT_BLESSING_PILLAR_X, x);
            mc.thePlayer.getEntityData().setInteger(com.voidsrift.riftflux.blessings.BlessingHelper.NBT_BLESSING_PILLAR_Y, y);
            mc.thePlayer.getEntityData().setInteger(com.voidsrift.riftflux.blessings.BlessingHelper.NBT_BLESSING_PILLAR_Z, z);
            mc.thePlayer.getEntityData().setInteger(com.voidsrift.riftflux.blessings.BlessingHelper.NBT_BLESSING_PILLAR_DIM, dim);
        } else {
            com.voidsrift.riftflux.blessings.BlessingHelper.clearBlessingSource(mc.thePlayer);
        }
    }

    @Override
    public void applyToroHealthDamage(int entityId, int damage) {
        if (!ModConfig.enableToroHealthModule || !ModConfig.toroHealthShowDamageParticles || damage <= 0) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.theWorld == null) {
            return;
        }

        Entity entity = mc.theWorld.getEntityByID(entityId);
        if (!(entity instanceof EntityLivingBase)) {
            return;
        }

        EntityLivingBase living = (EntityLivingBase) entity;
        DamageParticles.spawnDamageParticle(living, damage);

        if (living instanceof EntityLivingBaseExt) {
            ((EntityLivingBaseExt) living).riftflux$setToroHealthLastDamageParticleTick(living.ticksExisted);
        }
    }

    @Override
    public void applyRespawnDelaySync(long remainingMs) {
        ClientRespawnDelayState.applyRemainingMs(remainingMs);
    }

    @Override
    public void applyFenceOverrideSync(boolean fullSync, int dimensionId, boolean enabled, int[] coordinates) {
        FenceOverrideClientState.applySync(fullSync, dimensionId, enabled, coordinates);
    }

    @Override
    public void applyCrossedPlantFacingSync(int dimensionId, int x, int y, int z, int facing) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.theWorld == null || mc.theWorld.provider == null) {
            return;
        }
        if (mc.theWorld.provider.dimensionId != dimensionId) {
            return;
        }
        RFPlantContext.markCrossedPlantFacing(mc.theWorld, x, y, z, facing);
    }

    @Override
    public void applyCelestialFogEventSync(int dimensionId, boolean dayFog, boolean nightFog, boolean weatherFog) {
        CelestialFogEventClientState.applySync(dimensionId, dayFog, nightFog, weatherFog);
    }

    @Override
    public void preInitClientFeatures() {
        if (clientPreInitFeaturesInitialized) {
            return;
        }
        clientPreInitFeaturesInitialized = true;
        com.voidsrift.riftflux.geostrata.client.GeoStrataDecoGenTextureEvents.bootstrap();
    }

    @Override
    public void initClientFeatures() {
        if (clientFeaturesInitialized) {
            return;
        }
        clientFeaturesInitialized = true;

        // Register NEI handler tab icon
        com.voidsrift.riftflux.nei.GTNHNeiHandlerInfo.register();

        // Hanging ladders: double-sided renderer (used by Hanging Ladder block)
        if (ModConfig.enableHangingLadders && RFRenderIds.doubleSidedLadderRenderId < 0) {
            RFRenderIds.doubleSidedLadderRenderId = RenderingRegistry.getNextAvailableRenderId();
            RenderingRegistry.registerBlockHandler(new DoubleSidedLadderRenderer());
        }

        // HUD (item pickup notifications)
        if (ModConfig.enablePickupNotifier) {
            PickupNotifierHud.bootstrap();
        }
        if (ModConfig.enableIsometricPhotoMode) {
            IsometricPhotoModeHandler.bootstrap();
        }
        InventoryShortcutHandler.bootstrap();
        if (ModConfig.enableChatSelectionCopy) {
            ChatSelectionManager.bootstrap();
        }
        if (ModConfig.enableChatBubblesModule) {
            ChatBubblesClient.bootstrap();
        }
        if (ModConfig.enableWorldTooltips) {
            WorldTooltipClient.bootstrap();
        }
        CelestialFogEventClientState.bootstrap();
        PostProcessRenderer.bootstrap();
        BombCarryClientHandler.bootstrap();
        StarbeamRailHud.bootstrap();
        TorchBillboardRenderer.bootstrap();
        // Stars (tag new items so the GUI mixin can draw)
        if (ModConfig.enableItemPickupStar) {
            PickupStarClientTracker.bootstrap();
            if (ModConfig.itemPickupStarShowHotbarHud) {
                PickupStarHotbarHud.bootstrap();
            }
        }
        com.voidsrift.riftflux.placeditem.PlacedItemContent.initClient();
        PlaceableGunpowderContent.initClient();
        GlowstoneDustContent.initClient();
        DualHotbarClient.init();
        com.voidsrift.riftflux.vortex.vortexContent.initClient();
        com.voidsrift.riftflux.avatar.AvatarTLBContent.initClient();
        TerrariaContent.initClient();
        SpecialArmorContent.initClient();
        InventoryPetsContent.initClient();
        AsgardShieldContent.initClient();
        BlessingContent.initClient();
        FurnitureContent.initClient();
        ChesterContent.initClient();
        AxolotlContent.initClient();
        DucklingContent.initClient();
        WheatfieldContent.initClient();
        WAMContent.initClient();
        OffLawnContent.initClient();
        OffLawnClientContent.initClient();
        PumpkinPasturesContent.initClient();
        PalariaMobContent.initClient();
        if (PalariaMobContent.isEnabled()) {
            NimatinJumpHud.bootstrap();
        }
        if (Loader.isModLoaded("Hats")) {
            HatsKeybinds.ensureRegistered();
        }
        if (ModConfig.enableFenceTextureModule) {
            MinecraftForge.EVENT_BUS.register(new FenceOverrideClientEvents());
        }
        if (ModConfig.celestialFogDistanceGradient) {
            MinecraftForge.EVENT_BUS.register(new FogDistanceGradientRenderer());
        }
        MinecraftForge.EVENT_BUS.register(new LegendGearManaTooltipHandler());
        if (ModConfig.disableChromatiCraftItemFabricator && Loader.isModLoaded("ChromatiCraft")) {
            MinecraftForge.EVENT_BUS.register(new ChromatiCraftItemFabricatorTooltipHandler());
        }
        MinecraftForge.EVENT_BUS.register(new MovementSpeedFovLimitHandler());
        PetKnockdownTimeoutRenderer.bootstrap();
        PetKnockdownDizzyStarRenderer.bootstrap();
        MinecraftForge.EVENT_BUS.register(new UniversalDurabilityTooltipHandler());
        MinecraftForge.EVENT_BUS.register(new RiftExplorerSlingshotAmmoTooltipHandler());
        MinecraftForge.EVENT_BUS.register(new IceRodPlacementPreviewRenderer());
        com.voidsrift.riftflux.geostrata.client.GeoStrataDecoGenTextureEvents.bootstrap();
        FMLCommonHandler.instance().bus().register(new EyeOfCthulhuMusicHandler());

    }
}
