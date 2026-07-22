/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.mods.claysoldiers.entity.mount.EntityBunnyMount;
import de.sanandrew.mods.claysoldiers.util.IDisruptable;
import de.sanandrew.mods.claysoldiers.util.mount.EnumBunnyType;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemBunnyDoll
extends Item
implements IDisruptable {
    public ItemBunnyDoll() {
        this.setMaxStackSize(16);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    public static EntityBunnyMount spawnBunny(World world, EnumBunnyType type, double x, double y, double z) {
        EntityBunnyMount jordan = new EntityBunnyMount(world, type);
        jordan.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0f), 0.0f);
        jordan.rotationYawHead = jordan.rotationYaw;
        jordan.renderYawOffset = jordan.rotationYaw;
        world.spawnEntityInWorld(jordan);
        jordan.playSound("step.gravel", 1.0f, 1.0f);
        return jordan;
    }

    public static void setType(ItemStack stack, EnumBunnyType type) {
        stack.setItemDamage(type.ordinal());
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int blockX, int blockY, int blockZ, int side, float offX, float offY, float offZ) {
        if (world.isRemote) {
            return true;
        }
        Block block = world.getBlock(blockX, blockY, blockZ);
        double entityOffY = 0.0;
        int maxSpawns = stack.stackSize;
        if (player.isSneaking()) {
            maxSpawns = 1;
        }
        if (side == 1 && block.getRenderType() == 11) {
            entityOffY = 0.5;
        }
        blockX += Facing.offsetsXForSide[side];
        blockY += Facing.offsetsYForSide[side];
        blockZ += Facing.offsetsZForSide[side];
        for (int i = 0; i < maxSpawns; ++i) {
            EntityBunnyMount dan = ItemBunnyDoll.spawnBunny(world, EnumBunnyType.getTypeFromItem(stack), (double)blockX + 0.5, (double)blockY + entityOffY, (double)blockZ + 0.5);
            if (dan == null) continue;
            if (stack.hasDisplayName()) {
                dan.setCustomNameTag(stack.getDisplayName());
            }
            if (player.capabilities.isCreativeMode) continue;
            dan.dollItem = stack.splitStack(1);
        }
        return true;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + '.' + EnumBunnyType.getTypeFromItem(stack).toString().toLowerCase();
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int pass) {
        return EnumBunnyType.getTypeFromItem((ItemStack)stack).typeColor;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTab, List stacks) {
        for (EnumBunnyType type : EnumBunnyType.VALUES) {
            ItemStack stack = new ItemStack(this, 1);
            ItemBunnyDoll.setType(stack, type);
            stacks.add(stack);
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("claysoldiers:doll_bunny");
    }

    @Override
    public void disrupt() {
    }
}

