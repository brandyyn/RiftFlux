/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.util.Vec3
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.PlayerStarstatsExtension;
import net.nmccoy.legendgear.entity.EntityPing;
import net.nmccoy.legendgear.item.LGItem;
import net.nmccoy.legendgear.magic.IMana;

public class SpottingScope
extends LGItem
implements IMana {
    public SpottingScope() {
        this.setMaxStackSize(1);
        this.tabs.add(CreativeTabs.tabTools);
        this.setUnlocalizedName("spottingScope");
        this.setTextureName("legendgear:scope");
    }

    public int getMaxItemUseDuration(ItemStack p_77626_1_) {
        return 60000;
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int count) {
        boolean canUse = !LegendGear2.CONFIG_SPOTTING_SCOPE_CONSUMES_MANA || PlayerStarstatsExtension.availableMana(player) > 0.0f;
        if (!world.isRemote && this.getMaxItemUseDuration(stack) - count <= 5 && canUse) {
            float range = 192.0f;
            Vec3 from = Vec3.createVectorHelper((double)player.posX, (double)(player.posY + (double)player.getEyeHeight()), (double)player.posZ);
            Vec3 direction = player.getLookVec().normalize();
            direction.xCoord *= (double)range;
            direction.yCoord *= (double)range;
            direction.zCoord *= (double)range;
            direction = direction.addVector(from.xCoord, from.yCoord, from.zCoord);
            MovingObjectPosition trace = world.rayTraceBlocks(from, direction);
            Vec3 hit = null;
            if (trace != null) {
                hit = trace.hitVec;
            }
            if (hit != null) {
                world.spawnEntityInWorld((Entity)new EntityPing(world, hit.xCoord, hit.yCoord, hit.zCoord, 500, 0));
                if (LegendGear2.CONFIG_SPOTTING_SCOPE_CONSUMES_MANA) {
                    PlayerStarstatsExtension.get(player).expendMana(stack, this.getManaCost());
                }
            }
        }
    }

    @Override
    public float getManaCost() {
        return 10.0f;
    }
}
