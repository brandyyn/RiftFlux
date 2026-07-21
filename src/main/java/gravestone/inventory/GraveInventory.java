package gravestone.inventory;

import gravestone.block.GraveStoneHelper;
import gravestone.block.enums.EnumGraves;
import gravestone.config.GraveStoneConfig;
import gravestone.tileentity.TileEntityGSGraveStone;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class GraveInventory implements IInventory {
   public static final int DEFAULT_INVENTORY_SIZE = 54;
   private TileEntityGSGraveStone tileEntity;
   protected List<ItemStack> items = new ArrayList<>(54);
   private static final int[] POTION_LIST = new int[]{16273, 16307, 16341, 16310, 16281, 16318, 32657, 32658, 32659, 32725, 32694, 32665, 32702};
   private static final int EGG_PIG = 90;
   private static final int EGG_SHEEP = 91;
   private static final int EGG_COW = 92;
   private static final int EGG_CHICKEN = 93;
   private static final int EGG_SQUID = 94;
   private static final int EGG_WOLF = 95;
   private static final int EGG_MUSHROOM_COW = 96;
   private static final int EGG_CAT = 98;
   private static final int EGG_HORSE = 100;
   private static final int EGG_VILLAGER = 120;

   public GraveInventory(TileEntityGSGraveStone tileEntity) {
      this.tileEntity = tileEntity;
   }

   public void readItems(NBTTagCompound nbtTag) {
      NBTTagList ntbItemsList = nbtTag.getTagList("Items", 10);
      this.items = new ArrayList<>(54);

      for(int i = 0; i < ntbItemsList.tagCount(); ++i) {
         NBTTagCompound nbt = ntbItemsList.getCompoundTagAt(i);
         ItemStack stack = ItemStack.loadItemStackFromNBT(nbt);
         if (stack != null) {
            this.items.add(stack);
         }
      }

   }

   public void saveItems(NBTTagCompound nbtTag) {
      NBTTagList ntbList = new NBTTagList();

      for(ItemStack stack : this.items) {
         if (stack != null) {
            NBTTagCompound nbt = new NBTTagCompound();
            stack.writeToNBT(nbt);
            ntbList.appendTag(nbt);
         }
      }

      nbtTag.setTag("Items", ntbList);
   }

   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   public void addInventoryContent(ItemStack itemStack) {
      if (itemStack != null) {
         this.items.add(itemStack);
      }

   }

   public int getSizeInventory() {
      return this.items.size();
   }

   public int getSizeInventoryForGui() {
      return this.items.size() > 54 ? this.items.size() : 54;
   }

   public ItemStack getStackInSlot(int slot) {
      return slot < this.items.size() ? this.items.get(slot) : null;
   }

   public ItemStack decrStackSize(int slot, int amount) {
      ItemStack stack = this.getStackInSlot(slot);
      if (stack != null) {
         if (stack.stackSize <= amount) {
            this.setInventorySlotContents(slot, (ItemStack)null);
         } else {
            stack = stack.splitStack(amount);
            if (stack.stackSize == 0) {
               this.setInventorySlotContents(slot, (ItemStack)null);
            }
         }
      }

      return stack;
   }

   public ItemStack getStackInSlotOnClosing(int slot) {
      ItemStack stack = this.getStackInSlot(slot);
      if (stack != null) {
         this.setInventorySlotContents(slot, (ItemStack)null);
      }

      return stack;
   }

   public void setInventorySlotContents(int slot, ItemStack stack) {
      if (slot < this.items.size()) {
         this.items.set(slot, stack);
      }

   }

   public String getInventoryName() {
      return "";
   }

   public boolean hasCustomInventoryName() {
      return false;
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public void markDirty() {
   }

   public boolean isUseableByPlayer(EntityPlayer player) {
      return player.worldObj.getTileEntity(this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord) == this.tileEntity && player.getDistanceSq((double)this.tileEntity.xCoord + 0.5D, (double)this.tileEntity.yCoord + 0.5D, (double)this.tileEntity.zCoord + 0.5D) < 64.0D;
   }

   public void openInventory() {
   }

   public void closeInventory() {
   }

   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return false;
   }

   public void setItems(List<ItemStack> items) {
      if (items != null) {
         switch(GraveStoneConfig.graveItemsCount) {
         case 0:
            for(ItemStack item : items) {
               dropItem(item, this.tileEntity.getWorldObj(), this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord);
            }
            break;
         case 40:
            for(ItemStack item : items) {
               this.addInventoryContent(item);
            }
            break;
         default:
            int savedItems = GraveStoneConfig.graveItemsCount;
            Collections.shuffle(Arrays.asList(items.size()), new Random());

            for(ItemStack item : items) {
               if (item != null && savedItems > 0) {
                  this.addInventoryContent(item);
                  --savedItems;
               } else {
                  dropItem(item, this.tileEntity.getWorldObj(), this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord);
               }
            }
         }
      }

   }

   public void setAdditionalItems(ItemStack[] items) {
      if (items != null) {
         for(ItemStack item : items) {
            this.addInventoryContent(item);
         }
      }

   }

   public void setAdditionalItems(List<ItemStack> items) {
      if (items != null) {
         for(ItemStack item : items) {
            this.addInventoryContent(item);
         }
      }

   }

   public static void dropItem(ItemStack items, World world, int x, int y, int z) {
      if (items != null) {
         Random random = new Random();
         float var10 = random.nextFloat() * 0.8F + 0.1F;
         float var11 = random.nextFloat() * 0.8F + 0.1F;

         EntityItem entityItem;
         for(float var12 = random.nextFloat() * 0.8F + 0.1F; items.stackSize > 0; world.spawnEntityInWorld(entityItem)) {
            int var13 = random.nextInt(21) + 10;
            if (var13 > items.stackSize) {
               var13 = items.stackSize;
            }

            items.stackSize -= var13;
            entityItem = new EntityItem(world, (double)((float)x + var10), (double)((float)y + var11), (double)((float)z + var12), new ItemStack(items.getItem(), var13, items.getItemDamage()));
            entityItem.motionX = random.nextGaussian() * (double)0.05F;
            entityItem.motionY = random.nextGaussian() * (double)0.15F;
            entityItem.motionZ = random.nextGaussian() * (double)0.05F;
            if (items.hasTagCompound()) {
               entityItem.getEntityItem().setTagCompound((NBTTagCompound)items.getTagCompound().copy());
            }
         }
      }

   }

   public void dropItem(int slot) {
      dropItem(this.items.get(slot), this.tileEntity.getWorldObj(), this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord);
   }

   public void dropItem(ItemStack stack) {
      dropItem(stack, this.tileEntity.getWorldObj(), this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord);
   }

   public void dropAllItems() {
      for(ItemStack stack : this.items) {
         this.dropItem(stack);
      }

      this.items.clear();
   }

   public List<ItemStack> getGraveContent() {
      return this.items;
   }

   public void setRandomGraveContent(IInventory inventory, Random random, boolean isPetGrave, boolean allLoot) {
      this.addInventoryContent(new ItemStack(Items.bone, 1 + random.nextInt(5), 0));
      this.addInventoryContent(new ItemStack(Items.rotten_flesh, 1 + random.nextInt(5), 0));
      if (isPetGrave) {
         this.fillPetGrave(random);
      } else if (this.tileEntity.getGraveType().ordinal() != 3 && this.tileEntity.getGraveType().ordinal() != 4) {
         if (random.nextInt(50) == 0) {
            if (random.nextInt(2) == 0) {
               this.addInventoryContent(new ItemStack(Items.skull, 1, 0));
            } else {
               this.addInventoryContent(new ItemStack(Items.skull, 1, 2));
            }
         }

         int graveType = random.nextInt(80);
         if (allLoot) {
            if (this.tileEntity.isSwordGrave() && graveType > 5) {
               this.fillWarriorGrave(random, true);
            } else if (graveType < 4) {
               this.fillAdventureGrave(random);
            } else if (graveType < 7) {
               this.fillWorkerGrave(random);
            } else if (graveType < 10) {
               this.fillWizardGrave(random);
            } else if (graveType < 12) {
               this.fillMinerGrave(random);
            } else if (graveType == 13) {
               this.fillWarriorGrave(random, false);
            }
         } else if (graveType < 3) {
            this.fillWorkerGrave(random);
         }
      }

   }

   private void fillWarriorGrave(Random random, boolean isSwordGrave) {
      if (isSwordGrave) {
         if (random.nextInt(2) == 0) {
            this.addInventoryContent(new ItemStack(Items.leather_chestplate, 1, getRandomDamage(random, 30)));
         }

         if (random.nextInt(2) == 0) {
            this.addInventoryContent(new ItemStack(Items.leather_leggings, 1, getRandomDamage(random, 30)));
         }

         if (random.nextInt(2) == 0) {
            this.addInventoryContent(new ItemStack(Items.leather_helmet, 1, getRandomDamage(random, 30)));
         }

         if (random.nextInt(2) == 0) {
            this.addInventoryContent(new ItemStack(Items.leather_boots, 1, getRandomDamage(random, 30)));
         }

      } else {
         int armorType = random.nextInt(10);
         if (armorType > 5) {
            if (random.nextInt(2) == 0) {
               this.addInventoryContent(new ItemStack(Items.iron_chestplate, 1, getRandomDamage(random)));
            }

            if (random.nextInt(2) == 0) {
               this.addInventoryContent(new ItemStack(Items.iron_leggings, 1, getRandomDamage(random)));
            }

            if (random.nextInt(2) == 0) {
               this.addInventoryContent(new ItemStack(Items.iron_helmet, 1, getRandomDamage(random)));
            }

            if (random.nextInt(2) == 0) {
               this.addInventoryContent(new ItemStack(Items.iron_boots, 1, getRandomDamage(random)));
            }

            this.changeGraveTypeToSword(Items.iron_sword, getRandomDamage(random));
         } else if (armorType > 2) {
            if (random.nextInt(2) == 0) {
               this.addInventoryContent(new ItemStack(Items.chainmail_chestplate, 1, getRandomDamage(random)));
            }

            if (random.nextInt(2) == 0) {
               this.addInventoryContent(new ItemStack(Items.chainmail_leggings, 1, getRandomDamage(random)));
            }

            if (random.nextInt(2) == 0) {
               this.addInventoryContent(new ItemStack(Items.chainmail_helmet, 1, getRandomDamage(random)));
            }

            if (random.nextInt(2) == 0) {
               this.addInventoryContent(new ItemStack(Items.chainmail_boots, 1, getRandomDamage(random)));
            }

            this.changeGraveTypeToSword(Items.iron_sword, getRandomDamage(random));
         } else if (armorType > 0) {
            if (random.nextInt(2) == 0) {
               this.addInventoryContent(new ItemStack(Items.golden_chestplate, 1, getRandomDamage(random, 50)));
            }

            if (random.nextInt(2) == 0) {
               this.addInventoryContent(new ItemStack(Items.golden_leggings, 1, getRandomDamage(random, 50)));
            }

            if (random.nextInt(2) == 0) {
               this.addInventoryContent(new ItemStack(Items.golden_helmet, 1, getRandomDamage(random, 30)));
            }

            if (random.nextInt(2) == 0) {
               this.addInventoryContent(new ItemStack(Items.golden_boots, 1, getRandomDamage(random, 40)));
            }

            this.changeGraveTypeToSword(Items.golden_sword, getRandomDamage(random, 15));
         } else {
            if (random.nextInt(2) == 0) {
               this.addInventoryContent(new ItemStack(Items.diamond_chestplate, 1, getRandomDamage(random)));
            }

            if (random.nextInt(2) == 0) {
               this.addInventoryContent(new ItemStack(Items.diamond_leggings, 1, getRandomDamage(random)));
            }

            if (random.nextInt(2) == 0) {
               this.addInventoryContent(new ItemStack(Items.diamond_helmet, 1, getRandomDamage(random)));
            }

            if (random.nextInt(2) == 0) {
               this.addInventoryContent(new ItemStack(Items.diamond_boots, 1, getRandomDamage(random)));
            }

            this.changeGraveTypeToSword(Items.diamond_sword, getRandomDamage(random));
         }

         if (random.nextInt(3) == 0) {
            this.addInventoryContent(new ItemStack(Items.bow, 1, getRandomDamage(random)));
            this.addInventoryContent(new ItemStack(Items.arrow, 10 + random.nextInt(54), 0));
         }

      }
   }

   private void changeGraveTypeToSword(Item sword, int swordDamage) {
      this.tileEntity.setGraveType((byte)EnumGraves.SWORD.ordinal());
      this.tileEntity.setSword(new ItemStack(sword, 1, swordDamage));
   }

   private void fillMinerGrave(Random random) {
      if (random.nextInt(2) == 0) {
         int pickAxeType = random.nextInt(10);
         if (pickAxeType > 3) {
            this.addInventoryContent(new ItemStack(Items.iron_pickaxe, 1, getRandomDamage(random)));
            this.tileEntity.setGraveType(GraveStoneHelper.getRandomGrave(Arrays.asList(GraveStoneHelper.GENERATED_IRON_GRAVES), random));
         } else if (pickAxeType > 0) {
            this.addInventoryContent(new ItemStack(Items.golden_pickaxe, 1, getRandomDamage(random, 15)));
            this.tileEntity.setGraveType(GraveStoneHelper.getRandomGrave(Arrays.asList(GraveStoneHelper.GENERATED_GOLDEN_GRAVES), random));
         } else {
            this.addInventoryContent(new ItemStack(Items.diamond_pickaxe, 1, getRandomDamage(random)));
            this.tileEntity.setGraveType(GraveStoneHelper.getRandomGrave(Arrays.asList(GraveStoneHelper.GENERATED_DIAMOND_GRAVES), random));
         }
      }

      switch(random.nextInt(10)) {
      case 0:
         this.addInventoryContent(new ItemStack(Items.diamond, 1 + random.nextInt(3), 0));
         break;
      case 1:
         this.addInventoryContent(new ItemStack(Items.emerald, 1 + random.nextInt(3), 0));
      }

      switch(random.nextInt(5)) {
      case 0:
         this.addInventoryContent(new ItemStack(Items.gold_ingot, 3 + random.nextInt(5), 0));
         break;
      case 1:
      case 2:
         this.addInventoryContent(new ItemStack(Items.gold_ingot, 3 + random.nextInt(5), 0));
      }

      switch(random.nextInt(5)) {
      case 0:
         this.addInventoryContent(new ItemStack(Items.redstone, 3 + random.nextInt(8), 0));
         break;
      case 1:
         this.addInventoryContent(new ItemStack(Items.dye, 3 + random.nextInt(8), 4));
      }

   }

   private void fillWizardGrave(Random random) {
      switch(random.nextInt(10)) {
      case 0:
         EnchantmentData data = new EnchantmentData(Enchantment.enchantmentsBookList[random.nextInt(Enchantment.enchantmentsBookList.length)], 1 + random.nextInt(5));
         ItemStack items = Items.enchanted_book.getEnchantedItemStack(data);
         this.addInventoryContent(items);
         this.tileEntity.setGraveType(GraveStoneHelper.getRandomGrave(Arrays.asList(GraveStoneHelper.GENERATED_REDSTONE_GRAVES), random));
         break;
      case 1:
         this.addInventoryContent(new ItemStack(Items.potionitem, 1 + random.nextInt(5), POTION_LIST[random.nextInt(POTION_LIST.length)]));
         this.tileEntity.setGraveType(GraveStoneHelper.getRandomGrave(Arrays.asList(GraveStoneHelper.GENERATED_QUARTZ_GRAVES), random));
         break;
      case 2:
      case 3:
         this.addInventoryContent(new ItemStack(Items.book, 3 + random.nextInt(8), 0));
         this.tileEntity.setGraveType(GraveStoneHelper.getRandomGrave(Arrays.asList(GraveStoneHelper.GENERATED_LAPIS_GRAVES), random));
      }

      switch(random.nextInt(15)) {
      case 0:
         this.addInventoryContent(new ItemStack(Items.ender_pearl, 1, 0));
         break;
      case 1:
         this.addInventoryContent(new ItemStack(Items.blaze_powder, 1, 0));
         break;
      case 2:
         this.addInventoryContent(new ItemStack(Items.glowstone_dust, 3 + random.nextInt(8), 0));
      }

      switch(random.nextInt(6)) {
      case 0:
         this.addInventoryContent(new ItemStack(Items.magma_cream, 1, 0));
         break;
      case 1:
         this.addInventoryContent(new ItemStack(Items.gunpowder, 1, 0));
      }

      switch(random.nextInt(10)) {
      case 0:
         this.addInventoryContent(new ItemStack(Items.ghast_tear, 1, 0));
         break;
      case 1:
         this.addInventoryContent(new ItemStack(Items.nether_wart, 1, 0));
      }

      switch(random.nextInt(5)) {
      case 0:
         this.addInventoryContent(new ItemStack(Items.spider_eye, 1, 0));
         break;
      case 1:
         this.addInventoryContent(new ItemStack(Items.fermented_spider_eye, 1, 0));
      }

      switch(random.nextInt(8)) {
      case 0:
         this.addInventoryContent(new ItemStack(Items.golden_carrot, 1, 0));
         break;
      case 1:
         this.addInventoryContent(new ItemStack(Items.speckled_melon, 1, 0));
      }

   }

   private void fillWorkerGrave(Random random) {
      int toolType = random.nextInt(10);
      if (toolType > 3) {
         if (random.nextInt(2) == 0) {
            this.addInventoryContent(new ItemStack(Items.iron_axe, 1, getRandomDamage(random)));
         } else {
            this.addInventoryContent(new ItemStack(Items.iron_shovel, 1, getRandomDamage(random)));
         }

         this.tileEntity.setGraveType(GraveStoneHelper.getRandomGrave(Arrays.asList(GraveStoneHelper.GENERATED_IRON_GRAVES), random));
      } else if (toolType > 0) {
         if (random.nextInt(2) == 0) {
            this.addInventoryContent(new ItemStack(Items.golden_axe, 1, getRandomDamage(random, 15)));
         } else {
            this.addInventoryContent(new ItemStack(Items.golden_shovel, 1, getRandomDamage(random, 15)));
         }

         this.tileEntity.setGraveType(GraveStoneHelper.getRandomGrave(Arrays.asList(GraveStoneHelper.GENERATED_GOLDEN_GRAVES), random));
      } else {
         if (random.nextInt(2) == 0) {
            this.addInventoryContent(new ItemStack(Items.diamond_axe, 1, getRandomDamage(random)));
         } else {
            this.addInventoryContent(new ItemStack(Items.diamond_shovel, 1, getRandomDamage(random)));
         }

         this.tileEntity.setGraveType(GraveStoneHelper.getRandomGrave(Arrays.asList(GraveStoneHelper.GENERATED_DIAMOND_GRAVES), random));
      }

      switch(random.nextInt(6)) {
      case 0:
         this.addInventoryContent(new ItemStack(Items.clay_ball, 1 + random.nextInt(8), 0));
         break;
      case 1:
         this.addInventoryContent(new ItemStack(Items.brick, 3 + random.nextInt(5), 0));
      }

      switch(random.nextInt(6)) {
      case 0:
         this.addInventoryContent(new ItemStack(Items.leather, 1 + random.nextInt(5), 0));
         break;
      case 1:
         this.addInventoryContent(new ItemStack(Items.bucket, 1, 0));
      }

      if (random.nextInt(8) == 0) {
         this.addInventoryContent(new ItemStack(Items.saddle, 1, 0));
      }

   }

   private void fillAdventureGrave(Random random) {
      switch(random.nextInt(8)) {
      case 0:
         this.addInventoryContent(new ItemStack(Items.compass, 1, 0));
         break;
      case 1:
         this.addInventoryContent(new ItemStack(Items.clock, 1, 0));
         break;
      case 2:
         this.addInventoryContent(new ItemStack(Items.map, 1, 0));
      }

      switch(random.nextInt(10)) {
      case 0:
         this.addInventoryContent(new ItemStack(Items.painting, 1 + random.nextInt(5), 0));
         break;
      case 1:
         this.addInventoryContent(this.getRandomRecord(random));
         break;
      case 2:
         this.addInventoryContent(new ItemStack(Items.writable_book, 1, 0));
      }

      if (random.nextInt(4) == 0) {
         this.addInventoryContent(new ItemStack(Items.stick, 3 + random.nextInt(9), 0));
      }

      if (random.nextInt(5) == 0) {
         this.addInventoryContent(new ItemStack(Items.cookie, 3 + random.nextInt(5), 0));
      }

      if (random.nextInt(15) == 0) {
         this.addInventoryContent(this.getRandomEgg(random));
         this.tileEntity.setGraveType(GraveStoneHelper.getRandomGrave(Arrays.asList(GraveStoneHelper.GENERATED_EMERALD_GRAVES), random));
      }

   }

   private void fillPetGrave(Random random) {
      if (random.nextInt(10) == 0) {
         this.addInventoryContent(new ItemStack(Items.lead, 1, 0));
         if (Arrays.asList(GraveStoneHelper.DOGS_GRAVES).contains(this.tileEntity.getGraveType())) {
            this.tileEntity.setGraveType(GraveStoneHelper.getRandomGrave(Arrays.asList(GraveStoneHelper.DOG_GOLDEN_GRAVES), random));
         } else if (Arrays.asList(GraveStoneHelper.CATS_GRAVES).contains(this.tileEntity.getGraveType())) {
            this.tileEntity.setGraveType(GraveStoneHelper.getRandomGrave(Arrays.asList(GraveStoneHelper.CAT_GOLDEN_GRAVES), random));
         }
      }

      if (random.nextInt(10) == 0) {
         this.addInventoryContent(new ItemStack(Items.name_tag, 1, 0));
         if (Arrays.asList(GraveStoneHelper.DOGS_GRAVES).contains(this.tileEntity.getGraveType())) {
            this.tileEntity.setGraveType(GraveStoneHelper.getRandomGrave(Arrays.asList(GraveStoneHelper.DOG_DIAMOND_GRAVES), random));
         } else if (Arrays.asList(GraveStoneHelper.CATS_GRAVES).contains(this.tileEntity.getGraveType())) {
            this.tileEntity.setGraveType(GraveStoneHelper.getRandomGrave(Arrays.asList(GraveStoneHelper.CAT_DIAMOND_GRAVES), random));
         }
      }

   }

   public static int getRandomDamage(Random random) {
      return 20 + random.nextInt(100);
   }

   public static int getRandomDamage(Random random, int maxDamage) {
      return random.nextInt(maxDamage);
   }

   private ItemStack getRandomRecord(Random random) {
      switch(random.nextInt(13)) {
      case 0:
      default:
         return new ItemStack(Items.record_cat, 1, 0);
      case 1:
         return new ItemStack(Items.record_cat, 1, 0);
      case 2:
         return new ItemStack(Items.record_blocks, 1, 0);
      case 3:
         return new ItemStack(Items.record_chirp, 1, 0);
      case 4:
         return new ItemStack(Items.record_far, 1, 0);
      case 5:
         return new ItemStack(Items.record_mall, 1, 0);
      case 6:
         return new ItemStack(Items.record_mellohi, 1, 0);
      case 7:
         return new ItemStack(Items.record_stal, 1, 0);
      case 8:
         return new ItemStack(Items.record_strad, 1, 0);
      case 9:
         return new ItemStack(Items.record_ward, 1, 0);
      case 10:
         return new ItemStack(Items.record_11, 1, 0);
      case 11:
         return new ItemStack(Items.record_wait, 1, 0);
      case 12:
         return new ItemStack(Items.record_13, 1, 0);
      }
   }

   private ItemStack getRandomEgg(Random random) {
      switch(random.nextInt(11)) {
      case 0:
      default:
         return new ItemStack(Items.spawn_egg, 1, 120);
      case 1:
         return new ItemStack(Items.spawn_egg, 1, 90);
      case 2:
         return new ItemStack(Items.spawn_egg, 1, 91);
      case 3:
         return new ItemStack(Items.spawn_egg, 1, 92);
      case 4:
         return new ItemStack(Items.spawn_egg, 1, 93);
      case 5:
         return new ItemStack(Items.spawn_egg, 1, 94);
      case 6:
         return new ItemStack(Items.spawn_egg, 1, 95);
      case 7:
         return new ItemStack(Items.spawn_egg, 1, 96);
      case 8:
         return new ItemStack(Items.spawn_egg, 1, 98);
      case 9:
         return new ItemStack(Items.spawn_egg, 1, 100);
      case 10:
         return new ItemStack(Items.spawn_egg, 1, 120);
      }
   }
}
