/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  baubles.api.BaublesApi
 *  cpw.mods.fml.common.Loader
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.Vec3
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.item;

import baubles.api.BaublesApi;
import cpw.mods.fml.common.Loader;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.PlayerStarstatsExtension;
import net.nmccoy.legendgear.item.LGItem;
import net.nmccoy.legendgear.magic.IMana;

public class AzureFeather
extends LGItem
implements IMana {
    public static float FEATHER_BOOST = 16.0f;

    public AzureFeather() {
        this.setUnlocalizedName("azureFeather");
        this.setTextureName("legendgear:azureFeather");
        this.tabs.add(CreativeTabs.tabTransport);
        this.setShiny();
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.onGround) {
            return stack;
        }
        PlayerStarstatsExtension pse = PlayerStarstatsExtension.get(player);
        boolean used = false;
        if (Loader.isModLoaded((String)"Baubles")) {
            ItemStack neckStack = BaublesApi.getBaubles((EntityPlayer)player).getStackInSlot(0);
            if (neckStack == null) {
                return stack;
            }
            int damage = neckStack.getItemDamage();
            if (neckStack.getItem() == LegendGear2.charmPendant && (damage == 1 || damage == 2) && PlayerStarstatsExtension.availableMana(player) > 0.0f) {
                if (pse.getGlide() > 0.0f) {
                    pse.setGlide(pse.getGlide() + FEATHER_BOOST);
                    used = true;
                } else if (pse.skylensTagCharge > 0 || damage == 2 || player.fallDistance > 23.0f) {
                    pse.setGlide((float)player.posY + FEATHER_BOOST + player.fallDistance);
                    used = true;
                }
            }
        }
        if (used) {
            --stack.stackSize;
            world.playSoundAtEntity((Entity)player, "legendgear:feather", 0.3f, 1.0f);
            world.playSoundAtEntity((Entity)player, "legendgear:whirlwind", 0.3f, 1.5f);
            float boost = 0.25f;
            Vec3 ahead = player.getLookVec();
            player.motionX += ahead.xCoord * (double)boost;
            player.motionY += ahead.yCoord * (double)boost;
            player.motionZ += ahead.zCoord * (double)boost;
            pse.expendMana(stack, 12.0f);
        }
        return stack;
    }

    @Override
    public float getManaCost() {
        return 12.0f;
    }
}

