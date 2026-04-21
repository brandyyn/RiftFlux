package com.voidsrift.riftflux.duckling;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityDuckEgg extends EntityThrowable {
    public EntityDuckEgg(World world) {
        super(world);
    }

    public EntityDuckEgg(World world, EntityLivingBase thrower) {
        super(world, thrower);
    }

    public EntityDuckEgg(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void onImpact(MovingObjectPosition hit) {
        if (hit.entityHit != null) {
            hit.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0.0F);
        }

        if (this.worldObj.isRemote) {
            this.spawnBreakParticles();
            return;
        }

        if (!this.worldObj.isRemote) {
            if (this.rand.nextInt(8) == 0) {
                int count = this.rand.nextInt(32) == 0 ? 4 : 1;
                for (int i = 0; i < count; i++) {
                    EntityDuck duck = new EntityDuck(this.worldObj);
                    duck.setGrowingAge(-24000);
                    duck.setVariant(DuckVariant.randomNatural(this.rand));
                    duck.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
                    this.worldObj.spawnEntityInWorld(duck);
                }
            }

            this.worldObj.playSoundAtEntity(this, "random.pop", 0.7F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
            this.worldObj.setEntityState(this, (byte)3);
            this.setDead();
        }
    }

    @Override
    public void handleHealthUpdate(byte state) {
        if (state == 3) {
            this.spawnBreakParticles();
        } else {
            super.handleHealthUpdate(state);
        }
    }

    private void spawnBreakParticles() {
        int itemId = Item.getIdFromItem(DucklingContent.duckEgg);
        for (int i = 0; i < 8; i++) {
            this.worldObj.spawnParticle(
                    "iconcrack_" + itemId + "_0",
                    this.posX,
                    this.posY,
                    this.posZ,
                    (this.rand.nextFloat() - 0.5D) * 0.08D,
                    (this.rand.nextFloat() - 0.5D) * 0.08D,
                    (this.rand.nextFloat() - 0.5D) * 0.08D
            );
        }
    }
}
