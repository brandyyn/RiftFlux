package com.excsi.riftfixes.mixin.early;

import com.excsi.riftfixes.Constants;
import com.excsi.riftfixes.ModConfig;
import com.excsi.riftfixes.net.MsgClearPickupTag;
import com.excsi.riftfixes.net.RFNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Draw pickup stars after GL11.glPopMatrix() so they render under the tooltip.
 * On hover, send a server packet to clear the star (authoritative), instead of
 * clearing locally (which gets overwritten by server resyncs when opening chests).
 */
@Mixin(GuiContainer.class)
public abstract class MixinItemPickupStar {

    @Shadow protected int guiLeft;
    @Shadow protected int guiTop;
    @Shadow public Container inventorySlots;

    @Unique private static final String TAG_NEW  = "riftfixes_new";
    @Unique private static final ResourceLocation TEX =
            new ResourceLocation(Constants.MODID, "textures/gui/pickup_star.png"); // 16x16

    // Grace period so opening a GUI doesn't instantly clear hovered slot
    @Unique private int rf$hoverClearDelayTicks = 0;

    @Inject(method = "initGui()V", at = @At("RETURN"))
    private void rf$onInitGui(CallbackInfo ci) {
        rf$hoverClearDelayTicks = 5; // ~0.25s @ 20 TPS
    }

    /**
     * HEAD: if the hovered slot has the star, ask the SERVER to clear it.
     * Do NOT clear locally; let the server update propagate back.
     */
    @Inject(method = "drawScreen(IIF)V", at = @At("HEAD"))
    private void rf$requestServerClearOnHover(int mouseX, int mouseY, float pt, CallbackInfo ci) {
        if (!ModConfig.enableItemPickupStar) return;
        if (inventorySlots == null || inventorySlots.inventorySlots == null) return;

        if (rf$hoverClearDelayTicks > 0) {
            rf$hoverClearDelayTicks--;
            return;
        }

        @SuppressWarnings("rawtypes")
        List slots = inventorySlots.inventorySlots;
        for (Object o : slots) {
            Slot s = (Slot) o;
            final int x0 = guiLeft + s.xDisplayPosition;
            final int y0 = guiTop  + s.yDisplayPosition;

            if (mouseX >= x0 && mouseY >= y0 && mouseX < x0 + 16 && mouseY < y0 + 16) {
                ItemStack st = s.getStack();
                if (st != null && st.hasTagCompound()) {
                    NBTTagCompound tag = st.getTagCompound();
                    if (tag.getBoolean(TAG_NEW)) {
                        // Authoritative clear on the server
                        RFNetwork.CH.sendToServer(new MsgClearPickupTag(inventorySlots.windowId, s.slotNumber));
                    }
                }
                break; // only hovered slot
            }
        }
    }

    /**
     * AFTER glPopMatrix (absolute coords), BEFORE tooltip: draw stars.
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
        List slots = inventorySlots.inventorySlots;
        if (slots.isEmpty()) return;

        Minecraft.getMinecraft().getTextureManager().bindTexture(TEX);

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST); // under tooltip
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glColor4f(1F, 1F, 1F, 1F);

            Tessellator t = Tessellator.instance;
            for (Object o : slots) {
                Slot s = (Slot) o;
                ItemStack st = s.getStack();
                if (st == null) continue;
                NBTTagCompound tag = st.getTagCompound();
                if (tag == null || !tag.getBoolean(TAG_NEW)) continue;

                int x = guiLeft + s.xDisplayPosition;
                int y = guiTop  + s.yDisplayPosition;

                t.startDrawingQuads();
                t.addVertexWithUV(x    , y+16, 0, 0, 1);
                t.addVertexWithUV(x+16 , y+16, 0, 1, 1);
                t.addVertexWithUV(x+16 , y   , 0, 1, 0);
                t.addVertexWithUV(x    , y   , 0, 0, 0);
                t.draw();
            }
        } finally {
            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glPopAttrib();
        }
    }
}
