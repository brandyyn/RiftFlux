package com.voidsrift.riftflux.mixin.early;

import java.io.File;
import java.io.FileWriter;

import net.minecraft.crash.CrashReport;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrashReport.class)
public abstract class MixinCrashReport_NoFalseCrashImprover {

    @Shadow private File crashReportFile;
    @Shadow @Final private static Logger logger;
    @Shadow public abstract String getCompleteReport();

    @Inject(
            method = "saveToFile(Ljava/io/File;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void riftflux$skipFalseCrashImprover(File file, CallbackInfoReturnable<Boolean> cir) {
        if (this.crashReportFile != null) {
            cir.setReturnValue(false);
            return;
        }

        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(this.getCompleteReport());
            fileWriter.close();
            this.crashReportFile = file;
            cir.setReturnValue(true);
        } catch (Throwable throwable) {
            logger.error("Could not save crash report to " + file, throwable);
            cir.setReturnValue(false);
        }
    }
}
