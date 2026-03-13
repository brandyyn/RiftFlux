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

public class StaffFire
extends SpellItem {
    public StaffFire() {
        this.setUnlocalizedName("fireStaff");
        this.setTextureName("legendgear:fireStaffWood");
        this.setMaxDamage(Math.max(0, ModConfig.legendGearFireStaffDurability));
        this.baseArcanePower += 2.0;
        this.baseStaminaCost += 1.0f;
        this.baseMeleeDamage = 3;
        this.spellType = EntitySpellEffect.SpellType.Fire1;
        this.isStaff = true;
        this.tabs.add(CreativeTabs.tabCombat);
    }
}
