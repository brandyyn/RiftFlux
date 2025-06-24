package com.excsi.riftfixes.mixin.early;

import com.excsi.riftfixes.ModConfig;
import net.minecraftforge.common.ISpecialArmor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ISpecialArmor.ArmorProperties.class)
public class MixinArmorProperties {

    @Inject(method = "StandardizeList",at = @At("HEAD"), remap = false)
    private static void inject(ISpecialArmor.ArmorProperties[] armor, double damage, CallbackInfo ci) {
        for(ISpecialArmor.ArmorProperties prop : armor)
            prop.AbsorbRatio *= ModConfig.protectionMultiplier;
    }
}
