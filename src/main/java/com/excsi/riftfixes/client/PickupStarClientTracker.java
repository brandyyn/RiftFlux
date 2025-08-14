package com.excsi.riftfixes.client;

import com.excsi.riftfixes.ModConfig;
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

public final class PickupStarClientTracker {

    private static final String TAG_NEW  = "riftfixes_new";
    private static final String TAG_SEEN = "riftfixes_seen";
    private static final int WINDOW_TICKS = 40; // ~2s

    // 36-slot main inv baseline
    private static ItemStack[] baselineMain = null;

    // Any open container baseline (for modded inventories)
    private static ItemStack[] baselineCont = null;
    private static Container   lastCont     = null;

    // recent pickups queue (item+meta)
    private static final Deque<PickupKey> queue = new ArrayDeque<PickupKey>();

    private static final class PickupKey {
        final Item item; final int meta; int ttl;
        PickupKey(Item i, int m, int ttl){ item=i; meta=m; this.ttl=ttl; }
        boolean matches(ItemStack s){ return s!=null && s.getItem()==item && s.getItemDamage()==meta; }
    }

    /** Preferred enqueue (from packet): full stack */
    public static void enqueue(ItemStack st){
        if (st == null || st.getItem() == null) return;
        queue.addLast(new PickupKey(st.getItem(), st.getItemDamage(), WINDOW_TICKS));
    }

    /** Legacy helpers kept for old call sites */
    public static void enqueue(String registryName, int meta){
        Item it = (Item) Item.itemRegistry.getObject(registryName);
        if (it != null) queue.addLast(new PickupKey(it, meta, WINDOW_TICKS));
    }
    public static void enqueue(String registryName, int meta, int ignoredLegacyId){
        enqueue(registryName, meta);
    }

    public static void bootstrap(){
        cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(new PickupStarClientTracker());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent evt){
        if (!ModConfig.enableItemPickupStar) return;
        if (evt.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer p = (mc != null) ? mc.thePlayer : null;
        if (p == null) return;

        // decay queue
        if (!queue.isEmpty()){
            for (PickupKey pk : queue) pk.ttl--;
            while (!queue.isEmpty() && queue.peekFirst().ttl <= 0) queue.removeFirst();
        }

        // --- MAIN INVENTORY (36) ---
        ItemStack[] cur = p.inventory.mainInventory;
        if (cur != null) {
            if (baselineMain == null || baselineMain.length != cur.length){
                baselineMain = new ItemStack[cur.length];
                for (int i=0;i<cur.length;i++) baselineMain[i] = copy(cur[i]);
            } else {
                for (int i=0;i<cur.length;i++){
                    ItemStack now = cur[i], was = baselineMain[i];

                    boolean sameItem = (now != null && was != null && sameItem(was, now));
                    boolean grew     = sameItem && now.stackSize > was.stackSize;

                    boolean changed =
                            (now != null && was == null) ||
                                    (now != null && was != null && !sameItem) ||
                                    (ModConfig.itemPickupStarOnStackIncrease && grew);

                    if (changed && now != null) {
                        if (grew) {
                            // Truly new pickup merged into this stack: clear SEEN and mark NEW
                            NBTTagCompound tag = getOrCreate(now);
                            tag.removeTag(TAG_SEEN);
                            tag.setBoolean(TAG_NEW, true);
                            now.setTagCompound(tag);
                        } else if (matchesAnyPickup(now)) {
                            NBTTagCompound tag = getOrCreate(now);
                            if (!tag.getBoolean(TAG_SEEN)) { // don't re-tag if you hovered it already
                                tag.setBoolean(TAG_NEW, true);
                                now.setTagCompound(tag);
                            }
                        }
                    }

                    baselineMain[i] = copy(now);
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
                    baselineCont = new ItemStack[slots.size()];
                    for (int i=0;i<slots.size();i++) {
                        Slot s = (Slot) slots.get(i);
                        baselineCont[i] = copy(s.getStack());
                    }
                    lastCont = cont;
                } else {
                    for (int i=0;i<slots.size();i++) {
                        Slot s = (Slot) slots.get(i);
                        ItemStack now = s.getStack();
                        ItemStack was = baselineCont[i];

                        boolean sameItem = (now != null && was != null && sameItem(was, now));
                        boolean grew     = sameItem && now.stackSize > was.stackSize;

                        boolean changed =
                                (now != null && was == null) ||
                                        (now != null && was != null && !sameItem) ||
                                        (ModConfig.itemPickupStarOnStackIncrease && grew);

                        if (changed && now != null) {
                            if (grew) {
                                NBTTagCompound tag = getOrCreate(now);
                                tag.removeTag(TAG_SEEN);
                                tag.setBoolean(TAG_NEW, true);
                                now.setTagCompound(tag);
                            } else if (matchesAnyPickup(now)) {
                                NBTTagCompound tag = getOrCreate(now);
                                if (!tag.getBoolean(TAG_SEEN)) {
                                    tag.setBoolean(TAG_NEW, true);
                                    now.setTagCompound(tag);
                                }
                            }
                        }

                        baselineCont[i] = copy(now);
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
    }

    private static boolean matchesAnyPickup(ItemStack s){
        if (queue.isEmpty()) return false;
        for (PickupKey pk : queue) if (pk.matches(s)) return true;
        return false;
    }

    private static NBTTagCompound getOrCreate(ItemStack st){
        NBTTagCompound tag = st.getTagCompound();
        if (tag == null) tag = new NBTTagCompound();
        return tag;
    }

    private static ItemStack copy(ItemStack in){ return in!=null ? in.copy() : null; }
    private static boolean sameItem(ItemStack a, ItemStack b){
        return a.getItem()==b.getItem() && a.getItemDamage()==b.getItemDamage();
    }
}
