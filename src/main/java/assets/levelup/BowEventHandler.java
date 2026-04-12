/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.EventPriority
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.projectile.EntityArrow
 *  net.minecraft.item.EnumAction
 *  net.minecraftforge.event.entity.EntityJoinWorldEvent
 *  net.minecraftforge.event.entity.player.PlayerUseItemEvent$Start
 */
package assets.levelup;

import assets.levelup.PlayerExtendedProperties;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumAction;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;

public final class BowEventHandler {
    public static final BowEventHandler INSTANCE = new BowEventHandler();

    private BowEventHandler() {
    }

    @SubscribeEvent(priority=EventPriority.LOW)
    public void onSpawn(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityArrow) {
            int archer;
            EntityArrow arrow = (EntityArrow)event.entity;
            if (arrow.shootingEntity instanceof EntityPlayer && (archer = BowEventHandler.getArcherSkill((EntityPlayer)arrow.shootingEntity)) != 0) {
                arrow.motionX *= (double)(1.0f + (float)archer / 100.0f);
                arrow.motionY *= (double)(1.0f + (float)archer / 100.0f);
                arrow.motionZ *= (double)(1.0f + (float)archer / 100.0f);
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.LOW)
    public void onBowUse(PlayerUseItemEvent.Start event) {
        int archer;
        if (event.item != null && event.item.getMaxStackSize() == 1 && event.item.getItemUseAction() == EnumAction.bow && (archer = BowEventHandler.getArcherSkill(event.entityPlayer)) != 0 && event.duration > archer / 5) {
            event.duration -= archer / 5;
        }
    }

    public static int getArcherSkill(EntityPlayer player) {
        return PlayerExtendedProperties.getSkillFromIndex(player, 5);
    }
}

