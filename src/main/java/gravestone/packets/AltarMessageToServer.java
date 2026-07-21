package gravestone.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import gravestone.core.GSMessageHandler;
import gravestone.item.ItemGSCorpse;
import gravestone.item.corpse.CorpseHelper;
import gravestone.tileentity.TileEntityGSAltar;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class AltarMessageToServer implements IMessage, IMessageHandler<AltarMessageToServer, IMessage> {
   private int playerID;
   private int dimensionID;
   private int x;
   private int y;
   private int z;
   private AltarMessageToServer.MOB_TYPE mobType;

   public AltarMessageToServer() {
   }

   public AltarMessageToServer(EntityPlayer player, int x, int y, int z, AltarMessageToServer.MOB_TYPE mobType) {
      this.playerID = player.getEntityId();
      this.dimensionID = player.worldObj.provider.dimensionId;
      this.x = x;
      this.y = y;
      this.z = z;
      this.mobType = mobType;
   }

   public void fromBytes(ByteBuf buf) {
      this.playerID = buf.readInt();
      this.dimensionID = buf.readInt();
      this.x = buf.readInt();
      this.y = buf.readInt();
      this.z = buf.readInt();
      this.mobType = AltarMessageToServer.MOB_TYPE.getMobType(buf.readInt());
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.playerID);
      buf.writeInt(this.dimensionID);
      buf.writeInt(this.x);
      buf.writeInt(this.y);
      buf.writeInt(this.z);
      buf.writeInt(this.mobType.ordinal());
   }

   public IMessage onMessage(AltarMessageToServer message, MessageContext ctx) {
      if (ctx.side.isServer()) {
         World world = DimensionManager.getWorld(message.dimensionID);
         if (world == null || ctx.getServerHandler().playerEntity != null && ctx.getServerHandler().playerEntity.getEntityId() != message.playerID) {
            return null;
         }

         EntityPlayer player = (EntityPlayer)world.getEntityByID(message.playerID);
         TileEntity te = world.getTileEntity(message.x, message.y, message.z);
         if (te != null && te instanceof TileEntityGSAltar) {
            TileEntityGSAltar tileEntity = (TileEntityGSAltar)te;
            if (tileEntity.hasCorpse()) {
               ItemStack corpse = tileEntity.getCorpse();
               if (corpse != null && corpse.getItem() instanceof ItemGSCorpse && CorpseHelper.tryTakeExperience(player, corpse.getItemDamage())) {
                  boolean spawned = CorpseHelper.spawnMob(corpse.getItemDamage(), tileEntity.getWorldObj(), tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, corpse.stackTagCompound, player);
                  if (spawned) {
                     GSMessageHandler.networkWrapper.sendTo(new AltarMessageToClient(), (EntityPlayerMP)player);
                     tileEntity.setCorpse((ItemStack)null);
                  } else {
                     CorpseHelper.refundExperience(player, corpse.getItemDamage());
                  }
               }
            }
         }
      }

      return null;
   }

   public static enum MOB_TYPE {
      LIVED,
      ZOMBIE,
      SKELETON,
      GHOST;

      public static AltarMessageToServer.MOB_TYPE getMobType(int num) {
         return values().length <= num ? LIVED : values()[num];
      }
   }
}
