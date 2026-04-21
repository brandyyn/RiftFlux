package zairus.worldexplorer.archery.entity;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.net.sync.EntitySyncHelper;
import com.voidsrift.riftflux.net.sync.IEntitySyncData;
import cpw.mods.fml.common.registry.IThrowableEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import zairus.worldexplorer.archery.items.DartEffectHelper;
import zairus.worldexplorer.archery.items.WEArcheryItems;

public class EntityDart extends Entity implements IProjectile, IThrowableEntity, IEntitySyncData {
    public int canBePickedUp;
    public int arrowShake;
    public Entity shootingEntity;
    private ItemStack dartStack;
    private int blockX = -1;
    private int blockY = -1;
    private int blockZ = -1;
    private Block collisionBlock;
    private int inData;
    private boolean inGround;
    private int ticksInGround;
    private int ticksInAir;
    private double damage;
    private boolean critical;

    public EntityDart(World world) {
        super(world);
        this.renderDistanceWeight = 10.0;
        this.setSize(0.5f, 0.5f);
        this.damage = Math.max(0.0D, (double)ModConfig.riftExplorerDartDamage);
        this.setDartStack(new ItemStack(WEArcheryItems.dart, 1, 0));
    }

    public EntityDart(World world, double x, double y, double z) {
        this(world);
        this.setPosition(x, y, z);
        this.yOffset = 0.0f;
    }

    public EntityDart(World world, EntityLivingBase shooter, float power, ItemStack ammo) {
        this(world);
        this.shootingEntity = shooter;
        this.setDartStack(ammo);
        if (shooter instanceof EntityPlayer) {
            this.canBePickedUp = 1;
        }
        this.setLocationAndAngles(shooter.posX, shooter.posY + (double)shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        this.posX -= (double)(MathHelper.cos((float)(this.rotationYaw / 180.0f * (float)Math.PI)) * 0.16f);
        this.posY -= (double)0.1f;
        this.posZ -= (double)(MathHelper.sin((float)(this.rotationYaw / 180.0f * (float)Math.PI)) * 0.16f);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0f;
        this.motionX = -MathHelper.sin((float)(this.rotationYaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(this.rotationPitch / 180.0f * (float)Math.PI));
        this.motionZ = MathHelper.cos((float)(this.rotationYaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(this.rotationPitch / 180.0f * (float)Math.PI));
        this.motionY = -MathHelper.sin((float)(this.rotationPitch / 180.0f * (float)Math.PI));
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, power * 1.5f, 1.0f);
    }

    @Override
    protected void entityInit() {
    }

    @Override
    public void setThrowableHeading(double x, double y, double z, float velocity, float inaccuracy) {
        float length = MathHelper.sqrt_double((double)(x * x + y * y + z * z));
        if (length < 1.0E-7f) {
            return;
        }
        x /= (double)length;
        y /= (double)length;
        z /= (double)length;
        float configuredInaccuracy = ModConfig.riftExplorerDartInaccuracy;
        x += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * (double)0.0075f * (double)configuredInaccuracy;
        y += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * (double)0.0075f * (double)configuredInaccuracy;
        z += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * (double)0.0075f * (double)configuredInaccuracy;
        this.motionX = x *= (double)velocity;
        this.motionY = y *= (double)velocity;
        this.motionZ = z *= (double)velocity;
        float horizontal = MathHelper.sqrt_double((double)(x * x + z * z));
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(x, z) * 180.0 / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(y, horizontal) * 180.0 / Math.PI);
        this.ticksInGround = 0;
    }

    @SideOnly(value=Side.CLIENT)
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int updates) {
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
    }

    @SideOnly(value=Side.CLIENT)
    public void setVelocity(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            float horizontal = MathHelper.sqrt_double((double)(x * x + z * z));
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(x, z) * 180.0 / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(y, horizontal) * 180.0 / Math.PI);
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.ticksInGround = 0;
        }
    }

    @Override
    public void onUpdate() {
        Block block;
        super.onUpdate();
        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            float horizontal = MathHelper.sqrt_double((double)(this.motionX * this.motionX + this.motionZ * this.motionZ));
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, horizontal) * 180.0 / Math.PI);
        }

        if ((block = this.worldObj.getBlock(this.blockX, this.blockY, this.blockZ)).getMaterial() != Material.air) {
            block.setBlockBoundsBasedOnState((IBlockAccess)this.worldObj, this.blockX, this.blockY, this.blockZ);
            AxisAlignedBB blockBounds = block.getCollisionBoundingBoxFromPool(this.worldObj, this.blockX, this.blockY, this.blockZ);
            if (blockBounds != null && blockBounds.isVecInside(Vec3.createVectorHelper((double)this.posX, (double)this.posY, (double)this.posZ))) {
                this.inGround = true;
            }
        }

        if (this.arrowShake > 0) {
            --this.arrowShake;
        }

        if (this.inGround) {
            int metadata = this.worldObj.getBlockMetadata(this.blockX, this.blockY, this.blockZ);
            if (block == this.collisionBlock && metadata == this.inData) {
                ++this.ticksInGround;
                if (this.ticksInGround >= 1200) {
                    this.setDead();
                }
                return;
            }
            this.inGround = false;
            this.motionX *= (double)(this.rand.nextFloat() * 0.2f);
            this.motionY *= (double)(this.rand.nextFloat() * 0.2f);
            this.motionZ *= (double)(this.rand.nextFloat() * 0.2f);
            this.ticksInGround = 0;
            this.ticksInAir = 0;
        }

        ++this.ticksInAir;
        Vec3 start = Vec3.createVectorHelper((double)this.posX, (double)this.posY, (double)this.posZ);
        Vec3 end = Vec3.createVectorHelper((double)(this.posX + this.motionX), (double)(this.posY + this.motionY), (double)(this.posZ + this.motionZ));
        MovingObjectPosition hit = this.worldObj.func_147447_a(start, end, false, true, false);
        start = Vec3.createVectorHelper((double)this.posX, (double)this.posY, (double)this.posZ);
        end = Vec3.createVectorHelper((double)(this.posX + this.motionX), (double)(this.posY + this.motionY), (double)(this.posZ + this.motionZ));
        if (hit != null) {
            end = Vec3.createVectorHelper((double)hit.hitVec.xCoord, (double)hit.hitVec.yCoord, (double)hit.hitVec.zCoord);
        }

        Entity entity = null;
        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
        double closest = 0.0;
        for (int i = 0; i < list.size(); ++i) {
            Entity other = (Entity)list.get(i);
            float border = 0.3f;
            AxisAlignedBB expanded = other.boundingBox.expand((double)border, (double)border, (double)border);
            MovingObjectPosition intercept = expanded.calculateIntercept(start, end);
            if (!other.canBeCollidedWith() || other == this.shootingEntity && this.ticksInAir < 5 || intercept == null) {
                continue;
            }
            double distance = start.distanceTo(intercept.hitVec);
            if (!(distance < closest) && closest != 0.0) {
                continue;
            }
            entity = other;
            closest = distance;
        }
        if (entity != null) {
            hit = new MovingObjectPosition(entity);
        }
        if (hit != null && hit.entityHit instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)hit.entityHit;
            if (player.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer && !((EntityPlayer)this.shootingEntity).canAttackPlayer(player)) {
                hit = null;
            }
        }

        if (hit != null) {
            if (hit.entityHit != null) {
                this.handleEntityHit(hit);
            } else {
                this.handleBlockHit(hit);
            }
        }

        if (this.getIsCritical()) {
            for (int i = 0; i < 1; ++i) {
                this.worldObj.spawnParticle("crit", this.posX + this.motionX * (double)i / 4.0, this.posY + this.motionY * (double)i / 4.0, this.posZ + this.motionZ * (double)i / 4.0, -this.motionX, -this.motionY + 0.2, -this.motionZ);
            }
        }

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        float horizontal = MathHelper.sqrt_double((double)(this.motionX * this.motionX + this.motionZ * this.motionZ));
        this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
        this.rotationPitch = (float)(Math.atan2(this.motionY, horizontal) * 180.0 / Math.PI);
        while (this.rotationPitch - this.prevRotationPitch < -180.0f) {
            this.prevRotationPitch -= 360.0f;
        }
        while (this.rotationPitch - this.prevRotationPitch >= 180.0f) {
            this.prevRotationPitch += 360.0f;
        }
        while (this.rotationYaw - this.prevRotationYaw < -180.0f) {
            this.prevRotationYaw -= 360.0f;
        }
        while (this.rotationYaw - this.prevRotationYaw >= 180.0f) {
            this.prevRotationYaw += 360.0f;
        }
        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2f;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2f;

        float velocityScale = 0.99f;
        float gravity = 0.05f;
        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                float bubbleScale = 0.25f;
                this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double)bubbleScale, this.posY - this.motionY * (double)bubbleScale, this.posZ - this.motionZ * (double)bubbleScale, this.motionX, this.motionY, this.motionZ);
            }
            velocityScale = 0.8f;
        }
        if (this.isWet()) {
            this.extinguish();
        }
        this.motionX *= (double)velocityScale;
        this.motionY *= (double)velocityScale;
        this.motionZ *= (double)velocityScale;
        this.motionY -= (double)gravity;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.func_145775_I();
    }

    private void handleEntityHit(MovingObjectPosition hit) {
        float velocity = MathHelper.sqrt_double((double)(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ));
        int hitDamage = MathHelper.ceiling_double_int((double)((double)velocity * this.damage));
        if (this.getIsCritical()) {
            hitDamage += this.rand.nextInt(hitDamage / 2 + 2);
        }
        DamageSource damageSource = this.shootingEntity == null
                ? new EntityDamageSourceIndirect("dart", (Entity)this, (Entity)this).setProjectile()
                : new EntityDamageSourceIndirect("dart", (Entity)this, this.shootingEntity).setProjectile();
        if (this.isBurning() && !(hit.entityHit instanceof EntityEnderman)) {
            hit.entityHit.setFire(5);
        }
        if (hit.entityHit.attackEntityFrom(damageSource, (float)hitDamage)) {
            if (hit.entityHit instanceof EntityLivingBase) {
                EntityLivingBase livingTarget = (EntityLivingBase)hit.entityHit;
                if (this.shootingEntity instanceof EntityLivingBase) {
                    EnchantmentHelper.func_151384_a((EntityLivingBase)livingTarget, (Entity)this.shootingEntity);
                    EnchantmentHelper.func_151385_b((EntityLivingBase)((EntityLivingBase)this.shootingEntity), (Entity)livingTarget);
                }
                DartEffectHelper.applyEffects(livingTarget, this.shootingEntity, this.dartStack);
                if (this.shootingEntity != null && hit.entityHit != this.shootingEntity && hit.entityHit instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
                    ((EntityPlayerMP)this.shootingEntity).playerNetServerHandler.sendPacket((Packet)new S2BPacketChangeGameState(6, 0.0f));
                }
            }
            this.playSound("random.bowhit", 1.0f, 1.2f / (this.rand.nextFloat() * 0.2f + 0.9f));
            if (!(hit.entityHit instanceof EntityEnderman)) {
                this.setDead();
            }
        } else {
            this.motionX *= (double)-0.1f;
            this.motionY *= (double)-0.1f;
            this.motionZ *= (double)-0.1f;
            this.rotationYaw += 180.0f;
            this.prevRotationYaw += 180.0f;
            this.ticksInAir = 0;
        }
    }

    private void handleBlockHit(MovingObjectPosition hit) {
        this.blockX = hit.blockX;
        this.blockY = hit.blockY;
        this.blockZ = hit.blockZ;
        this.collisionBlock = this.worldObj.getBlock(this.blockX, this.blockY, this.blockZ);
        this.inData = this.worldObj.getBlockMetadata(this.blockX, this.blockY, this.blockZ);
        this.motionX = (float)(hit.hitVec.xCoord - this.posX);
        this.motionY = (float)(hit.hitVec.yCoord - this.posY);
        this.motionZ = (float)(hit.hitVec.zCoord - this.posZ);
        float motionLength = MathHelper.sqrt_double((double)(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ));
        if (motionLength > 0.0f) {
            this.posX -= this.motionX / (double)motionLength * (double)0.05f;
            this.posY -= this.motionY / (double)motionLength * (double)0.05f;
            this.posZ -= this.motionZ / (double)motionLength * (double)0.05f;
        }
        this.playSound("random.bowhit", 1.0f, 1.2f / (this.rand.nextFloat() * 0.2f + 0.9f));
        this.inGround = true;
        this.arrowShake = 7;
        this.setIsCritical(false);
        if (this.collisionBlock != null && this.collisionBlock.getMaterial() != Material.air) {
            this.collisionBlock.onEntityCollidedWithBlock(this.worldObj, this.blockX, this.blockY, this.blockZ, (Entity)this);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        tag.setShort("xTile", (short)this.blockX);
        tag.setShort("yTile", (short)this.blockY);
        tag.setShort("zTile", (short)this.blockZ);
        tag.setShort("life", (short)this.ticksInGround);
        tag.setByte("inTile", (byte)Block.getIdFromBlock((Block)this.collisionBlock));
        tag.setByte("inData", (byte)this.inData);
        tag.setByte("shake", (byte)this.arrowShake);
        tag.setByte("inGround", (byte)(this.inGround ? 1 : 0));
        tag.setByte("pickup", (byte)this.canBePickedUp);
        tag.setDouble("damage", this.damage);
        if (this.dartStack != null && this.dartStack.getItem() != null) {
            NBTTagCompound dartTag = new NBTTagCompound();
            this.dartStack.writeToNBT(dartTag);
            tag.setTag("dartStack", dartTag);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        this.blockX = tag.getShort("xTile");
        this.blockY = tag.getShort("yTile");
        this.blockZ = tag.getShort("zTile");
        this.ticksInGround = tag.getShort("life");
        this.collisionBlock = Block.getBlockById((int)(tag.getByte("inTile") & 0xFF));
        this.inData = tag.getByte("inData") & 0xFF;
        this.arrowShake = tag.getByte("shake") & 0xFF;
        this.inGround = tag.getByte("inGround") == 1;
        this.damage = tag.hasKey("damage", 99) ? tag.getDouble("damage") : Math.max(0.0D, (double)ModConfig.riftExplorerDartDamage);
        if (tag.hasKey("pickup", 99)) {
            this.canBePickedUp = tag.getByte("pickup");
        } else if (tag.hasKey("player", 99)) {
            this.canBePickedUp = tag.getBoolean("player") ? 1 : 0;
        }
        if (tag.hasKey("dartStack", 10)) {
            this.setDartStack(ItemStack.loadItemStackFromNBT((NBTTagCompound)tag.getCompoundTag("dartStack")));
        } else {
            this.setDartStack(new ItemStack(WEArcheryItems.dart, 1, 0));
        }
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        if (!this.worldObj.isRemote && this.inGround && this.arrowShake <= 0) {
            boolean canPickup = this.canBePickedUp == 1 || this.canBePickedUp == 2 && player.capabilities.isCreativeMode;
            ItemStack pickup = this.getPickupStack();
            if (this.canBePickedUp == 1 && (pickup == null || !player.inventory.addItemStackToInventory(pickup.copy()))) {
                canPickup = false;
            }
            if (canPickup) {
                this.playSound("random.pop", 0.2f, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
                player.onItemPickup((Entity)this, 1);
                this.setDead();
            }
        }
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public float getShadowSize() {
        return 0.0f;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getDamage() {
        return this.damage;
    }

    public boolean canAttackWithItem() {
        return false;
    }

    public void setIsCritical(boolean critical) {
        if (this.critical == critical) {
            return;
        }
        this.critical = critical;
        EntitySyncHelper.sync(this);
    }

    public boolean getIsCritical() {
        return this.critical;
    }

    private void setDartStack(ItemStack stack) {
        if (stack == null || stack.getItem() != WEArcheryItems.dart) {
            this.dartStack = new ItemStack(WEArcheryItems.dart, 1, 0);
        } else {
            this.dartStack = stack.copy();
            this.dartStack.stackSize = 1;
        }
    }

    private ItemStack getPickupStack() {
        if (this.dartStack == null || this.dartStack.getItem() != WEArcheryItems.dart) {
            this.dartStack = new ItemStack(WEArcheryItems.dart, 1, 0);
        }
        ItemStack pickup = this.dartStack.copy();
        pickup.stackSize = 1;
        return pickup;
    }

    @Override
    public Entity getThrower() {
        return this.shootingEntity;
    }

    @Override
    public void setThrower(Entity entity) {
        this.shootingEntity = entity;
    }

    @Override
    public void rf$writeSyncData(NBTTagCompound tag) {
        tag.setBoolean("Critical", this.critical);
    }

    @Override
    public void rf$readSyncData(NBTTagCompound tag) {
        this.critical = tag.getBoolean("Critical");
    }
}
