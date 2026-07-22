package gravestone.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.ModGraveStone;
import gravestone.block.enums.EnumTrap;
import gravestone.config.GraveStoneConfig;
import gravestone.core.GSPotion;
import gravestone.core.GSTabs;
import gravestone.core.TimeHelper;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockGSTrap extends Block {
   @SideOnly(Side.CLIENT)
   private IIcon thunderStoneIcon;

   public BlockGSTrap() {
      super(Material.rock);
      this.setStepSound(Block.soundTypeStone);
      this.setBlockName("trap.night");
      this.setHardness(4.5F);
      this.setResistance(5.0F);
      this.setCreativeTab(GSTabs.otherItemsTab);
      this.setHarvestLevel("pickaxe", 1);
   }

   public void registerBlockIcons(IIconRegister iconRegister) {
      this.blockIcon = iconRegister.registerIcon("nether_brick");
      this.thunderStoneIcon = iconRegister.registerIcon("stonebrick");
   }

   public IIcon getIcon(int side, int metadata) {
      switch(metadata) {
      case 0:
      default:
         return this.blockIcon;
      case 1:
         return this.thunderStoneIcon;
      }
   }

   public Item getItemDropped(int par1, Random random, int metadata) {
      switch(metadata) {
      case 0:
      default:
         return Item.getItemFromBlock(Blocks.nether_brick);
      case 1:
         return Item.getItemFromBlock(Blocks.stonebrick);
      }
   }

   public int damageDropped(int metadata) {
      return metadata;
   }

   public boolean canSilkHarvest() {
      return false;
   }

   public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
      if (entity instanceof EntityPlayer) {
         int meta = world.getBlockMetadata(x, y, z);
         if (meta == 0) {
            if (GraveStoneConfig.enableNightStone) {
               long time = world.getWorldTime();
               long dayTime = TimeHelper.getDayTime(time);
               if (dayTime >= 12000L && dayTime <= 22500L) {
                  if (dayTime > 20000L && dayTime < 22500L) {
                     time = time - dayTime + 14000L;
                     world.setWorldTime(time);
                  }
               } else {
                  time = time - dayTime + 12000L;
                  world.setWorldTime(time);
                  if (GraveStoneConfig.showNightStoneMessage) {
                     ((EntityPlayer)entity).addChatComponentMessage(new ChatComponentTranslation(ModGraveStone.proxy.getLocalizedString("block.trap.curse")));
                  }
               }

               ((EntityPlayer)entity).addPotionEffect(new PotionEffect(GSPotion.curse.getId(), 1200));
            }
         } else if (GraveStoneConfig.enableThunderStone && (!world.isThundering() || world.getWorldInfo().getThunderTime() < 1000)) {
            world.getWorldInfo().setRaining(true);
            world.getWorldInfo().setRainTime(10000);
            world.getWorldInfo().setThundering(true);
            world.getWorldInfo().setThunderTime(10000);
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public void getSubBlocks(Item item, CreativeTabs tab, List list) {
      for(byte meta = 0; meta < EnumTrap.values().length; ++meta) {
         list.add(new ItemStack(item, 1, meta));
      }

   }
}
