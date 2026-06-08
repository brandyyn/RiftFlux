package com.voidsrift.riftflux.dualhotbar;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.block.material.Material;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.Loader;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.asgardshield.AsgardShieldHud;
import com.voidsrift.riftflux.client.hud.HudHealthRowHelper;
import com.voidsrift.riftflux.legendgear.LegendGearClientState;
import net.nmccoy.legendgear.PlayerStarstatsExtension;

public class RenderHandler {
    private static final ResourceLocation WIDGITS = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation RIFT_SELECTOR = new ResourceLocation("riftflux:textures/gui/riftselector.png");
    private static final int SELECTOR_Y_OFFSET = 1;
    private static final int BATTLEGEAR_SLOT_MIN = 150;
    private static final int BATTLEGEAR_SLOT_MAX = 156;

    private boolean recievedPost = true;

    private static Constructor<ScaledResolution> scaledResolution172Constructor = null;
    private static boolean battlegearOverlayErrorLogged;
    private static boolean toolHighlightTicksFieldChecked;
    private static Field toolHighlightTicksField;
    private static boolean overlayMessageTicksFieldChecked;
    private static Field overlayMessageTicksField;
    private static boolean overlayMessageFieldChecked;
    private static Field overlayMessageField;
    private static boolean toolHighlightShiftPushed;
    private static boolean toolHighlightAttribPushed;

    private static boolean botaniaCompatChecked;
    private static boolean botaniaCompatAvailable;
    private static boolean botaniaCompatErrorLogged;
    private static Method botaniaHudRenderManaInvBar;
    private static Object botaniaHudHandlerInstance;
    private static Method botaniaGetPlayerBaubles;
    private static Class<?> botaniaIManaUsingItem;
    private static Method botaniaUsesManaMethod;
    private static Class<?> botaniaIManaItem;
    private static Method botaniaIsNoExportMethod;
    private static Method botaniaGetManaMethod;
    private static Method botaniaGetMaxManaMethod;
    private static Class<?> botaniaICreativeManaProvider;
    private static Method botaniaIsCreativeMethod;
    private static Class<?> botaniaTwigWandClass;

    static {
        try {
            scaledResolution172Constructor = ScaledResolution.class.getConstructor(
                    GameSettings.class,
                    int.class,
                    int.class
            );
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderHotbar(RenderGameOverlayEvent.Pre event) {
        if (!DualHotbarConfig.enable) {
            return;
        }

        if (event.type == ElementType.HOTBAR) {
            Minecraft mc = Minecraft.getMinecraft();

            ScaledResolution res;
            if (scaledResolution172Constructor != null) {
                try {
                    res = scaledResolution172Constructor.newInstance(
                            mc.gameSettings,
                            mc.displayWidth,
                            mc.displayHeight
                    );
                } catch (Exception ignored) {
                    return;
                }
            } else {
                res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            }

            int width = res.getScaledWidth();
            int height = res.getScaledHeight();

            mc.mcProfiler.startSection("actionBar");

            int offset = 20;

            boolean pushed = false;
            int previousMatrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
            try {
                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glPushMatrix();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glPushMatrix();
                mc.entityRenderer.setupOverlayRendering();
                pushed = true;

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.renderEngine.bindTexture(WIDGITS);

            InventoryPlayer inv = mc.thePlayer.inventory;
            int selectedSlot = getDisplaySelectedSlot(inv.currentItem, inv);
            int selectorX = 0;
            int selectorY = 0;
            boolean hasSelector = false;
            if (DualHotbarConfig.twoLayerRendering) {
                mc.ingameGUI.drawTexturedModalRect(width / 2 - 91, height - 22, 0, 0, 182, 22);

                if (!DualHotbarState.installedOnServer) {
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
                }

                for (int i = 1; i < DualHotbarConfig.numHotbars; i++) {
                    mc.ingameGUI.drawTexturedModalRect(
                            width / 2 - 91,
                            height - 22 * i - offset + (i - 1) * 2,
                            0,
                            0,
                            182,
                            21
                    );
                }

                if (!DualHotbarState.installedOnServer) {
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f);
                }

                if (selectedSlot >= 0 && selectedSlot < 9 * DualHotbarConfig.numHotbars) {
                    selectorX = width / 2 - 91 - 1 + (selectedSlot % 9) * 20;
                    selectorY = height - 22 - 1 - ((selectedSlot / 9) * offset);
                    hasSelector = true;
                }
            } else {
                mc.ingameGUI.drawTexturedModalRect(width / 2 - 91 - 90, height - 22, 0, 0, 182, 22);
                mc.ingameGUI.drawTexturedModalRect(width / 2 - 91 + 91, height - 22, 1, 0, 181, 22);
                mc.ingameGUI.drawTexturedModalRect(width / 2 - 91 + 91 - 1, height - 22, 20, 0, 22, 22);
                if (DualHotbarConfig.numHotbars == 4) {
                    mc.ingameGUI.drawTexturedModalRect(
                            width / 2 - 91 - 90,
                            height - 22 - offset,
                            0,
                            0,
                            182,
                            21
                    );
                    mc.ingameGUI.drawTexturedModalRect(
                            width / 2 - 91 + 91,
                            height - 22 - offset,
                            1,
                            0,
                            181,
                            21
                    );
                    mc.ingameGUI.drawTexturedModalRect(
                            width / 2 - 91 + 91 - 1,
                            height - 22 - offset,
                            20,
                            0,
                            22,
                            21
                    );
                }
                if (selectedSlot >= 0 && selectedSlot < 9 * DualHotbarConfig.numHotbars) {
                    selectorX = width / 2 - 91 - 1 + (selectedSlot % 18) * 20 - 90;
                    selectorY = height - 22 - 1 - ((selectedSlot / 18) * offset);
                    hasSelector = true;
                }
            }

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.enableGUIStandardItemLighting();
            RenderItem itemRenderer = new RenderItem();

            for (int i = 0; i < 9 * DualHotbarConfig.numHotbars; ++i) {
                if (DualHotbarConfig.twoLayerRendering) {
                    int x = width / 2 - 90 + (i % 9) * 20 + 2;
                    int z = height - 16 - 3 - ((i / 9) * offset);
                    if (!DualHotbarState.installedOnServer) {
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
                    }
                    renderInventorySlotItem(itemRenderer, i, x, z, 1f);
                    if (!DualHotbarState.installedOnServer) {
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f);
                    }
                } else {
                    int x = width / 2 - 90 + (i % 18) * 20 + 2 - 90;
                    int z = height - 16 - 3 - ((i / 18) * offset);
                    renderInventorySlotItem(itemRenderer, i, x, z, 1f);
                }
            }

            if (hasSelector) {
                drawSelectorAfterItems(mc, selectorX, selectorY);
            }

            for (int i = 0; i < 9 * DualHotbarConfig.numHotbars; ++i) {
                if (DualHotbarConfig.twoLayerRendering) {
                    int x = width / 2 - 90 + (i % 9) * 20 + 2;
                    int z = height - 16 - 3 - ((i / 9) * offset);
                    if (!DualHotbarState.installedOnServer) {
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
                    }
                    renderInventorySlotOverlay(itemRenderer, i, x, z);
                    if (!DualHotbarState.installedOnServer) {
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f);
                    }
                } else {
                    int x = width / 2 - 90 + (i % 18) * 20 + 2 - 90;
                    int z = height - 16 - 3 - ((i / 18) * offset);
                    renderInventorySlotOverlay(itemRenderer, i, x, z);
                }
            }

            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            mc.mcProfiler.endSection();

            renderBotaniaManaBarFallback(res, mc);
            renderBattlegearOverlayIfPresent(event);
            event.setCanceled(true);
            } finally {
                if (pushed) {
                    GL11.glMatrixMode(GL11.GL_MODELVIEW);
                    GL11.glPopMatrix();
                    GL11.glMatrixMode(GL11.GL_PROJECTION);
                    GL11.glPopMatrix();
                    GL11.glMatrixMode(previousMatrixMode);
                    GL11.glPopAttrib();
                }
            }
        }
    }

    private void renderBattlegearOverlayIfPresent(RenderGameOverlayEvent.Pre event) {
        if (!Loader.isModLoaded("battlegear2")) {
            return;
        }

        try {
            Object instance = mods.battlegear2.client.BattlegearClientEvents.INSTANCE;
            if (instance instanceof IBattlegearClientEventsBridge) {
                ((IBattlegearClientEventsBridge) instance).riftflux$renderGameOverlay(
                        event.partialTicks,
                        event.mouseX,
                        event.mouseY
                );
            }
        } catch (Throwable t) {
            if (!battlegearOverlayErrorLogged) {
                battlegearOverlayErrorLogged = true;
                System.out.println("[RiftFlux] Battlegear overlay render failed: " + t);
                t.printStackTrace();
            }
        }
    }

    private void renderBotaniaManaBarFallback(ScaledResolution res, Minecraft mc) {
        if (res == null || mc == null || mc.thePlayer == null) {
            return;
        }
        InventoryPlayer inv = mc.thePlayer.inventory;
        if (inv == null) {
            return;
        }
        if (!ensureBotaniaCompat()) {
            return;
        }

        BotaniaManaSnapshot snapshot = new BotaniaManaSnapshot();
        scanBotaniaManaInventory(inv, snapshot);
        IInventory baubles = getBaublesInventory(mc.thePlayer);
        scanBotaniaManaInventory(baubles, snapshot);

        boolean fallbackContext = inv.currentItem >= 9 || !snapshot.hasUsing;
        if (!fallbackContext) {
            return;
        }
        boolean hasHeldOrWornManaCarrier = hasHeldOrWornBotaniaManaCarrier(mc, baubles);
        if (!snapshot.hasUsing && !hasHeldOrWornManaCarrier) {
            return;
        }
        if (!snapshot.hasCreative && snapshot.totalMaxMana <= 0) {
            return;
        }

        try {
            int mana = Math.max(0, snapshot.totalMana);
            int maxMana = Math.max(0, snapshot.totalMaxMana);
            botaniaHudRenderManaInvBar.invoke(botaniaHudHandlerInstance, res, snapshot.hasCreative, mana, maxMana);
        } catch (Throwable t) {
            if (!botaniaCompatErrorLogged) {
                botaniaCompatErrorLogged = true;
                System.out.println("[RiftFlux] Botania mana bar fallback failed: " + t);
                t.printStackTrace();
            }
            botaniaCompatAvailable = false;
        }
    }

    private static boolean hasHeldOrWornBotaniaManaCarrier(Minecraft mc, IInventory baubles) {
        if (mc == null || mc.thePlayer == null) {
            return false;
        }
        if (isBotaniaManaCarrier(mc.thePlayer.getCurrentEquippedItem())) {
            return true;
        }
        if (baubles == null) {
            return false;
        }
        int size = baubles.getSizeInventory();
        for (int i = 0; i < size; i++) {
            if (isBotaniaManaCarrier(baubles.getStackInSlot(i))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isBotaniaManaCarrier(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        if (item == null) {
            return false;
        }
        if (botaniaIManaUsingItem != null && botaniaIManaUsingItem.isInstance(item)) {
            return true;
        }
        if (botaniaIManaItem != null && botaniaIManaItem.isInstance(item)) {
            return true;
        }
        return botaniaICreativeManaProvider != null && botaniaICreativeManaProvider.isInstance(item);
    }

    private static boolean ensureBotaniaCompat() {
        if (botaniaCompatChecked) {
            return botaniaCompatAvailable;
        }
        botaniaCompatChecked = true;

        try {
            ClassLoader loader = RenderHandler.class.getClassLoader();
            Class<?> hudHandlerClass = Class.forName("vazkii.botania.client.core.handler.HUDHandler", false, loader);
            botaniaHudRenderManaInvBar = hudHandlerClass.getDeclaredMethod(
                    "renderManaInvBar",
                    ScaledResolution.class,
                    boolean.class,
                    int.class,
                    int.class
            );
            botaniaHudRenderManaInvBar.setAccessible(true);
            botaniaHudHandlerInstance = hudHandlerClass.newInstance();

            botaniaIManaUsingItem = Class.forName("vazkii.botania.api.mana.IManaUsingItem", false, loader);
            botaniaUsesManaMethod = botaniaIManaUsingItem.getMethod("usesMana", ItemStack.class);

            botaniaIManaItem = Class.forName("vazkii.botania.api.mana.IManaItem", false, loader);
            botaniaIsNoExportMethod = botaniaIManaItem.getMethod("isNoExport", ItemStack.class);
            botaniaGetManaMethod = botaniaIManaItem.getMethod("getMana", ItemStack.class);
            botaniaGetMaxManaMethod = botaniaIManaItem.getMethod("getMaxMana", ItemStack.class);
            botaniaTwigWandClass = Class.forName("vazkii.botania.common.item.ItemTwigWand", false, loader);

            botaniaICreativeManaProvider = Class.forName("vazkii.botania.api.mana.ICreativeManaProvider", false, loader);
            botaniaIsCreativeMethod = botaniaICreativeManaProvider.getMethod("isCreative", ItemStack.class);

            try {
                Class<?> playerHandlerClass = Class.forName("baubles.common.lib.PlayerHandler", false, loader);
                Class<?> entityPlayerClass = Class.forName("net.minecraft.entity.player.EntityPlayer", false, loader);
                botaniaGetPlayerBaubles = playerHandlerClass.getMethod("getPlayerBaubles", entityPlayerClass);
            } catch (Throwable ignored) {
                botaniaGetPlayerBaubles = null;
            }

            botaniaCompatAvailable = true;
        } catch (Throwable t) {
            botaniaCompatAvailable = false;
        }

        return botaniaCompatAvailable;
    }

    private static IInventory getBaublesInventory(Object player) {
        if (botaniaGetPlayerBaubles == null || player == null) {
            return null;
        }
        try {
            Object inv = botaniaGetPlayerBaubles.invoke(null, player);
            if (inv instanceof IInventory) {
                return (IInventory) inv;
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static void scanBotaniaManaInventory(IInventory inventory, BotaniaManaSnapshot snapshot) {
        if (inventory == null || snapshot == null) {
            return;
        }

        int size = inventory.getSizeInventory();
        for (int i = 0; i < size; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            Item item = stack.getItem();
            if (item == null) {
                continue;
            }

            if (botaniaIManaUsingItem != null && botaniaIManaUsingItem.isInstance(item)) {
                if (invokeBoolean(botaniaUsesManaMethod, item, stack)) {
                    snapshot.hasUsing = true;
                }
            }

            if (botaniaIManaItem != null && botaniaIManaItem.isInstance(item)) {
                if (!invokeBoolean(botaniaIsNoExportMethod, item, stack)) {
                    snapshot.totalMana = safeAdd(snapshot.totalMana, invokeInt(botaniaGetManaMethod, item, stack));
                    snapshot.totalMaxMana = safeAdd(snapshot.totalMaxMana, invokeInt(botaniaGetMaxManaMethod, item, stack));
                }
            }

            if (botaniaICreativeManaProvider != null && botaniaICreativeManaProvider.isInstance(item)) {
                if (invokeBoolean(botaniaIsCreativeMethod, item, stack)) {
                    snapshot.hasCreative = true;
                }
            }
        }
    }

    private static boolean invokeBoolean(Method method, Object target, Object arg) {
        if (method == null || target == null) {
            return false;
        }
        try {
            Object value = method.invoke(target, arg);
            return value instanceof Boolean && ((Boolean) value);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static int invokeInt(Method method, Object target, Object arg) {
        if (method == null || target == null) {
            return 0;
        }
        try {
            Object value = method.invoke(target, arg);
            if (value instanceof Integer) {
                return (Integer) value;
            }
        } catch (Throwable ignored) {
        }
        return 0;
    }

    private static int safeAdd(int current, int delta) {
        if (delta <= 0) {
            return current;
        }
        long sum = (long) current + (long) delta;
        if (sum > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) sum;
    }

    private static final class BotaniaManaSnapshot {
        private boolean hasUsing;
        private boolean hasCreative;
        private int totalMana;
        private int totalMaxMana;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void shiftRendererUp(RenderGameOverlayEvent.Pre event) {
        if (event.type == ElementType.HEALTHMOUNT && !ModConfig.dualHotbarShowMountedHealth) {
            event.setCanceled(true);
            return;
        }

        if (!DualHotbarConfig.enable
                || (!DualHotbarConfig.twoLayerRendering && DualHotbarConfig.numHotbars != 4)) {
            return;
        }

        if (event.type == ElementType.ARMOR || event.type == ElementType.EXPERIENCE
                || event.type == ElementType.FOOD || event.type == ElementType.HEALTH
                || event.type == ElementType.HEALTHMOUNT || event.type == ElementType.JUMPBAR
                || event.type == ElementType.AIR) {
            if (recievedPost == false) {
                GL11.glPopMatrix();
            }

            recievedPost = false;
            GL11.glPushMatrix();

            if (DualHotbarConfig.twoLayerRendering) {
                GL11.glTranslatef(0, -20 * (DualHotbarConfig.numHotbars - 1), 0);
            } else {
                GL11.glTranslatef(0, -20 * (DualHotbarConfig.numHotbars / 2 - 1), 0);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void shiftRendererDown(RenderGameOverlayEvent.Post event) {
        if (event.type == ElementType.HEALTHMOUNT && !ModConfig.dualHotbarShowMountedHealth) {
            return;
        }

        if (!DualHotbarConfig.enable
                || (!DualHotbarConfig.twoLayerRendering && DualHotbarConfig.numHotbars != 4)) {
            return;
        }

        if (event.type == ElementType.ARMOR || event.type == ElementType.EXPERIENCE
                || event.type == ElementType.FOOD || event.type == ElementType.HEALTH
                || event.type == ElementType.HEALTHMOUNT || event.type == ElementType.JUMPBAR
                || event.type == ElementType.AIR) {
            recievedPost = true;
            GL11.glPopMatrix();
        }
    }

    public static void shiftUp() {
        if (!shouldShiftToolHighlight()) {
            toolHighlightShiftPushed = false;
            toolHighlightAttribPushed = false;
            return;
        }

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        toolHighlightAttribPushed = true;

        int shiftY = getDualHotbarTooltipShiftY();
        shiftY += getOverlayAwareTooltipShiftY();
        if (shiftY == 0) {
            toolHighlightShiftPushed = false;
            return;
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, (float) shiftY, 0.0F);
        toolHighlightShiftPushed = true;
    }

    public static void shiftDown() {
        if (!toolHighlightShiftPushed) {
            toolHighlightShiftPushed = false;
        } else {
            GL11.glPopMatrix();
            toolHighlightShiftPushed = false;
        }

        if (toolHighlightAttribPushed) {
            GL11.glPopAttrib();
            toolHighlightAttribPushed = false;
        }
    }

    private static int getDualHotbarTooltipShiftY() {
        if (!DualHotbarConfig.enable
                || (!DualHotbarConfig.twoLayerRendering && DualHotbarConfig.numHotbars != 4)) {
            return 0;
        }
        if (DualHotbarConfig.twoLayerRendering) {
            return -20 * (DualHotbarConfig.numHotbars - 1);
        }
        return -20 * (DualHotbarConfig.numHotbars / 2 - 1);
    }

    private static int getOverlayAwareTooltipShiftY() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null || mc.playerController == null || !mc.playerController.shouldDrawHUD()) {
            return 0;
        }

        EntityPlayer player = mc.thePlayer;
        if (player.capabilities != null && player.capabilities.isCreativeMode) {
            return 0;
        }

        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int screenHeight = res.getScaledHeight();
        int hotbarShiftY = AsgardShieldHud.getDualHotbarShiftY();
        int asgardTopY = AsgardShieldHud.getHudTopY(player, screenHeight);
        boolean airVisible = player.isInsideOfMaterial(Material.water);
        int extraHotbarTooltipTopY = getAdditionalHotbarTooltipTopY(mc, player, screenHeight);
        if (!DualHotbarConfig.heldItemTooltipAboveBars
                && !airVisible
                && asgardTopY == Integer.MIN_VALUE
                && extraHotbarTooltipTopY == Integer.MAX_VALUE) {
            return 0;
        }

        int overlayTopY = Integer.MAX_VALUE;

        if (DualHotbarConfig.heldItemTooltipAboveBars) {
            int healthRows = HudHealthRowHelper.getHealthRows(player);

            int heartsTopY = screenHeight - 39 - (healthRows - 1) * 10 - hotbarShiftY;
            int barsTopY = heartsTopY;
            if (player.getTotalArmorValue() > 0) {
                barsTopY -= 10;
            }
            if (isLegendGearManaBarVisible(player)) {
                barsTopY -= 10;
            }
            overlayTopY = Math.min(overlayTopY, barsTopY);
        }

        if (airVisible && asgardTopY == Integer.MIN_VALUE) {
            int airTopY = screenHeight - 39 - hotbarShiftY;
            if (player.ridingEntity == null) {
                airTopY -= 10;
            }
            airTopY = AsgardShieldHud.getTargetAirBubbleTopY(player, screenHeight, airTopY);
            overlayTopY = Math.min(overlayTopY, airTopY);
        }

        if (asgardTopY != Integer.MIN_VALUE) {
            overlayTopY = Math.min(overlayTopY, asgardTopY);
        }

        if (extraHotbarTooltipTopY != Integer.MAX_VALUE) {
            overlayTopY = Math.min(overlayTopY, extraHotbarTooltipTopY);
        }

        if (overlayTopY == Integer.MAX_VALUE) {
            return 0;
        }

        int padding = DualHotbarConfig.heldItemTooltipAboveBars
                ? Math.max(0, DualHotbarConfig.heldItemTooltipPadding)
                : 0;
        int targetY = overlayTopY - (DualHotbarConfig.heldItemTooltipAboveBars ? 8 : 10) - padding;
        if (AsgardShieldHud.isHudVisible(player)) {
            targetY -= 1;
        }
        // `shiftUp()` already applies the dual-hotbar vertical translation separately.
        // Use that translated baseline here so overlay-aware offsets don't count it twice.
        int defaultY = screenHeight - 59 + getDualHotbarTooltipShiftY();
        return targetY - defaultY;
    }

    public static int getHeldItemTooltipTopY(Minecraft mc, EntityPlayer player) {
        if (mc == null || player == null) {
            return Integer.MIN_VALUE;
        }
        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int screenHeight = res.getScaledHeight();
        return screenHeight - 59 + getDualHotbarTooltipShiftY() + getOverlayAwareTooltipShiftY();
    }

    public static int getCenteredOverlayTextTopY(Minecraft mc, EntityPlayer player) {
        if (mc == null || player == null) {
            return Integer.MIN_VALUE;
        }

        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int topY = res.getScaledHeight() - 72;
        if (isHeldItemTooltipVisible(mc)) {
            int tooltipTopY = getHeldItemTooltipTopY(mc, player);
            if (tooltipTopY != Integer.MIN_VALUE) {
                topY = Math.min(topY, tooltipTopY - 12);
            }
        }
        return topY;
    }

    public static float getCenteredOverlayTranslateY(Minecraft mc, EntityPlayer player, float defaultY) {
        int topY = getCenteredOverlayTextTopY(mc, player);
        if (topY == Integer.MIN_VALUE) {
            return defaultY;
        }
        return (float) topY + 4.0F;
    }

    public static boolean isHeldItemTooltipVisible(Minecraft mc) {
        return getRemainingHighlightTicks(mc) > 0;
    }

    public static int getOverlayMessageTicks(Minecraft mc) {
        if (mc == null || mc.ingameGUI == null) {
            return 0;
        }

        if (!overlayMessageTicksFieldChecked) {
            overlayMessageTicksFieldChecked = true;
            overlayMessageTicksField = findField(mc.ingameGUI.getClass(), "recordPlayingUpFor", "field_73845_h");
        }

        if (overlayMessageTicksField == null) {
            return 0;
        }

        try {
            return Math.max(0, overlayMessageTicksField.getInt(mc.ingameGUI));
        } catch (Exception ignored) {
            return 0;
        }
    }

    private static boolean isLegendGearManaBarVisible(EntityPlayer player) {
        if (player == null || !ModConfig.enableLegendGearModule) {
            return false;
        }
        try {
            boolean forceShow = LegendGearClientState.shouldForceShow(player);
            if (!forceShow && LegendGearClientState.isHoldingIceRodWithLegendGearManaDisabled(player)) {
                return false;
            }

            PlayerStarstatsExtension stats = PlayerStarstatsExtension.get(player);
            if (stats == null) {
                return forceShow;
            }

            int manaMissing = (int) (20.0f - stats.getMana());
            if (manaMissing < 0) {
                manaMissing = 0;
            } else if (manaMissing > 20) {
                manaMissing = 20;
            }
            return manaMissing < 20 || forceShow;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static int getAdditionalHotbarTooltipTopY(Minecraft mc, EntityPlayer player, int screenHeight) {
        int tooltipTopY = Integer.MAX_VALUE;
        if (isOverlayMessageVisible(mc) && !isMountOnboardOverlayVisible(mc, player)) {
            tooltipTopY = Math.min(tooltipTopY, screenHeight - 68);
        }
        if (isBotaniaWandModeDisplayVisible(mc, player)) {
            tooltipTopY = Math.min(tooltipTopY, screenHeight - 70);
        }
        return tooltipTopY;
    }

    private static boolean isOverlayMessageVisible(Minecraft mc) {
        return getOverlayMessageTicks(mc) > 0;
    }

    private static String getOverlayMessageText(Minecraft mc) {
        if (mc == null || mc.ingameGUI == null) {
            return null;
        }

        if (!overlayMessageFieldChecked) {
            overlayMessageFieldChecked = true;
            overlayMessageField = findField(mc.ingameGUI.getClass(), "recordPlaying", "field_73838_g");
        }

        if (overlayMessageField == null) {
            return null;
        }

        try {
            Object value = overlayMessageField.get(mc.ingameGUI);
            return value instanceof String ? (String) value : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    public static boolean isMountOnboardOverlayVisible(Minecraft mc, EntityPlayer player) {
        if (mc == null || player == null || mc.gameSettings == null) {
            return false;
        }

        String overlayMessage = getOverlayMessageText(mc);
        if (overlayMessage == null || overlayMessage.isEmpty()) {
            return false;
        }

        String mountPrompt = I18n.format(
                "mount.onboard",
                GameSettings.getKeyDisplayString(mc.gameSettings.keyBindSneak.getKeyCode())
        );
        return mountPrompt.equals(overlayMessage);
    }

    private static boolean isBotaniaWandModeDisplayVisible(Minecraft mc, EntityPlayer player) {
        if (mc == null || player == null) {
            return false;
        }

        ItemStack held = player.getHeldItem();
        if (held == null || held.getItem() == null) {
            return false;
        }

        if (!ensureBotaniaCompat() || botaniaTwigWandClass == null || !botaniaTwigWandClass.isInstance(held.getItem())) {
            return false;
        }

        return getRemainingHighlightTicks(mc) > 15;
    }

    private static int getDisplaySelectedSlot(int currentItem, InventoryPlayer inventory) {
        if (isBattlegearBattlemode(inventory)) {
            return -1;
        }
        return currentItem;
    }

    private static boolean isBattlegearBattlemode(InventoryPlayer inventory) {
        return inventory != null
                && inventory.currentItem >= BATTLEGEAR_SLOT_MIN
                && inventory.currentItem < BATTLEGEAR_SLOT_MAX;
    }

    private static boolean shouldShiftToolHighlight() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.ingameGUI == null) {
            return false;
        }

        return getRemainingHighlightTicks(mc) > 0;
    }

    private static int getRemainingHighlightTicks(Minecraft mc) {
        if (mc == null || mc.ingameGUI == null) {
            return 0;
        }

        if (!toolHighlightTicksFieldChecked) {
            toolHighlightTicksFieldChecked = true;
            toolHighlightTicksField = findField(mc.ingameGUI.getClass(), "field_92017_k", "remainingHighlightTicks");
        }

        if (toolHighlightTicksField == null) {
            return 0;
        }

        try {
            return toolHighlightTicksField.getInt(mc.ingameGUI);
        } catch (Exception ignored) {
            return 0;
        }
    }

    private static Field findField(Class<?> type, String... names) {
        Class<?> current = type;
        while (current != null) {
            for (String name : names) {
                try {
                    Field f = current.getDeclaredField(name);
                    f.setAccessible(true);
                    return f;
                } catch (Exception ignored) {
                }
            }
            current = current.getSuperclass();
        }
        return null;
    }

    protected void renderInventorySlotItem(RenderItem itemRenderer, int slotIndex, int x, int y, float partialTicks) {
        if (!DualHotbarConfig.enable) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();

        ItemStack itemstack = mc.thePlayer.inventory.mainInventory[slotIndex];

        if (itemstack != null) {
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.enableGUIStandardItemLighting();
            float anim = (float) itemstack.animationsToGo - partialTicks;

            try {
                if (anim > 0.0F) {
                    GL11.glPushMatrix();
                    float scale = 1.0F + anim / 5.0F;
                    GL11.glTranslatef((float) (x + 8), (float) (y + 12), 0.0F);
                    GL11.glScalef(1.0F / scale, (scale + 1.0F) / 2.0F, 1.0F);
                    GL11.glTranslatef((float) (-(x + 8)), (float) (-(y + 12)), 0.0F);
                }

                itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, x, y);

                if (anim > 0.0F) {
                    GL11.glPopMatrix();
                }
            } finally {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glPopAttrib();
            }

        }
    }

    protected void renderInventorySlotOverlay(RenderItem itemRenderer, int slotIndex, int x, int y) {
        if (!DualHotbarConfig.enable) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        ItemStack itemstack = mc.thePlayer.inventory.mainInventory[slotIndex];

        if (itemstack != null) {
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            itemRenderer.zLevel = 200.0F;
            itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, x, y);
            itemRenderer.zLevel = 0.0F;
            GL11.glPopAttrib();
        }
    }

    private void drawSelectorAfterItems(Minecraft mc, int x, int y) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (ModConfig.enableHotbarSelectorTexture) {
            mc.renderEngine.bindTexture(RIFT_SELECTOR);
            drawCustomSizedTexture(x, y + SELECTOR_Y_OFFSET, 0f, 0f, 24, 24, 24, 22, 24f, 24f);
            mc.renderEngine.bindTexture(WIDGITS);
        } else {
            mc.ingameGUI.drawTexturedModalRect(
                    x,
                    y,
                    0,
                    22,
                    24,
                    22
            );
            mc.ingameGUI.drawTexturedModalRect(
                    x,
                    y + 21,
                    0,
                    22,
                    24,
                    1
            );
        }
        GL11.glPopAttrib();
    }

    private void drawCustomSizedTexture(int x, int y, float u, float v, int regionWidth, int regionHeight,
                                        int drawWidth, int drawHeight, float textureWidth, float textureHeight) {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + drawHeight, 0.0D, (u) * f, (v + regionHeight) * f1);
        tessellator.addVertexWithUV(x + drawWidth, y + drawHeight, 0.0D, (u + regionWidth) * f, (v + regionHeight) * f1);
        tessellator.addVertexWithUV(x + drawWidth, y, 0.0D, (u + regionWidth) * f, (v) * f1);
        tessellator.addVertexWithUV(x, y, 0.0D, (u) * f, (v) * f1);
        tessellator.draw();
    }
}
