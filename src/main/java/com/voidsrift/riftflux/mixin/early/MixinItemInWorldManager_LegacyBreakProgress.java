package com.voidsrift.riftflux.mixin.early;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemInWorldManager.class)
public abstract class MixinItemInWorldManager_LegacyBreakProgress {
    @Shadow
    public World theWorld;

    @Shadow
    public EntityPlayerMP thisPlayerMP;

    @Shadow
    private boolean isDestroyingBlock;

    @Shadow
    private int initialDamage;

    @Shadow
    private int partiallyDestroyedBlockX;

    @Shadow
    private int partiallyDestroyedBlockY;

    @Shadow
    private int partiallyDestroyedBlockZ;

    @Shadow
    private int curblockDamage;

    @Shadow
    private boolean receivedFinishDiggingPacket;

    @Shadow
    private int posX;

    @Shadow
    private int posY;

    @Shadow
    private int posZ;

    @Shadow
    private int initialBlockDamage;

    @Unique
    private float riftflux$activeBreakProgress;

    @Unique
    private float riftflux$delayedBreakProgress;

    @Unique
    private boolean riftflux$trackingActiveBreak;

    @Unique
    private boolean riftflux$trackingDelayedBreak;

    @Inject(method = "onBlockClicked", at = @At("RETURN"))
    private void riftflux$beginLegacyBreakProgress(int x, int y, int z, int side, CallbackInfo ci) {
        if (this.isDestroyingBlock
                && x == this.partiallyDestroyedBlockX
                && y == this.partiallyDestroyedBlockY
                && z == this.partiallyDestroyedBlockZ) {
            Block block = this.theWorld.getBlock(x, y, z);
            this.riftflux$activeBreakProgress = block.getMaterial() == Material.air
                    ? 0.0F
                    : block.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, x, y, z);
            this.riftflux$trackingActiveBreak = block.getMaterial() != Material.air;
        }
    }

    @Inject(method = "updateBlockRemoving", at = @At("HEAD"))
    private void riftflux$accumulateLegacyBreakProgress(CallbackInfo ci) {
        if (this.receivedFinishDiggingPacket) {
            Block block = this.theWorld.getBlock(this.posX, this.posY, this.posZ);

            if (block.getMaterial() != Material.air) {
                if (!this.riftflux$trackingDelayedBreak) {
                    this.riftflux$delayedBreakProgress = 0.0F;
                    this.riftflux$trackingDelayedBreak = true;
                }

                this.riftflux$delayedBreakProgress += block.getPlayerRelativeBlockHardness(
                        this.thisPlayerMP,
                        this.thisPlayerMP.worldObj,
                        this.posX,
                        this.posY,
                        this.posZ
                );
            }
        } else if (this.isDestroyingBlock) {
            Block block = this.theWorld.getBlock(
                    this.partiallyDestroyedBlockX,
                    this.partiallyDestroyedBlockY,
                    this.partiallyDestroyedBlockZ
            );

            if (block.getMaterial() != Material.air) {
                if (!this.riftflux$trackingActiveBreak) {
                    this.riftflux$activeBreakProgress = block.getPlayerRelativeBlockHardness(
                            this.thisPlayerMP,
                            this.thisPlayerMP.worldObj,
                            this.partiallyDestroyedBlockX,
                            this.partiallyDestroyedBlockY,
                            this.partiallyDestroyedBlockZ
                    );
                    this.riftflux$trackingActiveBreak = true;
                }

                this.riftflux$activeBreakProgress += block.getPlayerRelativeBlockHardness(
                        this.thisPlayerMP,
                        this.thisPlayerMP.worldObj,
                        this.partiallyDestroyedBlockX,
                        this.partiallyDestroyedBlockY,
                        this.partiallyDestroyedBlockZ
                );
            }
        }
    }

    @Redirect(
            method = "updateBlockRemoving",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;getPlayerRelativeBlockHardness(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;III)F"
            )
    )
    private float riftflux$useAccumulatedProgressForUpdate(
            Block block,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z
    ) {
        if (this.receivedFinishDiggingPacket && this.riftflux$trackingDelayedBreak) {
            int elapsedTicks = this.curblockDamage - this.initialBlockDamage + 1;
            return this.riftflux$delayedBreakProgress / Math.max(1, elapsedTicks);
        }

        if (this.isDestroyingBlock && this.riftflux$trackingActiveBreak) {
            int elapsedTicks = this.curblockDamage - this.initialDamage + 1;
            return this.riftflux$activeBreakProgress / Math.max(1, elapsedTicks);
        }

        return block.getPlayerRelativeBlockHardness(player, world, x, y, z);
    }

    @Redirect(
            method = "uncheckedTryHarvestBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;getPlayerRelativeBlockHardness(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;III)F"
            )
    )
    private float riftflux$useAccumulatedProgressForFinish(
            Block block,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z
    ) {
        if (this.riftflux$trackingActiveBreak) {
            int elapsedTicks = this.curblockDamage - this.initialDamage + 1;
            return this.riftflux$activeBreakProgress / Math.max(1, elapsedTicks);
        }

        return block.getPlayerRelativeBlockHardness(player, world, x, y, z);
    }

    @Inject(method = "uncheckedTryHarvestBlock", at = @At("RETURN"))
    private void riftflux$transferLegacyBreakProgress(int x, int y, int z, CallbackInfo ci) {
        if (this.receivedFinishDiggingPacket && x == this.posX && y == this.posY && z == this.posZ) {
            this.riftflux$delayedBreakProgress = this.riftflux$activeBreakProgress;
            this.riftflux$trackingDelayedBreak = this.riftflux$trackingActiveBreak;
            this.riftflux$activeBreakProgress = 0.0F;
            this.riftflux$trackingActiveBreak = false;
        }
    }

    @Inject(method = "updateBlockRemoving", at = @At("RETURN"))
    private void riftflux$clearCompletedLegacyBreakProgress(CallbackInfo ci) {
        if (!this.isDestroyingBlock) {
            this.riftflux$activeBreakProgress = 0.0F;
            this.riftflux$trackingActiveBreak = false;
        }

        if (!this.receivedFinishDiggingPacket) {
            this.riftflux$delayedBreakProgress = 0.0F;
            this.riftflux$trackingDelayedBreak = false;
        }
    }

    @Inject(method = "cancelDestroyingBlock", at = @At("RETURN"))
    private void riftflux$cancelLegacyBreakProgress(int x, int y, int z, CallbackInfo ci) {
        this.riftflux$activeBreakProgress = 0.0F;
        this.riftflux$trackingActiveBreak = false;
    }

    @Inject(method = "tryHarvestBlock", at = @At("RETURN"))
    private void riftflux$clearHarvestedLegacyBreakProgress(
            int x,
            int y,
            int z,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (x == this.partiallyDestroyedBlockX
                && y == this.partiallyDestroyedBlockY
                && z == this.partiallyDestroyedBlockZ) {
            this.riftflux$activeBreakProgress = 0.0F;
            this.riftflux$trackingActiveBreak = false;
        }

        if (x == this.posX && y == this.posY && z == this.posZ) {
            this.riftflux$delayedBreakProgress = 0.0F;
            this.riftflux$trackingDelayedBreak = false;
        }
    }
}
