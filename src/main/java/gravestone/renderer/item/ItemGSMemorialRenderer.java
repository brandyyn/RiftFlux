package gravestone.renderer.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.block.enums.EnumHangedMobs;
import gravestone.tileentity.TileEntityGSMemorial;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;

@SideOnly(Side.CLIENT)
public class ItemGSMemorialRenderer implements IItemRenderer {
   public boolean handleRenderType(ItemStack item, ItemRenderType type) {
      return true;
   }

   public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
      return true;
   }

   public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
      TileEntityGSMemorial te = new TileEntityGSMemorial();
      if (item.stackTagCompound != null) {
         te.setGraveType(item.stackTagCompound.getByte("GraveType"));
         if (item.stackTagCompound.hasKey("Enchanted")) {
            te.setEnchanted(item.stackTagCompound.getBoolean("Enchanted"));
         }

         if (item.stackTagCompound.hasKey("HangedMob")) {
            te.setHangedMob(EnumHangedMobs.getByID(item.stackTagCompound.getByte("HangedMob")));
            te.setHangedVillagerProfession(item.stackTagCompound.getInteger("HangedVillagerProfession"));
         }
      }

      TileEntityRendererDispatcher.instance.renderTileEntityAt(te, 0.0D, 0.0D, 0.0D, 0.0F);
   }
}
