package com.voidsrift.riftflux.duckling;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemDucklingSpawnEgg extends Item {
    private final String mobKey;
    private final int primaryColor;
    private final int secondaryColor;

    public ItemDucklingSpawnEgg(String mobKey, String unlocalizedName, int primaryColor, int secondaryColor) {
        this.mobKey = mobKey;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.setHasSubtypes(false);
        this.setMaxStackSize(64);
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setTextureName("minecraft:spawn_egg");
        this.setUnlocalizedName(unlocalizedName);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
                             int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        EntityLiving mob = DuckMobFactory.createMobByKey(world, mobKey);
        if (mob == null) {
            return false;
        }

        ForgeDirection direction = ForgeDirection.getOrientation(side);
        mob.setLocationAndAngles(
                x + direction.offsetX + 0.5D,
                y + direction.offsetY + 0.2D,
                z + direction.offsetZ + 0.5D,
                world.rand.nextFloat() * 360.0F,
                0.0F
        );
        mob.onSpawnWithEgg(null);
        if (stack.hasDisplayName()) {
            mob.setCustomNameTag(stack.getDisplayName());
        }
        world.spawnEntityInWorld(mob);

        if (player == null || !player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
        return Items.spawn_egg.getIconFromDamageForRenderPass(0, pass);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
        return pass == 0 ? primaryColor : secondaryColor;
    }
}
