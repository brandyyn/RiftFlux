package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame_JackOLanternBlur {
    @Shadow protected Minecraft mc;

    @Shadow protected abstract void renderPumpkinBlur(int width, int height);

    @Inject(
            method = "renderGameOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/EntityPlayer;isPotionActive(Lnet/minecraft/potion/Potion;)Z",
                    ordinal = 0
            )
    )
    private void riftflux$renderJackOLanternBlur(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        if (!ModConfig.enableJackOLanternHelmet || ModConfig.disablePumpkinOverlay || this.mc == null || this.mc.thePlayer == null) {
            return;
        }
        if (this.mc.gameSettings.thirdPersonView != 0) {
            return;
        }

        ItemStack helmet = this.mc.thePlayer.inventory.armorItemInSlot(3);
        if (helmet == null || helmet.getItem() != Item.getItemFromBlock(Blocks.lit_pumpkin)) {
            return;
        }

        ScaledResolution scaledResolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        this.renderPumpkinBlur(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
    }
}
