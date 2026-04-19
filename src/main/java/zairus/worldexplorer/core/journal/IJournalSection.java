/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package zairus.worldexplorer.core.journal;

import net.minecraft.item.ItemStack;

public interface IJournalSection {
    public IJournalSection setTitle(String var1);

    public String getTitle();

    public ItemStack getIconStack();

    public void setContent(String var1);

    public String getContent();
}

