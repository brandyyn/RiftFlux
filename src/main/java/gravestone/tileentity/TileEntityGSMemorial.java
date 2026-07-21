package gravestone.tileentity;

import gravestone.block.enums.EnumHangedMobs;
import gravestone.block.enums.EnumMemorials;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TileEntityGSMemorial extends TileEntityGSGrave {
   private EnumHangedMobs hangedMob = EnumHangedMobs.NONE;
   private int hangedVillagerProfession = 0;

   public TileEntityGSMemorial() {
   }

   public TileEntityGSMemorial(World world) {
      this();
      this.worldObj = world;
   }

   public boolean receiveClientEvent(int par1, int par2) {
      return true;
   }

   public void readFromNBT(NBTTagCompound nbtTag) {
      super.readFromNBT(nbtTag);
      this.readType(nbtTag);
      this.gSDeathText.readText(nbtTag);
      this.hangedMob = EnumHangedMobs.getByID(nbtTag.getByte("HangedMob"));
      this.hangedVillagerProfession = nbtTag.getInteger("HangedVillagerProfession");
   }

   public void writeToNBT(NBTTagCompound nbtTag) {
      super.writeToNBT(nbtTag);
      this.saveType(nbtTag);
      this.gSDeathText.saveText(nbtTag);
      nbtTag.setByte("HangedMob", (byte)this.hangedMob.ordinal());
      nbtTag.setInteger("HangedVillagerProfession", this.hangedVillagerProfession);
   }

   public void setMemorialContent(Random random) {
      this.gSDeathText.setRandomDeathTextAndName(random, this.graveType, true, true);
   }

   public void setRandomMob(Random random) {
      this.hangedMob = EnumHangedMobs.values()[random.nextInt(EnumHangedMobs.values().length)];
   }

   public GSGraveStoneDeathText getDeathTextComponent() {
      return this.gSDeathText;
   }

   public boolean canUpdate() {
      return false;
   }

   public EnumMemorials getMemorialType() {
      return EnumMemorials.getByID(this.graveType);
   }

   public int getHangedVillagerProfession() {
      return this.hangedVillagerProfession;
   }

   public void setHangedVillagerProfession(int hangedVillagerProfession) {
      this.hangedVillagerProfession = hangedVillagerProfession;
   }

   public EnumHangedMobs getHangedMob() {
      return this.hangedMob;
   }

   public void setHangedMob(EnumHangedMobs hangedMob) {
      this.hangedMob = hangedMob;
   }
}
