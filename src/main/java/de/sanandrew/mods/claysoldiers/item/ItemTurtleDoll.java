/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package de.sanandrew.mods.claysoldiers.item;

import com.google.common.collect.Maps;
import de.sanandrew.mods.claysoldiers.entity.mount.EntityTurtleMount;
import de.sanandrew.mods.claysoldiers.util.IDisruptable;
import de.sanandrew.mods.claysoldiers.util.mount.EnumTurtleType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemTurtleDoll
extends Item
implements IDisruptable {
    private Map<EnumTurtleType, IIcon> icons;
    private IIcon baseIcon;

    public ItemTurtleDoll() {
        this.setMaxStackSize(16);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    public static EntityTurtleMount spawnTurtle(World world, EnumTurtleType type, double x, double y, double z) {
        EntityTurtleMount jordan = new EntityTurtleMount(world, type);
        jordan.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0f), 0.0f);
        jordan.rotationYawHead = jordan.rotationYaw;
        jordan.renderYawOffset = jordan.rotationYaw;
        world.spawnEntityInWorld(jordan);
        jordan.playSound("step.gravel", 1.0f, 1.0f);
        return jordan;
    }

    public static ItemStack setType(ItemStack stack, EnumTurtleType type) {
        if (type.itemData == null) {
            return stack;
        }
        NBTTagCompound nbt = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        nbt.setString("type", type.toString());
        stack.setTagCompound(nbt);
        return stack;
    }

    public static EnumTurtleType getType(ItemStack stack) {
        NBTTagCompound itemNbt = stack.getTagCompound();
        if (itemNbt != null && itemNbt.hasKey("type")) {
            return EnumTurtleType.valueOf(itemNbt.getString("type"));
        }
        return EnumTurtleType.COBBLE;
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
            EntityTurtleMount dan = ItemTurtleDoll.spawnTurtle(world, ItemTurtleDoll.getType(stack), (double)blockX + 0.5, (double)blockY + entityOffY, (double)blockZ + 0.5);
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
        return super.getUnlocalizedName(stack) + '.' + ItemTurtleDoll.getType(stack).toString().toLowerCase();
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int pass) {
        return pass == 0 ? ItemTurtleDoll.getType((ItemStack)stack).itemData.getValue2() : ItemTurtleDoll.getType((ItemStack)stack).itemData.getValue1();
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTab, List stacks) {
        for (EnumTurtleType type : EnumTurtleType.VALUES) {
            if (type.itemData == null) continue;
            ItemStack stack = new ItemStack(this, 1);
            ItemTurtleDoll.setType(stack, type);
            stacks.add(stack);
        }
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        HashMap names = Maps.newHashMap();
        this.icons = Maps.newEnumMap(EnumTurtleType.class);
        for (EnumTurtleType type : EnumTurtleType.VALUES) {
            if (type.itemData == null) continue;
            if (!names.containsKey(type.itemData.getValue0())) {
                names.put(type.itemData.getValue0(), iconRegister.registerIcon(type.itemData.getValue0()));
            }
            this.icons.put(type, (IIcon)names.get(type.itemData.getValue0()));
        }
        this.baseIcon = iconRegister.registerIcon("claysoldiers:doll_turtle_base");
    }

    public int getRenderPasses(int metadata) {
        return 2;
    }

    public IIcon getIcon(ItemStack stack, int pass) {
        return pass == 0 ? this.icons.get((Object)ItemTurtleDoll.getType(stack)) : this.baseIcon;
    }

    @Override
    public void disrupt() {
    }
}

