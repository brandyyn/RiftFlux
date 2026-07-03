package com.voidsrift.riftflux.terramine;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;

public class EntityDestroyerBody extends EntityDestroyerBase {
    private static final ResourceLocation DESTROYER_BODY_TEXTURE = new ResourceLocation("riftflux:textures/entities/destroyer/body.png");
    private static final ResourceLocation DESTROYER_BODY_GOLD_TEXTURE = new ResourceLocation("riftflux:textures/entities/destroyer/bodyGold.png");

    private Entity followedEntity;
    private int missingHeadTicks;

    public EntityDestroyerBody(World world) {
        super(world);
        this.setSize(2.0F, 2.0F);
    }

    @Override
    public ResourceLocation getTexture() {
        return this.isArmoredClient() ? DESTROYER_BODY_GOLD_TEXTURE : DESTROYER_BODY_TEXTURE;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        setBaseAttribute(SharedMonsterAttributes.maxHealth, Math.max(1.0D, ModConfig.destroyerHealth));
        setBaseAttribute(SharedMonsterAttributes.attackDamage, this.getContactDamage());
        setBaseAttribute(SharedMonsterAttributes.followRange, 128.0D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (this.getHealth() <= 0.0F) {
            return;
        }

        if (this.worldObj.isRemote) {
            return;
        }

        EntityDestroyerHead head = null;
        if (!this.worldObj.isRemote) {
            this.resolveFollowedEntity();
            head = this.findDestroyerHead();
            if (head == null || head.isDead || head.getHealth() <= 0.0F) {
                if (++this.missingHeadTicks > 60) {
                    this.setDead();
                    return;
                }
            } else {
                this.missingHeadTicks = 0;
                this.destroyerTarget = head.getDestroyerTarget();
                boolean canFire = this.shouldShootBodyLaser() && this.destroyerTarget != null && this.canSeeTarget(this.destroyerTarget);
                if (this.updateLaserAttackCounter(canFire, 10, Math.max(0, ModConfig.destroyerBodyLaserCooldownTicks - 10))) {
                    this.shootDestroyerLaser(
                            this.destroyerTarget,
                            Math.max(0.0F, ModConfig.destroyerBodyLaserDamage),
                            2.5F,
                            4.0F,
                            false
                    );
                }
            }
        }

        this.updateFollowWaypoint();
        this.steerBody();
        if (head != null && !this.worldObj.isRemote) {
            this.setHealth(head.getHealth());
        }
    }

    private void updateFollowWaypoint() {
        if (this.followedEntity != null && !this.followedEntity.isDead) {
            this.waypointX = this.followedEntity.posX;
            this.waypointY = this.followedEntity.posY;
            this.waypointZ = this.followedEntity.posZ;
        }
    }

    private void steerBody() {
        double dx = this.waypointX - this.posX;
        double dy = this.waypointY - this.posY;
        double dz = this.waypointZ - this.posZ;
        double distance = MathHelper.sqrt_double(dx * dx + dy * dy + dz * dz);
        if (distance < 0.001D) {
            distance = 0.001D;
        }

        double desired = Math.max(0.5D, ModConfig.destroyerSegmentDistance);
        double speed = Math.max(0.0D, Math.min(distance - desired, 1.4D));
        if (distance < desired * 0.895D) {
            this.motionX *= 0.8D;
            this.motionY *= 0.8D;
            this.motionZ *= 0.8D;
        } else {
            this.motionX = dx / distance * speed;
            this.motionY = dy / distance * speed;
            this.motionZ = dz / distance * speed;
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.98D;
        this.motionY *= 0.98D;
        this.motionZ *= 0.98D;
        this.updateRotationFromMotion();
    }

    private boolean shouldShootBodyLaser() {
        int every = Math.max(1, ModConfig.destroyerBodyLaserEverySegments);
        return this.partId % every == 0;
    }

    public void setFollowedEntity(Entity followedEntity) {
        this.followedEntity = followedEntity;
    }

    private void resolveFollowedEntity() {
        if (this.followedEntity != null && !this.followedEntity.isDead) {
            return;
        }

        List parts = this.worldObj.getEntitiesWithinAABB(EntityDestroyerBase.class, this.boundingBox.expand(96.0D, 96.0D, 96.0D));
        Entity fallback = null;
        for (Object obj : parts) {
            EntityDestroyerBase part = (EntityDestroyerBase) obj;
            if (part == this || part.getUniqueWormId() != this.getUniqueWormId()) {
                continue;
            }
            if (this.partId == 0 && part instanceof EntityDestroyerHead) {
                this.followedEntity = part;
                return;
            }
            if (part instanceof EntityDestroyerBody && ((EntityDestroyerBody) part).getPartId() == this.partId - 1) {
                this.followedEntity = part;
                return;
            }
            if (part instanceof EntityDestroyerHead) {
                fallback = part;
            }
        }
        this.followedEntity = fallback;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source == null || this.isEntityInvulnerable()) {
            return false;
        }
        EntityDestroyerHead head = this.findDestroyerHead();
        if (head != null && !head.isDead) {
            boolean hit = head.attackEntityFrom(source, MathHelper.ceiling_float_int(amount * 0.7F));
            if (hit) {
                this.hurtTime = this.maxHurtTime = 10;
            }
            return hit;
        }
        return false;
    }

    @Override
    protected boolean attackDestroyerTarget(EntityLivingBase target, float damage) {
        float scaledDamage = Math.max(0.0F, ModConfig.destroyerBodyContactDamage)
                + target.getHealth() * Math.max(0.0F, ModConfig.destroyerBodyContactHealthPercent);
        return super.attackDestroyerTarget(target, scaledDamage);
    }
    @Override
    protected float getContactDamage() {
        return Math.max(0.0F, ModConfig.destroyerBodyContactDamage);
    }

    @Override
    protected void despawnEntity() {
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setInteger("WormID", this.getUniqueWormId());
        tag.setInteger("PartID", this.getPartId());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.setUniqueWormId(tag.getInteger("WormID"));
        this.setPartId(tag.getInteger("PartID"));
    }
}
