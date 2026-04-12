/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.block.ITileEntityProvider
 *  net.minecraft.block.material.Material
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package tk.nukeduck.hearts.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.inventorypets.InventoryPetsContent;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tk.nukeduck.hearts.HeartCrystal;
import tk.nukeduck.hearts.block.TileEntityHeartCrystal;
import tk.nukeduck.hearts.network.ClientProxy;

public class BlockHeartCrystal
extends Block
implements ITileEntityProvider {
    public BlockHeartCrystal() {
        super(Material.glass);
        this.setStepSound(Block.soundTypeGlass);
        this.setBlockBounds(0.125f, 0.0f, 0.125f, 0.875f, 0.9375f, 0.875f);
        this.setLightLevel(0.5f);
    }

    public String getParticle() {
        return "reddust";
    }

    @SideOnly(value=Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        double xRand = (double)x + random.nextDouble();
        double yRand = (double)y + random.nextDouble();
        double zRand = (double)z + random.nextDouble();
        world.spawnParticle(this.getParticle(), xRand, yRand, zRand, random.nextDouble() * 0.7 + 0.3, 0.0, 0.0);
    }

    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityHeartCrystal();
    }

    @SideOnly(value=Side.CLIENT)
    public void registerBlockIcons(IIconRegister icon) {
        this.blockIcon = icon.registerIcon("hearts:heart_crystal");
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean hasTileEntity(int metadata) {
        return true;
    }

    public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
        super.breakBlock(world, x, y, z, block, metadata);
        world.removeTileEntity(x, y, z);
    }

    public void harvestBlock(World world, net.minecraft.entity.player.EntityPlayer player, int x, int y, int z, int metadata) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        boolean naturallyGenerated = tileEntity instanceof TileEntityHeartCrystal
                && ((TileEntityHeartCrystal) tileEntity).isNaturallyGenerated();

        super.harvestBlock(world, player, x, y, z, metadata);

        if (world.isRemote || !naturallyGenerated) {
            return;
        }

        float chance = Math.max(0.0F, Math.min(1.0F, ModConfig.heartCrystalHeartPetDropChance));
        if (chance <= 0.0F || world.rand.nextFloat() >= chance) {
            return;
        }

        Item heartPet = InventoryPetsContent.getItemByKey("heart");
        if (heartPet == null) {
            return;
        }

        double offset = 0.35D;
        EntityItem entity = new EntityItem(
                world,
                x + 0.5D + (world.rand.nextDouble() - 0.5D) * offset,
                y + 0.5D,
                z + 0.5D + (world.rand.nextDouble() - 0.5D) * offset,
                new ItemStack(heartPet)
        );
        entity.delayBeforeCanPickup = 10;
        world.spawnEntityInWorld(entity);
    }

    @SideOnly(value=Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public int getRenderType() {
        return ClientProxy.renderId;
    }

    @SideOnly(value=Side.CLIENT)
    public int getRenderBlockPass() {
        return 1;
    }
}
