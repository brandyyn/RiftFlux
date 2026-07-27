package com.voidsrift.riftflux.client;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.entity.PrimedTntCarry;
import com.voidsrift.riftflux.net.MsgBombCarryUse;
import com.voidsrift.riftflux.net.RFNetwork;
import com.voidsrift.riftflux.pets.PetKnockdown;
import com.voidsrift.riftflux.pets.PetKnockdownCarry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.nmccoy.legendgear.legacy.entities.EntityBomb;
import org.lwjgl.input.Mouse;

public class BombCarryClientHandler {
    private static boolean bootstrapped;
    private boolean wasUsePressed;

    public static void bootstrap() {
        if (bootstrapped) {
            return;
        }
        bootstrapped = true;
        FMLCommonHandler.instance().bus().register(new BombCarryClientHandler());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null || mc.currentScreen != null) {
            this.wasUsePressed = false;
            return;
        }

        boolean usePressed = Mouse.isButtonDown(1);
        if (usePressed
                && !this.wasUsePressed
                && isSupportedCarriedEntity(mc.thePlayer.riddenByEntity)
                && RFNetwork.CH != null) {
            RFNetwork.CH.sendToServer(new MsgBombCarryUse(mc.thePlayer.riddenByEntity.getEntityId()));
        }

        this.wasUsePressed = usePressed;
    }

    private static boolean isSupportedCarriedEntity(Entity entity) {
        if (entity instanceof EntityBomb) {
            return true;
        }
        if (entity instanceof EntityTNTPrimed) {
            return ModConfig.enablePrimedTntPickupAndThrow
                    && PrimedTntCarry.isCarried((EntityTNTPrimed) entity);
        }
        return entity instanceof EntityLivingBase
                && ModConfig.enablePetKnockdownPickupAndThrow
                && PetKnockdownCarry.isCarried((EntityLivingBase) entity)
                && PetKnockdown.isKnockedDown((EntityLivingBase) entity);
    }
}
