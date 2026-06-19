package com.voidsrift.riftflux.mixin.late.geostrata;

import Reika.GeoStrata.GeoClient;
import Reika.GeoStrata.Registry.GeoBlocks;
import com.voidsrift.riftflux.geostrata.client.DecoGenItemRenderer;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.MinecraftForgeClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GeoClient.class, remap = false)
public abstract class MixinGeoClient_DecoGenItemRenderer {
    @Inject(method = "registerRenderers", at = @At("TAIL"))
    private void riftflux$registerDecoGenItemRenderer(CallbackInfo ci) {
        DecoGenItemRenderer renderer = new DecoGenItemRenderer();
        MinecraftForge.EVENT_BUS.register(renderer);

        Item item = Item.getItemFromBlock(GeoBlocks.DECOGEN.getBlockInstance());
        if (item != null) {
            MinecraftForgeClient.registerItemRenderer(item, renderer);
        }
    }
}
