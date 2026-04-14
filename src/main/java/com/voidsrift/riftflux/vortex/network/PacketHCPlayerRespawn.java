package com.voidsrift.riftflux.vortex.network;

import com.voidsrift.riftflux.vortex.respawn.RespawnDelayHelper;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import com.voidsrift.riftflux.vortex.lib.helper.WorldHelper;

public class PacketHCPlayerRespawn implements IMessage, IMessageHandler<PacketHCPlayerRespawn, IMessage> {
   private int dim;
   private String playerUuid;
   private boolean doToggle;
   private boolean disableGui;

   public PacketHCPlayerRespawn() {
   }

   public PacketHCPlayerRespawn(EntityPlayer player, boolean doToggle, boolean disableGui) {
      this.dim = player.worldObj.provider.dimensionId;
      this.playerUuid = player.getPersistentID().toString();
      this.doToggle = doToggle;
      this.disableGui = disableGui;
   }

   public PacketHCPlayerRespawn(EntityPlayer player) {
      this(player, true, true);
   }

   public void fromBytes(ByteBuf buf) {
      this.dim = buf.readInt();
      this.playerUuid = ByteBufUtils.readUTF8String(buf);
      this.doToggle = buf.readBoolean();
      this.disableGui = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.dim);
      ByteBufUtils.writeUTF8String(buf, this.playerUuid);
      buf.writeBoolean(this.doToggle);
      buf.writeBoolean(this.disableGui);
   }

   public IMessage onMessage(PacketHCPlayerRespawn message, MessageContext ctx) {
      World world = DimensionManager.getWorld(message.dim);
      if (world != null && (ctx.getServerHandler().playerEntity == null || ctx.getServerHandler().playerEntity.getPersistentID().equals(UUID.fromString(message.playerUuid)))) {
         EntityPlayer player = ctx.getServerHandler().playerEntity;
         EntityPlayerMP reviver = MinecraftServer.getServer().getConfigurationManager().respawnPlayer((EntityPlayerMP)player, message.dim, false);
         reviver.playerNetServerHandler.playerEntity = reviver;
         RespawnDelayHelper.clearDeathTimer(reviver);
         RespawnDelayHelper.sync(reviver);
         RespawnDelayHelper.markPendingSync(reviver, 10);
         RespawnDelayHelper.markPendingRespawnStabilize(reviver, 10);
         if (message.doToggle) {
            WorldHelper.setPlayerHCRevive(player, false);
            return new PacketWorldDataSync(WorldHelper.getGlobalCustomData(world).getData(), message.disableGui);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }
}
