/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.event.world.WorldEvent
 */
package de.sanandrew.core.manpack.util.event.entity;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

public class CollidingEntityCheckEvent
extends WorldEvent {
    public final List entityList;
    public final Entity checkingEntity;
    public final AxisAlignedBB checkedBB;

    public CollidingEntityCheckEvent(World world, List entityList, Entity checkingEntity, AxisAlignedBB checkedBB) {
        super(world);
        this.entityList = entityList;
        this.checkingEntity = checkingEntity;
        this.checkedBB = checkedBB;
    }
}

