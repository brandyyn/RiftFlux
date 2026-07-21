package gravestone.item;

import gravestone.ModGraveStone;
import gravestone.core.GSBlock;
import gravestone.core.GSTabs;
import gravestone.core.Resources;
import gravestone.config.GraveStoneConfig;
import gravestone.core.recipe.GSChiselRegistry;
import gravestone.tileentity.TileEntityGSGrave;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemGSChisel extends Item {
   public ItemGSChisel() {
      this.setMaxStackSize(1);
      if (GSChiselRegistry.isBuiltInChiselEnabled()) {
         this.setCreativeTab(GSTabs.otherItemsTab);
      }
      this.setUnlocalizedName("gravestone chisel");
      this.setMaxDamage(GraveStoneConfig.chiselDurability);
      this.setTextureName(Resources.CHISEL);
   }

   public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
      player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
      return itemStack;
   }

   @Override
   public boolean hasContainerItem(ItemStack stack) {
      return true;
   }

   @Override
   public ItemStack getContainerItem(ItemStack stack) {
      return GSChiselRegistry.getDamagedCraftingChisel(stack);
   }

   @Override
   public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) {
      return false;
   }

   public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10) {
      if (world.isRemote) {
         if (world.getBlock(x, y, z).equals(GSBlock.graveStone)) {
            return this.setGraveText(stack, player, world, x, y, z, false);
         }

         if (world.getBlock(x, y, z).equals(GSBlock.memorial)) {
            return this.setGraveText(stack, player, world, x, y, z, true);
         }
      }

      return false;
   }

   private boolean setGraveText(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, boolean isMemorial) {
      TileEntityGSGrave tileEntity = (TileEntityGSGrave)world.getTileEntity(x, y, z);
      if (tileEntity != null && tileEntity.isEditable() && tileEntity.getDeathTextComponent().getDeathText().length() == 0) {
         ModGraveStone.proxy.openGraveGui(tileEntity);
         if (isMemorial) {
            stack.damageItem(5, player);
         } else {
            stack.damageItem(2, player);
         }

         return true;
      } else {
         return false;
      }
   }

   public float func_150893_a(ItemStack itemStack, Block block) {
      return 1.0F;
   }
}
