package com.voidsrift.riftflux.palaria.entity;

import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.HashSet;
import java.util.List;

public class CreeptileExplosion extends Explosion {
    private static final int RAY_COUNT = 16;

    private final World worldObj;
    private final boolean damagesEnvironment;
    private final float damageMultiplier;
    private final float knockbackMultiplier;

    public CreeptileExplosion(
            World world,
            Entity exploder,
            double x,
            double y,
            double z,
            float size,
            boolean damagesEnvironment,
            float damageMultiplier,
            float knockbackMultiplier
    ) {
        super(world, exploder, x, y, z, size);
        this.worldObj = world;
        this.damagesEnvironment = damagesEnvironment;
        this.damageMultiplier = Math.max(0.0F, damageMultiplier);
        this.knockbackMultiplier = Math.max(0.0F, knockbackMultiplier);
        this.isSmoking = damagesEnvironment;
    }

    public void doExplosion() {
        doExplosionA();
        if (damagesEnvironment) {
            doExplosionB(true);
            spawnMainExplosionParticle();
        } else {
            playExplosionSound();
            spawnMainExplosionParticle();
        }
    }

    @Override
    public void doExplosionA() {
        float originalSize = this.explosionSize;
        if (damagesEnvironment) {
            collectAffectedBlocks();
        }

        this.explosionSize *= 2.0F;
        int minX = MathHelper.floor_double(this.explosionX - this.explosionSize - 1.0D);
        int maxX = MathHelper.floor_double(this.explosionX + this.explosionSize + 1.0D);
        int minY = MathHelper.floor_double(this.explosionY - this.explosionSize - 1.0D);
        int maxY = MathHelper.floor_double(this.explosionY + this.explosionSize + 1.0D);
        int minZ = MathHelper.floor_double(this.explosionZ - this.explosionSize - 1.0D);
        int maxZ = MathHelper.floor_double(this.explosionZ + this.explosionSize + 1.0D);
        List<Entity> entities = this.worldObj.getEntitiesWithinAABBExcludingEntity(
                this.exploder,
                AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ)
        );
        ForgeEventFactory.onExplosionDetonate(this.worldObj, this, entities, this.explosionSize);

        Vec3 center = Vec3.createVectorHelper(this.explosionX, this.explosionY, this.explosionZ);
        for (Entity entity : entities) {
            if (!damagesEnvironment && !(entity instanceof EntityLivingBase)) {
                continue;
            }

            double normalizedDistance = entity.getDistance(this.explosionX, this.explosionY, this.explosionZ) / this.explosionSize;
            if (normalizedDistance > 1.0D) {
                continue;
            }

            double diffX = entity.posX - this.explosionX;
            double diffY = entity.posY + entity.getEyeHeight() - this.explosionY;
            double diffZ = entity.posZ - this.explosionZ;
            double displacement = MathHelper.sqrt_double(diffX * diffX + diffY * diffY + diffZ * diffZ);
            if (displacement == 0.0D) {
                continue;
            }

            diffX /= displacement;
            diffY /= displacement;
            diffZ /= displacement;

            double blockDensity = this.worldObj.getBlockDensity(center, entity.boundingBox);
            double impact = (1.0D - normalizedDistance) * blockDensity;
            float damage = (float) ((int) ((impact * impact + impact) / 2.0D * 8.0D * this.explosionSize + 1.0D));
            damage *= damageMultiplier;
            entity.attackEntityFrom(DamageSource.setExplosionSource(this), damage);

            double protectedImpact = EnchantmentProtection.func_92092_a(entity, impact) * knockbackMultiplier;
            entity.motionX += diffX * protectedImpact;
            entity.motionY += diffY * protectedImpact;
            entity.motionZ += diffZ * protectedImpact;

            if (entity instanceof EntityPlayer) {
                this.func_77277_b().put(
                        (EntityPlayer) entity,
                        Vec3.createVectorHelper(diffX * impact * knockbackMultiplier, diffY * impact * knockbackMultiplier, diffZ * impact * knockbackMultiplier)
                );
            }
        }

        this.explosionSize = originalSize;
    }

    private void collectAffectedBlocks() {
        HashSet<ChunkPosition> affectedBlocks = new HashSet<ChunkPosition>();

        for (int x = 0; x < RAY_COUNT; ++x) {
            for (int y = 0; y < RAY_COUNT; ++y) {
                for (int z = 0; z < RAY_COUNT; ++z) {
                    if (x != 0 && x != RAY_COUNT - 1
                            && y != 0 && y != RAY_COUNT - 1
                            && z != 0 && z != RAY_COUNT - 1) {
                        continue;
                    }

                    double stepX = (float) x / ((float) RAY_COUNT - 1.0F) * 2.0F - 1.0F;
                    double stepY = (float) y / ((float) RAY_COUNT - 1.0F) * 2.0F - 1.0F;
                    double stepZ = (float) z / ((float) RAY_COUNT - 1.0F) * 2.0F - 1.0F;
                    double magnitude = Math.sqrt(stepX * stepX + stepY * stepY + stepZ * stepZ);
                    stepX /= magnitude;
                    stepY /= magnitude;
                    stepZ /= magnitude;

                    float remainingPower = this.explosionSize * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
                    double currentX = this.explosionX;
                    double currentY = this.explosionY;
                    double currentZ = this.explosionZ;
                    final float stepSize = 0.3F;

                    while (remainingPower > 0.0F) {
                        int blockX = MathHelper.floor_double(currentX);
                        int blockY = MathHelper.floor_double(currentY);
                        int blockZ = MathHelper.floor_double(currentZ);
                        net.minecraft.block.Block block = this.worldObj.getBlock(blockX, blockY, blockZ);

                        if (block.getMaterial() != net.minecraft.block.material.Material.air) {
                            float resistance = this.exploder != null
                                    ? this.exploder.func_145772_a(this, this.worldObj, blockX, blockY, blockZ, block)
                                    : block.getExplosionResistance(this.exploder, this.worldObj, blockX, blockY, blockZ, this.explosionX, this.explosionY, this.explosionZ);
                            remainingPower -= (resistance + 0.3F) * stepSize;
                        }

                        if (remainingPower > 0.0F
                                && (this.exploder == null || this.exploder.func_145774_a(this, this.worldObj, blockX, blockY, blockZ, block, remainingPower))) {
                            affectedBlocks.add(new ChunkPosition(blockX, blockY, blockZ));
                        }

                        currentX += stepX * stepSize;
                        currentY += stepY * stepSize;
                        currentZ += stepZ * stepSize;
                        remainingPower -= stepSize * 0.75F;
                    }
                }
            }
        }

        this.affectedBlockPositions.addAll(affectedBlocks);
    }

    private void playExplosionSound() {
        this.worldObj.playSoundEffect(
                this.explosionX,
                this.explosionY,
                this.explosionZ,
                "random.explode",
                4.0F,
                (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F
        );
    }

    private void spawnMainExplosionParticle() {
        String particle = this.explosionSize >= 2.0F && this.isSmoking ? "hugeexplosion" : "largeexplode";
        if (this.worldObj instanceof WorldServer) {
            ((WorldServer) this.worldObj).func_147487_a(
                    particle,
                    this.explosionX,
                    this.explosionY,
                    this.explosionZ,
                    1,
                    0.0D,
                    0.0D,
                    0.0D,
                    0.0D
            );
        } else {
            this.worldObj.spawnParticle(particle, this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D);
        }
    }
}
