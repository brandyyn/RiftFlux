package gravestone.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityGSSkullCandle extends TileEntity {
   private byte rotation;

   public void writeToNBT(NBTTagCompound nbt) {
      super.writeToNBT(nbt);
      nbt.setByte("Rotation", this.rotation);
   }

   public void readFromNBT(NBTTagCompound nbt) {
      super.readFromNBT(nbt);
      this.rotation = nbt.getByte("Rotation");
   }

   public Packet getDescriptionPacket() {
      NBTTagCompound nbt = new NBTTagCompound();
      this.writeToNBT(nbt);
      return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 4, nbt);
   }

   public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
      this.readFromNBT(packet.func_148857_g());
   }

   public boolean canUpdate() {
      return false;
   }

   public byte getRotation() {
      return this.rotation;
   }

   public void setRotation(byte rotation) {
      this.rotation = rotation;
   }
}
