package gravestone.renderer.tileentity;

import gravestone.block.enums.EnumSpawner;
import gravestone.core.Resources;
import gravestone.models.block.ModelSpawnerPentagram;
import gravestone.tileentity.TileEntityGSSpawner;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public class TileEntityGSSpawnerRenderer extends TileEntityGSRenderer {
   private static ModelSpawnerPentagram witherSpawnerModel = new ModelSpawnerPentagram(Resources.WITHER_SKULL_CANDLE);
   private static ModelSpawnerPentagram skeletonSpawnerModel = new ModelSpawnerPentagram(Resources.SKELETON_SKULL_CANDLE);
   private static ModelSpawnerPentagram zombieSpawnerModel = new ModelSpawnerPentagram(Resources.ZOMBIE_SKULL_CANDLE);
   public static TileEntityGSSpawnerRenderer instance;

   public TileEntityGSSpawnerRenderer() {
      instance = this;
   }

   public void renderSpawnerPentagramAt(TileEntityGSSpawner tileEntity, float x, float y, float z, float par8) {
      this.bindTexture(Resources.PENTAGRAM);
      GL11.glPushMatrix();
      if (tileEntity.getWorldObj() != null) {
         GL11.glTranslatef(x + 0.5F, y + 1.5F, z + 0.5F);
         GL11.glScalef(1.0F, -1.0F, -1.0F);
      } else {
         GL11.glTranslatef(x + 0.5F, y + 1.0F, z + 0.5F);
         GL11.glScalef(0.6F, -0.6F, -0.6F);
      }

      GL11.glTranslated(0.0D, -0.01D, 0.0D);
      EnumSpawner spawnerType = EnumSpawner.getById((byte)tileEntity.getBlockMetadata());
      ModelSpawnerPentagram spawner = getSpawnerModel(spawnerType);
      spawner.renderAll();
      GL11.glPopMatrix();
   }

   public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float par8) {
      this.renderSpawnerPentagramAt((TileEntityGSSpawner)tileEntity, (float)x, (float)y, (float)z, par8);
   }

   private static ModelSpawnerPentagram getSpawnerModel(EnumSpawner spawnerType) {
      switch(spawnerType) {
      case WITHER_SPAWNER:
         return witherSpawnerModel;
      case SKELETON_SPAWNER:
         return skeletonSpawnerModel;
      case ZOMBIE_SPAWNER:
      default:
         return zombieSpawnerModel;
      }
   }
}
