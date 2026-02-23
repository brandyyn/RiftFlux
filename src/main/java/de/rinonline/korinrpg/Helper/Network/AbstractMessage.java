/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Throwables
 *  cpw.mods.fml.common.network.simpleimpl.IMessage
 *  cpw.mods.fml.common.network.simpleimpl.IMessageHandler
 *  cpw.mods.fml.common.network.simpleimpl.MessageContext
 *  cpw.mods.fml.relauncher.Side
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.PacketBuffer
 */
package de.rinonline.korinrpg.Helper.Network;

import com.google.common.base.Throwables;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import de.rinonline.korinrpg.Springmain;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

public abstract class AbstractMessage<T extends AbstractMessage<T>>
implements IMessage,
IMessageHandler<T, IMessage> {
    protected abstract void read(PacketBuffer var1) throws IOException;

    protected abstract void write(PacketBuffer var1) throws IOException;

    public abstract void process(EntityPlayer var1, Side var2);

    protected boolean isValidOnSide(Side side) {
        return true;
    }

    protected boolean requiresMainThread() {
        return true;
    }

    public void fromBytes(ByteBuf buffer) {
        try {
            this.read(new PacketBuffer(buffer));
        }
        catch (IOException e) {
            throw Throwables.propagate((Throwable)e);
        }
    }

    public void toBytes(ByteBuf buffer) {
        try {
            this.write(new PacketBuffer(buffer));
        }
        catch (IOException e) {
            throw Throwables.propagate((Throwable)e);
        }
    }

    public final IMessage onMessage(T msg, MessageContext ctx) {
        if (ctx.side.name() == "CLIENT") {
            ((AbstractMessage)msg).process(Springmain.proxy.getPlayerEntity(ctx), ctx.side);
        } else if (ctx.side.name() == "SERVER") {
            // empty if block
        }
        ((AbstractMessage)msg).process(Springmain.proxy.getPlayerEntity(ctx), ctx.side);
        return null;
    }

    private void checkThreadAndEnqueue(T msg, MessageContext ctx) {
    }

    public static abstract class AbstractServerMessage<T extends AbstractMessage<T>>
    extends AbstractMessage<T> {
        @Override
        protected final boolean isValidOnSide(Side side) {
            return side.isServer();
        }
    }

    public static abstract class AbstractClientMessage<T extends AbstractMessage<T>>
    extends AbstractMessage<T> {
        @Override
        protected final boolean isValidOnSide(Side side) {
            return side.isClient();
        }
    }
}

