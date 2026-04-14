package com.voidsrift.riftflux.terramine;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.lang.reflect.Method;

public class EntityEyeOfCthulhu extends EntityFlying implements IBossDisplayData, IEntityMultiPart, IMob {
    private static final int ATTACK_HOVER = 0;
    private static final int ATTACK_TELEGRAPH = 1;
    private static final int ATTACK_CHARGE = 2;
    private static final int ATTACK_DODGE = 3;
    private static final int ATTACK_FIREBALL = 4;

    public double targetX;
    public double targetY;
    public double targetZ;
    public double[][] ringBuffer = new double[64][3];
    public int ringBufferIndex = -1;

    public EntityDragonPart[] dragonPartArray;
    public EntityDragonPart dragonPartHead = new EntityDragonPart(this, "head", 5.0F, 5.0F);
    public EntityDragonPart dragonPartBody = new EntityDragonPart(this, "body", 5.0F, 5.0F);
    public EntityDragonPart dragonPartTail1 = new EntityDragonPart(this, "tail", 4.0F, 4.0F);
    public EntityDragonPart dragonPartTail2 = new EntityDragonPart(this, "tail", 4.0F, 4.0F);
    public EntityDragonPart dragonPartTail3 = new EntityDragonPart(this, "tail", 4.0F, 4.0F);
    public EntityDragonPart dragonPartWing1 = new EntityDragonPart(this, "wing", 4.0F, 4.0F);
    public EntityDragonPart dragonPartWing2 = new EntityDragonPart(this, "wing", 4.0F, 4.0F);

    public float prevAnimTime;
    public float animTime;
    public boolean forceNewTarget;
    public boolean slowed;
    private Entity target;
    public int deathTicks;
    private int deathBurstXpDropped;
    private int clientGrowlCooldown;
    private static Method clientGrowlMethod;
    private static long clientGrowlMethodRetryTick;
    public EntityEnderCrystal healingEnderCrystal;
    private final Set<Integer> summonedDemonEyeIds = new HashSet<Integer>();
    private long nextDemonEyeWaveTick = -1L;
    private int attackState = ATTACK_HOVER;
    private int attackStateTicks = 30;
    private float hoverOrbitDirection = 1.0F;
    private float hoverOrbitAngle;
    private double attackAimX;
    private double attackAimY;
    private double attackAimZ;
    private int fireballShotsRemaining;
    private int fireballShotCooldown;
    private int noPlayerDespawnTicks;

    public EntityEyeOfCthulhu(World world) {
        super(world);
        this.dragonPartArray = new EntityDragonPart[]{
                this.dragonPartHead,
                this.dragonPartBody,
                this.dragonPartTail1,
                this.dragonPartTail2,
                this.dragonPartTail3,
                this.dragonPartWing1,
                this.dragonPartWing2
        };
        this.setHealth(this.getMaxHealth());
        this.setSize(7.0F, 7.0F);
        this.noClip = false;
        this.isImmuneToFire = true;
        this.targetY = 100.0D;
        this.ignoreFrustumCheck = true;
        this.experienceValue = getConfiguredXpDrop();
    }

    public void setInitialTarget(Entity target) {
        this.target = target;
        if (target != null) {
            this.targetX = target.posX;
            this.targetY = target.posY + target.getEyeHeight();
            this.targetZ = target.posZ;
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        setBaseAttribute(SharedMonsterAttributes.maxHealth, Math.max(1.0D, ModConfig.eyeOfCthulhuHealth));
        setBaseAttribute(SharedMonsterAttributes.movementSpeed, 0.36D);
        setBaseAttribute(SharedMonsterAttributes.attackDamage, 10.0D);
        setBaseAttribute(SharedMonsterAttributes.followRange, 128.0D);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    private void setBaseAttribute(IAttribute attribute, double baseValue) {
        if (attribute == null) {
            return;
        }
        IAttributeInstance instance = this.getEntityAttribute(attribute);
        if (instance == null) {
            try {
                this.getAttributeMap().registerAttribute(attribute);
            } catch (IllegalArgumentException ignored) {
                // Already registered by another path.
            }
            instance = this.getEntityAttribute(attribute);
        }
        if (instance != null) {
            instance.setBaseValue(baseValue);
        }
    }

    public double[] getMovementOffsets(int offset, float partialTick) {
        if (this.getHealth() <= 0.0F) {
            partialTick = 0.0F;
        }

        partialTick = 1.0F - partialTick;
        int newer = this.ringBufferIndex - offset & 63;
        int older = this.ringBufferIndex - offset - 1 & 63;

        double[] out = new double[3];
        double yaw = this.ringBuffer[newer][0];
        double yawDelta = MathHelper.wrapAngleTo180_double(this.ringBuffer[older][0] - yaw);
        out[0] = yaw + yawDelta * partialTick;
        double y = this.ringBuffer[newer][1];
        double yDelta = this.ringBuffer[older][1] - y;
        out[1] = y + yDelta * partialTick;
        out[2] = this.ringBuffer[newer][2] + (this.ringBuffer[older][2] - this.ringBuffer[newer][2]) * partialTick;
        return out;
    }

    @Override
    protected String getLivingSound() {
        return "mob.enderdragon.growl";
    }

    @Override
    protected String getHurtSound() {
        return "mob.enderdragon.hit";
    }

    @Override
    protected String getDeathSound() {
        return "mob.enderdragon.end";
    }

    @Override
    public void onLivingUpdate() {
        if (this.worldObj.isRemote) {
            if (this.clientGrowlCooldown > 0) {
                this.clientGrowlCooldown--;
            }
            float f = MathHelper.cos(this.animTime * (float) Math.PI * 2.0F);
            float fPrev = MathHelper.cos(this.prevAnimTime * (float) Math.PI * 2.0F);
            if (fPrev <= -0.3F && f >= -0.3F && this.clientGrowlCooldown <= 0) {
                this.playGrowlSoundFollowingEye(2.2F, 0.8F + this.rand.nextFloat() * 0.3F);
                this.clientGrowlCooldown = 280 + this.rand.nextInt(181);
            }
        }

        this.prevAnimTime = this.animTime;

        if (this.getHealth() <= 0.0F) {
            float x = (this.rand.nextFloat() - 0.5F) * 8.0F;
            float y = (this.rand.nextFloat() - 0.5F) * 4.0F;
            float z = (this.rand.nextFloat() - 0.5F) * 8.0F;
            this.worldObj.spawnParticle("largeexplode", this.posX + x, this.posY + 2.0D + y, this.posZ + z, 0.0D, 0.0D, 0.0D);
            return;
        }

        if (!this.worldObj.isRemote) {
            if (this.shouldDespawnImmediatelyForWorldState()) {
                this.setDead();
                return;
            }

            if (this.shouldDespawnForNoPlayers()) {
                int despawnDelayTicks = this.getNoPlayerDespawnDelayTicks();
                if (despawnDelayTicks <= 0 || ++this.noPlayerDespawnTicks >= despawnDelayTicks) {
                    this.broadcastNoPlayerDespawnMessage();
                    this.setDead();
                    return;
                }
            } else {
                this.noPlayerDespawnTicks = 0;
            }
        }

        this.updateDragonEnderCrystal();

        float animSpeed = 0.2F / (MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 10.0F + 1.0F);
        this.animTime += this.slowed ? animSpeed * 0.5F : animSpeed * (float) Math.pow(2.0D, this.motionY);
        this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);

        if (this.ringBufferIndex < 0) {
            for (int i = 0; i < this.ringBuffer.length; ++i) {
                this.ringBuffer[i][0] = this.rotationYaw;
                this.ringBuffer[i][1] = this.posY;
                this.ringBuffer[i][2] = this.posZ;
            }
        }

        if (++this.ringBufferIndex == this.ringBuffer.length) {
            this.ringBufferIndex = 0;
        }

        this.ringBuffer[this.ringBufferIndex][0] = this.rotationYaw;
        this.ringBuffer[this.ringBufferIndex][1] = this.posY;
        this.ringBuffer[this.ringBufferIndex][2] = this.posZ;

        if (this.worldObj.isRemote) {
            if (this.newPosRotationIncrements > 0) {
                double x = this.posX + (this.newPosX - this.posX) / this.newPosRotationIncrements;
                double y = this.posY + (this.newPosY - this.posY) / this.newPosRotationIncrements;
                double z = this.posZ + (this.newPosZ - this.posZ) / this.newPosRotationIncrements;
                double yaw = MathHelper.wrapAngleTo180_double(this.newRotationYaw - this.rotationYaw);
                this.rotationYaw = (float) (this.rotationYaw + yaw / this.newPosRotationIncrements);
                this.rotationPitch = (float) (this.rotationPitch + (this.newRotationPitch - this.rotationPitch) / this.newPosRotationIncrements);
                --this.newPosRotationIncrements;
                this.setPosition(x, y, z);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            }
        } else {
            this.refreshNearestPlayerTarget();
            this.updateCombatTargeting();

            double dx = this.targetX - this.posX;
            double dy = this.targetY - this.posY;
            double dz = this.targetZ - this.posZ;
            double distanceSq = dx * dx + dy * dy + dz * dz;

            boolean collisionRetarget = this.attackState == ATTACK_HOVER && (this.isCollidedHorizontally || this.isCollidedVertically);
            if (this.forceNewTarget || distanceSq > 6400.0D || collisionRetarget) {
                this.setNewTarget();
                dx = this.targetX - this.posX;
                dy = this.targetY - this.posY;
                dz = this.targetZ - this.posZ;
            }

            double horizontalDistance = MathHelper.sqrt_double(dx * dx + dz * dz);
            if (horizontalDistance < 0.001D) {
                horizontalDistance = 0.001D;
            }
            dy /= horizontalDistance;
            float verticalLimit = 0.6F;
            if (dy < -verticalLimit) {
                dy = -verticalLimit;
            }
            if (dy > verticalLimit) {
                dy = verticalLimit;
            }

            this.motionY += dy * 0.1D;
            this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);
            double yawToTarget = 180.0D - Math.atan2(dx, dz) * 180.0D / Math.PI;
            double yawDelta = MathHelper.wrapAngleTo180_double(yawToTarget - this.rotationYaw);
            double yawClamp = this.getYawClampForState();
            if (yawDelta > yawClamp) {
                yawDelta = yawClamp;
            }
            if (yawDelta < -yawClamp) {
                yawDelta = -yawClamp;
            }

            Vec3 toTarget = Vec3.createVectorHelper(this.targetX - this.posX, this.targetY - this.posY, this.targetZ - this.posZ).normalize();
            Vec3 forward = Vec3.createVectorHelper(MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F), this.motionY, -MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F)).normalize();
            float alignment = (float) (forward.dotProduct(toTarget) + 0.5D) / 1.5F;
            if (alignment < 0.0F) {
                alignment = 0.0F;
            }

            this.randomYawVelocity *= 0.8F;
            float speedNorm = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) + 1.0F;
            double speed = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) + 1.0D;
            if (speed > 40.0D) {
                speed = 40.0D;
            }

            this.randomYawVelocity = (float) (this.randomYawVelocity + yawDelta * (0.7D / speed / speedNorm));
            this.rotationYaw += this.randomYawVelocity * 0.1F;
            float accel = (float) (2.0D / (speed + 1.0D));
            float thrust = this.getThrustForState();
            this.moveFlying(0.0F, -1.0F, thrust * (alignment * accel + (1.0F - accel)));

            if (this.attackState == ATTACK_CHARGE) {
                this.applyChargeMotion(dx, dy, dz);
            }

            if (this.slowed) {
                this.moveEntity(this.motionX * 0.8D, this.motionY * 0.8D, this.motionZ * 0.8D);
            } else {
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
            }

            Vec3 motion = Vec3.createVectorHelper(this.motionX, this.motionY, this.motionZ).normalize();
            float drag = (float) (motion.dotProduct(forward) + 1.0D) / 2.0F;
            drag = 0.8F + 0.15F * drag;
            this.motionX *= drag;
            this.motionZ *= drag;
            this.motionY *= 0.91D;

            this.updateDemonEyeWaveSpawns();
        }

        this.renderYawOffset = this.rotationYaw;
        this.rotationYawHead = this.rotationYaw;
        this.prevRotationYawHead = this.rotationYaw;
        this.dragonPartHead.height = 3.0F;
        this.dragonPartHead.width = 3.0F;
        this.dragonPartTail1.height = 2.0F;
        this.dragonPartTail1.width = 2.0F;
        this.dragonPartTail2.height = 2.0F;
        this.dragonPartTail2.width = 2.0F;
        this.dragonPartTail3.height = 2.0F;
        this.dragonPartTail3.width = 2.0F;
        this.dragonPartBody.height = 3.0F;
        this.dragonPartBody.width = 3.0F;
        this.dragonPartWing1.height = 2.0F;
        this.dragonPartWing1.width = 4.0F;
        this.dragonPartWing2.height = 3.0F;
        this.dragonPartWing2.width = 4.0F;

        float bodyPitch = (float) (this.getMovementOffsets(5, 1.0F)[1] - this.getMovementOffsets(10, 1.0F)[1]) * 10.0F / 180.0F * (float) Math.PI;
        float cos = MathHelper.cos(bodyPitch);
        float sin = -MathHelper.sin(bodyPitch);
        float yawRad = this.rotationYaw * (float) Math.PI / 180.0F;
        float yawSin = MathHelper.sin(yawRad);
        float yawCos = MathHelper.cos(yawRad);
        this.dragonPartBody.onUpdate();
        this.dragonPartBody.setLocationAndAngles(this.posX + yawSin * 0.5D, this.posY, this.posZ - yawCos * 0.5D, 0.0F, 0.0F);
        this.dragonPartWing1.onUpdate();
        this.dragonPartWing1.setLocationAndAngles(this.posX + yawCos * 4.5D, this.posY + 2.0D, this.posZ + yawSin * 4.5D, 0.0F, 0.0F);
        this.dragonPartWing2.onUpdate();
        this.dragonPartWing2.setLocationAndAngles(this.posX - yawCos * 4.5D, this.posY + 2.0D, this.posZ - yawSin * 4.5D, 0.0F, 0.0F);

        if (!this.worldObj.isRemote && this.hurtTime == 0) {
            this.collideWithEntities(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing1.boundingBox.expand(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D)));
            this.collideWithEntities(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing2.boundingBox.expand(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D)));
            this.attackEntitiesInList(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartBody.boundingBox.expand(1.0D, 1.0D, 1.0D)));
        }

        double[] mid = this.getMovementOffsets(5, 1.0F);
        double[] head = this.getMovementOffsets(0, 1.0F);
        float headSin = MathHelper.sin(yawRad);
        float headCos = MathHelper.cos(yawRad);
        this.dragonPartHead.onUpdate();
        this.dragonPartHead.setLocationAndAngles(
                this.posX + headSin * 5.5D * cos,
                this.posY + (head[1] - mid[1]) + sin * 5.5D,
                this.posZ - headCos * 5.5D * cos,
                0.0F,
                0.0F
        );

        for (int i = 0; i < 3; ++i) {
            EntityDragonPart part = i == 0 ? this.dragonPartTail1 : (i == 1 ? this.dragonPartTail2 : this.dragonPartTail3);
            double[] tail = this.getMovementOffsets(12 + i * 2, 1.0F);
            float tailYaw = this.rotationYaw * (float) Math.PI / 180.0F + this.simplifyAngle(tail[0] - mid[0]) * (float) Math.PI / 180.0F;
            float tailSin = MathHelper.sin(tailYaw);
            float tailCos = MathHelper.cos(tailYaw);
            float base = 1.5F;
            float seg = (i + 1) * 2.0F;
            part.onUpdate();
            part.setLocationAndAngles(
                    this.posX - (yawSin * base + tailSin * seg) * cos,
                    this.posY + (tail[1] - mid[1]) - (seg + base) * sin + 1.5D,
                    this.posZ + (yawCos * base + tailCos * seg) * cos,
                    0.0F,
                    0.0F
            );
        }
    }

    private void updateDemonEyeWaveSpawns() {
        int waveCount = Math.max(0, ModConfig.eyeOfCthulhuDemonEyeWaveCount);
        int intervalSeconds = Math.max(1, ModConfig.eyeOfCthulhuDemonEyeWaveIntervalSeconds);
        int maxSummoned = Math.max(0, ModConfig.eyeOfCthulhuDemonEyeCap);

        if (waveCount <= 0 || maxSummoned <= 0 || this.target == null || this.isDead || this.getHealth() <= 0.0F) {
            return;
        }

        this.pruneSummonedDemonEyes();
        if (this.summonedDemonEyeIds.size() >= maxSummoned) {
            return;
        }

        long now = this.worldObj.getTotalWorldTime();
        if (this.nextDemonEyeWaveTick < 0L) {
            this.nextDemonEyeWaveTick = now + intervalSeconds * 20L;
            return;
        }
        if (now < this.nextDemonEyeWaveTick) {
            return;
        }

        int spawnCount = Math.min(waveCount, maxSummoned - this.summonedDemonEyeIds.size());
        for (int i = 0; i < spawnCount; i++) {
            this.spawnDemonEyeEscort();
        }
        this.nextDemonEyeWaveTick = now + intervalSeconds * 20L;
    }

    private void playGrowlSoundFollowingEye(float volume, float pitch) {
        if (this.worldObj == null || !this.worldObj.isRemote) {
            return;
        }
        long now = this.worldObj.getTotalWorldTime();
        try {
            if (clientGrowlMethod == null && now >= clientGrowlMethodRetryTick) {
                Class<?> handlerClass = Class.forName("com.voidsrift.riftflux.terramine.EyeOfCthulhuMusicHandler");
                clientGrowlMethod = handlerClass.getMethod("playGrowl", EntityEyeOfCthulhu.class, float.class, float.class);
            }
            if (clientGrowlMethod != null) {
                clientGrowlMethod.invoke(null, this, Float.valueOf(volume), Float.valueOf(pitch));
                return;
            }
        } catch (Throwable t) {
            clientGrowlMethod = null;
            clientGrowlMethodRetryTick = now + 100L;
        }
        this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.enderdragon.growl", volume, pitch, false);
    }

    private void pruneSummonedDemonEyes() {
        if (this.summonedDemonEyeIds.isEmpty()) {
            return;
        }

        Iterator<Integer> iterator = this.summonedDemonEyeIds.iterator();
        while (iterator.hasNext()) {
            Integer id = iterator.next();
            Entity entity = this.worldObj.getEntityByID(id.intValue());
            if (!(entity instanceof EntityDemonEye) || entity.isDead) {
                iterator.remove();
            }
        }
    }

    private void spawnDemonEyeEscort() {
        EntityDemonEye demonEye = new EntityDemonEye(this.worldObj);
        double angle = this.rand.nextDouble() * Math.PI * 2.0D;
        double radius = 4.0D + this.rand.nextDouble() * 6.0D;
        double spawnX = this.posX + Math.cos(angle) * radius;
        double spawnY = this.posY + 1.0D + this.rand.nextDouble() * 3.0D;
        double spawnZ = this.posZ + Math.sin(angle) * radius;

        demonEye.setLocationAndAngles(spawnX, spawnY, spawnZ, this.rand.nextFloat() * 360.0F, 0.0F);
        demonEye.setIsBatHanging(false);
        if (this.target instanceof EntityLivingBase) {
            demonEye.setAttackTarget((EntityLivingBase) this.target);
        }

        if (this.worldObj.spawnEntityInWorld(demonEye)) {
            this.summonedDemonEyeIds.add(Integer.valueOf(demonEye.getEntityId()));
        }
    }

    private void updateDragonEnderCrystal() {
        if (this.healingEnderCrystal != null) {
            if (this.healingEnderCrystal.isDead) {
                if (!this.worldObj.isRemote) {
                    this.attackEntityFromPart(this.dragonPartHead, DamageSource.setExplosionSource((Explosion) null), 10.0F);
                }
                this.healingEnderCrystal = null;
            } else if (this.ticksExisted % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
                this.setHealth(this.getHealth() + 1.0F);
            }
        }

        if (this.rand.nextInt(10) == 0) {
            float range = 32.0F;
            List list = this.worldObj.getEntitiesWithinAABB(EntityEnderCrystal.class, this.boundingBox.expand(range, range, range));
            EntityEnderCrystal closest = null;
            double distSq = Double.MAX_VALUE;

            for (int i = 0; i < list.size(); i++) {
                Object obj = list.get(i);
                if (!(obj instanceof EntityEnderCrystal)) {
                    continue;
                }
                EntityEnderCrystal crystal = (EntityEnderCrystal) obj;
                double d = crystal.getDistanceSqToEntity(this);
                if (d < distSq) {
                    distSq = d;
                    closest = crystal;
                }
            }

            this.healingEnderCrystal = closest;
        }
    }

    private void collideWithEntities(List list) {
        double cx = (this.dragonPartBody.boundingBox.minX + this.dragonPartBody.boundingBox.maxX) / 2.0D;
        double cz = (this.dragonPartBody.boundingBox.minZ + this.dragonPartBody.boundingBox.maxZ) / 2.0D;

        for (int i = 0; i < list.size(); i++) {
            Object obj = list.get(i);
            if (!(obj instanceof EntityLivingBase)) {
                continue;
            }

            Entity entity = (Entity) obj;
            double dx = entity.posX - cx;
            double dz = entity.posZ - cz;
            double len = dx * dx + dz * dz;
            if (len < 0.01D) {
                len = 0.01D;
            }
            entity.addVelocity(dx / len * 4.0D, 0.2D, dz / len * 4.0D);
        }
    }

    private void attackEntitiesInList(List list) {
        for (int i = 0; i < list.size(); ++i) {
            Object obj = list.get(i);
            if (!(obj instanceof EntityLivingBase)) {
                continue;
            }
            Entity entity = (Entity) obj;
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 10.0F);
        }
    }

    private void refreshNearestPlayerTarget() {
        if (this.target != null) {
            if (this.target.isDead || this.getDistanceSqToEntity(this.target) > 19600.0D) {
                this.target = null;
            }
        }

        if (this.target == null || this.ticksExisted % 10 == 0) {
            EntityPlayer closest = this.worldObj.getClosestPlayerToEntity(this, 160.0D);
            if (closest != null) {
                this.target = closest;
            }
        }
    }

    private void updateTargetCoordinates() {
        if (this.target == null) {
            return;
        }
        double tx = this.target.posX;
        double ty = this.target.posY + this.target.getEyeHeight() + 0.6D;
        double tz = this.target.posZ;

        double dx = tx - this.posX;
        double dz = tz - this.posZ;
        double distSq = dx * dx + dz * dz;
        double minDist = 10.0D;
        if (distSq < minDist * minDist && distSq > 1.0E-6D) {
            double dist = Math.sqrt(distSq);
            double scale = minDist / dist;
            tx = this.posX + dx * scale;
            tz = this.posZ + dz * scale;
        }

        this.targetX = tx;
        this.targetZ = tz;
        this.targetY = ty;
    }

    private void updateCombatTargeting() {
        if (!(this.target instanceof EntityLivingBase)) {
            this.attackState = ATTACK_HOVER;
            this.attackStateTicks = 20;
            this.targetX += this.rand.nextGaussian() * 1.5D;
            this.targetY += this.rand.nextGaussian() * 0.5D;
            this.targetZ += this.rand.nextGaussian() * 1.5D;
            return;
        }

        EntityLivingBase targetLiving = (EntityLivingBase) this.target;
        if (this.attackStateTicks > 0) {
            this.attackStateTicks--;
        }

        switch (this.attackState) {
            case ATTACK_FIREBALL:
                this.updateFireballTarget(targetLiving);
                if (this.fireballShotCooldown > 0) {
                    this.fireballShotCooldown--;
                }
                if (this.fireballShotsRemaining > 0 && this.fireballShotCooldown <= 0) {
                    this.shootPhaseFireball(targetLiving);
                    this.fireballShotsRemaining--;
                    this.fireballShotCooldown = 7 + this.rand.nextInt(5);
                }
                if (this.fireballShotsRemaining <= 0 || this.attackStateTicks <= 0) {
                    this.beginTelegraph(targetLiving);
                }
                break;
            case ATTACK_TELEGRAPH:
                this.updateTelegraphTarget(targetLiving);
                if (this.attackStateTicks <= 0) {
                    this.beginCharge(targetLiving);
                }
                break;
            case ATTACK_CHARGE:
                this.targetX = this.attackAimX;
                this.targetY = this.attackAimY;
                this.targetZ = this.attackAimZ;
                if (this.attackStateTicks <= 0
                        || this.getDistanceSq(this.attackAimX, this.attackAimY, this.attackAimZ) < 16.0D
                        || this.isCollidedHorizontally
                        || this.isCollidedVertically) {
                    this.beginDodge(targetLiving);
                }
                break;
            case ATTACK_DODGE:
                this.updateDodgeTarget(targetLiving);
                if (this.attackStateTicks <= 0) {
                    this.beginHover(targetLiving);
                }
                break;
            case ATTACK_HOVER:
            default:
                this.updateHoverTarget(targetLiving);
                if (this.attackStateTicks <= 0) {
                    this.chooseNextAttack(targetLiving);
                }
                break;
        }
    }

    private void chooseNextAttack(EntityLivingBase targetLiving) {
        if (this.getHealth() <= this.getMaxHealth() * 0.5F) {
            this.beginFireballVolley(targetLiving);
            return;
        }
        this.beginTelegraph(targetLiving);
    }

    private void beginHover(EntityLivingBase targetLiving) {
        this.attackState = ATTACK_HOVER;
        this.attackStateTicks = this.getHealth() <= this.getMaxHealth() * 0.5F ? 18 + this.rand.nextInt(12) : 26 + this.rand.nextInt(16);
        if (this.rand.nextBoolean()) {
            this.hoverOrbitDirection *= -1.0F;
        }
        this.hoverOrbitAngle = (float) Math.atan2(this.posZ - targetLiving.posZ, this.posX - targetLiving.posX);
        this.updateHoverTarget(targetLiving);
    }

    private void beginTelegraph(EntityLivingBase targetLiving) {
        this.attackState = ATTACK_TELEGRAPH;
        this.attackStateTicks = this.getHealth() <= this.getMaxHealth() * 0.5F ? 12 + this.rand.nextInt(6) : 16 + this.rand.nextInt(8);
        this.updateTelegraphTarget(targetLiving);
    }

    private void beginFireballVolley(EntityLivingBase targetLiving) {
        this.attackState = ATTACK_FIREBALL;
        this.fireballShotsRemaining = 3 + this.rand.nextInt(2);
        this.fireballShotCooldown = 12;
        this.attackStateTicks = 28 + this.fireballShotsRemaining * 6;
        this.updateFireballTarget(targetLiving);
    }

    private void beginCharge(EntityLivingBase targetLiving) {
        this.attackState = ATTACK_CHARGE;
        this.attackStateTicks = this.getHealth() <= this.getMaxHealth() * 0.5F ? 34 + this.rand.nextInt(10) : 28 + this.rand.nextInt(8);

        double leadTicks = this.getHealth() <= this.getMaxHealth() * 0.5F ? 7.0D : 4.5D;
        double predictedX = targetLiving.posX + targetLiving.motionX * leadTicks;
        double predictedY = targetLiving.posY + targetLiving.getEyeHeight() + 0.2D + targetLiving.motionY * (leadTicks * 0.35D);
        double predictedZ = targetLiving.posZ + targetLiving.motionZ * leadTicks;

        double dirX = predictedX - this.posX;
        double dirY = predictedY - this.posY;
        double dirZ = predictedZ - this.posZ;
        double length = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        if (length < 0.001D) {
            float yawRad = this.rotationYaw * (float) Math.PI / 180.0F;
            dirX = MathHelper.sin(yawRad);
            dirY = 0.0D;
            dirZ = -MathHelper.cos(yawRad);
            length = 1.0D;
        }

        double overshoot = this.getHealth() <= this.getMaxHealth() * 0.5F ? 12.0D : 9.0D;
        this.attackAimX = predictedX + dirX / length * overshoot;
        this.attackAimY = predictedY + dirY / length * (overshoot * 0.35D);
        this.attackAimZ = predictedZ + dirZ / length * overshoot;
        this.targetX = this.attackAimX;
        this.targetY = this.attackAimY;
        this.targetZ = this.attackAimZ;
    }

    private void beginDodge(EntityLivingBase targetLiving) {
        this.attackState = ATTACK_DODGE;
        this.attackStateTicks = 10 + this.rand.nextInt(8);
        if (this.rand.nextBoolean()) {
            this.hoverOrbitDirection *= -1.0F;
        }
        this.updateDodgeTarget(targetLiving);
    }

    private void updateHoverTarget(EntityLivingBase targetLiving) {
        double orbitSpeed = this.getHealth() <= this.getMaxHealth() * 0.5F ? 0.28D : 0.18D;
        this.hoverOrbitAngle += orbitSpeed * this.hoverOrbitDirection;
        double orbitRadius = this.getHealth() <= this.getMaxHealth() * 0.5F ? 11.0D : 14.0D;
        this.targetX = targetLiving.posX + Math.cos(this.hoverOrbitAngle) * orbitRadius;
        this.targetZ = targetLiving.posZ + Math.sin(this.hoverOrbitAngle) * orbitRadius;
        this.targetY = targetLiving.posY + targetLiving.getEyeHeight() + 4.5D + Math.sin((this.ticksExisted + this.attackStateTicks) * 0.18D) * 1.5D;
    }

    private void updateFireballTarget(EntityLivingBase targetLiving) {
        double orbitSpeed = 0.22D;
        this.hoverOrbitAngle += orbitSpeed * this.hoverOrbitDirection;
        double orbitRadius = 13.0D;
        this.targetX = targetLiving.posX + Math.cos(this.hoverOrbitAngle) * orbitRadius;
        this.targetZ = targetLiving.posZ + Math.sin(this.hoverOrbitAngle) * orbitRadius;
        this.targetY = targetLiving.posY + targetLiving.getEyeHeight() + 3.5D + Math.sin((this.ticksExisted + this.attackStateTicks) * 0.22D) * 1.2D;
    }

    private void updateTelegraphTarget(EntityLivingBase targetLiving) {
        double dx = targetLiving.posX - this.posX;
        double dz = targetLiving.posZ - this.posZ;
        double horizontalDistance = MathHelper.sqrt_double(dx * dx + dz * dz);
        if (horizontalDistance < 0.001D) {
            horizontalDistance = 0.001D;
        }

        double forwardX = dx / horizontalDistance;
        double forwardZ = dz / horizontalDistance;
        double sideX = -forwardZ * this.hoverOrbitDirection;
        double sideZ = forwardX * this.hoverOrbitDirection;
        double backOff = this.getHealth() <= this.getMaxHealth() * 0.5F ? 11.0D : 14.0D;
        double sideOffset = 6.0D + this.rand.nextDouble() * 3.0D;

        this.targetX = targetLiving.posX - forwardX * backOff + sideX * sideOffset;
        this.targetZ = targetLiving.posZ - forwardZ * backOff + sideZ * sideOffset;
        this.targetY = targetLiving.posY + targetLiving.getEyeHeight() + 3.0D + this.rand.nextDouble() * 2.5D;
    }

    private void updateDodgeTarget(EntityLivingBase targetLiving) {
        double dx = targetLiving.posX - this.posX;
        double dz = targetLiving.posZ - this.posZ;
        double horizontalDistance = MathHelper.sqrt_double(dx * dx + dz * dz);
        if (horizontalDistance < 0.001D) {
            horizontalDistance = 0.001D;
        }

        double forwardX = dx / horizontalDistance;
        double forwardZ = dz / horizontalDistance;
        double sideX = -forwardZ * this.hoverOrbitDirection;
        double sideZ = forwardX * this.hoverOrbitDirection;
        double sideDistance = 10.0D + this.rand.nextDouble() * 4.0D;
        double backDistance = 4.0D + this.rand.nextDouble() * 3.0D;

        this.targetX = targetLiving.posX + sideX * sideDistance - forwardX * backDistance;
        this.targetZ = targetLiving.posZ + sideZ * sideDistance - forwardZ * backDistance;
        this.targetY = targetLiving.posY + targetLiving.getEyeHeight() + 4.0D + this.rand.nextDouble() * 3.0D;
    }

    private double getYawClampForState() {
        if (this.attackState == ATTACK_CHARGE) {
            return 120.0D;
        }
        if (this.attackState == ATTACK_DODGE) {
            return 105.0D;
        }
        if (this.attackState == ATTACK_FIREBALL) {
            return 98.0D;
        }
        if (this.attackState == ATTACK_TELEGRAPH) {
            return 95.0D;
        }
        return 75.0D;
    }

    private float getThrustForState() {
        if (this.attackState == ATTACK_CHARGE) {
            return this.getHealth() <= this.getMaxHealth() * 0.5F ? 0.24F : 0.19F;
        }
        if (this.attackState == ATTACK_DODGE) {
            return 0.15F;
        }
        if (this.attackState == ATTACK_FIREBALL) {
            return 0.13F;
        }
        if (this.attackState == ATTACK_TELEGRAPH) {
            return 0.12F;
        }
        return this.target != null ? 0.09F : 0.06F;
    }

    private void applyChargeMotion(double dx, double dy, double dz) {
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance < 0.001D) {
            return;
        }

        double strength = this.getHealth() <= this.getMaxHealth() * 0.5F ? 0.42D : 0.34D;
        this.motionX = this.motionX * 0.82D + dx / distance * strength;
        this.motionY = this.motionY * 0.82D + dy / distance * strength;
        this.motionZ = this.motionZ * 0.82D + dz / distance * strength;
    }

    private void shootPhaseFireball(EntityLivingBase targetLiving) {
        if (this.worldObj.isRemote) {
            return;
        }

        double spawnX = this.posX;
        double spawnY = this.posY + 2.5D;
        double spawnZ = this.posZ;
        double targetX = targetLiving.posX + targetLiving.motionX * 3.0D;
        double targetY = targetLiving.posY + targetLiving.getEyeHeight() - 0.2D + targetLiving.motionY * 2.0D;
        double targetZ = targetLiving.posZ + targetLiving.motionZ * 3.0D;
        double spread = 0.18D;
        double accelX = targetX - spawnX + this.rand.nextGaussian() * spread;
        double accelY = targetY - spawnY + this.rand.nextGaussian() * (spread * 0.6D);
        double accelZ = targetZ - spawnZ + this.rand.nextGaussian() * spread;

        EntityEyeFireball fireball = new EntityEyeFireball(this.worldObj, this, accelX, accelY, accelZ);
        fireball.posX = spawnX;
        fireball.posY = spawnY;
        fireball.posZ = spawnZ;
        this.worldObj.spawnEntityInWorld(fireball);
        this.worldObj.playAuxSFXAtEntity((EntityPlayer) null, 1009, (int) this.posX, (int) this.posY, (int) this.posZ, 0);
    }

    private void setNewTarget() {
        this.forceNewTarget = false;
        this.refreshNearestPlayerTarget();
        if (this.target != null) {
            if (this.target instanceof EntityLivingBase) {
                this.beginHover((EntityLivingBase) this.target);
            } else {
                this.updateTargetCoordinates();
            }
            return;
        }

        this.targetX = this.posX + (this.rand.nextFloat() * 48.0F - 24.0F);
        this.targetY = this.posY + (this.rand.nextFloat() * 16.0F - 8.0F);
        this.targetZ = this.posZ + (this.rand.nextFloat() * 48.0F - 24.0F);
    }

    private float simplifyAngle(double angle) {
        return (float) MathHelper.wrapAngleTo180_double(angle);
    }

    @Override
    public boolean attackEntityFromPart(EntityDragonPart part, DamageSource source, float amount) {
        if (source == null || this.isEntityInvulnerable()) {
            return false;
        }

        boolean rangedOrMagic = this.isRangedOrMagicDamage(source);
        if (amount <= 0.0F) {
            if (!rangedOrMagic) {
                return false;
            }
            amount = 0.01F;
        }

        if (part != this.dragonPartHead) {
            // Keep non-head hits relevant for projectiles/spells so modded ranged attacks
            // (AM2, wand/focus projectiles, arrows) do not randomly "bounce off" body parts.
            if (!rangedOrMagic) {
                amount = amount * 0.55F + 1.0F;
            }
        }

        if (rangedOrMagic) {
            // Reduce missed-hit feel for rapid projectile collisions.
            this.hurtResistantTime = 0;
        }

        float yawRad = this.rotationYaw * (float) Math.PI / 180.0F;
        float yawSin = MathHelper.sin(yawRad);
        float yawCos = MathHelper.cos(yawRad);
        this.targetX = this.posX + yawSin * 5.0D + (this.rand.nextFloat() - 0.5F) * 2.0D;
        this.targetY = this.posY + this.rand.nextFloat() * 3.0D + 1.0D;
        this.targetZ = this.posZ - yawCos * 5.0D + (this.rand.nextFloat() - 0.5F) * 2.0D;

        Entity attacker = source.getEntity();
        if (attacker instanceof EntityLivingBase) {
            this.target = attacker;
            if (this.rand.nextFloat() < 0.35F) {
                this.beginDodge((EntityLivingBase) attacker);
            } else {
                this.beginTelegraph((EntityLivingBase) attacker);
            }
        }

        return this.func_82195_e(source, amount);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source == null || this.isEntityInvulnerable()) {
            return false;
        }

        if (this.isRangedOrMagicDamage(source)) {
            this.hurtResistantTime = 0;
        }

        Entity directSource = source.getSourceOfDamage();
        Entity attacker = source.getEntity();
        if (directSource == this || attacker == this) {
            return false;
        }

        return this.attackEntityFromPart(this.dragonPartHead, source, amount);
    }

    protected boolean func_82195_e(DamageSource source, float amount) {
        if (this.isRangedOrMagicDamage(source)) {
            int previousMaxHurtResistantTime = this.maxHurtResistantTime;
            float previousLastDamage = this.lastDamage;
            this.hurtResistantTime = 0;
            this.maxHurtResistantTime = 0;
            this.lastDamage = -Float.MAX_VALUE;
            boolean hit = super.attackEntityFrom(source, amount);
            this.maxHurtResistantTime = previousMaxHurtResistantTime;
            if (!hit) {
                this.lastDamage = previousLastDamage;
                // Fallback path for modded projectiles that fail vanilla hurt checks:
                // still apply the damage so impacts don't "bounce off" with no effect.
                float clampedAmount = Math.max(0.01F, amount);
                float newHealth = this.getHealth() - clampedAmount;
                this.lastDamage = clampedAmount;
                this.hurtResistantTime = 0;
                this.hurtTime = this.maxHurtTime = 10;
                this.attackedAtYaw = 0.0F;
                if (newHealth <= 0.0F) {
                    this.setHealth(0.0F);
                    this.onDeath(source);
                } else {
                    this.setHealth(newHealth);
                }
                hit = true;
            }
            return hit;
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    protected void onDeathUpdate() {
        ++this.deathTicks;
        if (this.deathTicks >= 60 && this.deathTicks <= 80) {
            float x = (this.rand.nextFloat() - 0.5F) * 8.0F;
            float y = (this.rand.nextFloat() - 0.5F) * 4.0F;
            float z = (this.rand.nextFloat() - 0.5F) * 8.0F;
            this.worldObj.spawnParticle("hugeexplosion", this.posX + x, this.posY + 2.0D + y, this.posZ + z, 0.0D, 0.0D, 0.0D);
        }

        if (!this.worldObj.isRemote) {
            if (this.deathTicks >= 45 && this.deathTicks <= 80 && this.deathTicks % 5 == 0) {
                int totalXp = getConfiguredXpDrop();
                int burstTotal = totalXp * 2 / 3;
                int burstIndex = (this.deathTicks - 40) / 5; // 1..8
                int targetDroppedAtBurst = burstTotal * burstIndex / 8;
                int toDrop = targetDroppedAtBurst - this.deathBurstXpDropped;
                if (toDrop > 0) {
                    this.deathBurstXpDropped += toDrop;
                    dropXp(toDrop);
                }
            }

            if (this.deathTicks == 1) {
                this.worldObj.playAuxSFX(1018, (int) this.posX, (int) this.posY, (int) this.posZ, 0);
            }
        }

        this.moveEntity(0.0D, 0.1D, 0.0D);
        this.renderYawOffset = this.rotationYaw += 50.0F;

        if (this.deathTicks == 80 && !this.worldObj.isRemote) {
            int remainingXp = getConfiguredXpDrop() - this.deathBurstXpDropped;
            if (remainingXp > 0) {
                dropXp(remainingXp);
            }

            TerrariaContent.dropConfiguredEyeLoot(this, this.rand);
            this.setDead();
        }
    }

    @Override
    protected void despawnEntity() {
    }

    @Override
    public Entity[] getParts() {
        // Keep Eye internals using dragon parts, but avoid exposing multipart hit targets
        // to external target-selection code paths that expect a single living entity.
        return new Entity[0];
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean canAttackWithItem() {
        return true;
    }

    @Override
    public boolean canBePushed() {
        return true;
    }

    @Override
    public World func_82194_d() {
        return this.worldObj;
    }

    @Override
    public int getTalkInterval() {
        return 260;
    }

    private int getConfiguredXpDrop() {
        return Math.max(0, ModConfig.eyeOfCthulhuExperience);
    }

    private void dropXp(int amount) {
        int xp = amount;
        while (xp > 0) {
            int split = EntityXPOrb.getXPSplit(xp);
            xp -= split;
            this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, split));
        }
    }

    private boolean shouldDespawnImmediatelyForWorldState() {
        return this.worldObj == null || this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL;
    }

    private boolean shouldDespawnForNoPlayers() {
        if (this.worldObj == null) {
            return true;
        }
        int chunkRadius = ModConfig.eyeOfCthulhuDespawnNoPlayerChunkRadius;
        double radius;
        if (chunkRadius > 0) {
            radius = Math.max(16.0D, chunkRadius * 16.0D);
        } else {
            radius = Math.max(16.0D, (double) ModConfig.eyeOfCthulhuDespawnNoPlayerRadius);
        }
        return this.findNearestAlivePlayer(radius) == null;
    }

    private EntityPlayer findNearestAlivePlayer(double radius) {
        if (this.worldObj == null || this.worldObj.playerEntities == null || radius <= 0.0D) {
            return null;
        }

        double bestDistanceSq = radius * radius;
        EntityPlayer best = null;
        for (Object playerObj : this.worldObj.playerEntities) {
            if (!(playerObj instanceof EntityPlayer)) {
                continue;
            }

            EntityPlayer player = (EntityPlayer) playerObj;
            if (player.isDead || player.getHealth() <= 0.0F) {
                continue;
            }

            double distanceSq = this.getDistanceSqToEntity(player);
            if (distanceSq <= bestDistanceSq) {
                bestDistanceSq = distanceSq;
                best = player;
            }
        }
        return best;
    }

    private int getNoPlayerDespawnDelayTicks() {
        return Math.max(0, ModConfig.eyeOfCthulhuDespawnNoPlayerDelaySeconds) * 20;
    }

    private boolean isRangedOrMagicDamage(DamageSource source) {
        if (source == null) {
            return false;
        }
        if (source.isProjectile() || source.isMagicDamage()) {
            return true;
        }
        Entity direct = source.getSourceOfDamage();
        Entity attacker = source.getEntity();
        return direct != null && direct != attacker;
    }

    private void broadcastNoPlayerDespawnMessage() {
        if (this.worldObj == null || this.worldObj.playerEntities == null) {
            return;
        }

        for (Object playerObj : this.worldObj.playerEntities) {
            if (!(playerObj instanceof EntityPlayer)) {
                continue;
            }
            EntityPlayer player = (EntityPlayer) playerObj;
            if (player == null || player.isDead) {
                continue;
            }
            player.addChatMessage(new ChatComponentText("The Eye has wondered off"));
        }
    }

    private static class EntityEyeFireball extends EntitySmallFireball {
        public EntityEyeFireball(World world) {
            super(world);
        }

        public EntityEyeFireball(World world, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
            super(world, shooter, accelX, accelY, accelZ);
        }

        @Override
        protected void onImpact(MovingObjectPosition hit) {
            if (!this.worldObj.isRemote) {
                if (hit.entityHit != null && !hit.entityHit.isImmuneToFire()
                        && hit.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 5.0F)) {
                    hit.entityHit.setFire(4);
                }
                this.setDead();
            }
        }
    }
}
