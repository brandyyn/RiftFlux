/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.boss.EntityDragon
 *  net.minecraft.entity.boss.EntityWither
 *  net.minecraft.entity.monster.EntityMob
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraftforge.common.IExtendedEntityProperties
 *  net.minecraftforge.event.entity.EntityEvent$EntityConstructing
 *  net.minecraftforge.event.entity.EntityJoinWorldEvent
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.event.entity.player.EntityItemPickupEvent
 *  net.minecraftforge.event.world.BlockEvent$HarvestDropsEvent
 */
package zelda;

import com.voidsrift.riftflux.util.ConfigResolver;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import zelda.Config;
import zelda.ExtendedPlayerProperties;
import zelda.items.ZItems;
import zelda.proxy.CommonProxy;

public class ZEventHandler {
    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (!Config.HEARTS_ENABLED) {
            return;
        }
        if (event.entity instanceof EntityPlayer && ExtendedPlayerProperties.get((EntityPlayer)event.entity) == null) {
            ExtendedPlayerProperties.register((EntityPlayer)event.entity);
        }
    }

    @SubscribeEvent
    public void onLivingDeathEvent(LivingDeathEvent event) {
        if (!Config.HEARTS_ENABLED) {
            return;
        }
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer) {
            NBTTagCompound playerData = new NBTTagCompound();
            ExtendedPlayerProperties props = ExtendedPlayerProperties.get((EntityPlayer)event.entity);
            if (props != null) {
                props.saveNBTData(playerData);
                CommonProxy.storeEntityData(((EntityPlayer)event.entity).getUniqueID().toString(), playerData);
            }
        }
        Random rand = new Random();
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            if (this.shouldDropHeartContainer(event)) {
                event.entity.entityDropItem(new ItemStack(ZItems.heartContainer), 0.0f);
            } else if (event.entity instanceof EntityMob && Config.MOB_DROP > 0 && rand.nextInt(Config.MOB_DROP) == 0) {
                event.entity.entityDropItem(new ItemStack(ZItems.heart), 0.0f);
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!Config.HEARTS_ENABLED) {
            return;
        }
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer) {
            NBTTagCompound playerData = CommonProxy.getEntityData(((EntityPlayer)event.entity).getUniqueID().toString());
            ExtendedPlayerProperties props = ExtendedPlayerProperties.get((EntityPlayer)event.entity);
            if (props == null) {
                ExtendedPlayerProperties.register((EntityPlayer)event.entity);
                props = ExtendedPlayerProperties.get((EntityPlayer)event.entity);
            }
            if (playerData != null) {
                if (props != null) {
                    props.loadNBTData(playerData);
                }
            }
            if (playerData == null && props != null) {
                props.loadNBTData(event.entity.getEntityData());
            }
            if (event.entity instanceof EntityPlayer && props != null && props.isFresh()) {
                props.setBaseHeartsMax();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!Config.HEARTS_ENABLED) {
            return;
        }
        if (event.entityPlayer == null || event.original == null) {
            return;
        }
        ExtendedPlayerProperties originalProps = ExtendedPlayerProperties.get(event.original);
        if (originalProps == null) {
            return;
        }
        ExtendedPlayerProperties props = ExtendedPlayerProperties.get(event.entityPlayer);
        if (props == null) {
            ExtendedPlayerProperties.register(event.entityPlayer);
            props = ExtendedPlayerProperties.get(event.entityPlayer);
        }
        if (props != null) {
            props.setBaseHearts(originalProps.getMaxHearts());
            if (event.wasDeath) {
                props.setBaseHeartsMax();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent event) {
        if (!Config.HEARTS_ENABLED || event == null || event.player == null || event.player.worldObj == null || event.player.worldObj.isRemote) {
            return;
        }
        CommonProxy.clearEntityData(event.player.getUniqueID().toString());
    }

    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event) {
        if (!Config.HEARTS_ENABLED) {
            return;
        }
        ItemStack item = event.item.getEntityItem();
        if (item.getItem() == ZItems.heart) {
            EntityPlayer player = event.entityPlayer;
            if (!player.worldObj.isRemote) {
                player.heal((float)(item.stackSize * 4));
                player.worldObj.playSoundAtEntity((Entity)event.item, "random.pop", 0.3f, 1.0f);
                event.item.setDead();
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!Config.HEARTS_ENABLED) {
            return;
        }
        if (event.world == null || event.world.isRemote) {
            return;
        }
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR
                && event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        EntityPlayer player = event.entityPlayer;
        if (player == null) {
            return;
        }
        ItemStack held = player.getHeldItem();
        if (held == null || held.getItem() != ZItems.heartContainer) {
            return;
        }
        ExtendedPlayerProperties props = ExtendedPlayerProperties.get(player);
        if (props == null) {
            ExtendedPlayerProperties.register(player);
            props = ExtendedPlayerProperties.get(player);
        }
        int maxHearts = Math.max(Config.STARTING_HEARTS, Config.MAXIMUM_HEARTS);
        if (props != null && props.getMaxHearts() < (double)maxHearts) {
            props.addHeart();
            event.world.playSoundAtEntity((Entity)player, "random.levelup", 0.5f,
                    event.world.rand.nextFloat() * 0.1f + 0.9f);
            if (!player.capabilities.isCreativeMode) {
                --held.stackSize;
                if (held.stackSize <= 0) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }
            }
            player.inventory.markDirty();
        } else {
            player.addChatComponentMessage((net.minecraft.util.IChatComponent)
                    new net.minecraft.util.ChatComponentText("You are at the maximum heart capacity."));
        }
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.HarvestDropsEvent event) {
        if (!Config.HEARTS_ENABLED) {
            return;
        }
        Random rand = new Random();
        if (event.block == Blocks.tallgrass && Config.BLOCK_DROP > 0 && rand.nextInt(Config.BLOCK_DROP) == 0) {
            event.drops.add(new ItemStack(ZItems.heart));
        }
    }

    private boolean shouldDropHeartContainer(LivingDeathEvent event) {
        if (event == null || !ConfigResolver.matchesConfiguredEntity(event.entity, Config.HEART_CONTAINER_DROP_MOB_IDS)) {
            return false;
        }
        if (!Config.HEART_CONTAINER_FIRST_KILL_ONLY) {
            return true;
        }

        EntityPlayer killer = this.getKillingPlayer(event);
        if (killer == null) {
            return false;
        }

        ExtendedPlayerProperties props = ExtendedPlayerProperties.get(killer);
        if (props == null) {
            ExtendedPlayerProperties.register(killer);
            props = ExtendedPlayerProperties.get(killer);
        }
        if (props == null) {
            return false;
        }

        String mobId = ConfigResolver.getConfiguredEntityId(event.entity);
        if (mobId.isEmpty() || props.hasKilledHeartContainerMob(mobId)) {
            return false;
        }

        return props.addKilledHeartContainerMob(mobId);
    }

    private EntityPlayer getKillingPlayer(LivingDeathEvent event) {
        if (event == null || event.source == null) {
            return null;
        }
        Entity attacker = event.source.getEntity();
        return attacker instanceof EntityPlayer ? (EntityPlayer)attacker : null;
    }
}
