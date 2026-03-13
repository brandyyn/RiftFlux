/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraft.entity.ai.attributes.IAttribute
 *  net.minecraft.entity.ai.attributes.RangedAttribute
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraftforge.event.entity.EntityEvent$EntityConstructing
 */
package net.nmccoy.legendgear;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent;

public class CustomAttributes {
    public static final IAttribute criticalBonusDamage = new RangedAttribute("lg2.criticalBonus", 0.0, 0.0, Double.MAX_VALUE).setDescription("Critical Damage Bonus");
    public static final IAttribute arcanePower = new RangedAttribute("lg2.arcanePower", 0.0, 0.0, Double.MAX_VALUE).setDescription("Arcane Power");
    public static final IAttribute spellDamageRatio = new RangedAttribute("lg2.spellDamageRatio", 1.0, 0.0, Double.MAX_VALUE).setDescription("Spell Damage Ratio");
    public static final IAttribute spellDurationRatio = new RangedAttribute("lg2.spellDurationRatio", 1.0, 0.0, Double.MAX_VALUE).setDescription("Spell Duration Ratio");
    public static final IAttribute spellSurgePower = new RangedAttribute("lg2.spellSurgePower", 0.0, 0.0, Double.MAX_VALUE).setDescription("Spellsurge Power");
    public static final IAttribute armorPenaltyScaling = new RangedAttribute("lg2.armorPenaltyScaling", 1.0, 0.0, Double.MAX_VALUE).setDescription("Armor Penalty Multiplier");
    public static final IAttribute spellRange = new RangedAttribute("lg2.spellRange", 0.0, 0.0, Double.MAX_VALUE).setDescription("Spell Range").setShouldWatch(true);
    public static final IAttribute spellRadius = new RangedAttribute("lg2.spellRadius", 0.0, 0.0, Double.MAX_VALUE).setDescription("Spell Radius").setShouldWatch(true);

    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.entity;
            player.getAttributeMap().registerAttribute(criticalBonusDamage);
            player.getAttributeMap().registerAttribute(arcanePower);
            player.getAttributeMap().registerAttribute(spellSurgePower);
            player.getAttributeMap().registerAttribute(spellDamageRatio);
            player.getAttributeMap().registerAttribute(spellDurationRatio);
            player.getAttributeMap().registerAttribute(armorPenaltyScaling);
            player.getAttributeMap().registerAttribute(spellRange);
            player.getAttributeMap().registerAttribute(spellRadius);
        }
    }
}

