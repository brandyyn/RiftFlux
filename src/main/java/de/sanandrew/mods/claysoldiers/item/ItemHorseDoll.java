/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package de.sanandrew.mods.claysoldiers.item;

import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.mods.claysoldiers.entity.mount.EntityHorseMount;
import de.sanandrew.mods.claysoldiers.entity.mount.EntityPegasusMount;
import de.sanandrew.mods.claysoldiers.util.IDisruptable;
import de.sanandrew.mods.claysoldiers.util.mount.EnumHorseType;
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

public class ItemHorseDoll
extends Item
implements IDisruptable {
    private Map<EnumHorseType, IIcon> p_icons;
    private IIcon p_pegasusWings;

    public ItemHorseDoll() {
        this.setMaxStackSize(16);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    public static EntityHorseMount spawnHorse(World world, EnumHorseType type, boolean isPegasus, double x, double y, double z) {
        EntityHorseMount jordan = isPegasus ? new EntityPegasusMount(world, type) : new EntityHorseMount(world, type);
        jordan.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0f), 0.0f);
        jordan.rotationYawHead = jordan.rotationYaw;
        jordan.renderYawOffset = jordan.rotationYaw;
        world.spawnEntityInWorld(jordan);
        jordan.playSound("step.gravel", 1.0f, 1.0f);
        return jordan;
    }

    public static void setType(ItemStack stack, EnumHorseType type, boolean isPegasus) {
        if (type.itemData == null) {
            return;
        }
        NBTTagCompound nbt = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        nbt.setString("type", type.toString());
        nbt.setBoolean("pegasus", isPegasus);
        stack.setTagCompound(nbt);
    }

    public static EnumHorseType getType(ItemStack stack) {
        NBTTagCompound itemNbt = stack.getTagCompound();
        if (itemNbt != null && itemNbt.hasKey("type")) {
            return EnumHorseType.valueOf(itemNbt.getString("type"));
        }
        return EnumHorseType.DIRT;
    }

    public static boolean isPegasus(ItemStack stack) {
        NBTTagCompound itemNbt = stack.getTagCompound();
        return itemNbt != null && itemNbt.getBoolean("pegasus");
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
            EntityHorseMount dan = ItemHorseDoll.spawnHorse(world, ItemHorseDoll.getType(stack), ItemHorseDoll.isPegasus(stack), (double)blockX + 0.5, (double)blockY + entityOffY, (double)blockZ + 0.5);
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
        String name = super.getUnlocalizedName(stack) + '.' + ItemHorseDoll.getType(stack).toString().toLowerCase();
        if (ItemHorseDoll.isPegasus(stack)) {
            name = name + ".pegasus";
        }
        return name;
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int pass) {
        return pass == 0 || !ItemHorseDoll.isPegasus(stack) ? ItemHorseDoll.getType((ItemStack)stack).itemData.getValue1() : 0xFFFFFF;
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTab, List stacks) {
        for (EnumHorseType type : EnumHorseType.VALUES) {
            if (type.itemData == null) continue;
            ItemStack stack = new ItemStack(this, 1);
            ItemHorseDoll.setType(stack, type, false);
            stacks.add(stack.copy());
            ItemHorseDoll.setType(stack, type, true);
            stacks.add(stack);
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        HashMap names = Maps.newHashMap();
        this.p_icons = Maps.newEnumMap(EnumHorseType.class);
        for (EnumHorseType type : EnumHorseType.VALUES) {
            if (type.itemData == null) continue;
            if (!names.containsKey(type.itemData.getValue0())) {
                names.put(type.itemData.getValue0(), iconRegister.registerIcon(type.itemData.getValue0()));
            }
            this.p_icons.put(type, (IIcon)names.get(type.itemData.getValue0()));
        }
        this.p_pegasusWings = iconRegister.registerIcon("claysoldiers:doll_pegasus_wing");
    }

    public int getRenderPasses(int metadata) {
        return 2;
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        return pass == 0 || !ItemHorseDoll.isPegasus(stack) ? this.p_icons.get((Object)ItemHorseDoll.getType(stack)) : this.p_pegasusWings;
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon getIconFromType(EnumHorseType type) {
        return this.p_icons.get((Object)type);
    }

    @Override
    public void disrupt() {
    }
}

