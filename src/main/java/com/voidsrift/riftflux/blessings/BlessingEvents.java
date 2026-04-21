package com.voidsrift.riftflux.blessings;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemPotion;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import com.voidsrift.riftflux.net.MsgSyncBlessing;
import com.voidsrift.riftflux.net.RFNetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class BlessingEvents {
    private static final ThreadLocal<Boolean> APPLYING_NINJA_INVISIBILITY = new ThreadLocal<Boolean>();
    private static final String NBT_PENDING_SYNC = "BlessingPendingSync";
    private static final String NBT_PENDING_SYNC_DELAY = "BlessingPendingSyncDelay";
    private static final Random RNG = new Random();
    private static final UUID SCOUT_SPEED_UUID = UUID.fromString("f1d7e0c2-7d02-4e2c-9df5-77bb2f4b4c25");
    private static final UUID NINJA_DAMAGE_UUID = UUID.fromString("7f9e0a13-9e3b-4a5f-8a87-1d0c0b8a4f3f");
    private static final UUID WARRIOR_DAMAGE_UUID = UUID.fromString("8f0d3c52-4f65-4bf1-9c47-0c1c47f0c2b6");
    private static final UUID LUMBERJACK_DAMAGE_UUID = UUID.fromString("a7b9f44f-5c2d-4c08-8f7a-1c6a1bd9f302");
    private static final UUID BERSERKER_DAMAGE_UUID = UUID.fromString("f4c0d4a2-2c62-4a0b-8f15-2b5de9b3dd5e");
    private static final UUID BERSERKER_SPEED_UUID = UUID.fromString("c5d6c8b6-7f0d-4bb9-9e26-8f1b9c6b8e51");
    private static final UUID BERSERKER_HEALTH_UUID = UUID.fromString("d1aa6c2b-5f28-4b18-8b3f-7f3c5b7d9a0d");
    private static final UUID INFERNO_DAMAGE_UUID = UUID.fromString("c0a3b9df-7e8c-4b77-81e3-4f3a7f59b2a1");
    private static final UUID ROGUE_DAMAGE_UUID = UUID.fromString("5d6b2271-0e0d-4ac7-9a56-1e38a4a13a4d");
    private static final UUID DRUNK_DAMAGE_UUID = UUID.fromString("9b7b0a6f-2f8a-4a39-8b83-64d5f5642a6b");
    private static final List<ThiefDrop> THIEF_DROPS = new ArrayList<ThiefDrop>();
    private static int thiefDropsHash;

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event) {
        if (!ModConfig.blessingsEnabled) {
            return;
        }
        EntityPlayer player = event.player;
        if (player == null || player.worldObj == null || player.worldObj.isRemote) {
            return;
        }
        if (!BlessingHelper.hasBlessing(player) && ModConfig.blessingsGrantOnFirstJoin) {
            String blessing = BlessingHelper.getRandomBlessing(RNG, ModConfig.blessingsAllowInfernoOnFirstJoin);
            if (blessing != null) {
                BlessingHelper.setBlessing(player, blessing);
                BlessingHelper.resetBlessingState(player);
                BlessingHelper.clearBlessingSource(player);
            }
        }
        String existing = BlessingHelper.getBlessing(player);
        if (existing != null && !BlessingHelper.isBlessingEnabled(existing)) {
            BlessingHelper.clearBlessing(player);
            BlessingHelper.clearBlessingSource(player);
            BlessingHelper.resetBlessingState(player);
        }
        if (ModConfig.loseBlessingOnArtifactBreak && BlessingHelper.hasBlessingSource(player)) {
            int x = player.getEntityData().getInteger(BlessingHelper.NBT_BLESSING_PILLAR_X);
            int y = player.getEntityData().getInteger(BlessingHelper.NBT_BLESSING_PILLAR_Y);
            int z = player.getEntityData().getInteger(BlessingHelper.NBT_BLESSING_PILLAR_Z);
            int dim = player.getEntityData().getInteger(BlessingHelper.NBT_BLESSING_PILLAR_DIM);
            if (BlessingPillarData.isBroken(player.worldObj, x, y, z, dim)) {
                BlessingHelper.clearBlessing(player);
                BlessingHelper.clearBlessingSource(player);
                BlessingHelper.resetBlessingState(player);
                BlessingLossNotifier.queue(player, BlessingLossNotifier.OFFLINE_BREAK_DELAY_TICKS);
                if (player instanceof EntityPlayerMP && RFNetwork.CH != null) {
                    RFNetwork.CH.sendTo(new MsgSyncBlessing(player), (EntityPlayerMP) player);
                }
            }
        }
        BlessingHelper.ensureBlessingState(player);
        BlessingHelper.ensurePersistedBlessing(player);
        syncBlessing(player);
        markPendingSync(player, 15);
        updateBlessingLight(player);
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerLoggedOutEvent event) {
        if (!ModConfig.blessingsEnabled) {
            return;
        }
        EntityPlayer player = event.player;
        if (player == null || player.worldObj == null || player.worldObj.isRemote) {
            return;
        }
        // Keep artifact activation state visible for other players even when the owner logs out.
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!ModConfig.blessingsEnabled) {
            return;
        }
        EntityPlayer original = event.original;
        EntityPlayer player = event.entityPlayer;
        if (original == null || player == null) {
            return;
        }
        boolean keepBlessing = !event.wasDeath || !ModConfig.loseBlessingOnDeath;
        if (keepBlessing) {
            String blessing = BlessingHelper.getBlessing(original);
            if (blessing == null) {
                blessing = BlessingHelper.getPersistedBlessing(original);
            }
            if (blessing == null) {
                if (original.getEntityData().hasKey(BlessingHelper.NBT_BLESSING)) {
                    blessing = original.getEntityData().getString(BlessingHelper.NBT_BLESSING);
                }
            }
            if (blessing != null && !blessing.isEmpty()) {
                BlessingHelper.setBlessing(player, blessing);
            }
            if (original.getEntityData().hasKey(BlessingHelper.NBT_BLESSING_ACTIVE)) {
                player.getEntityData().setBoolean(BlessingHelper.NBT_BLESSING_ACTIVE,
                        original.getEntityData().getBoolean(BlessingHelper.NBT_BLESSING_ACTIVE));
            }
            if (original.getEntityData().hasKey(BlessingHelper.NBT_BLESSING_COOLDOWN)) {
                player.getEntityData().setInteger(BlessingHelper.NBT_BLESSING_COOLDOWN,
                        original.getEntityData().getInteger(BlessingHelper.NBT_BLESSING_COOLDOWN));
            }
            if (original.getEntityData().hasKey(BlessingHelper.NBT_BLESSING_COUNTER)) {
                player.getEntityData().setInteger(BlessingHelper.NBT_BLESSING_COUNTER,
                        original.getEntityData().getInteger(BlessingHelper.NBT_BLESSING_COUNTER));
            }
            if (original.getEntityData().hasKey(BlessingHelper.NBT_BLESSING_TIMER)) {
                player.getEntityData().setInteger(BlessingHelper.NBT_BLESSING_TIMER,
                        original.getEntityData().getInteger(BlessingHelper.NBT_BLESSING_TIMER));
            }
            if (original.getEntityData().hasKey(BlessingHelper.NBT_NINJA_INVIS_COOLDOWN)) {
                player.getEntityData().setInteger(BlessingHelper.NBT_NINJA_INVIS_COOLDOWN,
                        original.getEntityData().getInteger(BlessingHelper.NBT_NINJA_INVIS_COOLDOWN));
            }
            if (BlessingHelper.hasBlessingSource(original)) {
                int x = original.getEntityData().getInteger(BlessingHelper.NBT_BLESSING_PILLAR_X);
                int y = original.getEntityData().getInteger(BlessingHelper.NBT_BLESSING_PILLAR_Y);
                int z = original.getEntityData().getInteger(BlessingHelper.NBT_BLESSING_PILLAR_Z);
                int dim = original.getEntityData().getInteger(BlessingHelper.NBT_BLESSING_PILLAR_DIM);
                BlessingHelper.setBlessingSource(player, x, y, z, dim);
            } else {
                BlessingHelper.clearBlessingSource(player);
            }
        } else {
            BlessingHelper.clearBlessing(player);
            BlessingHelper.clearBlessingSource(player);
            BlessingHelper.resetBlessingState(player);
        }
        if (!player.worldObj.isRemote) {
            syncBlessing(player);
            markPendingSync(player, 15);
        }
        updateBlessingLight(player);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!ModConfig.blessingsEnabled) {
            return;
        }
        if (ModConfig.loseBlessingOnDeath) {
            return;
        }
        EntityPlayer player = event.player;
        if (player == null || player.worldObj == null || player.worldObj.isRemote) {
            return;
        }
        if (!BlessingHelper.hasBlessing(player)) {
            String blessing = BlessingHelper.getPersistedBlessing(player);
            if (blessing == null || blessing.isEmpty()) {
                return;
            }
            BlessingHelper.setBlessing(player, blessing);
            BlessingHelper.ensureBlessingState(player);
        }
        if (player instanceof EntityPlayerMP && RFNetwork.CH != null) {
            RFNetwork.CH.sendTo(new MsgSyncBlessing(player), (EntityPlayerMP) player);
        }
        markPendingSync(player, 15);
        updateBlessingLight(player);
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (!ModConfig.blessingsEnabled) {
            return;
        }
        EntityPlayer player = event.entityPlayer;
        if (player == null || player.worldObj == null) {
            return;
        }
        String blessing = getActiveBlessing(player);
        if (blessing == null) {
            return;
        }
        ItemStack held = player.getHeldItem();
        int meta = event.metadata;
        if ("Miner".equals(blessing)) {
            String tool = event.block.getHarvestTool(meta);
            if ("pickaxe".equals(tool)) {
                if (held != null && held.getItem() instanceof ItemPickaxe) {
                    event.newSpeed = event.originalSpeed * 1.25f;
                }
            } else {
                event.newSpeed = event.originalSpeed * 0.6f;
            }
        } else if ("Lumberjack".equals(blessing)) {
            if (held != null && held.getItem() instanceof ItemAxe) {
                event.newSpeed = event.originalSpeed * 1.25f;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingAttack(LivingAttackEvent event) {
        if (!ModConfig.blessingsEnabled) {
            return;
        }
        if (event.entityLiving == null || event.entityLiving.worldObj == null || event.entityLiving.worldObj.isRemote) {
            return;
        }
        if (!(event.entityLiving instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        String blessing = getActiveBlessing(player);
        if (blessing == null) {
            return;
        }
        if ("Inferno".equals(blessing) && event.source != null && event.source.isFireDamage()) {
            event.setCanceled(true);
            return;
        }
        if ("Paratrooper".equals(blessing) && isFallDamage(event.source)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingFall(LivingFallEvent event) {
        if (!ModConfig.blessingsEnabled) {
            return;
        }
        if (event.entityLiving == null || event.entityLiving.worldObj == null || event.entityLiving.worldObj.isRemote) {
            return;
        }
        if (!(event.entityLiving instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        String blessing = getActiveBlessing(player);
        if (!"Paratrooper".equals(blessing)) {
            return;
        }
        event.distance = 0.0f;
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingHurt(LivingHurtEvent event) {
        if (!ModConfig.blessingsEnabled) {
            return;
        }
        if (event.entityLiving == null || event.entityLiving.worldObj == null || event.entityLiving.worldObj.isRemote) {
            return;
        }
        if (event.ammount <= 0.0f) {
            return;
        }

        EntityLivingBase victim = event.entityLiving;
        if (victim instanceof EntityPlayer) {
            applyIncoming((EntityPlayer) victim, event);
        }

        Entity source = event.source == null ? null : event.source.getEntity();
        if (source instanceof EntityPlayer) {
            applyOutgoing((EntityPlayer) source, victim, event);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingDeath(LivingDeathEvent event) {
        if (!ModConfig.blessingsEnabled) {
            return;
        }
        if (event.entity == null || event.entity.worldObj == null || event.entity.worldObj.isRemote) {
            return;
        }
        if (!ModConfig.loseBlessingOnDeath && event.entity instanceof EntityPlayer) {
            BlessingHelper.ensurePersistedBlessing((EntityPlayer) event.entity);
        }
        if (ModConfig.loseBlessingOnDeath && event.entity instanceof EntityPlayer) {
            EntityPlayer dead = (EntityPlayer) event.entity;
            if (BlessingHelper.hasBlessing(dead)) {
                BlessingHelper.clearBlessing(dead);
                BlessingHelper.clearBlessingSource(dead);
                BlessingHelper.resetBlessingState(dead);
                if (dead instanceof EntityPlayerMP && RFNetwork.CH != null) {
                    RFNetwork.CH.sendTo(new MsgSyncBlessing(dead), (EntityPlayerMP) dead);
                }
            }
        }
        Entity source = event.source == null ? null : event.source.getEntity();
        if (!(source instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) source;
        String blessing = getActiveBlessing(player);
        if (blessing == null) {
            return;
        }
        if ("Thief".equals(blessing) && event.entity.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
            for (ThiefDrop drop : getThiefDrops()) {
                if (drop == null) {
                    continue;
                }
                if (RNG.nextFloat() <= drop.chance) {
                    event.entity.entityDropItem(new ItemStack(drop.item, 1, drop.meta), 0.0f);
                }
            }
        }
        if ("Berserker".equals(blessing)) {
            int counter = BlessingHelper.getCounter(player);
            BlessingHelper.setCounter(player, Math.min(10, counter + 1));
        }
    }

    @SubscribeEvent
    public void onPotionFinish(PlayerUseItemEvent.Finish event) {
        if (!ModConfig.blessingsEnabled) {
            return;
        }
        if (event.entityPlayer == null || event.entityPlayer.worldObj == null || event.entityPlayer.worldObj.isRemote) {
            return;
        }
        EntityPlayer player = event.entityPlayer;
        String blessing = getActiveBlessing(player);
        if (!"Alchemist".equals(blessing)) {
            return;
        }
        ItemStack stack = event.item;
        if (stack == null || !(stack.getItem() instanceof ItemPotion)) {
            return;
        }
        if (ItemPotion.isSplash(stack.getItemDamage())) {
            return;
        }
        Potion extra = getRandomPotionFromIds(ModConfig.blessingAlchemistPotionIds);
        if (extra == null) {
            return;
        }
        int duration = extra.isInstant() ? 1 : 20 * 45;
        player.addPotionEffect(new PotionEffect(extra.id, duration, 0, false));
    }

    @SubscribeEvent
    public void onPickupXp(PlayerPickupXpEvent event) {
        if (!ModConfig.blessingsEnabled) {
            return;
        }
        if (event.entityPlayer == null || event.entityPlayer.worldObj == null || event.entityPlayer.worldObj.isRemote) {
            return;
        }
        if (event.orb == null) {
            return;
        }
        String blessing = getActiveBlessing(event.entityPlayer);
        if (!"Gambler".equals(blessing)) {
            return;
        }
        int base = Math.max(0, event.orb.xpValue);
        int bonus = Math.round(base * 0.6f);
        event.orb.xpValue = base + Math.max(0, bonus);
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (!ModConfig.blessingsEnabled) {
            return;
        }
        EntityPlayer player = event.entityPlayer;
        if (player == null || player.worldObj == null || player.worldObj.isRemote) {
            return;
        }
        String blessing = getActiveBlessing(player);
        if (!"Ninja".equals(blessing)) {
            clearNinjaDamageBoost(player);
            player.getEntityData().removeTag(BlessingHelper.NBT_NINJA_DAMAGE_BOOST_TICK);
            return;
        }
        clearNinjaDamageBoost(player);
        if (!(event.target instanceof EntityLivingBase)
                || !BlessingCombatHelper.shouldApplyNinjaFirstStrike(player, (EntityLivingBase) event.target)) {
            player.getEntityData().removeTag(BlessingHelper.NBT_NINJA_DAMAGE_BOOST_TICK);
            return;
        }
        player.getEntityData().setInteger(BlessingHelper.NBT_NINJA_DAMAGE_BOOST_TICK, player.ticksExisted);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        EntityPlayer player = event.player;
        if (player == null || player.worldObj == null || player.worldObj.isRemote) {
            return;
        }
        tryPendingSync(player);
        BlessingLossNotifier.tick(player);
        if (!ModConfig.blessingsEnabled) {
            applyScoutSpeed(player, false);
            clearNinjaDamageBoost(player);
            clearDamageModifiers(player);
            clearBerserkerStats(player);
            return;
        }

        String blessing = getActiveBlessing(player);
        if (blessing == null) {
            applyScoutSpeed(player, false);
            clearNinjaDamageBoost(player);
            clearDamageModifiers(player);
            clearBerserkerStats(player);
            return;
        }

        boolean active = BlessingHelper.isActive(player);
        int cooldown = BlessingHelper.getCooldown(player);
        int counter = BlessingHelper.getCounter(player);
        int timer = BlessingHelper.getTimer(player);
        int ninjaCooldown = player.getEntityData().getInteger(BlessingHelper.NBT_NINJA_INVIS_COOLDOWN);

        if (cooldown > 0) {
            cooldown--;
        }
        if (ninjaCooldown > 0) {
            ninjaCooldown--;
        }

        timer = active ? timer + 1 : 0;

        if ("Berserker".equals(blessing) && active && timer % 40 == 0) {
            counter = Math.max(0, counter - 1);
            if (counter == 0) {
                active = false;
                cooldown = 1200;
                timer = 0;
                player.addChatComponentMessage(new ChatComponentText("You calm down."));
            }
        }

        if ("Ninja".equals(blessing) && player.isSneaking() && ninjaCooldown <= 0) {
            PotionEffect effect = player.getActivePotionEffect(Potion.invisibility);
            if (effect == null || effect.getDuration() < 10) {
                addNinjaInvisibility(player);
            }
        } else if ("Diver".equals(blessing)) {
            player.setAir(300);
        }

        applyScoutSpeed(player, "Scout".equals(blessing));
        updateDamageModifiers(player, blessing, active);
        updateBerserkerStats(player, blessing, active);
        if (!"Ninja".equals(blessing)) {
            clearNinjaDamageBoost(player);
            player.getEntityData().removeTag(BlessingHelper.NBT_NINJA_DAMAGE_BOOST_TICK);
        } else {
            int boostTick = player.getEntityData().getInteger(BlessingHelper.NBT_NINJA_DAMAGE_BOOST_TICK);
            if (boostTick > 0 && player.ticksExisted > boostTick) {
                clearNinjaDamageBoost(player);
                player.getEntityData().removeTag(BlessingHelper.NBT_NINJA_DAMAGE_BOOST_TICK);
            }
        }

        BlessingHelper.setActive(player, active);
        BlessingHelper.setCooldown(player, cooldown);
        BlessingHelper.setCounter(player, counter);
        BlessingHelper.setTimer(player, timer);
        player.getEntityData().setInteger(BlessingHelper.NBT_NINJA_INVIS_COOLDOWN, Math.max(0, ninjaCooldown));
    }

    private void applyIncoming(EntityPlayer player, LivingHurtEvent event) {
        String blessing = getActiveBlessing(player);
        if (blessing == null) {
            return;
        }
        if ("Ninja".equals(blessing)) {
            triggerNinjaCooldown(player);
        }
        if ("Inferno".equals(blessing) && event.source != null && event.source.isFireDamage()) {
            event.setCanceled(true);
            return;
        }
        if ("Paratrooper".equals(blessing) && isFallDamage(event.source)) {
            event.setCanceled(true);
            return;
        }
        if ("Scout".equals(blessing) && isFallDamage(event.source)) {
            event.ammount *= 2.0f;
        }
        if ("Guardian".equals(blessing)) {
            event.ammount *= 0.8f;
        }
        if ("Gambler".equals(blessing)) {
            event.ammount *= 1.2f;
        }
        if ("Berserker".equals(blessing) && BlessingHelper.isActive(player)) {
            event.ammount *= 0.67f;
        }
        if ("Mechanic".equals(blessing) && event.source != null && event.source.isExplosion()) {
            event.ammount *= 0.75f;
        }
        if ("Vampire".equals(blessing) && isInDirectSun(player)) {
            event.ammount *= 1.2f;
        }
        if ("Porcupine".equals(blessing)) {
            Entity attacker = event.source == null ? null : event.source.getEntity();
            if (attacker instanceof EntityLivingBase && attacker != player && event.source != null && !event.source.isProjectile()) {
                attacker.attackEntityFrom(DamageSource.causeThornsDamage(player), 2.0f);
            }
        }
        if ("Drunk".equals(blessing) && event.ammount > 0.0f) {
            applyDrunkEffect(player);
        }
    }

    private void applyOutgoing(EntityPlayer attacker, EntityLivingBase victim, LivingHurtEvent event) {
        String blessing = getActiveBlessing(attacker);
        if (blessing == null) {
            return;
        }
        boolean directMelee = BlessingCombatHelper.isDirectPlayerMeleeSource(attacker, event.source);
        if (!directMelee) {
            float blessingMultiplier = BlessingCombatHelper.getIndirectDamageMultiplier(attacker, victim, event.source);
            if (blessingMultiplier != 1.0F) {
                event.ammount *= blessingMultiplier;
            }
        }

        switch (blessing) {
            case "Swamp":
                if (victim != null) {
                    victim.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 80, 1));
                }
                break;
            case "Ninja":
                if (!directMelee && attacker.isSneaking() && ensureNinjaInvisibility(attacker)
                        && victim != null && victim.getHealth() >= victim.getMaxHealth()) {
                    event.ammount *= 2.0f;
                }
                triggerNinjaCooldown(attacker);
                break;
            default:
                break;
        }

        if ("Vampire".equals(blessing) && event.ammount > 0.0f) {
            attacker.heal(event.ammount * (ModConfig.blessingVampireHealPercent / 100.0f));
        }
    }

    private boolean isFallDamage(DamageSource source) {
        if (source == null) {
            return false;
        }
        if (source == DamageSource.fall) {
            return true;
        }
        return "fall".equals(source.damageType);
    }

    private void triggerNinjaCooldown(EntityPlayer player) {
        if (player == null) {
            return;
        }
        String blessing = getActiveBlessing(player);
        if (!"Ninja".equals(blessing)) {
            return;
        }
        int cooldownTicks = Math.max(0, ModConfig.blessingNinjaInvisCooldownSeconds * 20);
        player.getEntityData().setInteger(BlessingHelper.NBT_NINJA_INVIS_COOLDOWN, cooldownTicks);
        if (player.isPotionActive(Potion.invisibility)) {
            player.removePotionEffect(Potion.invisibility.id);
        }
    }

    private void applyScoutSpeed(EntityPlayer player, boolean enable) {
        IAttributeInstance attr = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        if (attr == null) {
            return;
        }
        AttributeModifier mod = attr.getModifier(SCOUT_SPEED_UUID);
        if (enable) {
            if (mod == null) {
                attr.applyModifier(new AttributeModifier(SCOUT_SPEED_UUID, "BlessingScoutSpeed", 0.2D, 2).setSaved(false));
            }
        } else if (mod != null) {
            attr.removeModifier(mod);
        }
    }

    private void applyNinjaDamageBoost(EntityPlayer player, boolean enable) {
        IAttributeInstance attr = player.getEntityAttribute(SharedMonsterAttributes.attackDamage);
        if (attr == null) {
            return;
        }
        AttributeModifier mod = attr.getModifier(NINJA_DAMAGE_UUID);
        if (enable) {
            if (mod == null) {
                attr.applyModifier(new AttributeModifier(NINJA_DAMAGE_UUID, "BlessingNinjaDamage", 1.0D, 2).setSaved(false));
            }
        } else if (mod != null) {
            attr.removeModifier(mod);
        }
    }

    private void clearNinjaDamageBoost(EntityPlayer player) {
        applyNinjaDamageBoost(player, false);
    }

    private boolean hasNinjaDamageBoost(EntityPlayer player) {
        IAttributeInstance attr = player.getEntityAttribute(SharedMonsterAttributes.attackDamage);
        return attr != null && attr.getModifier(NINJA_DAMAGE_UUID) != null;
    }

    private boolean ensureNinjaInvisibility(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        if (!player.isPotionActive(Potion.invisibility)) {
            int ninjaCooldown = player.getEntityData().getInteger(BlessingHelper.NBT_NINJA_INVIS_COOLDOWN);
            if (ninjaCooldown <= 0) {
                addNinjaInvisibility(player);
            }
        }
        return player.isPotionActive(Potion.invisibility);
    }

    public static boolean isApplyingNinjaInvisibility() {
        return Boolean.TRUE.equals(APPLYING_NINJA_INVISIBILITY.get());
    }

    private static void addNinjaInvisibility(EntityPlayer player) {
        if (player == null) {
            return;
        }
        APPLYING_NINJA_INVISIBILITY.set(Boolean.TRUE);
        try {
            player.addPotionEffect(new PotionEffect(Potion.invisibility.id, 10, 0, false));
        } finally {
            APPLYING_NINJA_INVISIBILITY.remove();
        }
    }

    private void updateDamageModifiers(EntityPlayer player, String blessing, boolean berserkerActive) {
        clearDamageModifiers(player);
    }

    private void clearDamageModifiers(EntityPlayer player) {
        IAttributeInstance attr = player.getEntityAttribute(SharedMonsterAttributes.attackDamage);
        if (attr == null) {
            return;
        }
        removeModifier(attr, WARRIOR_DAMAGE_UUID);
        removeModifier(attr, LUMBERJACK_DAMAGE_UUID);
        removeModifier(attr, BERSERKER_DAMAGE_UUID);
        removeModifier(attr, INFERNO_DAMAGE_UUID);
        removeModifier(attr, ROGUE_DAMAGE_UUID);
        removeModifier(attr, DRUNK_DAMAGE_UUID);
    }

    private void applyOrUpdateModifier(IAttributeInstance attr, UUID id, String name, double amount, boolean enable) {
        applyOrUpdateModifier(attr, id, name, amount, enable, 2);
    }

    private void applyOrUpdateModifier(IAttributeInstance attr, UUID id, String name, double amount, boolean enable, int operation) {
        AttributeModifier existing = attr.getModifier(id);
        if (!enable) {
            if (existing != null) {
                attr.removeModifier(existing);
            }
            return;
        }
        if (existing != null) {
            if (Double.compare(existing.getAmount(), amount) == 0 && existing.getOperation() == operation) {
                return;
            }
            attr.removeModifier(existing);
        }
        attr.applyModifier(new AttributeModifier(id, name, amount, operation).setSaved(false));
    }

    private void removeModifier(IAttributeInstance attr, UUID id) {
        AttributeModifier existing = attr.getModifier(id);
        if (existing != null) {
            attr.removeModifier(existing);
        }
    }

    private void updateBerserkerStats(EntityPlayer player, String blessing, boolean berserkerActive) {
        boolean active = "Berserker".equals(blessing) && berserkerActive;
        IAttributeInstance speed = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        if (speed != null) {
            applyOrUpdateModifier(speed, BERSERKER_SPEED_UUID, "BlessingBerserkerSpeed", 0.33D, active, 2);
        }
        IAttributeInstance health = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        if (health != null) {
            applyOrUpdateModifier(health, BERSERKER_HEALTH_UUID, "BlessingBerserkerHealth", 12.0D, active, 0);
        }
        if (!active && player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    private void clearBerserkerStats(EntityPlayer player) {
        IAttributeInstance speed = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        if (speed != null) {
            removeModifier(speed, BERSERKER_SPEED_UUID);
        }
        IAttributeInstance health = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        if (health != null) {
            removeModifier(health, BERSERKER_HEALTH_UUID);
        }
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    private void updateBlessingLight(EntityPlayer player) {
        updateBlessingLight(player, false);
    }

    private void updateBlessingLight(EntityPlayer player, boolean ignorePlayer) {
        if (player == null || !BlessingHelper.hasBlessingSource(player)) {
            return;
        }
        int x = player.getEntityData().getInteger(BlessingHelper.NBT_BLESSING_PILLAR_X);
        int y = player.getEntityData().getInteger(BlessingHelper.NBT_BLESSING_PILLAR_Y);
        int z = player.getEntityData().getInteger(BlessingHelper.NBT_BLESSING_PILLAR_Z);
        int dim = player.getEntityData().getInteger(BlessingHelper.NBT_BLESSING_PILLAR_DIM);
        net.minecraft.world.World world = player.worldObj;
        if (world == null) {
            return;
        }
        if (world.provider != null && world.provider.dimensionId != dim) {
            net.minecraft.server.MinecraftServer server = net.minecraft.server.MinecraftServer.getServer();
            if (server != null) {
                world = server.worldServerForDimension(dim);
            }
        }
        if (world != null) {
            if (ignorePlayer) {
                BlockBlessingPillar.updatePillarActive(world, x, y, z, dim, player);
            } else {
                BlockBlessingPillar.updatePillarActive(world, x, y, z, dim);
            }
            world.updateLightByType(net.minecraft.world.EnumSkyBlock.Block, x, y, z);
            world.updateLightByType(net.minecraft.world.EnumSkyBlock.Block, x, y + 1, z);
        }
    }

    private boolean isInDirectSun(EntityPlayer player) {
        if (player == null || player.worldObj == null) {
            return false;
        }
        if (!player.worldObj.isDaytime()) {
            return false;
        }
        int x = (int) Math.floor(player.posX);
        int y = (int) Math.floor(player.posY);
        int z = (int) Math.floor(player.posZ);
        return player.worldObj.canBlockSeeTheSky(x, y, z);
    }

    private void syncBlessing(EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP)) {
            return;
        }
        if (RFNetwork.CH == null) {
            return;
        }
        RFNetwork.CH.sendTo(new MsgSyncBlessing(player), (EntityPlayerMP) player);
    }

    private void markPendingSync(EntityPlayer player, int delayTicks) {
        if (player == null || player.worldObj == null || player.worldObj.isRemote) {
            return;
        }
        if (!(player instanceof EntityPlayerMP)) {
            return;
        }
        NBTTagCompound tag = player.getEntityData();
        tag.setBoolean(NBT_PENDING_SYNC, true);
        tag.setInteger(NBT_PENDING_SYNC_DELAY, Math.max(0, delayTicks));
    }

    private void tryPendingSync(EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP)) {
            return;
        }
        if (RFNetwork.CH == null) {
            return;
        }
        NBTTagCompound tag = player.getEntityData();
        if (!tag.getBoolean(NBT_PENDING_SYNC)) {
            return;
        }
        int delay = tag.getInteger(NBT_PENDING_SYNC_DELAY);
        if (delay > 0) {
            tag.setInteger(NBT_PENDING_SYNC_DELAY, delay - 1);
            return;
        }
        RFNetwork.CH.sendTo(new MsgSyncBlessing(player), (EntityPlayerMP) player);
        tag.removeTag(NBT_PENDING_SYNC);
        tag.removeTag(NBT_PENDING_SYNC_DELAY);
    }

    private String getActiveBlessing(EntityPlayer player) {
        if (player == null) {
            return null;
        }
        String blessing = BlessingHelper.getBlessing(player);
        if (blessing == null) {
            return null;
        }
        return BlessingHelper.isBlessingEnabled(blessing) ? blessing : null;
    }

    private static Potion getRandomPotionFromIds(int[] ids) {
        if (ids == null || ids.length == 0) {
            return null;
        }
        for (int i = 0; i < ids.length * 2; i++) {
            int id = ids[RNG.nextInt(ids.length)];
            if (id >= 0 && id < Potion.potionTypes.length) {
                Potion potion = Potion.potionTypes[id];
                if (potion != null) {
                    return potion;
                }
            }
        }
        return null;
    }

    private void applyDrunkEffect(EntityPlayer player) {
        Potion potion = getRandomPotionFromIds(ModConfig.blessingDrunkNegativePotionIds);
        if (potion == null) {
            return;
        }
        int duration = potion.isInstant() ? 1 : 20 * 10;
        player.addPotionEffect(new PotionEffect(potion.id, duration, 0, false));
    }

    private static List<ThiefDrop> getThiefDrops() {
        String[] entries = ModConfig.blessingThiefDropEntries;
        int hash = Arrays.hashCode(entries);
        if (hash == thiefDropsHash && !THIEF_DROPS.isEmpty()) {
            return THIEF_DROPS;
        }
        thiefDropsHash = hash;
        THIEF_DROPS.clear();
        if (entries == null) {
            return THIEF_DROPS;
        }
        for (String entry : entries) {
            parseThiefDropEntry(entry, THIEF_DROPS);
        }
        return THIEF_DROPS;
    }

    private static void parseThiefDropEntry(String entry, List<ThiefDrop> out) {
        if (entry == null) {
            return;
        }
        String trimmed = entry.trim();
        if (trimmed.isEmpty()) {
            return;
        }
        String itemPart = trimmed;
        String chancePart = null;
        int meta = 0;

        int pipe = itemPart.indexOf('|');
        if (pipe >= 0) {
            chancePart = itemPart.substring(pipe + 1).trim();
            itemPart = itemPart.substring(0, pipe).trim();
        }

        String[] commaParts = itemPart.split(",");
        if (commaParts.length >= 2) {
            itemPart = commaParts[0].trim();
            meta = parseIntSafe(commaParts[1].trim(), 0);
            if (commaParts.length >= 3 && (chancePart == null || chancePart.isEmpty())) {
                chancePart = commaParts[2].trim();
            }
        }

        int at = itemPart.indexOf('@');
        if (at >= 0) {
            meta = parseIntSafe(itemPart.substring(at + 1).trim(), meta);
            itemPart = itemPart.substring(0, at).trim();
        }

        Item item = findItem(itemPart);
        if (item == null) {
            return;
        }
        float chance = parseChance(chancePart, 0.05f);
        if (chance <= 0.0f) {
            return;
        }
        out.add(new ThiefDrop(item, meta, chance));
    }

    private static Item findItem(String id) {
        if (id == null) {
            return null;
        }
        String trimmed = id.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        int colon = trimmed.indexOf(':');
        if (colon <= 0 || colon >= trimmed.length() - 1) {
            return null;
        }
        String modid = trimmed.substring(0, colon);
        String name = trimmed.substring(colon + 1);
        Item item = GameRegistry.findItem(modid, name);
        if (item != null) {
            return item;
        }
        Block block = GameRegistry.findBlock(modid, name);
        if (block != null) {
            return Item.getItemFromBlock(block);
        }
        return null;
    }

    private static int parseIntSafe(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static float parseChance(String value, float fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        try {
            float chance = Float.parseFloat(value.trim());
            if (chance > 1.0f) {
                chance /= 100.0f;
            }
            if (chance < 0.0f) {
                chance = 0.0f;
            }
            if (chance > 1.0f) {
                chance = 1.0f;
            }
            return chance;
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static class ThiefDrop {
        private final Item item;
        private final int meta;
        private final float chance;

        private ThiefDrop(Item item, int meta, float chance) {
            this.item = item;
            this.meta = meta;
            this.chance = chance;
        }
    }
}
