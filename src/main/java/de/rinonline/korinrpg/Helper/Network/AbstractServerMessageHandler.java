/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.network.simpleimpl.IMessage
 *  cpw.mods.fml.common.network.simpleimpl.MessageContext
 *  net.minecraft.entity.player.EntityPlayer
 */
package de.rinonline.korinrpg.Helper.Network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.rinonline.korinrpg.Helper.Network.AbstractMessageHandler;
import net.minecraft.entity.player.EntityPlayer;

public abstract class AbstractServerMessageHandler<T extends IMessage>
extends AbstractMessageHandler<T> {
    @Override
    public final IMessage handleClientMessage(EntityPlayer player, T message, MessageContext ctx) {
        return null;
    }
}

