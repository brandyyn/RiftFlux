package com.voidsrift.riftflux.placeablegunpowder;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.List;

public class GunpowderExplosion extends Explosion {

    private final World worldObj;

    public GunpowderExplosion(World world, Entity entity, double x, double y, double z, float size) {
        super(world, entity, x, y, z, size);
        this.worldObj = world;
    }

    public void doExplosion() {
        int x = MathHelper.floor_double(this.explosionX);
        int y = MathHelper.floor_double(this.explosionY);
        int z = MathHelper.floor_double(this.explosionZ);

        this.explosionSize *= 2.0F;
        int minX = MathHelper.floor_double(this.explosionX - this.explosionSize - 1.0D);
        int minY = MathHelper.floor_double(this.explosionY - this.explosionSize - 1.0D);
        int minZ = MathHelper.floor_double(this.explosionZ - this.explosionSize - 1.0D);
        int maxX = MathHelper.floor_double(this.explosionX + this.explosionSize + 1.0D);
        int maxY = MathHelper.floor_double(this.explosionY + this.explosionSize + 1.0D);
        int maxZ = MathHelper.floor_double(this.explosionZ + this.explosionSize + 1.0D);

        List<Entity> entities = this.worldObj.getEntitiesWithinAABBExcludingEntity(
                this.exploder,
                AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ)
        );
        ForgeEventFactory.onExplosionDetonate(this.worldObj, this, entities, this.explosionSize);

        Vec3 explosionCenter = Vec3.createVectorHelper(this.explosionX, this.explosionY, this.explosionZ);
        for (Entity entity : entities) {
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

            double blockDensity = this.worldObj.getBlockDensity(explosionCenter, entity.boundingBox);
            double damageAmount = (1.0D - normalizedDistance) * blockDensity;
            entity.attackEntityFrom(
                    DamageSource.setExplosionSource(this),
                    (float) ((int) ((damageAmount * damageAmount + damageAmount) / 2.0D * 8.0D * this.explosionSize + 1.0D))
            );
            double protectionMultiplier = EnchantmentProtection.func_92092_a(entity, damageAmount);
            entity.motionX += diffX * protectionMultiplier;
            entity.motionY += diffY * protectionMultiplier;
            entity.motionZ += diffZ * protectionMultiplier;

            if (entity instanceof EntityPlayer) {
                this.func_77277_b().put((EntityPlayer) entity, Vec3.createVectorHelper(diffX * damageAmount, diffY * damageAmount, diffZ * damageAmount));
            }
        }

        HbmExplosiveCompat.tryIgniteAdjacentExplosiveBarrels(this.worldObj, x, y, z);
        HbmExplosiveCompat.tryIgniteAdjacentExplosiveBarrels(this.worldObj, x, y - 1, z);

        if (ModConfig.placeableGunpowderSetsFireBelow && Blocks.fire.canCatchFire(this.worldObj, x, y - 1, z, ForgeDirection.UP)) {
            this.worldObj.setBlock(x, y, z, Blocks.fire);
        }
    }
}
