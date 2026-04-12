/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.network.FMLNetworkEvent$ClientCustomPacketEvent
 *  cpw.mods.fml.common.network.FMLNetworkEvent$ServerCustomPacketEvent
 *  cpw.mods.fml.common.network.internal.FMLProxyPacket
 *  cpw.mods.fml.relauncher.Side
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.network.NetHandlerPlayServer
 *  net.minecraftforge.common.config.Property
 */
package assets.levelup;

import assets.levelup.ClassBonus;
import assets.levelup.FMLEventHandler;
import assets.levelup.LevelUp;
import assets.levelup.PlayerExtendedProperties;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.common.config.Property;

public final class SkillPacketHandler {
    public static final String[] CHAN = new String[]{"LEVELUPINIT", "LEVELUPCLASSES", "LEVELUPSKILLS", "LEVELUPCFG"};

    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
        if (event.packet.channel().equals(CHAN[1])) {
            this.handleClassChange(event.packet.payload().readByte(), ((NetHandlerPlayServer)event.handler).playerEntity);
        } else if (event.packet.channel().equals(CHAN[2])) {
            this.handlePacket(event.packet, (EntityPlayer)((NetHandlerPlayServer)event.handler).playerEntity);
        }
    }

    private void handleClassChange(byte newClass, EntityPlayerMP entityPlayerMP) {
        if (newClass >= 0) {
            PlayerExtendedProperties.from((EntityPlayer)entityPlayerMP).setPlayerClass(newClass);
            FMLEventHandler.INSTANCE.loadPlayer((EntityPlayer)entityPlayerMP);
        }
    }

    @SubscribeEvent
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
        if (event.packet.channel().equals(CHAN[0])) {
            this.handlePacket(event.packet, LevelUp.proxy.getPlayer());
        } else if (event.packet.channel().equals(CHAN[3])) {
            this.handleConfig(event.packet);
        }
    }

    private void handlePacket(FMLProxyPacket packet, EntityPlayer player) {
        ByteBuf buf = packet.payload();
        byte button = buf.readByte();
        int[] data = null;
        int sum = 0;
        if (packet.channel().equals(CHAN[0]) || button == -1) {
            data = new int[ClassBonus.skillNames.length];
            for (int i = 0; i < data.length; ++i) {
                data[i] = buf.readInt();
                sum += data[i];
            }
        }
        PlayerExtendedProperties properties = PlayerExtendedProperties.from(player);
        if (packet.channel().equals(CHAN[2])) {
            if (properties.hasClass() && data != null && button == -1 && sum == 0 && data[data.length - 1] != 0 && -data[data.length - 1] <= properties.getSkillFromIndex("XP")) {
                for (int index = 0; index < data.length; ++index) {
                    if (data[index] == 0) continue;
                    properties.addToSkill(ClassBonus.skillNames[index], data[index]);
                }
                FMLEventHandler.INSTANCE.loadPlayer(player);
            }
        } else if (packet.channel().equals(CHAN[0]) && data != null) {
            properties.setPlayerClass(button);
            properties.setPlayerData(data);
        }
    }

    public static FMLProxyPacket getPacket(Side side, int channel, byte id, int ... dat) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte((int)id);
        if ((id < 0 || channel == 0) && dat != null) {
            for (int da : dat) {
                buf.writeInt(da);
            }
        }
        FMLProxyPacket pkt = new FMLProxyPacket(buf, CHAN[channel]);
        pkt.setTarget(side);
        return pkt;
    }

    public static FMLProxyPacket getConfigPacket(Property ... dat) {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < dat.length; ++i) {
            if (i == 2) {
                buf.writeDouble(dat[i].getDouble());
                continue;
            }
            if (i < 4) {
                buf.writeInt(dat[i].getInt());
                continue;
            }
            buf.writeBoolean(dat[i].getBoolean());
        }
        FMLProxyPacket pkt = new FMLProxyPacket(buf, CHAN[3]);
        pkt.setTarget(Side.CLIENT);
        return pkt;
    }

    private void handleConfig(FMLProxyPacket packet) {
        ByteBuf buf = packet.payload();
        Property[] properties = LevelUp.instance.getServerProperties();
        for (int i = 0; i < properties.length; ++i) {
            if (i == 2) {
                properties[i].set(buf.readDouble());
                continue;
            }
            if (i < 4) {
                properties[i].set(buf.readInt());
                continue;
            }
            properties[i].set(buf.readBoolean());
        }
        LevelUp.instance.useServerProperties();
    }
}

