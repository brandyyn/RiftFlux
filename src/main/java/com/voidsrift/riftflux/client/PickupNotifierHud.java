package com.voidsrift.riftflux.client;

import com.voidsrift.riftflux.ModConfig;
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
 * Pickup notifier HUD with stable sliding + safe promotions.
 *
 * - New/merged entries end up at the TOP OF DISPLAY (end of list).
 * - If the oldest row is mid-slide and a NEW item arrives, the oldest snaps fully back on-screen
 *   and is promoted to TOP (next tick).
 * - If a row that HAS BEGUN SLIDING gets merged (count updated), we cancel its previous slide,
 *   reset its timer, refresh TTL, and then promote — so it won't "insta-expire" later.
 * - Only the oldest row ever removes itself (after the slide finishes).
 * - No structural removals during enqueue; overflow marks a non-oldest row to expire later.
 */
public final class PickupNotifierHud {

    private static final PickupNotifierHud INSTANCE = new PickupNotifierHud();
    private static final List<Entry> entries = new ArrayList<Entry>();

    private static final int ICON = 16, PAD = 2, TEXT_Y = 5, ROW = ICON + 2;

    private static final int  CYCLE_PAUSE_TICKS = 10; // ~0.5s between slides
    private static       int  pauseTicks = 0;

    private static final int FIRST_DELAY_TICKS = 8; // ~0.4s dwell for first in a burst
    private static int firstDelayTicks = 0;

    private static int  secToTicks(float s){ return Math.max(1, Math.round(s*20f)); }
    private static long nowMs()              { return System.nanoTime() / 1_000_000L; }

    private static class Entry {
        ItemStack stack; int count; int ttl; final int slideTicks;
        // Slide state
        boolean slidingActive = false;
        long  slideStartMs    = -1L;
        float visT            = 0f;

        Entry(ItemStack s, int c, int total, int slide){
            stack = s.copy(); stack.stackSize = Math.max(1,c);
            count = Math.max(1,c);
            ttl   = Math.max(1,total);
            slideTicks = Math.max(1, Math.min(slide, total));
        }
        boolean shouldStartSliding(){ return ttl <= 0 && !slidingActive; }
        float slideTFromNow(long now){
            if (!slidingActive || slideStartMs <= 0L) return 0f;
            float durMs = slideTicks * 50f;
            float t = (now - slideStartMs) / durMs;
            return t < 0 ? 0 : (t > 1 ? 1 : t);
        }
        boolean sameKey(ItemStack o){
            return o.getItem()==stack.getItem() && o.getItemDamage()==stack.getItemDamage();
        }
        String text(){ String n = stack.getDisplayName(); return count>1 ? (n+" x"+count) : n; }
        int color(){
            EnumRarity r = stack.getItem().getRarity(stack);
            if (r == null) return 0xFFFFFF;
            switch (r){ case uncommon: return 0xFFFF55; case rare: return 0x55FFFF; case epic: return 0xFF55FF; default: return 0xFFFFFF; }
        }
    }

    // Pending promotions applied at a safe point (end of client tick)
    private static final ArrayList<Entry> pendingPromotions = new ArrayList<Entry>();

    private static int findExistingIndex(ItemStack key) {
        for (int i = entries.size() - 1; i >= 0; i--) {
            if (entries.get(i).sameKey(key)) return i;
        }
        return -1;
    }

    /** Oldest (smallest ttl) row, but never the bottom row (index 0). */
    private static int indexOfOldestExcludingBottom() {
        int best = -1, bestTtl = Integer.MAX_VALUE;
        for (int i = 1; i < entries.size(); i++) { // start at 1 to exclude bottom row
            final Entry e = entries.get(i);
            if (e.ttl < bestTtl) { bestTtl = e.ttl; best = i; }
        }
        return best;
    }

    private static void schedulePromotion(Entry e) {
        if (!pendingPromotions.contains(e)) pendingPromotions.add(e);
    }

    /** Pop oldest fully back on-screen & schedule promotion to TOP; refresh TTL so it doesn't instantly slide again. */
    private static void popAndPromoteOldest(int refreshedTtl) {
        if (entries.isEmpty()) return;
        final Entry first = entries.get(0);
        if (first.slidingActive || first.visT > 0f) {
            first.slidingActive = false;
            first.slideStartMs  = -1L;
            first.visT          = 0f;
        }
        first.ttl = Math.max(1, refreshedTtl);
        schedulePromotion(first);
    }

    /** Cancel slide & re-arm TTL for a specific entry (used when a sliding row is merged). */
    private static void resetAndRearm(Entry e, int refreshedTtl) {
        e.slidingActive = false;
        e.slideStartMs  = -1L;
        e.visT          = 0f;
        e.ttl           = Math.max(1, refreshedTtl);
    }

    /** Add/merge an entry. Never removes here; promotion is deferred to tick safe point. */
    public static void enqueue(ItemStack display, int pickedCount) {
        if (!ModConfig.enablePickupNotifier) return;

        final int total = secToTicks(ModConfig.pickupNotifyDurationSeconds);
        final int slide = secToTicks(ModConfig.pickupNotifyFadeSeconds);
        final int cap   = Math.max(1, ModConfig.pickupNotifyMaxEntries);
        final boolean wasEmpty = entries.isEmpty();
        final int add = Math.max(1, pickedCount);

        // MERGE with existing row (anywhere)
        int idx = findExistingIndex(display);
        if (idx >= 0) {
            final Entry e = entries.get(idx);
            e.count += add;

            // ✅ If that row has begun sliding at any point, cancel its old slide & re-arm TTL
            if (e.slidingActive || e.visT > 0f) {
                resetAndRearm(e, total);
            } else {
                e.ttl = total; // normal refresh
            }
            schedulePromotion(e);    // promote to top at safe point
            return;
        }

        // NEW ROW path
        if (entries.size() >= cap) {
            int drop = indexOfOldestExcludingBottom();
            if (drop >= 0) entries.get(drop).ttl = 0; // will slide when it becomes oldest
        }

        // Insert brand-new row at TOP OF DISPLAY = END OF LIST
        entries.add(new Entry(display.copy(), add, total, slide));

        // If the bottom/oldest is sliding, pop it fully back and promote it to TOP too
        if (entries.size() > 1) {
            popAndPromoteOldest(total);
        }

        if (wasEmpty) firstDelayTicks = FIRST_DELAY_TICKS;
    }

    public static void bootstrap(){
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        FMLCommonHandler.instance().bus().register(INSTANCE);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e){
        if (!ModConfig.enablePickupNotifier) return;
        if (e.phase != TickEvent.Phase.END) return;
        if (entries.isEmpty()) return;

        final long now = nowMs();

        // TTL runs for ALL entries every tick (non-oldest rows are NOT removed on ttl<=0)
        for (int i = 0; i < entries.size(); i++) {
            Entry en = entries.get(i);
            if (en.ttl > 0) en.ttl--;
        }

        // Small extra dwell for the first in a new burst
        if (firstDelayTicks > 0 && !entries.isEmpty()) {
            entries.get(0).ttl++;
            firstDelayTicks--;
        }

        if (pauseTicks > 0) { pauseTicks--; applyPromotionsSafe(); return; }

        // Oldest row slide lifecycle (only row that slides/removes)
        if (!entries.isEmpty()) {
            Entry first = entries.get(0);

            // Start slide when TTL hits 0
            if (first.shouldStartSliding()) {
                first.slidingActive = true;
                first.slideStartMs  = now;
                first.visT          = 0f;
            }

            // Remove after full duration
            if (first.slidingActive) {
                long elapsed = now - first.slideStartMs;
                long need    = (long)(first.slideTicks * 50L);
                if (elapsed >= need) {
                    entries.remove(0);
                    pauseTicks = CYCLE_PAUSE_TICKS;
                }
            }
        }

        // Apply queued promotions AFTER any removal (safe point).
        applyPromotionsSafe();
    }

    /** Move promoted rows to TOP OF DISPLAY (end of list) without touching animation fields. */
    private static void applyPromotionsSafe() {
        if (pendingPromotions.isEmpty()) return;
        for (int k = 0; k < pendingPromotions.size(); k++) {
            final Entry e = pendingPromotions.get(k);
            final int i = entries.indexOf(e);
            if (i >= 0 && i != entries.size() - 1) {
                entries.remove(i);
                entries.add(e); // top of display = end of list
            }
        }
        pendingPromotions.clear();
    }

    private static int stableFloor(float y){
        return (int)Math.floor(y + 1.0e-4f);
    }
    private static float clamp01(float v){ return v < 0 ? 0 : (v > 1 ? 1 : v); }
    private static float smootherstep(float t){
        t = clamp01(t);
        return t*t*t * (t*(t*6f - 15f) + 10f);
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

        // Update visual progress for the oldest row
        if (pauseTicks == 0 && !entries.isEmpty()) {
            Entry first = entries.get(0);
            float tRaw = first.slideTFromNow(now);
            float eased = smootherstep(tRaw);
            if (eased < first.visT) eased = first.visT;
            first.visT = eased;
        }

        final float visT = entries.isEmpty() ? 0f : entries.get(0).visT;
        final float rowShift = visT * ROW;

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

                String text = en.text();
                int textW = Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
                int w = ICON + PAD + textW;
                int x = right - w;

                if (i == 0) {
                    int baseY = sh - 4 - i * ROW;
                    int off   = sh + ICON + 4;
                    float y   = baseY + (off - baseY) * visT;
                    int yBase = stableFloor(y);
                    float yFrac = y - yBase;

                    GL11.glPushMatrix();
                    GL11.glTranslatef(0f, yFrac, 0f);

                    RenderHelper.enableGUIStandardItemLighting();
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    ri.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), en.stack, x, yBase - ICON);
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    RenderHelper.disableStandardItemLighting();

                    Minecraft.getMinecraft().fontRenderer.drawString(text, x + ICON + PAD, yBase - ICON + TEXT_Y, en.color(), false);
                    GL11.glPopMatrix();
                } else {
                    int baseY = sh - 4 - i * ROW;
                    float y = baseY + rowShift;
                    int yBase = stableFloor(y);
                    float yFrac = y - yBase;

                    GL11.glPushMatrix();
                    GL11.glTranslatef(0f, yFrac, 0f);

                    RenderHelper.enableGUIStandardItemLighting();
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    ri.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), en.stack, x, yBase - ICON);
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    RenderHelper.disableStandardItemLighting();

                    Minecraft.getMinecraft().fontRenderer.drawString(text, x + ICON + PAD, yBase - ICON + TEXT_Y, en.color(), false);
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
