package com.voidsrift.riftflux.chester.client;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.chester.ContainerChester;
import com.voidsrift.riftflux.chester.EntityChester;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiChester extends GuiContainer {
    private static final ResourceLocation CHEST_GUI =
            new ResourceLocation("textures/gui/container/generic_54.png");

    private final EntityChester chester;
    private final int inventoryRows;

    public GuiChester(IInventory playerInventory, IInventory chesterInventory, EntityChester chester) {
        super(new ContainerChester(playerInventory, chesterInventory, chester));
        this.chester = chester;
        this.inventoryRows = chester.getInventoryRows();
        this.ySize = 114 + inventoryRows * 18;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        int labelColor = getLabelColor();
        String chesterNameKey = chester.getChesterType() == EntityChester.TYPE_SHADOW
                ? "container.shadow_chester"
                : "container.chester";
        fontRendererObj.drawString(I18n.format(chesterNameKey), 8, 6, labelColor);
        fontRendererObj.drawString(
                I18n.format(mc.thePlayer.inventory.getInventoryName()),
                8,
                ySize - 96 + 2,
                labelColor
        );
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        boolean shadow = chester.getChesterType() == EntityChester.TYPE_SHADOW;
        applyInventoryTint(shadow
                ? ModConfig.shadowChesterInventoryColor
                : ModConfig.chesterInventoryColor);
        mc.getTextureManager().bindTexture(CHEST_GUI);
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        int inventoryHeight = inventoryRows * 18 + 17;
        drawTexturedModalRect(left, top, 0, 0, xSize, inventoryHeight);
        drawTexturedModalRect(
                left,
                top + inventoryHeight,
                0,
                126,
                xSize,
                96
        );
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void applyInventoryTint(String configuredColor) {
        Integer color = ModConfig.parseRgbColorOrNull(configuredColor);
        if (color == null) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            return;
        }
        GL11.glColor4f(
                ((color >> 16) & 255) / 255.0F,
                ((color >> 8) & 255) / 255.0F,
                (color & 255) / 255.0F,
                1.0F
        );
    }

    private int getLabelColor() {
        String configuredColor = chester.getChesterType() == EntityChester.TYPE_SHADOW
                ? ModConfig.shadowChesterInventoryColor
                : ModConfig.chesterInventoryColor;
        return ModConfig.parseRgbColorOrNull(configuredColor) == null ? 0x404040 : 0xE0E0E0;
    }

}
