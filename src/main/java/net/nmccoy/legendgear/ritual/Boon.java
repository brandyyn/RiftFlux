/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package net.nmccoy.legendgear.ritual;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Boon
implements Comparable<Boon> {
    public int cost;
    public Object requestItem;
    public Object reward;
    public boolean allowGratitude;

    public Boon(Object reward, int value, Object request) {
        this.cost = value;
        this.requestItem = request;
        this.reward = reward;
        this.allowGratitude = true;
    }

    public Boon(Object reward, int value, Object request, boolean gratitude) {
        this(reward, value, request);
        this.allowGratitude = gratitude;
    }

    public ItemStack getRewardItem(int count) {
        ItemStack prize = null;
        if (this.reward instanceof ItemStack) {
            prize = ((ItemStack)this.reward).copy();
            prize.stackSize *= count;
        }
        if (this.reward instanceof Item) {
            prize = new ItemStack((Item)this.reward, count);
        }
        return prize;
    }

    @Override
    public int compareTo(Boon other) {
        return this.cost - other.cost;
    }
}

