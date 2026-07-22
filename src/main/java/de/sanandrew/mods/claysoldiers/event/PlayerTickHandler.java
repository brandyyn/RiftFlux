/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.gameevent.TickEvent$Phase
 *  cpw.mods.fml.common.gameevent.TickEvent$PlayerTickEvent
 */
package de.sanandrew.mods.claysoldiers.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import de.sanandrew.mods.claysoldiers.util.CsmPlayerProperties;

public class PlayerTickHandler {
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        CsmPlayerProperties prop;
        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.END && (prop = CsmPlayerProperties.get(event.player)) != null) {
            prop.decrDisruptDelay();
        }
    }
}

