package com.voidsrift.riftflux.terramine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemDemonEyeSpawnEgg extends Item {
    private final int primaryColor;
    private final int secondaryColor;

    public ItemDemonEyeSpawnEgg(int primaryColor, int secondaryColor) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.setHasSubtypes(false);
        this.setMaxStackSize(64);
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setTextureName("minecraft:spawn_egg");
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
                             int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        ForgeDirection direction = ForgeDirection.getOrientation(side);
        double spawnX = x + direction.offsetX + 0.5D;
        double spawnY = y + direction.offsetY + 0.1D;
        double spawnZ = z + direction.offsetZ + 0.5D;

        EntityDemonEye demonEye = new EntityDemonEye(world);
        demonEye.setLocationAndAngles(spawnX, spawnY, spawnZ, world.rand.nextFloat() * 360.0F, 0.0F);
        demonEye.setIsBatHanging(false);
        world.spawnEntityInWorld(demonEye);

        if (player == null || !player.capabilities.isCreativeMode) {
            stack.stackSize--;
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
        return Items.spawn_egg.getIconFromDamageForRenderPass(0, pass);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getColorFromItemStack(ItemStack stack, int pass) {
        return pass == 0 ? this.primaryColor : this.secondaryColor;
    }
}
