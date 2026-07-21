package gravestone.renderer.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.core.Resources;
import gravestone.models.block.ModelPileOfBones;
import gravestone.tileentity.TileEntityGSPileOfBones;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileEntityGSPileOfBonesRenderer extends TileEntitySpecialRenderer {
   private ModelPileOfBones pileOfBonesModel = new ModelPileOfBones();
   private final int[] modelDisplayLists = new int[]{-1, -1};

   public void renderTileEntityCandleAt(TileEntityGSPileOfBones tileEntity, float x, float y, float z, float par8) {
      this.bindTexture(Resources.PILE_OF_BONES);
      GL11.glPushMatrix();
      int meta;
      if (tileEntity.getWorldObj() == null) {
         GL11.glTranslatef(x + 0.5F, y + 2.7F, z + 0.5F);
         GL11.glScalef(1.8F, -1.8F, -1.8F);
         GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
         meta = tileEntity.blockMetadata;
      } else {
         GL11.glTranslatef(x + 0.5F, y + 1.5F, z + 0.5F);
         GL11.glScalef(1.0F, -1.0F, -1.0F);
         meta = tileEntity.getBlockMetadata();
         int direction = tileEntity.getDirection();
         switch(direction) {
         case 0:
            direction = 180;
            break;
         case 1:
            direction = -90;
            break;
         case 2:
            direction = 0;
            break;
         case 3:
         default:
            direction = 90;
         }

         GL11.glRotatef((float)direction, 0.0F, 1.0F, 0.0F);
      }

      this.renderCachedModel(meta != 0);
      GL11.glPopMatrix();
   }

   private void renderCachedModel(boolean haveSkull) {
      int index = haveSkull ? 1 : 0;
      int displayList = this.modelDisplayLists[index];
      if (displayList == -1) {
         // Warm the model's per-part lists before compiling one aggregate draw call.
         this.pileOfBonesModel.renderAll(haveSkull);
         this.bindTexture(Resources.PILE_OF_BONES);
         displayList = GLAllocation.generateDisplayLists(1);
         GL11.glNewList(displayList, GL11.GL_COMPILE);
         this.pileOfBonesModel.renderAll(haveSkull);
         GL11.glEndList();
         this.modelDisplayLists[index] = displayList;
         return;
      }
      GL11.glCallList(displayList);
   }

   public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float par8) {
      this.renderTileEntityCandleAt((TileEntityGSPileOfBones)tileEntity, (float)x, (float)y, (float)z, par8);
   }
}
