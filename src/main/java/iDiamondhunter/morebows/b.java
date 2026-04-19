/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.Event
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.projectile.EntityArrow
 *  net.minecraft.init.Items
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.ItemBow
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.player.ArrowLooseEvent
 */
package iDiamondhunter.morebows;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import iDiamondhunter.morebows.MoreBows;
import iDiamondhunter.morebows.d;
import iDiamondhunter.morebows.e;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;

public final class b
extends ItemBow {
    private final byte var_byte_a;
    private final double var_double_a;
    public final byte[] var_byte_arr_a;
    private final boolean var_boolean_a;
    private final float var_float_a;
    private final EnumRarity var_net_minecraft_item_EnumRarity_a;
    @SideOnly(value=Side.CLIENT)
    private IIcon[] var_net_minecraft_util_IIcon_arr_a;

    public b(int n, byte by, double d2, byte[] byArray, boolean bl, float f, EnumRarity enumRarity) {
        this.setMaxDamage(n);
        this.var_byte_a = by;
        this.var_double_a = d2;
        this.var_byte_arr_a = byArray;
        this.var_boolean_a = bl;
        this.var_float_a = f;
        this.var_net_minecraft_item_EnumRarity_a = enumRarity;
    }

    @SideOnly(value=Side.CLIENT)
    public final IIcon getIcon(ItemStack itemStack, int n, EntityPlayer entityPlayer, ItemStack itemStack2, int n2) {
        if (n2 == 0) {
            return this.itemIcon;
        }
        if ((n2 = 72000 - n2) >= this.var_byte_arr_a[0]) {
            return this.var_net_minecraft_util_IIcon_arr_a[2];
        }
        if (n2 > this.var_byte_arr_a[1]) {
            return this.var_net_minecraft_util_IIcon_arr_a[1];
        }
        return this.var_net_minecraft_util_IIcon_arr_a[0];
    }

    public final EnumAction getItemUseAction(ItemStack itemStack) {
        return EnumAction.none;
    }

    public final EnumRarity getRarity(ItemStack itemStack) {
        return this.var_net_minecraft_item_EnumRarity_a;
    }

    public final boolean onEntitySwing(EntityLivingBase entityLivingBase, ItemStack itemStack) {
        if (this.var_byte_a == 1) {
            MoreBows.a(entityLivingBase.worldObj, (Entity)entityLivingBase, "portal", true, 1.0);
        }
        return false;
    }

    public final void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityPlayer entityPlayer, int n) {
        EntityArrow[] entityArrowArray;
        float f;
        block24: {
            int n2;
            block25: {
                block26: {
                    boolean bl;
                    boolean bl2;
                    ArrowLooseEvent arrowLooseEvent = new ArrowLooseEvent(entityPlayer, itemStack, 72000 - n);
                    MinecraftForge.EVENT_BUS.post((Event)arrowLooseEvent);
                    if (arrowLooseEvent.isCanceled()) {
                        return;
                    }
                    f = (float)arrowLooseEvent.charge / this.var_float_a;
                    f = (f * f + f * 2.0f) / 3.0f;
                    if (f < 0.1f) {
                        return;
                    }
                    if (f >= 1.0f) {
                        f = 1.0f;
                        bl2 = true;
                    } else {
                        bl2 = false;
                    }
                    boolean bl3 = bl = entityPlayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel((int)Enchantment.infinity.effectId, (ItemStack)itemStack) > 0;
                    if (this.var_boolean_a) {
                        if (this.var_byte_a == 1) {
                            entityArrowArray = new EntityArrow[]{new e(world, (EntityLivingBase)entityPlayer, f * 2.0f, (byte)1), new e(world, (EntityLivingBase)entityPlayer, f, (byte)1), new e(world, (EntityLivingBase)entityPlayer, f * 1.2f, (byte)1), new e(world, (EntityLivingBase)entityPlayer, f * 1.5f, (byte)1), new e(world, (EntityLivingBase)entityPlayer, f * 1.75f, (byte)1), new e(world, (EntityLivingBase)entityPlayer, f * 1.825f, (byte)1)};
                            entityArrowArray[1].canBePickedUp = 2;
                            entityArrowArray[2].canBePickedUp = 2;
                            entityArrowArray[3].canBePickedUp = 2;
                            entityArrowArray[4].canBePickedUp = 2;
                            entityArrowArray[5].canBePickedUp = 2;
                        } else {
                            if (itemRand.nextInt(4) == 0) {
                                entityArrowArray = new EntityArrow[]{new EntityArrow(world, (EntityLivingBase)entityPlayer, f * 2.0f), new EntityArrow(world, (EntityLivingBase)entityPlayer, f * 1.65f), new EntityArrow(world, (EntityLivingBase)entityPlayer, f * 1.275f)};
                                (new EntityArrow[]{new EntityArrow(world, (EntityLivingBase)entityPlayer, f * 2.0f), new EntityArrow(world, (EntityLivingBase)entityPlayer, f * 1.65f), new EntityArrow(world, (EntityLivingBase)entityPlayer, f * 1.275f)})[2].canBePickedUp = 2;
                            } else {
                                entityArrowArray = new EntityArrow[]{new EntityArrow(world, (EntityLivingBase)entityPlayer, f * 2.0f), new EntityArrow(world, (EntityLivingBase)entityPlayer, f * 1.65f)};
                            }
                            entityArrowArray[1].canBePickedUp = 2;
                        }
                    } else {
                        entityArrowArray = this.var_byte_a == 0 ? new EntityArrow[]{new EntityArrow(world, (EntityLivingBase)entityPlayer, f * 2.0f)} : new EntityArrow[]{new e(world, (EntityLivingBase)entityPlayer, f * 2.0f, this.var_byte_a)};
                    }
                    if (bl) {
                        entityArrowArray[0].canBePickedUp = 2;
                    }
                    int n3 = EnchantmentHelper.getEnchantmentLevel((int)Enchantment.power.effectId, (ItemStack)itemStack);
                    int n4 = EnchantmentHelper.getEnchantmentLevel((int)Enchantment.punch.effectId, (ItemStack)itemStack);
                    boolean bl4 = EnchantmentHelper.getEnchantmentLevel((int)Enchantment.flame.effectId, (ItemStack)itemStack) > 0;
                    for (n2 = 0; n2 < entityArrowArray.length; ++n2) {
                        if (bl2) {
                            entityArrowArray[n2].setIsCritical(true);
                        }
                        if (n3 > 0) {
                            entityArrowArray[n2].setDamage(entityArrowArray[n2].getDamage() + (double)n3 * 0.5 + 0.5);
                        }
                        if (n4 > 0) {
                            entityArrowArray[n2].setKnockbackStrength(n4);
                        }
                        if (bl4) {
                            entityArrowArray[n2].setFire(100);
                            if (this.var_byte_a == 2) {
                                entityArrowArray[n2].setDamage(entityArrowArray[n2].getDamage() * 1.25);
                            }
                        } else if (this.var_byte_a == 2) {
                            entityArrowArray[n2].setFire(50);
                        }
                        entityArrowArray[n2].setDamage(entityArrowArray[n2].getDamage() * this.var_double_a);
                    }
                    if (!bl) {
                        entityPlayer.inventory.consumeInventoryItem(Items.arrow);
                    }
                    itemStack.damageItem(1, (EntityLivingBase)entityPlayer);
                    if (world.isRemote) break block24;
                    if (!this.var_boolean_a) break block25;
                    if (this.var_byte_a != 1) break block26;
                    world.spawnEntityInWorld((Entity)new d(world, entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, f, entityArrowArray));
                    break block24;
                }
                world.spawnEntityInWorld((Entity)entityArrowArray[0]);
                world.spawnEntityInWorld((Entity)entityArrowArray[1]);
                entityArrowArray[1].posX += (double)(entityArrowArray[1].shootingEntity.rotationYaw / 180.0f);
                entityArrowArray[0].setDamage(entityArrowArray[0].getDamage() * 1.5);
                entityArrowArray[1].setDamage(entityArrowArray[1].getDamage() * 1.3);
                if (entityArrowArray.length <= 2) break block24;
                world.spawnEntityInWorld((Entity)entityArrowArray[2]);
                entityArrowArray[2].posX -= (double)(entityArrowArray[2].shootingEntity.rotationYaw / 180.0f);
                entityArrowArray[2].setDamage(entityArrowArray[2].getDamage() * 1.15);
                break block24;
            }
            for (n2 = 0; n2 < entityArrowArray.length; ++n2) {
                world.spawnEntityInWorld((Entity)entityArrowArray[n2]);
            }
        }
        if (this.var_boolean_a && this.var_byte_a != 1) {
            world.playSoundAtEntity((Entity)entityPlayer, "random.bow", 1.0f, 1.0f / (itemRand.nextFloat() * 0.4f + 1.2f) + f * 0.5f);
            world.playSoundEffect(entityPlayer.posX + (double)(entityPlayer.rotationYaw / 180.0f), entityPlayer.posY, entityPlayer.posZ, "random.bow", 1.0f, 1.0f / (itemRand.nextFloat() * 0.4f + 1.2f) + f * 0.5f);
            if (entityArrowArray.length > 2) {
                world.playSoundEffect(entityPlayer.posX - (double)(entityPlayer.rotationYaw / 180.0f), entityPlayer.posY, entityPlayer.posZ, "random.bow", 1.0f, 1.0f / (itemRand.nextFloat() * 0.4f + 1.2f) + f * 0.5f);
                return;
            }
        } else {
            world.playSoundAtEntity((Entity)entityPlayer, "random.bow", 1.0f, 1.0f / (itemRand.nextFloat() * 0.4f + 1.2f) + f * 0.5f);
        }
    }

    @SideOnly(value=Side.CLIENT)
    public final void registerIcons(IIconRegister iIconRegister) {
        this.itemIcon = iIconRegister.registerIcon(this.getIconString() + "1");
        this.var_net_minecraft_util_IIcon_arr_a = new IIcon[3];
        for (int i = 0; i < this.var_net_minecraft_util_IIcon_arr_a.length; ++i) {
            this.var_net_minecraft_util_IIcon_arr_a[i] = iIconRegister.registerIcon(this.getIconString() + (i + 2));
        }
    }
}
