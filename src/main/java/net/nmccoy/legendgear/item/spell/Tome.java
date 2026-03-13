/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 */
package net.nmccoy.legendgear.item.spell;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.nmccoy.legendgear.entity.EntitySpellEffect;
import net.nmccoy.legendgear.item.spell.SpellItem;

public class Tome
extends SpellItem {
    public TomeType type;
    public IIcon baseIcon;
    public IIcon symbolIcon;
    public IIcon pagesIcon;

    public Tome(TomeType type) {
        this.type = type;
        this.spellType = type.spell;
        this.baseArcanePower = 12.0;
        if (type == TomeType.EXEUNT) {
            this.baseArcanePower = 5.0;
        }
        this.setMaxDamage(16);
        this.baseStaminaCost = 8.0f;
        this.baseMeleeDamage = 0;
        this.baseCastRange = 9.0;
        this.baseCastRadius = 7.0;
        this.baseCastTime = 3.0;
        this.isTome = true;
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister ireg) {
        this.baseIcon = ireg.registerIcon("legendgear:whiteBook");
        this.symbolIcon = ireg.registerIcon("legendgear:spellbookSymbol2");
        this.pagesIcon = ireg.registerIcon("legendgear:bookPages");
    }

    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    public String getUnlocalizedName(ItemStack stack) {
        return "item.tome." + this.type.toString();
    }

    public int getRenderPasses(int metadata) {
        return 3;
    }

    public IIcon getIcon(ItemStack stack, int pass) {
        if (pass == 0) {
            return this.baseIcon;
        }
        if (pass == 1) {
            return this.symbolIcon;
        }
        return this.pagesIcon;
    }

    public int getColorFromItemStack(ItemStack stack, int pass) {
        if (pass == 0) {
            return this.type.colorBase;
        }
        if (pass == 1) {
            return this.type.colorSymbol;
        }
        return 0xFFFFFF;
    }

    public static enum TomeType {
        SCYTHEWIND(EntitySpellEffect.SpellType.ScytheWind, 0x33AAFF, 0xEEEEEE),
        RAYFIRE(EntitySpellEffect.SpellType.Rayfire, 0xDD0055, 0xFFFFAA),
        EXEUNT(EntitySpellEffect.SpellType.Exit, 0x664411, 0x66AAFF);

        public EntitySpellEffect.SpellType spell;
        public Object[] ingredients;
        public int colorBase;
        public int colorSymbol;

        private TomeType(EntitySpellEffect.SpellType spell, int baseColor, int symbolColor) {
            this.spell = spell;
            this.colorBase = baseColor;
            this.colorSymbol = symbolColor;
        }
    }
}

