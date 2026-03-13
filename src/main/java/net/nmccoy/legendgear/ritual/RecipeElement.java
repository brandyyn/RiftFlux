/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 */
package net.nmccoy.legendgear.ritual;

import net.minecraft.block.Block;

public class RecipeElement {
    public Block block;
    public int meta;
    public static final int ANY = -1;

    public RecipeElement(Block b) {
        this.block = b;
        this.meta = -1;
    }

    public RecipeElement(Block b, int m) {
        this.block = b;
        this.meta = m;
    }

    public RecipeElement generalize() {
        return new RecipeElement(this.block);
    }

    public boolean match(RecipeElement other) {
        if (other == null) {
            return false;
        }
        if (other.block != this.block) {
            return false;
        }
        return this.meta == -1 || other.meta == -1 || this.meta == other.meta;
    }

    public boolean equals(Object o) {
        if (o != null && o instanceof RecipeElement) {
            RecipeElement oel = (RecipeElement)o;
            return this.block == oel.block && this.meta == oel.meta;
        }
        return false;
    }

    public int hashCode() {
        return this.block.hashCode() + this.meta;
    }
}

