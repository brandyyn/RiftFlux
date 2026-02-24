package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.blessings.BlessingHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class MsgActivateBlessing implements IMessage {
    private int x;
    private int y;
    private int z;

    public MsgActivateBlessing() {
    }

    public MsgActivateBlessing(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    public static class Handler implements IMessageHandler<MsgActivateBlessing, IMessage> {
        @Override
        public IMessage onMessage(MsgActivateBlessing msg, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null) {
                return null;
            }
            if (!ModConfig.blessingsEnabled) {
                return null;
            }
            String blessing = BlessingHelper.getBlessing(player);
            if (blessing == null) {
                return null;
            }

            BlessingHelper.ensureBlessingState(player);
            boolean active = BlessingHelper.isActive(player);
            int cooldown = BlessingHelper.getCooldown(player);

            if (cooldown > 0) {
                int seconds = cooldown / 20;
                player.addChatComponentMessage(new ChatComponentText("Blessing is on cooldown. (" + seconds + "s)"));
                return null;
            }

            if ("Berserker".equals(blessing)) {
                if (!active) {
                    int counter = BlessingHelper.getCounter(player);
                    if (counter > 0) {
                        BlessingHelper.setTimer(player, 0);
                        BlessingHelper.setActive(player, true);
                        player.addChatComponentMessage(new ChatComponentText("You enter berserk mode."));
                    } else {
                        player.addChatComponentMessage(new ChatComponentText("You have no berserk counters."));
                    }
                } else {
                    BlessingHelper.setActive(player, false);
                    BlessingHelper.setCooldown(player, 1200);
                    BlessingHelper.setTimer(player, 0);
                    player.addChatComponentMessage(new ChatComponentText("You calm down."));
                }
                return null;
            }

            if ("Mechanic".equals(blessing)) {
                player.addChatComponentMessage(new ChatComponentText("No selected trap."));
                return null;
            }

            player.addChatComponentMessage(new ChatComponentText("Your blessing has no active ability."));
            return null;
        }
    }
}
