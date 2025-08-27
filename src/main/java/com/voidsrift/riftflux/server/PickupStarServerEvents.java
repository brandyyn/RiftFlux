package com.voidsrift.riftflux.server;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.net.MsgPickup;
import com.voidsrift.riftflux.net.RFNetwork;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import java.util.*;

public final class PickupStarServerEvents {

    // IMPORTANT: match the star system invariant tag
    private static final String TAG_NEW = "riftfixes_new";

    private static final class Key {
        final Item item; final int meta;
        Key(Item i, int m){ item=i; meta=m; }
        @Override public boolean equals(Object o){ if(this==o) return true; if(!(o instanceof Key)) return false; Key k=(Key)o; return item==k.item && meta==k.meta; }
        @Override public int hashCode(){ return (System.identityHashCode(item)*31) ^ meta; }
        ItemStack toStack(int count){ return new ItemStack(item, Math.max(1,count), meta); }
    }
    private static Key keyOf(ItemStack s){ return new Key(s.getItem(), s.getItemDamage()); }

    private static final class State {
        ItemStack[] baseMain;
        ItemStack[] baseCont;
        int baseWinId = Integer.MIN_VALUE;
        boolean pending = false;
        final Map<Key,Integer> expect = new HashMap<Key,Integer>();
    }

    private final Map<UUID, State> states = new HashMap<UUID, State>();

    @SubscribeEvent
    public void onPickup(EntityItemPickupEvent e) {
        if (e.entityPlayer == null || e.entityPlayer.worldObj.isRemote) return;

        final UUID id = e.entityPlayer.getUniqueID();
        State st = states.get(id);
        if (st == null) { st = new State(); states.put(id, st); }

        st.baseMain = copyArray(e.entityPlayer.inventory.mainInventory);

        final Container c = e.entityPlayer.openContainer;
        if (c != null && c.inventorySlots != null) {
            st.baseWinId = c.windowId;
            st.baseCont  = copyContainer(c);
        } else {
            st.baseWinId = Integer.MIN_VALUE;
            st.baseCont  = null;
        }

        final ItemStack is = e.item != null ? e.item.getEntityItem() : null;
        if (is != null && is.getItem() != null) {
            final Key k = keyOf(is);
            final int add = Math.max(1, is.stackSize);
            st.expect.put(k, st.expect.containsKey(k) ? st.expect.get(k) + add : add);
        }

        st.pending = true;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if (e.player.worldObj.isRemote) return;

        final UUID id = e.player.getUniqueID();
        final State st = states.get(id);
        if (st == null || !st.pending) return;

        final Map<Key,Integer> counted = new HashMap<Key,Integer>();
        final Set<Key> tagged = new HashSet<Key>();
        final Set<Key> present = new HashSet<Key>();
        boolean anyContainerChange = false;

        // main inventory diff + presence + tag probe
        final ItemStack[] curMain = e.player.inventory.mainInventory;
        if (curMain != null) {
            for (int i=0;i<curMain.length;i++){
                final ItemStack now = curMain[i];
                final ItemStack was = (st.baseMain != null && i < st.baseMain.length) ? st.baseMain[i] : null;

                if (now != null) {
                    final Key k = keyOf(now);
                    present.add(k);
                }

                final int added = grewOrNew(now, was);
                if (added > 0) {
                    accum(counted, keyOf(now), added);
                    sendNotify((EntityPlayerMP) e.player, now, added);
                    if (now.getTagCompound() != null) now.getTagCompound().setBoolean(TAG_NEW, true);
                    tagged.add(keyOf(now));
                } else if (now != null) {
                    final NBTTagCompound t = now.getTagCompound();
                    if (t != null && t.getBoolean(TAG_NEW)) tagged.add(keyOf(now));
                }
            }
        }

        // open container diff + presence + tag probe
        final Container cur = e.player.openContainer;
        if (cur != null && cur.inventorySlots != null && st.baseCont != null && cur.windowId == st.baseWinId) {
            @SuppressWarnings("rawtypes")
            final List slots = cur.inventorySlots;
            if (st.baseCont.length == slots.size()) {
                for (int i=0;i<slots.size();i++){
                    final Slot s = (Slot) slots.get(i);
                    if (s.inventory == e.player.inventory) continue;

                    final ItemStack now = s.getStack();
                    final ItemStack was = st.baseCont[i];

                    if (now != null) {
                        present.add(keyOf(now));
                    }

                    final int added = grewOrNew(now, was);
                    if (added > 0) {
                        anyContainerChange = true;
                        s.onSlotChanged();
                        accum(counted, keyOf(now), added);
                        sendNotify((EntityPlayerMP) e.player, now, added);
                        if (now.getTagCompound() != null) now.getTagCompound().setBoolean(TAG_NEW, true);
                        tagged.add(keyOf(now));
                    } else if (now != null) {
                        final NBTTagCompound t = now.getTagCompound();
                        if (t != null && t.getBoolean(TAG_NEW)) tagged.add(keyOf(now));
                    }
                }
            }
            if (anyContainerChange) cur.detectAndSendChanges();
        } else {
            // even if no container snapshot, still record presence from any open container now
            final Container c2 = e.player.openContainer;
            if (c2 != null && c2.inventorySlots != null) {
                @SuppressWarnings("rawtypes")
                final List slots = c2.inventorySlots;
                for (int i=0;i<slots.size();i++){
                    final Slot s = (Slot) slots.get(i);
                    final ItemStack now = s.getStack();
                    if (now != null) present.add(keyOf(now));
                }
            }
        }

        // presence-based fallback (fixes: hovered-then-merge with strict diff miss)
        if (ModConfig.enablePickupNotifier && st.expect != null && !st.expect.isEmpty()) {
            for (Map.Entry<Key,Integer> ex : st.expect.entrySet()){
                final Key k = ex.getKey();
                final int want = ex.getValue();
                final int got  = counted.containsKey(k) ? counted.get(k) : 0;
                final int missing = want - got;
                if (missing > 0 && (tagged.contains(k) || present.contains(k))) {
                    ItemStack display = k.toStack(missing);
                    RFNetwork.CH.sendTo(new MsgPickup(display, missing), (EntityPlayerMP) e.player);
                }
            }
        }

        st.pending = false;
        st.baseMain = null;
        st.baseCont = null;
        st.baseWinId = Integer.MIN_VALUE;
        st.expect.clear();
    }

    private static int grewOrNew(ItemStack now, ItemStack was) {
        if (now == null) return 0;
        final boolean isNew = (was == null);
        final boolean grew = (!isNew && sameStrict(was, now) && now.stackSize > was.stackSize);
        if (isNew) return now.stackSize;
        if (grew) return now.stackSize - was.stackSize;
        return 0;
    }

    private static boolean sameStrict(ItemStack a, ItemStack b){
        if (a == null || b == null) return false;
        if (a.getItem() != b.getItem()) return false;
        if (a.getItemDamage() != b.getItemDamage()) return false;
        final NBTTagCompound ta = a.getTagCompound();
        final NBTTagCompound tb = b.getTagCompound();
        if (ta == null && tb == null) return true;
        if (ta == null || tb == null) return false;
        return ta.equals(tb);
    }

    private static ItemStack[] copyArray(ItemStack[] src){
        if (src == null) return null;
        ItemStack[] out = new ItemStack[src.length];
        for (int i=0;i<src.length;i++) out[i] = src[i] != null ? src[i].copy() : null;
        return out;
    }

    @SuppressWarnings("rawtypes")
    private static ItemStack[] copyContainer(Container c){
        final List slots = c.inventorySlots;
        ItemStack[] out = new ItemStack[slots.size()];
        for (int i=0;i<slots.size();i++){
            final Slot s = (Slot) slots.get(i);
            final ItemStack st = s.getStack();
            out[i] = (st != null) ? st.copy() : null;
        }
        return out;
    }

    private static void accum(Map<Key,Integer> m, Key k, int add){
        m.put(k, m.containsKey(k) ? m.get(k) + add : add);
    }

    private static void sendNotify(EntityPlayerMP p, ItemStack now, int added){
        if (!ModConfig.enablePickupNotifier) return;
        if (now == null || added <= 0) return;
        ItemStack display = now.copy();
        display.stackSize = added;
        RFNetwork.CH.sendTo(new MsgPickup(display, added), p);
    }
}
