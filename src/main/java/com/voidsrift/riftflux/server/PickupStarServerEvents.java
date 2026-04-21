package com.voidsrift.riftflux.server;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.net.MsgPickup;
import com.voidsrift.riftflux.net.RFNetwork;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
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
    private static final String TAG_SYNTHETIC_PICKUP = "riftflux_synthetic_pickup";
    private static final int WINDOW_TICKS = 40; // ~2s
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

    private static final class StarKey {
        final Item item;
        final int meta;
        final boolean wildcardMeta;
        int ttl;
        StarKey(Item i, int m, boolean wildcardMeta, int ttl) {
            item = i;
            meta = m;
            this.wildcardMeta = wildcardMeta;
            this.ttl = ttl;
        }
        boolean matches(ItemStack s) {
            if (s == null || s.getItem() == null) return false;
            if (s.getItem() != item) return false;
            if (wildcardMeta || s.isItemStackDamageable()) return true;
            return s.getItemDamage() == meta;
        }
    }

    private final Map<UUID, State> states = new HashMap<UUID, State>();
    private final Map<UUID, Deque<StarKey>> starQueues = new HashMap<UUID, Deque<StarKey>>();
    private final Map<UUID, ItemStack[]> starBaselineMain = new HashMap<UUID, ItemStack[]>();
    private final Map<UUID, ItemStack[]> starBaselineCont = new HashMap<UUID, ItemStack[]>();
    private final Map<UUID, Container> starBaselineContRef = new HashMap<UUID, Container>();

    @SubscribeEvent
    public void onPickup(EntityItemPickupEvent e) {
        if (e.entityPlayer == null || e.entityPlayer.worldObj.isRemote) return;
        if (!ModConfig.enablePickupNotifier && !ModConfig.enableItemPickupStar) return;

        final EntityPlayer player = e.entityPlayer;
        final ItemStack pickedStack = (e.item == null) ? null : e.item.getEntityItem();
        final boolean syntheticPickup = e.item != null
                && e.item.getEntityData() != null
                && e.item.getEntityData().getBoolean(TAG_SYNTHETIC_PICKUP);

        if (ModConfig.enablePickupNotifier) {
            State st = states.computeIfAbsent(player.getUniqueID(), k -> new State());

            if (syntheticPickup && pickedStack != null && pickedStack.getItem() != null) {
                // Synthetic pickup events (boomerang-return payload, etc.) happen after inventory mutation.
                // Use current totals minus the announced stack so unrelated changes (like boomerang return)
                // do not get reported as "picked up".
                Map<Key, Integer> syntheticBaseline = buildSyntheticBaseline(player, pickedStack);
                if (st.baseTotals == null) {
                    st.baseTotals = syntheticBaseline;
                } else {
                    mergeSyntheticBaseline(st.baseTotals, pickedStack);
                }
            } else {
                // establish baseline from last snapshot, not current inventory
                st.baseTotals = (st.lastTotals != null)
                        ? new HashMap<Key, Integer>(st.lastTotals)
                        : totalsNow(player.openContainer, player);
            }

            st.pending = true;
            st.idleTicks = MAX_IDLE_TICKS;
        }

        if (ModConfig.enableItemPickupStar && pickedStack != null) {
            ItemStack stack = pickedStack;
            if (stack.getItem() != null) {
                Deque<StarKey> q = starQueues.computeIfAbsent(player.getUniqueID(), k -> new ArrayDeque<StarKey>());
                boolean wildcard = stack.isItemStackDamageable();
                q.addLast(new StarKey(stack.getItem(), stack.getItemDamage(), wildcard, WINDOW_TICKS));
            }
        }
    }

    private static Map<Key, Integer> buildSyntheticBaseline(EntityPlayer player, ItemStack pickedStack) {
        Map<Key, Integer> baseline = totalsNow(player.openContainer, player);
        Key key = Key.of(pickedStack);
        int current = baseline.getOrDefault(key, 0);
        int adjusted = current - Math.max(1, pickedStack.stackSize);
        if (adjusted > 0) {
            baseline.put(key, adjusted);
        } else {
            baseline.remove(key);
        }
        return baseline;
    }

    private static void mergeSyntheticBaseline(Map<Key, Integer> baseline, ItemStack pickedStack) {
        if (baseline == null || pickedStack == null || pickedStack.getItem() == null) {
            return;
        }
        Key key = Key.of(pickedStack);
        int current = baseline.getOrDefault(key, 0);
        int adjusted = current - Math.max(1, pickedStack.stackSize);
        if (adjusted > 0) {
            baseline.put(key, adjusted);
        } else {
            baseline.remove(key);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if (e.player.worldObj.isRemote) return;

        final EntityPlayer player = e.player;
        final UUID id = player.getUniqueID();

        if (!ModConfig.enablePickupNotifier && !ModConfig.enableItemPickupStar) {
            clearPlayer(id);
            return;
        }

        if (ModConfig.enablePickupNotifier) {
            State st = states.computeIfAbsent(id, k -> new State());

            // Always update lastTotals while the pickup notifier is enabled.
            final Map<Key, Integer> cur = totalsNow(player.openContainer, player);

            if (st.pending) {
                if (st.baseTotals != null) {
                    int sent = 0;
                    boolean changed = false;

                    for (Map.Entry<Key, Integer> ent : cur.entrySet()) {
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
        } else {
            states.remove(id);
        }

        if (ModConfig.enableItemPickupStar) {
            tickStarTracking(player);
        } else {
            clearStarTracking(id);
        }

        if (ModConfig.itemPickupStarClearHeldItem) {
            clearStarTag(player.getCurrentEquippedItem());
        }

        if (ModConfig.itemPickupStarClearOnLeaveInventory) {
            clearStarsInNonPlayerSlots(player.openContainer, player);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event == null || event.player == null || event.player.worldObj == null || event.player.worldObj.isRemote) {
            return;
        }
        clearPlayer(event.player.getUniqueID());
    }

    private void clearPlayer(UUID id) {
        if (id == null) {
            return;
        }
        states.remove(id);
        clearStarTracking(id);
    }

    private void clearStarTracking(UUID id) {
        if (id == null) {
            return;
        }
        starQueues.remove(id);
        starBaselineMain.remove(id);
        starBaselineCont.remove(id);
        starBaselineContRef.remove(id);
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

    private static void clearStarsInNonPlayerSlots(Container cont, EntityPlayer p) {
        if (cont == null || cont.inventorySlots == null) return;
        @SuppressWarnings("rawtypes")
        final List slots = cont.inventorySlots;
        for (int i = 0; i < slots.size(); i++) {
            final Slot s = (Slot) slots.get(i);
            if (isPlayerOwnedSlot(s, p)) continue;
            clearStarTag(s.getStack());
        }
    }

    private static void clearStarTag(ItemStack st) {
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

    private void tickStarTracking(EntityPlayer player) {
        UUID id = player.getUniqueID();
        Deque<StarKey> q = starQueues.computeIfAbsent(id, k -> new ArrayDeque<StarKey>());
        if (!q.isEmpty()) {
            for (StarKey pk : q) pk.ttl--;
            while (!q.isEmpty() && q.peekFirst().ttl <= 0) q.removeFirst();
        }

        ItemStack[] cur = player.inventory.mainInventory;
        if (cur != null) {
            ItemStack[] base = starBaselineMain.get(id);
            if (base == null || base.length != cur.length) {
                base = new ItemStack[cur.length];
                for (int i = 0; i < cur.length; i++) base[i] = copy(cur[i]);
                starBaselineMain.put(id, base);
            } else {
                for (int i = 0; i < cur.length; i++) {
                    ItemStack now = cur[i];
                    ItemStack was = base[i];
                    if (now == null) {
                        base[i] = null;
                        continue;
                    }
                    boolean sameItem = (was != null && sameItem(was, now));
                    boolean grew = sameItem && now.stackSize > was.stackSize;
                    boolean inserted = (was == null) || (was != null && !sameItem);
                    if (ModConfig.itemPickupStarOnStackIncrease && grew) {
                        markStarTag(now);
                    } else if (inserted && matchesAny(q, now)) {
                        markStarTag(now);
                    }
                    base[i] = copy(now);
                }
            }
        } else {
            starBaselineMain.remove(id);
        }

        Container cont = player.openContainer;
        if (cont != null && cont.inventorySlots != null) {
            @SuppressWarnings("rawtypes")
            final List slots = cont.inventorySlots;
            Container last = starBaselineContRef.get(id);
            ItemStack[] base = starBaselineCont.get(id);
            if (cont != last || base == null || base.length != slots.size()) {
                base = new ItemStack[slots.size()];
                for (int i = 0; i < slots.size(); i++) {
                    Slot s = (Slot) slots.get(i);
                    base[i] = copy(s.getStack());
                }
                starBaselineCont.put(id, base);
                starBaselineContRef.put(id, cont);
            } else {
                for (int i = 0; i < slots.size(); i++) {
                    Slot s = (Slot) slots.get(i);
                    ItemStack now = s.getStack();
                    ItemStack was = base[i];
                    if (!isPlayerOwnedSlot(s, player)) {
                        base[i] = copy(now);
                        continue;
                    }
                    if (now == null) {
                        base[i] = null;
                        continue;
                    }
                    boolean sameItem = (was != null && sameItem(was, now));
                    boolean grew = sameItem && now.stackSize > was.stackSize;
                    boolean inserted = (was == null) || (was != null && !sameItem);
                    if (ModConfig.itemPickupStarOnStackIncrease && grew) {
                        markStarTag(now);
                    } else if (inserted && matchesAny(q, now)) {
                        markStarTag(now);
                    }
                    base[i] = copy(now);
                }
            }
        } else {
            starBaselineCont.remove(id);
            starBaselineContRef.remove(id);
        }
    }

    private static boolean matchesAny(Deque<StarKey> q, ItemStack s) {
        if (q == null || q.isEmpty()) return false;
        for (StarKey pk : q) if (pk.matches(s)) return true;
        return false;
    }

    private static void markStarTag(ItemStack st) {
        if (st == null) return;
        NBTTagCompound tag = st.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
        } else if (tag.getBoolean(TAG_NEW)) {
            return;
        } else {
            tag = (NBTTagCompound) tag.copy();
        }
        tag.setBoolean(TAG_NEW, true);
        st.setTagCompound(tag);
    }

    private static ItemStack copy(ItemStack in){ return in!=null ? in.copy() : null; }

    private static boolean sameItem(ItemStack a, ItemStack b){
        return a.getItem()==b.getItem() && a.getItemDamage()==b.getItemDamage();
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
