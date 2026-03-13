/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.gameevent.PlayerEvent$PlayerRespawnEvent
 *  net.minecraft.block.Block
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.ai.attributes.IAttributeInstance
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.event.entity.living.LivingDropsEvent
 */
package tk.nukeduck.hearts.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import java.util.UUID;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import tk.nukeduck.hearts.HeartCrystal;

public class CommonEvents {
    public static final UUID BOOST_UUID = UUID.nameUUIDFromBytes(new byte[]{98, 117, 109});
    public static final String HEARTS_KEY = "HeartCrystals";

    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerRespawnEvent e) {
        NBTTagCompound persisted = e.player.getEntityData().getCompoundTag("PlayerPersisted");
        if (persisted.hasKey(HEARTS_KEY)) {
            double hearts = persisted.getDouble(HEARTS_KEY);
            this.onEat(e.player, hearts);
        }
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent e) {
        EntityPlayerMP player;
        AttributeModifier boost;
        if (e.entityLiving instanceof EntityPlayerMP && (boost = this.getBoost((EntityPlayer)(player = (EntityPlayerMP)e.entityLiving))) != null) {
            double totalKept = Math.max(0.0, boost.getAmount());
            if (!player.getEntityData().hasKey("PlayerPersisted")) {
                player.getEntityData().setTag("PlayerPersisted", (NBTBase)new NBTTagCompound());
            }
            NBTTagCompound persisted = player.getEntityData().getCompoundTag("PlayerPersisted");
            persisted.setDouble(HEARTS_KEY, totalKept);
        }
    }

    @SubscribeEvent
    public void onDrop(LivingDropsEvent e) {
        if (!(e.entityLiving instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer)e.entityLiving;
        this.clearBoost(player);
        if (!player.getEntityData().hasKey("PlayerPersisted")) {
            player.getEntityData().setTag("PlayerPersisted", (NBTBase)new NBTTagCompound());
        }
        NBTTagCompound persisted = player.getEntityData().getCompoundTag("PlayerPersisted");
        this.onEat(player, persisted.getDouble(HEARTS_KEY));
    }

    public void onEat(EntityPlayer player) {
        this.onEat(player, this.getBonusPerCrystal());
    }

    public void onEat(EntityPlayer player, double value) {
        IAttributeInstance attr = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        AttributeModifier modifier = attr.getModifier(BOOST_UUID);
        double current = 0.0;
        if (modifier != null) {
            current = modifier.getAmount();
            attr.removeModifier(modifier);
        }
        double next = current + value;
        if (next < 0.0) {
            next = 0.0;
        }
        double maxBonus = this.getMaxBonusHealth();
        if (next > maxBonus) {
            next = maxBonus;
        }
        if (next > 0.0) {
            AttributeModifier modifierNew = new AttributeModifier(BOOST_UUID, "Heart Crystal", next, 0);
            attr.applyModifier(modifierNew);
        }
    }

    public AttributeModifier getBoost(EntityPlayer player) {
        IAttributeInstance attr = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        return attr.getModifier(BOOST_UUID);
    }

    public void clearBoost(EntityPlayer player) {
        IAttributeInstance attr = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        AttributeModifier modifier = attr.getModifier(BOOST_UUID);
        if (modifier != null) {
            attr.removeModifier(modifier);
        }
    }

    private double getBonusPerCrystal() {
        return (double)Math.max(1, HeartCrystal.config.getHeartsPerCrystal()) * 2.0;
    }

    private double getMaxBonusHealth() {
        return (double)Math.max(0, HeartCrystal.config.getMaxHearts()) * 2.0;
    }
}
