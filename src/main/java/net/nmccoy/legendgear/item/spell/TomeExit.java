/*
 * Decompiled with CFR 0.152.
 */
package net.nmccoy.legendgear.item.spell;

import net.nmccoy.legendgear.entity.EntitySpellEffect;
import net.nmccoy.legendgear.item.spell.SpellItem;

public class TomeExit
extends SpellItem {
    public TomeExit() {
        this.setUnlocalizedName("tomeExit");
        this.setTextureName("legendgear:spellbook");
        this.setMaxDamage(32);
        this.baseArcanePower = 0.0;
        this.baseMeleeDamage = 0;
        this.baseCastRadius = 5.0;
        this.baseCastRange = 6.0;
        this.baseCastTime = 4.0;
        this.spellType = EntitySpellEffect.SpellType.Exit;
        this.isStaff = false;
    }
}

