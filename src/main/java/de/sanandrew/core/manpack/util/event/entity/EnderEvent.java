/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.event.entity.living.LivingEvent
 */
package de.sanandrew.core.manpack.util.event.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraftforge.event.entity.living.LivingEvent;

public class EnderEvent
extends LivingEvent {
    public final EntityEnderman entityEnderman;

    public EnderEvent(EntityEnderman entity) {
        super((EntityLivingBase)entity);
        this.entityEnderman = entity;
    }
}

