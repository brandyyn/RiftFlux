package com.voidsrift.riftflux.asgardshield;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;

public class AsgardShieldEventHandler {

    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (!(event.entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.entity;
        // Ensure tags exist with deterministic defaults.
        AsgardShieldState.setGuardGauge(player, AsgardShieldState.getGuardGauge(player));
        AsgardShieldState.setGuardBroken(player, AsgardShieldState.isGuardBroken(player));
        AsgardShieldState.setVanguardCount(player, AsgardShieldState.getVanguardCount(player));
        AsgardShieldState.setVanguardTicks(player, AsgardShieldState.getVanguardTicks(player));
        AsgardShieldState.setLivingmetalAura(player, AsgardShieldState.getLivingmetalAura(player));
        AsgardShieldState.setBiomassAura(player, AsgardShieldState.getBiomassAura(player));
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        if (AsgardShieldLogic.isBlockingWithAsgardItem(player)) {
            return;
        }
        ItemStack held = player.getHeldItem();
        float mult = AsgardShieldLogic.getPassiveDamageMultiplier(held);
        if (mult != 1.0F) {
            event.ammount = event.ammount * mult;
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        if (!AsgardShieldLogic.isBlockingWithAsgardItem(player)) {
            return;
        }
        if (AsgardShieldState.isGuardBroken(player)) {
            return;
        }
        if (AsgardShieldLogic.handleGuardHit(player, event.source, event.ammount)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        tickGuardGauge(player);
        tickVanguard(player);
    }

    private void tickGuardGauge(EntityPlayer player) {
        int gauge = AsgardShieldState.getGuardGauge(player);
        boolean broken = AsgardShieldState.isGuardBroken(player);
        boolean guarding = AsgardShieldLogic.isBlockingWithAsgardItem(player) && !broken;

        ItemStack inUse = player.getItemInUse();
        boolean gildedShield = inUse != null
                && inUse.getItem() instanceof ItemAsgardShield
                && ((ItemAsgardShield) inUse.getItem()).isGilded();

        if (guarding) {
            boolean wardActivated = false;
            int wardLevel = getConfiguredEnchantLevel(ModConfig.asgardShieldHarkenWardAugmentId, inUse);
            if (wardLevel > 0 && (player.getRNG().nextInt(100) + 1) <= (wardLevel * 10 + 1)) {
                wardActivated = true;
            }

            int increase = 0;
            if (wardActivated) {
                increase = 1;
            } else {
                if (!gildedShield) {
                    increase++;
                }
                increase += 2;
            }
            gauge += increase;

            if (gauge >= 200) {
                gauge = 200;
                broken = true;
                player.stopUsingItem();
            }
        }

        if (gauge > 0) {
            gauge--;
            if (broken) {
                gauge--;
            }
        }
        if (gauge < 0) {
            gauge = 0;
        }
        if (broken && gauge <= 60) {
            broken = false;
        }

        if (player.isDead) {
            broken = false;
        }

        AsgardShieldState.setGuardGauge(player, gauge);
        AsgardShieldState.setGuardBroken(player, broken);
    }

    private void tickVanguard(EntityPlayer player) {
        if (!ModConfig.asgardShieldEnableVanguard) {
            return;
        }

        int count = AsgardShieldState.getVanguardCount(player);
        int ticks = AsgardShieldState.getVanguardTicks(player);

        if (playerHasShieldInHotbar(player) && !player.capabilities.isCreativeMode) {
            if (count < 7) {
                ticks++;
                if (ticks >= 100) {
                    ticks = 0;
                    count++;
                }
            }
        }

        boolean ready = count >= 7;
        ItemStack inUse = player.getItemInUse();
        boolean usingShield = AsgardShieldLogic.isBlockingWithAsgardItem(player)
                && inUse != null
                && inUse.getItem() instanceof ItemAsgardShield;

        if (ready && usingShield && player.isSneaking()) {
            if (player.worldObj.isRemote) {
                spawnVanguardParticles(player);
            } else {
                applyVanguardEffect(player);
            }
            player.worldObj.playSoundAtEntity(player, "random.break", 1.0F, 1.0F);
            count = 0;
            ticks = 0;
            AsgardShieldState.setGuardGauge(player, AsgardShieldState.getGuardGauge(player) / 2);
        }

        AsgardShieldState.setVanguardCount(player, count);
        AsgardShieldState.setVanguardTicks(player, ticks);
    }

    private static void spawnVanguardParticles(EntityPlayer player) {
        for (int i = 0; i < 5; i++) {
            double px = player.posX + (player.getRNG().nextDouble() - 0.5D) * 0.8D;
            double py = player.posY + player.getEyeHeight();
            double pz = player.posZ + (player.getRNG().nextDouble() - 0.5D) * 0.8D;
            player.worldObj.spawnParticle("instantSpell", px, py, pz, 0.0D, 0.05D, 0.0D);
        }
    }

    private static void applyVanguardEffect(EntityPlayer player) {
        AxisAlignedBB area = player.boundingBox.expand(6.0D, 6.0D, 6.0D);
        List<EntityLivingBase> targets = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, area);
        for (EntityLivingBase target : targets) {
            if (target == null || target == player || target.isDead) {
                continue;
            }
            target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 200, 0));
            target.addPotionEffect(new PotionEffect(Potion.poison.id, 200, 0));
            target.addPotionEffect(new PotionEffect(Potion.weakness.id, 200, 2));
            if (player.canEntityBeSeen(target)) {
                player.attackTargetEntityWithCurrentItem(target);
            }
        }
    }

    private static boolean playerHasShieldInHotbar(EntityPlayer player) {
        if (player == null || player.inventory == null || player.inventory.mainInventory == null) {
            return false;
        }
        for (int i = 0; i < 9 && i < player.inventory.mainInventory.length; i++) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack != null && stack.getItem() instanceof ItemAsgardShield) {
                return true;
            }
        }
        return false;
    }

    private static int getConfiguredEnchantLevel(int enchantId, ItemStack stack) {
        if (stack == null || enchantId <= 0 || enchantId >= Enchantment.enchantmentsList.length) {
            return 0;
        }
        if (Enchantment.enchantmentsList[enchantId] == null) {
            return 0;
        }
        return EnchantmentHelper.getEnchantmentLevel(enchantId, stack);
    }
}
