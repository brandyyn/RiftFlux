package com.voidsrift.riftflux.mixin.early;

import gravestone.block.BlockGSGraveStone;
import gravestone.block.GravestoneRemovalGuard;
import gravestone.config.GraveStoneConfig;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Prevents any non-player system from replacing protected gravestones through World APIs. */
@Mixin(World.class)
public abstract class MixinWorld_ProtectGravestones {
   @Shadow
   public abstract Block getBlock(int x, int y, int z);

   @Inject(
         method = "setBlock(IIILnet/minecraft/block/Block;II)Z",
         at = @At("HEAD"),
         cancellable = true
   )
   private void rf$protectGravestoneReplacement(int x, int y, int z, Block replacement, int metadata, int flags,
                                                CallbackInfoReturnable<Boolean> cir) {
      if (!GraveStoneConfig.onlyPlayersCanBreakGraves || GravestoneRemovalGuard.isRemovalAuthorized()) {
         return;
      }

      Block current = this.getBlock(x, y, z);
      if (current instanceof BlockGSGraveStone && replacement != current) {
         cir.setReturnValue(false);
      }
   }
}
