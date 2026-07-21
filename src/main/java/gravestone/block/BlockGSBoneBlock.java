package gravestone.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.block.enums.EnumBoneBlock;
import gravestone.config.GraveStoneConfig;
import gravestone.core.GSTabs;
import gravestone.core.Resources;
import gravestone.entity.monster.EntitySkullCrawler;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockGSBoneBlock extends Block {
   @SideOnly(Side.CLIENT)
   private IIcon skullIcon;

   public BlockGSBoneBlock() {
      super(Material.rock);
      this.setStepSound(Block.soundTypeStone);
      this.setBlockName("bone_block");
      this.setHardness(2.0F);
      this.setResistance(2.0F);
      this.setCreativeTab(GSTabs.otherItemsTab);
      this.setHarvestLevel("pickaxe", 0);
   }

   public void registerBlockIcons(IIconRegister iconRegister) {
      this.blockIcon = iconRegister.registerIcon(Resources.BONE_BLOCK);
      this.skullIcon = iconRegister.registerIcon(Resources.SKULL_BONE_BLOCK);
   }

   public IIcon getIcon(int side, int metadata) {
      return metadata != 1 && metadata != 3 ? this.blockIcon : this.skullIcon;
   }

   @SideOnly(Side.CLIENT)
   public void getSubBlocks(Item item, CreativeTabs tab, List list) {
      for(byte meta = 0; meta < EnumBoneBlock.values().length; ++meta) {
         list.add(new ItemStack(item, 1, meta));
      }

   }

   public int damageDropped(int metadata) {
      if (this.isSkullCrawlerBlock(metadata)) {
         metadata -= 2;
      }

      return metadata;
   }

   public boolean isSkullCrawlerBlock(int metadata) {
      return metadata == 2 || metadata == 3;
   }

   public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int metadata) {
      if (!world.isRemote && this.isSkullCrawlerBlock(metadata) && GraveStoneConfig.spawnSkullCrawlersAtBoneBlockDestruction) {
         EntitySkullCrawler skullCrawler = new EntitySkullCrawler(world);
         skullCrawler.setLocationAndAngles((double)x + 0.5D, (double)y, (double)z + 0.5D, 0.0F, 0.0F);
         world.spawnEntityInWorld(skullCrawler);
         skullCrawler.spawnExplosionParticle();
      }

      super.onBlockDestroyedByPlayer(world, x, y, z, metadata);
   }
}
