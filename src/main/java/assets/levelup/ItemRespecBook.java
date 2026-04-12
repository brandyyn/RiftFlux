/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.StatCollector
 *  net.minecraft.world.World
 */
package assets.levelup;

import assets.levelup.FMLEventHandler;
import assets.levelup.PlayerExtendedProperties;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public final class ItemRespecBook
extends Item {
    public ItemRespecBook() {
        this.setHasSubtypes(true);
    }

    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if (!world.isRemote) {
            PlayerExtendedProperties.from(entityplayer).convertPointsToXp(itemstack.getItemDamage() > 0);
            FMLEventHandler.INSTANCE.loadPlayer(entityplayer);
        }
        if (!entityplayer.capabilities.isCreativeMode) {
            --itemstack.stackSize;
        }
        return itemstack;
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        super.getSubItems(item, tab, list);
        list.add(new ItemStack(item, 1, 1));
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean isAdvanced) {
        if (itemStack.getItemDamage() > 0) {
            list.add(StatCollector.translateToLocal((String)"respecbook.canresetclass"));
        }
    }
}

