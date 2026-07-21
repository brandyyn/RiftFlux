package gravestone.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.inventory.GraveInventory;
import java.util.List;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.entity.player.EntityPlayer;

public abstract class TileEntityGSGrave extends TileEntity {
   protected GraveInventory inventory;
   protected GSGraveStoneDeathText gSDeathText;
   protected boolean isEditable = true;
   protected boolean isEnchanted = false;
   protected byte graveType = 0;
   protected int age = -1;
   protected String ownerName = "";
   protected String ownerUuid = "";

   public TileEntityGSGrave() {
      this.gSDeathText = new GSGraveStoneDeathText(this);
   }

   public void setGraveType(byte graveType) {
      this.graveType = graveType;
   }

   public byte getGraveTypeNum() {
      return this.graveType;
   }

   protected void readType(NBTTagCompound nbtTag) {
      this.graveType = nbtTag.getByte("GraveType");
   }

   protected void saveType(NBTTagCompound nbtTag) {
      nbtTag.setByte("GraveType", this.graveType);
   }

   public void setGraveContent(Random random, boolean isPetGrave, boolean allLoot) {
      this.gSDeathText.setRandomDeathTextAndName(random, this.graveType, false, true);
      this.inventory.setRandomGraveContent(this.inventory, random, isPetGrave, allLoot);
      this.setRandomAge();
   }

   public GraveInventory getInventory() {
      return this.inventory;
   }

   public String getInvName() {
      return "container.gravestone";
   }

   public void setItems(List<ItemStack> items) {
      this.inventory.setItems(items);
   }

   public void setAdditionalItems(ItemStack[] items) {
      this.inventory.setAdditionalItems(items);
   }

   public void dropAllItems() {
      this.inventory.dropAllItems();
   }

   public GSGraveStoneDeathText getDeathTextComponent() {
      return this.gSDeathText;
   }

   public int getAge() {
      return this.age;
   }

   public void setAge(int age) {
      this.age = age;
   }

   protected void setRandomAge() {
      this.age = 10 + (new Random()).nextInt(100);
   }

   public boolean isEditable() {
      return this.isEditable;
   }

   public boolean isEnchanted() {
      return this.isEnchanted;
   }

   public void setEnchanted(boolean isEnchanted) {
      this.isEnchanted = isEnchanted;
   }

   public void setOwner(EntityPlayer player) {
      if (player != null) {
         this.ownerName = player.getCommandSenderName();
         this.ownerUuid = player.getUniqueID().toString();
         this.markDirty();
      }
   }

   public void setOwner(String ownerName, String ownerUuid) {
      this.ownerName = ownerName == null ? "" : ownerName;
      this.ownerUuid = ownerUuid == null ? "" : ownerUuid;
      this.markDirty();
   }

   public boolean hasOwner() {
      return !this.ownerUuid.isEmpty() || !this.ownerName.isEmpty();
   }

   public boolean isOwner(EntityPlayer player) {
      if (player == null || !this.hasOwner()) {
         return !this.hasOwner();
      }
      return !this.ownerUuid.isEmpty()
         ? this.ownerUuid.equals(player.getUniqueID().toString())
         : this.ownerName.equalsIgnoreCase(player.getCommandSenderName());
   }

   public String getOwnerName() {
      return this.ownerName;
   }

   public String getOwnerUuid() {
      return this.ownerUuid;
   }

   @SideOnly(Side.CLIENT)
   public void setEditable(boolean isEditable) {
      this.isEditable = isEditable;
   }

   public void readFromNBT(NBTTagCompound nbtTag) {
      super.readFromNBT(nbtTag);
      if (nbtTag.hasKey("Enchanted")) {
         this.isEnchanted = nbtTag.getBoolean("Enchanted");
      }

      this.ownerName = nbtTag.getString("GraveOwnerName");
      this.ownerUuid = nbtTag.getString("GraveOwnerUUID");

   }

   public void writeToNBT(NBTTagCompound nbtTag) {
      super.writeToNBT(nbtTag);
      nbtTag.setBoolean("Enchanted", this.isEnchanted);
      if (this.hasOwner()) {
         nbtTag.setString("GraveOwnerName", this.ownerName);
         nbtTag.setString("GraveOwnerUUID", this.ownerUuid);
      }
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
