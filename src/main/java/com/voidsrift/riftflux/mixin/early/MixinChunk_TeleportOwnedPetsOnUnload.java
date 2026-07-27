package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.pets.PetKnockdown;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Keeps player-owned tameable pets from getting stranded when their old chunk unloads
 * after the owner teleports far away in the same dimension.
 */
@Mixin(Chunk.class)
public abstract class MixinChunk_TeleportOwnedPetsOnUnload {

    @Inject(method = "onChunkUnload", at = @At("HEAD"))
    private void riftflux$teleportOwnedPetsBeforeUnload(CallbackInfo ci) {
        if (!ModConfig.teleportOwnedPetsFromUnloadedChunks) {
            return;
        }

        Chunk self = (Chunk) (Object) this;
        World world = self.worldObj;
        if (world == null || world.isRemote) {
            return;
        }

        double minDistance = Math.max(0.0D, (double) ModConfig.teleportOwnedPetsMinimumDistance);

        for (int listIndex = 0; listIndex < self.entityLists.length; ++listIndex) {
            List entityList = self.entityLists[listIndex];
            for (int entityIndex = entityList.size() - 1; entityIndex >= 0; --entityIndex) {
                Object entry = entityList.get(entityIndex);
                if (!(entry instanceof EntityTameable)) {
                    continue;
                }

                EntityTameable pet = (EntityTameable) entry;
                EntityLivingBase owner = pet.getOwner();
                if (!this.riftflux$shouldRecoverToOwner(pet, owner, minDistance)) {
                    continue;
                }

                if (!this.riftflux$tryTeleportNearOwner(world, entityList, entityIndex, pet, owner)) {
                    continue;
                }
            }
        }
    }

    @Unique
    private boolean riftflux$shouldRecoverToOwner(EntityTameable pet, EntityLivingBase owner, double minDistance) {
        if (pet == null || pet.isDead || owner == null || owner.isDead) {
            return false;
        }
        if (PetKnockdown.isKnockedDown(pet)) {
            return false;
        }
        if (!pet.isTamed() || pet.isSitting() || pet.getLeashed()) {
            return false;
        }
        if (pet.worldObj != owner.worldObj) {
            return false;
        }

        double distanceSq = pet.getDistanceSqToEntity(owner);
        return distanceSq >= minDistance * minDistance;
    }

    @Unique
    private boolean riftflux$tryTeleportNearOwner(World world, List entityList, int entityIndex, EntityTameable pet, EntityLivingBase owner) {
        int baseX = MathHelper.floor_double(owner.posX);
        int baseY = MathHelper.floor_double(owner.boundingBox.minY);
        int baseZ = MathHelper.floor_double(owner.posZ);

        for (int offsetX = -2; offsetX <= 2; ++offsetX) {
            for (int offsetZ = -2; offsetZ <= 2; ++offsetZ) {
                if (offsetX == 0 && offsetZ == 0) {
                    continue;
                }

                int x = baseX + offsetX;
                int y = baseY;
                int z = baseZ + offsetZ;
                if (!this.riftflux$isSafeTeleportSpot(world, x, y, z)) {
                    continue;
                }

                entityList.remove(entityIndex);
                pet.setLocationAndAngles((double) x + 0.5D, (double) y, (double) z + 0.5D, pet.rotationYaw, pet.rotationPitch);
                pet.fallDistance = 0.0F;
                pet.getNavigator().clearPathEntity();
                world.updateEntityWithOptionalForce(pet, false);
                return true;
            }
        }

        return false;
    }

    @Unique
    private boolean riftflux$isSafeTeleportSpot(World world, int x, int y, int z) {
        return World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)
                && !world.getBlock(x, y, z).isNormalCube()
                && !world.getBlock(x, y + 1, z).isNormalCube();
    }
}
