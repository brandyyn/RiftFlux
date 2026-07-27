package com.voidsrift.riftflux.client.inventory;

import assets.levelup.GuiClasses;
import assets.levelup.GuiSkills;
import assets.levelup.LevelUpHUD;
import assets.levelup.LevelUpTuning;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import com.voidsrift.riftflux.gokistats.GokiStatsContent;
import com.voidsrift.riftflux.levelup.LevelUpContent;
import com.voidsrift.riftflux.mixin.accessor.GuiScreenAccessor;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import goki.stats.client.gui.GuiStats;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

public final class InventoryShortcutHandler {
    private static final File CONFIG_FILE = new File("config/riftflux.cfg");
    private static final InventoryShortcutHandler INSTANCE = new InventoryShortcutHandler();
    private static final int PHOTO_MODE_BUTTON_ID = 0x524600;
    private static final int GOKI_STATS_BUTTON_ID = 0x524601;
    private static final int LEVEL_UP_BUTTON_ID = 0x524602;
    private static final int WAYPOINTS_BUTTON_ID = 0x524603;
    private static boolean bootstrapped;
    private long lastConfigCheckMillis;
    private long lastConfigModified = CONFIG_FILE.lastModified();

    private InventoryShortcutHandler() {
    }

    public static void bootstrap() {
        if (bootstrapped) {
            return;
        }
        bootstrapped = true;
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        FMLCommonHandler.instance().bus().register(INSTANCE);
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!(event.gui instanceof GuiInventory)) {
            return;
        }

        syncButtons(event.gui, event.buttonList);
    }

    @SubscribeEvent
    public void onDrawScreenPre(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if (event.gui instanceof GuiInventory) {
            syncButtons(event.gui, ((GuiScreenAccessor) event.gui).getButtonList());
        }
    }

    @SubscribeEvent
    public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!(event.gui instanceof GuiInventory)) {
            return;
        }

        List buttonList = ((GuiScreenAccessor) event.gui).getButtonList();
        for (Object object : buttonList) {
            if (!(object instanceof InventoryShortcutButton)) {
                continue;
            }

            InventoryShortcutButton button = (InventoryShortcutButton) object;
            if (!button.isHovered(event.mouseX, event.mouseY)) {
                continue;
            }

            String text = StatCollector.translateToLocal(button.shortcut.translationKey);
            drawForegroundLabel(button, text);
            return;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !ModConfig.inventoryShortcutsHotSwap) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now - this.lastConfigCheckMillis < 1000L) {
            return;
        }
        this.lastConfigCheckMillis = now;

        if (!CONFIG_FILE.isFile()) {
            return;
        }
        long modified = CONFIG_FILE.lastModified();
        if (modified != this.lastConfigModified && ModConfig.reload()) {
            LevelUpTuning.restoreServerValues();
            this.lastConfigModified = CONFIG_FILE.lastModified();
        }
    }

    @SubscribeEvent
    public void onButtonPressed(GuiScreenEvent.ActionPerformedEvent.Post event) {
        if (!(event.button instanceof InventoryShortcutButton)) {
            return;
        }

        InventoryShortcutButton button = (InventoryShortcutButton) event.button;
        switch (button.shortcut) {
            case PHOTO_MODE:
                openPhotoMode();
                break;
            case GOKI_STATS:
                openGokiStats();
                break;
            case LEVEL_UP:
                openLevelUp(event.gui);
                break;
            case WAYPOINTS:
                XaeroWaypointsShortcut.open(event.gui);
                break;
            default:
                break;
        }
    }

    private static void openPhotoMode() {
        Minecraft mc = Minecraft.getMinecraft();
        mc.displayGuiScreen(null);
        IsometricPhotoModeController.instance().toggle();
    }

    private static void openGokiStats() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            mc.displayGuiScreen(new GuiStats(mc.thePlayer));
        }
    }

    private static void openLevelUp(GuiScreen parentScreen) {
        GuiScreen screen = null;
        if (LevelUpHUD.canShowSkills()) {
            screen = new GuiSkills(parentScreen);
        } else if (LevelUpHUD.canOpenClassSkillsMenu()) {
            screen = new GuiClasses(parentScreen);
        }

        if (screen != null) {
            Minecraft.getMinecraft().displayGuiScreen(screen);
        }
    }

    private static void drawForegroundLabel(InventoryShortcutButton button, String text) {
        Minecraft mc = Minecraft.getMinecraft();
        int x = button.getTextX() - mc.fontRenderer.getStringWidth(text) / 2;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.fontRenderer.drawStringWithShadow(text, x, button.getTextY(), 0xFFFFFF);
        } finally {
            GL11.glPopAttrib();
        }
    }

    private static void syncButtons(GuiScreen gui, List buttonList) {
        syncButton(
                buttonList,
                PHOTO_MODE_BUTTON_ID,
                gui.width / 2 + ModConfig.inventoryPhotoModeIconX,
                gui.height / 2 + ModConfig.inventoryPhotoModeIconY,
                ModConfig.inventoryPhotoModeIconEnabled && ModConfig.enableIsometricPhotoMode,
                InventoryShortcutButton.Shortcut.PHOTO_MODE
        );
        syncButton(
                buttonList,
                GOKI_STATS_BUTTON_ID,
                gui.width / 2 + ModConfig.inventoryGokiStatsIconX,
                gui.height / 2 + ModConfig.inventoryGokiStatsIconY,
                ModConfig.inventoryGokiStatsIconEnabled && GokiStatsContent.isEnabled(),
                InventoryShortcutButton.Shortcut.GOKI_STATS
        );
        syncButton(
                buttonList,
                LEVEL_UP_BUTTON_ID,
                gui.width / 2 + ModConfig.inventoryLevelUpIconX,
                gui.height / 2 + ModConfig.inventoryLevelUpIconY,
                ModConfig.inventoryLevelUpIconEnabled && LevelUpContent.isEnabled(),
                InventoryShortcutButton.Shortcut.LEVEL_UP
        );
        syncButton(
                buttonList,
                WAYPOINTS_BUTTON_ID,
                gui.width / 2 + ModConfig.inventoryWaypointsIconX,
                gui.height / 2 + ModConfig.inventoryWaypointsIconY,
                ModConfig.inventoryWaypointsIconEnabled && Loader.isModLoaded("XaeroMinimap"),
                InventoryShortcutButton.Shortcut.WAYPOINTS
        );
    }

    private static void syncButton(
            List buttonList,
            int id,
            int x,
            int y,
            boolean shouldExist,
            InventoryShortcutButton.Shortcut shortcut
    ) {
        InventoryShortcutButton existing = null;
        Iterator iterator = buttonList.iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            if (object instanceof InventoryShortcutButton && ((InventoryShortcutButton) object).id == id) {
                if (!shouldExist || existing != null) {
                    iterator.remove();
                } else {
                    existing = (InventoryShortcutButton) object;
                }
            }
        }

        if (!shouldExist) {
            return;
        }
        if (existing == null) {
            buttonList.add(new InventoryShortcutButton(id, x, y, shortcut));
        } else {
            existing.xPosition = x;
            existing.yPosition = y;
        }
    }
}
