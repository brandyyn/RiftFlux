package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.painting.PaintingSelectionData;
import com.voidsrift.riftflux.riftflux;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemHangingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemHangingEntity.class)
public abstract class MixinItemHangingEntity_PaintingSelection {

    @Shadow
    @Final
    private Class hangingEntityClass;

    @Inject(method = "onItemUse", at = @At("HEAD"), cancellable = true)
    private void riftflux$openSelectorOnSneakUse(ItemStack stack,
                                                  EntityPlayer player,
                                                  World world,
                                                  int x, int y, int z,
                                                  int side,
                                                  float hitX, float hitY, float hitZ,
                                                  CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.enablePaintingSelection) {
            return;
        }
        if (this.hangingEntityClass != EntityPainting.class) {
            return;
        }
        if (player == null || !player.isSneaking()) {
            return;
        }

        if (world != null && world.isRemote && riftflux.proxy != null) {
            riftflux.proxy.openPaintingSelectorScreen();
        }
        cir.setReturnValue(true);
    }

    @ModifyVariable(method = "onItemUse", at = @At(value = "STORE"), ordinal = 0)
    private EntityHanging riftflux$applySelectedPainting(EntityHanging hanging,
                                                         ItemStack stack,
                                                         EntityPlayer player,
                                                         World world,
                                                         int x, int y, int z,
                                                         int side,
                                                         float hitX, float hitY, float hitZ) {
        if (!ModConfig.enablePaintingSelection) {
            return hanging;
        }
        if (!(hanging instanceof EntityPainting) || player == null) {
            return hanging;
        }

        EntityPainting.EnumArt selected = PaintingSelectionData.resolveSelectedArt(player);
        if (selected == null) {
            return hanging;
        }

        EntityPainting painting = (EntityPainting) hanging;
        painting.art = selected;
        painting.setDirection(Direction.facingToDirection[side]);
        return painting;
    }
}
