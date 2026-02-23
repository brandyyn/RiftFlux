/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.network.simpleimpl.IMessage
 *  cpw.mods.fml.common.network.simpleimpl.IMessageHandler
 *  cpw.mods.fml.common.network.simpleimpl.MessageContext
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.entity.player.EntityPlayer
 */
package de.rinonline.korinrpg.Helper.Network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.rinonline.korinrpg.Springmain;
import net.minecraft.entity.player.EntityPlayer;

public abstract class AbstractMessageHandler<T extends IMessage>
implements IMessageHandler<T, IMessage> {
    @SideOnly(value=Side.CLIENT)
    public abstract IMessage handleClientMessage(EntityPlayer var1, T var2, MessageContext var3);

    public abstract IMessage handleServerMessage(EntityPlayer var1, T var2, MessageContext var3);

    public IMessage onMessage(T message, MessageContext ctx) {
        if (ctx.side.isClient()) {
            return this.handleClientMessage(Springmain.proxy.getPlayerEntity(ctx), message, ctx);
        }
        System.out.println("aaaaa");
        return this.handleServerMessage(Springmain.proxy.getPlayerEntity(ctx), message, ctx);
    }
}

