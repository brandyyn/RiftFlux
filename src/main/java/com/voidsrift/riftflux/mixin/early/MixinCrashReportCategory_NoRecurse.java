package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.crash.CrashReportCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Prevent StackOverflowError during crash report generation by short-circuiting
 * re-entrant calls to CrashReportCategory.getCoordinateInfo(...).
 */
@Mixin(CrashReportCategory.class)
public abstract class MixinCrashReportCategory_NoRecurse {

    @Unique private static final ThreadLocal<Integer> RF$DEPTH =
            new ThreadLocal<Integer>() { @Override protected Integer initialValue() { return 0; } };

    // MCP + SRG double overloads
    @Inject(method = {
            "func_85074_a(DDD)Ljava/lang/String;"
    },
            at = @At("HEAD"), cancellable = true)
    private static void rf$guardHead(double x, double y, double z, CallbackInfoReturnable<String> cir) {
        if (!ModConfig.enableStackOverflowGuard) return;
        int d = RF$DEPTH.get();
        if (d > 0) {
            cir.setReturnValue(String.format("(%.2f,%.2f,%.2f) [suppressed lookup]", x, y, z));
            cir.cancel();
            return;
        }
        RF$DEPTH.set(d + 1);
    }

    @Inject(method = {
            "func_85074_a(DDD)Ljava/lang/String;"
    },
            at = @At("RETURN"))
    private static void rf$guardReturn(double x, double y, double z, CallbackInfoReturnable<String> cir) {
        if (!ModConfig.enableStackOverflowGuard) return;
        int d = RF$DEPTH.get();
        if (d > 0) RF$DEPTH.set(d - 1);
    }
}
