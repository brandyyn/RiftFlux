package gravestone.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.tileentity.TileEntityGSAltar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderAltar extends TileEntitySpecialRenderer {
   public void renderTileEntityAt(TileEntityGSAltar te, double x, double y, double z, float f) {
      ItemStack corpse = te.getCorpse();
      if (corpse != null) {
         GL11.glPushMatrix();
         float time = (float)Minecraft.getMinecraft().theWorld.getTotalWorldTime() + f;
         GL11.glTranslatef((float)x + 0.5F, (float)y + 1.2F, (float)z + 0.5F);
         GL11.glRotatef(time % 360.0F, 0.0F, 1.0F, 0.0F);
         EntityItem entityItem = new EntityItem(te.getWorldObj(), 0.0D, 0.0D, 0.0D, corpse);
         if (corpse.hasTagCompound()) {
            entityItem.getEntityItem().setTagCompound((NBTTagCompound)corpse.getTagCompound().copy());
         }

         entityItem.hoverStart = 0.0F;
         RenderManager.instance.renderEntityWithPosYaw(entityItem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
         GL11.glPopMatrix();
      }

   }

   public void renderTileEntityAt(TileEntity te, double x, double y, double z, float xz) {
      this.renderTileEntityAt((TileEntityGSAltar)te, x, y, z, xz);
   }
}
