package com.voidsrift.riftflux.mixin.early.legendgear;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C0CPacketInput;
import net.nmccoy.legendgear.legacy.entities.EntityGrindStar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer_StarbeamRailJump {
    @Shadow
    public EntityPlayerMP playerEntity;

    @Inject(method = "processInput(Lnet/minecraft/network/play/client/C0CPacketInput;)V", at = @At("HEAD"))
    private void riftflux$captureStarbeamRailJump(C0CPacketInput packet, CallbackInfo ci) {
        if (this.playerEntity == null || !(this.playerEntity.ridingEntity instanceof EntityGrindStar)) {
            return;
        }

        NBTTagCompound data = this.playerEntity.getEntityData();
        boolean jumping = packet.func_149618_e();
        boolean wasJumping = data.getBoolean(EntityGrindStar.STARBEAM_PACKET_WAS_JUMPING);
        if (jumping && !wasJumping) {
            data.setInteger(EntityGrindStar.STARBEAM_SERVER_JUMP_TICK, this.playerEntity.ticksExisted);
        }
        data.setBoolean(EntityGrindStar.STARBEAM_PACKET_WAS_JUMPING, jumping);
    }
}
