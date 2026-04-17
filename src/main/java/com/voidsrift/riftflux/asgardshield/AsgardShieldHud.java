package com.voidsrift.riftflux.asgardshield;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.dualhotbar.DualHotbarConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;

public final class AsgardShieldHud extends Gui {
    private static final AsgardShieldHud INSTANCE = new AsgardShieldHud();
    private static final ResourceLocation TEX_GUARD =
            new ResourceLocation("riftflux", "textures/gui/asgardshield/GuardGauge.png");
    private static final ResourceLocation TEX_VANGUARD =
            new ResourceLocation("riftflux", "textures/gui/asgardshield/VCGauge.png");
    private static final int GUARD_PIP_COUNT = 10;
    private static final int GUARD_GAUGE_MAX = 200;
    private static final int GUARD_GAUGE_PER_PIP = GUARD_GAUGE_MAX / GUARD_PIP_COUNT;
    private static final int GUARD_PIP_WIDTH = 9;
    private static final float VANGUARD_CHARGE_STAGES_PER_SECOND = 0.2F;
    private static final float VANGUARD_DRAIN_STAGES_PER_SECOND = 6.0F;
    private static final float GUI_TEX_SCALE = 1.0F / 256.0F;

    private static float displayedVanguardStage;
    private static long vanguardAnimationTimeNs;
    private static String vanguardAnimationPlayerKey;
    private static int vanguardAnimationDirection;
    private static boolean vanguardAnimationInitialized;

    private AsgardShieldHud() {
    }

    public static void bootstrap() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void onHud(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null || mc.gameSettings == null || mc.gameSettings.hideGUI) {
            return;
        }

        EntityPlayer player = mc.thePlayer;
        if (!isHudVisible(player)) {
            return;
        }

        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();
        int hotbarShiftY = getDualHotbarShiftY();

        renderVanguard(mc, player, width, height, hotbarShiftY);
        renderGuardGauge(mc, player, width, height, hotbarShiftY);
    }

    private void renderVanguard(Minecraft mc, EntityPlayer player, int width, int height, int hotbarShiftY) {
        if (!ModConfig.asgardShieldEnableVanguard) {
            return;
        }

        int x = width / 2 - 8;
        int y = height - 54 - ModConfig.asgardShieldHudVanguardYOffset * 16 - hotbarShiftY;

        TextureManager textures = mc.getTextureManager();
        textures.bindTexture(TEX_VANGUARD);
        drawAnimatedVanguardStage(player, x, y);
    }

    private void renderGuardGauge(Minecraft mc, EntityPlayer player, int width, int height, int hotbarShiftY) {
        int gauge = AsgardShieldState.getGuardGauge(player);
        boolean blocking = AsgardShieldLogic.isBlockingWithAsgardItem(player);
        if (!blocking && gauge <= 0) {
            return;
        }

        ItemStack active = AsgardShieldLogic.getEquippedAsgardItem(player);
        boolean gilded = active != null
                && ((active.getItem() instanceof ItemAsgardShield
                && ((ItemAsgardShield) active.getItem()).isGilded())
                || AsgardShieldLogic.isEnderflameSword(active));
        boolean greatsword = active != null && active.getItem() instanceof ItemAsgardGreatsword;
        boolean broken = AsgardShieldState.isGuardBroken(player);
        boolean ward = active != null && getConfiguredEnchantLevel(ModConfig.asgardShieldHarkenWardAugmentId, active) > 0;

        int baseU = 0;
        if (greatsword) {
            baseU = 36;
        } else if (gilded) {
            baseU = 18;
        }

        int rowV = 0;
        if (broken) {
            rowV = 9;
        } else if (ward) {
            rowV = 18;
        }

        int fullPips = Math.max(0, Math.min(GUARD_PIP_COUNT, gauge / GUARD_GAUGE_PER_PIP));
        int x = width / 2 + 10;
        int y = height - 49 - ModConfig.asgardShieldHudGuardGaugeYOffset * 9 - hotbarShiftY;

        TextureManager textures = mc.getTextureManager();
        textures.bindTexture(TEX_GUARD);

        for (int i = 0; i < GUARD_PIP_COUNT; i++) {
            int px = x + i * 8;
            int emptyU = baseU + GUARD_PIP_WIDTH;
            this.drawTexturedModalRect(px, y, emptyU, rowV, GUARD_PIP_WIDTH, 9);
            if (i < fullPips) {
                this.drawTexturedModalRect(px, y, baseU, rowV, GUARD_PIP_WIDTH, 9);
            }
        }
    }

    public static boolean isHudVisible(EntityPlayer player) {
        if (!ModConfig.enableAsgardShieldModule || player == null) {
            return false;
        }
        return AsgardShieldLogic.isAsgardItem(AsgardShieldLogic.getEquippedAsgardItem(player));
    }

    public static boolean isGuardGaugeVisible(EntityPlayer player) {
        if (!isHudVisible(player)) {
            return false;
        }
        return AsgardShieldLogic.isBlockingWithAsgardItem(player) || AsgardShieldState.getGuardGauge(player) > 0;
    }

    public static int getGuardGaugeY(EntityPlayer player, int screenHeight) {
        if (!isGuardGaugeVisible(player)) {
            return Integer.MIN_VALUE;
        }
        return screenHeight - 49 - ModConfig.asgardShieldHudGuardGaugeYOffset * 9 - getDualHotbarShiftY();
    }

    public static boolean isVanguardVisible(EntityPlayer player) {
        return isHudVisible(player) && ModConfig.asgardShieldEnableVanguard;
    }

    public static int getVanguardY(EntityPlayer player, int screenHeight) {
        if (!isVanguardVisible(player)) {
            return Integer.MIN_VALUE;
        }
        return screenHeight - 54 - ModConfig.asgardShieldHudVanguardYOffset * 16 - getDualHotbarShiftY();
    }

    public static int getHudTopY(EntityPlayer player, int screenHeight) {
        int topY = Integer.MAX_VALUE;
        int vanguardY = getVanguardY(player, screenHeight);
        if (vanguardY != Integer.MIN_VALUE) {
            topY = Math.min(topY, vanguardY);
        }
        int guardY = getGuardGaugeY(player, screenHeight);
        if (guardY != Integer.MIN_VALUE) {
            topY = Math.min(topY, guardY);
        }
        return topY == Integer.MAX_VALUE ? Integer.MIN_VALUE : topY;
    }

    public static int getTargetAirBubbleTopY(EntityPlayer player, int screenHeight, int defaultAirTopY) {
        int guardY = getGuardGaugeY(player, screenHeight);
        if (guardY != Integer.MIN_VALUE) {
            return Math.min(defaultAirTopY, guardY - 10);
        }
        return defaultAirTopY;
    }

    private static int getConfiguredEnchantLevel(int enchantId, ItemStack stack) {
        if (stack == null || enchantId <= 0 || enchantId >= Enchantment.enchantmentsList.length) {
            return 0;
        }
        if (Enchantment.enchantmentsList[enchantId] == null) {
            return 0;
        }
        return EnchantmentHelper.getEnchantmentLevel(enchantId, stack);
    }

    public static int getDualHotbarShiftY() {
        if (!DualHotbarConfig.enable
                || (!DualHotbarConfig.twoLayerRendering && DualHotbarConfig.numHotbars != 4)) {
            return 0;
        }
        if (DualHotbarConfig.twoLayerRendering) {
            return 20 * (DualHotbarConfig.numHotbars - 1);
        }
        return 20 * (DualHotbarConfig.numHotbars / 2 - 1);
    }

    private static int getVanguardU(int count) {
        int u = count * 16 - 16;
        if (u < 0) {
            return 0;
        }
        return Math.min(96, u);
    }

    private void drawAnimatedVanguardStage(EntityPlayer player, int x, int y) {
        float targetStage = getTargetVanguardStage(player);
        float stage = updateDisplayedVanguardStage(player, targetStage);
        int lowerStage = Math.max(0, Math.min(7, (int) Math.floor(stage)));
        int upperStage = Math.max(0, Math.min(7, lowerStage + 1));
        float blend = Math.max(0.0F, Math.min(1.0F, stage - lowerStage));
        int lowerU = getVanguardU(lowerStage);
        int upperU = getVanguardU(upperStage);

        if (upperU == lowerU || blend <= 0.0F) {
            this.drawTexturedModalRect(x, y, lowerU, 0, 16, 16);
            return;
        }

        if (vanguardAnimationDirection < 0) {
            this.drawTexturedModalRect(x, y, upperU, 0, 16, 16);
            drawVanguardStageRegion(x, y, lowerU, 0.0F, 16.0F * (1.0F - blend));
            return;
        }

        this.drawTexturedModalRect(x, y, lowerU, 0, 16, 16);
        float overlayHeight = 16.0F * blend;
        drawVanguardStageRegion(x, y, upperU, 16.0F - overlayHeight, overlayHeight);
    }

    private static float getTargetVanguardStage(EntityPlayer player) {
        int count = AsgardShieldState.getVanguardCount(player);
        int ticks = AsgardShieldState.getVanguardTicks(player);
        float stage = count;
        if (count < 7) {
            stage += Math.max(0.0F, Math.min(1.0F, ticks / 100.0F));
        }
        return Math.max(0.0F, Math.min(7.0F, stage));
    }

    private static float updateDisplayedVanguardStage(EntityPlayer player, float targetStage) {
        long now = System.nanoTime();
        String playerKey = getVanguardAnimationPlayerKey(player);
        if (!vanguardAnimationInitialized
                || playerKey == null
                || !playerKey.equals(vanguardAnimationPlayerKey)
                || now <= 0L
                || now < vanguardAnimationTimeNs) {
            displayedVanguardStage = targetStage;
            vanguardAnimationTimeNs = now;
            vanguardAnimationPlayerKey = playerKey;
            vanguardAnimationDirection = 0;
            vanguardAnimationInitialized = true;
            return displayedVanguardStage;
        }

        float previousStage = displayedVanguardStage;
        float deltaSeconds = Math.min(0.25F, (now - vanguardAnimationTimeNs) / 1_000_000_000.0F);
        float speed = targetStage >= previousStage
                ? VANGUARD_CHARGE_STAGES_PER_SECOND
                : VANGUARD_DRAIN_STAGES_PER_SECOND;
        float maxDelta = speed * Math.max(0.0F, deltaSeconds);

        if (targetStage > previousStage) {
            displayedVanguardStage = Math.min(targetStage, previousStage + maxDelta);
        } else if (targetStage < previousStage) {
            displayedVanguardStage = Math.max(targetStage, previousStage - maxDelta);
        }

        if (displayedVanguardStage > previousStage) {
            vanguardAnimationDirection = 1;
        } else if (displayedVanguardStage < previousStage) {
            vanguardAnimationDirection = -1;
        } else if (targetStage > previousStage) {
            vanguardAnimationDirection = 1;
        } else if (targetStage < previousStage) {
            vanguardAnimationDirection = -1;
        } else {
            vanguardAnimationDirection = 0;
        }

        vanguardAnimationTimeNs = now;
        vanguardAnimationPlayerKey = playerKey;
        return Math.max(0.0F, Math.min(7.0F, displayedVanguardStage));
    }

    private static String getVanguardAnimationPlayerKey(EntityPlayer player) {
        if (player == null) {
            return null;
        }
        if (player.getGameProfile() != null && player.getGameProfile().getId() != null) {
            return player.getGameProfile().getId().toString();
        }
        return player.getCommandSenderName();
    }

    private void drawVanguardStageRegion(int x, int y, int u, float sourceY, float regionHeight) {
        if (regionHeight <= 0.0F) {
            return;
        }

        float startY = y + sourceY;
        float endY = startY + regionHeight;
        float minU = u * GUI_TEX_SCALE;
        float maxU = (u + 16) * GUI_TEX_SCALE;
        float minV = sourceY * GUI_TEX_SCALE;
        float maxV = (sourceY + regionHeight) * GUI_TEX_SCALE;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, endY, this.zLevel, minU, maxV);
        tessellator.addVertexWithUV(x + 16, endY, this.zLevel, maxU, maxV);
        tessellator.addVertexWithUV(x + 16, startY, this.zLevel, maxU, minV);
        tessellator.addVertexWithUV(x, startY, this.zLevel, minU, minV);
        tessellator.draw();
    }

}
