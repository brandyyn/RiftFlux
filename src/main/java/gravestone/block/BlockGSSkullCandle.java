package gravestone.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.block.enums.EnumSkullCandle;
import gravestone.config.GraveStoneConfig;
import gravestone.core.GSTabs;
import gravestone.core.TimeHelper;
import gravestone.particle.EntityGreenFlameFX;
import gravestone.tileentity.TileEntityGSSkullCandle;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockGSSkullCandle extends BlockContainer {
   public BlockGSSkullCandle() {
      super(Material.circuits);
      this.setStepSound(Block.soundTypeStone);
      this.setBlockName("Skull Candle");
      this.setHardness(0.5F);
      this.setResistance(5.0F);
      this.setLightLevel(1.0F);
      this.setBlockTextureName("snow");
      this.setCreativeTab(GSTabs.otherItemsTab);
      this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean renderAsNormalBlock() {
      return false;
   }

   public int getRenderType() {
      return GraveStoneConfig.skullCandleRenderID;
   }

   public TileEntity createNewTileEntity(World world, int var2) {
      return new TileEntityGSSkullCandle();
   }

   public int damageDropped(int damage) {
      return damage;
   }

   public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
      world.setBlockMetadataWithNotify(x, y, z, stack.getItemDamage(), 2);
      TileEntityGSSkullCandle tileEntity = (TileEntityGSSkullCandle)world.getTileEntity(x, y, z);
      if (tileEntity != null) {
         float skullRotation = entity.rotationYaw - 180.0F - 22.5F;
         if (skullRotation < 0.0F) {
            skullRotation += 360.0F;
         }

         tileEntity.setRotation((byte)MathHelper.ceiling_double_int((double)(skullRotation * 8.0F / 360.0F)));
      }

   }

   @SideOnly(Side.CLIENT)
   public void getSubBlocks(Item item, CreativeTabs tab, List list) {
      for(byte i = 0; i < EnumSkullCandle.values().length; ++i) {
         list.add(new ItemStack(item, 1, i));
      }

   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(World world, int x, int y, int z, Random random) {
      TileEntityGSSkullCandle tileEntity = (TileEntityGSSkullCandle)world.getTileEntity(x, y, z);
      if (tileEntity != null) {
         double xPos = (double)((float)x + 0.5F);
         double yPos = (double)y + 0.85D;
         double zPos = (double)((float)z + 0.5F);
         double rotation = Math.toRadians((double)((float)(tileEntity.getRotation() * 360) / 8.0F));
         double d = 0.07D;
         double dx = -Math.sin(rotation) * d;
         double dz = Math.cos(rotation) * d;
         long dayTime = TimeHelper.getDayTime(world);
         if (dayTime >= 13000L && dayTime <= 23000L) {
            EntityFX entityfx = new EntityGreenFlameFX(world, xPos + dx, yPos, zPos + dz, 0.0D, 0.0D, 0.0D);
            Minecraft.getMinecraft().effectRenderer.addEffect(entityfx);
         } else {
            world.spawnParticle("flame", xPos + dx, yPos, zPos + dz, 0.0D, 0.0D, 0.0D);
         }

         world.spawnParticle("smoke", xPos + dx, yPos, zPos + dz, 0.0D, 0.0D, 0.0D);
      }

   }
}
