package com.voidsrift.riftflux.mixin.late.invasion;

import gravestone.block.BlockGSGraveStone;
import gravestone.config.GraveStoneConfig;
import invmod.common.entity.ModifyBlockEntry;
import net.minecraft.entity.EntityLiving;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "invmod.common.entity.TerrainModifier", remap = false)
public abstract class MixinTerrainModifier_ProtectGravestones {
    @Shadow(remap = false)
    private EntityLiving theEntity;

    @Inject(
            method = "changeBlock",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void riftflux$rejectGravestoneMining(
            ModifyBlockEntry entry,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (GraveStoneConfig.onlyPlayersCanBreakGraves
                && this.theEntity.worldObj.getBlock(entry.getXCoord(), entry.getYCoord(), entry.getZCoord())
                instanceof BlockGSGraveStone) {
            cir.setReturnValue(false);
        }
    }
}
