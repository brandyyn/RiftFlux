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

public final class PickupNotifierHud {

    private static final PickupNotifierHud INSTANCE = new PickupNotifierHud();
    private static final List<Entry> entries = new ArrayList<Entry>();

    private static final int ICON = 16, PAD = 2, TEXT_Y = 5, ROW = ICON + 2;

    private static final int  CYCLE_PAUSE_TICKS = 10;
    private static       int  pauseTicks = 0;

    private static final int FIRST_DELAY_TICKS = 8;
    private static int firstDelayTicks = 0;

    private static int  secToTicks(float s){ return Math.max(1, Math.round(s*20f)); }
    private static long nowMs()              { return System.nanoTime() / 1_000_000L; }

    private static class Entry {
        ItemStack stack; int count; int ttl; final int slideTicks;
        boolean slidingActive = false;
        long  slideStartMs    = -1L;
        float visT            = 0f;

        Entry(ItemStack s, int c, int total, int slide){
            stack = s.copy();
            stack.stackSize = Math.max(1, c);
            count = Math.max(1, c);
            ttl   = Math.max(1, total);
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
            if (o == null) return false;
            if (o.getItem() != stack.getItem()) return false;
            if (o.getItemDamage() != stack.getItemDamage()) return false;
            return ItemStack.areItemStackTagsEqual(o, stack);
        }

        String text() {
            String n = stack.getDisplayName();

            if (n.indexOf('\u00A7') < 0) {
                EnumRarity r = stack.getItem().getRarity(stack);
                if (r != null && r.rarityColor != null) {
                    n = r.rarityColor.toString() + n;
                }
            }

            return count > 1 ? (n + " x" + count) : n;
        }

        int color(){
            return 0xFFFFFF;
        }
    }

    private static int findExistingIndex(ItemStack key) {
        for (int i = entries.size() - 1; i >= 0; i--) {
            if (entries.get(i).sameKey(key)) return i;
        }
        return -1;
    }

    private static void resetAndRearm(Entry e, int refreshedTtl) {
        e.slidingActive = false;
        e.slideStartMs  = -1L;
        e.visT          = 0f;
        e.ttl           = Math.max(1, refreshedTtl);
    }

    public static void enqueue(ItemStack display, int pickedCount) {
        if (!ModConfig.enablePickupNotifier) return;

        final int total = secToTicks(ModConfig.pickupNotifyDurationSeconds);
        final int slide = secToTicks(ModConfig.pickupNotifyFadeSeconds);
        final int cap   = Math.max(1, ModConfig.pickupNotifyMaxEntries);
        final boolean wasEmpty = entries.isEmpty();
        final int add = Math.max(1, pickedCount);

        int idx = findExistingIndex(display);
        if (idx >= 0) {
            final Entry e = entries.get(idx);
            e.count += add;

            e.stack = display.copy();
            e.stack.stackSize = e.count;

            if (e.slidingActive || e.visT > 0f) {
                resetAndRearm(e, total);
            } else {
                e.ttl = total;
            }

            // 🔥 IMPORTANT FIX —
            // When a merged item jumps to top, force a delay like a new pickup
            firstDelayTicks = FIRST_DELAY_TICKS;
            pauseTicks = CYCLE_PAUSE_TICKS;

            // Move to top
            entries.remove(idx);
            entries.add(e);
            return;
        }

        if (entries.size() >= cap) {
            for (int i = 1; i < entries.size(); i++) {
                entries.get(i).ttl = 0;
            }
        }

        entries.add(new Entry(display.copy(), add, total, slide));
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

        for (Entry en : entries) if (en.ttl > 0) en.ttl--;

        if (firstDelayTicks > 0) {
            entries.get(0).ttl++;
            firstDelayTicks--;
        }

        if (pauseTicks > 0) { pauseTicks--; return; }

        Entry first = entries.get(0);

        if (first.shouldStartSliding()) {
            first.slidingActive = true;
            first.slideStartMs  = now;
            first.visT          = 0f;
        }

        if (first.slidingActive) {
            long elapsed = now - first.slideStartMs;
            long need    = (long)(first.slideTicks * 50L);
            if (elapsed >= need) {
                entries.remove(0);
                pauseTicks = CYCLE_PAUSE_TICKS;
            }
        }
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

        Entry first = entries.get(0);
        float visT;
        if (pauseTicks == 0) {
            float tRaw = first.slideTFromNow(now);
            float eased = smootherstep(tRaw);
            if (eased < first.visT) eased = first.visT;
            first.visT = eased;
        }
        visT = first.visT;
        float rowShift = visT * ROW;

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
                int textW = mc.fontRenderer.getStringWidth(text);
                int w = ICON + PAD + textW;
                int x = right - w;

                int baseY = sh - 4 - i * ROW;
                float y = (i == 0)
                        ? baseY + ((sh + ICON + 4) - baseY) * visT
                        : baseY + rowShift;

                int yBase = stableFloor(y);
                float yFrac = y - yBase;

                GL11.glPushMatrix();
                GL11.glTranslatef(0f, yFrac, 0f);

                RenderHelper.enableGUIStandardItemLighting();
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                ri.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), en.stack, x, yBase - ICON);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                RenderHelper.disableStandardItemLighting();

                mc.fontRenderer.drawString(text, x + ICON + PAD, yBase - ICON + TEXT_Y, en.color(), false);

                GL11.glPopMatrix();
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
