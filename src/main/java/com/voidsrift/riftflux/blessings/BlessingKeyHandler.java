package com.voidsrift.riftflux.blessings;

import com.voidsrift.riftflux.net.MsgActivateBlessing;
import com.voidsrift.riftflux.net.RFNetwork;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Keyboard;

public class BlessingKeyHandler {
    private static final String CATEGORY = "RiftFlux";
    private static final KeyBinding ACTIVATE_BLESSING = new KeyBinding("Activate Blessing", Keyboard.KEY_K, CATEGORY);
    private static boolean registered;

    public static void bootstrap() {
        if (registered) {
            return;
        }
        try {
            ClientRegistry.registerKeyBinding(ACTIVATE_BLESSING);
            FMLCommonHandler.instance().bus().register(new BlessingKeyHandler());
            registered = true;
        } catch (Throwable ignored) {
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (ACTIVATE_BLESSING.isPressed()) {
            handleActivate();
        }
    }

    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent event) {
        if (ACTIVATE_BLESSING.isPressed()) {
            handleActivate();
        }
    }

    private void handleActivate() {
        if (RFNetwork.CH == null) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null || mc.currentScreen != null) {
            return;
        }
        int x = (int) Math.floor(mc.thePlayer.posX);
        int y = (int) Math.floor(mc.thePlayer.posY);
        int z = (int) Math.floor(mc.thePlayer.posZ);
        MovingObjectPosition mop = mc.objectMouseOver;
        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            x = mop.blockX;
            y = mop.blockY;
            z = mop.blockZ;
        }
        RFNetwork.CH.sendToServer(new MsgActivateBlessing(x, y, z));
    }
}
