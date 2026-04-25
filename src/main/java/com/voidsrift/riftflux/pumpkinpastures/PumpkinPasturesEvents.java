package com.voidsrift.riftflux.pumpkinpastures;

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
        if (!isPumpkinTool(held)) {
            return;
        }
        if (held.getItem() == PumpkinPasturesContent.pumpkinPickaxe) {
            return;
        }

        Item blockItem = Item.getItemFromBlock(event.block);
        if (blockItem == null) {
            return;
        }
        ItemStack smeltInput = new ItemStack(blockItem, 1, event.blockMetadata);
        ItemStack smeltResult = FurnaceRecipes.smelting().getSmeltingResult(smeltInput);
        if (smeltResult == null) {
            return;
        }

        event.drops.clear();
        ItemStack output = smeltResult.copy();
        if (output.stackSize <= 0) {
            output.stackSize = 1;
        }
        event.drops.add(output);

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

    private static boolean isPumpkinTool(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return false;
        }
        Item item = stack.getItem();
        return item == PumpkinPasturesContent.pumpkinSword
                || item == PumpkinPasturesContent.pumpkinPickaxe
                || item == PumpkinPasturesContent.pumpkinAxe
                || item == PumpkinPasturesContent.pumpkinShovel;
    }
}
