package com.voidsrift.riftflux.furniture.client.gui;

import com.voidsrift.riftflux.Constants;
import com.voidsrift.riftflux.furniture.container.ContainerCabinet;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class GuiCabinet extends GuiContainer {
    private static final ResourceLocation GUI_TEXTURE =
            new ResourceLocation(Constants.MODID, "textures/gui/cabinet.png");

    private final IInventory playerInventory;
    private final IInventory storage;

    public GuiCabinet(IInventory playerInventory, IInventory storage) {
        super(new ContainerCabinet(playerInventory, storage));
        this.playerInventory = playerInventory;
        this.storage = storage;
        this.xSize = 176;
        this.ySize = 167;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String storageName = StatCollector.translateToLocal(this.storage.getInventoryName());
        this.fontRendererObj.drawString(
                storageName,
                (this.xSize - this.fontRendererObj.getStringWidth(storageName)) / 2,
                6,
                4210752
        );
        this.fontRendererObj.drawString(
                StatCollector.translateToLocal(this.playerInventory.getInventoryName()),
                8,
                this.ySize - 94,
                4210752
        );
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
        int left = (this.width - this.xSize) / 2;
        int top = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(left, top, 0, 0, this.xSize, this.ySize);
    }
}
