/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.claysoldiers.entity.mount.EntityGeckoMount;
import de.sanandrew.mods.claysoldiers.util.IDisruptable;
import de.sanandrew.mods.claysoldiers.util.mount.EnumGeckoType;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemGeckoDoll
extends Item
implements IDisruptable {
    public IIcon iconBody;

    public ItemGeckoDoll() {
        this.setMaxStackSize(16);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    public static EntityGeckoMount spawnGecko(World world, EnumGeckoType type, double x, double y, double z) {
        EntityGeckoMount jordan = new EntityGeckoMount(world, type);
        jordan.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0f), 0.0f);
        jordan.rotationYawHead = jordan.rotationYaw;
        jordan.renderYawOffset = jordan.rotationYaw;
        world.spawnEntityInWorld(jordan);
        jordan.playSound("step.wood", 1.0f, 1.0f);
        return jordan;
    }

    public static EnumGeckoType getType(ItemStack stack) {
        if (stack == null || !SAPUtils.isIndexInRange((Object[])EnumGeckoType.VALUES, stack.getItemDamage())) {
            return EnumGeckoType.BIRCH_BIRCH;
        }
        return EnumGeckoType.VALUES[stack.getItemDamage()];
    }

    public static void setType(ItemStack stack, EnumGeckoType type) {
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
            EntityGeckoMount dan = ItemGeckoDoll.spawnGecko(world, ItemGeckoDoll.getType(stack), (double)blockX + 0.5, (double)blockY + entityOffY, (double)blockZ + 0.5);
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
        return super.getUnlocalizedName(stack) + '.' + ItemGeckoDoll.getType(stack).toString().toLowerCase();
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int pass) {
        return pass == 1 ? ItemGeckoDoll.getType((ItemStack)stack).colors.getValue1() : ItemGeckoDoll.getType((ItemStack)stack).colors.getValue0();
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTab, List stacks) {
        for (EnumGeckoType type : EnumGeckoType.VALUES) {
            ItemStack stack = new ItemStack(this, 1);
            ItemGeckoDoll.setType(stack, type);
            stacks.add(stack);
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("claysoldiers:doll_gecko_spots");
        this.iconBody = iconRegister.registerIcon("claysoldiers:doll_gecko_body");
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    public int getRenderPasses(int metadata) {
        return 2;
    }

    @Override
    public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
        return pass == 1 ? this.iconBody : this.itemIcon;
    }

    @Override
    public void disrupt() {
    }
}

