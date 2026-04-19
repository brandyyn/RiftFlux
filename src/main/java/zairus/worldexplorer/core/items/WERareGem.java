/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.MathHelper
 */
package zairus.worldexplorer.core.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import zairus.worldexplorer.core.WorldExplorer;
import zairus.worldexplorer.core.items.WEItem;

public class WERareGem
extends WEItem {
    public static final String[] gem_types = new String[]{"raregem_1"};
    private IIcon[] gemIcons;

    public WERareGem() {
        this.setUnlocalizedName("raregem");
        this.setTextureName("worldexplorer:raregem_1");
        this.setCreativeTab(WorldExplorer.tabWorldExplorer);
        this.setHasSubtypes(true);
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        int j = MathHelper.clamp_int((int)damage, (int)0, (int)(gem_types.length - 1));
        return this.gemIcons[j];
    }

    public String getUnlocalizedName(ItemStack stack) {
        int i = MathHelper.clamp_int((int)stack.getItemDamage(), (int)0, (int)(gem_types.length - 1));
        return super.getUnlocalizedName() + "." + gem_types[i];
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs creativeTab, List list) {
        for (int i = 0; i < gem_types.length; ++i) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.gemIcons = new IIcon[gem_types.length];
        for (int i = 0; i < gem_types.length; ++i) {
            this.gemIcons[i] = iconRegister.registerIcon("worldexplorer:" + gem_types[i]);
        }
    }
}

