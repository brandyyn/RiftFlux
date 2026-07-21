package gravestone.tileentity;

import gravestone.block.enums.EnumHauntedChest;
import gravestone.config.GraveStoneConfig;
import gravestone.core.GSMobSpawn;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileEntityGSHauntedChest extends TileEntity {
   private int openTicks = 0;
   private boolean isOpen = false;
   public float lidAngle;
   public float prevLidAngle;
   private EnumHauntedChest chestType = EnumHauntedChest.BATS_CHEST;

   public void updateEntity() {
      super.updateEntity();
      if (this.openTicks > 0) {
         --this.openTicks;
      }

      if (this.worldObj.isRemote) {
         this.prevLidAngle = this.lidAngle;
         float f = 0.1F;
         if (this.openTicks > 0 && this.lidAngle == 0.0F) {
            double d1 = (double)this.xCoord + 0.5D;
            double d0 = (double)this.zCoord + 0.5D;
            this.worldObj.playSoundEffect(d1, (double)this.yCoord + 0.5D, d0, "random.chestopen", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
         }

         if (this.openTicks == 0 && this.lidAngle > 0.0F || this.openTicks > 0 && this.lidAngle < 1.0F) {
            float f1 = this.lidAngle;
            if (this.openTicks > 0) {
               this.lidAngle += f;
            } else {
               this.lidAngle -= f;
            }

            if (this.lidAngle > 1.0F) {
               this.lidAngle = 1.0F;
            }

            float f2 = 0.5F;
            if (this.lidAngle < f2 && f1 >= f2) {
               double d0 = (double)this.xCoord + 0.5D;
               double d2 = (double)this.zCoord + 0.5D;
               this.worldObj.playSoundEffect(d0, (double)this.yCoord + 0.5D, d2, "random.chestclosed", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
            }

            if (this.lidAngle < 0.0F) {
               this.lidAngle = 0.0F;
            }
         }
      } else if (this.openTicks == 45) {
         this.spawnMobs(this.worldObj);
      }

      if (this.openTicks == 0) {
         if (this.isOpen && GraveStoneConfig.replaceHauntedChest) {
            int meta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
            this.getWorldObj().removeTileEntity(this.xCoord, this.yCoord, this.zCoord);
            this.getWorldObj().setBlock(this.xCoord, this.yCoord, this.zCoord, Blocks.chest);
            this.getWorldObj().setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, meta, 2);
         }

         this.isOpen = false;
      }

   }

   public void openChest() {
      if (this.openTicks == 0) {
         this.openTicks = 50;
         this.isOpen = true;
      }

   }

   public EnumHauntedChest getChestType() {
      return this.chestType;
   }

   public void setChestType(EnumHauntedChest chestType) {
      this.chestType = chestType;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      super.readFromNBT(nbt);
      this.chestType = EnumHauntedChest.getById(nbt.getByte("ChestType"));
   }

   public void writeToNBT(NBTTagCompound nbt) {
      super.writeToNBT(nbt);
      nbt.setByte("ChestType", (byte)this.chestType.ordinal());
   }

   public void spawnMobs(World world) {
      switch(this.getChestType()) {
      case SKELETON_CHEST:
         EntitySkeleton skeleton = GSMobSpawn.getSkeleton(world, (byte)1);
         skeleton.setLocationAndAngles((double)this.xCoord + 0.5D, (double)this.yCoord, (double)this.zCoord + 0.5D, 0.0F, 0.0F);
         world.spawnEntityInWorld(skeleton);
         break;
      case BATS_CHEST:
      default:
         int batsCount = 15;

         for(byte i = 0; i < batsCount; ++i) {
            EntityBat bat = new EntityBat(world);
            bat.setLocationAndAngles((double)this.xCoord + 0.5D, (double)this.yCoord + 0.7D, (double)this.zCoord + 0.5D, 0.0F, 0.0F);
            world.spawnEntityInWorld(bat);
         }
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
