package com.voidsrift.riftflux.axolotl;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemAxolotlSpawnEgg extends Item {
    public ItemAxolotlSpawnEgg() {
        this.setMaxStackSize(64);
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setTextureName("riftflux:axolotl_spawn_egg");
        this.setUnlocalizedName("axolotl_spawn_egg");
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
                             int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        ForgeDirection direction = ForgeDirection.getOrientation(side);
        EntityAxolotl axolotl = new EntityAxolotl(world);
        axolotl.setVariant(AxolotlVariant.getRandomVariant(world.rand));
        axolotl.setPerrySkin(EntityAxolotl.rollPerrySkin(world.rand));
        axolotl.setLocationAndAngles(
                x + direction.offsetX + 0.5D,
                y + direction.offsetY + 0.2D,
                z + direction.offsetZ + 0.5D,
                world.rand.nextFloat() * 360.0F,
                0.0F
        );
        world.spawnEntityInWorld(axolotl);

        if (stack.hasDisplayName()) {
            axolotl.setCustomNameTag(stack.getDisplayName());
        }

        if (player == null || !player.capabilities.isCreativeMode) {
            stack.stackSize--;
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return false;
    }
}
