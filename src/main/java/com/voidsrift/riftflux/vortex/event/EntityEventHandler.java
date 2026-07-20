package com.voidsrift.riftflux.vortex.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent.Finish;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent.Tick;

import com.voidsrift.riftflux.vortex.entity.EntityDeathRune;
import com.voidsrift.riftflux.vortex.item.ModItems;
import com.voidsrift.riftflux.vortex.item.ItemButterflyKnife;
import com.voidsrift.riftflux.vortex.lib.container.InventoryBackpack;
import com.voidsrift.riftflux.vortex.lib.event.LivingDestroyArmorEvent;
import com.voidsrift.riftflux.vortex.lib.helper.ContainerHelper;
import com.voidsrift.riftflux.vortex.lib.helper.ItemHelper;
import com.voidsrift.riftflux.vortex.lib.helper.WorldHelper;
import com.voidsrift.riftflux.vortex.network.ModPackets;
import com.voidsrift.riftflux.vortex.network.PacketWorldDataSync;
import com.voidsrift.riftflux.ModConfig;

/**
 * Event handler ported from vortex.
 *
 * Nutrition functionality was intentionally removed.
 */
public class EntityEventHandler {
    private final Random rand = new Random();

    private static final float bandTrigger = 8.0F;
    private static final float bandSave = 1.0F;
    private static final String TAG_DROPPED_ON_BREAK = "rf_bp_dropped";
    private static final String TAG_RUNE_LAST_SAVE_TICK = "rf_thanatos_last_save_tick";
    private static final String TAG_RUNE_PROTECTED_UNTIL = "rf_thanatos_protected_until";

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingAttack(LivingAttackEvent event) {
        if (!ModConfig.butterflyKnifeFlickParry
                || !(event.entity instanceof EntityPlayer)
                || event.source.isUnblockable()) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.entity;
        ItemStack held = player.getCurrentEquippedItem();
        if (held == null
                || held.getItem() != ModItems.butterflyKnife
                || !ItemButterflyKnife.isFlickParryActive(held, player.worldObj)) {
            return;
        }

        Entity damagingEntity = event.source.getSourceOfDamage();
        if (damagingEntity != null) {
            if (event.source.isProjectile()) {
                damagingEntity.motionX = -damagingEntity.motionX / 1.5D;
                damagingEntity.motionY = -damagingEntity.motionY / 1.5D;
                damagingEntity.motionZ = -damagingEntity.motionZ / 1.5D;
                if (damagingEntity instanceof EntityArrow) {
                    ((EntityArrow) damagingEntity).shootingEntity = player;
                }
            } else {
                damagingEntity.motionX = -damagingEntity.motionX * 5.0D;
                damagingEntity.motionY = 0.42D;
                damagingEntity.motionZ = -damagingEntity.motionZ * 5.0D;
            }
        }

        player.worldObj.playSoundAtEntity(
                player,
                "random.anvil_land",
                0.6F,
                Math.max(1.4F - event.ammount / 10.0F, 0.4F));
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingHurt(LivingHurtEvent event) {
        if (!event.entity.worldObj.isRemote) {
            Entity target = event.entity;
            Entity attacker = event.source.getEntity();


            if (target instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) target;
                if (isRuneThanatosProtected(player)) {
                    event.setCanceled(true);
                    player.fallDistance = 0.0F;
                    return;
                }
                float currentHealth = player.getHealth();
                if (ItemHelper.hasBauble(player, ModItems.focusBand)
                        && currentHealth >= bandTrigger
                        && event.ammount >= currentHealth) {
                    event.ammount = currentHealth - bandSave;
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entityLiving;
            long worldTime = player.worldObj.getTotalWorldTime();
            if (isRuneThanatosProtected(player)) {
                event.setCanceled(true);
                if (player.getHealth() <= 0.0F) {
                    player.setHealth(1.0F);
                }
                return;
            }
            if (player.getEntityData().getLong(TAG_RUNE_LAST_SAVE_TICK) == worldTime) {
                event.setCanceled(true);
                if (player.getHealth() <= 0.0F) {
                    player.setHealth(1.0F);
                }
                return;
            }
            boolean consumed = player.inventory.consumeInventoryItem(ModItems.runeThanatos)
                    || ItemHelper.consumeBauble(player, ModItems.runeThanatos);
            if (consumed) {
                player.getEntityData().setLong(TAG_RUNE_LAST_SAVE_TICK, worldTime);
                player.getEntityData().setLong(TAG_RUNE_PROTECTED_UNTIL, worldTime + 60L);
                if (player.worldObj.getWorldInfo().isHardcoreModeEnabled()) {
                    WorldHelper.setPlayerHCRevive(player, true);
                    ModPackets.instance.sendTo(
                            new PacketWorldDataSync(WorldHelper.getGlobalCustomData(player.worldObj).getData()),
                            (EntityPlayerMP) player);
                }

                // Prevent death and 'consume' the lethal hit.
                event.setCanceled(true);
                player.setHealth(Math.max(1.0F, player.getMaxHealth() * 0.25F));
                player.extinguish();
                player.fallDistance = 0.0F;
                player.hurtResistantTime = 60;

                EntityDeathRune rune = new EntityDeathRune(player);
                rune.setPosition(player.posX, player.posY + 1.6D, player.posZ);
                player.worldObj.spawnEntityInWorld(rune);
            }
        }
    }

    private boolean isRuneThanatosProtected(EntityPlayer player) {
        return player != null
                && player.worldObj != null
                && player.getEntityData().getLong(TAG_RUNE_PROTECTED_UNTIL) > player.worldObj.getTotalWorldTime();
    }

    @SubscribeEvent
    public void onLivingDestroyArmor(LivingDestroyArmorEvent event) {
        EntityLivingBase wearer = event.entityLiving;
        ItemStack armor = event.armor;
        if (armor != null && armor.getItem() == ModItems.backpack && ModConfig.backpackDurability) {
            NBTTagCompound tag = armor.getTagCompound();
            if (tag != null && tag.getBoolean(TAG_DROPPED_ON_BREAK)) {
                return;
            }
            InventoryBackpack backpack = ContainerHelper.getBackpackInventory(armor);

            for (int i = 0; i < backpack.getSizeInventory(); ++i) {
                ItemStack itemStack = backpack.getStackInSlot(i);
                if (itemStack != null) {
                    wearer.entityDropItem(itemStack.copy(), 0.0F);
                    backpack.setInventorySlotContents(i, (ItemStack) null);
                }
            }
            if (tag == null) {
                tag = new NBTTagCompound();
                armor.setTagCompound(tag);
            }
            tag.setBoolean(TAG_DROPPED_ON_BREAK, true);
        }
    }

    @SubscribeEvent
    public void onPlayerDestroyItem(PlayerDestroyItemEvent event) {
        if (event == null || event.entityPlayer == null) return;
        ItemStack stack = event.original;
        if (stack != null && stack.getItem() == ModItems.backpack && ModConfig.backpackDurability) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag != null && tag.getBoolean(TAG_DROPPED_ON_BREAK)) {
                return;
            }
            InventoryBackpack backpack = ContainerHelper.getBackpackInventory(stack);
            for (int i = 0; i < backpack.getSizeInventory(); ++i) {
                ItemStack itemStack = backpack.getStackInSlot(i);
                if (itemStack != null) {
                    event.entityPlayer.entityDropItem(itemStack.copy(), 0.0F);
                    backpack.setInventorySlotContents(i, (ItemStack) null);
                }
            }
            if (tag == null) {
                tag = new NBTTagCompound();
                stack.setTagCompound(tag);
            }
            tag.setBoolean(TAG_DROPPED_ON_BREAK, true);
        }
    }


    @SubscribeEvent
    public void onPlayerDrops(PlayerDropsEvent event) {
        if (!event.entity.worldObj.isRemote && event.source.getEntity() instanceof EntityPlayer) {
            EntityPlayer attacker = (EntityPlayer) event.source.getEntity();
            EntityPlayer target = event.entityPlayer;
            if (ItemHelper.hasArmor(attacker, ModItems.highlandSpirit, 1)) {
                attacker.heal(20.0F);
                event.entity.worldObj.playSoundAtEntity(attacker, "mob.wither.shoot", 0.5F, 0.4F);
                int n = this.rand.nextInt(5);
                if (n == 0) {
                    ItemStack head = new ItemStack(Items.skull, 1, 3);
                    NBTTagCompound name = new NBTTagCompound();
                    name.setString("SkullOwner", target.getDisplayName());
                    head.setTagCompound(name);
                    EntityItem headDrop = new EntityItem(target.getEntityWorld(), target.posX, target.posY, target.posZ, head);
                    event.drops.add(headDrop);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        if (!event.player.worldObj.isRemote) {
            EntityPlayer player = event.player;
            ModPackets.instance.sendTo(
                    new PacketWorldDataSync(WorldHelper.getGlobalCustomData(player.worldObj).getData()),
                    (EntityPlayerMP) player);
        }
    }

    @SubscribeEvent
    public void onPlayerUseItemEventTick(Tick event) {
        EntityPlayer player = event.entityPlayer;
        ItemStack itemStack = event.item;
        if (ItemHelper.hasBauble(player, ModItems.gluttonyCharm)
                && (itemStack.getItemUseAction() == EnumAction.eat || itemStack.getItemUseAction() == EnumAction.drink)) {
            event.duration = 0;
        }
    }

    @SubscribeEvent
    public void onPlayerUseItemFinish(Finish event) {
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
    }
}
