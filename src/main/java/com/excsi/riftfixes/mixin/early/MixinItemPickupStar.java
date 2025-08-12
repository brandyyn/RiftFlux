package com.excsi.riftfixes.mixin.early;

import com.excsi.riftfixes.Constants;
import com.excsi.riftfixes.ModConfig;
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
 * Draw pickup stars right after GL11.glPopMatrix() inside drawScreen:
 *  • Always runs (even if a subclass overrides foreground)
 *  • Absolute coords (guiLeft/guiTop + slot pos)
 *  • Tooltip renders after this, so tooltip is always on top
 * Hovered slot is cleared at HEAD so it never shows a star under the tooltip.
 */
@Mixin(GuiContainer.class)
public abstract class MixinItemPickupStar {

    @Shadow
    protected int guiLeft;
    @Shadow
    protected int guiTop;
    @Shadow
    public Container inventorySlots;

    @Unique
    private static final String TAG = "riftfixes_new";
    @Unique
    private static final ResourceLocation TEX =
            new ResourceLocation(Constants.MODID, "textures/gui/pickup_star.png"); // 16x16

    /**
     * Clear ONLY the hovered slot’s star tag so that slot never shows a star under the tooltip.
     */
    @Inject(method = "drawScreen(IIF)V", at = @At("HEAD"))
    private void rf$clearHovered(int mouseX, int mouseY, float pt, CallbackInfo ci) {
        if (!ModConfig.enableItemPickupStar) return;
        if (inventorySlots == null || inventorySlots.inventorySlots == null) return;

        @SuppressWarnings("rawtypes")
        List slots = inventorySlots.inventorySlots;
        for (Object o : slots) {
            Slot s = (Slot) o;
            int x0 = guiLeft + s.xDisplayPosition;
            int y0 = guiTop + s.yDisplayPosition;
            if (mouseX >= x0 && mouseY >= y0 && mouseX < x0 + 16 && mouseY < y0 + 16) {
                ItemStack st = s.getStack();
                if (st != null && st.hasTagCompound()) {
                    NBTTagCompound nbt = st.getTagCompound();
                    if (nbt.getBoolean(TAG)) {
                        nbt.removeTag(TAG);
                        if (nbt.hasNoTags()) st.setTagCompound(null);
                    }
                }
                break; // only the hovered slot
            }
        }
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
        java.util.List slots = inventorySlots.inventorySlots;
        if (slots.isEmpty()) return;

        Minecraft.getMinecraft().getTextureManager().bindTexture(
                new net.minecraft.util.ResourceLocation(Constants.MODID, "textures/gui/pickup_star.png")
        );

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

            net.minecraft.client.renderer.Tessellator t = net.minecraft.client.renderer.Tessellator.instance;
            for (Object o : slots) {
                net.minecraft.inventory.Slot s = (net.minecraft.inventory.Slot) o;
                net.minecraft.item.ItemStack st = s.getStack();
                if (st == null) continue;
                net.minecraft.nbt.NBTTagCompound tag = st.getTagCompound();
                if (tag == null || !tag.getBoolean("riftfixes_new")) continue;

                final int x = guiLeft + s.xDisplayPosition; // absolute coords here
                final int y = guiTop + s.yDisplayPosition;

                t.startDrawingQuads();
                t.addVertexWithUV(x, y + 16, 0, 0, 1);
                t.addVertexWithUV(x + 16, y + 16, 0, 1, 1);
                t.addVertexWithUV(x + 16, y, 0, 1, 0);
                t.addVertexWithUV(x, y, 0, 0, 0);
                t.draw();
            }
        } finally {
            // restore everything and ensure no lingering tint
            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glPopAttrib();
        }
    }

}