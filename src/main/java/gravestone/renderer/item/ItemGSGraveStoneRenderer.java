package gravestone.renderer.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.block.GraveStoneHelper;
import gravestone.tileentity.TileEntityGSGraveStone;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;

@SideOnly(Side.CLIENT)
public class ItemGSGraveStoneRenderer implements IItemRenderer {
   private final TileEntityGSGraveStone tileEntity = new TileEntityGSGraveStone();

   public boolean handleRenderType(ItemStack item, ItemRenderType type) {
      return true;
   }

   public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
      return true;
   }

   public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
      ItemStack sword = getSword(item);
      TileEntityGSGraveStone te = this.tileEntity;
      te.setGraveType((byte)0);
      te.setSword(null);
      te.setEnchanted(false);
      if (item.stackTagCompound != null) {
         te.setGraveType(item.stackTagCompound.getByte("GraveType"));
         if (sword != null) {
            te.setSword(sword);
         }

         if (item.stackTagCompound.hasKey("Enchanted")) {
            te.setEnchanted(item.stackTagCompound.getBoolean("Enchanted"));
         }
      }

      TileEntityRendererDispatcher.instance.renderTileEntityAt(te, 0.0D, 0.0D, 0.0D, 0.0F);
   }

   private static ItemStack getSword(ItemStack grave) {
      return grave.stackTagCompound != null && grave.stackTagCompound.hasKey("Sword")
              ? GraveStoneHelper.loadSwordInfo(grave.getTagCompound())
              : null;
   }

}
