/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package zairus.worldexplorer.core.journal;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import zairus.worldexplorer.core.items.WorldExplorerItems;
import zairus.worldexplorer.core.journal.IJournalSection;

public class JournalSectionMain
implements IJournalSection {
    private String title = "";
    String content = "";

    @Override
    public String getContent() {
        this.content = "";
        this.content = this.content + "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor ";
        this.content = this.content + "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis ";
        this.content = this.content + "nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. ";
        this.content = this.content + "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore ";
        this.content = this.content + "eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, ";
        this.content = this.content + "sunt in culpa qui officia deserunt mollit anim id est laborum.";
        this.content = this.content + "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor ";
        this.content = this.content + "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis ";
        this.content = this.content + "nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. ";
        this.content = this.content + "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore ";
        this.content = this.content + "eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, ";
        this.content = this.content + "sunt in culpa qui officia deserunt mollit anim id est laborum. END";
        this.content = this.content + "";
        return this.content;
    }

    @Override
    public void setContent(String content) {
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public ItemStack getIconStack() {
        return new ItemStack((Item)WorldExplorerItems.journal);
    }

    @Override
    public IJournalSection setTitle(String sTitle) {
        this.title = sTitle;
        return this;
    }
}

