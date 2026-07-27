package com.voidsrift.riftflux.client.inventory;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

final class InventoryShortcutButton extends GuiButton {
    static final int SIZE = 8;

    enum Shortcut {
        PHOTO_MODE("textures/gui/inventory/photo_mode.png", "gui.riftflux.inventoryShortcut.photoMode"),
        GOKI_STATS("textures/gui/inventory/goki_stats.png", "gui.riftflux.inventoryShortcut.gokiStats"),
        LEVEL_UP("textures/gui/inventory/level_up.png", "gui.riftflux.inventoryShortcut.levelUp"),
        WAYPOINTS("textures/gui/inventory/waypoints.png", "gui.riftflux.inventoryShortcut.waypoints");

        private final ResourceLocation texture;
        final String translationKey;

        Shortcut(String texturePath, String translationKey) {
            this.texture = new ResourceLocation("riftflux", texturePath);
            this.translationKey = translationKey;
        }
    }

    final Shortcut shortcut;

    InventoryShortcutButton(int id, int x, int y, Shortcut shortcut) {
        super(id, x, y, SIZE, SIZE, "");
        this.shortcut = shortcut;
    }

    boolean isHovered(int mouseX, int mouseY) {
        return this.visible
                && mouseX >= this.xPosition
                && mouseY >= this.yPosition
                && mouseX < this.xPosition + this.width
                && mouseY < this.yPosition + this.height;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!this.visible) {
            return;
        }

        boolean hovered = this.isHovered(mouseX, mouseY);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        mc.getTextureManager().bindTexture(this.shortcut.texture);
        this.drawIcon();

        if (hovered) {
            drawRect(
                    this.xPosition,
                    this.yPosition,
                    this.xPosition + this.width,
                    this.yPosition + this.height,
                    0x40FFFFFF
            );
        }
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void drawIcon() {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(this.xPosition, this.yPosition + SIZE, this.zLevel, 0.0D, 1.0D);
        tessellator.addVertexWithUV(this.xPosition + SIZE, this.yPosition + SIZE, this.zLevel, 1.0D, 1.0D);
        tessellator.addVertexWithUV(this.xPosition + SIZE, this.yPosition, this.zLevel, 1.0D, 0.0D);
        tessellator.addVertexWithUV(this.xPosition, this.yPosition, this.zLevel, 0.0D, 0.0D);
        tessellator.draw();
    }

    int getTextX() {
        return this.xPosition + this.getTextOffsetX();
    }

    int getTextY() {
        return this.yPosition + this.getTextOffsetY();
    }

    private int getTextOffsetX() {
        switch (this.shortcut) {
            case PHOTO_MODE:
                return ModConfig.inventoryPhotoModeTextX;
            case GOKI_STATS:
                return ModConfig.inventoryGokiStatsTextX;
            case LEVEL_UP:
                return ModConfig.inventoryLevelUpTextX;
            case WAYPOINTS:
                return ModConfig.inventoryWaypointsTextX;
            default:
                return SIZE / 2;
        }
    }

    private int getTextOffsetY() {
        switch (this.shortcut) {
            case PHOTO_MODE:
                return ModConfig.inventoryPhotoModeTextY;
            case GOKI_STATS:
                return ModConfig.inventoryGokiStatsTextY;
            case LEVEL_UP:
                return ModConfig.inventoryLevelUpTextY;
            case WAYPOINTS:
                return ModConfig.inventoryWaypointsTextY;
            default:
                return SIZE;
        }
    }
}
