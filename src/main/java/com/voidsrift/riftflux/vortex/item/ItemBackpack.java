package com.voidsrift.riftflux.vortex.item;

import baubles.api.BaublesApi;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.expanded.IBaubleExpanded;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import java.util.Random;
import com.voidsrift.riftflux.vortex.client.model.item.ModelBackpack;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.vortex.entity.EntityItemBackpack;
import com.voidsrift.riftflux.vortex.item.ModItems;
import makamys.satchels.compat.BaublesCompat;

public class ItemBackpack extends ItemArmor implements IBaubleExpanded, IBauble {
   private static final Random GUI_ID_RANDOM = new Random();

   @SideOnly(Side.CLIENT)
   private ModelBiped model;
   @SideOnly(Side.CLIENT)
   public IIcon icon;
   @SideOnly(Side.CLIENT)
   public IIcon overlay;

   public ItemBackpack(ArmorMaterial material, int type) {
      super(material, 0, type);
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister p_94581_1_) {
      this.icon = p_94581_1_.registerIcon("riftflux:backpack");
      this.overlay = p_94581_1_.registerIcon("riftflux:backpack_overlay");
   }

   @SideOnly(Side.CLIENT)
   public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
      return pass == 0 ? this.icon : this.overlay;
   }

   @SideOnly(Side.CLIENT)
   public boolean requiresMultipleRenderPasses() {
      return true;
   }

   @SideOnly(Side.CLIENT)
   public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
      if (this.model == null) {
         this.model = new ModelBackpack();
      }

      this.model.isSneak = entityLiving.isSneaking();
      this.model.isRiding = entityLiving.isRiding();
      this.model.isChild = entityLiving.isChild();
      this.model.aimedBow = false;
      this.model.heldItemRight = entityLiving.getHeldItem() != null ? 1 : 0;
      int n = this.model.heldItemRight;
      if (entityLiving instanceof EntityPlayer && ((EntityPlayer)entityLiving).getItemInUseDuration() > 0) {
         EnumAction enumaction = ((EntityPlayer)entityLiving).getItemInUse().getItemUseAction();
         if (enumaction == EnumAction.block) {
            this.model.heldItemRight = 3;
         } else if (enumaction == EnumAction.bow) {
            this.model.aimedBow = true;
         }
      }

      return this.model;
   }

   public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
      return type == null ? "riftflux:textures/armor/backpack.png" : "riftflux:textures/armor/backpack_overlay.png";
   }

   public boolean hasCustomEntity(ItemStack stack) {
      return true;
   }

   public Entity createEntity(World world, Entity location, ItemStack itemStack) {
      EntityItemBackpack entityItemBackpack = new EntityItemBackpack(world, location.posX, location.posY, location.posZ, itemStack);
      EntityItem entityItem = (EntityItem)location;
      entityItemBackpack.motionX = entityItem.motionX;
      entityItemBackpack.motionY = entityItem.motionY;
      entityItemBackpack.motionZ = entityItem.motionZ;
      entityItemBackpack.delayBeforeCanPickup = entityItem.delayBeforeCanPickup;
      return entityItemBackpack;
   }

   public void onCreated(ItemStack stack, World world, EntityPlayer player) {
      if (!world.isRemote) {
         ensureBackpackId(stack, player);
      }
   }

   public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held) {
      if (!world.isRemote) {
         EntityPlayer player = entity instanceof EntityPlayer ? (EntityPlayer) entity : null;
         ensureBackpackId(stack, player);
      }
   }

   @Override
   public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
      if (!world.isRemote && stack != null) {
         if (BaublesCompat.equipToFirstEmpty(player, stack, getBaubleTypes(stack))) {
            return stack;
         }
      }
      return stack;
   }

   public int getColor(ItemStack p_82814_1_) {
      NBTTagCompound nbttagcompound = p_82814_1_.getTagCompound();
      if (nbttagcompound == null) {
         return 10511680;
      } else {
         NBTTagCompound display = nbttagcompound.getCompoundTag("display");
         return display == null ? 10511680 : (display.hasKey("color", 3) ? display.getInteger("color") : 10511680);
      }
   }

   public void func_82813_b(ItemStack itemStack, int color) {
      NBTTagCompound nbttagcompound = itemStack.getTagCompound();
      if (nbttagcompound == null) {
         nbttagcompound = new NBTTagCompound();
         itemStack.setTagCompound(nbttagcompound);
      }

      NBTTagCompound display = nbttagcompound.getCompoundTag("display");
      if (!nbttagcompound.hasKey("display", 10)) {
         nbttagcompound.setTag("display", display);
      }

      display.setInteger("color", color);
   }

   public boolean isDamaged(ItemStack stack) {
      return ModConfig.backpackDurability ? super.isDamaged(stack) : false;
   }

   public int getMaxDamage(ItemStack stack) {
      if (!ModConfig.backpackDurability) {
         return 0;
      }
      return Math.max(1, ModConfig.backpackDurabilityAmount);
   }

   public void setDamage(ItemStack stack, int damage) {
      if (ModConfig.backpackDurability) {
         super.setDamage(stack, damage);
      }

   }

   @Override
   public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
      return false;
   }

   @Override
   public String[] getBaubleTypes(ItemStack stack) {
      return BaublesCompat.getTypes(BaublesCompat.ITEM_BACKPACK, BaublesCompat.TYPE_BACKPACK);
   }

   @Override
   public BaubleType getBaubleType(ItemStack stack) {
      return BaubleType.UNIVERSAL;
   }

   @Override
   public void onWornTick(ItemStack stack, EntityLivingBase player) {
      if (stack != null && player instanceof EntityPlayer && !player.worldObj.isRemote) {
         ensureBackpackId(stack, (EntityPlayer) player);
      }
   }

   @Override
   public void onEquipped(ItemStack stack, EntityLivingBase player) {
      if (stack != null && player instanceof EntityPlayer && !player.worldObj.isRemote) {
         ensureBackpackId(stack, (EntityPlayer) player);
      }
   }

   @Override
   public void onUnequipped(ItemStack stack, EntityLivingBase player) {
   }

   @Override
   public boolean canEquip(ItemStack stack, EntityLivingBase player) {
      return true;
   }

   @Override
   public boolean canUnequip(ItemStack stack, EntityLivingBase player) {
      if (!ModConfig.backpackStorage && stack != null) {
         com.voidsrift.riftflux.vortex.lib.container.InventoryBackpack inv = com.voidsrift.riftflux.vortex.lib.helper.ContainerHelper.getBackpackInventory(stack);
         if (inv != null && !inv.isEmpty()) {
            return false;
         }
      }
      return true;
   }

   @Override
   public void onPlayerLoad(ItemStack stack, EntityLivingBase player) {
   }

   public static ItemStack getEquippedBackpack(EntityPlayer player) {
      return BaublesCompat.getBaubleStack(
              player,
              ModItems.backpack,
              0,
              BaublesCompat.getTypes(BaublesCompat.ITEM_BACKPACK, BaublesCompat.TYPE_BACKPACK)
      );
   }

   private static void ensureBackpackId(ItemStack stack, EntityPlayer player) {
      if (stack == null) return;
      NBTTagCompound tag = stack.getTagCompound();
      if (tag == null) {
         tag = new NBTTagCompound();
         stack.setTagCompound(tag);
      }
      boolean changed = false;
      int id;
      if (!tag.hasKey("backpackGuiId", 3)) {
         id = GUI_ID_RANDOM.nextInt();
         tag.setInteger("backpackGuiId", id);
         changed = true;
      } else {
         id = tag.getInteger("backpackGuiId");
      }

      ItemStack equipped = player != null ? getEquippedBackpack(player) : null;
      boolean isEquipped = equipped == stack;

      // If the equipped backpack shares an ID with another backpack, reassign to keep IDs unique.
      if (isEquipped) {
         if (hasDuplicateBackpackId(player, stack, id)) {
            id = GUI_ID_RANDOM.nextInt();
            tag.setInteger("backpackGuiId", id);
            changed = true;
         }
      }

      if (changed && player != null && player.inventory != null) {
         player.inventory.markDirty();
         if (player instanceof net.minecraft.entity.player.EntityPlayerMP) {
            net.minecraft.entity.player.EntityPlayerMP mp = (net.minecraft.entity.player.EntityPlayerMP) player;
            mp.inventoryContainer.detectAndSendChanges();
            try {
               // Only sync via PacketBackpackSync when this stack is actually equipped.
               // Otherwise, rely on normal inventory syncing to avoid overwriting the equipped backpack's ID.
               if (isEquipped) {
                  com.voidsrift.riftflux.vortex.network.ModPackets.instance
                          .sendTo(new com.voidsrift.riftflux.vortex.network.PacketBackpackSync(player, id), mp);
               }
            } catch (Throwable ignored) {
            }
         }
      }
   }

   private static boolean hasDuplicateBackpackId(EntityPlayer player, ItemStack self, int id) {
      if (player == null || player.inventory == null) return false;
      ItemStack[] main = player.inventory.mainInventory;
      if (main != null) {
         for (ItemStack stack : main) {
            if (stack == null || stack == self) continue;
            if (stack.getItem() != ModItems.backpack) continue;
            NBTTagCompound tag = stack.getTagCompound();
            if (tag != null && tag.hasKey("backpackGuiId", 3) && tag.getInteger("backpackGuiId") == id) {
               return true;
            }
         }
      }
      ItemStack[] armor = player.inventory.armorInventory;
      if (armor != null) {
         for (ItemStack stack : armor) {
            if (stack == null || stack == self) continue;
            if (stack.getItem() != ModItems.backpack) continue;
            NBTTagCompound tag = stack.getTagCompound();
            if (tag != null && tag.hasKey("backpackGuiId", 3) && tag.getInteger("backpackGuiId") == id) {
               return true;
            }
         }
      }
      IInventory baubles = BaublesApi.getBaubles(player);
      if (baubles != null) {
         for (int i = 0; i < baubles.getSizeInventory(); i++) {
            ItemStack stack = baubles.getStackInSlot(i);
            if (stack == null || stack == self) continue;
            if (stack.getItem() != ModItems.backpack) continue;
            NBTTagCompound tag = stack.getTagCompound();
            if (tag != null && tag.hasKey("backpackGuiId", 3) && tag.getInteger("backpackGuiId") == id) {
               return true;
            }
         }
      }
      return false;
   }
}
