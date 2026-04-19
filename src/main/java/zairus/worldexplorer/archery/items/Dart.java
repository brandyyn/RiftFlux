/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.MathHelper
 */
package zairus.worldexplorer.archery.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import zairus.worldexplorer.core.WorldExplorer;
import zairus.worldexplorer.core.items.WEItem;

public class Dart
extends WEItem {
    public static final String[] dart_types = new String[]{"dart"};
    public static String KEY_HASEFFECT = "dartHasEffect";
    public static String KEY_EFFECTID = "dartEffectId";
    public static String KEY_EFFECTNAME = "dartEffectName";
    private IIcon[] dartIcons;

    public Dart() {
        this.setUnlocalizedName("dart");
        this.setTextureName("worldexplorer:dart");
        this.setCreativeTab(WorldExplorer.tabWorldExplorer);
        this.setHasSubtypes(true);
        this.maxStackSize = 64;
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        int j = MathHelper.clamp_int((int)damage, (int)0, (int)(dart_types.length - 1));
        return this.dartIcons[j];
    }

    @SideOnly(value=Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        boolean hasEffect = super.hasEffect(stack, pass);
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(KEY_HASEFFECT)) {
            hasEffect = stack.getTagCompound().getBoolean(KEY_HASEFFECT);
        }
        return hasEffect;
    }

    public String getUnlocalizedName(ItemStack stack) {
        int i = MathHelper.clamp_int((int)stack.getItemDamage(), (int)0, (int)(dart_types.length - 1));
        return super.getUnlocalizedName() + "." + dart_types[i];
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs creativeTab, List list) {
        for (int i = 0; i < dart_types.length; ++i) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.dartIcons = new IIcon[dart_types.length];
        for (int i = 0; i < dart_types.length; ++i) {
            this.dartIcons[i] = iconRegister.registerIcon(this.getIconString());
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        if (stack.hasTagCompound()) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag.hasKey(KEY_EFFECTNAME)) {
                list.add("Effect: " + tag.getString(KEY_EFFECTNAME));
            }
            if (tag.hasKey(KEY_EFFECTID)) {
                list.add("Id: " + tag.getString(KEY_EFFECTID));
            }
        }
    }
}

