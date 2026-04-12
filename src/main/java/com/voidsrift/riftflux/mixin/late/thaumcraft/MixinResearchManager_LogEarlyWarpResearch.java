package com.voidsrift.riftflux.mixin.late.thaumcraft;

import com.voidsrift.riftflux.compat.thaumcraft.ThaumcraftWarpSyncCompat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.research.ResearchManager;

@Mixin(value = ResearchManager.class, remap = false)
public abstract class MixinResearchManager_LogEarlyWarpResearch {
    @Inject(method = "completeResearch", at = @At("HEAD"))
    private void riftflux$logEarlyWarpResearch(EntityPlayer player, String key, CallbackInfo ci) {
        if (!(player instanceof EntityPlayerMP) || player.worldObj == null || player.worldObj.isRemote || Config.wuss) {
            return;
        }

        EntityPlayerMP playerMP = (EntityPlayerMP) player;
        if (playerMP.playerNetServerHandler != null) {
            return;
        }

        String cleanKey = key != null && key.startsWith("@") ? key.substring(1) : key;
        int warp = ThaumcraftApi.getWarp(cleanKey);
        if (warp <= 0) {
            return;
        }

        ResearchItem research = cleanKey == null ? null : ResearchCategories.getResearch(cleanKey);
        boolean autoUnlock = research != null && research.isAutoUnlock();
        ThaumcraftWarpSyncCompat.logEarlyWarpResearch(playerMP, cleanKey == null ? key : cleanKey, warp, autoUnlock);
    }
}
