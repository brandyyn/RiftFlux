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

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.BlockEvent;
import zelda.Config;
import zelda.ExtendedPlayerProperties;
import zelda.items.ZItems;
import zelda.proxy.CommonProxy;

public class ZEventHandler {
    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayer && ExtendedPlayerProperties.get((EntityPlayer)event.entity) == null) {
            ExtendedPlayerProperties.register((EntityPlayer)event.entity);
        }
        if (event.entity instanceof EntityPlayer && event.entity.getExtendedProperties("ExtendedPlayer") == null) {
            event.entity.registerExtendedProperties("ExtendedPlayer", (IExtendedEntityProperties)new ExtendedPlayerProperties((EntityPlayer)event.entity));
        }
    }

    @SubscribeEvent
    public void onLivingDeathEvent(LivingDeathEvent event) {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer) {
            NBTTagCompound playerData = new NBTTagCompound();
            ((ExtendedPlayerProperties)event.entity.getExtendedProperties("ExtendedPlayer")).saveNBTData(playerData);
            CommonProxy.storeEntityData(((EntityPlayer)event.entity).getUniqueID().toString(), playerData);
        }
        Random rand = new Random();
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            if (event.entity instanceof EntityDragon) {
                event.entity.entityDropItem(new ItemStack(ZItems.heartContainer), 0.0f);
            } else if (event.entity instanceof EntityWither) {
                event.entity.entityDropItem(new ItemStack(ZItems.heartContainer), 0.0f);
            } else if (event.entity instanceof EntityMob && Config.MOB_DROP > 0 && rand.nextInt(Config.MOB_DROP) == 0) {
                event.entity.entityDropItem(new ItemStack(ZItems.heart), 0.0f);
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayer) {
            NBTTagCompound playerData = CommonProxy.getEntityData(((EntityPlayer)event.entity).getUniqueID().toString());
            if (playerData != null) {
                ((ExtendedPlayerProperties)event.entity.getExtendedProperties("ExtendedPlayer")).loadNBTData(playerData);
            }
            ExtendedPlayerProperties props = ExtendedPlayerProperties.get((EntityPlayer)event.entity);
            if (event.entity instanceof EntityPlayer && props.isFresh()) {
                props.setBaseHeartsMax();
            }
        }
    }

    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event) {
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
    public void onBlockBreak(BlockEvent.HarvestDropsEvent event) {
        Random rand = new Random();
        if (event.block == Blocks.tallgrass && Config.BLOCK_DROP > 0 && rand.nextInt(Config.BLOCK_DROP) == 0) {
            event.drops.add(new ItemStack(ZItems.heart));
        }
    }
}

