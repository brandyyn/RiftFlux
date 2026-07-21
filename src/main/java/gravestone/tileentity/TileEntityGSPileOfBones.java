package gravestone.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityGSPileOfBones extends TileEntity {
   private byte direction = 0;

   public byte getDirection() {
      return this.direction;
   }

   public void setDirection(byte direction) {
      this.direction = direction;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      super.readFromNBT(nbt);
      this.direction = nbt.getByte("Direction");
   }

   public void writeToNBT(NBTTagCompound nbt) {
      super.writeToNBT(nbt);
      nbt.setByte("Direction", this.direction);
   }

   public boolean canUpdate() {
      return false;
   }

   public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
      this.readFromNBT(packet.func_148857_g());
   }

   public Packet getDescriptionPacket() {
      NBTTagCompound nbtTag = new NBTTagCompound();
      this.writeToNBT(nbtTag);
      return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
   }
}
