/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.network.simpleimpl.IMessage
 *  cpw.mods.fml.common.network.simpleimpl.IMessageHandler
 *  cpw.mods.fml.common.network.simpleimpl.MessageContext
 *  net.minecraft.client.Minecraft
 *  net.minecraft.entity.player.EntityPlayer
 */
package net.nmccoy.legendgear.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.nmccoy.legendgear.PlayerStarstatsExtension;
import net.nmccoy.legendgear.network.StarwellMessage;

public class StarwellMessageHandler
implements IMessageHandler<StarwellMessage, IMessage> {
    public IMessage onMessage(StarwellMessage message, MessageContext ctx) {
        PlayerStarstatsExtension.get((EntityPlayer)Minecraft.getMinecraft().thePlayer).starwellCharge = message.chargeLevel;
        return null;
    }
}

