package com.voidsrift.riftflux.vortex.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import com.voidsrift.riftflux.vortex.potion.ModPotions;

public class ItemHighlandSpirit extends ItemArmor {
   public IIcon icon;

   public ItemHighlandSpirit(ArmorMaterial material, int type) {
      super(material, 0, type);
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister p_94581_1_) {
      this.icon = p_94581_1_.registerIcon("riftflux:highlandspirit");
   }

   @SideOnly(Side.CLIENT)
   public IIcon getIconFromDamage(int par1) {
      return this.icon;
   }

   public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
      return "riftflux:textures/armor/kilt.png";
   }

   public boolean hasEffect(ItemStack par1ItemStack, int pass) {
      return true;
   }

   public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
      if (!world.isRemote) {
         player.removePotionEffect(ModPotions.headBuff.id);
         player.addPotionEffect(new PotionEffect(ModPotions.headBuff.id, 5, this.headCount(player) - 9, false));
      }

   }

   public EnumRarity getRarity(ItemStack p_77613_1_) {
      return EnumRarity.epic;
   }

   public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
      list.add(EnumChatFormatting.DARK_AQUA + "Slain players can drop heads");
      list.add(EnumChatFormatting.DARK_AQUA + "Get faster for each unique hotbar head");
   }

   private int headCount(EntityPlayer player) {
      Set<String> uniqueHeadNames = new HashSet<String>();

      for(int i = 0; i < 9; ++i) {
         ItemStack hotbarItem = player.inventory.getStackInSlot(i);
         if (hotbarItem != null && hotbarItem.getItem() == Items.skull && hotbarItem.getItemDamage() == 3) {
            NBTTagCompound tag = hotbarItem.getTagCompound();
            if (tag != null && tag.hasKey("SkullOwner", 8)) {
               String skullOwner = tag.getString("SkullOwner");
               if (skullOwner != null && !skullOwner.isEmpty()) {
                  uniqueHeadNames.add(skullOwner);
               }
            }
         }
      }

      return uniqueHeadNames.size();
   }
}
