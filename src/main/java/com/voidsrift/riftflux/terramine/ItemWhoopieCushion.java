package com.voidsrift.riftflux.terramine;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.legendgear.LegendGearContent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.nmccoy.legendgear.PlayerStarstatsExtension;

import java.util.List;

public class ItemWhoopieCushion extends Item {
    public ItemWhoopieCushion() {
        this.setMaxStackSize(1);
        this.setTextureName("riftflux:whoopie_cushion");
        this.setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabMisc);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote && player != null) {
            boolean useLegendGearMana = shouldUseLegendGearMana(player);
            if (useLegendGearMana && !hasEnoughLegendGearMana(player)) {
                return stack;
            }

            float pitch = 0.95F + world.rand.nextFloat() * 0.15F;
            world.playSoundAtEntity(player, "riftflux:whoopie_cushion", 1.0F, pitch);

            if (ModConfig.whoopieCushionKnockbackEnabled) {
                applyMobKnockback(world, player);
            }

            if (useLegendGearMana) {
                spendLegendGearMana(stack, player);
            }
        }
        return stack;
    }

    private static float getConfiguredLegendGearManaCost() {
        return Math.max(0.0F, ModConfig.whoopieCushionLegendGearManaCost);
    }

    private static boolean shouldUseLegendGearMana(EntityPlayer player) {
        return player != null
                && !player.capabilities.isCreativeMode
                && ModConfig.enableLegendGearModule
                && LegendGearContent.isEnabled()
                && getConfiguredLegendGearManaCost() > 0.0F;
    }

    private static boolean hasEnoughLegendGearMana(EntityPlayer player) {
        if (player == null || player.capabilities.isCreativeMode) {
            return true;
        }
        float manaCost = getConfiguredLegendGearManaCost();
        if (manaCost <= 0.0F) {
            return true;
        }
        PlayerStarstatsExtension stats = PlayerStarstatsExtension.get(player);
        if (stats == null) {
            return true;
        }
        return PlayerStarstatsExtension.availableMana(player) + 1.0e-4f >= manaCost;
    }

    private static void spendLegendGearMana(ItemStack stack, EntityPlayer player) {
        if (player == null || player.capabilities.isCreativeMode) {
            return;
        }
        float manaCost = getConfiguredLegendGearManaCost();
        if (manaCost <= 0.0F) {
            return;
        }
        PlayerStarstatsExtension stats = PlayerStarstatsExtension.get(player);
        if (stats == null) {
            return;
        }
        stats.expendMana(stack, manaCost);
    }

    private static void applyMobKnockback(World world, EntityPlayer player) {
        double radius = Math.max(0.0D, ModConfig.whoopieCushionKnockbackRadius);
        if (radius <= 0.0D) {
            return;
        }

        double strength = Math.max(0.0D, ModConfig.whoopieCushionKnockbackStrength);
        if (strength <= 0.0D) {
            return;
        }

        AxisAlignedBB area = player.boundingBox.expand(radius, radius, radius);
        List list = world.getEntitiesWithinAABBExcludingEntity(player, area);
        if (list == null || list.isEmpty()) {
            return;
        }

        for (int i = 0; i < list.size(); i++) {
            Object obj = list.get(i);
            if (!(obj instanceof EntityLivingBase) || obj instanceof EntityPlayer) {
                continue;
            }

            Entity entity = (Entity) obj;
            double dy = entity.posY - player.posY;
            double dx = entity.posX - player.posX;
            double dz = entity.posZ - player.posZ;
            double radiusSq = radius * radius;
            if (dx * dx + dy * dy + dz * dz > radiusSq) {
                continue;
            }
            double distSq = dx * dx + dz * dz;
            if (distSq < 1.0E-4D) {
                float yaw = player.rotationYaw * (float) Math.PI / 180.0F;
                dx = -MathHelper.sin(yaw);
                dz = MathHelper.cos(yaw);
                distSq = dx * dx + dz * dz;
            }

            double dist = Math.sqrt(distSq);
            if (dist <= 1.0E-5D) {
                continue;
            }

            double nx = dx / dist;
            double nz = dz / dist;
            double horizontal = 0.8D * strength;
            double vertical = 0.35D * strength;

            entity.addVelocity(nx * horizontal, vertical, nz * horizontal);
            entity.velocityChanged = true;
        }
    }
}
