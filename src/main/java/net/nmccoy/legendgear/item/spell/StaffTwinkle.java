/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 */
package net.nmccoy.legendgear.item.spell;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.creativetab.CreativeTabs;
import net.nmccoy.legendgear.entity.EntitySpellEffect;
import net.nmccoy.legendgear.item.spell.SpellItem;

public class StaffTwinkle
extends SpellItem {
    public StaffTwinkle() {
        this.setUnlocalizedName("twinkleStaff");
        this.setTextureName("legendgear:twinkleStaff");
        this.setMaxDamage(Math.max(0, ModConfig.legendGearTwinkleStaffDurability));
        this.baseStaminaCost = Math.max(0.0f, ModConfig.legendGearTwinkleStaffManaCost);
        this.baseMeleeDamage = 3;
        this.spellType = EntitySpellEffect.SpellType.Twinkle;
        this.isStaff = true;
        this.tabs.add(CreativeTabs.tabCombat);
    }

    @Override
    public float getManaCost() {
        return Math.max(0.0f, ModConfig.legendGearTwinkleStaffManaCost);
    }
}
