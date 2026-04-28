package com.voidsrift.riftflux.mixin.late.thaumcraft;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.common.lib.network.playerdata.PacketPlayerCompleteToServer;
import thaumcraft.common.lib.research.ResearchManager;

@Mixin(value = PacketPlayerCompleteToServer.class, remap = false)
public abstract class MixinPacketPlayerCompleteToServer_WarpOnlyResearchNotes {
    @Inject(
            method = "onMessage(Lthaumcraft/common/lib/network/playerdata/PacketPlayerCompleteToServer;Lcpw/mods/fml/common/network/simpleimpl/MessageContext;)Lcpw/mods/fml/common/network/simpleimpl/IMessage;",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void riftflux$applyWarpOnlyResearchNotes(PacketPlayerCompleteToServer message, MessageContext context, CallbackInfoReturnable<IMessage> cir) {
        AccessorPacketPlayerCompleteToServer access = (AccessorPacketPlayerCompleteToServer)message;
        byte type = access.riftflux$getType();
        String key = cleanResearchKey(access.riftflux$getKey());
        if (key == null || type != 0 || ThaumcraftApi.getWarp(key) <= 0) {
            return;
        }

        String username = access.riftflux$getUsername();
        WorldServer world = DimensionManager.getWorld(access.riftflux$getDim());
        EntityPlayerMP sender = context == null || context.getServerHandler() == null
                ? null
                : context.getServerHandler().playerEntity;
        if (world == null || username == null || sender == null || !sender.getCommandSenderName().equals(username)) {
            cir.setReturnValue(null);
            return;
        }

        EntityPlayer player = world.getPlayerEntityByName(username);
        if (!(player instanceof EntityPlayerMP)) {
            cir.setReturnValue(null);
            return;
        }

        if (player.capabilities.isCreativeMode) {
            return;
        }

        if (ResearchManager.isResearchComplete(username, key)) {
            cir.setReturnValue(null);
            return;
        }

        if (!ResearchManager.doesPlayerHaveRequisites(username, key)) {
            player.addChatMessage(new ChatComponentTranslation("tc.researcherror"));
            cir.setReturnValue(null);
            return;
        }

        EntityPlayerMP playerMP = (EntityPlayerMP)player;
        ResearchManager.createResearchNoteForPlayer(world, playerMP, key);
        world.playSoundAtEntity(player, "thaumcraft:learn", 0.75F, 1.0F);
        cir.setReturnValue(null);
    }

    private static String cleanResearchKey(String key) {
        if (key == null) {
            return null;
        }
        return key.startsWith("@") ? key.substring(1) : key;
    }
}
