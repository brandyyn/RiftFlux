package goki.stats.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import goki.stats.lib.Helper;
import goki.stats.lib.Reference;
import goki.stats.stats.Stat;
import java.util.UUID;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityFurnace;

public class TickHandler {
    public static final UUID knockbackResistanceID = UUID.randomUUID();
    public static final UUID stealthSpeedID = UUID.randomUUID();
    public static final UUID swimSpeedID = UUID.randomUUID();

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (player == null || player.worldObj == null) {
            return;
        }

        if (player.isInWater() && !Reference.isPlayerAPILoaded) {
            float multiplier = Math.max(0.0F, Stat.STAT_ATHLETICISM.getBonus(player));
            player.moveEntity(player.motionX * multiplier, player.motionY * multiplier, player.motionZ * multiplier);
        }

        if (player.isOnLadder() && !Reference.isPlayerAPILoaded && !player.isSneaking() && player.isCollidedHorizontally) {
            float multiplier = Stat.STAT_CLIMBING.getBonus(player);
            player.moveEntity(player.motionX, player.motionY * multiplier, player.motionZ);
        }

        updateModifier(
                player.getEntityAttribute(SharedMonsterAttributes.movementSpeed),
                stealthSpeedID,
                "SneakSpeed",
                Stat.STAT_STEALTH.getBonus(player) / 100.0F,
                1,
                player.isSneaking()
        );
        updateModifier(
                player.getEntityAttribute(SharedMonsterAttributes.knockbackResistance),
                knockbackResistanceID,
                "KnockbackResistance",
                Stat.STAT_STEADY_GUARD.getBonus(player),
                0,
                player.isBlocking()
        );

        if (Helper.getPlayerStatLevel(player, Stat.STAT_FURNACE_FINESSE) > 0) {
            boostNearbyFurnaces(player);
        }
    }

    private static void updateModifier(
            IAttributeInstance attribute,
            UUID id,
            String name,
            float amount,
            int operation,
            boolean active
    ) {
        AttributeModifier existing = attribute.getModifier(id);
        if (!active) {
            if (existing != null) {
                attribute.removeModifier(existing);
            }
            return;
        }

        if (existing != null && existing.getAmount() == amount) {
            return;
        }

        if (existing != null) {
            attribute.removeModifier(existing);
        }
        attribute.applyModifier(new AttributeModifier(id, name, amount, operation));
    }

    private static void boostNearbyFurnaces(EntityPlayer player) {
        int tickBonus = (int) Stat.STAT_FURNACE_FINESSE.getBonus(player);
        float timeBonus = (int) Stat.STAT_FURNACE_FINESSE.getSecondaryBonus(player);

        for (Object entry : player.worldObj.loadedTileEntityList) {
            if (!(entry instanceof TileEntityFurnace)) {
                continue;
            }

            TileEntityFurnace furnace = (TileEntityFurnace) entry;
            if (player.getDistance(furnace.xCoord, furnace.yCoord, furnace.zCoord) >= 4.0F) {
                continue;
            }

            if (player.getRNG().nextFloat() < 0.3F) {
                player.worldObj.spawnParticle(
                        "reddust",
                        furnace.xCoord + 0.5D,
                        furnace.yCoord + 1.0D,
                        furnace.zCoord + 0.5D,
                        1.0D,
                        1.0D,
                        0.0D
                );
            }

            if (!furnace.isBurning()) {
                continue;
            }
            if (furnace.furnaceCookTime < 200) {
                if ((float) player.getRNG().nextInt(100) < timeBonus) {
                    furnace.furnaceCookTime += tickBonus;
                }
            } else {
                furnace.furnaceCookTime = 199;
            }
        }
    }
}
