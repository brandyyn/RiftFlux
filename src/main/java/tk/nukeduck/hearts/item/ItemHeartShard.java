/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.ItemFood
 */
package tk.nukeduck.hearts.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemFood;

public class ItemHeartShard
extends ItemFood {
    public ItemHeartShard() {
        super(0, 0.0f, false);
        this.setPotionEffect(10, 5, 2, 1.0f);
        this.setAlwaysEdible();
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("hearts:heart_shard");
    }
}

