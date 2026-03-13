package com.voidsrift.riftflux.terramine;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.RFPlantContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockTerraPlant extends BlockBush {
    public enum PlacementRule {
        SURFACE_ONLY,
        ANY_SOLID
    }

    private final String textureName;
    private final PlacementRule placementRule;
    private final String plantKey;

    @SideOnly(Side.CLIENT)
    private IIcon icon;

    public BlockTerraPlant(String blockName, String textureName, PlacementRule placementRule) {
        super();
        this.setBlockName(blockName);
        this.plantKey = blockName;
        this.textureName = textureName;
        this.placementRule = placementRule;
        this.setStepSound(soundTypeGrass);
        this.setHardness(0.0F);
        this.setBlockBounds(0.2F, 0.0F, 0.2F, 0.8F, 0.85F, 0.8F);
        this.setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabDecorations);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.icon = iconRegister.registerIcon("riftflux:" + this.textureName);
        this.blockIcon = this.icon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return this.icon;
    }

    @Override
    protected boolean canPlaceBlockOn(Block block) {
        if (block == null) {
            return false;
        }
        if (this.placementRule == PlacementRule.SURFACE_ONLY) {
            return block == Blocks.grass || block == Blocks.dirt || block == Blocks.mycelium;
        }
        return block.isNormalCube();
    }

    @Override
    public boolean canBlockStay(net.minecraft.world.World world, int x, int y, int z) {
        if (world == null || y <= 0) {
            return false;
        }

        if (ModConfig.allowPlantsOnAnyBlock && RFPlantContext.isPlayerPlaced(world, x, y, z)) {
            Block support = world.getBlock(x, y - 1, z);
            if (support != null && support.getMaterial().isSolid()) {
                return true;
            }
        }

        Block below = world.getBlock(x, y - 1, z);
        if (below == null || !canPlaceBlockOn(below)) {
            return false;
        }
        if (this.placementRule == PlacementRule.ANY_SOLID) {
            return true;
        }
        return super.canBlockStay(world, x, y, z);
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Item.getItemFromBlock(this);
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
        if (world != null && !world.isRemote && requiresShearsForPickup() && !isPlayerUsingShears(player)) {
            return;
        }
        super.harvestBlock(world, player, x, y, z, meta);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block oldBlock, int oldMeta) {
        RFPlantContext.clearPlayerPlaced(world, x, y, z);
        super.breakBlock(world, x, y, z, oldBlock, oldMeta);
    }

    private boolean requiresShearsForPickup() {
        if ("terra_mushroom".equals(this.plantKey)) return ModConfig.terraMushroomRequireShears;
        if ("daybloom".equals(this.plantKey)) return ModConfig.daybloomRequireShears;
        if ("blinkroot".equals(this.plantKey)) return ModConfig.blinkrootRequireShears;
        if ("waterleaf".equals(this.plantKey)) return ModConfig.waterleafRequireShears;
        if ("deathweed".equals(this.plantKey)) return ModConfig.deathweedRequireShears;
        if ("fireblossom".equals(this.plantKey)) return ModConfig.fireblossomRequireShears;
        if ("jungle_spore".equals(this.plantKey)) return ModConfig.jungleSporeRequireShears;
        if ("moonglow".equals(this.plantKey)) return ModConfig.moonglowRequireShears;
        return false;
    }

    private static boolean isPlayerUsingShears(EntityPlayer player) {
        if (player == null || player.capabilities.isCreativeMode) {
            return true;
        }
        ItemStack held = player.getCurrentEquippedItem();
        return held != null && held.getItem() == Items.shears;
    }
}
