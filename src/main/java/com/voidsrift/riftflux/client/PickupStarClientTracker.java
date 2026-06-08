package com.voidsrift.riftflux.client;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Locale;

public final class PickupStarClientTracker {

    private static final String TAG_NEW  = "riftflux_new";
    private static final int WINDOW_TICKS = 40; // ~2s

    // 36-slot main inv baseline
    private static SlotSnapshot[] baselineMain = null;
    private static int[] recentMainTtl = null;

    // Any open container baseline (for modded inventories)
    private static SlotSnapshot[] baselineCont = null;
    private static Container   lastCont     = null;

    // recent pickups queue (item+meta, wildcard for damageables)
    private static final Deque<PickupKey> queue = new ArrayDeque<PickupKey>();
    private static boolean bootstrapped;

    private static final class PickupKey {
        final Item item; final int meta; final boolean wildcardMeta; int ttl;
        PickupKey(Item i, int m, boolean wildcardMeta, int ttl){
            item=i; meta=m; this.wildcardMeta = wildcardMeta; this.ttl=ttl;
        }
        boolean matches(ItemStack s){
            if (s == null) return false;
            if (s.getItem() != item) return false;
            if (wildcardMeta || s.isItemStackDamageable()) return true; // tools/armor: any damage value
            return s.getItemDamage() == meta;
        }
    }

    private static final class SlotSnapshot {
        final Item item;
        final int meta;
        final int size;

        SlotSnapshot(ItemStack stack) {
            this.item = stack.getItem();
            this.meta = stack.getItemDamage();
            this.size = stack.stackSize;
        }

        boolean sameItem(ItemStack stack) {
            return stack != null && stack.getItem() == item && stack.getItemDamage() == meta;
        }
    }

    /** Preferred enqueue (from packet): full stack */
    public static void enqueue(ItemStack st){
        if (st == null || st.getItem() == null) return;
        final boolean wildcard = st.isItemStackDamageable();
        queue.addLast(new PickupKey(st.getItem(), st.getItemDamage(), wildcard, WINDOW_TICKS));
    }

    /** Legacy helpers kept for old call sites */
    public static void enqueue(String registryName, int meta){
        Item it = (Item) Item.itemRegistry.getObject(registryName);
        if (it != null) {
            final boolean wildcard = it.isDamageable(); // 1.7.10: maxDamage > 0
            queue.addLast(new PickupKey(it, meta, wildcard, WINDOW_TICKS));
        }
    }
    public static void enqueue(String registryName, int meta, int ignoredLegacyId){
        enqueue(registryName, meta);
    }

    public static void bootstrap(){
        if (bootstrapped) {
            return;
        }
        bootstrapped = true;
        cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(new PickupStarClientTracker());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent evt){
        if (!ModConfig.enableItemPickupStar) {
            reset();
            return;
        }
        if (evt.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer p = (mc != null) ? mc.thePlayer : null;
        if (p == null) {
            reset();
            return;
        }

        // decay queue
        if (!queue.isEmpty()){
            for (PickupKey pk : queue) pk.ttl--;
            while (!queue.isEmpty() && queue.peekFirst().ttl <= 0) queue.removeFirst();
        }

        // --- MAIN INVENTORY (36) ---
        ItemStack[] cur = p.inventory.mainInventory;
        if (cur != null) {
            if (baselineMain == null || baselineMain.length != cur.length || recentMainTtl == null || recentMainTtl.length != cur.length){
                baselineMain = new SlotSnapshot[cur.length];
                recentMainTtl = new int[cur.length];
                for (int i=0;i<cur.length;i++) baselineMain[i] = snapshot(cur[i]);
            } else {
                for (int i=0;i<recentMainTtl.length;i++){
                    if (recentMainTtl[i] > 0) recentMainTtl[i]--;
                }
                for (int i=0;i<cur.length;i++){
                    ItemStack now = cur[i];
                    SlotSnapshot was = baselineMain[i];

                    if (now == null) {
                        recentMainTtl[i] = 0;
                        baselineMain[i] = null;
                        continue;
                    }

                    boolean sameItem = (was != null && was.sameItem(now));
                    boolean grew     = sameItem && now.stackSize > was.size;
                    boolean inserted = (now != null && was == null) || (now != null && was != null && !sameItem);

                    if (!sameItem) {
                        recentMainTtl[i] = 0;
                    }

                    // IMPORTANT: do NOT tag on "inserted" for damageables here — that causes
                    // stars to reappear on shift-click moves. We only tag on actual growth
                    // (merge) or when correlating with the pickup queue.
                    if (ModConfig.itemPickupStarOnStackIncrease && grew) {
                        NBTTagCompound tag = getOrCreate(now);
                        
                        tag.setBoolean(TAG_NEW, true);
                        now.setTagCompound(tag);
                        recentMainTtl[i] = WINDOW_TICKS;
                    } else if (inserted && matchesAnyPickup(now)) {
                        NBTTagCompound tag = getOrCreate(now);
                        if (!tag.getBoolean(TAG_NEW)) {
                            tag.setBoolean(TAG_NEW, true);
                            now.setTagCompound(tag);
                        }
                        recentMainTtl[i] = WINDOW_TICKS;
                    }

                    if (recentMainTtl[i] > 0) {
                        NBTTagCompound tag = now.getTagCompound();
                        if (tag == null || !tag.getBoolean(TAG_NEW)) {
                            tag = getOrCreate(now);
                            tag.setBoolean(TAG_NEW, true);
                            now.setTagCompound(tag);
                        }
                    }

                    baselineMain[i] = snapshot(now);
                }
            }
        }

        // --- ANY OPEN CONTAINER (vanilla + modded) ---
        if (mc.currentScreen instanceof GuiContainer) {
            Container cont = p.openContainer;
            if (cont != null && cont.inventorySlots != null) {
                @SuppressWarnings("rawtypes")
                List slots = cont.inventorySlots;

                if (cont != lastCont || baselineCont == null || baselineCont.length != slots.size()) {
                    baselineCont = new SlotSnapshot[slots.size()];
                    for (int i=0;i<slots.size();i++) {
                        Slot s = (Slot) slots.get(i);
                        baselineCont[i] = snapshot(s.getStack());
                    }
                    lastCont = cont;
                } else {
                    for (int i=0;i<slots.size();i++) {
                        Slot s = (Slot) slots.get(i);
                        ItemStack now = s.getStack();
                        SlotSnapshot was = baselineCont[i];
                        boolean isPlayerSlot = isPlayerOwnedSlot(s, p);

                        if (!isPlayerSlot && ModConfig.itemPickupStarClearOnLeaveInventory) {
                            clearStarTagLocal(now);
                            baselineCont[i] = snapshot(now);
                            continue;
                        }

                        boolean sameItem = (was != null && was.sameItem(now));
                        boolean grew     = sameItem && now.stackSize > was.size;
                        boolean inserted = (now != null && was == null) || (now != null && was != null && !sameItem);

                        // Same rule as main inv: no unconditional damageable tagging on insert.
                        if (now != null) {
                            if (ModConfig.itemPickupStarOnStackIncrease && grew) {
                                NBTTagCompound tag = getOrCreate(now);
                                
                                tag.setBoolean(TAG_NEW, true);
                                now.setTagCompound(tag);
                            } else if (inserted && matchesAnyPickup(now)) {
                                NBTTagCompound tag = getOrCreate(now);
                                if (!tag.getBoolean(TAG_NEW)) {
                                    tag.setBoolean(TAG_NEW, true);
                                    now.setTagCompound(tag);
                                }
                            }
                        }

                        baselineCont[i] = snapshot(now);
                    }
                }
            } else {
                baselineCont = null;
                lastCont = null;
            }
        } else {
            baselineCont = null;
            lastCont = null;
        }

        if (ModConfig.itemPickupStarClearHeldItem) {
            int heldIndex = p.inventory.currentItem;
            if (heldIndex >= 0 && heldIndex < p.inventory.mainInventory.length) {
                clearStarTagLocal(p.inventory.mainInventory[heldIndex], heldIndex);
            }
        }
    }

    private static boolean matchesAnyPickup(ItemStack s){
        if (queue.isEmpty()) return false;
        for (PickupKey pk : queue) if (pk.matches(s)) return true;
        return false;
    }

    private static NBTTagCompound getOrCreate(ItemStack st){
        NBTTagCompound tag = st.getTagCompound();
        if (tag == null) return new NBTTagCompound();
        return (NBTTagCompound) tag.copy();
    }

    private static void clearStarTagLocal(ItemStack st) {
        if (st == null) return;
        NBTTagCompound tag = st.getTagCompound();
        if (tag == null || !tag.getBoolean(TAG_NEW)) return;
        NBTTagCompound copy = (NBTTagCompound) tag.copy();
        copy.removeTag(TAG_NEW);
        if (copy.hasNoTags()) {
            st.setTagCompound(null);
        } else {
            st.setTagCompound(copy);
        }
    }

    private static void clearStarTagLocal(ItemStack st, int mainIndex) {
        clearStarTagLocal(st);
        clearRecentMain(mainIndex);
    }

    public static void clearRecentMain(int mainIndex) {
        if (recentMainTtl == null) return;
        if (mainIndex < 0 || mainIndex >= recentMainTtl.length) return;
        recentMainTtl[mainIndex] = 0;
    }

    private static void reset() {
        baselineMain = null;
        recentMainTtl = null;
        baselineCont = null;
        lastCont = null;
        queue.clear();
    }

    private static SlotSnapshot snapshot(ItemStack stack) {
        return stack != null ? new SlotSnapshot(stack) : null;
    }

    private static boolean isPlayerOwnedSlot(Slot s, EntityPlayer p) {
        if (s == null || s.inventory == null) return false;
        if (s.inventory == p.inventory) return true;
        String name = null;
        try {
            name = s.inventory.getInventoryName();
        } catch (Throwable ignored) {
        }
        if (name == null) return false;
        String lower = name.toLowerCase(Locale.ROOT);
        return lower.contains("satchel") || lower.contains("backpack");
    }
}
