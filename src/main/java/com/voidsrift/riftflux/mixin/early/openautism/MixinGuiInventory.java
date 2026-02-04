package com.voidsrift.riftflux.mixin.early.vortex;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import com.voidsrift.riftflux.vortex.item.ModItems;
import com.voidsrift.riftflux.vortex.lib.helper.ItemHelper;
import com.voidsrift.riftflux.vortex.network.ModPackets;
import com.voidsrift.riftflux.vortex.network.PacketBackpackGuiHandle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiInventory.class})
public abstract class MixinGuiInventory {

   @Inject(
      method = {"updateScreen()V"},
      at = {@At("RETURN")}
   )
   private void onUpdateScreen(CallbackInfo ci) {
      // Use Minecraft singleton instead of @Shadow to avoid obfuscation issues
      EntityPlayer player = Minecraft.getMinecraft().thePlayer;
      if (((Object)this).getClass() == GuiInventory.class && ItemHelper.hasArmor(player, ModItems.backpack, 2)) {
         ModPackets.instance.sendToServer(new PacketBackpackGuiHandle(player, true));
      }

   }
}
