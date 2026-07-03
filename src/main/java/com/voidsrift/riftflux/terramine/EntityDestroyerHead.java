package com.voidsrift.riftflux.terramine;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;

public class EntityDestroyerHead extends EntityDestroyerBase implements IBossDisplayData {
    private static final ResourceLocation DESTROYER_HEAD_TEXTURE = new ResourceLocation("riftflux:textures/entities/destroyer/head.png");
    private static final ResourceLocation DESTROYER_HEAD_GOLD_TEXTURE = new ResourceLocation("riftflux:textures/entities/destroyer/headGold.png");
    private static final double TARGET_SEARCH_RANGE = 192.0D;
    private static final double UNLOAD_GUARD_RANGE = 128.0D;

    private boolean bodySpawned;
    private int spawnX;
    private int spawnY;
    private int spawnZ;
    private int noPlayerDespawnTicks;
    private int deathTicks;
    private int deathXpDropped;
    private int probeCooldown;
    private int aggroCooldown;
    private int movementPhaseTicks;
    private boolean wasNearGround;
    private int surfaceY = 60;

    public EntityDestroyerHead(World world) {
        super(world);
        this.setSize(3.0F, 3.0F);
        this.experienceValue = Math.max(0, ModConfig.destroyerExperience);
        this.aggroCooldown = 60;
    }

    @Override
    public ResourceLocation getTexture() {
        return this.isArmored() ? DESTROYER_HEAD_GOLD_TEXTURE : DESTROYER_HEAD_TEXTURE;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        setBaseAttribute(SharedMonsterAttributes.maxHealth, Math.max(1.0D, ModConfig.destroyerHealth));
        setBaseAttribute(SharedMonsterAttributes.movementSpeed, 0.15D);
        setBaseAttribute(SharedMonsterAttributes.attackDamage, this.getContactDamage());
        setBaseAttribute(SharedMonsterAttributes.followRange, 192.0D);
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
        this.setUniqueWormId(this.rand.nextInt(Integer.MAX_VALUE));
        this.spawnX = MathHelper.floor_double(this.posX);
        this.spawnY = MathHelper.floor_double(this.posY);
        this.spawnZ = MathHelper.floor_double(this.posZ);
        this.surfaceY = 60;
        this.wasNearGround = false;
        this.waypointX = this.spawnX - 30 + this.rand.nextInt(60);
        this.waypointY = this.spawnY - 10 + this.rand.nextInt(20);
        this.waypointZ = this.spawnZ - 30 + this.rand.nextInt(60);
        this.aggroCooldown = 60;
        if (!this.worldObj.isRemote) {
            this.broadcast("You feel vibrations from deep below...");
        }
        return super.onSpawnWithEgg(data);
    }

    @Override
    public void onLivingUpdate() {
        if (!this.worldObj.isRemote && !this.bodySpawned && this.ticksExisted > 1) {
            this.spawnBodySegments();
        }

        super.onLivingUpdate();

        if (this.getHealth() <= 0.0F) {
            return;
        }

        if (!this.worldObj.isRemote) {
            if (this.probeCooldown > 0) {
                this.probeCooldown--;
            }

            if (this.shouldDespawnForNoPlayers()) {
                int despawnDelay = Math.max(0, ModConfig.destroyerDespawnNoPlayerDelaySeconds) * 20;
                if (despawnDelay <= 0 || ++this.noPlayerDespawnTicks >= despawnDelay) {
                    this.broadcast("The Destroyer burrows away.");
                    this.killSegments();
                    this.setDead();
                    return;
                }
            } else {
                this.noPlayerDespawnTicks = 0;
            }

            if (this.aggroCooldown == 21) {
                this.broadcast("The Destroyer has awoken!");
            }

            EntityLivingBase candidate = this.findDestroyerTarget();
            if (candidate != null && this.aggroCooldown <= 0) {
                this.destroyerTarget = candidate;
                this.aggroCooldown = 20;
            } else if (this.destroyerTarget != null && (this.destroyerTarget.isDead || this.destroyerTarget.getHealth() <= 0.0F)) {
                this.destroyerTarget = null;
            }
            if (this.aggroCooldown > -1000000) {
                --this.aggroCooldown;
            }

            this.keepFightNearTarget();
            this.updateHeadWaypoint();
            boolean canFire = this.destroyerTarget != null
                    && this.getDistanceSqToEntity(this.destroyerTarget) < this.getLaserRange() * this.getLaserRange()
                    && this.canSeeTarget(this.destroyerTarget);
            if (this.updateLaserAttackCounter(canFire, 10, Math.max(0, ModConfig.destroyerHeadLaserCooldownTicks - 10))) {
                this.shootDestroyerLaser(
                        this.destroyerTarget,
                        Math.max(0.0F, ModConfig.destroyerHeadLaserDamage),
                        2.5F,
                        2.0F,
                        true
                );
                this.maybeSpawnProbe();
            }
        }

        this.steerTowardWaypoint(0.15D, 1.0D, 0.006D, true);
    }

    public void spawnBodySegments() {
        if (this.worldObj.isRemote || this.bodySpawned) {
            return;
        }
        this.bodySpawned = true;

        int count = MathHelper.clamp_int(ModConfig.destroyerSegmentCount, 1, 160);
        double yawRadians = this.rotationYaw * Math.PI / 180.0D;
        double backX = Math.sin(yawRadians);
        double backZ = -Math.cos(yawRadians);
        double spacing = Math.max(0.5D, Math.min(3.0D, ModConfig.destroyerSegmentDistance));
        Entity previous = this;
        for (int i = 0; i < count; i++) {
            EntityDestroyerBody body = new EntityDestroyerBody(this.worldObj);
            body.setUniqueWormId(this.getUniqueWormId());
            body.setPartId(i);
            body.setFollowedEntity(previous);
            double distanceBehindHead = spacing * (i + 1);
            body.setLocationAndAngles(
                    this.posX + backX * distanceBehindHead,
                    this.posY,
                    this.posZ + backZ * distanceBehindHead,
                    this.rotationYaw,
                    this.rotationPitch
            );
            body.motionX = this.motionX;
            body.motionY = this.motionY;
            body.motionZ = this.motionZ;
            this.worldObj.spawnEntityInWorld(body);
            previous = body;
        }
    }
    private void updateHeadWaypoint() {
        EntityLivingBase target = this.destroyerTarget;
        if (target != null) {
            if (this.getDistanceSqToEntity(target) < TARGET_SEARCH_RANGE * TARGET_SEARCH_RANGE) {
                double diveY = Math.max(1.0D, ModConfig.destroyerDiveDepth);
                if (this.wasNearGround) {
                    this.waypointX = target.posX;
                    this.waypointY = target.posY;
                    this.waypointZ = target.posZ;
                    if (this.rand.nextInt(80) == 0 && this.posY > this.surfaceY && !this.isCourseTraversable()) {
                        this.wasNearGround = false;
                        this.movementPhaseTicks = 0;
                    }
                } else {
                    this.waypointX = target.posX;
                    this.waypointY = diveY;
                    this.waypointZ = target.posZ;
                    if (this.posY < diveY + 5.0D) {
                        this.wasNearGround = true;
                        this.movementPhaseTicks = 0;
                    }
                }
            } else {
                this.waypointX = target.posX;
                this.waypointY = target.posY;
                this.waypointZ = target.posZ;
                this.wasNearGround = true;
            }
            return;
        }

        if (target == null && (this.courseChangeCooldown <= 0 || this.getDistanceSq(this.waypointX, this.waypointY, this.waypointZ) < 16.0D)) {
            this.waypointX = this.spawnX - 30 + this.rand.nextInt(60);
            this.waypointY = this.spawnY - 10 + this.rand.nextInt(20);
            this.waypointZ = this.spawnZ - 30 + this.rand.nextInt(60);
        }
    }

    private EntityLivingBase findDestroyerTarget() {
        EntityPlayer player = this.findNearestPlayerIncludingCreative(TARGET_SEARCH_RANGE);
        if (player != null) {
            this.setAttackTarget(player);
            return player;
        }

        EntityLivingBase current = this.getAttackTarget();
        if (this.isValidDestroyerTarget(current, -1.0D)) {
            return current;
        }

        if (this.isValidDestroyerTarget(this.destroyerTarget, -1.0D)) {
            return this.destroyerTarget;
        }

        return null;
    }

    private boolean isValidDestroyerTarget(EntityLivingBase target, double range) {
        return target != null
                && !target.isDead
                && target.getHealth() > 0.0F
                && !(target instanceof EntityDestroyerBase)
                && !(target instanceof EntityDestroyerProbe)
                && (range <= 0.0D || this.getDistanceSqToEntity(target) <= range * range);
    }

    private EntityPlayer findNearestPlayerIncludingCreative(double range) {
        double bestDistance = range * range;
        EntityPlayer best = null;
        for (Object obj : this.worldObj.playerEntities) {
            if (!(obj instanceof EntityPlayer)) {
                continue;
            }
            EntityPlayer player = (EntityPlayer) obj;
            if (player.isDead || player.getHealth() <= 0.0F) {
                continue;
            }
            double distance = this.getDistanceSqToEntity(player);
            if (distance <= bestDistance) {
                bestDistance = distance;
                best = player;
            }
        }
        return best;
    }

    private void keepFightNearTarget() {
        EntityLivingBase target = this.destroyerTarget;
        if (!this.isValidDestroyerTarget(target, -1.0D)) {
            return;
        }

        double guardRange = Math.max(32.0D, UNLOAD_GUARD_RANGE);
        if (this.getDistanceSqToEntity(target) <= guardRange * guardRange) {
            return;
        }

        this.wasNearGround = true;
        this.movementPhaseTicks = 0;
        this.waypointX = target.posX;
        this.waypointY = target.posY;
        this.waypointZ = target.posZ;
        double dx = target.posX - this.posX;
        double dy = target.posY - this.posY;
        double dz = target.posZ - this.posZ;
        double distance = Math.max(1.0D, MathHelper.sqrt_double(dx * dx + dy * dy + dz * dz));
        this.motionX += dx / distance * 0.18D;
        this.motionY += dy / distance * 0.18D;
        this.motionZ += dz / distance * 0.18D;
    }

    private void maybeSpawnProbe() {
        if (!ModConfig.destroyerProbesEnabled || this.probeCooldown > 0 || this.rand.nextInt(Math.max(1, ModConfig.destroyerProbeChance)) != 0) {
            return;
        }

        EntityDestroyerProbe probe = new EntityDestroyerProbe(this.worldObj);
        probe.setSpawnerWormId(this.getUniqueWormId());
        probe.setTarget(this.destroyerTarget);
        probe.setLocationAndAngles(this.posX, this.posY + 1.0D, this.posZ, this.rotationYaw, this.rotationPitch);
        this.worldObj.spawnEntityInWorld(probe);
        this.probeCooldown = Math.max(0, ModConfig.destroyerProbeCooldownTicks);
    }


    public void startSpawnEggFight(EntityLivingBase target) {
        this.wasNearGround = false;
        this.surfaceY = MathHelper.floor_double(this.posY);
        this.aggroCooldown = 0;
        if (target != null && !target.isDead) {
            this.destroyerTarget = target;
            this.setAttackTarget(target);
            this.waypointX = target.posX;
            this.waypointY = Math.max(1.0D, ModConfig.destroyerDiveDepth);
            this.waypointZ = target.posZ;
        } else {
            this.waypointX = this.posX;
            this.waypointY = this.posY;
            this.waypointZ = this.posZ;
        }
    }

    public EntityLivingBase getDestroyerTarget() {
        return this.destroyerTarget;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source == null || this.isEntityInvulnerable()) {
            return false;
        }
        Entity direct = source.getSourceOfDamage();
        Entity attacker = source.getEntity();
        if (direct instanceof EntityDestroyerBase || attacker instanceof EntityDestroyerBase || direct instanceof EntityDestroyerProbe) {
            return false;
        }
        if (source.isProjectile()) {
            amount *= this.isArmored() ? 0.35F : 0.6F;
            this.hurtResistantTime = 0;
        }
        return super.attackEntityFrom(source, amount);
    }

    public boolean isArmored() {
        return this.getHealth() <= Math.max(0.0F, ModConfig.destroyerArmoredHealthThreshold);
    }

    @Override
    protected void updateArmoredFlag() {
        this.setArmoredFlag(this.isArmored());
    }

    @Override
    protected boolean attackDestroyerTarget(EntityLivingBase target, float damage) {
        float scaledDamage = Math.max(0.0F, ModConfig.destroyerHeadContactDamage)
                + target.getHealth() * Math.max(0.0F, ModConfig.destroyerHeadContactHealthPercent);
        return super.attackDestroyerTarget(target, scaledDamage);
    }

    @Override
    protected float getContactDamage() {
        return Math.max(0.0F, ModConfig.destroyerHeadContactDamage);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        TerrariaContent.dropConfiguredDestroyerLoot(this, this.rand);
        if (this.rand.nextBoolean()) {
            this.entityDropItem(new ItemStack(Items.redstone, 8 + this.rand.nextInt(9)), 0.0F);
        }
    }

    @Override
    protected void onDeathUpdate() {
        ++this.deathTicks;
        if (this.deathTicks >= 20 && this.deathTicks <= 80 && this.deathTicks % 5 == 0) {
            this.worldObj.spawnParticle(
                    "hugeexplosion",
                    this.posX + (this.rand.nextDouble() - 0.5D) * 5.0D,
                    this.posY + this.rand.nextDouble() * 3.0D,
                    this.posZ + (this.rand.nextDouble() - 0.5D) * 5.0D,
                    0.0D,
                    0.0D,
                    0.0D
            );
            if (!this.worldObj.isRemote) {
                this.worldObj.playAuxSFX(1018, (int) this.posX, (int) this.posY, (int) this.posZ, 0);
                int totalXp = Math.max(0, ModConfig.destroyerExperience);
                int targetDropped = totalXp * Math.min(this.deathTicks, 80) / 80;
                int toDrop = targetDropped - this.deathXpDropped;
                if (toDrop > 0) {
                    this.deathXpDropped += toDrop;
                    this.dropXp(toDrop);
                }
            }
        }

        this.moveEntity(0.0D, 0.08D, 0.0D);
        this.rotationYaw += 30.0F;
        this.renderYawOffset = this.rotationYaw;

        if (this.deathTicks == 80 && !this.worldObj.isRemote) {
            this.killSegments();
            int remainingXp = Math.max(0, ModConfig.destroyerExperience) - this.deathXpDropped;
            if (remainingXp > 0) {
                this.dropXp(remainingXp);
            }
            this.setDead();
        }
    }

    private void dropXp(int amount) {
        int xp = amount;
        while (xp > 0) {
            int split = EntityXPOrb.getXPSplit(xp);
            xp -= split;
            this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, split));
        }
    }

    private void killSegments() {
        List parts = this.worldObj.getEntitiesWithinAABB(EntityDestroyerBase.class, this.boundingBox.expand(256.0D, 256.0D, 256.0D));
        for (Object obj : parts) {
            EntityDestroyerBase part = (EntityDestroyerBase) obj;
            if (part != this && part.getUniqueWormId() == this.getUniqueWormId()) {
                part.setDead();
            }
        }
    }

    private boolean shouldDespawnForNoPlayers() {
        double radius = Math.max(16.0D, Math.max(1, ModConfig.destroyerDespawnNoPlayerChunkRadius) * 16.0D);
        if (this.isValidDestroyerTarget(this.destroyerTarget, radius)) {
            return false;
        }
        return this.findNearestPlayerIncludingCreative(radius) == null;
    }

    private void broadcast(String message) {
        for (Object obj : this.worldObj.playerEntities) {
            if (obj instanceof EntityPlayer) {
                ((EntityPlayer) obj).addChatMessage(new ChatComponentText(message));
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setBoolean("BodySpawned", this.bodySpawned);
        tag.setInteger("WormID", this.getUniqueWormId());
        tag.setInteger("SpawnX", this.spawnX);
        tag.setInteger("SpawnY", this.spawnY);
        tag.setInteger("SpawnZ", this.spawnZ);
        tag.setInteger("AggroCD", this.aggroCooldown);
        tag.setBoolean("WasNearGround", this.wasNearGround);
        tag.setInteger("SurfaceY", this.surfaceY);
        tag.setInteger("MovementPhaseTicks", this.movementPhaseTicks);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.bodySpawned = tag.getBoolean("BodySpawned");
        this.setUniqueWormId(tag.getInteger("WormID"));
        this.spawnX = tag.getInteger("SpawnX");
        this.spawnY = tag.getInteger("SpawnY");
        this.spawnZ = tag.getInteger("SpawnZ");
        this.aggroCooldown = tag.getInteger("AggroCD");
        this.wasNearGround = tag.getBoolean("WasNearGround");
        this.surfaceY = tag.hasKey("SurfaceY") ? tag.getInteger("SurfaceY") : 60;
        this.movementPhaseTicks = tag.getInteger("MovementPhaseTicks");
    }

    @Override
    protected void despawnEntity() {
    }
}
