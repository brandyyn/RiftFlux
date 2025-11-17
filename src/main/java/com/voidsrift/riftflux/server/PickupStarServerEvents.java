package com.voidsrift.riftflux.server;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.net.MsgPickup;
import com.voidsrift.riftflux.net.RFNetwork;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import java.util.*;

public final class PickupStarServerEvents {

    private static final String TAG_NEW  = "riftflux_new";
    private static final int MAX_PER_TICK_KEYS = 8;
    private static final int MAX_IDLE_TICKS = 8; // how long we wait for merges

    private static final class Key {
        final Item item;
        final int  meta;
        Key(Item i, int m){ item = i; meta = m; }

        @Override public boolean equals(Object o){
            if (this == o) return true;
            if (!(o instanceof Key)) return false;
            Key k = (Key)o;
            return item == k.item && meta == k.meta;
        }
        @Override public int hashCode(){
            return (System.identityHashCode(item) * 31) ^ meta;
        }
        ItemStack toStack(int count){ return new ItemStack(item, Math.max(1, count), meta); }

        static Key of(ItemStack s){
            return new Key(s.getItem(), s.isItemStackDamageable() ? 0 : s.getItemDamage());
        }
    }

    private static final class State {
        Map<Key,Integer> lastTotals;
        Map<Key,Integer> baseTotals;
        boolean pending;
        int idleTicks;
    }

    private final Map<UUID, State> states = new HashMap<UUID, State>();

    @SubscribeEvent
    public void onPickup(EntityItemPickupEvent e) {
        if (e.entityPlayer == null || e.entityPlayer.worldObj.isRemote) return;
        final EntityPlayer player = e.entityPlayer;

        State st = states.computeIfAbsent(player.getUniqueID(), k -> new State());

        // establish baseline from last snapshot, not current inventory
        st.baseTotals = (st.lastTotals != null)
                ? new HashMap<>(st.lastTotals)
                : totalsNow(player.openContainer, player);

        st.pending = true;
        st.idleTicks = MAX_IDLE_TICKS;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if (e.player.worldObj.isRemote) return;

        final EntityPlayer player = e.player;
        final UUID id = player.getUniqueID();
        State st = states.computeIfAbsent(id, k -> new State());

        // Always update lastTotals
        final Map<Key,Integer> cur = totalsNow(player.openContainer, player);

        if (st.pending && ModConfig.enablePickupNotifier) {
            if (st.baseTotals != null) {
                int sent = 0;
                boolean changed = false;

                for (Map.Entry<Key,Integer> ent : cur.entrySet()) {
                    if (sent >= MAX_PER_TICK_KEYS) break;

                    final Key k = ent.getKey();
                    final int now = ent.getValue();
                    final int before = st.baseTotals.getOrDefault(k, 0);
                    final int delta = now - before;

                    if (delta > 0) {
                        changed = true;
                        RFNetwork.CH.sendTo(new MsgPickup(k.toStack(delta), delta), (EntityPlayerMP) player);
                        sent++;
                    }
                }

                if (changed) {
                    st.baseTotals = cur; // reset baseline
                    st.idleTicks = MAX_IDLE_TICKS;
                } else {
                    st.idleTicks--;
                }

                if (st.idleTicks <= 0) {
                    st.pending = false;
                    st.baseTotals = null;
                }
            }
        }

        st.lastTotals = cur; // always update
    }

    // ---- inventory aggregation ----

    private static Map<Key,Integer> totalsNow(Container cur, EntityPlayer p){
        final Map<Key,Integer> m = new HashMap<>();
        addTotals(m, p.inventory.mainInventory);

        if (cur != null && cur.inventorySlots != null) {
            @SuppressWarnings("rawtypes")
            final List slots = cur.inventorySlots;
            for (int i = 0; i < slots.size(); i++) {
                final Slot s = (Slot) slots.get(i);
                if (s.inventory == p.inventory) continue;
                final ItemStack st = s.getStack();
                if (st == null || st.getItem() == null || st.stackSize <= 0) continue;
                addOne(m, st);
            }
        }
        return m;
    }

    private static void addTotals(Map<Key,Integer> m, ItemStack[] arr){
        if (arr == null) return;
        for (ItemStack st : arr) {
            if (st == null || st.getItem() == null || st.stackSize <= 0) continue;
            addOne(m, st);
        }
    }

    private static void addOne(Map<Key,Integer> m, ItemStack st){
        final Key k = Key.of(st);
        m.put(k, m.getOrDefault(k, 0) + st.stackSize);
    }
}
