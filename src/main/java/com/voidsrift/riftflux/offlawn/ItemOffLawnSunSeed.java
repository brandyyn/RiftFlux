package com.voidsrift.riftflux.offlawn;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.RFPlantContext;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemOffLawnSunSeed extends Item {
    public ItemOffLawnSunSeed() {
        setUnlocalizedName("offlawn_sun_seed");
        setTextureName("riftflux:offlawn/sun_seed");
        setCreativeTab(CreativeTabs.tabMaterials);
        setMaxStackSize(64);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
                             float hitX, float hitY, float hitZ) {
        if (side != 1) {
            return false;
        }
        if (player != null && !player.canPlayerEdit(x, y, z, side, stack)) {
            return false;
        }

        ForgeDirection dir = ForgeDirection.UP;
        net.minecraft.block.Block soil = world.getBlock(x, y, z);
        int placeX = x + dir.offsetX;
        int placeY = y + dir.offsetY;
        int placeZ = z + dir.offsetZ;

        if (!world.isAirBlock(placeX, placeY, placeZ) || !world.isAirBlock(placeX, placeY + 1, placeZ)) {
            return false;
        }

        if (soil == null || !(OffLawnContent.sunflowerBush instanceof IPlantable)) {
            return false;
        }
        boolean supportsAnyPlant = ModConfig.allowPlantsOnAnyBlock && soil.getMaterial().isSolid();
        boolean supportsSunflower = soil == OffLawnContent.beanstalk
                || soil.canSustainPlant(world, x, y, z, ForgeDirection.UP, (IPlantable) OffLawnContent.sunflowerBush);
        if (!supportsAnyPlant && !supportsSunflower) {
            return false;
        }

        net.minecraft.block.Block plantedSunflower = OffLawnContent.sunflowerBush;
        if (OffLawnContent.brightSunflower != null
                && world.rand.nextFloat() < ModConfig.offLawnBrightSunflowerSeedChance) {
            plantedSunflower = OffLawnContent.brightSunflower;
        }

        if (plantedSunflower instanceof BlockOffLawnSunflowerBush) {
            ((BlockOffLawnSunflowerBush) plantedSunflower).placeAt(world, placeX, placeY, placeZ, 2);
        } else {
            world.setBlock(placeX, placeY, placeZ, plantedSunflower, 0, 3);
        }

        RFPlantContext.markPlayerPlaced(world, placeX, placeY, placeZ);
        RFPlantContext.markPlayerPlaced(world, placeX, placeY + 1, placeZ);
        boolean shouldMarkFacing = ModConfig.directionalCrossedPlantRenderingByPlacement
                && ModConfig.directionalCrossedPlantFacePlayerOnPlacement;
        if (shouldMarkFacing && player != null) {
            int sunflowerFacing = ((net.minecraft.util.MathHelper
                    .floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) + 3) % 4;
            world.setBlockMetadataWithNotify(placeX, placeY + 1, placeZ, 8 | sunflowerFacing, 2);
            RFPlantContext.markCrossedPlantFacingFromPlacer(world, placeX, placeY, placeZ, player);
            RFPlantContext.markCrossedPlantFacingFromPlacer(world, placeX, placeY + 1, placeZ, player);
        }

        if (plantedSunflower != null && plantedSunflower.stepSound != null) {
            world.playSoundEffect(
                    placeX + 0.5D,
                    placeY + 0.5D,
                    placeZ + 0.5D,
                    plantedSunflower.stepSound.getStepResourcePath(),
                    (plantedSunflower.stepSound.getVolume() + 1.0F) / 2.0F,
                    plantedSunflower.stepSound.getPitch() * 0.8F
            );
        }

        if (player == null || !player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }
        return true;
    }
}
