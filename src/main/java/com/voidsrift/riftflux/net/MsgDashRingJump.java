package com.voidsrift.riftflux.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.PlayerStarstatsExtension;
import net.nmccoy.legendgear.item.MagicRing;

public class MsgDashRingJump implements IMessage {
    private boolean preserveSprintState;
    private double clientMotionX;
    private double clientMotionZ;

    public MsgDashRingJump() {
    }

    public MsgDashRingJump(boolean preserveSprintState, double clientMotionX, double clientMotionZ) {
        this.preserveSprintState = preserveSprintState;
        this.clientMotionX = clientMotionX;
        this.clientMotionZ = clientMotionZ;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.preserveSprintState = buf.readBoolean();
        this.clientMotionX = buf.readDouble();
        this.clientMotionZ = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.preserveSprintState);
        buf.writeDouble(this.clientMotionX);
        buf.writeDouble(this.clientMotionZ);
    }

    public static class Handler implements IMessageHandler<MsgDashRingJump, IMessage> {
        @Override
        public IMessage onMessage(MsgDashRingJump msg, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null || !MagicRing.PlayerWears(player, MagicRing.RingType.SPEED_RING)) {
                return null;
            }

            boolean physicallyGrounded = player.onGround || player.isOnLadder() || player.isInWater();
            if (physicallyGrounded || PlayerStarstatsExtension.availableMana(player) <= 0.0F) {
                return null;
            }
            if (LegendGear2.CONFIG_DASH_RING_AIR_JUMPS_REQUIRE_SPRINTING && !msg.preserveSprintState) {
                return null;
            }

            if (!LegendGear2.CONFIG_DASH_RING_USE_ORIGINAL_BEHAVIOR) {
                if (LegendGear2.CONFIG_DASH_RING_MAX_AIR_JUMPS <= 0
                        || MagicRing.getDashAirJumps(player) >= LegendGear2.CONFIG_DASH_RING_MAX_AIR_JUMPS) {
                    return null;
                }
            }

            double clientHorizontalSpeedSq = msg.clientMotionX * msg.clientMotionX + msg.clientMotionZ * msg.clientMotionZ;
            double serverHorizontalSpeedSq = player.motionX * player.motionX + player.motionZ * player.motionZ;
            if (!Double.isNaN(clientHorizontalSpeedSq)
                    && !Double.isInfinite(clientHorizontalSpeedSq)
                    && clientHorizontalSpeedSq > serverHorizontalSpeedSq) {
                player.motionX = msg.clientMotionX;
                player.motionZ = msg.clientMotionZ;
            }

            MagicRing.performDashJump(player, msg.preserveSprintState);
            if (!LegendGear2.CONFIG_DASH_RING_USE_ORIGINAL_BEHAVIOR) {
                MagicRing.setDashAirJumps(player, MagicRing.getDashAirJumps(player) + 1);
            }
            if (LegendGear2.CONFIG_DASH_RING_AIR_JUMP_MANA_COST > 0.0F) {
                MagicRing.spendRingMana(player, LegendGear2.CONFIG_DASH_RING_AIR_JUMP_MANA_COST);
            }
            player.playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(player));
            return null;
        }
    }
}
