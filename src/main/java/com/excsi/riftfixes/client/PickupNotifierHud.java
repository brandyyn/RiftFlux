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
 * Notifier: constant-speed slide of oldest, small pause between slides.
 */
public final class PickupNotifierHud {

    private static final PickupNotifierHud INSTANCE = new PickupNotifierHud();
    private static final List<Entry> entries = new ArrayList<Entry>();

    private static final int ICON = 16, PAD = 2, TEXT_Y = 5, ROW = ICON + 2;

    // Small pause (in ticks) after an entry leaves before the next starts sliding
    private static final int CYCLE_PAUSE_TICKS = 10; // ~0.5s
    private static int pauseTicks = 0;

    private static int secToTicks(float s){ return Math.max(1, Math.round(s*20f)); }

    private static class Entry {
        ItemStack stack; int count; int ttl; final int slideTicks;
        Entry(ItemStack s, int c, int total, int slide){
            stack = s.copy(); stack.stackSize = Math.max(1,c);
            count = Math.max(1,c);
            ttl   = Math.max(1,total);
            slideTicks = Math.max(1, Math.min(slide, total));
        }
        boolean sliding(){ return ttl <= slideTicks; }
        // Linear 0..1 for constant speed
        float slideT(float pt){ float t = 1f - ((ttl - pt) / (float)slideTicks); return t<0?0:t>1?1:t; }
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

    /** Oldest counts down; when it ends, insert a short pause before next slide starts. */
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e){
        if (!ModConfig.enablePickupNotifier) return;
        if (e.phase != TickEvent.Phase.END) return;

        if (pauseTicks > 0) { pauseTicks--; return; }

        if (!entries.isEmpty()){
            Entry first = entries.get(0);
            if (--first.ttl <= 0) {
                entries.remove(0);
                pauseTicks = CYCLE_PAUSE_TICKS;
            }
        }
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

        float t = 0f;
        if (pauseTicks == 0 && !entries.isEmpty() && entries.get(0).sliding()) {
            t = entries.get(0).slideT(e.partialTicks); // 0..1, linear
        }
        float rowShift = t * ROW;

        RenderItem ri = new RenderItem();

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);

            for (int i=0;i<entries.size();i++){
                Entry en = entries.get(i);
                String text = en.text();
                int textW = mc.fontRenderer.getStringWidth(text);
                int w = ICON + PAD + textW;
                int x = right - w;

                int baseY = sh - 4 - i * ROW;
                float yDraw;

                if (i == 0) {
                    // Oldest slides only during slide window (constant speed)
                    int off = sh + ICON + 4;
                    yDraw = baseY + (off - baseY) * t;
                } else {
                    // Others slide down exactly one row during that same window
                    yDraw = baseY + rowShift;
                }

                RenderHelper.enableGUIStandardItemLighting();
                ri.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), en.stack, x, Math.round(yDraw) - ICON);
                RenderHelper.disableStandardItemLighting();

                mc.fontRenderer.drawString(text, x + ICON + PAD, Math.round(yDraw) - ICON + TEXT_Y, en.color(), false);
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_BLEND);
        } finally {
            GL11.glColor4f(1,1,1,1);
            GL11.glPopAttrib();
        }
    }
}
