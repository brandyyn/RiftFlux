package com.voidsrift.riftflux.chester;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.riftflux;
import com.voidsrift.riftflux.net.sync.EntitySyncHelper;
import com.voidsrift.riftflux.net.sync.IEntitySyncData;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import java.lang.ref.WeakReference;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class EntityChester extends EntityCreature implements IEntitySyncData, IEntityAdditionalSpawnData {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_SHADOW = 2;

    private static final int TYPE_MASK = 3;
    private static final int OPEN_FLAG = 4;
    private static final int SITTING_FLAG = 8;
    private static final int HAPPY_PANT_FLAG = 16;
    private static final int ANCHOR_IDLE_NONE = 0;
    private static final int ANCHOR_IDLE_STAY = 1;
    private static final int ANCHOR_IDLE_SIT = 2;
    private static final int ANCHOR_IDLE_CIRCLE = 3;

    private ChesterInventory inventory;
    private int packedState;
    private String ownerId = "";
    private WeakReference<EntityPlayer> staffHolderReference;
    private long staffHolderSeenAt = Long.MIN_VALUE;
    private boolean staffHolderPersistent;
    private double staffAnchorX;
    private double staffAnchorY;
    private double staffAnchorZ;
    private boolean hasStaffAnchor;
    private float previousSitProgress;
    private float sitProgress;
    private float previousMouthOpenProgress;
    private float mouthOpenProgress;
    private boolean registeredBinding;
    private boolean commandedSitting;
    private int anchorIdleMode;
    private int anchorIdleTicks;
    private int anchorIdleDelay = 60;
    private float anchorCircleAngle;
    private float anchorCircleDirection;
    private double anchorCircleRadius;
    private int nextPathRecalculationTick;
    private int happyPantTicks;
    private int happyPantSoundCooldown;

    public EntityChester(World world) {
        super(world);
        setSize(0.5F, 1.0F);
        stepHeight = 0.5F;
        getNavigator().setAvoidsWater(false);
        getNavigator().setCanSwim(true);
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(9, new EntityAILookIdle(this));
        setupInventory();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.4D);
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    @Override
    public int getTalkInterval() {
        return 1;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public boolean interact(EntityPlayer player) {
        if (!isOwner(player)) {
            return false;
        }

        ItemStack held = player.getCurrentEquippedItem();
        if (held != null && held.getItem() == Items.name_tag && held.hasDisplayName()) {
            if (!worldObj.isRemote) {
                setCustomNameTag(held.getDisplayName());
                if (!player.capabilities.isCreativeMode) {
                    --held.stackSize;
                    if (held.stackSize <= 0) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    }
                }
            }
            return true;
        }

        if (player.isSneaking() && held == null) {
            if (!worldObj.isRemote) {
                setSitting(!commandedSitting);
                getNavigator().clearPathEntity();
            }
            return true;
        }

        if (!worldObj.isRemote) {
            player.openGui(
                    riftflux.instance,
                    ChesterContent.GUI_ID,
                    worldObj,
                    getEntityId(),
                    0,
                    0
            );
            if (player.openContainer instanceof ContainerChester) {
                setOpen(true);
                worldObj.playSoundEffect(posX, posY, posZ, "chester:chesteropen", 0.3F, 1.0F);
                worldObj.playSoundEffect(posX, posY, posZ, "chester:chestopen", 0.3F, 1.0F);
            }
        }
        return true;
    }

    @Override
    protected String getLivingSound() {
        return "chester:chesterbreathe";
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    public void onStruckByLightning(EntityLightningBolt lightning) {
        if (!worldObj.isRemote && getChesterType() != TYPE_SHADOW) {
            setChesterType(TYPE_SHADOW);
        }
        extinguish();
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        previousSitProgress = sitProgress;
        if (isSitting()) {
            sitProgress = Math.min(1.0F, sitProgress + 0.18F);
        } else {
            sitProgress = Math.max(0.0F, sitProgress - 0.18F);
        }
        previousMouthOpenProgress = mouthOpenProgress;
        if (isOpen()) {
            mouthOpenProgress = Math.min(1.0F, mouthOpenProgress + 0.15F);
        } else {
            mouthOpenProgress = Math.max(0.0F, mouthOpenProgress - 0.15F);
        }
        if (worldObj.isRemote) {
            return;
        }

        updateHappyPanting();

        if (!registeredBinding) {
            ChesterBinding.register(this);
            registeredBinding = true;
        }
        extinguish();

        long now = worldObj.getTotalWorldTime();
        EntityPlayer staffHolder = staffHolderReference == null ? null : staffHolderReference.get();
        boolean hasCurrentHolder = staffHolder != null
                && !staffHolder.isDead
                && staffHolder.worldObj == worldObj
                && (staffHolderPersistent || now - staffHolderSeenAt <= 15L);

        if (hasCurrentHolder) {
            cancelAnchorIdle();
            setVisualSitting(commandedSitting);
            double teleportDistance = Math.max(0.0D, ModConfig.chesterTeleportDistance);
            if (!commandedSitting
                    && teleportDistance > 0.0D
                    && getDistanceSqToEntity(staffHolder) >= teleportDistance * teleportDistance
                    && tryTeleportNear(staffHolder)) {
                getNavigator().clearPathEntity();
            } else {
                moveNear(staffHolder.posX, staffHolder.posY, staffHolder.posZ);
            }
        } else {
            staffHolderReference = null;
            if (hasStaffAnchor) {
                updateAnchorIdle();
            } else {
                cancelAnchorIdle();
                setVisualSitting(commandedSitting);
                getNavigator().clearPathEntity();
            }
        }

        if (!isSitting() && onGround && isCollidedHorizontally && !getNavigator().noPath()) {
            getJumpHelper().setJumping();
        }
    }

    public void markStaffHeldBy(EntityPlayer player) {
        markStaffHeldBy(player, false);
    }

    public void markStaffHeldBy(EntityPlayer player, boolean persistent) {
        EntityPlayer currentHolder = staffHolderReference == null ? null : staffHolderReference.get();
        if (currentHolder != player) {
            staffHolderReference = new WeakReference<EntityPlayer>(player);
        }
        staffHolderSeenAt = worldObj.getTotalWorldTime();
        staffHolderPersistent = persistent;
        setStaffAnchor(player.posX, player.posY, player.posZ);
    }

    public void clearStaffHeldBy(EntityPlayer player) {
        EntityPlayer currentHolder = staffHolderReference == null ? null : staffHolderReference.get();
        if (currentHolder == player) {
            staffHolderReference = null;
            staffHolderPersistent = false;
        }
    }

    public void setStaffAnchor(double x, double y, double z) {
        staffAnchorX = x;
        staffAnchorY = y;
        staffAnchorZ = z;
        hasStaffAnchor = true;
    }

    private void moveNear(double x, double y, double z) {
        if (isSitting()) {
            if (!getNavigator().noPath()) {
                getNavigator().clearPathEntity();
            }
            return;
        }
        double dx = posX - x;
        double dy = posY - y;
        double dz = posZ - z;
        if (dx * dx + dy * dy + dz * dz > 16.0D) {
            if (ticksExisted >= nextPathRecalculationTick) {
                getNavigator().tryMoveToXYZ(x, y, z, 1.0D);
                nextPathRecalculationTick = ticksExisted + 10;
            }
        } else if (!getNavigator().noPath()) {
            getNavigator().clearPathEntity();
        }
    }

    private boolean tryTeleportNear(EntityPlayer player) {
        int centerX = MathHelper.floor_double(player.posX);
        int centerY = MathHelper.floor_double(player.boundingBox.minY);
        int centerZ = MathHelper.floor_double(player.posZ);

        for (int attempt = 0; attempt < 24; attempt++) {
            int offsetX = rand.nextInt(7) - 3;
            int offsetZ = rand.nextInt(7) - 3;
            if (Math.abs(offsetX) < 2 && Math.abs(offsetZ) < 2) {
                continue;
            }

            int x = centerX + offsetX;
            int z = centerZ + offsetZ;
            if (!worldObj.getBlock(x, centerY - 1, z).getMaterial().blocksMovement()
                    || worldObj.getBlock(x, centerY, z).getMaterial().blocksMovement()
                    || worldObj.getBlock(x, centerY + 1, z).getMaterial().blocksMovement()) {
                continue;
            }

            double targetX = x + 0.5D;
            double targetY = centerY;
            double targetZ = z + 0.5D;
            AxisAlignedBB targetBox = AxisAlignedBB.getBoundingBox(
                    targetX - width / 2.0F,
                    targetY,
                    targetZ - width / 2.0F,
                    targetX + width / 2.0F,
                    targetY + height,
                    targetZ + width / 2.0F
            );
            if (!worldObj.getCollidingBoundingBoxes(this, targetBox).isEmpty()
                    || worldObj.isAnyLiquid(targetBox)) {
                continue;
            }

            setLocationAndAngles(targetX, targetY, targetZ, rotationYaw, rotationPitch);
            fallDistance = 0.0F;
            nextPathRecalculationTick = ticksExisted;
            return true;
        }
        return false;
    }

    @Override
    public void setDead() {
        ChesterBinding.unregister(this);
        super.setDead();
    }

    private void updateAnchorIdle() {
        double dx = posX - staffAnchorX;
        double dy = posY - staffAnchorY;
        double dz = posZ - staffAnchorZ;
        double distanceSq = dx * dx + dy * dy + dz * dz;

        if (commandedSitting) {
            cancelAnchorIdle();
            setVisualSitting(true);
            getNavigator().clearPathEntity();
            return;
        }
        if (distanceSq > 16.0D) {
            cancelAnchorIdle();
            setVisualSitting(false);
            moveNear(staffAnchorX, staffAnchorY, staffAnchorZ);
            return;
        }

        if (anchorIdleTicks > 0) {
            --anchorIdleTicks;
            if (anchorIdleMode == ANCHOR_IDLE_SIT) {
                setVisualSitting(true);
                getNavigator().clearPathEntity();
            } else if (anchorIdleMode == ANCHOR_IDLE_CIRCLE) {
                setVisualSitting(false);
                if (anchorIdleTicks % 8 == 0) {
                    anchorCircleAngle += anchorCircleDirection * 0.28F;
                    double targetX = staffAnchorX + Math.cos(anchorCircleAngle) * anchorCircleRadius;
                    double targetZ = staffAnchorZ + Math.sin(anchorCircleAngle) * anchorCircleRadius;
                    getNavigator().tryMoveToXYZ(targetX, staffAnchorY, targetZ, 0.72D);
                }
            } else {
                setVisualSitting(false);
                getNavigator().clearPathEntity();
            }
            return;
        }

        anchorIdleMode = ANCHOR_IDLE_NONE;
        setVisualSitting(false);
        getNavigator().clearPathEntity();
        if (anchorIdleDelay > 0) {
            --anchorIdleDelay;
            return;
        }
        beginAnchorIdleBehavior();
    }

    private void beginAnchorIdleBehavior() {
        int roll = rand.nextInt(100);
        if (roll < 55) {
            anchorIdleMode = ANCHOR_IDLE_CIRCLE;
            int laps = 2 + rand.nextInt(3);
            anchorCircleDirection = rand.nextBoolean() ? 1.0F : -1.0F;
            anchorCircleRadius = 2.25D + rand.nextDouble() * 1.25D;
            anchorCircleAngle = (float) Math.atan2(posZ - staffAnchorZ, posX - staffAnchorX);
            int stepsPerLap = (int) Math.ceil(Math.PI * 2.0D / 0.28D);
            anchorIdleTicks = laps * stepsPerLap * 8;
        } else if (roll < 78) {
            anchorIdleMode = ANCHOR_IDLE_SIT;
            anchorIdleTicks = 80 + rand.nextInt(121);
        } else {
            anchorIdleMode = ANCHOR_IDLE_STAY;
            anchorIdleTicks = 50 + rand.nextInt(91);
        }
        anchorIdleDelay = 80 + rand.nextInt(201);
    }

    private void cancelAnchorIdle() {
        anchorIdleMode = ANCHOR_IDLE_NONE;
        anchorIdleTicks = 0;
    }

    @Override
    protected boolean isMovementBlocked() {
        return isSitting() || super.isMovementBlocked();
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    public float getSitAnimationProgress(float partialTicks) {
        return previousSitProgress + (sitProgress - previousSitProgress) * partialTicks;
    }

    public float getMouthOpenAnimationProgress(float partialTicks) {
        float progress = previousMouthOpenProgress
                + (mouthOpenProgress - previousMouthOpenProgress) * partialTicks;
        float remaining = 1.0F - progress;
        return 1.0F - remaining * remaining * remaining;
    }

    public ChesterInventory getInventory() {
        return inventory;
    }

    public int getInventorySize() {
        return 27;
    }

    public int getInventoryRows() {
        return 3;
    }

    public int getChesterType() {
        return packedState & TYPE_MASK;
    }

    public void setChesterType(int type) {
        setPackedState((packedState & ~TYPE_MASK) | (type & TYPE_MASK));
    }

    @Override
    public String getCommandSenderName() {
        if (hasCustomNameTag()) {
            return getCustomNameTag();
        }
        return StatCollector.translateToLocal(
                getChesterType() == TYPE_SHADOW
                        ? "entity.riftflux.ShadowChester.name"
                        : "entity.riftflux.Chester.name"
        );
    }

    public boolean isOpen() {
        return (packedState & (OPEN_FLAG | HAPPY_PANT_FLAG)) != 0;
    }

    public void setOpen(boolean open) {
        setPackedState(open ? packedState | OPEN_FLAG : packedState & ~OPEN_FLAG);
    }

    private void updateHappyPanting() {
        if (happyPantTicks > 0) {
            --happyPantTicks;
            if (--happyPantSoundCooldown <= 0) {
                playSound("chester:chesterpant", 0.4F, 1.0F);
                happyPantSoundCooldown = 18 + rand.nextInt(13);
            }
            if (happyPantTicks <= 0) {
                setHappyPanting(false);
            }
            return;
        }

        if ((packedState & OPEN_FLAG) == 0 && rand.nextInt(500) == 0) {
            happyPantTicks = 70 + rand.nextInt(91);
            happyPantSoundCooldown = 1;
            setHappyPanting(true);
        }
    }

    private void setHappyPanting(boolean panting) {
        setPackedState(panting
                ? packedState | HAPPY_PANT_FLAG
                : packedState & ~HAPPY_PANT_FLAG);
    }

    public boolean isSitting() {
        return (packedState & SITTING_FLAG) != 0;
    }

    public void setSitting(boolean sitting) {
        commandedSitting = sitting;
        cancelAnchorIdle();
        setVisualSitting(sitting);
    }

    private void setVisualSitting(boolean sitting) {
        setPackedState(sitting ? packedState | SITTING_FLAG : packedState & ~SITTING_FLAG);
        if (sitting) {
            getNavigator().clearPathEntity();
            motionX = 0.0D;
            motionZ = 0.0D;
        }
    }

    public boolean isTamed() {
        return !ownerId.isEmpty();
    }

    public void setTamed(boolean tamed) {
        if (!tamed) {
            ownerId = "";
            syncState();
        }
    }

    public void func_152115_b(String id) {
        ownerId = id == null ? "" : id;
        syncState();
    }

    @SuppressWarnings("unchecked")
    public EntityPlayer getOwner() {
        if (ownerId.isEmpty() || worldObj == null) {
            return null;
        }
        for (EntityPlayer player : (java.util.List<EntityPlayer>) worldObj.playerEntities) {
            if (ownerId.equals(player.getUniqueID().toString())) {
                return player;
            }
        }
        return null;
    }

    public boolean canPlayerUseInventory(EntityPlayer player) {
        return isEntityAlive()
                && isOwner(player)
                && getDistanceSqToEntity(player) <= 64.0D;
    }

    private boolean isOwner(EntityPlayer player) {
        return player != null && ownerId.equals(player.getUniqueID().toString());
    }

    private void setupInventory() {
        ChesterInventory oldInventory = inventory;
        inventory = new ChesterInventory("container.chester", getInventorySize());
        if (oldInventory != null) {
            int copySlots = Math.min(oldInventory.getSizeInventory(), inventory.getSizeInventory());
            for (int slot = 0; slot < copySlots; slot++) {
                ItemStack stack = oldInventory.getStackInSlot(slot);
                if (stack != null) {
                    inventory.setInventorySlotContents(slot, stack.copy());
                }
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setInteger("Type", getChesterType());
        tag.setInteger("PackedState", packedState & ~(OPEN_FLAG | HAPPY_PANT_FLAG));
        tag.setString("OwnerUUID", ownerId);
        tag.setBoolean("CommandedSitting", commandedSitting);
        tag.setBoolean("HasStaffAnchor", hasStaffAnchor);
        if (hasStaffAnchor) {
            tag.setDouble("StaffAnchorX", staffAnchorX);
            tag.setDouble("StaffAnchorY", staffAnchorY);
            tag.setDouble("StaffAnchorZ", staffAnchorZ);
        }
        NBTTagList items = new NBTTagList();
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack == null) {
                continue;
            }
            NBTTagCompound itemTag = new NBTTagCompound();
            itemTag.setByte("Slot", (byte) slot);
            stack.writeToNBT(itemTag);
            items.appendTag(itemTag);
        }
        tag.setTag("Items", items);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        packedState = tag.hasKey("PackedState")
                ? tag.getInteger("PackedState")
                : tag.getInteger("Type") & TYPE_MASK;
        packedState &= ~(OPEN_FLAG | HAPPY_PANT_FLAG);
        ownerId = tag.getString("OwnerUUID");
        commandedSitting = tag.hasKey("CommandedSitting")
                ? tag.getBoolean("CommandedSitting")
                : isSitting();
        hasStaffAnchor = tag.getBoolean("HasStaffAnchor");
        staffAnchorX = tag.getDouble("StaffAnchorX");
        staffAnchorY = tag.getDouble("StaffAnchorY");
        staffAnchorZ = tag.getDouble("StaffAnchorZ");
        setupInventory();

        NBTTagList items = tag.getTagList("Items", 10);
        for (int index = 0; index < items.tagCount(); index++) {
            NBTTagCompound itemTag = items.getCompoundTagAt(index);
            int slot = itemTag.getByte("Slot") & 255;
            if (slot < inventory.getSizeInventory()) {
                inventory.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(itemTag));
            }
        }
        setHealth(getMaxHealth());
        ChesterBinding.register(this);
        registeredBinding = true;
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        data.writeInt(packedState);
        ByteBufUtils.writeUTF8String(data, ownerId);
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        packedState = data.readInt();
        ownerId = ByteBufUtils.readUTF8String(data);
        sitProgress = isSitting() ? 1.0F : 0.0F;
        previousSitProgress = sitProgress;
        mouthOpenProgress = isOpen() ? 1.0F : 0.0F;
        previousMouthOpenProgress = mouthOpenProgress;
    }

    @Override
    public void rf$writeSyncData(NBTTagCompound tag) {
        tag.setInteger("ChesterState", packedState);
        tag.setString("ChesterOwner", ownerId);
    }

    @Override
    public void rf$readSyncData(NBTTagCompound tag) {
        if (tag.hasKey("ChesterState")) {
            packedState = tag.getInteger("ChesterState");
        }
        if (tag.hasKey("ChesterOwner")) {
            ownerId = tag.getString("ChesterOwner");
        }
    }

    private void setPackedState(int state) {
        if (packedState == state) {
            return;
        }
        packedState = state;
        syncState();
    }

    private void syncState() {
        if (worldObj != null && !worldObj.isRemote) {
            EntitySyncHelper.sync(this);
        }
    }
}
