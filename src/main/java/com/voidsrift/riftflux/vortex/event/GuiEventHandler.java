package com.voidsrift.riftflux.vortex.event;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.client.ClientRespawnDelayState;
import com.voidsrift.riftflux.client.GuiDeathOverlayChat;
import com.voidsrift.riftflux.mixin.accessor.GuiScreenAccessor;
import com.voidsrift.riftflux.mixin.accessor.GuiYesNoAccessor;
import com.voidsrift.riftflux.vortex.client.gui.GuiRuneGameOver;
import com.voidsrift.riftflux.vortex.lib.helper.WorldHelper;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post;

import java.util.List;

public class GuiEventHandler {
    private static GuiEventHandler primaryInstance;

    private static boolean isPrimary(GuiEventHandler instance) {
        if (primaryInstance == null) {
            primaryInstance = instance;
        }
        return primaryInstance == instance;
    }

    private static boolean isRespawnScreen(GuiScreen gui) {
        return gui instanceof GuiGameOver || gui instanceof GuiRuneGameOver;
    }

    private static GuiScreen getRespawnBaseScreen(GuiScreen gui) {
        if (isRespawnScreen(gui) || isDeathConfirmScreen(gui)) {
            return gui;
        }
        if (gui instanceof GuiDeathOverlayChat) {
            GuiScreen background = ((GuiDeathOverlayChat) gui).getBackgroundScreen();
            if (isRespawnScreen(background)) {
                return background;
            }
        }
        return null;
    }

    private static boolean isDeathConfirmScreen(GuiScreen gui) {
        return getDeathConfirmParent(gui) != null;
    }

    private static GuiScreen getDeathConfirmParent(GuiScreen gui) {
        if (!(gui instanceof GuiYesNo)) {
            return null;
        }
        GuiYesNoAccessor accessor = (GuiYesNoAccessor) gui;
        if (!(accessor.riftflux$getParentScreen() instanceof GuiScreen)) {
            return null;
        }
        GuiScreen parent = (GuiScreen) accessor.riftflux$getParentScreen();
        return isRespawnScreen(parent) ? parent : null;
    }

    private static int getLockedRespawnButtonId(GuiScreen gui) {
        if (gui instanceof GuiDeathOverlayChat) {
            GuiScreen background = ((GuiDeathOverlayChat) gui).getBackgroundScreen();
            return isRespawnScreen(background) ? 0 : -1;
        }
        if (isRespawnScreen(gui)) {
            return 0;
        }
        if (isDeathConfirmScreen(gui)) {
            return 1;
        }
        return -1;
    }

    private static long getRemainingRespawnMs() {
        return ClientRespawnDelayState.getRemainingMs();
    }

    private static String translateOrDefault(String key, String fallback) {
        String translated = StatCollector.translateToLocal(key);
        if (translated == null || translated.isEmpty() || key.equals(translated)) {
            return fallback;
        }
        return translated;
    }

    private static void applyRespawnLockToButtons(GuiScreen gui) {
        int lockedButtonId = getLockedRespawnButtonId(gui);
        if (lockedButtonId < 0) {
            return;
        }
        List buttonList = ((GuiScreenAccessor) gui).getButtonList();
        if (buttonList == null) {
            return;
        }
        boolean locked = getRemainingRespawnMs() > 0L;
        for (Object obj : buttonList) {
            if (!(obj instanceof GuiButton)) {
                continue;
            }
            GuiButton button = (GuiButton) obj;
            if (button.id == lockedButtonId) {
                button.enabled = !locked;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (!isPrimary(this)) {
            return;
        }
        ClientRespawnDelayState.cleanupLegacyFiles();

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc == null ? null : mc.thePlayer;
        if (event.gui instanceof GuiGameOver
                && mc != null
                && mc.theWorld != null
                && mc.theWorld.getWorldInfo().isHardcoreModeEnabled()
                && WorldHelper.canPlayerHCRevive(player)) {
            event.gui = new GuiRuneGameOver();
        }

        if (isRespawnScreen(event.gui)
                && player != null
                && !player.isEntityAlive()
                && ClientRespawnDelayState.getRemainingMs() <= 0L
                && ModConfig.deathRespawnDelaySeconds > 0) {
            ClientRespawnDelayState.applyRemainingMs(Math.max(0L, ModConfig.deathRespawnDelaySeconds) * 1000L);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onInitGuiPostLowest(Post event) {
        if (!isPrimary(this)) {
            return;
        }
        applyRespawnLockToButtons(event.gui);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onActionPerformedPostLowest(GuiScreenEvent.ActionPerformedEvent.Post event) {
        if (!isPrimary(this)) {
            return;
        }
        applyRespawnLockToButtons(event.gui);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onActionPerformedPreHighest(GuiScreenEvent.ActionPerformedEvent.Pre event) {
        if (!isPrimary(this) || event.button == null) {
            return;
        }
        int lockedButtonId = getLockedRespawnButtonId(event.gui);
        if (lockedButtonId >= 0 && event.button.id == lockedButtonId && getRemainingRespawnMs() > 0L) {
            event.setCanceled(true);
            applyRespawnLockToButtons(event.gui);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!isPrimary(this) || event.phase != TickEvent.Phase.END) {
            return;
        }

        ClientRespawnDelayState.cleanupLegacyFiles();
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.thePlayer != null && mc.theWorld != null && mc.thePlayer.isEntityAlive()) {
            ClientRespawnDelayState.clear();
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDrawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!isPrimary(this)) {
            return;
        }

        GuiScreen respawnBase = getRespawnBaseScreen(event.gui);
        if (respawnBase == null) {
            return;
        }

        applyRespawnLockToButtons(respawnBase);
        long remainingMs = getRemainingRespawnMs();
        if (remainingMs <= 0L) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.fontRenderer == null) {
            return;
        }

        int seconds = (int) Math.ceil(remainingMs / 1000.0D);
        String suffix = translateOrDefault("gui.riftflux.respawn_delay_suffix", "s");
        String text;
        try {
            text = StatCollector.translateToLocalFormatted("gui.riftflux.respawn_delay", Integer.valueOf(seconds), suffix);
        } catch (Exception ignored) {
            text = "Respawn available in " + seconds + suffix;
        }

        int textWidth = mc.fontRenderer.getStringWidth(text);
        int x = (event.gui.width - textWidth) / 2;
        int y = event.gui.height / 2;

        List buttonList = ((GuiScreenAccessor) respawnBase).getButtonList();
        if (buttonList != null) {
            int lockedButtonId = getLockedRespawnButtonId(respawnBase);
            for (Object obj : buttonList) {
                if (!(obj instanceof GuiButton)) {
                    continue;
                }
                GuiButton button = (GuiButton) obj;
                if (button.id == lockedButtonId) {
                    x = button.xPosition + (button.width - textWidth) / 2;
                    y = button.yPosition - mc.fontRenderer.FONT_HEIGHT - 2;
                    break;
                }
            }
        }

        mc.fontRenderer.drawStringWithShadow(text, x, y, 0xA0A0A0);
    }
}
