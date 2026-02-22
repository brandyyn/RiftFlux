package com.voidsrift.riftflux.painting;

import com.voidsrift.riftflux.net.MsgSetPaintingSelection;
import com.voidsrift.riftflux.net.RFNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GuiPaintingSelector extends GuiScreen {

    private static final ResourceLocation VANILLA_PAINTINGS =
            new ResourceLocation("textures/painting/paintings_kristoffer_zetterstrand.png");

    private static final int GRID_COLS = 4;
    private static final int CELL_W = 144;
    private static final int CELL_H = 78;
    private static final int CELL_GAP = 8;
    private static final int GRID_TOP = 54;
    private static final int SCROLL_STEP = 28;
    private static final int SCROLLBAR_W = 8;
    private static final int SCROLLBAR_MARGIN = 8;
    private static final int THUMB_MIN_H = 20;

    private final List<EntityPainting.EnumArt> arts = new ArrayList<EntityPainting.EnumArt>();

    private int scrollY;
    private String selectedMotive;
    private boolean draggingScrollbar;
    private int scrollbarDragOffset;

    private GuiButton buttonRandom;
    private GuiButton buttonDone;

    public GuiPaintingSelector() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        this.selectedMotive = PaintingSelectionData.getSelectedMotive(player);
    }

    @Override
    public void initGui() {
        this.arts.clear();
        EntityPainting.EnumArt[] values = EntityPainting.EnumArt.values();
        for (int i = 0; i < values.length; i++) {
            this.arts.add(values[i]);
        }
        Collections.sort(this.arts, new Comparator<EntityPainting.EnumArt>() {
            @Override
            public int compare(EntityPainting.EnumArt a, EntityPainting.EnumArt b) {
                int areaA = a.sizeX * a.sizeY;
                int areaB = b.sizeX * b.sizeY;
                if (areaA != areaB) {
                    return areaA - areaB;
                }
                if (a.sizeY != b.sizeY) {
                    return a.sizeY - b.sizeY;
                }
                if (a.sizeX != b.sizeX) {
                    return a.sizeX - b.sizeX;
                }
                return a.title.compareToIgnoreCase(b.title);
            }
        });

        int buttonY = this.height - 26;
        int centerX = this.width / 2;

        this.buttonList.clear();
        this.buttonRandom = new GuiButton(3, centerX - 136, buttonY, 120, 20, "Use Random");
        this.buttonDone = new GuiButton(0, centerX + 20, buttonY, 120, 20, "Done");

        this.buttonList.add(this.buttonRandom);
        this.buttonList.add(this.buttonDone);

        clampScroll();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == null || !button.enabled) {
            return;
        }
        if (button.id == 0) {
            this.mc.displayGuiScreen((GuiScreen) null);
            return;
        }
        if (button.id == 3) {
            applySelection(null);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (button != 0) {
            return;
        }

        // Buttons must always take priority over painting cards.
        for (int i = 0; i < this.buttonList.size(); i++) {
            GuiButton guiButton = (GuiButton) this.buttonList.get(i);
            if (guiButton != null
                    && guiButton.visible
                    && mouseX >= guiButton.xPosition
                    && mouseX < guiButton.xPosition + guiButton.width
                    && mouseY >= guiButton.yPosition
                    && mouseY < guiButton.yPosition + guiButton.height) {
                super.mouseClicked(mouseX, mouseY, button);
                return;
            }
        }

        if (handleScrollbarClick(mouseX, mouseY)) {
            return;
        }

        int index = getIndexAt(mouseX, mouseY);
        if (index < 0 || index >= this.arts.size()) {
            return;
        }

        EntityPainting.EnumArt art = this.arts.get(index);
        applySelection(art.title);
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel > 0) {
            this.scrollY -= SCROLL_STEP;
            clampScroll();
        } else if (wheel < 0) {
            this.scrollY += SCROLL_STEP;
            clampScroll();
        }
    }

    @Override
    protected void keyTyped(char c, int keyCode) {
        super.keyTyped(c, keyCode);
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        super.mouseMovedOrUp(mouseX, mouseY, button);
        if (button == 0) {
            this.draggingScrollbar = false;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        if (!this.draggingScrollbar || clickedMouseButton != 0) {
            return;
        }
        applyScrollbarThumbY(mouseY - this.scrollbarDragOffset);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        drawCenteredString(this.fontRendererObj, "Painting Selector", this.width / 2, 12, 0xFFFFFF);
        String selectedText = this.selectedMotive == null ? "Selected: Random" : "Selected: " + this.selectedMotive;
        drawCenteredString(this.fontRendererObj, selectedText, this.width / 2, 24, 0xCFCFCF);
        drawCenteredString(this.fontRendererObj, "Scroll to browse all paintings", this.width / 2, 34, 0xA8A8A8);

        int gridWidth = GRID_COLS * CELL_W + (GRID_COLS - 1) * CELL_GAP;
        int gridLeft = (this.width - gridWidth) / 2;
        int viewportTop = getViewportTop();
        int viewportBottom = getViewportBottom();

        clampScroll();

        int hoveredIndex = -1;
        int gridRight = gridLeft + gridWidth;

        beginGridScissor(gridLeft, viewportTop, gridRight, viewportBottom);
        for (int idx = 0; idx < this.arts.size(); idx++) {
            int col = idx % GRID_COLS;
            int row = idx / GRID_COLS;

            int x = gridLeft + col * (CELL_W + CELL_GAP);
            int y = GRID_TOP + row * (CELL_H + CELL_GAP) - this.scrollY;

            if (y + CELL_H < viewportTop || y > viewportBottom) {
                continue;
            }

            EntityPainting.EnumArt art = this.arts.get(idx);

            boolean hovered = mouseY >= viewportTop
                    && mouseY < viewportBottom
                    && mouseX >= x
                    && mouseX < x + CELL_W
                    && mouseY >= y
                    && mouseY < y + CELL_H;
            if (hovered) {
                hoveredIndex = idx;
            }

            boolean selected = art.title.equals(this.selectedMotive);
            int fillColor;
            if (selected) {
                fillColor = 0xAA2E6630;
            } else if (hovered) {
                fillColor = 0xAA3F3F3F;
            } else {
                fillColor = 0xAA1D1D1D;
            }

            drawRect(x, y, x + CELL_W, y + CELL_H, fillColor);

            int borderColor;
            if (selected) {
                borderColor = 0xFFE0FFB0;
            } else if (hovered) {
                borderColor = 0xFFFFFFFF;
            } else {
                borderColor = 0xFF5A5A5A;
            }
            drawCellBorder(x, y, CELL_W, CELL_H, borderColor);

            int previewX = x + 4;
            int previewY = y + 4;
            int previewW = CELL_W - 8;
            int previewH = CELL_H - 20;
            drawPaintingPreview(art, previewX, previewY, previewW, previewH);

            String title = this.fontRendererObj.trimStringToWidth(art.title, CELL_W - 8);
            drawCenteredString(this.fontRendererObj, title, x + CELL_W / 2, y + CELL_H - 12, 0xFFFFFF);
        }
        endGridScissor();

        drawScrollbar();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (hoveredIndex >= 0 && hoveredIndex < this.arts.size()) {
            EntityPainting.EnumArt hovered = this.arts.get(hoveredIndex);
            List<String> lines = new ArrayList<String>();
            lines.add(hovered.title);
            lines.add(hovered.sizeX + "x" + hovered.sizeY);
            this.func_146283_a(lines, mouseX, mouseY);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void applySelection(String motive) {
        this.selectedMotive = motive;

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        PaintingSelectionData.setSelectedMotive(player, motive);

        if (RFNetwork.CH != null) {
            RFNetwork.CH.sendToServer(new MsgSetPaintingSelection(motive));
        }
    }

    private int getIndexAt(int mouseX, int mouseY) {
        int gridWidth = GRID_COLS * CELL_W + (GRID_COLS - 1) * CELL_GAP;
        int gridLeft = (this.width - gridWidth) / 2;
        int viewportTop = getViewportTop();
        int viewportBottom = getViewportBottom();
        if (mouseY < viewportTop || mouseY >= viewportBottom) {
            return -1;
        }

        for (int index = 0; index < this.arts.size(); index++) {
            int col = index % GRID_COLS;
            int row = index / GRID_COLS;
            int x = gridLeft + col * (CELL_W + CELL_GAP);
            int y = GRID_TOP + row * (CELL_H + CELL_GAP) - this.scrollY;
            if (y + CELL_H < viewportTop || y > viewportBottom) {
                continue;
            }
            if (mouseX >= x && mouseX < x + CELL_W && mouseY >= y && mouseY < y + CELL_H) {
                return index;
            }
        }
        return -1;
    }

    private boolean handleScrollbarClick(int mouseX, int mouseY) {
        int scrollbarX = getScrollbarX();
        int top = getViewportTop();
        int bottom = getViewportBottom();
        int trackHeight = Math.max(1, bottom - top);

        if (mouseX < scrollbarX || mouseX >= scrollbarX + SCROLLBAR_W || mouseY < top || mouseY >= bottom) {
            return false;
        }

        if (getMaxScroll() <= 0) {
            return true;
        }

        int thumbHeight = getScrollbarThumbHeight(trackHeight);
        int thumbY = getScrollbarThumbY(top, trackHeight, thumbHeight);

        if (mouseY >= thumbY && mouseY < thumbY + thumbHeight) {
            this.draggingScrollbar = true;
            this.scrollbarDragOffset = mouseY - thumbY;
            return true;
        }

        this.draggingScrollbar = true;
        this.scrollbarDragOffset = thumbHeight / 2;
        applyScrollbarThumbY(mouseY - this.scrollbarDragOffset);
        return true;
    }

    private void clampScroll() {
        int max = getMaxScroll();
        if (this.scrollY < 0) {
            this.scrollY = 0;
        } else if (this.scrollY > max) {
            this.scrollY = max;
        }
    }

    private int getMaxScroll() {
        int rows = (this.arts.size() + GRID_COLS - 1) / GRID_COLS;
        int contentHeight = rows <= 0 ? 0 : (rows * (CELL_H + CELL_GAP) - CELL_GAP);
        int viewportHeight = Math.max(1, getViewportBottom() - getViewportTop());
        return Math.max(0, contentHeight - viewportHeight);
    }

    private int getViewportTop() {
        return GRID_TOP;
    }

    private int getViewportBottom() {
        return this.height - 50;
    }

    private int getScrollbarX() {
        int gridWidth = GRID_COLS * CELL_W + (GRID_COLS - 1) * CELL_GAP;
        int gridLeft = (this.width - gridWidth) / 2;
        return gridLeft + gridWidth + SCROLLBAR_MARGIN;
    }

    private int getScrollbarThumbHeight(int trackHeight) {
        int rows = (this.arts.size() + GRID_COLS - 1) / GRID_COLS;
        int contentHeight = rows <= 0 ? 0 : (rows * (CELL_H + CELL_GAP) - CELL_GAP);
        if (contentHeight <= 0) {
            return trackHeight;
        }
        int thumb = (int) ((double) trackHeight * ((double) trackHeight / (double) contentHeight));
        if (thumb < THUMB_MIN_H) {
            thumb = THUMB_MIN_H;
        }
        if (thumb > trackHeight) {
            thumb = trackHeight;
        }
        return thumb;
    }

    private int getScrollbarThumbY(int top, int trackHeight, int thumbHeight) {
        int maxScroll = getMaxScroll();
        if (maxScroll <= 0) {
            return top;
        }
        int movable = Math.max(1, trackHeight - thumbHeight);
        double ratio = (double) this.scrollY / (double) maxScroll;
        return top + (int) Math.round(ratio * (double) movable);
    }

    private void applyScrollbarThumbY(int thumbY) {
        int top = getViewportTop();
        int bottom = getViewportBottom();
        int trackHeight = Math.max(1, bottom - top);
        int thumbHeight = getScrollbarThumbHeight(trackHeight);
        int movable = Math.max(1, trackHeight - thumbHeight);

        int clampedThumbY = thumbY;
        if (clampedThumbY < top) {
            clampedThumbY = top;
        }
        int maxThumbY = top + movable;
        if (clampedThumbY > maxThumbY) {
            clampedThumbY = maxThumbY;
        }

        int maxScroll = getMaxScroll();
        if (maxScroll <= 0) {
            this.scrollY = 0;
            return;
        }

        double ratio = (double) (clampedThumbY - top) / (double) movable;
        this.scrollY = (int) Math.round(ratio * (double) maxScroll);
        clampScroll();
    }

    private void drawScrollbar() {
        int top = getViewportTop();
        int bottom = getViewportBottom();
        int trackHeight = Math.max(1, bottom - top);
        int x = getScrollbarX();

        drawRect(x, top, x + SCROLLBAR_W, bottom, 0x99202020);
        drawCellBorder(x, top, SCROLLBAR_W, trackHeight, 0xFF5A5A5A);

        int thumbHeight = getScrollbarThumbHeight(trackHeight);
        int thumbY = getScrollbarThumbY(top, trackHeight, thumbHeight);

        drawRect(x + 1, thumbY + 1, x + SCROLLBAR_W - 1, thumbY + thumbHeight - 1, 0xCCB0B0B0);
        drawCellBorder(x, thumbY, SCROLLBAR_W, thumbHeight, 0xFFE0E0E0);
    }

    private void beginGridScissor(int left, int top, int right, int bottom) {
        int clampedLeft = Math.max(0, left);
        int clampedTop = Math.max(0, top);
        int clampedRight = Math.min(this.width, right);
        int clampedBottom = Math.min(this.height, bottom);
        if (clampedRight <= clampedLeft || clampedBottom <= clampedTop) {
            return;
        }

        ScaledResolution scaled = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        int scaleFactor = scaled.getScaleFactor();

        int scissorX = clampedLeft * scaleFactor;
        int scissorY = this.mc.displayHeight - clampedBottom * scaleFactor;
        int scissorW = (clampedRight - clampedLeft) * scaleFactor;
        int scissorH = (clampedBottom - clampedTop) * scaleFactor;

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(scissorX, scissorY, scissorW, scissorH);
    }

    private void endGridScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private void drawCellBorder(int x, int y, int w, int h, int color) {
        drawHorizontalLine(x, x + w - 1, y, color);
        drawHorizontalLine(x, x + w - 1, y + h - 1, color);
        drawVerticalLine(x, y, y + h - 1, color);
        drawVerticalLine(x + w - 1, y, y + h - 1, color);
    }

    private void drawPaintingPreview(EntityPainting.EnumArt art, int x, int y, int width, int height) {
        ResourceLocation texture = CustomPaintingRegistry.getCustomTexture(art);
        boolean fullTexture = texture != null && CustomPaintingRegistry.usesFullTexture(art);
        if (texture == null) {
            texture = VANILLA_PAINTINGS;
        }
        this.mc.getTextureManager().bindTexture(texture);

        float scaleX = (float) width / (float) art.sizeX;
        float scaleY = (float) height / (float) art.sizeY;
        float scale = Math.min(scaleX, scaleY);

        int drawW = Math.max(1, Math.round(art.sizeX * scale));
        int drawH = Math.max(1, Math.round(art.sizeY * scale));
        int drawX = x + (width - drawW) / 2;
        int drawY = y + (height - drawH) / 2;

        float u0;
        float v0;
        float u1;
        float v1;
        if (fullTexture) {
            u0 = 0.0F;
            v0 = 0.0F;
            u1 = 1.0F;
            v1 = 1.0F;
        } else {
            u0 = art.offsetX / 256.0F;
            v0 = art.offsetY / 256.0F;
            u1 = (art.offsetX + art.sizeX) / 256.0F;
            v1 = (art.offsetY + art.sizeY) / 256.0F;
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double) drawX, (double) (drawY + drawH), this.zLevel, (double) u0, (double) v1);
        tessellator.addVertexWithUV((double) (drawX + drawW), (double) (drawY + drawH), this.zLevel, (double) u1, (double) v1);
        tessellator.addVertexWithUV((double) (drawX + drawW), (double) drawY, this.zLevel, (double) u1, (double) v0);
        tessellator.addVertexWithUV((double) drawX, (double) drawY, this.zLevel, (double) u0, (double) v0);
        tessellator.draw();
    }
}
