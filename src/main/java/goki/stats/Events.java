/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.DamageSource
 *  net.minecraftforge.event.entity.EntityJoinWorldEvent
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.event.entity.living.LivingEvent$LivingJumpEvent
 *  net.minecraftforge.event.entity.living.LivingFallEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$BreakSpeed
 *  net.minecraftforge.event.world.BlockEvent$HarvestDropsEvent
 */
package goki.stats;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import goki.stats.GokiStats;
import goki.stats.handlers.PacketStatAlter;
import goki.stats.handlers.PacketSyncStatConfig;
import goki.stats.lib.Helper;
import goki.stats.lib.IDMDTuple;
import goki.stats.stats.Stat;
import goki.stats.stats.StatMiningMagician;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;

public class Events {
    @SubscribeEvent
    public void harvestBlock(BlockEvent.HarvestDropsEvent event) {
        EntityPlayer player = event.harvester;
        if (player != null) {
            if (Helper.getPlayerStatLevel(player, Stat.STAT_TREASURE_FINDER) > 0) {
                boolean treasureFound = false;
                Random random = player.getRNG();
                List<ItemStack> items = Stat.STAT_TREASURE_FINDER.getApplicableItemStackList(event.block, event.blockMetadata, Helper.getPlayerStatLevel(player, Stat.STAT_TREASURE_FINDER));
                List<Integer> chances = Stat.STAT_TREASURE_FINDER.getApplicableChanceList(event.block, event.blockMetadata, Helper.getPlayerStatLevel(player, Stat.STAT_TREASURE_FINDER));
                for (int i = 0; i < items.size(); ++i) {
                    Integer roll = random.nextInt(10000);
                    if (roll > chances.get(i)) continue;
                    if (items.get(i) != null) {
                        event.drops.add(items.get(i));
                        treasureFound = true;
                        continue;
                    }
                    System.out.println("Tried to add an item from Treasure Finder, but it failed!");
                }
                if (treasureFound) {
                    player.worldObj.playSoundAtEntity((Entity)player, "gokiStats:treasure", 1.0f, 1.0f);
                }
            }
            if (Helper.getPlayerStatLevel(player, Stat.STAT_MINING_MAGICIAN) > 0) {
                boolean magicHappened = false;
                IDMDTuple mme = new IDMDTuple(event.block, event.blockMetadata);
                if (Stat.STAT_MINING_MAGICIAN.isAffectedByStat(mme) != 0) {
                    block1: for (int i = 0; i < event.drops.size(); ++i) {
                        IDMDTuple entry;
                        if (!(player.getRNG().nextDouble() * 100.0 <= (double)Stat.STAT_MINING_MAGICIAN.getBonus(player))) continue;
                        ItemStack item = (ItemStack)event.drops.get(i);
                        if (item.getItem() instanceof ItemBlock && ItemBlock.getIdFromItem((Item)((ItemBlock)item.getItem())) == Block.getIdFromBlock((Block)event.block)) {
                            if (item.getItemDamage() != event.blockMetadata) continue;
                            int randomEntry = player.getRNG().nextInt(StatMiningMagician.blockEntries.size());
                            entry = StatMiningMagician.blockEntries.get(randomEntry);
                            ItemStack stack = new ItemStack(Item.getItemById((int)entry.id), 1, entry.md);
                            stack.stackSize = ((ItemStack)event.drops.get((int)i)).stackSize;
                            event.drops.set(i, stack);
                            magicHappened = true;
                            continue;
                        }
                        for (int j = 0; j < StatMiningMagician.itemEntries.size(); ++j) {
                            entry = StatMiningMagician.itemEntries.get(j);
                            if (Item.getIdFromItem((Item)item.getItem()) != entry.id || item.getItemDamage() != entry.md) continue;
                            int randomEntry = player.getRNG().nextInt(StatMiningMagician.itemEntries.size());
                            IDMDTuple chosenEntry = StatMiningMagician.itemEntries.get(randomEntry);
                            ItemStack stack = new ItemStack(Item.getItemById((int)chosenEntry.id), 1, chosenEntry.md);
                            stack.stackSize = ((ItemStack)event.drops.get((int)i)).stackSize;
                            event.drops.set(i, stack);
                            magicHappened = true;
                            continue block1;
                        }
                    }
                    if (magicHappened) {
                        player.worldObj.playSoundAtEntity((Entity)player, "gokiStats:magician", 0.3f, 1.0f);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void playerJoinWorld(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.entity;
            if (!player.worldObj.isRemote) {
                GokiStats.packetPipeline.sendTo(new PacketSyncStatConfig(Stat.loseStatsOnDeath, Stat.globalBonusMultiplier, Stat.globalCostMultiplier, Stat.globalLimitMultiplier), (EntityPlayerMP)player);
            } else {
                GokiStats.packetPipeline.sendToServer(new PacketStatAlter(0, 0));
            }
        }
    }

    @SubscribeEvent
    public void playerFall(LivingFallEvent event) {
        EntityPlayer player;
        int featherFallLevel;
        if (event.entity instanceof EntityPlayer && (double)event.distance < 3.0 + (double)(featherFallLevel = Helper.getPlayerStatLevel(player = (EntityPlayer)event.entity, Stat.STAT_FEATHER_FALL)) * 0.1) {
            event.distance = 0.0f;
        }
    }

    @SubscribeEvent
    public void playerDead(LivingDeathEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.entityLiving;
            if (Stat.loseStatsOnDeath) {
                for (int stat = 0; stat < Stat.totalStats; ++stat) {
                    Helper.setPlayerStatLevel(player, Stat.stats.get(stat), 0);
                }
            }
        }
    }

    @SubscribeEvent
    public void playerJump(LivingEvent.LivingJumpEvent event) {
        EntityPlayer player;
        if (event.entity instanceof EntityPlayer && (player = (EntityPlayer)event.entity).isSprinting()) {
            player.motionY *= (double)(1.0f + Stat.STAT_LEAPERV.getBonus(player));
            player.motionX *= (double)(1.0f + Stat.STAT_LEAPERH.getBonus(player));
            player.motionZ *= (double)(1.0f + Stat.STAT_LEAPERH.getBonus(player));
        }
    }

    @SubscribeEvent
    public void playerBreakSpeed(PlayerEvent.BreakSpeed event) {
        ItemStack heldItem = event.entityPlayer.getHeldItem();
        EntityPlayer player = event.entityPlayer;
        Block block = event.block;
        int metadata = event.metadata;
        float multiplier = 1.0f + ((float)Stat.STAT_MINING.isAffectedByStat(heldItem, block, metadata) * Stat.STAT_MINING.getBonus(player) + (float)Stat.STAT_DIGGING.isAffectedByStat(heldItem, block, metadata) * Stat.STAT_DIGGING.getBonus(player) + (float)Stat.STAT_CHOPPING.isAffectedByStat(heldItem, block, metadata) * Stat.STAT_CHOPPING.getBonus(player) + (float)Stat.STAT_TRIMMING.isAffectedByStat(heldItem, block, metadata) * Stat.STAT_TRIMMING.getBonus(player));
        event.newSpeed = event.originalSpeed * multiplier;
    }

    @SubscribeEvent
    public void entityHurt(LivingHurtEvent event) {
        DamageSource source = event.source;
        if (event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.entity;
            float damageMultiplier = 1.0f - (Stat.STAT_PROTECTION.getAppliedBonus(player, source) + Stat.STAT_TOUGH_SKIN.getAppliedBonus(player, source) + Stat.STAT_FEATHER_FALL.getAppliedBonus(player, source) + Stat.STAT_TEMPERING.getAppliedBonus(player, source));
            event.ammount *= damageMultiplier;
        } else if (event.source.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.source.getEntity();
            ItemStack heldItem = player.getHeldItem();
            Entity target = event.entity;
            float damage = event.ammount;
            float bonus = 0.0f;
            if (player.getHeldItem() != null) {
                bonus = Math.round(damage * (Stat.STAT_SWORDSMANSHIP.getAppliedBonus(player, heldItem) + Stat.STAT_BOWMANSHIP.getAppliedBonus(player, heldItem)));
            }
            event.ammount = bonus + damage;
            if (Stat.STAT_REAPER.isAffectedByStat(target) != 0) {
                float reap = Stat.STAT_REAPER.getBonus(player);
                float reapBonus = reap * Stat.STAT_STEALTH.getSecondaryBonus(player) / 100.0f * (float)Stat.STAT_STEALTH.isAffectedByStat(player);
                float reapChance = reap + reapBonus;
                if (player.getRNG().nextFloat() <= reapChance) {
                    player.onEnchantmentCritical(target);
                    player.worldObj.playSoundAtEntity((Entity)player, "gokiStats:reaper", 1.0f, 1.0f);
                    event.ammount = 100000.0f;
                }
            }
        }
    }
}

