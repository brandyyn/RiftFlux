/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package zairus.worldexplorer.core.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import zairus.worldexplorer.core.WorldExplorer;
import zairus.worldexplorer.core.items.WEItem;
import zairus.worldexplorer.core.player.CorePlayerManager;

public class BookJournal
extends WEItem {
    public BookJournal() {
        this.setUnlocalizedName("journal");
        this.setTextureName("worldexplorer:journal");
        this.setCreativeTab(WorldExplorer.tabWorldExplorer);
        this.maxStackSize = 1;
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        CorePlayerManager.unlockItem(player, this);
        world.playSoundAtEntity((Entity)player, "worldexplorer:book_tab", 1.0f, 1.0f / (itemRand.nextFloat() * 0.4f + 1.2f) + 0.5f);
        WorldExplorer.proxy.displayBookJournalGUI(player, stack);
        return stack;
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("JournalTitle")) {
            list.add(stack.getTagCompound().getString("JournalTitle"));
        }
    }
}

