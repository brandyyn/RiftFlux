package com.voidsrift.riftflux.avatar.appa;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.ConfigResolver;
import net.minecraft.util.MathHelper;

public class EntityBison extends EntityFamiliar {
    private static final float APPA_WIDTH = 2.916f;
    private static final float APPA_HEIGHT = 3.0375f;
    public float tailAngle = 0.0f;
    private final float maxSpeed;
    private float currentSpeed;
    private double moveSpeedAir = 1.5;
    public float moveSpeedAirVert = 0.0f;
    private float yawAdd;
    private int yawSpeed = 30;
    private boolean controlUp;
    private boolean controlDown;
    private static final int PASSENGER_SEATS = 5;
    private static final int EJECT_NO_REMOUNT_TICKS = 40;
    private static final float DRIVER_FORWARD_OFFSET = 1.5f;
    private static final float DRIVER_UP_OFFSET = 0.75f;
    private static final float[][] SEAT_OFFSETS = new float[][]{
            {-0.6f, 0.1875f, -0.8f},
            {0.6f, 0.1875f, -0.8f},
            {-0.6f, 0.1875f, 0.4f},
            {0.6f, 0.1875f, 0.4f},
            {0.0f, 0.1875f, -1.9f}
    };
    private final EntityBisonSeat[] passengerSeats = new EntityBisonSeat[PASSENGER_SEATS];
    private final HashMap<Integer, Integer> seatNoRemountTicks = new HashMap<Integer, Integer>();
    private int lastDriverEntityId = -1;
    private boolean restoreYawAfterLoad = false;
    private float loadedYaw = 0.0F;

    public EntityBison(World world) {
        super(world);
        this.getNavigator().setAvoidsWater(true);
        this.preventEntitySpawning = true;
        this.ignoreFrustumCheck = true;
        this.setSize(APPA_WIDTH, APPA_HEIGHT);
        this.moveSpeedAir = getConfiguredMoveSpeed() * 0.32;
        this.moveSpeedAirVert = 0.25f;
        this.maxSpeed = (float) this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getBaseValue();
        this.currentSpeed = 0.0f;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(200.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(getConfiguredMoveSpeed());
        this.moveSpeedAir = getConfiguredMoveSpeed() * 0.32;
    }

    @Override
    public void moveEntityWithHeading(float strafeMovement, float forwardMovement) {
        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityLivingBase) {
            EntityLivingBase rider = (EntityLivingBase) this.riddenByEntity;
            float turnInput = rider.moveStrafing;
            if (Math.abs(turnInput) < 0.05f) {
                turnInput = 0.0f;
            }
            float forwardInput = rider.moveForward;
            this.prevRotationYaw = this.rotationYaw;
            this.rotationYaw -= turnInput * 3.0f;
            this.rotationPitch = 0.0f;
            this.setRotation(this.rotationYaw, this.rotationPitch);
            this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
            strafeMovement = 0.0f;
            forwardMovement = forwardInput;
            if (forwardMovement <= 0.0f) {
                forwardMovement *= 0.25f;
            }
            this.stepHeight = 1.0f;
            if (!this.worldObj.isRemote) {
                boolean wantsVerticalMove = this.controlUp || this.controlDown;
                if (wantsVerticalMove || !this.onGround) {
                    this.moveFlying(strafeMovement, forwardMovement, (float) this.moveSpeedAir);
                    float vertical = 0.0f;
                    float speed = this.moveSpeedAirVert > 0.0f ? this.moveSpeedAirVert : 0.25f;
                    if (this.controlUp) {
                        vertical += speed;
                    }
                    if (this.controlDown) {
                        vertical -= speed;
                    }
                    this.motionY = vertical;
                    this.moveEntity(this.motionX, this.motionY, this.motionZ);
                    this.motionX *= 0.9;
                    this.motionY *= 0.9;
                    this.motionZ *= 0.9;
                    this.fallDistance = 0.0f;
                    this.isAirBorne = true;
                } else {
                    this.setAIMoveSpeed(getConfiguredMoveSpeed());
                    super.moveEntityWithHeading(strafeMovement, forwardMovement);
                }
            }
            return;
        }
        super.moveEntityWithHeading(strafeMovement, forwardMovement);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.restoreYawAfterLoad && this.riddenByEntity == null && this.ticksExisted < 40) {
            this.rotationYaw = this.loadedYaw;
            this.prevRotationYaw = this.loadedYaw;
            this.rotationYawHead = this.loadedYaw;
            this.renderYawOffset = this.loadedYaw;
            this.prevRenderYawOffset = this.loadedYaw;
        } else if (this.restoreYawAfterLoad && (this.ticksExisted >= 40 || this.riddenByEntity != null)) {
            this.restoreYawAfterLoad = false;
        }
        ensureSeats();
        boolean playerOnBack = this.riddenByEntity == null && isAnyPlayerOnBackZone();
        if (playerOnBack) {
            // Keep client and server in sync while players stand on top.
            this.fallDistance = 0.0F;
            this.motionY = 0.0D;
        }
        boolean recallActive = false;
        if (!this.worldObj.isRemote) {
            tickSeatNoRemounts();
            if (playerOnBack) {
                stabilizePlayersOnBack();
            }
            recallActive = updateOwnerRecall(playerOnBack);
            updateDriverDismountSafety();
            if (playerOnBack) {
                if (Math.abs(this.motionX) < 0.01D) {
                    this.motionX = 0.0D;
                }
                if (Math.abs(this.motionZ) < 0.01D) {
                    this.motionZ = 0.0D;
                }
            }
        } else if (this.riddenByEntity == null && shouldClientHoverNearOwner()) {
            // Mirror server hover stop state.
            this.motionY = 0.0D;
            this.fallDistance = 0.0F;
        }
        if (!this.worldObj.isRemote && ModConfig.appaAllowMobPassengers) {
            tryMountNearbyMob();
        }
        if (!(this.riddenByEntity instanceof EntityPlayer)) {
            this.controlUp = false;
            this.controlDown = false;
        }
        boolean riderVerticalControl = this.riddenByEntity instanceof EntityPlayer && (this.controlUp || this.controlDown);
        if (!this.isInWater() && !riderVerticalControl) {
            if (this.worldObj.isRemote) {
                if (this.motionY < 0.0D) {
                    this.motionY = 0.0D;
                }
            } else if (!recallActive) {
                this.motionY = 0.0D;
            }
            this.fallDistance = 0.0f;
        }
    }

    public void setGlideControls(boolean up, boolean down) {
        this.controlUp = up;
        this.controlDown = down;
    }

    public void setMoveSpeedAir(double moveSpeedAir) {
        this.moveSpeedAir = moveSpeedAir;
    }

    public void setMoveSpeedAirVertical(float moveSpeedAirVert) {
        this.moveSpeedAirVert = moveSpeedAirVert;
    }

    private float getConfiguredMoveSpeed() {
        return Math.max(0.0f, ModConfig.appaMovementSpeed);
    }

    @Override
    protected boolean isAIEnabled() {
        return true;
    }

    @Override
    public Item getTameItem() {
        return Items.apple;
    }

    @Override
    public void setOwner(EntityPlayer ownerPlayer) {
        if (ownerPlayer == null) {
            return;
        }
        this.owner = ownerPlayer.getDisplayName();
        this.sendNameUpdate();
        this.sendHealthUpdate();
    }

    @Override
    public int getTotalArmorValue() {
        return 8;
    }

    public int getAttackStrength(Entity par1Entity) {
        return 10;
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEFINED;
    }

    @Override
    protected String getLivingSound() {
        return "avatarMobs.BisonRoar";
    }

    @Override
    protected String getHurtSound() {
        return "mob.cow.hurt";
    }

    @Override
    protected String getDeathSound() {
        return "avatarMobs.BisonDeath";
    }

    @Override
    protected void func_145780_a(int par1, int par2, int par3, Block par4) {
        this.worldObj.playSoundAtEntity(this, "mob.cow.step", 0.4f, 1.0f);
    }

    @Override
    protected Item getDropItem() {
        return this.isBurning() ? Items.cooked_beef : Items.beef;
    }

    @Override
    protected void dropRareDrop(int par1) {
        this.dropItem(Items.beef, 27);
    }

    @Override
    protected void dropFewItems(boolean par1, int par2) {
        this.dropItem(Items.beef, 27);
    }

    @Override
    protected void fall(float distance) {
        this.fallDistance = 0.0f;
    }

    @Override
    public void receivedMessage(String message) {
    }

    @Override
    public boolean interact(EntityPlayer player) {
        if (this.worldObj.isRemote) {
            return true;
        }
        boolean hasOwner = this.owner != null && !this.owner.isEmpty();
        ItemStack held = player.getHeldItem();
        if (!hasOwner && held != null && held.getItem() == this.getTameItem()) {
            --held.stackSize;
            Random r = this.getRNG();
            for (int i = 0; i < 7; ++i) {
                double vx = r.nextGaussian() * 202.0;
                double vy = r.nextGaussian() * 202.0;
                double vz = r.nextGaussian() * 202.0;
                this.worldObj.spawnParticle("heart",
                        this.posX + (double) (r.nextFloat() * this.width * 2.0f) - (double) this.width,
                        this.posY + 0.5 + (double) (r.nextFloat() * this.height),
                        this.posZ + (double) (r.nextFloat() * this.width * 2.0f) - (double) this.width,
                        vx, vy, vz);
            }
            this.setOwner(player);
            player.addChatComponentMessage(new ChatComponentText("Appa likes it! Hop on his back!"));
            return true;
        }

        if (ModConfig.appaRequireTameToRide && !hasOwner) {
            player.addChatComponentMessage(new ChatComponentText("Appa likes apples!"));
            return true;
        }
        if (ModConfig.appaRestrictRideToOwner && hasOwner) {
            String playerName = player.getDisplayName();
            if (playerName == null || !playerName.equals(this.owner)) {
                return true;
            }
        }
        if (player.ridingEntity != null) {
            return true;
        }
        if (this.riddenByEntity == null) {
            player.mountEntity(this);
            return true;
        }
        EntityBisonSeat seat = getAvailableSeat();
        if (seat != null) {
            player.mountEntity(seat);
        }
        return true;
    }

    @Override
    public void updateRiderPosition() {
        if (this.riddenByEntity != null) {
            updatePassenger(this.riddenByEntity);
        }
    }

    public void updatePassenger(Entity passenger) {
        if (passenger == null) {
            return;
        }
        if (passenger == this.riddenByEntity) {
            float yawRad = (float) Math.toRadians(this.rotationYaw);
            float sin = MathHelper.sin(yawRad);
            float cos = MathHelper.cos(yawRad);
            double px = this.posX - (double) (sin * DRIVER_FORWARD_OFFSET);
            double pz = this.posZ + (double) (cos * DRIVER_FORWARD_OFFSET);
            double py = this.posY + this.getMountedYOffset() + passenger.getYOffset() + DRIVER_UP_OFFSET;
            passenger.setPosition(px, py, pz);
            return;
        }
        float xOffset = 0.0f;
        float yOffset = 0.0f;
        float zOffset = 0.0f;
        if (passenger.ridingEntity instanceof EntityBisonSeat) {
            EntityBisonSeat seat = (EntityBisonSeat) passenger.ridingEntity;
            float[] offset = getSeatOffset(seat.getSeatIndex());
            xOffset = offset[0];
            yOffset = offset[1];
            zOffset = offset[2];
        }
        float yawRad = (float) Math.toRadians(this.rotationYaw);
        float sin = MathHelper.sin(yawRad);
        float cos = MathHelper.cos(yawRad);
        double px = this.posX + xOffset * cos - zOffset * sin;
        double pz = this.posZ + xOffset * sin + zOffset * cos;
        double py = this.posY + this.getMountedYOffset() + passenger.getYOffset() + yOffset;
        passenger.setPosition(px, py, pz);
        passenger.fallDistance = 0.0f;
        if (passenger instanceof EntityLivingBase && !(passenger instanceof EntityPlayer)) {
            EntityLivingBase living = (EntityLivingBase) passenger;
            living.moveStrafing = 0.0f;
            living.moveForward = 0.0f;
        }
    }

    @Override
    public void setDead() {
        super.setDead();
        if (!this.worldObj.isRemote) {
            clearSeats();
        }
    }

    private void ensureSeats() {
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }
        for (int i = 0; i < passengerSeats.length; i++) {
            EntityBisonSeat seat = passengerSeats[i];
            if (seat != null && (seat.isDead || seat.worldObj != this.worldObj)) {
                passengerSeats[i] = null;
                seat = null;
            }
            if (seat == null) {
                seat = new EntityBisonSeat(this.worldObj, this, i);
                seat.setPosition(this.posX, this.posY, this.posZ);
                if (this.worldObj.spawnEntityInWorld(seat)) {
                    passengerSeats[i] = seat;
                } else {
                    passengerSeats[i] = null;
                    continue;
                }
            } else {
                seat.setParent(this);
                seat.setSeatIndex(i);
                if (this.worldObj.getEntityByID(seat.getEntityId()) != seat) {
                    seat.setPosition(this.posX, this.posY, this.posZ);
                    if (!this.worldObj.spawnEntityInWorld(seat)) {
                        passengerSeats[i] = null;
                    }
                }
            }
        }
    }

    private void clearSeats() {
        for (int i = 0; i < passengerSeats.length; i++) {
            EntityBisonSeat seat = passengerSeats[i];
            if (seat != null) {
                seat.setDead();
                passengerSeats[i] = null;
            }
        }
    }

    private EntityBisonSeat getAvailableSeat() {
        ensureSeats();
        for (EntityBisonSeat seat : passengerSeats) {
            if (isSeatLive(seat) && seat.riddenByEntity == null) {
                return seat;
            }
        }
        return null;
    }

    public float[] getSeatOffset(int seatIndex) {
        if (seatIndex < 0 || seatIndex >= SEAT_OFFSETS.length) {
            return new float[]{0.0f, 0.0f, 0.0f};
        }
        return SEAT_OFFSETS[seatIndex];
    }

    private void tryMountNearbyMob() {
        boolean hasOwner = this.owner != null && !this.owner.isEmpty();
        if (ModConfig.appaRequireTameToRide && !hasOwner) {
            return;
        }
        if (!isStandingStillForMobMount()) {
            return;
        }
        EntityBisonSeat seat = getAvailableSeat();
        if (seat == null) {
            return;
        }
        java.util.List entities = this.worldObj.getEntitiesWithinAABBExcludingEntity(
                this, this.boundingBox.expand(0.6D, 0.6D, 0.6D));
        for (Object obj : entities) {
            if (!(obj instanceof EntityLivingBase)) {
                continue;
            }
            EntityLivingBase candidate = (EntityLivingBase) obj;
            if (candidate instanceof EntityPlayer) {
                continue;
            }
            if (candidate.ridingEntity instanceof EntityBisonSeat) {
                EntityBisonSeat ridingSeat = (EntityBisonSeat) candidate.ridingEntity;
                if (!isSeatLive(ridingSeat) || ridingSeat.getParent() == null) {
                    candidate.mountEntity(null);
                }
            }
            if (candidate.isDead || candidate.ridingEntity != null || candidate.riddenByEntity != null) {
                continue;
            }
            if (!isMobPassengerAllowed(candidate)) {
                continue;
            }
            if (isInNoRemountCooldown(candidate)) {
                continue;
            }
            if (!isSeatLive(seat)) {
                return;
            }
            candidate.mountEntity(seat);
            if (candidate.ridingEntity == seat) {
                updatePassenger(candidate);
            }
            break;
        }
    }

    private boolean isStandingStillForMobMount() {
        double motionSq = this.motionX * this.motionX + this.motionZ * this.motionZ;
        double deltaX = this.posX - this.prevPosX;
        double deltaZ = this.posZ - this.prevPosZ;
        double deltaSq = deltaX * deltaX + deltaZ * deltaZ;
        return motionSq < 9.0E-4D && deltaSq < 9.0E-4D;
    }

    private boolean isMobPassengerAllowed(EntityLivingBase entity) {
        if (entity == null) {
            return false;
        }
        String[] filter = ModConfig.appaMobPassengerEntityFilter;
        boolean whitelist = ModConfig.appaMobPassengerWhitelistMode;
        if (filter == null || filter.length == 0) {
            return !whitelist;
        }
        boolean match = ConfigResolver.matchesConfiguredEntity(entity, filter);
        return whitelist ? match : !match;
    }

    public boolean ejectSeatPassenger(Entity passenger) {
        if (passenger == null) {
            return false;
        }
        if (!(passenger.ridingEntity instanceof EntityBisonSeat)) {
            return false;
        }
        EntityBisonSeat seat = (EntityBisonSeat) passenger.ridingEntity;
        if (seat.getParent() != this) {
            return false;
        }
        passenger.mountEntity(null);
        setNoRemountCooldown(passenger, EJECT_NO_REMOUNT_TICKS);
        if (!this.worldObj.isRemote) {
            float yawRad = (float) Math.toRadians(this.rotationYaw);
            double dropX = this.posX - MathHelper.sin(yawRad) * 1.2D;
            double dropZ = this.posZ + MathHelper.cos(yawRad) * 1.2D;
            double dropY = this.posY + this.getMountedYOffset() + passenger.getYOffset() + 0.2D;
            passenger.setPosition(dropX, dropY, dropZ);
            passenger.motionX = this.motionX;
            passenger.motionY = 0.08D;
            passenger.motionZ = this.motionZ;
            passenger.velocityChanged = true;
        }
        return true;
    }

    public boolean ejectClosestSeatPassenger(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        Entity closest = null;
        double closestDist = Double.MAX_VALUE;
        for (EntityBisonSeat seat : passengerSeats) {
            if (!isSeatLive(seat) || seat.riddenByEntity == null) {
                continue;
            }
            Entity candidate = seat.riddenByEntity;
            if (candidate instanceof EntityPlayer) {
                continue;
            }
            double dist = candidate.getDistanceSqToEntity(player);
            if (dist < closestDist) {
                closestDist = dist;
                closest = candidate;
            }
        }
        if (closest == null) {
            return false;
        }
        return ejectSeatPassenger(closest);
    }

    private boolean isSeatLive(EntityBisonSeat seat) {
        return seat != null
                && !seat.isDead
                && seat.worldObj == this.worldObj
                && this.worldObj.getEntityByID(seat.getEntityId()) == seat;
    }

    private void tickSeatNoRemounts() {
        if (seatNoRemountTicks.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<Integer, Integer>> iterator = seatNoRemountTicks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            int next = entry.getValue().intValue() - 1;
            if (next <= 0) {
                iterator.remove();
            } else {
                entry.setValue(Integer.valueOf(next));
            }
        }
    }

    private boolean isInNoRemountCooldown(Entity entity) {
        if (entity == null) {
            return false;
        }
        Integer ticks = seatNoRemountTicks.get(Integer.valueOf(entity.getEntityId()));
        return ticks != null && ticks.intValue() > 0;
    }

    private void setNoRemountCooldown(Entity entity, int ticks) {
        if (entity == null || ticks <= 0) {
            return;
        }
        seatNoRemountTicks.put(Integer.valueOf(entity.getEntityId()), Integer.valueOf(ticks));
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity) {
        if (entity == this.riddenByEntity || isSeatPassenger(entity) || isSeatEntity(entity)) {
            return null;
        }
        if (entity instanceof EntityPlayer && entity.ridingEntity == null && isEntityOnBackZone(entity)) {
            // Keep only a thin "deck" collider for standing players to avoid side-collision shove jitter.
            return AxisAlignedBB.getBoundingBox(
                    this.boundingBox.minX,
                    this.boundingBox.maxY - 0.10D,
                    this.boundingBox.minZ,
                    this.boundingBox.maxX,
                    this.boundingBox.maxY + 0.24D,
                    this.boundingBox.maxZ
            );
        }
        return this.boundingBox;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public void applyEntityCollision(Entity entity) {
        if (entity == this.riddenByEntity
                || isSeatPassenger(entity)
                || isSeatEntity(entity)
                || isEntityOnBackZone(entity)
                || hasMobSeatPassenger()) {
            return;
        }
        super.applyEntityCollision(entity);
    }

    private boolean isSeatPassenger(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (!(entity.ridingEntity instanceof EntityBisonSeat)) {
            return false;
        }
        EntityBisonSeat seat = (EntityBisonSeat) entity.ridingEntity;
        return seat.getParent() == this;
    }

    private boolean isSeatEntity(Entity entity) {
        if (!(entity instanceof EntityBisonSeat)) {
            return false;
        }
        EntityBisonSeat seat = (EntityBisonSeat) entity;
        return seat.getParent() == this;
    }

    @Override
    public void addVelocity(double x, double y, double z) {
        if (hasMobSeatPassenger() || isAnyPlayerOnBackZone()) {
            return;
        }
        super.addVelocity(x, y, z);
    }

    private boolean hasMobSeatPassenger() {
        for (EntityBisonSeat seat : passengerSeats) {
            if (!isSeatLive(seat)) {
                continue;
            }
            if (seat.riddenByEntity instanceof EntityLivingBase && !(seat.riddenByEntity instanceof EntityPlayer)) {
                return true;
            }
        }
        return false;
    }

    private boolean updateOwnerRecall(boolean playerOnBack) {
        if (this.ticksExisted < 40) {
            return false;
        }
        if (this.riddenByEntity != null || this.ridingEntity != null) {
            return false;
        }
        if (this.onGround || this.isInWater()) {
            return false;
        }
        if (this.owner == null || this.owner.isEmpty()) {
            return false;
        }
        if (playerOnBack || isAnyPlayerOnBackZone()) {
            cancelRecallMotion();
            return false;
        }
        EntityPlayer ownerPlayer = this.worldObj.getPlayerEntityByName(this.owner);
        if (ownerPlayer == null || ownerPlayer.isDead) {
            cancelRecallMotion();
            return false;
        }
        double ownerFeetY = ownerPlayer.boundingBox != null ? ownerPlayer.boundingBox.minY : ownerPlayer.posY;
        if (ownerFeetY >= this.boundingBox.minY - 0.2D) {
            // Only recall when owner is below Appa.
            cancelRecallMotion();
            return false;
        }
        double dx = ownerPlayer.posX - this.posX;
        double dz = ownerPlayer.posZ - this.posZ;
        double horizontalSq = dx * dx + dz * dz;
        double horizontalDistance = Math.sqrt(horizontalSq);
        double verticalGap = this.boundingBox.minY - ownerFeetY;
        final double horizontalStopRadius = 6.0D;
        final double verticalStopGap = 6.0D;

        if (horizontalDistance <= horizontalStopRadius) {
            // Once close enough horizontally, stop drifting toward the owner.
            this.motionX *= 0.6D;
            this.motionZ *= 0.6D;
            if (Math.abs(this.motionX) < 0.01D) {
                this.motionX = 0.0D;
            }
            if (Math.abs(this.motionZ) < 0.01D) {
                this.motionZ = 0.0D;
            }

            if (verticalGap <= verticalStopGap) {
                // Close enough: stop descending to avoid down-then-snap behavior.
                this.motionY = 0.0D;
                this.fallDistance = 0.0f;
                return true;
            }

            // Close on X/Z, but still high above owner: descend only.
            double targetDescent = -Math.max(0.003D, getConfiguredMoveSpeed() * 0.08D);
            this.motionY += (targetDescent - this.motionY) * 0.1D;
            if (this.motionY < -0.02D) {
                this.motionY = -0.02D;
            }
            this.fallDistance = 0.0f;
            this.velocityChanged = true;
            return true;
        }

        double followRadius = 2.75D;
        float targetYaw = (float) (Math.atan2(-dx, dz) * 180.0D / Math.PI);
        this.rotationYaw = approachYaw(this.rotationYaw, targetYaw, 2.25f);
        this.renderYawOffset = this.rotationYaw;
        this.rotationYawHead = this.rotationYaw;
        if (horizontalSq > 1.0E-6D) {
            double travel = Math.max(0.0D, horizontalDistance - followRadius);
            double maxHorizontal = Math.max(0.005D, getConfiguredMoveSpeed() * 0.45D);
            double desiredSpeed = Math.min(maxHorizontal, travel * 0.045D);
            double horizontal = horizontalDistance;
            double desiredX = (dx / horizontal) * desiredSpeed;
            double desiredZ = (dz / horizontal) * desiredSpeed;
            this.motionX += (desiredX - this.motionX) * 0.12D;
            this.motionZ += (desiredZ - this.motionZ) * 0.12D;
            double motionHorizontalSq = this.motionX * this.motionX + this.motionZ * this.motionZ;
            if (motionHorizontalSq > maxHorizontal * maxHorizontal) {
                double scale = maxHorizontal / Math.sqrt(motionHorizontalSq);
                this.motionX *= scale;
                this.motionZ *= scale;
            }
            if (horizontalDistance <= followRadius + 0.3D) {
                this.motionX *= 0.75D;
                this.motionZ *= 0.75D;
            }
        } else {
            this.motionX *= 0.75D;
            this.motionZ *= 0.75D;
        }
        double targetDescent = -Math.max(0.003D, getConfiguredMoveSpeed() * 0.08D);
        this.motionY += (targetDescent - this.motionY) * 0.1D;
        if (this.motionY < -0.02D) {
            this.motionY = -0.02D;
        }
        this.fallDistance = 0.0f;
        this.velocityChanged = true;
        return true;
    }

    private void cancelRecallMotion() {
        this.motionX *= 0.6D;
        this.motionZ *= 0.6D;
        if (Math.abs(this.motionX) < 0.01D) {
            this.motionX = 0.0D;
        }
        if (Math.abs(this.motionZ) < 0.01D) {
            this.motionZ = 0.0D;
        }
        this.motionY = 0.0D;
        this.fallDistance = 0.0f;
    }

    private boolean isAlmostStationaryInAir() {
        double horizontalMotionSq = this.motionX * this.motionX + this.motionZ * this.motionZ;
        double horizontalDeltaX = this.posX - this.prevPosX;
        double horizontalDeltaZ = this.posZ - this.prevPosZ;
        double horizontalDeltaSq = horizontalDeltaX * horizontalDeltaX + horizontalDeltaZ * horizontalDeltaZ;
        return horizontalMotionSq < 1.0E-4D && horizontalDeltaSq < 1.0E-4D && Math.abs(this.motionY) < 1.0E-3D;
    }

    private boolean isEntityOnBackZone(Entity entity) {
        if (entity == null || entity.boundingBox == null) {
            return false;
        }
        AxisAlignedBB backZone = AxisAlignedBB.getBoundingBox(
                this.boundingBox.minX - 0.25D,
                this.boundingBox.maxY - 1.25D,
                this.boundingBox.minZ - 0.25D,
                this.boundingBox.maxX + 0.25D,
                this.boundingBox.maxY + 1.6D,
                this.boundingBox.maxZ + 0.25D
        );
        return entity.boundingBox.intersectsWith(backZone);
    }

    private boolean isAnyPlayerOnBackZone() {
        if (this.worldObj == null || this.worldObj.playerEntities == null) {
            return false;
        }
        for (Object obj : this.worldObj.playerEntities) {
            if (!(obj instanceof EntityPlayer)) {
                continue;
            }
            EntityPlayer player = (EntityPlayer) obj;
            if (player.isDead || player.ridingEntity != null) {
                continue;
            }
            if (isEntityOnBackZone(player)) {
                return true;
            }
        }
        return false;
    }

    private void stabilizePlayersOnBack() {
        if (this.worldObj == null || this.worldObj.playerEntities == null) {
            return;
        }
        double targetFeetY = this.boundingBox.maxY + 0.02D;
        for (Object obj : this.worldObj.playerEntities) {
            if (!(obj instanceof EntityPlayer)) {
                continue;
            }
            EntityPlayer player = (EntityPlayer) obj;
            if (player.isDead || player.ridingEntity != null) {
                continue;
            }
            if (!isEntityOnBackZone(player)) {
                continue;
            }
            if (player.boundingBox != null && player.boundingBox.minY < targetFeetY - 0.02D) {
                player.setPosition(player.posX, targetFeetY, player.posZ);
                // Prevent server-side interpolation spikes after relog correction.
                player.prevPosY = targetFeetY;
                player.lastTickPosY = targetFeetY;
            }
            if (player.motionY < 0.0D) {
                player.motionY = 0.0D;
            }
            if (Math.abs(player.motionX) < 0.005D) {
                player.motionX = 0.0D;
            }
            if (Math.abs(player.motionZ) < 0.005D) {
                player.motionZ = 0.0D;
            }
            player.fallDistance = 0.0F;
        }
    }

    private float approachYaw(float current, float target, float step) {
        float delta = MathHelper.wrapAngleTo180_float(target - current);
        if (delta > step) {
            delta = step;
        } else if (delta < -step) {
            delta = -step;
        }
        return current + delta;
    }

    private void updateDriverDismountSafety() {
        if (this.riddenByEntity != null) {
            this.lastDriverEntityId = this.riddenByEntity.getEntityId();
            return;
        }
        if (this.lastDriverEntityId < 0) {
            return;
        }
        Entity formerDriver = this.worldObj.getEntityByID(this.lastDriverEntityId);
        this.lastDriverEntityId = -1;
        if (formerDriver == null || formerDriver.isDead || formerDriver.ridingEntity != null) {
            return;
        }
        placeDismountedDriverSafely(formerDriver);
    }

    private void placeDismountedDriverSafely(Entity driver) {
        if (driver == null || driver.boundingBox == null) {
            return;
        }
        double x = this.posX;
        double z = this.posZ;
        double y = this.boundingBox.maxY + 1.0D;
        if (this.motionY > 0.02D) {
            y += Math.min(1.25D, this.motionY * 8.0D + 0.35D);
        }
        for (int i = 0; i < 8; i++) {
            double testY = y + i * 0.25D;
            if (isClearDismountSpot(driver, x, testY, z)) {
                applyDismountPosition(driver, x, testY, z);
                return;
            }
        }
        applyDismountPosition(driver, x, y + 1.0D, z);
    }

    private boolean isClearDismountSpot(Entity entity, double x, double y, double z) {
        AxisAlignedBB box = entity.boundingBox.copy().offset(
                x - entity.posX,
                y - entity.posY,
                z - entity.posZ
        );
        return this.worldObj.getCollidingBoundingBoxes(entity, box).isEmpty();
    }

    private void applyDismountPosition(Entity driver, double x, double y, double z) {
        driver.setPosition(x, y, z);
        driver.motionX = this.motionX * 0.9D;
        driver.motionZ = this.motionZ * 0.9D;
        driver.motionY = Math.max(driver.motionY, this.motionY + 0.06D);
        driver.fallDistance = 0.0f;
        driver.velocityChanged = true;
    }

    @Override
    public void setPositionAndUpdate(double x, double y, double z) {
        if (shouldBlockOwnerTeleport(x, y, z)) {
            return;
        }
        super.setPositionAndUpdate(x, y, z);
    }

    @Override
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int increments) {
        if (shouldBlockOwnerTeleport(x, y, z)) {
            return;
        }
        super.setPositionAndRotation2(x, y, z, yaw, pitch, increments);
    }

    private boolean shouldBlockOwnerTeleport(double targetX, double targetY, double targetZ) {
        if (this.worldObj == null || this.worldObj.isRemote) {
            return false;
        }
        if (this.ticksExisted < 40) {
            return false;
        }
        if (this.ridingEntity != null || this.riddenByEntity != null) {
            return false;
        }
        if (this.owner == null || this.owner.isEmpty()) {
            return false;
        }
        EntityPlayer ownerPlayer = this.worldObj.getPlayerEntityByName(this.owner);
        if (ownerPlayer == null) {
            return false;
        }
        double targetToOwnerSq = ownerPlayer.getDistanceSq(targetX, targetY, targetZ);
        if (targetToOwnerSq > 36.0D) {
            return false;
        }
        double currentToOwnerSq = this.getDistanceSqToEntity(ownerPlayer);
        return currentToOwnerSq > 1024.0D;
    }

    private boolean shouldClientHoverNearOwner() {
        if (this.worldObj == null || !this.worldObj.isRemote) {
            return false;
        }
        if (this.onGround || this.isInWater()) {
            return false;
        }
        if (this.owner == null || this.owner.isEmpty()) {
            return false;
        }
        EntityPlayer ownerPlayer = this.worldObj.getPlayerEntityByName(this.owner);
        if (ownerPlayer == null || ownerPlayer.isDead) {
            return false;
        }
        double ownerFeetY = ownerPlayer.boundingBox != null ? ownerPlayer.boundingBox.minY : ownerPlayer.posY;
        if (ownerFeetY >= this.boundingBox.minY - 0.2D) {
            return false;
        }
        double dx = ownerPlayer.posX - this.posX;
        double dz = ownerPlayer.posZ - this.posZ;
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        double verticalGap = this.boundingBox.minY - ownerFeetY;
        return horizontalDistance <= 6.0D && verticalGap <= 6.0D;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.loadedYaw = this.rotationYaw;
        this.restoreYawAfterLoad = true;
    }

}
