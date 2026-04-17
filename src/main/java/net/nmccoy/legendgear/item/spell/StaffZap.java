/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.material.Material
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.Vec3
 */
package net.nmccoy.legendgear.item.spell;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.nmccoy.legendgear.entity.EntitySpellEffect;
import net.nmccoy.legendgear.item.spell.SpellItem;

public class StaffZap
extends SpellItem {
    public StaffZap() {
        this.setUnlocalizedName("zapStaff");
        this.setTextureName("legendgear:zapStaffWood");
        this.setMaxDamage(Math.max(0, ModConfig.legendGearZapStaffDurability));
        this.baseArcanePower += 2.0;
        this.baseStaminaCost = Math.max(0.0f, ModConfig.legendGearZapStaffManaCost);
        this.baseMeleeDamage = 3;
        this.spellType = EntitySpellEffect.SpellType.Lightning1;
        this.isStaff = true;
        this.hitsWater = true;
        this.tabs.add(CreativeTabs.tabCombat);
    }

    @Override
    public float getManaCost() {
        return Math.max(0.0f, ModConfig.legendGearZapStaffManaCost);
    }

    @Override
    public void GenerateSpell(EntityPlayer caster, EntitySpellEffect.SpellType id, Vec3 location, double radius, double power, boolean critical) {
        AxisAlignedBB bb = AxisAlignedBB.getBoundingBox((double)(location.xCoord - 0.5), (double)(location.yCoord - 0.5), (double)(location.zCoord - 0.5), (double)(location.xCoord + 0.5), (double)(location.yCoord - 0.5), (double)(location.zCoord - 0.5));
        if (caster.worldObj.isMaterialInBB(bb, Material.water)) {
            super.GenerateSpell(caster, id, location, radius, power, critical);
        } else {
            super.GenerateSpell(caster, id, location, radius, power, critical);
        }
    }
}
