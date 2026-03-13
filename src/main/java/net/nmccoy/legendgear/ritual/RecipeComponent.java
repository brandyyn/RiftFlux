/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 */
package net.nmccoy.legendgear.ritual;

import java.util.ArrayList;
import java.util.HashSet;
import net.minecraft.block.Block;
import net.nmccoy.legendgear.ritual.RecipeElement;

public class RecipeComponent {
    public HashSet<RecipeElement> blocks;
    public ComponentType type;
    public HashSet<RecipeComponent> alternatives;

    private RecipeComponent() {
        this.blocks = new HashSet();
        this.alternatives = new HashSet();
    }

    public RecipeComponent or(RecipeComponent alt) {
        if (this.alternatives == null) {
            this.alternatives = new HashSet();
        }
        this.alternatives.add(alt);
        return this;
    }

    public RecipeComponent generic() {
        ArrayList<RecipeElement> parts = new ArrayList<RecipeElement>();
        parts.addAll(this.blocks);
        RecipeComponent generalized = new RecipeComponent();
        if (this.type == ComponentType.SINGLETON) {
            generalized.type = this.type;
            generalized.blocks.add(((RecipeElement)parts.get(0)).generalize());
            return generalized;
        }
        for (RecipeElement part : this.blocks) {
            generalized.blocks.add(part.generalize());
        }
        generalized.type = generalized.blocks.size() == 2 ? ComponentType.DUO : ComponentType.MATCH;
        return generalized;
    }

    public boolean isMetBy(RecipeComponent other) {
        boolean alternativeMet = false;
        if (this.alternatives != null) {
            for (RecipeComponent alt : this.alternatives) {
                alternativeMet = alternativeMet || alt.isMetBy(other);
            }
        }
        if (alternativeMet) {
            return true;
        }
        if (other.type != this.type) {
            return false;
        }
        for (RecipeElement ingredient : other.blocks) {
            if (this.blocks.contains(ingredient) || this.blocks.contains(ingredient.generalize())) continue;
            return false;
        }
        return true;
    }

    public RecipeComponent(Block bfirst, Block bsecond) {
        RecipeElement first = new RecipeElement(bfirst);
        RecipeElement second = new RecipeElement(bsecond);
        this.blocks = new HashSet();
        if (first.equals(second)) {
            this.blocks.add(first);
            this.type = ComponentType.MATCH;
        } else {
            this.blocks.add(first);
            this.blocks.add(second);
            this.type = ComponentType.DUO;
        }
    }

    public RecipeComponent(Block bfirst, int mfirst, Block bsecond, int msecond) {
        RecipeElement first = new RecipeElement(bfirst, mfirst);
        RecipeElement second = new RecipeElement(bsecond, msecond);
        this.blocks = new HashSet();
        if (first.equals(second)) {
            this.blocks.add(first);
            this.type = ComponentType.MATCH;
        } else {
            this.blocks.add(first);
            this.blocks.add(second);
            this.type = ComponentType.DUO;
        }
    }

    public RecipeComponent(Block bsingle) {
        RecipeElement single = new RecipeElement(bsingle);
        this.blocks = new HashSet();
        this.blocks.add(single);
        this.type = ComponentType.SINGLETON;
    }

    public RecipeComponent(Block bsingle, int meta) {
        RecipeElement single = new RecipeElement(bsingle, meta);
        this.blocks = new HashSet();
        this.blocks.add(single);
        this.type = ComponentType.SINGLETON;
    }

    public boolean equals(Object other) {
        if (other == null || !(other instanceof RecipeComponent)) {
            return false;
        }
        RecipeComponent otherComponent = (RecipeComponent)other;
        return otherComponent.type == this.type && otherComponent.blocks.equals(this.blocks);
    }

    public int hashCode() {
        return this.blocks.hashCode() + this.type.hashCode();
    }

    public String toString() {
        ArrayList<RecipeElement> parts = new ArrayList<RecipeElement>();
        parts.addAll(this.blocks);
        String name1 = ((RecipeElement)parts.get((int)0)).block.getUnlocalizedName();
        name1 = name1.replaceAll(".name", "").replaceAll("tile.", "");
        if (((RecipeElement)parts.get((int)0)).meta != -1) {
            name1 = name1 + ":" + ((RecipeElement)parts.get((int)0)).meta;
        }
        String name2 = "";
        if (parts.size() > 1) {
            name2 = ((RecipeElement)parts.get((int)1)).block.getUnlocalizedName();
            name2 = name2.replaceAll(".name", "").replaceAll("tile.", "");
            if (((RecipeElement)parts.get((int)1)).meta != -1) {
                name2 = name2 + ":" + ((RecipeElement)parts.get((int)1)).meta;
            }
        }
        if (this.type == ComponentType.SINGLETON) {
            return "[" + name1 + "]";
        }
        if (this.type == ComponentType.MATCH) {
            return "[" + name1 + " - " + name1 + "]";
        }
        return "[" + name1 + " / " + name2 + "]";
    }

    public static enum ComponentType {
        SINGLETON,
        MATCH,
        DUO;

    }
}

