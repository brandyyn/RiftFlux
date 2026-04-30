package com.voidsrift.riftflux.wam.entity;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.net.sync.EntitySyncHelper;
import com.voidsrift.riftflux.net.sync.IEntitySyncData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBlackWidow extends EntitySpider implements IEntitySyncData {
    private static final int CLIMB_FACE_NONE = 0;
    private static final int CLIMB_FACE_NORTH = 1;
    private static final int CLIMB_FACE_SOUTH = 2;
    private static final int CLIMB_FACE_WEST = 3;
    private static final int CLIMB_FACE_EAST = 4;
    private static final byte AGGRO_FLAG = 4;
    private static final byte THREAD_FLAG = 8;
    private static final double BASE_MOVEMENT_SPEED = 0.8D;
    private static final double CHASE_MOVEMENT_SPEED = 0.95D;
    private static final double AMBUSH_PATH_SPEED = 1.0D;
    private static final double CHASE_PATH_SPEED = 1.08D;
    private static final double CHASE_STRAFE_FORCE = 0.10D;
    private static final double CHASE_APPROACH_FORCE = 0.05D;
    private static final double THREAD_POUNCE_SPEED = 0.78D;
    private static final double GROUND_POUNCE_SPEED = 0.72D;
    private static final double GROUND_POUNCE_VERTICAL_SPEED = 0.42D;
    private static final double WALL_CLIMB_SPEED = 0.18D;
    private static final float HANG_DROP_TRIGGER_RANGE = 4.5F;
    private static final float HANG_DROP_VERTICAL_RANGE = 8.0F;
    private static final int AMBUSH_SEARCH_RADIUS = 6;
    private static final int AMBUSH_SEARCH_VERTICAL = 3;
    private static final int ORBIT_MIN_TICKS = 10;
    private static final int ORBIT_MAX_TICKS = 22;
    private static final int CHASE_REPATH_COOLDOWN = 4;
    private static final int POUNCE_COOLDOWN_TICKS = 18;
    private static final int AGGRO_MEMORY_TICKS = 100;
    private static final double ORBIT_RADIUS = 2.2D;

    private int ambushTargetX = Integer.MIN_VALUE;
    private int ambushTargetY = Integer.MIN_VALUE;
    private int ambushTargetZ = Integer.MIN_VALUE;
    private int ambushRepathCooldown = 0;
    private int chaseRepathCooldown = 0;
    private int orbitTicks = 0;
    private int orbitDirection = 1;
    private int pounceCooldown = 0;
    private int aggroMemoryTicks = 0;
    private int threadAnchorX = Integer.MIN_VALUE;
    private int threadAnchorY = Integer.MIN_VALUE;
    private int threadAnchorZ = Integer.MIN_VALUE;
    private byte syncedFlags;

    public EntityBlackWidow(World world) {
        super(world);
        setSize(1.4F, 0.9F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(Math.max(1.0D, ModConfig.blackWidowMaxHealth));
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(BASE_MOVEMENT_SPEED);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!worldObj.isRemote) {
            if (aggroMemoryTicks > 0) {
                aggroMemoryTicks--;
            }
            if (pounceCooldown > 0) {
                pounceCooldown--;
            }
            if (orbitTicks > 0) {
                orbitTicks--;
            }

            EntityLivingBase chaseTarget = getChaseTarget();
            if (chaseTarget != null) {
                aggroMemoryTicks = AGGRO_MEMORY_TICKS;
            }
            updateMovementSpeed(chaseTarget != null);
            if (!updatePredatoryChase(chaseTarget)) {
                updateAmbushBehavior();
            }
            setAggroFlag(hasAggroMemory());
        }

        if (isOnThread()) {
            if (!worldObj.isRemote) {
                holdThreadPosition();
            }
        } else if (isClimbingWallPose()) {
            fallDistance = 0.0F;
            motionY = Math.max(motionY, WALL_CLIMB_SPEED);
        }
    }

    @Override
    protected Entity findPlayerToAttack() {
        Entity target = super.findPlayerToAttack();
        if (target == null || !isOnThread()) {
            return target;
        }
        return target instanceof EntityPlayer && isWithinAmbushDropRange((EntityPlayer) target) ? target : null;
    }

    @Override
    protected void attackEntity(Entity target, float distanceToTarget) {
        if (target instanceof EntityLivingBase && pounceCooldown <= 0 && onGround && distanceToTarget > 2.0F && distanceToTarget < 6.5F) {
            performGroundPounce((EntityLivingBase) target);
        }
        super.attackEntity(target, distanceToTarget);
    }

    private boolean updatePredatoryChase(EntityLivingBase target) {
        if (target == null) {
            chaseRepathCooldown = 0;
            return false;
        }

        resetAmbushTarget();
        if (isOnThread()) {
            launchFromThread(target);
            return true;
        }

        setOnThread(false);
        if (target.isDead) {
            clearAttackTargetState();
            return false;
        }

        double dx = target.posX - posX;
        double dz = target.posZ - posZ;
        double distanceSq = dx * dx + dz * dz;
        if (distanceSq < 0.0001D) {
            distanceSq = 0.0001D;
        }

        double distance = Math.sqrt(distanceSq);
        double normX = dx / distance;
        double normZ = dz / distance;

        if (orbitTicks <= 0) {
            orbitTicks = ORBIT_MIN_TICKS + rand.nextInt(ORBIT_MAX_TICKS - ORBIT_MIN_TICKS + 1);
            orbitDirection = rand.nextBoolean() ? 1 : -1;
        }

        double tangentX = -normZ * orbitDirection;
        double tangentZ = normX * orbitDirection;
        double desiredX = target.posX - normX * ORBIT_RADIUS + tangentX * 1.6D;
        double desiredZ = target.posZ - normZ * ORBIT_RADIUS + tangentZ * 1.6D;

        if (chaseRepathCooldown > 0) {
            chaseRepathCooldown--;
        } else {
            getNavigator().tryMoveToXYZ(desiredX, target.posY, desiredZ, CHASE_PATH_SPEED);
            chaseRepathCooldown = CHASE_REPATH_COOLDOWN;
        }

        motionX += clampMotion(tangentX * CHASE_STRAFE_FORCE + normX * CHASE_APPROACH_FORCE, 0.18D);
        motionZ += clampMotion(tangentZ * CHASE_STRAFE_FORCE + normZ * CHASE_APPROACH_FORCE, 0.18D);

        if (pounceCooldown <= 0 && onGround && distanceSq > 4.0D && distanceSq < 36.0D && (orbitTicks <= 4 || rand.nextInt(5) == 0)) {
            performGroundPounce(target);
        }

        return true;
    }

    private void updateAmbushBehavior() {
        if (isOnThread()) {
            if (!canMaintainThreadAmbush()) {
                stopThreadAmbush();
                return;
            }

            EntityPlayer nearbyPlayer = findNearbyAmbushPlayer();
            if (nearbyPlayer != null) {
                dropFromThread(nearbyPlayer);
            }
            return;
        }

        if (!canAttemptAmbush()) {
            resetAmbushTarget();
            return;
        }

        if (getCeilingDistance() <= 1.25F && getClimbFace() != CLIMB_FACE_NONE) {
            startThreadAmbush();
            return;
        }

        moveTowardAmbushPerch();
    }

    private boolean canAttemptAmbush() {
        return !hasAggroState()
                && !isRidden()
                && findNearbyAmbushPlayer() == null
                && getCeilingDistance() > 0.0F;
    }

    private boolean canMaintainThreadAmbush() {
        return !isRidden() && !hasAggroState() && hasValidThreadAnchor();
    }

    private void moveTowardAmbushPerch() {
        if (ambushRepathCooldown > 0) {
            ambushRepathCooldown--;
        }

        if (!hasValidAmbushTarget()) {
            if (!findAmbushPerch()) {
                return;
            }
        }

        double targetX = ambushTargetX + 0.5D;
        double targetZ = ambushTargetZ + 0.5D;
        double dx = targetX - posX;
        double dz = targetZ - posZ;
        double horizontalDistanceSq = dx * dx + dz * dz;

        if (horizontalDistanceSq > 1.0D) {
            if (ambushRepathCooldown <= 0) {
                getNavigator().tryMoveToXYZ(targetX, ambushTargetY, targetZ, AMBUSH_PATH_SPEED);
                ambushRepathCooldown = 10;
            }
            return;
        }

        getNavigator().clearPathEntity();
        motionX += clampMotion(dx * 0.2D, 0.08D);
        motionZ += clampMotion(dz * 0.2D, 0.08D);

        if (getClimbFace() != CLIMB_FACE_NONE || isCollidedHorizontally) {
            motionY = Math.max(motionY, WALL_CLIMB_SPEED);
            fallDistance = 0.0F;
        }

        if (getCeilingDistance() <= 1.25F && getClimbFace() != CLIMB_FACE_NONE) {
            startThreadAmbush();
        }
    }

    private boolean findAmbushPerch() {
        int baseX = MathHelper.floor_double(posX);
        int baseY = MathHelper.floor_double(posY);
        int baseZ = MathHelper.floor_double(posZ);
        int bestScore = Integer.MAX_VALUE;
        int bestX = Integer.MIN_VALUE;
        int bestY = Integer.MIN_VALUE;
        int bestZ = Integer.MIN_VALUE;

        for (int dy = -1; dy <= AMBUSH_SEARCH_VERTICAL; dy++) {
            int y = baseY + dy;
            for (int dx = -AMBUSH_SEARCH_RADIUS; dx <= AMBUSH_SEARCH_RADIUS; dx++) {
                for (int dz = -AMBUSH_SEARCH_RADIUS; dz <= AMBUSH_SEARCH_RADIUS; dz++) {
                    int x = baseX + dx;
                    int z = baseZ + dz;

                    if (!isOpenPerch(x, y, z)) {
                        continue;
                    }
                    if (findClimbFaceAt(x, y, z) == CLIMB_FACE_NONE) {
                        continue;
                    }
                    if (findCeilingBlockY(x, y, z) < y + 2) {
                        continue;
                    }

                    int score = dx * dx + dz * dz + dy * dy * 2;
                    if (score < bestScore) {
                        bestScore = score;
                        bestX = x;
                        bestY = y;
                        bestZ = z;
                    }
                }
            }
        }

        if (bestScore == Integer.MAX_VALUE) {
            resetAmbushTarget();
            return false;
        }

        ambushTargetX = bestX;
        ambushTargetY = bestY;
        ambushTargetZ = bestZ;
        ambushRepathCooldown = 0;
        return true;
    }

    private boolean hasValidAmbushTarget() {
        return ambushTargetX != Integer.MIN_VALUE
                && isOpenPerch(ambushTargetX, ambushTargetY, ambushTargetZ)
                && findClimbFaceAt(ambushTargetX, ambushTargetY, ambushTargetZ) != CLIMB_FACE_NONE
                && findCeilingBlockY(ambushTargetX, ambushTargetY, ambushTargetZ) >= ambushTargetY + 2;
    }

    private void resetAmbushTarget() {
        ambushTargetX = Integer.MIN_VALUE;
        ambushTargetY = Integer.MIN_VALUE;
        ambushTargetZ = Integer.MIN_VALUE;
        ambushRepathCooldown = 0;
    }

    private boolean isOpenPerch(int x, int y, int z) {
        if (y <= 0 || y >= worldObj.getActualHeight() - 2) {
            return false;
        }
        return isReplaceableForWidow(worldObj.getBlock(x, y, z))
                && isReplaceableForWidow(worldObj.getBlock(x, y + 1, z));
    }

    private void startThreadAmbush() {
        if (!setThreadAnchorFromCurrentPosition()) {
            return;
        }
        setAggroFlag(false);
        setOnThread(true);
        resetAmbushTarget();
        clearAttackTargetState();
        getNavigator().clearPathEntity();
        worldObj.setEntityState(this, (byte) 8);
    }

    private void stopThreadAmbush() {
        setOnThread(false);
        clearThreadAnchor();
        resetAmbushTarget();
    }

    private void dropFromThread(EntityPlayer player) {
        launchFromThread(player);
    }

    private void holdThreadPosition() {
        if (!hasValidThreadAnchor()) {
            stopThreadAmbush();
            return;
        }

        double desiredY = threadAnchorY - height - 0.05D;
        setPosition(posX, desiredY, posZ);
        motionX = 0.0D;
        motionZ = 0.0D;
        motionY = 0.0D;
        fallDistance = 0.0F;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        setOnThread(false);
        aggroMemoryTicks = AGGRO_MEMORY_TICKS;
        setAggroFlag(true);
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        setOnThread(false);
        aggroMemoryTicks = AGGRO_MEMORY_TICKS;
        setAggroFlag(true);
        boolean attacked = super.attackEntityAsMob(target);
        if (attacked && target instanceof EntityLivingBase) {
            int poisonDuration = 0;
            int difficultyId = worldObj.difficultySetting.getDifficultyId();
            if (difficultyId == 2) {
                poisonDuration = 7;
            } else if (difficultyId == 3) {
                poisonDuration = 15;
            }

            if (poisonDuration > 0) {
                ((EntityLivingBase) target).addPotionEffect(new PotionEffect(Potion.poison.id, poisonDuration * 20, 0));
            }
        }
        return attacked;
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        WAMMobDrops.dropConfigured(this, ModConfig.wamBlackWidowDropEntries);
    }

    @Override
    public boolean isOnLadder() {
        return !isOnThread() && !hasAggroState() && (super.isOnLadder() || isClimbingWallPose());
    }

    public float getCeilingDistance() {
        int ceilingY = findCeilingBlockY(
                MathHelper.floor_double(posX),
                MathHelper.floor_double(posY),
                MathHelper.floor_double(posZ));
        if (ceilingY <= 0) {
            return 0.0F;
        }
        return ceilingY - MathHelper.floor_double(posY);
    }

    private EntityLivingBase getChaseTarget() {
        EntityLivingBase target = getAttackTarget();
        if (target == null) {
            target = getAITarget();
        }
        if (target == null || target.isDead) {
            return null;
        }
        return target;
    }

    private void updateMovementSpeed(boolean chasing) {
        double targetSpeed = chasing ? CHASE_MOVEMENT_SPEED : BASE_MOVEMENT_SPEED;
        if (getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue() != targetSpeed) {
            getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(targetSpeed);
        }
    }

    private void launchFromThread(EntityLivingBase target) {
        stopThreadAmbush();
        setAttackTarget(target);
        setTarget(target);
        aggroMemoryTicks = AGGRO_MEMORY_TICKS;
        setAggroFlag(true);
        pounceCooldown = POUNCE_COOLDOWN_TICKS;
        orbitTicks = ORBIT_MIN_TICKS;
        chaseRepathCooldown = 0;
        getNavigator().clearPathEntity();

        double dx = target.posX - posX;
        double dz = target.posZ - posZ;
        double distance = Math.max(0.001D, Math.sqrt(dx * dx + dz * dz));
        double dy = target.boundingBox.minY + target.height * 0.35D - posY;

        motionX = dx / distance * THREAD_POUNCE_SPEED;
        motionZ = dz / distance * THREAD_POUNCE_SPEED;
        motionY = clampValue(dy * 0.2D, -0.85D, -0.2D);
        fallDistance = 0.0F;
    }

    private void performGroundPounce(EntityLivingBase target) {
        if (target == null) {
            return;
        }

        double dx = target.posX - posX;
        double dz = target.posZ - posZ;
        double distance = Math.max(0.001D, Math.sqrt(dx * dx + dz * dz));
        double tangentX = -dz / distance * orbitDirection;
        double tangentZ = dx / distance * orbitDirection;

        getNavigator().clearPathEntity();
        motionX = dx / distance * GROUND_POUNCE_SPEED + tangentX * 0.22D;
        motionZ = dz / distance * GROUND_POUNCE_SPEED + tangentZ * 0.22D;
        motionY = GROUND_POUNCE_VERTICAL_SPEED;
        fallDistance = 0.0F;
        pounceCooldown = POUNCE_COOLDOWN_TICKS;
        orbitTicks = ORBIT_MIN_TICKS;
    }

    private void clearAttackTargetState() {
        setAttackTarget(null);
        setTarget(null);
    }

    public boolean isOnThread() {
        return (this.syncedFlags & THREAD_FLAG) != 0;
    }

    public void setOnThread(boolean onThread) {
        byte value = this.syncedFlags;
        if (onThread) {
            value = (byte) (value | THREAD_FLAG);
        } else {
            value = (byte) (value & ~THREAD_FLAG);
        }
        if (this.syncedFlags == value) {
            return;
        }
        this.syncedFlags = value;
        EntitySyncHelper.sync(this);
    }

    public boolean isThreadAmbushPose() {
        return isOnThread() && !hasAggroFlag();
    }

    public boolean isClimbingWallPose() {
        return !isOnThread() && !hasAggroState() && !onGround && getClimbFace() != CLIMB_FACE_NONE;
    }

    public int getClimbFace() {
        return findClimbFaceAt(
                MathHelper.floor_double(posX),
                MathHelper.floor_double(posY),
                MathHelper.floor_double(posZ));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setBoolean("OnThread", isOnThread());
        tag.setInteger("ThreadAnchorX", threadAnchorX);
        tag.setInteger("ThreadAnchorY", threadAnchorY);
        tag.setInteger("ThreadAnchorZ", threadAnchorZ);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        setOnThread(tag.getBoolean("OnThread"));
        threadAnchorX = tag.getInteger("ThreadAnchorX");
        threadAnchorY = tag.getInteger("ThreadAnchorY");
        threadAnchorZ = tag.getInteger("ThreadAnchorZ");
    }

    @Override
    public boolean isPotionApplicable(PotionEffect effect) {
        return effect != null && effect.getPotionID() == Potion.poison.id ? false : super.isPotionApplicable(effect);
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
        setOnThread(false);
        return super.onSpawnWithEgg(data);
    }

    @SideOnly(Side.CLIENT)
    public float widowScaleAmount() {
        return 1.0F;
    }

    private boolean isRidden() {
        return riddenByEntity != null || ridingEntity != null;
    }

    private EntityPlayer findNearbyPlayer(float range) {
        EntityPlayer player = worldObj.getClosestVulnerablePlayerToEntity(this, range);
        if (player == null || !canEntityBeSeen(player)) {
            return null;
        }
        return player;
    }

    private EntityPlayer findNearbyAmbushPlayer() {
        EntityPlayer player = worldObj.getClosestVulnerablePlayerToEntity(this, HANG_DROP_VERTICAL_RANGE);
        if (player == null || !canEntityBeSeen(player) || !isWithinAmbushDropRange(player)) {
            return null;
        }
        return player;
    }

    private boolean hasAggroMemory() {
        return aggroMemoryTicks > 0 || getAttackTarget() != null || getAITarget() != null;
    }

    private boolean hasAggroState() {
        return hasAggroMemory() || hasAggroFlag();
    }

    private boolean hasAggroFlag() {
        return (this.syncedFlags & AGGRO_FLAG) != 0;
    }

    private void setAggroFlag(boolean aggressive) {
        byte value = this.syncedFlags;
        if (aggressive) {
            value = (byte) (value | AGGRO_FLAG);
        } else {
            value = (byte) (value & ~AGGRO_FLAG);
        }
        if (this.syncedFlags == value) {
            return;
        }
        this.syncedFlags = value;
        EntitySyncHelper.sync(this);
    }

    private boolean setThreadAnchorFromCurrentPosition() {
        int anchorX = MathHelper.floor_double(posX);
        int anchorY = findCeilingBlockY(
                anchorX,
                MathHelper.floor_double(posY),
                MathHelper.floor_double(posZ));
        int anchorZ = MathHelper.floor_double(posZ);

        if (anchorY <= 0) {
            clearThreadAnchor();
            return false;
        }

        threadAnchorX = anchorX;
        threadAnchorY = anchorY;
        threadAnchorZ = anchorZ;
        return true;
    }

    private boolean hasValidThreadAnchor() {
        if (threadAnchorX == Integer.MIN_VALUE || threadAnchorY <= 0 || threadAnchorZ == Integer.MIN_VALUE) {
            return false;
        }

        Block anchor = worldObj.getBlock(threadAnchorX, threadAnchorY, threadAnchorZ);
        return anchor != null && !isReplaceableForWidow(anchor) && anchor.getMaterial().isSolid();
    }

    private void clearThreadAnchor() {
        threadAnchorX = Integer.MIN_VALUE;
        threadAnchorY = Integer.MIN_VALUE;
        threadAnchorZ = Integer.MIN_VALUE;
    }

    private boolean isWithinAmbushDropRange(EntityPlayer player) {
        if (player == null) {
            return false;
        }

        double dx = player.posX - posX;
        double dz = player.posZ - posZ;
        double horizontalDistanceSq = dx * dx + dz * dz;
        double verticalDistance = Math.abs((player.posY + player.getEyeHeight()) - posY);
        return horizontalDistanceSq <= HANG_DROP_TRIGGER_RANGE * HANG_DROP_TRIGGER_RANGE
                && verticalDistance <= HANG_DROP_VERTICAL_RANGE;
    }

    private int findCeilingBlockY(int x, int y, int z) {
        for (int offset = 1; offset < 8; offset++) {
            int checkY = y + offset;
            Block block = worldObj.getBlock(x, checkY, z);
            if (isReplaceableForWidow(block)) {
                continue;
            }
            return checkY;
        }

        return -1;
    }

    private int findClimbFaceAt(int x, int y, int z) {
        if (isSolidAnchorBlock(x, y, z - 1) || isSolidAnchorBlock(x, y + 1, z - 1)) {
            return CLIMB_FACE_NORTH;
        }
        if (isSolidAnchorBlock(x, y, z + 1) || isSolidAnchorBlock(x, y + 1, z + 1)) {
            return CLIMB_FACE_SOUTH;
        }
        if (isSolidAnchorBlock(x - 1, y, z) || isSolidAnchorBlock(x - 1, y + 1, z)) {
            return CLIMB_FACE_WEST;
        }
        if (isSolidAnchorBlock(x + 1, y, z) || isSolidAnchorBlock(x + 1, y + 1, z)) {
            return CLIMB_FACE_EAST;
        }
        return CLIMB_FACE_NONE;
    }

    private boolean isSolidAnchorBlock(int x, int y, int z) {
        Block block = worldObj.getBlock(x, y, z);
        return block != null && !isReplaceableForWidow(block) && block.getMaterial().isSolid();
    }

    private static boolean isReplaceableForWidow(Block block) {
        if (block == null) {
            return true;
        }
        Material material = block.getMaterial();
        return material == Material.air
                || material == Material.vine
                || material == Material.plants
                || material == Material.web;
    }

    private static double clampMotion(double value, double maxMagnitude) {
        if (value > maxMagnitude) {
            return maxMagnitude;
        }
        if (value < -maxMagnitude) {
            return -maxMagnitude;
        }
        return value;
    }

    private static double clampValue(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    @Override
    public void rf$writeSyncData(NBTTagCompound tag) {
        tag.setByte("WidowFlags", this.syncedFlags);
    }

    @Override
    public void rf$readSyncData(NBTTagCompound tag) {
        this.syncedFlags = tag.getByte("WidowFlags");
    }
}
