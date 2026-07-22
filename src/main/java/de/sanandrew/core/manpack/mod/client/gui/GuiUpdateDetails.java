/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Mouse
 *  org.lwjgl.opengl.GL11
 */
package de.sanandrew.core.manpack.mod.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.managers.SAPUpdateManager;
import de.sanandrew.core.manpack.util.client.helpers.GuiUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class GuiUpdateDetails
extends GuiScreen {
    private static final ResourceLocation TEXTURE = new ResourceLocation("sapmanpack", "textures/gui/updater/details_list.png");
    private final GuiScreen updateList;
    private final SAPUpdateManager manager;
    private GuiButton backToList;
    private float scrollAmount = 0.0f;
    private float scrollMax = 0.0f;
    private boolean isScrolling = false;

    public GuiUpdateDetails(GuiScreen updateListGui, SAPUpdateManager mgr) {
        this.updateList = updateListGui;
        this.manager = mgr;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.backToList = new GuiButton(this.buttonList.size(), (this.width - 200) / 2, (this.height - 240) / 2 + 215, "Back");
        this.buttonList.add(this.backToList);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partTicks) {
        int xPos = (this.width - 276) / 2;
        int yPos = (this.height - 240) / 2;
        int listX = 16;
        int listY = 60;
        int listWidth = 244;
        int listHeight = 150;
        int listTextY = 5;
        boolean isLeftMBDown = Mouse.isButtonDown((int)0);
        int scrollX = xPos + listX + listWidth - 4;
        int scrollY = yPos + listY;
        if (!this.isScrolling && isLeftMBDown && mouseX >= scrollX && mouseY >= scrollY && mouseX < scrollX + 4 && mouseY < scrollY + listHeight) {
            boolean bl = this.isScrolling = this.scrollMax > 0.0f;
        }
        if (!isLeftMBDown) {
            this.isScrolling = false;
        }
        if (this.isScrolling) {
            this.scrollAmount = Math.min(this.scrollMax, Math.max(0.0f, (float)(mouseY - scrollY - 5) / ((float)listHeight - 10.0f)) * this.scrollMax);
        }
        this.drawDefaultBackground();
        GL11.glEnable((int)3042);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        Gui.drawRect(xPos, 0, xPos + 276, this.height, Integer.MIN_VALUE);
        GuiUtils.drawGradientRect(xPos, 0, xPos + 5, this.height, -16777216, 0, this.zLevel);
        GuiUtils.drawGradientRect(xPos + 271, 0, xPos + 276, this.height, 0, -16777216, this.zLevel);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)16.0f, (float)4.0f, (float)0.0f);
        GL11.glScalef((float)1.5f, (float)1.5f, (float)1.0f);
        GL11.glTranslatef((float)((float)xPos / 1.5f - (float)xPos), (float)((float)yPos / 1.5f - (float)yPos), (float)0.0f);
        this.fontRendererObj.drawString(this.manager.getModName(), xPos, yPos, -1);
        GL11.glPopMatrix();
        String s = "Currently installed version";
        this.fontRendererObj.drawString(s, xPos + 16, yPos + 22, -6250336);
        Gui.drawRect(xPos + this.fontRendererObj.getStringWidth(s) + 18, yPos + 26, xPos + 160, yPos + 27, -10461088);
        s = "Updated version";
        this.fontRendererObj.drawString(s, xPos + 16, yPos + 32, -6250336);
        Gui.drawRect(xPos + this.fontRendererObj.getStringWidth(s) + 18, yPos + 36, xPos + 160, yPos + 37, -10461088);
        this.fontRendererObj.drawString(this.manager.getVersion().toString(), xPos + 162, yPos + 22, -1);
        this.fontRendererObj.drawString(this.manager.getUpdateInfo().version, xPos + 162, yPos + 32, -1);
        this.fontRendererObj.drawString("Details and Changelog:", xPos + 16, yPos + 48, -6250336);
        this.mc.renderEngine.bindTexture(TEXTURE);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.drawTexturedModalRect(xPos + listX - 1, yPos + listY - 1, 0, 0, listWidth + 2, listHeight + 2);
        GuiUtils.doGlScissor(xPos + listX, yPos + listY, listWidth, listHeight);
        GL11.glEnable((int)3089);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)0.0f, (float)(-this.scrollAmount), (float)0.0f);
        s = this.manager.getUpdateInfo().description;
        this.fontRendererObj.drawSplitString(s, xPos + listX + 4, yPos + listY + listTextY + 2, listWidth - 12, -6291360);
        listTextY += this.fontRendererObj.splitStringWidth(s, listWidth - 12) + 4;
        if (this.manager.getUpdateInfo().changelog != null) {
            for (int i = 0; i < this.manager.getUpdateInfo().changelog.length; ++i) {
                s = this.manager.getUpdateInfo().changelog[i];
                Gui.drawRect(xPos + listX + 4, yPos + listY + listTextY + 4, xPos + listX + 8, yPos + listY + listTextY + 8, i % 2 == 0 ? -16777216 : -10461088);
                this.fontRendererObj.drawSplitString(s, xPos + listX + 14, yPos + listY + listTextY + 2, listWidth - 22, i % 2 == 0 ? -16777216 : -10461088);
                listTextY += this.fontRendererObj.splitStringWidth(s, listWidth - 22) + 2;
            }
        }
        this.scrollMax = listTextY - listHeight + 4;
        GL11.glPopMatrix();
        GL11.glDisable((int)3089);
        this.mc.renderEngine.bindTexture(TEXTURE);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int scrollPos = (int)(this.scrollAmount / this.scrollMax * ((float)listHeight - 10.0f));
        this.drawTexturedModalRect(xPos + listX + listWidth - 4, yPos + listY + scrollPos, listWidth + 2, 0, 4, 10);
        GL11.glDisable((int)3042);
        super.drawScreen(mouseX, mouseY, partTicks);
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int mWheelDir = Mouse.getEventDWheel();
        if (mWheelDir != 0 && this.scrollMax > 0.0f) {
            this.scrollAmount = mWheelDir > 0 ? Math.max(0.0f, this.scrollAmount - 5.0f) : Math.min(this.scrollMax, this.scrollAmount + 5.0f);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == this.backToList) {
            this.mc.displayGuiScreen(this.updateList);
        }
    }
}

