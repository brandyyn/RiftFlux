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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import com.voidsrift.riftflux.ModConfig;
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
        ensureSeats();
        if (!this.worldObj.isRemote) {
            tickSeatNoRemounts();
        }
        if (!this.worldObj.isRemote && ModConfig.appaAllowMobPassengers) {
            tryMountNearbyMob();
        }
        if (!(this.riddenByEntity instanceof EntityPlayer)) {
            this.controlUp = false;
            this.controlDown = false;
        }
        if (this.riddenByEntity == null && !this.onGround && !this.isInWater()) {
            this.motionY = Math.max(this.motionY, -0.1D);
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
        if (passenger instanceof EntityPlayer) {
            applyYawToPassenger(passenger);
        }
    }

    private void applyYawToPassenger(Entity passenger) {
        if (passenger instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) passenger;
            float yawDelta = MathHelper.wrapAngleTo180_float(living.rotationYawHead - this.rotationYaw);
            float clamped = MathHelper.clamp_float(yawDelta, -105.0F, 105.0F);
            float adjust = clamped - yawDelta;
            passenger.prevRotationYaw += adjust;
            passenger.rotationYaw += adjust;
            living.rotationYawHead += adjust;
            living.prevRotationYawHead += adjust;
            living.renderYawOffset = this.rotationYaw;
            living.prevRenderYawOffset = this.rotationYaw;
        } else {
            passenger.rotationYaw = this.rotationYaw;
            passenger.prevRotationYaw = this.rotationYaw;
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
            if (candidate instanceof EntityBison) {
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
        if (hasMobSeatPassenger()) {
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

}
