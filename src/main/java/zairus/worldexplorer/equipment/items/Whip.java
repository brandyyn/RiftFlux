/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 *  net.minecraft.world.World
 */
package zairus.worldexplorer.equipment.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import zairus.worldexplorer.core.WorldExplorer;
import zairus.worldexplorer.core.items.WEItem;
import zairus.worldexplorer.equipment.entity.EntityWhipTip;

public class Whip
extends WEItem {
    public static final String[] whipIconNameArray = new String[]{"tail"};
    public boolean unleashed = false;
    private IIcon[] tailIcons;
    private EntityWhipTip whipTip;
    private EntityPlayer shooterPlayer;
    private float throwYaw = 0.0f;
    private float throwPitch = 0.0f;
    private float maxReach = 4.0f;

    public Whip() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("whip");
        this.setTextureName("worldexplorer:whip");
        this.setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabCombat);
        this.setFull3D();
        this.setMaxDamage(512);
    }

    public EntityPlayer getShooter() {
        return this.shooterPlayer;
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon(this.getIconString());
        this.tailIcons = new IIcon[whipIconNameArray.length];
        for (int i = 0; i < this.tailIcons.length; ++i) {
            this.tailIcons[i] = iconRegister.registerIcon(this.getIconString() + "_" + whipIconNameArray[i]);
        }
    }

    public IIcon getIconFromUseTick() {
        return this.tailIcons[0];
    }

    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        return false;
    }

    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int useCount) {
        if (this.whipTip != null && !this.whipTip.isDead) {
            this.whipTip.setDead();
        }
        this.unleashed = false;
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        stack.damageItem(1, (EntityLivingBase)player);
        this.unleashed = true;
        this.shooterPlayer = player;
        player.swingItem();
        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        EntityWhipTip whipTip1 = new EntityWhipTip(world, (Entity)player, 1.0f);
        this.throwYaw = player.rotationYaw;
        this.throwPitch = player.rotationPitch;
        whipTip1.setReach(this.maxReach);
        whipTip1.setWhipItem(player.inventory.getStackInSlot(player.inventory.currentItem));
        whipTip1.setShootingEntity((Entity)player);
        if (!world.isRemote) {
            world.playSoundAtEntity((Entity)player, "worldexplorer:whip_swing", 1.0f, 1.0f / (itemRand.nextFloat() * 0.4f + 1.2f) + 0.5f);
        }
        world.spawnEntityInWorld((Entity)whipTip1);
        this.whipTip = whipTip1;
        return stack;
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.bow;
    }

    public int getMaxItemUseDuration(ItemStack duration) {
        return 72000;
    }

    public double getWhipTipDistance() {
        double distance = 0.0;
        if (this.whipTip != null) {
            distance = this.whipTip.getDistanceFromShooter();
        }
        return distance;
    }

    public float getPercentaje() {
        float p = 0.0f;
        p = (float)this.getWhipTipDistance() / this.maxReach;
        if (p > 1.0f) {
            p = 1.0f;
        }
        return p;
    }

    public float getThrowYaw() {
        return this.throwYaw;
    }

    public float getThrowPitch() {
        return this.throwPitch;
    }

    public float getCurYaw() {
        float curYaw = 0.0f;
        if (this.shooterPlayer != null) {
            curYaw = this.shooterPlayer.rotationYaw;
        }
        return curYaw;
    }

    public float getCurPitch() {
        float curPitch = 0.0f;
        if (this.shooterPlayer != null) {
            curPitch = this.shooterPlayer.rotationPitch;
        }
        return curPitch;
    }
}
