package makamys.satchels.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.expanded.IBaubleExpanded;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import makamys.satchels.EntityPropertiesSatchels;
import makamys.satchels.Satchels;
import makamys.satchels.compat.BaublesCompat;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.IIcon;

public class ItemSatchel extends ItemEquippable implements IBaubleExpanded, IBauble {
    
    public static IIcon emptyIcon;
    
    public ItemSatchel() {
        setMaxStackSize(1);
        setUnlocalizedName(Satchels.MODID + "." + "satchel");
        setCreativeTab(CreativeTabs.tabTools);
        setTextureName("satchel");
    }
    
   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister iconRegister) {
       super.itemIcon = iconRegister.registerIcon("satchels:satchel");
       emptyIcon = iconRegister.registerIcon("satchels:empty_equipment_slot_satchel");
   }

   @Override
   public String[] getBaubleTypes(net.minecraft.item.ItemStack stack) {
       return new String[]{BaublesCompat.TYPE_SATCHEL};
   }

   @Override
   public BaubleType getBaubleType(net.minecraft.item.ItemStack stack) {
       return BaubleType.UNIVERSAL;
   }

   @Override
   public void onWornTick(net.minecraft.item.ItemStack stack, EntityLivingBase player) {
   }

   @Override
   public void onEquipped(net.minecraft.item.ItemStack stack, EntityLivingBase player) {
       if(player instanceof net.minecraft.entity.player.EntityPlayer) {
           EntityPropertiesSatchels.fromPlayer((net.minecraft.entity.player.EntityPlayer)player).updateInventories(stack);
       }
   }

   @Override
   public void onUnequipped(net.minecraft.item.ItemStack stack, EntityLivingBase player) {
       if(player instanceof net.minecraft.entity.player.EntityPlayer) {
           EntityPropertiesSatchels.fromPlayer((net.minecraft.entity.player.EntityPlayer)player).updateInventories(stack);
       }
   }

   @Override
   public boolean canEquip(net.minecraft.item.ItemStack stack, EntityLivingBase player) {
       return true;
   }

   @Override
   public boolean canUnequip(net.minecraft.item.ItemStack stack, EntityLivingBase player) {
       return true;
   }

   @Override
   public void onPlayerLoad(net.minecraft.item.ItemStack stack, EntityLivingBase player) {
   }
    
}
