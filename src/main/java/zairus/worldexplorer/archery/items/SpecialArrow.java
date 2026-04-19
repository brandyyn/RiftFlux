/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.MathHelper
 */
package zairus.worldexplorer.archery.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import zairus.worldexplorer.core.WorldExplorer;
import zairus.worldexplorer.core.items.WEItem;

public class SpecialArrow
extends WEItem {
    public static final String[] arrow_types = new String[]{"sharpened_stick", "stone_arrow", "flint_arrow", "iron_arrow", "diamond_arrow", "obsidian_arrow"};
    private IIcon[] arrowIcons;

    public SpecialArrow() {
        this.setUnlocalizedName("specialarrow");
        this.setTextureName("worldexplorer:specialarrow");
        this.setCreativeTab(WorldExplorer.tabWorldExplorer);
        this.setHasSubtypes(true);
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        int j = MathHelper.clamp_int((int)damage, (int)0, (int)(arrow_types.length - 1));
        return this.arrowIcons[j];
    }

    public String getUnlocalizedName(ItemStack stack) {
        int i = MathHelper.clamp_int((int)stack.getItemDamage(), (int)0, (int)(arrow_types.length - 1));
        return super.getUnlocalizedName() + "." + arrow_types[i];
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs creativeTab, List list) {
        for (int i = 0; i < arrow_types.length; ++i) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.arrowIcons = new IIcon[arrow_types.length];
        for (int i = 0; i < arrow_types.length; ++i) {
            this.arrowIcons[i] = iconRegister.registerIcon(this.getIconString() + (i > 0 ? "_" + arrow_types[i] : ""));
        }
    }

    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        DamageSource damagesource = DamageSource.causePlayerDamage((EntityPlayer)player);
        damagesource.damageType = "specialarrow";
        float damage = 2.0f;
        damage = (float)((double)damage + (double)stack.getItemDamage() * 1.05);
        entity.attackEntityFrom(damagesource, damage);
        entity.playSound("random.bowhit", 1.1f, 1.2f / (player.worldObj.rand.nextFloat() * 0.2f + 0.9f));
        if (!player.capabilities.isCreativeMode) {
            player.inventory.decrStackSize(player.inventory.currentItem, 1);
        }
        return true;
    }
}

