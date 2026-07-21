package gravestone.structures;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public class ComponentGraveStone extends StructureComponent {
   protected ComponentGraveStone(int direction) {
      super(direction);
      this.coordBaseMode = direction;
   }

   public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
      return true;
   }

   public boolean addComponentParts(World world, Random random) {
      return true;
   }

   public void placeBlockAtCurrentPosition(World world, Block block, int blockMeta, int x, int y, int z, StructureBoundingBox boundingBox) {
      super.placeBlockAtCurrentPosition(world, block, blockMeta, x, y, z, boundingBox);
   }

   public int getXWithOffset(int x, int z) {
      return super.getXWithOffset(x, z);
   }

   public int getYWithOffset(int y) {
      return super.getYWithOffset(y);
   }

   public int getZWithOffset(int x, int z) {
      return super.getZWithOffset(x, z);
   }

   public boolean generateStructureChestContents(World world, StructureBoundingBox boundingBox, Random random, int x, int y, int z, WeightedRandomChestContent[] chestContent, int par8) {
      return super.generateStructureChestContents(world, boundingBox, random, x, y, z, chestContent, par8);
   }

   protected void func_143012_a(NBTTagCompound nbttagcompound) {
   }

   protected void func_143011_b(NBTTagCompound nbttagcompound) {
   }
}
