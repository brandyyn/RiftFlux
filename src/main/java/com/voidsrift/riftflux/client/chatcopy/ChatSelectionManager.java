package com.voidsrift.riftflux.client.chatcopy;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.mixin.accessor.GuiNewChatAccessor;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public final class ChatSelectionManager {
    private static final int CHAT_LEFT = 2;
    private static final int CHAT_HIT_LEFT = 3;
    private static final int CHAT_BOTTOM = 27;
    private static final int DRAG_THRESHOLD_SQ = 9;
    private static final char SECTION_SIGN = '\u00A7';
    private static final ChatSelectionManager INSTANCE = new ChatSelectionManager();

    private final Minecraft mc = Minecraft.getMinecraft();
    private final Cursor cursor1 = new Cursor();
    private final Cursor cursor2 = new Cursor();
    private boolean bootstrapped;
    private boolean leftMouseDown;
    private boolean selecting;
    private boolean dragging;
    private int pressMouseX;
    private int pressMouseY;

    private ChatSelectionManager() {
    }

    public static void bootstrap() {
        if (!INSTANCE.bootstrapped) {
            INSTANCE.bootstrapped = true;
            MinecraftForge.EVENT_BUS.register(INSTANCE);
        }
    }

    public static boolean interceptChatClick(GuiChat gui, int mouseX, int mouseY, int button) {
        return false;
    }

    public static void handleChatDrag(GuiChat gui, int mouseX, int mouseY, int button) {
        // Selection is tracked from DrawScreenEvent so it stays in sync with raw mouse state.
    }

    public static void handleChatMouseUp(GuiChat gui, int mouseX, int mouseY, int button) {
        // Selection is tracked from DrawScreenEvent so it stays in sync with raw mouse state.
    }

    public static boolean handleChatKeyTyped(char typedChar, int keyCode) {
        return INSTANCE.handleKeyPress(keyCode);
    }

    public static void resetChatState() {
        INSTANCE.resetState();
    }

    private void beginCapture() {
        Cursor hit = resolveCursor(true);
        if (hit.chatLine == null) {
            selecting = false;
            clearSelection();
            return;
        }

        pressMouseX = getScaledMouseX();
        pressMouseY = getScaledMouseYFromBottom();
        selecting = true;
        dragging = false;
        cursor1.copyFrom(hit);
        cursor2.copyFrom(hit);
    }

    private void updateCapture() {
        if (!selecting) {
            return;
        }

        Cursor current = resolveCursor(false);
        if (current.chatLine != null) {
            cursor2.copyFrom(current);
        }
        if (!dragging && movedPastDragThreshold(getScaledMouseX(), getScaledMouseYFromBottom())) {
            dragging = true;
        }
    }

    private void finishCapture() {
        if (!selecting) {
            return;
        }

        updateCapture();
        selecting = false;
        dragging = false;
        if (!hasSelection()) {
            clearSelection();
        }
    }

    @SubscribeEvent
    public void onPostInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.gui instanceof GuiChat) {
            resetState();
        }
    }

    @SubscribeEvent
    public void onPostDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!(event.gui instanceof GuiChat) || !ModConfig.enableChatSelectionCopy) {
            return;
        }

        boolean currentLeftMouseDown = Mouse.isButtonDown(0);
        if (currentLeftMouseDown) {
            if (!leftMouseDown) {
                beginCapture();
            } else {
                updateCapture();
            }
        } else if (leftMouseDown) {
            finishCapture();
        }
        leftMouseDown = currentLeftMouseDown;

        if (hasSelection()) {
            drawSelection();
        }
    }

    private boolean movedPastDragThreshold(int mouseX, int mouseY) {
        if (!sameCursor(cursor1, cursor2)) {
            return true;
        }
        int dx = mouseX - pressMouseX;
        int dy = mouseY - pressMouseY;
        return dx * dx + dy * dy >= DRAG_THRESHOLD_SQ;
    }

    private void drawSelection() {
        SelectionBounds bounds = getSelectionBounds();
        if (bounds == null) {
            return;
        }

        GuiNewChat chatGui = mc.ingameGUI.getChatGUI();
        List drawnChatLines = getDrawnChatLines(chatGui);
        int scrollPos = ((GuiNewChatAccessor) chatGui).getScrollPos();
        int lineCount = chatGui.func_146232_i();

        GL11.glPushMatrix();
        GL11.glTranslatef(CHAT_LEFT, new ScaledResolution(mc, mc.displayWidth, mc.displayHeight).getScaledHeight() - CHAT_BOTTOM, 0.0f);
        float chatScale = chatGui.func_146244_h();
        GL11.glScalef(chatScale, chatScale, 1.0f);

        for (int i = bounds.max; i >= bounds.min; --i) {
            int relativeIndex = i - scrollPos;
            if (relativeIndex < 0 || relativeIndex >= lineCount) {
                continue;
            }

            ChatLine chatLine = (ChatLine) drawnChatLines.get(i);
            String visibleText = getVisibleText(chatLine);
            int left = i == bounds.max
                    ? mc.fontRenderer.getStringWidth(visibleText.substring(0, bounds.startPosition))
                    : 0;
            int right = mc.fontRenderer.getStringWidth(i == bounds.min
                    ? visibleText.substring(0, bounds.endPosition)
                    : visibleText);
            int bottom = -relativeIndex * 9;
            drawSelectionBox(left, bottom - 9, right, bottom);
        }

        GL11.glPopMatrix();
    }

    private void drawSelectionBox(int left, int top, int right, int bottom) {
        Tessellator tessellator = Tessellator.instance;
        GL11.glColor4f(0.0f, 0.0f, 255.0f, 255.0f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glLogicOp(GL11.GL_OR_REVERSE);
        tessellator.startDrawingQuads();
        tessellator.addVertex(left, bottom, 0.0);
        tessellator.addVertex(right, bottom, 0.0);
        tessellator.addVertex(right, top, 0.0);
        tessellator.addVertex(left, top, 0.0);
        tessellator.draw();
        GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private String getSelectedText(boolean includeFormattingCodes) {
        SelectionBounds bounds = getSelectionBounds();
        if (bounds == null) {
            return "";
        }

        StringBuilder out = new StringBuilder();
        List drawnChatLines = getDrawnChatLines(mc.ingameGUI.getChatGUI());
        for (int i = bounds.max; i >= bounds.min; --i) {
            ChatLine chatLine = (ChatLine) drawnChatLines.get(i);
            String rawText = getRawText(chatLine);
            String visibleText = getVisibleText(rawText);
            int start = i == bounds.max ? bounds.startPosition : 0;
            int end = i == bounds.min ? bounds.endPosition : visibleText.length();
            if (start < end) {
                out.append(includeFormattingCodes
                        ? sliceFormattedText(rawText, start, end)
                        : visibleText.substring(start, end));
            }
            if (i > bounds.min) {
                out.append('\n');
            }
        }
        return out.toString();
    }

    private boolean handleKeyPress(int keyCode) {
        if (!ModConfig.enableChatSelectionCopy || !hasSelection()) {
            return false;
        }
        if (!GuiScreen.isCtrlKeyDown() || keyCode != Keyboard.KEY_C) {
            return false;
        }

        String selectedText = getSelectedText(false);
        if (!selectedText.isEmpty()) {
            GuiScreen.setClipboardString(selectedText);
        }
        clearSelection();
        return true;
    }

    private String sliceFormattedText(String rawText, int start, int end) {
        if (rawText == null || start >= end) {
            return "";
        }

        StringBuilder out = new StringBuilder();
        StringBuilder activeCodes = new StringBuilder();
        int visibleIndex = 0;
        boolean started = false;

        for (int i = 0; i < rawText.length(); ++i) {
            char c = rawText.charAt(i);
            if (c == SECTION_SIGN && i + 1 < rawText.length()) {
                char code = rawText.charAt(++i);
                updateActiveCodes(activeCodes, code);
                if (started && visibleIndex < end) {
                    out.append(SECTION_SIGN).append(code);
                }
                continue;
            }

            if (visibleIndex >= start && visibleIndex < end) {
                if (!started && activeCodes.length() > 0) {
                    out.append(activeCodes);
                    started = true;
                } else if (!started) {
                    started = true;
                }
                out.append(c);
            }

            ++visibleIndex;
            if (visibleIndex >= end) {
                break;
            }
        }

        return out.toString();
    }

    private void updateActiveCodes(StringBuilder activeCodes, char code) {
        char lower = Character.toLowerCase(code);
        if (lower == 'r') {
            activeCodes.setLength(0);
            return;
        }

        if (isColorCode(lower)) {
            activeCodes.setLength(0);
            activeCodes.append(SECTION_SIGN).append(lower);
            return;
        }

        if (isFormatCode(lower)) {
            String token = String.valueOf(SECTION_SIGN) + lower;
            if (activeCodes.indexOf(token) < 0) {
                activeCodes.append(token);
            }
        }
    }

    private boolean isColorCode(char code) {
        return (code >= '0' && code <= '9') || (code >= 'a' && code <= 'f');
    }

    private boolean isFormatCode(char code) {
        return code >= 'k' && code <= 'o';
    }

    private boolean hasSelection() {
        return cursor1.chatLine != null
                && cursor2.chatLine != null
                && !sameCursor(cursor1, cursor2);
    }

    private boolean sameCursor(Cursor a, Cursor b) {
        return a.chatLine == b.chatLine && a.position == b.position;
    }

    private SelectionBounds getSelectionBounds() {
        if (!hasSelection()) {
            return null;
        }

        List drawnChatLines = getDrawnChatLines(mc.ingameGUI.getChatGUI());
        int index1 = drawnChatLines.indexOf(cursor1.chatLine);
        int index2 = drawnChatLines.indexOf(cursor2.chatLine);
        if (index1 == -1 || index2 == -1) {
            clearSelection();
            return null;
        }

        if (index1 > index2) {
            return new SelectionBounds(index2, index1, cursor1.position, cursor2.position);
        }
        if (index1 < index2) {
            return new SelectionBounds(index1, index2, cursor2.position, cursor1.position);
        }
        if (cursor1.position < cursor2.position) {
            return new SelectionBounds(index2, index1, cursor1.position, cursor2.position);
        }
        return new SelectionBounds(index1, index2, cursor2.position, cursor1.position);
    }

    private Cursor resolveCursor(boolean requireHitBox) {
        GuiNewChat chatGui = mc.ingameGUI.getChatGUI();
        List drawnChatLines = getDrawnChatLines(chatGui);
        if (drawnChatLines.isEmpty()) {
            return Cursor.EMPTY;
        }

        int lineIndex = getLineIndex(chatGui, requireHitBox);
        if (lineIndex < 0 || lineIndex >= drawnChatLines.size()) {
            return Cursor.EMPTY;
        }

        ChatLine chatLine = (ChatLine) drawnChatLines.get(lineIndex);
        return new Cursor(chatLine, getTextPosition(chatGui, getVisibleText(chatLine), getScaledMouseX()));
    }

    private int getLineIndex(GuiNewChat chatGui, boolean requireHitBox) {
        List drawnChatLines = getDrawnChatLines(chatGui);
        if (drawnChatLines.isEmpty()) {
            return -1;
        }

        ScaledResolution scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int scaleFactor = scaledResolution.getScaleFactor();
        float chatScale = chatGui.func_146244_h();
        int chatY = MathHelper.floor_float((float) (Mouse.getY() / scaleFactor - CHAT_BOTTOM) / chatScale);
        int scrollPos = ((GuiNewChatAccessor) chatGui).getScrollPos();
        int visibleLineCount = Math.min(chatGui.func_146232_i(), drawnChatLines.size());
        int index = chatY / mc.fontRenderer.FONT_HEIGHT + scrollPos;

        if (requireHitBox) {
            int chatX = MathHelper.floor_float((float) (Mouse.getX() / scaleFactor - CHAT_HIT_LEFT) / chatScale);
            int maxWidth = MathHelper.floor_float((float) chatGui.func_146228_f() / chatScale);
            if (chatX < 0 || chatX > maxWidth) {
                return -1;
            }
            if (chatY < 0 || chatY >= visibleLineCount * mc.fontRenderer.FONT_HEIGHT + visibleLineCount) {
                return -1;
            }
            return index >= 0 && index < drawnChatLines.size() ? index : -1;
        }

        int minIndex = scrollPos;
        int maxIndex = Math.min(drawnChatLines.size() - 1, scrollPos + visibleLineCount - 1);
        return MathHelper.clamp_int(index, minIndex, maxIndex);
    }

    private int getTextPosition(GuiNewChat chatGui, String visibleText, int mouseX) {
        if (visibleText.isEmpty()) {
            return 0;
        }
        int width = MathHelper.floor_float((float) (mouseX - CHAT_LEFT) / chatGui.func_146244_h());
        if (width <= 0) {
            return 0;
        }
        return mc.fontRenderer.trimStringToWidth(visibleText, width).length();
    }

    private int getScaledMouseX() {
        ScaledResolution scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        return Mouse.getX() / scaledResolution.getScaleFactor();
    }

    private int getScaledMouseYFromBottom() {
        ScaledResolution scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        return Mouse.getY() / scaledResolution.getScaleFactor();
    }

    private List getDrawnChatLines(GuiNewChat chatGui) {
        return ((GuiNewChatAccessor) chatGui).getDrawnChatLines();
    }

    private String getRawText(ChatLine chatLine) {
        return chatLine.func_151461_a().getFormattedText();
    }

    private String getVisibleText(ChatLine chatLine) {
        return getVisibleText(getRawText(chatLine));
    }

    private String getVisibleText(String rawText) {
        if (rawText == null || rawText.isEmpty()) {
            return "";
        }
        String stripped = EnumChatFormatting.getTextWithoutFormattingCodes(rawText);
        return stripped == null ? "" : stripped;
    }

    private void clearSelection() {
        cursor1.clear();
        cursor2.clear();
    }

    private void resetState() {
        leftMouseDown = Mouse.isButtonDown(0);
        selecting = false;
        dragging = false;
        clearSelection();
    }

    private static final class Cursor {
        private static final Cursor EMPTY = new Cursor(null, 0);

        private ChatLine chatLine;
        private int position;

        private Cursor() {
        }

        private Cursor(ChatLine chatLine, int position) {
            this.chatLine = chatLine;
            this.position = position;
        }

        private void copyFrom(Cursor other) {
            this.chatLine = other.chatLine;
            this.position = other.position;
        }

        private void clear() {
            this.chatLine = null;
            this.position = 0;
        }
    }

    private static final class SelectionBounds {
        private final int min;
        private final int max;
        private final int startPosition;
        private final int endPosition;

        private SelectionBounds(int min, int max, int startPosition, int endPosition) {
            this.min = min;
            this.max = max;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
        }
    }
}
