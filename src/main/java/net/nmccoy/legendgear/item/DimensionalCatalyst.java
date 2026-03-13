/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.stats.StatBase
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.item.LGItem;

public class DimensionalCatalyst
extends LGItem {
    public DimensionalCatalyst() {
        this.setUnlocalizedName("dimensionalCatalyst");
        this.tabs.add(CreativeTabs.tabMaterials);
        this.setTextureName("legendgear:dimensionalCatalyst");
    }

    public boolean hasEffect(ItemStack p_77636_1_) {
        return false;
    }

    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        if (player.capabilities.allowEdit && (block.canHarvestBlock(player, meta) || block == LegendGear2.azuriteOreBlock) && world.canMineBlock(player, x, y, z) && !block.hasTileEntity(meta) && block.getMobilityFlag() == 0 && !world.isRemote) {
            if (block == LegendGear2.azuriteOreBlock) {
                int dx = EnumFacing.values()[side].getFrontOffsetX();
                int dy = EnumFacing.values()[side].getFrontOffsetY();
                int dz = EnumFacing.values()[side].getFrontOffsetZ();
                world.setBlock(x, y, z, Blocks.stone, 0, 3);
                for (int j = 0; j < 3; ++j) {
                    EntityItem item = new EntityItem(world, (double)x + 0.5 + (double)dx, (double)y + 0.5 + (double)dy, (double)z + 0.5 + (double)dz, new ItemStack((Item)LegendGear2.azurite, 1, 1));
                    world.spawnEntityInWorld((Entity)item);
                }
                world.playSoundEffect((double)x, (double)y, (double)z, "mob.endermen.portal", 1.0f, 1.0f);
                --stack.stackSize;
                player.addStat((StatBase)LegendGear2.achievementAzurite, 1);
                return true;
            }
            for (int i = 0; i < 6; ++i) {
                int dz;
                int pz;
                int dy;
                int py;
                int dx = world.rand.nextInt(3) - 1;
                int px = dx + x;
                if (!block.canPlaceBlockAt(world, px, py = (dy = world.rand.nextInt(3) - 1) + y, pz = (dz = world.rand.nextInt(3) - 1) + z) || !world.isAirBlock(px, py, pz)) continue;
                world.setBlock(px, py, pz, block, meta, 3);
                world.playSoundEffect((double)x, (double)y, (double)z, "mob.endermen.portal", 1.0f, 1.0f);
                world.setBlockToAir(x, y, z);
                --stack.stackSize;
                return true;
            }
        }
        return false;
    }
}

