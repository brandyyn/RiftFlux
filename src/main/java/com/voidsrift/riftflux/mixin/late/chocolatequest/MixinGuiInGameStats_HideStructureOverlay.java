package com.voidsrift.riftflux.mixin.late.chocolatequest;

import com.chocolate.chocolateQuest.builder.BuilderHelper;
import com.voidsrift.riftflux.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "com.chocolate.chocolateQuest.gui.GuiInGameStats", remap = false)
public abstract class MixinGuiInGameStats_HideStructureOverlay {

    @Redirect(
            method = "onRenderExperienceBar",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/chocolate/chocolateQuest/builder/BuilderHelper;getStructureGenerationAmmount()I",
                    remap = false
            ),
            remap = false,
            require = 0
    )
    private int riftflux$hideStructureOverlay(BuilderHelper helper) {
        if (ModConfig.hideChocolateQuestGeneratingStructureOverlay) {
            return 0;
        }
        return helper.getStructureGenerationAmmount();
    }
}
