package com.voidsrift.riftflux.mixin.late.chocolatequest;

import com.chocolate.chocolateQuest.API.BuilderBase;
import com.voidsrift.riftflux.ModConfig;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Pseudo
@Mixin(targets = "com.chocolate.chocolateQuest.WorldGeneratorNew", remap = false)
public abstract class MixinWorldGeneratorNew_CatchBuilderArithmetic {

    @Redirect(
            method = "generateBigDungeon",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/chocolate/chocolateQuest/API/BuilderBase;generate(Ljava/util/Random;Lnet/minecraft/world/World;III)V",
                    remap = false
            ),
            remap = false,
            require = 0
    )
    private void riftflux$guardBuilderGenerate(
            BuilderBase builder,
            Random random,
            World world,
            int x,
            int z,
            int mobId
    ) {
        if (!ModConfig.fixChocolateQuestDivideByZero) {
            builder.generate(random, world, x, z, mobId);
            return;
        }

        try {
            builder.generate(random, world, x, z, mobId);
        } catch (ArithmeticException e) {
            System.err.println("[RiftFlux] Suppressed ArithmeticException from CQ dungeon builder "
                    + builder.getClass().getName()
                    + " at " + x + "," + z + ": " + e);
            e.printStackTrace();
        }
    }
}
