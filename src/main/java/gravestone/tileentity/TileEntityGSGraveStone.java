package gravestone.tileentity;

import gravestone.block.GraveStoneHelper;
import gravestone.block.enums.EnumGraves;
import gravestone.config.GraveStoneConfig;
import gravestone.core.TimeHelper;
import gravestone.core.event.GSRenderEventHandler;
import gravestone.core.event.GSTickEventHandler;
import gravestone.core.logger.GSLogger;
import gravestone.inventory.GraveInventory;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TileEntityGSGraveStone extends TileEntityGSGrave {
   protected GSGraveStoneSpawn gsSpawn;
   protected ItemStack sword = null;
   protected ItemStack flower = null;
   public static final int FOG_RANGE = 30;

   public TileEntityGSGraveStone() {
      this.gsSpawn = new GSGraveStoneSpawn(this);
      this.inventory = new GraveInventory(this);
   }

   public TileEntityGSGraveStone(World world) {
      this.worldObj = world;
      this.gsSpawn = new GSGraveStoneSpawn(this);
      this.inventory = new GraveInventory(this);
   }

   public void updateEntity() {
      this.gsSpawn.updateEntity();
      if (this.worldObj.isRemote && GraveStoneConfig.isFogEnabled && GSTickEventHandler.getFogTicCount() == 0) {
         EntityPlayer player = this.worldObj.getClosestPlayer((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D, 30.0D);
         if (player != null && player.getCommandSenderName().equals(Minecraft.getMinecraft().thePlayer.getCommandSenderName()) && TimeHelper.isFogTime(this.worldObj)) {
            GSRenderEventHandler.addFog();
         }
      }

   }

   public static boolean isFogTime(World world) {
      if (world.isRaining()) {
         return false;
      } else {
         long dayTime = TimeHelper.getDayTime(world);
         return dayTime > 12000L && dayTime < 24000L;
      }
   }

   public boolean receiveClientEvent(int par1, int par2) {
      if (par1 == 1 && this.worldObj.isRemote) {
         this.gsSpawn.setMinDelay();
      }

      return true;
   }

   public void readFromNBT(NBTTagCompound nbtTag) {
      super.readFromNBT(nbtTag);
      this.readType(nbtTag);
      this.inventory.readItems(nbtTag);
      this.gSDeathText.readText(nbtTag);
      if (nbtTag.hasKey("Age")) {
         this.age = nbtTag.getInteger("Age");
      }

      this.readSwordInfo(nbtTag);
      this.readFlowerInfo(nbtTag);
   }

   public void writeToNBT(NBTTagCompound nbtTag) {
      super.writeToNBT(nbtTag);
      this.saveType(nbtTag);
      this.inventory.saveItems(nbtTag);
      this.gSDeathText.saveText(nbtTag);
      nbtTag.setInteger("Age", this.age);
      this.writeSwordInfo(nbtTag);
      this.writeFlowerInfo(nbtTag);
   }

   private void readSwordInfo(NBTTagCompound nbtTag) {
      if ((this.graveType <= 4 || this.graveType >= 10) && !nbtTag.hasKey("SwordGrave")) {
         if (nbtTag.hasKey("Sword")) {
            this.sword = GraveStoneHelper.loadSwordInfo(nbtTag);
         }
      } else {
         this.convertSword(nbtTag);
      }

   }

   private void writeSwordInfo(NBTTagCompound nbtTag) {
      if (this.sword != null) {
         NBTTagCompound swordNBT = new NBTTagCompound();
         this.sword.writeToNBT(swordNBT);
         nbtTag.setTag("Sword", swordNBT);
      }

   }

   private void readFlowerInfo(NBTTagCompound nbtTag) {
      if (nbtTag.hasKey("Flower")) {
         this.flower = ItemStack.loadItemStackFromNBT(nbtTag.getCompoundTag("Flower"));
      }

   }

   private void writeFlowerInfo(NBTTagCompound nbtTag) {
      if (this.flower != null) {
         NBTTagCompound flowerNBT = new NBTTagCompound();
         this.flower.writeToNBT(flowerNBT);
         nbtTag.setTag("Flower", flowerNBT);
      }

   }

   public ItemStack getSword() {
      return this.sword;
   }

   public void setSword(ItemStack sword) {
      this.sword = sword;
   }

   public void dropSword() {
      if (this.sword != null) {
         GraveInventory var10000 = this.inventory;
         GraveInventory.dropItem(this.sword, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
      }

   }

   public boolean isSwordGrave() {
      return this.sword != null;
   }

   public ItemStack getFlower() {
      return this.flower;
   }

   public void setFlower(ItemStack flower) {
      this.flower = flower;
   }

   public void dropFlower() {
      if (this.flower != null) {
         GraveInventory var10000 = this.inventory;
         GraveInventory.dropItem(this.flower, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
      }

   }

   public boolean hasFlower() {
      return this.flower != null;
   }

   public EnumGraves getGraveType() {
      return EnumGraves.getByID(this.graveType);
   }

   public boolean isEmpty() {
      return this.inventory.isEmpty();
   }

   private void convertSword(NBTTagCompound nbtTag) {
      GSLogger.logInfo("Start converting sword gravestone!");

      try {
         byte swordType = nbtTag.getByte("SwordType");
         if (swordType == 0) {
            swordType = (byte)(this.graveType - 4);
         }

         Item sword;
         switch(swordType) {
         case 2:
            sword = Items.stone_sword;
            break;
         case 3:
            sword = Items.iron_sword;
            break;
         case 4:
            sword = Items.golden_sword;
            break;
         case 5:
            sword = Items.diamond_sword;
            break;
         default:
            sword = Items.wooden_sword;
         }

         GSLogger.logInfo("Sword type - " + nbtTag.getByte("SwordType") + ". Will be converted to " + sword.getUnlocalizedName());
         int damage = 0;
         if (nbtTag.hasKey("SwordDamage")) {
            damage = nbtTag.getInteger("SwordDamage");
            GSLogger.logInfo("Sword damage - " + damage);
         }

         ItemStack stack = new ItemStack(sword, 1, damage);
         if (nbtTag.hasKey("SwordName")) {
            stack.setStackDisplayName(nbtTag.getString("SwordName"));
            GSLogger.logInfo("Sword name - " + nbtTag.getString("SwordName"));
         }

         if (nbtTag.hasKey("SwordNBT")) {
            stack.setTagCompound(nbtTag.getCompoundTag("SwordNBT"));
         }

         this.sword = stack;
      } catch (Exception var6) {
         GSLogger.logError("Something went wrong!!!");
         var6.printStackTrace();
      }

      GSLogger.logInfo("Gravestone converting complete!");
   }

   public void setGraveContent(Random random, boolean isPetGrave, boolean allLoot) {
      super.setGraveContent(random, isPetGrave, allLoot);
      this.setRandomFlower(random);
   }

   public void setRandomFlower(Random random) {
      if (random.nextInt(4) == 0) {
         ItemStack flower = new ItemStack(GraveStoneHelper.FLOWERS.get(random.nextInt(GraveStoneHelper.FLOWERS.size())), 1);
         if (GraveStoneHelper.canFlowerBePlaced(this.worldObj, this.xCoord, this.yCoord, this.zCoord, flower, this)) {
            this.setFlower(flower);
         }
      }

   }
}
