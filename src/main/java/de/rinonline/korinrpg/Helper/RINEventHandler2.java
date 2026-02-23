/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.gameevent.PlayerEvent$PlayerLoggedInEvent
 *  cpw.mods.fml.common.gameevent.PlayerEvent$PlayerLoggedOutEvent
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraftforge.event.entity.EntityEvent$EntityConstructing
 *  net.minecraftforge.event.entity.EntityJoinWorldEvent
 *  net.minecraftforge.event.entity.living.LivingEvent$LivingUpdateEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$Clone
 */
package de.rinonline.korinrpg.Helper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import de.rinonline.korinrpg.Helper.NBT.RINPlayer2;
import de.rinonline.korinrpg.Helper.Network.SuperPacketDispatcher;
import de.rinonline.korinrpg.Helper.Network.SyncNewPlayerPropsMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class RINEventHandler2 {
    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayer) {
            if (RINPlayer2.get((EntityPlayer)event.entity) == null) {
                RINPlayer2.register((EntityPlayer)event.entity);
            } else {
                SuperPacketDispatcher.sendTo(new SyncNewPlayerPropsMessage((EntityPlayer)event.entity), (EntityPlayerMP)event.entity);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayer) {
            if (RINPlayer2.get(event.player) == null) {
                RINPlayer2.register(event.player);
            } else {
                SuperPacketDispatcher.sendTo(new SyncNewPlayerPropsMessage(event.player), (EntityPlayerMP)event.player);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            SuperPacketDispatcher.sendTo(new SyncNewPlayerPropsMessage(event.player), (EntityPlayerMP)event.player);
        }
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityPlayerMP && RINPlayer2.get((EntityPlayer)event.entity) == null) {
            RINPlayer2.register((EntityPlayer)event.entity);
        }
    }

    @SubscribeEvent
    public void onClonePlayer(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        RINPlayer2.get(event.entityPlayer).copy(RINPlayer2.get(event.original));
        SuperPacketDispatcher.sendTo(new SyncNewPlayerPropsMessage(event.entityPlayer), (EntityPlayerMP)event.entityPlayer);
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.entity;
            RINPlayer2 props = RINPlayer2.get(player);
            props.onUpdate();
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingHurtEvent event) {
        if (event.source.getSourceOfDamage() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.source.getSourceOfDamage();
            RINPlayer2 props = RINPlayer2.get(player);
            props.onAttack();
        }
    }
}
