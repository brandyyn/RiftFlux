package com.voidsrift.riftflux.mixin.early.asgardshield;

import com.voidsrift.riftflux.asgardshield.AsgardShieldHud;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiIngameForge.class, remap = false)
public abstract class MixinGuiIngameForge_AsgardAirOffset {
    @Shadow(remap = false)
    private static int right_height;

    @Unique
    private int riftflux$asgardAirShift;

    @Inject(method = "renderAir", at = @At("HEAD"), remap = false)
    private void riftflux$moveAirAboveAsgardGauge(int width, int height, CallbackInfo ci) {
        riftflux$asgardAirShift = 0;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc == null ? null : mc.thePlayer;
        if (player == null || !player.isInsideOfMaterial(Material.water)) {
            return;
        }

        int currentAirY = height - right_height;
        int targetAirY = AsgardShieldHud.getTargetAirBubbleTopY(player, height, currentAirY);
        int shiftAmount = currentAirY - targetAirY;
        if (shiftAmount <= 0) {
            return;
        }

        right_height += shiftAmount;
        riftflux$asgardAirShift = shiftAmount;
    }

    @Inject(method = "renderAir", at = @At("RETURN"), remap = false)
    private void riftflux$restoreAirOffset(int width, int height, CallbackInfo ci) {
        if (riftflux$asgardAirShift <= 0) {
            return;
        }
        right_height -= riftflux$asgardAirShift;
        riftflux$asgardAirShift = 0;
    }
}
