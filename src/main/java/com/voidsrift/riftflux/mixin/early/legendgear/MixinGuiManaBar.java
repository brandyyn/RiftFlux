package com.voidsrift.riftflux.mixin.early.legendgear;

import com.voidsrift.riftflux.client.hud.HudHealthRowHelper;
import com.voidsrift.riftflux.legendgear.LegendGearClientState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeHooks;
import net.nmccoy.legendgear.PlayerStarstatsExtension;
import net.nmccoy.legendgear.render.GuiManaBar;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = GuiManaBar.class, remap = false)
public abstract class MixinGuiManaBar extends Gui {
    @Shadow(remap = false) private Random rand;
    @Shadow(remap = false) private int updateCounter;
    @Shadow(remap = false) private static ResourceLocation mod_icons;

    @Inject(method = "onRenderArmorBar", at = @At("HEAD"), cancellable = true, remap = false)
    private void riftflux$renderArmorBar(RenderGameOverlayEvent event, CallbackInfo ci) {
        if (!event.isCancelable() || event.type != RenderGameOverlayEvent.ElementType.ARMOR) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (player == null) {
            ci.cancel();
            return;
        }

        updateCounter = (int) ((Minecraft.getSystemTime() / 50L) % 1000L);
        rand.setSeed(updateCounter);

        boolean forceShow = LegendGearClientState.shouldForceShow(player);
        if (!forceShow && LegendGearClientState.isHoldingIceRodWithLegendGearManaDisabled(player)) {
            ci.cancel();
            return;
        }

        int manaMissing = (int)(20.0f - PlayerStarstatsExtension.get(player).getMana());
        if (manaMissing < 0) {
            manaMissing = 0;
        } else if (manaMissing > 20) {
            manaMissing = 20;
        }

        if (manaMissing >= 20 && !forceShow) {
            ci.cancel();
            return;
        }

        ScaledResolution res = event.resolution != null
                ? event.resolution
                : new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int width = res.getScaledWidth();

        mc.getTextureManager().bindTexture(mod_icons);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        int x = width / 2 - 91;
        int healthRows = HudHealthRowHelper.getHealthRows(player);

        int armorValue = ForgeHooks.getTotalArmorValue(player);
        int topHeartY = res.getScaledHeight() - 39 - (healthRows - 1) * 10;
        int y = topHeartY - 10;
        if (armorValue > 0) {
            y -= 10;
        }
        y = Math.max(0, y);

        for (int i = 1; i < 20; i += 2) {
            int icon = 0;
            if (i < armorValue) {
                icon = 9;
            } else if (i == armorValue) {
                icon = 18;
            }

            if (i < manaMissing) {
                drawTexturedModalRect(x, y, 0, icon, 9, 9);
            } else if (i == manaMissing) {
                drawTexturedModalRect(x, y, 9, icon, 9, 9);
            } else {
                drawTexturedModalRect(x, y, 18, icon, 9, 9);
            }
            x += 8;
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(Gui.icons);

        ci.cancel();
    }
}
