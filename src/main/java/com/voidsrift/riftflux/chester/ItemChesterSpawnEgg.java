package com.voidsrift.riftflux.chester;

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

public class ItemChesterSpawnEgg extends Item {
    private static final int PRIMARY_COLOR = 0x164BF0;
    private static final int SECONDARY_COLOR = 0xC4A000;

    public ItemChesterSpawnEgg() {
        setHasSubtypes(false);
        setMaxStackSize(64);
        setCreativeTab(CreativeTabs.tabMisc);
        setTextureName("minecraft:spawn_egg");
        setUnlocalizedName("chester_spawn_egg");
    }

    @Override
    public boolean onItemUse(
            ItemStack stack,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z,
            int side,
            float hitX,
            float hitY,
            float hitZ
    ) {
        if (world.isRemote) {
            return true;
        }

        ForgeDirection direction = ForgeDirection.getOrientation(side);
        EntityChester chester = new EntityChester(world);
        chester.setLocationAndAngles(
                x + direction.offsetX + 0.5D,
                y + direction.offsetY + 0.2D,
                z + direction.offsetZ + 0.5D,
                world.rand.nextFloat() * 360.0F,
                0.0F
        );
        chester.setTamed(true);
        chester.func_152115_b(player.getUniqueID().toString());
        chester.setHealth(chester.getMaxHealth());
        chester.onSpawnWithEgg(null);
        if (stack.hasDisplayName()) {
            chester.setCustomNameTag(stack.getDisplayName());
        }
        world.spawnEntityInWorld(chester);
        world.setEntityState(chester, (byte) 7);

        if (!player.capabilities.isCreativeMode) {
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
        return pass == 0 ? PRIMARY_COLOR : SECONDARY_COLOR;
    }
}
