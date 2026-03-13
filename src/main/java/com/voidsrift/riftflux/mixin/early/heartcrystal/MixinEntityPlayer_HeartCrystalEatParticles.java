package com.voidsrift.riftflux.mixin.early.heartcrystal;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tk.nukeduck.hearts.item.ItemHeartCrystal;
import tk.nukeduck.hearts.registry.HeartsBlocks;
import java.util.Random;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer_HeartCrystalEatParticles {
    @Inject(method = "updateItemUse", at = @At("HEAD"), cancellable = true)
    private void riftflux$useHeartCrystalParticles(ItemStack stack, int iterations, CallbackInfo ci) {
        if (stack == null || stack.getItem() == null || !(stack.getItem() instanceof ItemHeartCrystal)) {
            return;
        }
        if (stack.getItemUseAction() != EnumAction.eat) {
            return;
        }

        EntityPlayer self = (EntityPlayer)(Object)this;
        Random rng = self.getRNG();
        String particle = "blockcrack_" + Block.getIdFromBlock(HeartsBlocks.crystal) + "_0";
        int loops = Math.max(0, iterations);
        for (int i = 0; i < loops; ++i) {
            Vec3 velocity = Vec3.createVectorHelper(((double)rng.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            velocity.rotateAroundX(-self.rotationPitch * (float)Math.PI / 180.0f);
            velocity.rotateAroundY(-self.rotationYaw * (float)Math.PI / 180.0f);
            Vec3 origin = Vec3.createVectorHelper(((double)rng.nextFloat() - 0.5) * 0.3, (double)(-rng.nextFloat()) * 0.6 - 0.3, 0.6);
            origin.rotateAroundX(-self.rotationPitch * (float)Math.PI / 180.0f);
            origin.rotateAroundY(-self.rotationYaw * (float)Math.PI / 180.0f);
            origin = origin.addVector(self.posX, self.posY + (double)self.getEyeHeight(), self.posZ);
            self.worldObj.spawnParticle(particle, origin.xCoord, origin.yCoord, origin.zCoord, velocity.xCoord, velocity.yCoord + 0.05, velocity.zCoord);
        }

        self.playSound("random.eat", 0.5f + 0.5f * (float)rng.nextInt(2), (rng.nextFloat() - rng.nextFloat()) * 0.2f + 1.0f);
        ci.cancel();
    }
}
