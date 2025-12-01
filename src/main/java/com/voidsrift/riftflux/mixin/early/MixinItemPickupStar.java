package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.Constants;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.net.MsgClearPickupTag;
import com.voidsrift.riftflux.net.RFNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Draw pickup stars right after GL11.glPopMatrix() inside drawScreen:
 *  • Always runs (even if a subclass overrides foreground)
 *  • Absolute coords (guiLeft/guiTop + slot pos)
 *  • Tooltip renders after this, so tooltip is always on top
 *
 * Also: at HEAD, if mouse hovers a slot that has the star tag, send a server packet to clear it,
 * and clear locally for instant visual feedback – but only after a short, deliberate hover with
 * no mouse buttons or modifiers held.
 */
@Mixin(GuiContainer.class)
public abstract class MixinItemPickupStar {

    @Shadow protected int guiLeft;
    @Shadow protected int guiTop;
    @Shadow public Container inventorySlots;

    @Unique private static final String TAG_NEW  = "riftflux_new";
    @Unique private static final ResourceLocation TEX =
            new ResourceLocation(Constants.MODID, "textures/gui/pickup_star.png"); // 16x16

    // For "intentional hover" detection
    @Unique private static final long HOVER_CLEAR_DELAY_MS = 20L;
    @Unique private int rf$lastHoverIndex = -1;
    @Unique private long rf$hoverStartMs  = 0L;

    /**
     * If hovering a starred slot, clear it: send MsgClearPickupTag(windowId, slotIndex) and
     * remove the tag locally to avoid any one-frame flicker.
     *
     * Requires:
     *  - same slot hovered for HOVER_CLEAR_DELAY_MS
     *  - no mouse buttons pressed
     *  - no Shift (or Ctrl) held, so shift-clicking doesn't insta-clear stars
     */
    @Inject(method = "drawScreen(IIF)V", at = @At("HEAD"))
    private void rf$sendClearIfHovered(int mouseX, int mouseY, float pt, CallbackInfo ci) {
        if (!ModConfig.enableItemPickupStar) return;
        if (inventorySlots == null || inventorySlots.inventorySlots == null) return;

        @SuppressWarnings("rawtypes")
        final java.util.List slots = inventorySlots.inventorySlots;
        if (slots.isEmpty()) return;

        // Find which slot (if any) is currently hovered
        int hoveredIndex = -1;
        Slot hoveredSlot = null;
        for (int i = 0; i < slots.size(); i++) {
            final Slot s = (Slot) slots.get(i);
            final int x0 = guiLeft + s.xDisplayPosition;
            final int y0 = guiTop  + s.yDisplayPosition;
            if (mouseX >= x0 && mouseY >= y0 && mouseX < x0 + 16 && mouseY < y0 + 16) {
                hoveredIndex = i;
                hoveredSlot = s;
                break;
            }
        }

        if (hoveredIndex < 0 || hoveredSlot == null) {
            // Not hovering any slot; reset tracking
            rf$lastHoverIndex = -1;
            rf$hoverStartMs = 0L;
            return;
        }

        final long now = Minecraft.getSystemTime();

        // If we just moved onto a different slot, start a new hover timer
        if (hoveredIndex != rf$lastHoverIndex) {
            rf$lastHoverIndex = hoveredIndex;
            rf$hoverStartMs = now;
            return;
        }

        // If not hovered long enough, don't clear yet
        if (now - rf$hoverStartMs < HOVER_CLEAR_DELAY_MS) {
            return;
        }

        // Don't clear while actively interacting: any mouse button or modifier keys
        if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1) || Mouse.isButtonDown(2)
                || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)
                || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)
                || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)
                || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
            // Keep bumping the timer so clear only happens after you release them
            rf$hoverStartMs = now;
            return;
        }

        final ItemStack st = hoveredSlot.getStack();
        if (st == null || !st.hasTagCompound()) {
            return;
        }

        final NBTTagCompound nbt = st.getTagCompound();
        if (!nbt.getBoolean(TAG_NEW)) {
            return;
        }

        // 1) Server-authoritative clear
        RFNetwork.CH.sendToServer(new MsgClearPickupTag(inventorySlots.windowId, hoveredIndex));

        // 2) Local UX: clear immediately so the star vanishes this frame
        nbt.removeTag(TAG_NEW);
        if (nbt.hasNoTags()) {
            st.setTagCompound(null);
        }

        // After clearing, reset hover tracking so we don't spam packets
        rf$lastHoverIndex = -1;
        rf$hoverStartMs = 0L;
    }

    /**
     * Draw ALL stars immediately AFTER the GUI pops its matrix (i.e., back to absolute coords)
     * and BEFORE vanilla renders the tooltip. This is robust across overrides/OptiFine.
     */
    @Inject(
            method = "drawScreen(IIF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V",
                    shift = At.Shift.AFTER,
                    remap = false
            )
    )
    private void rf$drawStarsAfterPop(int mouseX, int mouseY, float pt, CallbackInfo ci) {
        if (!ModConfig.enableItemPickupStar) return;
        if (inventorySlots == null || inventorySlots.inventorySlots == null) return;

        @SuppressWarnings("rawtypes")
        final java.util.List slots = inventorySlots.inventorySlots;
        if (slots.isEmpty()) return;

        Minecraft.getMinecraft().getTextureManager().bindTexture(TEX);

        // Force clean state so the star isn't tinted or dark
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glColor4f(1F, 1F, 1F, 1F);

            final Tessellator t = Tessellator.instance;
            for (Object o : slots) {
                final Slot s = (Slot) o;
                final ItemStack st = s.getStack();
                if (st == null) continue;
                final NBTTagCompound tag = st.getTagCompound();
                if (tag == null || !tag.getBoolean(TAG_NEW)) continue;

                final int x = guiLeft + s.xDisplayPosition; // absolute coords here
                final int y = guiTop  + s.yDisplayPosition;

                t.startDrawingQuads();
                t.addVertexWithUV(x    , y+16, 0, 0, 1);
                t.addVertexWithUV(x+16 , y+16, 0, 1, 1);
                t.addVertexWithUV(x+16 , y   , 0, 1, 0);
                t.addVertexWithUV(x    , y   , 0, 0, 0);
                t.draw();
            }
        } finally {
            // restore everything and ensure no lingering tint
            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glPopAttrib();
        }
    }
}
