/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.Event
 */
package de.sanandrew.mods.claysoldiers.event;

import cpw.mods.fml.common.eventhandler.Event;
import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;

public class SoldierEvent
extends Event {
    public final EntityClayMan clayMan;

    public SoldierEvent(EntityClayMan clayMan) {
        this.clayMan = clayMan;
    }
}

