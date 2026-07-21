package gravestone.tileentity;

import net.minecraft.tileentity.TileEntity;

public class TileEntityGSSpawner extends TileEntity {
   protected GSMobSpawner spawner = new GSMobSpawner(this);

   public void updateEntity() {
      this.spawner.updateEntity();
   }
}
