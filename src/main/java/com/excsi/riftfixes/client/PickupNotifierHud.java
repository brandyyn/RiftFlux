package com.excsi.riftfixes.client;

import com.excsi.riftfixes.ModConfig;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.ArrayList;
import java.util.List;

/**
 * Notifier: eased slide of oldest with jitter-free start/end and a short pause between slides.
 * Timing is unchanged; motion uses sub-pixel rendering + epsilon clamp at both edges.
 */
public final class PickupNotifierHud {

    private static final PickupNotifierHud INSTANCE = new PickupNotifierHud();
    private static final List<Entry> entries = new ArrayList<Entry>();

    private static final int ICON = 16, PAD = 2, TEXT_Y = 5, ROW = ICON + 2;

    private static final int  CYCLE_PAUSE_TICKS = 10; // ~0.5s between slides
    private static       int  pauseTicks = 0;

    // Anti-jitter tuning
    private static final long  START_FREEZE_MS     = 16L;      // hold very first frame
    private static final float EPS_START           = 0.006f;   // snap to 0 if below this
    private static final float EPS_END             = 0.006f;   // snap to 1 if above 1-EPS
    private static final float END_LATCH_THRESHOLD = 1f - EPS_END;

    private static int  secToTicks(float s){ return Math.max(1, Math.round(s*20f)); }
    private static long nowMs()              { return System.nanoTime() / 1_000_000L; }

    private static class Entry {
        ItemStack stack; int count; int ttl; final int slideTicks;
        long  slideStartMs = -1L;   // stamped on first render of the slide
        float visT         = 0f;    // monotonic eased progress (0..1)
        boolean endLatched = false; // keep ghost-removing at the end
        float latchedVisT  = -1f;   // exact eased progress at latch for continuity

        Entry(ItemStack s, int c, int total, int slide){
            stack = s.copy(); stack.stackSize = Math.max(1,c);
            count = Math.max(1,c);
            ttl   = Math.max(1,total);
            slideTicks = Math.max(1, Math.min(slide, total));
        }
        boolean sliding(){ return ttl <= slideTicks; }
        float slideTFromTime(long now){
            if (slideStartMs <= 0) return 0f;
            float durMs = slideTicks * 50f;
            float t = (now - slideStartMs) / durMs;
            return t < 0 ? 0 : (t > 1 ? 1 : t);
        }
        boolean sameKey(ItemStack o){ return o.getItem()==stack.getItem() && o.getItemDamage()==stack.getItemDamage(); }
        String text(){ String n = stack.getDisplayName(); return count>1 ? (n+" x"+count) : n; }
        int color(){
            EnumRarity r = stack.getItem().getRarity(stack);
            if (r == null) return 0xFFFFFF;
            switch (r){ case uncommon: return 0xFFFF55; case rare: return 0x55FFFF; case epic: return 0xFF55FF; default: return 0xFFFFFF; }
        }
    }

    public static void enqueue(ItemStack display, int pickedCount){
        if (!ModConfig.enablePickupNotifier) return;

        final int total = secToTicks(ModConfig.pickupNotifyDurationSeconds);
        final int slide = secToTicks(ModConfig.pickupNotifyFadeSeconds);
        final int merge = secToTicks(ModConfig.pickupNotifyMergeWindowSeconds);

        if (!entries.isEmpty()){
            Entry last = entries.get(entries.size()-1);
            if (last.sameKey(display) && last.ttl > (total - merge)) {
                last.count += Math.max(1, pickedCount);
                last.ttl = total;
                return;
            }
        }

        final int cap = Math.max(1, ModConfig.pickupNotifyMaxEntries);
        while (entries.size() >= cap) entries.remove(0);

        entries.add(new Entry(display, Math.max(1,pickedCount), total, slide));
    }

    public static void bootstrap(){
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        FMLCommonHandler.instance().bus().register(INSTANCE);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e){
        if (!ModConfig.enablePickupNotifier) return;
        if (e.phase != TickEvent.Phase.END) return;

        if (pauseTicks > 0) { pauseTicks--; return; }
        if (entries.isEmpty()) return;

        Entry first = entries.get(0);
        if (--first.ttl <= 0) {
            if (first.slideStartMs > 0L) {
                long elapsed = nowMs() - first.slideStartMs;
                long need    = (long)(first.slideTicks * 50L);
                if (elapsed >= need) {
                    entries.remove(0);
                    pauseTicks = CYCLE_PAUSE_TICKS;
                } else {
                    first.ttl = 0; // hold until render finishes motion
                }
            } else {
                first.ttl = 0;
            }
        }
    }

    private static float clamp01(float v){ return v < 0 ? 0 : (v > 1 ? 1 : v); }
    private static float smootherstep(float t){
        t = clamp01(t);
        return t*t*t * (t*(t*6f - 15f) + 10f);
    }
    private static float epsilonSnap01(float t){
        if (t <= EPS_START) return 0f;
        if (t >= 1f - EPS_END) return 1f;
        return t;
    }

    @SubscribeEvent
    public void onHud(RenderGameOverlayEvent.Post e){
        if (!ModConfig.enablePickupNotifier) return;
        if (e.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if (entries.isEmpty()) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        final int sw = sr.getScaledWidth(), sh = sr.getScaledHeight();
        final int right = sw - 4;
        final long now = nowMs();

        // Progress for the oldest + latching with epsilon snap
        if (pauseTicks == 0 && !entries.isEmpty()) {
            Entry first = entries.get(0);
            if (first.sliding()) {
                if (first.slideStartMs <= 0L) first.slideStartMs = now;
                float tRaw = first.slideTFromTime(now);

                // Freeze the first frame
                if (now - first.slideStartMs <= START_FREEZE_MS) tRaw = 0f;

                // Easing + epsilon clamp + monotonic
                float eased = smootherstep(tRaw);
                eased = epsilonSnap01(eased);
                if (eased < first.visT) eased = first.visT;
                first.visT = eased;

                if (!first.endLatched && first.visT >= END_LATCH_THRESHOLD) {
                    first.endLatched = true;
                    first.latchedVisT = first.visT; // freeze others exactly here
                }
            }
        }

        final boolean ghostRemove = (!entries.isEmpty() && entries.get(0).endLatched);
        final float   visT        = entries.isEmpty() ? 0f : entries.get(0).visT;
        final float   rowShiftDyn = visT * ROW;
        final float   rowShiftFrozen = (!entries.isEmpty() && entries.get(0).latchedVisT >= 0f)
                ? entries.get(0).latchedVisT * ROW : rowShiftDyn;

        RenderItem ri = new RenderItem();

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);

            for (int i=0;i<entries.size();i++){
                Entry en = entries.get(i);

                // Skip drawing the oldest during latched end-phase (already off-screen)
                if (i == 0 && ghostRemove) continue;

                String text = en.text();
                int textW = mc.fontRenderer.getStringWidth(text);
                int w = ICON + PAD + textW;
                int x = right - w;

                if (!ghostRemove) {
                    // Normal: oldest slides to off-screen; others track with rowShiftDyn
                    if (i == 0) {
                        int baseY = sh - 4 - i * ROW;
                        int off   = sh + ICON + 4;
                        float y   = baseY + (off - baseY) * visT;
                        int yBase = (int)Math.floor(y);
                        float yFrac = y - yBase;

                        GL11.glPushMatrix();
                        GL11.glTranslatef(0f, yFrac, 0f);

                        RenderHelper.enableGUIStandardItemLighting();
                        ri.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), en.stack, x, yBase - ICON);
                        RenderHelper.disableStandardItemLighting();
                        mc.fontRenderer.drawString(text, x + ICON + PAD, yBase - ICON + TEXT_Y, en.color(), false);

                        GL11.glPopMatrix();
                    } else {
                        int baseY = sh - 4 - i * ROW;
                        float y = baseY + rowShiftDyn;
                        int yBase = (int)Math.floor(y);
                        float yFrac = y - yBase;

                        GL11.glPushMatrix();
                        GL11.glTranslatef(0f, yFrac, 0f);

                        RenderHelper.enableGUIStandardItemLighting();
                        ri.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), en.stack, x, yBase - ICON);
                        RenderHelper.disableStandardItemLighting();
                        mc.fontRenderer.drawString(text, x + ICON + PAD, yBase - ICON + TEXT_Y, en.color(), false);

                        GL11.glPopMatrix();
                    }
                } else {
                    // End-latched: render as if oldest is removed, frozen at latch continuity
                    int logicalIndex = (i > 0) ? (i - 1) : 0;
                    int baseY = sh - 4 - logicalIndex * ROW;
                    float y = baseY + (rowShiftFrozen - ROW);
                    int yBase = (int)Math.floor(y);
                    float yFrac = y - yBase;

                    GL11.glPushMatrix();
                    GL11.glTranslatef(0f, yFrac, 0f);

                    RenderHelper.enableGUIStandardItemLighting();
                    ri.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), en.stack, x, yBase - ICON);
                    RenderHelper.disableStandardItemLighting();
                    mc.fontRenderer.drawString(text, x + ICON + PAD, yBase - ICON + TEXT_Y, en.color(), false);

                    GL11.glPopMatrix();
                }
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_BLEND);
        } finally {
            GL11.glColor4f(1,1,1,1);
            GL11.glPopAttrib();
        }
    }
}
