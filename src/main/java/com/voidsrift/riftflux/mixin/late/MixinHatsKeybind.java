package com.voidsrift.riftflux.mixin.late;

import com.voidsrift.riftflux.compat.hats.HatsKeybinds;
import cpw.mods.fml.common.gameevent.TickEvent;
import hats.common.Hats;
import hats.common.packet.PacketPing;
import ichun.common.core.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentTranslation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = hats.client.core.TickHandlerClient.class, remap = false)
public abstract class MixinHatsKeybind {

    @Shadow
    public boolean hasScreen;

    @Inject(method = "worldTick", at = @At("RETURN"))
    private void riftflux$handleHatsKeybind(TickEvent.ClientTickEvent event, CallbackInfo ci) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        HatsKeybinds.ensureRegistered();

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }
        if (mc.currentScreen != null || this.hasScreen) {
            return;
        }
        if (!HatsKeybinds.isOpenGuiPressed()) {
            return;
        }

        int mode = Hats.config.getSessionInt("playerHatsMode");
        if (mode == 3) {
            PacketHandler.sendToServer(Hats.channels, new PacketPing(0, false));
            return;
        }
        if (mode == 2) {
            mc.thePlayer.addChatMessage(new ChatComponentTranslation("hats.lockedMode"));
            return;
        }
        if (mode == 5) {
            String currentKing = Hats.config.getSessionString("currentKing");
            if (!currentKing.equalsIgnoreCase(mc.thePlayer.getCommandSenderName())) {
                mc.thePlayer.addChatMessage(new ChatComponentTranslation("hats.kingOfTheHat.notKing", currentKing));
                return;
            }
        }

        Hats.proxy.openHatsGui();
    }
}
