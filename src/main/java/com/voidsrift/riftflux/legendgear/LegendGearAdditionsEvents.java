package com.voidsrift.riftflux.legendgear;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.entity.EntitySpellEffect;

public class LegendGearAdditionsEvents {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void phoenixFeatherRevive(LivingDeathEvent event) {
        if (!LegendGear2.CONFIG_PHOENIX_FEATHER_REVIVE_ENABLED
                || !(event.entityLiving instanceof EntityPlayer)
                || event.entityLiving.worldObj.isRemote) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.entityLiving;
        if (!consumeFirst(player, LegendGear2.phoenixFeather)) {
            return;
        }

        player.setHealth(1.0F);
        player.hurtResistantTime = 65;
        LegendGear2.addConfiguredPotionEffect(player, LegendGear2.CONFIG_PHOENIX_REVIVE_REGENERATION_POTION_ID, Potion.regeneration, 28, 3, false);
        LegendGear2.addConfiguredPotionEffect(player, LegendGear2.CONFIG_PHOENIX_REVIVE_RESISTANCE_POTION_ID, Potion.resistance, 65, 4, false);
        LegendGear2.addConfiguredPotionEffect(player, LegendGear2.CONFIG_PHOENIX_REVIVE_FIRE_RESISTANCE_POTION_ID, Potion.fireResistance, 65, 1, false);
        player.removePotionEffect(Potion.poison.id);
        if (Potion.wither != null) {
            player.removePotionEffect(Potion.wither.id);
        }
        player.setFire(3);
        player.worldObj.playSoundAtEntity(player, "legendgear:revive", 1.0F, 1.0F);
        player.worldObj.playSoundAtEntity(player, "legendgear:feather", 0.8F, 1.0F);
        player.worldObj.spawnEntityInWorld(new EntitySpellEffect(player.worldObj, EntitySpellEffect.SpellType.Fire1, player,
                Vec3.createVectorHelper(player.posX, player.posY + 1.0D, player.posZ), 2.0D, 10.0D, false));
        event.setCanceled(true);
    }

    private boolean consumeFirst(EntityPlayer player, Item item) {
        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack != null && stack.getItem() == item) {
                if (!player.capabilities.isCreativeMode && --stack.stackSize <= 0) {
                    player.inventory.setInventorySlotContents(i, null);
                }
                player.inventory.markDirty();
                return true;
            }
        }
        return false;
    }
}
