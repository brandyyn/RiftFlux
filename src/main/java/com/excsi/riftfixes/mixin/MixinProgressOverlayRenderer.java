package com.excsi.riftfixes.mixin;

import Reika.ChromatiCraft.Auxiliary.Render.ProgressOverlayRenderer;
import Reika.ChromatiCraft.Magic.Progression.ChromaResearchManager;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaShaders;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import com.excsi.riftfixes.ModConfig;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProgressOverlayRenderer.class)
public class MixinProgressOverlayRenderer {

    @Shadow private int soundCooldown;

    @Inject(method = "addProgressionNote",at = @At(value = "INVOKE_ASSIGN",target = "Ljava/util/TreeMap;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"),cancellable = true,remap = false)
    public void inject(ChromaResearchManager.ProgressElement p, CallbackInfo ci){
        if (ModConfig.hasSound && this.soundCooldown == 0) {
            ReikaSoundHelper.playClientSound(ChromaSounds.GAINPROGRESS, Minecraft.getMinecraft().thePlayer, 0.5F, 1.0F, false);
            this.soundCooldown = 24;
        }
        if (ModConfig.hasShader && p instanceof ProgressStage) {
            if (ChromaOptions.PROGSHADER.getState()) {
                ChromaShaders.GAINPROGRESS.setIntensity(1.0625F);
                ChromaShaders.GAINPROGRESS.refresh();
                ChromaShaders.GAINPROGRESS.lingerTime = 0;
                ChromaShaders.GAINPROGRESS.rampDownAmount = 0.009F;
                ChromaShaders.GAINPROGRESS.rampDownFactor = 0.99F;
            } else {
                ChromaShaders.GAINPROGRESS.setIntensity(1.0F);
                ChromaShaders.GAINPROGRESS.refresh();
                ChromaShaders.GAINPROGRESS.lingerTime = 30;
                ChromaShaders.GAINPROGRESS.rampDownAmount = 0.004F;
                ChromaShaders.GAINPROGRESS.rampDownFactor = 0.997F;
            }
        }
        ci.cancel();
    }
}
