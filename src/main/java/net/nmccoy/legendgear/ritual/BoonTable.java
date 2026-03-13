/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package net.nmccoy.legendgear.ritual;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.nmccoy.legendgear.ritual.Boon;

public class BoonTable {
    public List<Boon> boons = new ArrayList<Boon>();

    public Boon matchRequest(Object request) {
        for (Boon b : this.boons) {
            Class rqclass;
            if (b.requestItem == null) continue;
            if (b.requestItem.equals(request)) {
                return b;
            }
            if (!(request instanceof ItemStack)) continue;
            ItemStack stack = (ItemStack)request;
            if (b.requestItem instanceof ItemStack) {
                ItemStack rqstack = (ItemStack)b.requestItem;
                if (stack.getItem() == rqstack.getItem() && stack.getItemDamage() == rqstack.getItemDamage()) {
                    return b;
                }
            }
            if (b.requestItem.equals(stack.getItem())) {
                return b;
            }
            if (!(b.requestItem instanceof Class) || !(rqclass = (Class)b.requestItem).isInstance(stack.getItem())) continue;
            return b;
        }
        return null;
    }

    public void add(Object reward, int price, Item request) {
        this.boons.add(new Boon(reward, price, request));
    }

    public void addHidden(Object reward, int price, Object request) {
        this.boons.add(new Boon(reward, price, request, false));
    }
}

