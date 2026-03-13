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

public class StaffIce
extends SpellItem {
    public StaffIce() {
        this.setUnlocalizedName("iceStaff");
        this.setTextureName("legendgear:iceStaffWood");
        this.setMaxDamage(Math.max(0, ModConfig.legendGearIceStaffDurability));
        this.baseArcanePower += 2.0;
        this.baseStaminaCost += 1.0f;
        this.baseMeleeDamage = 3;
        this.spellType = EntitySpellEffect.SpellType.Ice1;
        this.isStaff = true;
        this.hitsWater = true;
        this.tabs.add(CreativeTabs.tabCombat);
    }
}
