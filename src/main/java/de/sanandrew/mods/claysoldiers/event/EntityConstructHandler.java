/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.event.entity.EntityEvent$EntityConstructing
 */
package de.sanandrew.mods.claysoldiers.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.sanandrew.mods.claysoldiers.util.CsmPlayerProperties;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent;

public class EntityConstructHandler {
    @SubscribeEvent
    public void onEntityConstruct(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayer && CsmPlayerProperties.get((EntityPlayer)event.entity) == null) {
            CsmPlayerProperties.register((EntityPlayer)event.entity);
        }
    }
}

