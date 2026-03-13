/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.util.EnumChatFormatting
 *  net.minecraft.util.StatCollector
 */
package tk.nukeduck.hearts.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import tk.nukeduck.hearts.block.ItemBlock16;

public class ItemHeartLantern
extends ItemBlock16 {
    public ItemHeartLantern(Block block) {
        super(block);
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add(EnumChatFormatting.GRAY + "Right-click to heal half a heart when charged.");
        tooltip.add(EnumChatFormatting.GRAY + "Slowly recharges on its own over time.");
        tooltip.add(EnumChatFormatting.GRAY + "Emits a glowing aura");
    }
}
