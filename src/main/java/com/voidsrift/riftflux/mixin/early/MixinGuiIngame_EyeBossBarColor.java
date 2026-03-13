package com.voidsrift.riftflux.mixin.early;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

@Mixin(GuiIngame.class)
public abstract class MixinGuiIngame_EyeBossBarColor extends Gui {
    @Shadow
    protected Minecraft mc;

    @Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
    private void riftflux$renderEyeBossBarRed(CallbackInfo ci) {
        if (!riftflux$isEyeOfCthulhuBossBar()) {
            return;
        }

        if (BossStatus.bossName == null || BossStatus.statusBarTime <= 0) {
            ci.cancel();
            return;
        }

        --BossStatus.statusBarTime;
        FontRenderer fontRenderer = this.mc.fontRenderer;
        ScaledResolution scaled = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        int width = scaled.getScaledWidth();
        int barWidth = 182;
        int x = width / 2 - barWidth / 2;
        int y = 12;
        int fill = (int) (BossStatus.healthScale * (float) (barWidth + 1));

        this.drawTexturedModalRect(x, y, 0, 74, barWidth, 5);
        this.drawTexturedModalRect(x, y, 0, 74, barWidth, 5);
        if (fill > 0) {
            GL11.glColor4f(0.9F, 0.15F, 0.15F, 1.0F);
            this.drawTexturedModalRect(x, y, 0, 79, fill, 5);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }

        String name = BossStatus.bossName;
        fontRenderer.drawStringWithShadow(name, width / 2 - fontRenderer.getStringWidth(name) / 2, y - 10, 0xFFFFFF);
        this.mc.getTextureManager().bindTexture(icons);
        ci.cancel();
    }

    @Unique
    private static boolean riftflux$isEyeOfCthulhuBossBar() {
        String current = riftflux$normalize(BossStatus.bossName);
        if (current.isEmpty()) {
            return false;
        }

        if (current.contains("eye of cthulhu")) {
            return true;
        }

        String legacyName = riftflux$normalize(StatCollector.translateToLocal("entity.EyeOfCthulhu.name"));
        if (!legacyName.isEmpty() && current.equals(legacyName)) {
            return true;
        }

        String moddedName = riftflux$normalize(StatCollector.translateToLocal("entity.riftflux.EyeOfCthulhu.name"));
        return !moddedName.isEmpty() && current.equals(moddedName);
    }

    @Unique
    private static String riftflux$normalize(String text) {
        if (text == null) {
            return "";
        }
        return StringUtils.stripControlCodes(text).trim().toLowerCase(Locale.ROOT);
    }
}
