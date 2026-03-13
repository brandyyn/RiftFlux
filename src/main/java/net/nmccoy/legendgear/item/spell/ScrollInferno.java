/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.ItemStack
 */
package net.nmccoy.legendgear.item.spell;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.nmccoy.legendgear.entity.EntitySpellEffect;
import net.nmccoy.legendgear.item.spell.SpellItem;

public class ScrollInferno
extends SpellItem {
    public ScrollInferno() {
        this.baseArcanePower = 15.0;
        this.setUnlocalizedName("infernoScroll");
        this.setTextureName("legendgear:scroll");
        this.setMaxDamage(0);
        this.setMaxStackSize(16);
        this.baseStaminaCost = 6.0f;
        this.baseMeleeDamage = 0;
        this.spellType = EntitySpellEffect.SpellType.Fire1;
        this.tabs.add(CreativeTabs.tabCombat);
        this.baseCastRange = 9.0;
        this.baseCastRadius = 7.0;
        this.baseCastTime = 3.0;
        this.isScroll = true;
    }

    public int getColorFromItemStack(ItemStack p_82790_1_, int p_82790_2_) {
        return 0xFFCC33;
    }

    @Override
    public boolean hasEffect(ItemStack stack, int pass) {
        return stack.getItemDamage() == 0;
    }
}

