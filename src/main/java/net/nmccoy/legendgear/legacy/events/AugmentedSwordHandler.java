package net.nmccoy.legendgear.legacy.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.nmccoy.legendgear.legacy.LegendGear;
import net.nmccoy.legendgear.legacy.entities.EntityQuake;
import net.nmccoy.legendgear.legacy.entities.EntityWhirlwind;

import java.util.Random;

public class AugmentedSwordHandler {
    private static final int SWORD_CHARGE_TIME = 30;
    private static final int SWORD_DISCHARGE_TIME = 15;
    private static final float SPARK_DISTANCE = 0.75F;

    private final Random rand = new Random();

    @SubscribeEvent
    public void swordTick(LivingEvent.LivingUpdateEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.entityLiving;
        ItemStack stack = player.inventory.getCurrentItem();

        if (player.isBlocking() && this.isAugmentedSword(stack)) {
            int swordCharge = player.getEntityData().getInteger("swordCharge") + 1;
            player.getEntityData().setInteger("swordCharge", swordCharge);

            if (swordCharge < SWORD_CHARGE_TIME && player.worldObj.isRemote && swordCharge > 4) {
                double x = player.posX - (double) (MathHelper.sin(player.rotationYaw / 180.0F * (float) Math.PI) * SPARK_DISTANCE)
                        * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI);
                double y = player.posY + player.getEyeHeight()
                        - (double) (MathHelper.sin(player.rotationPitch / 180.0F * (float) Math.PI) * SPARK_DISTANCE) - 0.125D;
                double z = player.posZ + (double) (MathHelper.cos(player.rotationYaw / 180.0F * (float) Math.PI) * SPARK_DISTANCE)
                        * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI);

                double offX = (double) (MathHelper.cos(player.rotationYaw / 180.0F * (float) Math.PI) * SPARK_DISTANCE);
                double offZ = (double) (MathHelper.sin(player.rotationYaw / 180.0F * (float) Math.PI) * SPARK_DISTANCE);

                double radius = (double) (swordCharge - SWORD_CHARGE_TIME) / (double) SWORD_CHARGE_TIME;
                double theta = radius * Math.PI * 3.0D;
                double horizontal = Math.cos(theta) * radius;
                double vertical = Math.sin(theta) * radius;
                offX *= horizontal;
                offZ *= horizontal;

                double span = 0.6D;
                LegendGear.proxy.addSparkleParticle(player.worldObj, x + offX * span, y + vertical * span, z + offZ * span, 0.0D, 0.0D, 0.0D, 0.3F);
                LegendGear.proxy.addSparkleParticle(player.worldObj, x - offX * span, y - vertical * span, z - offZ * span, 0.0D, 0.0D, 0.0D, 0.3F);
            }

            if (swordCharge == SWORD_CHARGE_TIME && !player.worldObj.isRemote) {
                player.worldObj.playSoundAtEntity(player, "legendgear:partialcharge", 0.15F, 2.0F);
            }
            if (swordCharge >= SWORD_CHARGE_TIME) {
                player.getEntityData().setInteger("medallionEnergy", SWORD_DISCHARGE_TIME);
            }
        } else {
            player.getEntityData().setInteger("swordCharge", 0);
        }

        int energy = player.getEntityData().getInteger("medallionEnergy");
        if (!this.isAugmentedSword(stack)) {
            energy = 0;
        }

        if (energy > 0) {
            energy--;
            if (player.worldObj.isRemote) {
                double x = player.posX - (double) (MathHelper.sin(player.rotationYaw / 180.0F * (float) Math.PI) * SPARK_DISTANCE)
                        * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI);
                double y = player.posY + player.getEyeHeight()
                        - (double) (MathHelper.sin(player.rotationPitch / 180.0F * (float) Math.PI) * SPARK_DISTANCE) - 0.125D;
                double z = player.posZ + (double) (MathHelper.cos(player.rotationYaw / 180.0F * (float) Math.PI) * SPARK_DISTANCE)
                        * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI);

                x += this.rand.nextGaussian() * 0.05D;
                y += this.rand.nextGaussian() * 0.05D;
                z += this.rand.nextGaussian() * 0.05D;

                LegendGear.proxy.addSparkleParticle(player.worldObj, x, y, z, 0.0D, 0.0D, 0.0D, 0.6F);
            }
            if (player.swingProgressInt == 1 && player.getEntityData().getInteger("swordCharge") == 0) {
                this.doSpecialAbility(player, stack);
                energy = 0;
            }
        }

        player.getEntityData().setInteger("medallionEnergy", energy);
    }

    public void doSpecialAbility(EntityPlayer player, ItemStack sword) {
        if (!this.isAugmentedSword(sword)) {
            return;
        }

        Item medallion = this.resolveMedallion(sword);
        if (medallion == null) {
            return;
        }

        int uses = sword.getItem().getItemEnchantability() * 4;
        int health = sword.getItem().getMaxDamage();
        int damage = health / uses;
        double overflow = sword.getTagCompound().getDouble("damageOverflow") + health * 1.0D / uses - damage;
        if (overflow > 1.0D) {
            damage++;
            overflow -= 1.0D;
        }
        sword.getTagCompound().setDouble("damageOverflow", overflow);

        sword.damageItem(damage, player);
        if (sword.stackSize == 0) {
            player.destroyCurrentEquippedItem();
        }

        double atX = -(double) MathHelper.sin(player.rotationYaw / 180.0F * (float) Math.PI)
                * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI);
        double atY = -(double) MathHelper.sin(player.rotationPitch / 180.0F * (float) Math.PI);
        double atZ = (double) MathHelper.cos(player.rotationYaw / 180.0F * (float) Math.PI)
                * MathHelper.cos(player.rotationPitch / 180.0F * (float) Math.PI);

        if (medallion == LegendGear.fireMedallion) {
            if (!player.worldObj.isRemote) {
                EntitySmallFireball fireball = new EntitySmallFireball(
                        player.worldObj,
                        player.posX + atX,
                        player.posY + atY + player.getEyeHeight(),
                        player.posZ + atZ,
                        atX * 0.01D,
                        atY * 0.01D,
                        atZ * 0.01D
                );
                fireball.shootingEntity = player;
                player.worldObj.spawnEntityInWorld(fireball);
                player.worldObj.playSoundAtEntity(player, "mob.ghast.fireball", 1.0F, 1.0F);
            } else {
                for (int i = 0; i < 10; i++) {
                    player.worldObj.spawnParticle("flame",
                            player.posX + atX,
                            player.posY + atY + player.getEyeHeight(),
                            player.posZ + atZ,
                            this.rand.nextGaussian() * 0.04D,
                            this.rand.nextGaussian() * 0.04D,
                            this.rand.nextGaussian() * 0.04D);
                }
            }
        }

        if (medallion == LegendGear.earthMedallion && !player.worldObj.isRemote) {
            EntityQuake quake = new EntityQuake(player.worldObj, player.posX, player.posY, player.posZ, player, true, 5.0D);
            quake.damage_per_hit = 5;
            player.worldObj.spawnEntityInWorld(quake);
            player.worldObj.playSoundAtEntity(quake, "random.explode", 1.0F, 1.2F);
        }

        if (medallion == LegendGear.windMedallion && !player.worldObj.isRemote) {
            player.worldObj.playSoundAtEntity(player, "legendgear:whirlwind", 1.0F, 0.8F + (float) Math.random() * 0.4F);
            player.worldObj.spawnEntityInWorld(new EntityWhirlwind(player.worldObj, player));
        }
    }

    private boolean isAugmentedSword(ItemStack stack) {
        return stack != null
                && stack.getItem() instanceof ItemSword
                && stack.hasTagCompound()
                && (stack.getTagCompound().hasKey("medallion") || stack.getTagCompound().hasKey("medallionName"));
    }

    private Item resolveMedallion(ItemStack sword) {
        if (sword == null || !sword.hasTagCompound()) {
            return null;
        }

        if (sword.getTagCompound().hasKey("medallionName")) {
            Object item = Item.itemRegistry.getObject(sword.getTagCompound().getString("medallionName"));
            if (item instanceof Item) {
                return (Item) item;
            }
        }

        if (sword.getTagCompound().hasKey("medallion")) {
            return Item.getItemById(sword.getTagCompound().getInteger("medallion"));
        }

        return null;
    }
}
