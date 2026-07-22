/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.claysoldiers.util.CsmPlayerProperties;
import de.sanandrew.mods.claysoldiers.util.IDisruptable;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class ItemDisruptor
extends Item {
    private final boolean p_isHard;

    public ItemDisruptor(boolean hardened) {
        this.maxStackSize = 1;
        this.p_isHard = hardened;
        this.setMaxDamage((hardened ? 50 : 10) - 1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        ItemDisruptor.disrupt(stack, world, player.posX, player.posY, player.posZ, player);
        return stack;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("claysoldiers" + (this.p_isHard ? ":disruptor_cooked" : ":disruptor"));
    }

    public static void disrupt(ItemStack stack, World world, double x, double y, double z, EntityPlayer player) {
        if (!world.isRemote) {
            if (player != null && player.capabilities.isCreativeMode && player.isSneaking()) {
                List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, ItemDisruptor.getRangedBB(x, y, z, 32.0));
                for (EntityItem item : items) {
                    if (item.getEntityItem() == null || !(item.getEntityItem().getItem() instanceof IDisruptable)) continue;
                    item.setDead();
                }
            }
            if (player != null) {
                CsmPlayerProperties prop = CsmPlayerProperties.get(player);
                if (prop != null && prop.canDisruptorBeUsed()) {
                    prop.setDisruptorFired();
                } else {
                    return;
                }
            }
            List<IDisruptable> disruptables = world.getEntitiesWithinAABB(IDisruptable.class, ItemDisruptor.getRangedBB(x, y, z, 32.0));
            for (IDisruptable disruptable : disruptables) {
                disruptable.disrupt();
            }
            if (player != null) {
                if (!player.capabilities.isCreativeMode) {
                    stack.damageItem(1, player);
                }
            } else if (stack.attemptDamageItem(1, SAPUtils.RNG)) {
                --stack.stackSize;
            }
        }
    }

    private static AxisAlignedBB getRangedBB(double x, double y, double z, double range) {
        return AxisAlignedBB.getBoundingBox(x - range, y - range, z - range, x + range, y + range, z + range);
    }
}
