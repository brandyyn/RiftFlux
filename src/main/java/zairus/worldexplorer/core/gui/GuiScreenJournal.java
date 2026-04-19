/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.audio.ISound
 *  net.minecraft.client.audio.PositionedSoundRecord
 *  net.minecraft.client.audio.SoundHandler
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.nbt.NBTTagString
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.input.Keyboard
 *  org.lwjgl.opengl.GL11
 */
package zairus.worldexplorer.core.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import zairus.worldexplorer.core.WorldExplorer;
import zairus.worldexplorer.core.block.WorldExplorerBlocks;
import zairus.worldexplorer.core.helpers.RenderHelper;
import zairus.worldexplorer.core.items.WorldExplorerItems;
import zairus.worldexplorer.core.journal.IJournalSection;
import zairus.worldexplorer.core.journal.JournalSectionMain;
import zairus.worldexplorer.core.player.CorePlayerManager;
import zairus.worldexplorer.core.util.network.JournalPacket;

@SideOnly(value=Side.CLIENT)
public class GuiScreenJournal
extends GuiScreen {
    private static final ResourceLocation bookGuiTextures = new ResourceLocation("worldexplorer", "textures/gui/zairus_charts.png");
    private final EntityPlayer editingPlayer;
    private final ItemStack bookObj;
    private int bookImageWidth = 251;
    private int bookImageHeight = 157;
    private int bookTotalPages = 1;
    private int currPage = 0;
    private NBTTagList bookPages;
    private String bookTitle;
    private JournalPageButton buttonNextPage;
    private JournalPageButton buttonPreviousPage;
    private JournalPageButton buttonCancel;
    private JournalPageButton buttonHome;
    private float sheetScaleX;
    private float sheetScaleY;
    private List<IJournalSection> sections;

    public GuiScreenJournal(EntityPlayer player, ItemStack stack) {
        this.bookTitle = Minecraft.getMinecraft().thePlayer.getDisplayName() + "'s Journal";
        this.sheetScaleX = 1.4f;
        this.sheetScaleY = 1.4f;
        this.sections = new ArrayList<IJournalSection>();
        this.editingPlayer = player;
        this.bookObj = stack;
        if (stack.hasTagCompound()) {
            NBTTagCompound nbttagcompound = stack.getTagCompound();
            this.bookPages = nbttagcompound.getTagList("pages", 8);
            if (this.bookPages != null) {
                this.bookPages = (NBTTagList)this.bookPages.copy();
                this.bookTotalPages = this.bookPages.tagCount();
                if (this.bookTotalPages < 1) {
                    this.bookTotalPages = 1;
                }
            }
        }
        if (this.bookPages == null) {
            this.bookPages = new NBTTagList();
            this.bookPages.appendTag((NBTBase)new NBTTagString(""));
            this.bookTotalPages = 1;
        }
        if (!this.bookObj.hasTagCompound()) {
            this.bookObj.setTagCompound(new NBTTagCompound());
        }
        if (this.bookObj.getTagCompound().hasKey("JournalTitle")) {
            this.bookTitle = this.bookObj.getTagCompound().getString("JournalTitle");
        } else {
            WorldExplorer.packetPipeline.sendToServer(new JournalPacket());
            this.bookObj.getTagCompound().setString("JournalTitle", this.bookTitle);
        }
        CorePlayerManager.checkInitialize(player);
        WorldExplorer.packetPipeline.sendToServer(new JournalPacket());
    }

    public void updateScreen() {
        super.updateScreen();
    }

    public void initGui() {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents((boolean)true);
        int rightMargin = 13;
        int buttonSpacing = 5;
        int buttonWidth = 21;
        int bottomMargin = 31;
        float sheetScaleX = 1.4f;
        float sheetScaleY = 1.4f;
        int left = (int)(((float)this.width - this.scaleWidthF()) / 2.0f);
        int scaledWidth = (int)((float)this.bookImageWidth * sheetScaleX) + left;
        int scaledHeight = 10 + (int)((float)this.bookImageHeight * sheetScaleY);
        this.buttonCancel = new JournalPageButton(0, scaledWidth - rightMargin - buttonSpacing - buttonWidth * 3, scaledHeight - bottomMargin, 2);
        this.buttonList.add(this.buttonCancel);
        this.buttonPreviousPage = new JournalPageButton(1, scaledWidth - rightMargin - buttonSpacing - buttonWidth * 2, scaledHeight - bottomMargin, 1);
        this.buttonList.add(this.buttonPreviousPage);
        this.buttonNextPage = new JournalPageButton(2, scaledWidth - rightMargin - buttonWidth, scaledHeight - bottomMargin, 0);
        this.buttonList.add(this.buttonNextPage);
        this.buttonHome = new JournalPageButton(3, this.width / 2 + 5, scaledHeight - bottomMargin, 63, 9, 1.0f, 1.0f, 6, "Home");
        this.buttonList.add(this.buttonHome);
        ArrayList<ItemStack> tabIcons = new ArrayList<ItemStack>();
        tabIcons.add(new ItemStack((Item)WorldExplorerItems.journal));
        tabIcons.add(new ItemStack(WorldExplorerBlocks.studydesk));
        tabIcons.add(new ItemStack((Block)Blocks.red_flower));
        tabIcons.add(new ItemStack((Item)Items.map));
        for (int i = 0; i < tabIcons.size(); ++i) {
            this.buttonList.add(new JournalPageButton(i + 4, 0, 0, 18, 26, sheetScaleX, sheetScaleY, 7, (ItemStack)tabIcons.get(i)));
        }
        this.updateButtons();
        this.sections.add(new JournalSectionMain().setTitle(this.bookTitle));
    }

    public void onGuiClosed() {
        Keyboard.enableRepeatEvents((boolean)false);
    }

    private void updateButtons() {
        int left = (int)(((float)this.width - this.scaleWidthF()) / 2.0f);
        int top = 13;
        for (int i = 0; i < 4; ++i) {
            ((JournalPageButton)((Object)this.buttonList.get((int)(i + 4)))).soundType = 2;
            ((JournalPageButton)((Object)this.buttonList.get(i + 4))).width = 18;
            ((JournalPageButton)((Object)this.buttonList.get(i + 4))).height = 26;
            ((JournalPageButton)((Object)this.buttonList.get((int)(i + 4)))).xPosition = left - 10;
            ((JournalPageButton)((Object)this.buttonList.get((int)(i + 4)))).yPosition = top + 24 * i;
        }
        this.buttonCancel.xPosition = left + ((int)this.scaleWidthF() - this.buttonCancel.width) - 10;
        this.buttonCancel.yPosition = top + ((int)this.scaleHeightF() - this.buttonCancel.height) - 7;
        this.buttonCancel.visible = true;
        this.buttonNextPage.soundType = 1;
        this.buttonNextPage.visible = true;
        this.buttonNextPage.xPosition = left + (int)this.scaleWidthF() - this.buttonCancel.width - this.buttonNextPage.width - 10;
        this.buttonNextPage.yPosition = this.buttonCancel.yPosition;
        this.buttonPreviousPage.soundType = 1;
        this.buttonPreviousPage.visible = true;
        this.buttonPreviousPage.xPosition = this.buttonNextPage.xPosition - this.buttonPreviousPage.width;
        this.buttonPreviousPage.yPosition = this.buttonNextPage.yPosition;
        this.buttonHome.visible = false;
    }

    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            switch (button.id) {
                case 1: {
                    --this.currPage;
                    if (this.currPage >= 0) break;
                    this.currPage = 0;
                    break;
                }
                case 2: {
                    ++this.currPage;
                    if (this.currPage <= this.bookTotalPages) break;
                    this.currPage = this.bookTotalPages;
                    break;
                }
                case 3: {
                    this.currPage = 0;
                    break;
                }
                case 4: 
                case 5: 
                case 6: 
                case 7: {
                    break;
                }
                default: {
                    this.mc.displayGuiScreen((GuiScreen)null);
                }
            }
            this.updateButtons();
        }
    }

    protected void keyTyped(char keyChar, int p_73869_2_) {
        super.keyTyped(keyChar, p_73869_2_);
    }

    private float scaleWidthF() {
        return (float)this.bookImageWidth * this.sheetScaleX;
    }

    private float scaleHeightF() {
        return (float)this.bookImageHeight * this.sheetScaleY;
    }

    public void setScale(float xScale, float yScale) {
        this.sheetScaleX = xScale;
        this.sheetScaleY = yScale;
    }

    public void drawScreen(int screenX, int screenY, float f1) {
        int j;
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(bookGuiTextures);
        int left = (int)(((float)this.width - this.scaleWidthF()) / 2.0f);
        int top = 10;
        GL11.glScalef((float)(1.0f * this.sheetScaleX), (float)(1.0f * this.sheetScaleY), (float)0.0f);
        this.drawTexturedModalRect((int)((float)left / this.sheetScaleX), (int)((float)top / this.sheetScaleY), 3, 1, this.bookImageWidth, this.bookImageHeight);
        GL11.glScalef((float)(1.0f / this.sheetScaleX), (float)(1.0f / this.sheetScaleY), (float)0.0f);
        String s1 = "";
        super.drawScreen(screenX, screenY, f1);
        int leftMargin = 30;
        int topMargin = 30;
        int lineHeight = 14;
        IJournalSection sec = this.sections.get(0);
        s1 = sec.getTitle();
        this.fontRendererObj.drawString(s1, left + leftMargin, top + topMargin, 0);
        this.fontRendererObj.drawString(s1, left + leftMargin + 1, top + topMargin, 0);
        int lineX = 0;
        int lineY = 0;
        ArrayList pages = new ArrayList();
        int curPage = 0;
        s1 = "";
        for (int i = 0; i < sec.getContent().length(); ++i) {
            s1 = s1 + sec.getContent().substring(i, i + 1);
            if (++lineX > 23 || i == sec.getContent().length() - 1) {
                if (pages.size() == 0) {
                    pages.add(new ArrayList());
                }
                ((List)pages.get(curPage)).add(s1);
                lineX = 0;
                s1 = "";
                ++lineY;
            }
            if (lineY <= 9) continue;
            ++curPage;
            pages.add(new ArrayList());
            lineY = 0;
            lineX = 0;
        }
        int textX = left + leftMargin;
        int textY = top + topMargin + lineHeight + 5;
        this.bookTotalPages = MathHelper.ceiling_float_int((float)((float)pages.size() / 2.0f)) - 1;
        int pageIndex = this.currPage * 2;
        if (pageIndex < pages.size()) {
            for (j = 0; j < ((List)pages.get(pageIndex)).size(); ++j) {
                this.fontRendererObj.drawString((String)((List)pages.get(pageIndex)).get(j), textX, textY + lineHeight * j, 0);
            }
        }
        if (++pageIndex < pages.size()) {
            for (j = 0; j < ((List)pages.get(pageIndex)).size(); ++j) {
                this.fontRendererObj.drawString((String)((List)pages.get(pageIndex)).get(j), textX + 155, textY + lineHeight * j, 0);
            }
        }
        leftMargin = 40;
        int bottomRowTop = (int)((float)top + (float)this.bookImageHeight * this.sheetScaleY - 22.0f);
        this.drawItemStack(new ItemStack(Items.compass), left + leftMargin, bottomRowTop, "");
        this.drawItemStack(new ItemStack(Items.clock), left + leftMargin + 20, bottomRowTop, "");
    }

    public int getCurrentPage() {
        return this.currPage;
    }

    private void drawItemStack(ItemStack stack, int x, int y, String text) {
        RenderHelper.drawItemStack(stack, x, y, text);
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    static class JournalPageButton
    extends GuiButton {
        public int soundType = 0;
        public ItemStack iconStack = null;
        private final int type;
        private int width = 0;
        private int height = 0;
        private float scaleWidth = 1.0f;
        private float scaleHeight = 1.0f;
        private String bText;

        public JournalPageButton(int buttonId, int posX, int posY, int buttonType) {
            super(buttonId, posX, posY, 21, 18, "");
            this.type = buttonType;
            this.width = 21;
            this.height = 18;
        }

        public JournalPageButton(int buttonId, int posX, int posY, int w, int h, float scaleW, float scaleH, int buttonType, String text) {
            super(buttonId, posX, posY, w, h, text);
            this.type = buttonType;
            this.width = w;
            this.height = h;
            this.scaleWidth = scaleW;
            this.scaleHeight = scaleH;
            this.bText = text;
        }

        public JournalPageButton(int buttonId, int posX, int posY, int w, int h, float scaleW, float scaleH, int buttonType, ItemStack icon) {
            this(buttonId, posX, posY, w, h, scaleW, scaleH, buttonType, "");
            this.iconStack = icon;
        }

        public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
            if (this.visible) {
                boolean flag = mouseX >= (int)((float)this.xPosition * this.scaleWidth) && mouseY >= (int)((float)this.yPosition * this.scaleHeight) && mouseX < (int)((float)(this.xPosition + this.width) * this.scaleWidth) && mouseY < (int)((float)(this.yPosition + this.height) * this.scaleHeight);
                GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                minecraft.getTextureManager().bindTexture(bookGuiTextures);
                int k = 0;
                int l = 0;
                if (this.type < 7) {
                    k = 0;
                    l = 160 + this.type * this.height;
                } else {
                    k = 86;
                    l = 160;
                }
                if (flag) {
                    k += this.width;
                }
                GL11.glScalef((float)(1.0f * this.scaleWidth), (float)(1.0f * this.scaleHeight), (float)0.0f);
                this.drawTexturedModalRect(this.xPosition, this.yPosition, k, l, this.width, this.height);
                GL11.glScalef((float)(1.0f / this.scaleWidth), (float)(1.0f / this.scaleHeight), (float)0.0f);
                if (this.iconStack != null) {
                    RenderHelper.drawItemStack(this.iconStack, (int)((float)this.xPosition * this.scaleWidth + 6.0f), (int)((float)this.yPosition * this.scaleHeight + 10.0f), "");
                }
                this.drawCenteredString(Minecraft.getMinecraft().fontRenderer, this.bText, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, 0xFFFFFF);
            }
        }

        public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
            boolean pressed = this.enabled && this.visible && mouseX >= (int)((float)this.xPosition * this.scaleWidth) && mouseY >= (int)((float)this.yPosition * this.scaleHeight) && mouseX < (int)((float)(this.xPosition + this.width) * this.scaleWidth) && mouseY < (int)((float)(this.yPosition + this.height) * this.scaleHeight);
            return pressed;
        }

        public void func_146113_a(SoundHandler soundHandler) {
            switch (this.soundType) {
                case 1: {
                    soundHandler.playSound((ISound)PositionedSoundRecord.func_147674_a((ResourceLocation)new ResourceLocation("worldexplorer", "book_page"), (float)1.0f));
                    break;
                }
                case 2: {
                    soundHandler.playSound((ISound)PositionedSoundRecord.func_147674_a((ResourceLocation)new ResourceLocation("worldexplorer", "book_tab"), (float)1.0f));
                    break;
                }
                default: {
                    soundHandler.playSound((ISound)PositionedSoundRecord.func_147674_a((ResourceLocation)new ResourceLocation("worldexplorer", "book_close"), (float)1.0f));
                }
            }
        }
    }
}

