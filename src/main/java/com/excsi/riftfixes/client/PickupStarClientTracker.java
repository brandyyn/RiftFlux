package com.excsi.riftfixes.client;

import com.excsi.riftfixes.ModConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/** Server-truth for main inv, tiny client fallback for non-player containers (modded UIs). */
public final class PickupStarClientTracker {

    private static final String TAG = "riftfixes_new";
    private static final int WINDOW_TICKS = 40; // ~2s correlation window

    // Main inv baseline (36)
    private static ItemStack[] baselineMain = null;

    // Open container baseline
    private static ItemStack[] baselineCont = null;
    private static Container   lastCont     = null;

    // Recent pickups from server
    private static final Deque<PickupKey> queue = new ArrayDeque<PickupKey>();
    private static final class PickupKey {
        final Item i; final int m; int ttl;
        PickupKey(Item i, int m, int ttl){this.i=i; this.m=m; this.ttl=ttl;}
        boolean matches(ItemStack s){return s!=null && s.getItem()==i && s.getItemDamage()==m;}
    }

    /** Called by your MsgPickup client handler. */
    public static void enqueue(ItemStack st){
        if (st==null || st.getItem()==null) return;
        queue.addLast(new PickupKey(st.getItem(), st.getItemDamage(), WINDOW_TICKS));
    }
    public static void enqueue(String registryName, int meta){
        Item it = (Item) Item.itemRegistry.getObject(registryName);
        if (it != null) queue.addLast(new PickupKey(it, meta, WINDOW_TICKS));
    }
    public static void enqueue(String registryName, int meta, int ignoredLegacy){ enqueue(registryName, meta); }

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
        if (!queue.isEmpty()) {
            for (PickupKey pk : queue) pk.ttl--;
            while (!queue.isEmpty() && queue.peekFirst().ttl <= 0) queue.removeFirst();
        }

        // ---- MAIN INVENTORY (server truth stays) ----
        ItemStack[] cur = p.inventory.mainInventory;
        if (cur != null) {
            if (baselineMain == null || baselineMain.length != cur.length) {
                baselineMain = new ItemStack[cur.length];
                for (int i=0;i<cur.length;i++) baselineMain[i] = copy(cur[i]);
            } else {
                for (int i=0;i<cur.length;i++) {
                    ItemStack now = cur[i], was = baselineMain[i];
                    boolean changed = (now != null && was == null)
                            || (now != null && was != null && !sameItem(was, now))
                            || (ModConfig.itemPickupStarOnStackIncrease && now!=null && was!=null
                            && sameItem(was, now) && now.stackSize > was.stackSize);
                    // Do NOT tag here on client; server already tags main inv.
                    baselineMain[i] = copy(now);
                }
            }
        }

        // ---- ANY OPEN CONTAINER (fallback for modded inventories) ----
        if (mc.currentScreen instanceof GuiContainer) {
            Container c = p.openContainer;
            if (c != null && c.inventorySlots != null) {
                @SuppressWarnings("rawtypes") List slots = c.inventorySlots;

                // reset baseline if container changed/size changed
                if (c != lastCont || baselineCont == null || baselineCont.length != slots.size()) {
                    baselineCont = new ItemStack[slots.size()];
                    for (int i=0;i<slots.size();i++) {
                        Slot s = (Slot) slots.get(i);
                        baselineCont[i] = copy(s.getStack());
                    }
                    lastCont = c;
                } else if (!queue.isEmpty()) { // only try to tag during pickup window
                    for (int i=0;i<slots.size();i++) {
                        Slot s = (Slot) slots.get(i);
                        ItemStack now = s.getStack();
                        ItemStack was = baselineCont[i];

                        boolean changed = (now != null && was == null)
                                || (now != null && was != null && !sameItem(was, now))
                                || (ModConfig.itemPickupStarOnStackIncrease && now!=null && was!=null
                                && sameItem(was, now) && now.stackSize > was.stackSize);

                        if (changed && now != null && matchesAnyPickup(now)) {
                            // only tag if NOT a ContainerPlayer (server already handled main inv)
                            if (!(c instanceof ContainerPlayer)) {
                                NBTTagCompound tag = now.getTagCompound();
                                if (tag == null) tag = new NBTTagCompound();
                                tag.setBoolean(TAG, true);
                                now.setTagCompound(tag);
                            }
                        }
                        baselineCont[i] = copy(now);
                    }
                }
            } else {
                baselineCont = null; lastCont = null;
            }
        } else {
            baselineCont = null; lastCont = null;
        }
    }

    private static boolean matchesAnyPickup(ItemStack s){
        if (queue.isEmpty()) return false;
        for (PickupKey pk : queue) if (pk.matches(s)) return true;
        return false;
    }

    private static ItemStack copy(ItemStack in){ return in!=null ? in.copy() : null; }
    private static boolean sameItem(ItemStack a, ItemStack b){
        return a.getItem()==b.getItem() && a.getItemDamage()==b.getItemDamage();
    }
}
