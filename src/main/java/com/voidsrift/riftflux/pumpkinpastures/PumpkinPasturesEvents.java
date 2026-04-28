package com.voidsrift.riftflux.pumpkinpastures;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PumpkinPasturesEvents {
    @SubscribeEvent
    public void onSkeletonArrowHurt(LivingHurtEvent event) {
        if (event == null || event.source == null || event.entityLiving == null) {
            return;
        }
        if (!(event.source.getSourceOfDamage() instanceof EntityArrow)) {
            return;
        }
        EntityArrow arrow = (EntityArrow) event.source.getSourceOfDamage();
        if (!(arrow.shootingEntity instanceof EntityPumpkinSkeleton)) {
            return;
        }
        EntityLivingBase target = event.entityLiving;
        target.addPotionEffect(new PotionEffect(Potion.wither.id, 100, 0));
    }

    @SubscribeEvent
    public void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        if (event == null || event.world == null || event.world.isRemote || event.harvester == null || event.block == null) {
            return;
        }
        ItemStack held = event.harvester.getCurrentEquippedItem();
        if (!shouldAutoSmeltDrops(held)) {
            return;
        }

        List<ItemStack> smeltedDrops = new ArrayList<ItemStack>(event.drops.size());
        int fortuneMultiplier = this.shouldApplyFortuneAfterAutoSmelt(event)
                ? this.getFortuneMultiplier(event.fortuneLevel, event.world.rand)
                : 1;
        boolean changed = false;
        for (ItemStack drop : event.drops) {
            if (drop == null || drop.getItem() == null) {
                continue;
            }

            ItemStack smelted = FurnaceRecipes.smelting().getSmeltingResult(drop);
            if (smelted == null) {
                smeltedDrops.add(drop);
                continue;
            }

            ItemStack output = smelted.copy();
            output.stackSize = Math.max(1, output.stackSize) * Math.max(1, drop.stackSize) * fortuneMultiplier;
            smeltedDrops.add(output);
            changed = true;
        }

        if (!changed) {
            return;
        }

        event.drops.clear();
        event.drops.addAll(smeltedDrops);

        for (int i = 0; i < 10; i++) {
            event.world.spawnParticle(
                    "flame",
                    event.x + 0.5D + (event.world.rand.nextDouble() - 0.5D) * 0.5D,
                    event.y + 0.35D + event.world.rand.nextDouble() * 0.5D,
                    event.z + 0.5D + (event.world.rand.nextDouble() - 0.5D) * 0.5D,
                    0.0D,
                    0.02D,
                    0.0D
            );
        }
    }

    private static boolean shouldAutoSmeltDrops(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return false;
        }
        if (stack.getItem() == PumpkinPasturesContent.pumpkinPickaxe) {
            return ModConfig.pumpkinPasturesEnderflamePickaxeAutoSmelt;
        }
        if (stack.getItem() == PumpkinPasturesContent.pumpkinAxe) {
            return ModConfig.pumpkinPasturesEnderflameShaxAutoSmelt;
        }
        return false;
    }

    private boolean shouldApplyFortuneAfterAutoSmelt(BlockEvent.HarvestDropsEvent event) {
        if (event == null || event.fortuneLevel <= 0 || event.block == null) {
            return false;
        }

        Item blockItem = Item.getItemFromBlock(event.block);
        if (blockItem == null) {
            return false;
        }

        int[] oreIds = OreDictionary.getOreIDs(new ItemStack(blockItem, 1, event.blockMetadata));
        for (int oreId : oreIds) {
            String oreName = OreDictionary.getOreName(oreId);
            if (oreName != null && oreName.startsWith("ore") && oreName.length() > 3) {
                return true;
            }
        }
        return false;
    }

    private int getFortuneMultiplier(int fortuneLevel, Random random) {
        if (fortuneLevel <= 0) {
            return 1;
        }

        int multiplier = random.nextInt(fortuneLevel + 2) - 1;
        if (multiplier < 0) {
            multiplier = 0;
        }
        return multiplier + 1;
    }
}
