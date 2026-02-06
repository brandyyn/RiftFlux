/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.gameevent.TickEvent$PlayerTickEvent
 *  cpw.mods.fml.common.gameevent.TickEvent$WorldTickEvent
 */
package zelda;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import zelda.Config;

public class TickHandler {
    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
    }

    @SubscribeEvent
    public void worldTick(TickEvent.WorldTickEvent event) {
        if (!Config.HEARTS_ENABLED || event.world == null || event.world.isRemote) {
            return;
        }
        boolean desired = !Config.DISABLE_REGEN;
        boolean current = event.world.getGameRules().getGameRuleBooleanValue("naturalRegeneration");
        if (current != desired) {
            event.world.getGameRules().setOrCreateGameRule("naturalRegeneration", desired ? "true" : "false");
        }
    }
}
