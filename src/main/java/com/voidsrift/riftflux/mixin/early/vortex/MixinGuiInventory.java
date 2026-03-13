package com.voidsrift.riftflux.mixin.early.vortex;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import com.voidsrift.riftflux.blessings.BlessingHelper;
import com.voidsrift.riftflux.mixin.accessor.GuiContainerAccessor;
import com.voidsrift.riftflux.mixin.accessor.GuiScreenAccessor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.ArrayList;
import java.util.List;

@Mixin({GuiInventory.class})
public abstract class MixinGuiInventory {
   @Unique private int rf$playerModelX;
   @Unique private int rf$playerModelY;
   @Unique private int rf$playerModelScale;
   @Unique private boolean rf$playerModelCaptured;

   @Inject(
      method = {"updateScreen()V"},
      at = {@At("RETURN")}
   )
   private void onUpdateScreen(CallbackInfo ci) {
      // No-op: backpack inventory is integrated into the satchels GUI.
   }

   @Redirect(
      method = {"drawGuiContainerBackgroundLayer"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/inventory/GuiInventory;func_147046_a(IIFLnet/minecraft/entity/EntityLivingBase;)V"
      )
   )
   private void onDrawPlayerModel(int x, int y, int scale, float yaw, float pitch, EntityLivingBase entity) {
      rf$playerModelX = x;
      rf$playerModelY = y;
      rf$playerModelScale = scale;
      rf$playerModelCaptured = true;
      GuiInventory.func_147046_a(x, y, scale, yaw, pitch, entity);
   }

   @Inject(
      method = {"drawScreen(IIF)V"},
      at = {@At("HEAD")}
   )
   private void onDrawScreenStart(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
      rf$playerModelCaptured = false;
   }

   @Inject(
      method = {"drawScreen(IIF)V"},
      at = {@At("RETURN")}
   )
   private void onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
      if (!ModConfig.blessingsEnabled) {
         return;
      }
      EntityPlayer player = Minecraft.getMinecraft().thePlayer;
      if (player == null) {
         return;
      }
      GuiContainerAccessor accessor = (GuiContainerAccessor)(Object)this;
      int centerX = (rf$playerModelCaptured ? rf$playerModelX : accessor.getGuiLeft() + 51) + 14;
      int centerY = rf$playerModelCaptured ? rf$playerModelY : accessor.getGuiTop() + 75;
      int scale = rf$playerModelScale > 0 ? rf$playerModelScale : 30;
      int halfWidth = Math.round(scale * 0.25f);
      int height = Math.round(scale * 1.8f);
      int left = centerX - halfWidth;
      int right = centerX + halfWidth;
      int top = centerY - height;
      int bottom = centerY + 2;
      left -= 4;
      right += 9;
      top -= 3;
      bottom -= 3;
      if (mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom) {
         String blessing = BlessingHelper.getBlessing(player);
         if (blessing == null || blessing.isEmpty()) {
            blessing = BlessingHelper.getPersistedBlessing(player);
         }
         List<String> rawLines = new ArrayList<String>();
         if (blessing == null || blessing.isEmpty()) {
            rawLines.add(EnumChatFormatting.YELLOW + rf$translate("blessing.riftflux.none", "Blessing: None"));
         } else {
            rawLines.add(EnumChatFormatting.YELLOW + BlessingHelper.getLocalizedTitle(blessing));
            String description = BlessingHelper.getDescription(blessing);
            if (description != null && !description.isEmpty()) {
               rawLines.add(EnumChatFormatting.YELLOW.toString() + EnumChatFormatting.ITALIC + description);
            }
         }
         FontRenderer font = Minecraft.getMinecraft().fontRenderer;
         int maxWidth = 260;
         List<String> lines = new ArrayList<String>();
         if (font != null) {
            for (String line : rawLines) {
               if (font.getStringWidth(line) > maxWidth) {
                  lines.addAll(font.listFormattedStringToWidth(line, maxWidth));
               } else {
                  lines.add(line);
               }
            }
         } else {
            lines.addAll(rawLines);
         }
         ((GuiScreenAccessor)(Object)this).callDrawHoveringText(lines, mouseX, mouseY);
      }
   }

   @Unique
   private static String rf$translate(String key, String fallback) {
      String translated = StatCollector.translateToLocal(key);
      if (translated == null || translated.isEmpty() || key.equals(translated)) {
         return fallback;
      }
      return translated;
   }
}
