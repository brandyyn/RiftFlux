package gravestone.renderer.item;

import gravestone.tileentity.TileEntityGSPileOfBones;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;

public class ItemGSPileOfBonesRenderer implements IItemRenderer {
   private final TileEntityGSPileOfBones tileEntity = new TileEntityGSPileOfBones();

   public boolean handleRenderType(ItemStack item, ItemRenderType type) {
      return true;
   }

   public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
      return true;
   }

   public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
      TileEntityGSPileOfBones te = this.tileEntity;
      te.blockMetadata = item.getItemDamage();
      TileEntityRendererDispatcher.instance.renderTileEntityAt(te, 0.0D, 0.0D, 0.0D, 0.0F);
   }
}
