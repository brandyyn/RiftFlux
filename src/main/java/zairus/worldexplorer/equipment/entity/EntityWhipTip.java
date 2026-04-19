/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IProjectile
 *  net.minecraft.entity.monster.EntityEnderman
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.server.S2BPacketChangeGameState
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EntityDamageSourceIndirect
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.util.Vec3
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package zairus.worldexplorer.equipment.entity;

import java.util.ArrayList;
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
import net.minecraft.item.Item;
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
import zairus.worldexplorer.equipment.items.WEEquipmentItems;
import zairus.worldexplorer.equipment.items.Whip;

public class EntityWhipTip
extends Entity
implements IProjectile {
    private int positionX = -1;
    private int positionY = -1;
    private int positionZ = -1;
    private Block collisionBlock;
    private int ticksInAir;
    private double damage = 2.5;
    private int knockbackStrength;
    private ItemStack whipItem;
    private float tipWidth = 0.5f;
    private float tipHeight = 0.5f;
    private float strength;
    private float whipReach = 4.0f;
    private boolean destReached = false;
    private boolean isCritical = false;
    private double distanceFromShooter = 0.0;
    private List<Entity> owners = new ArrayList<Entity>();
    public Entity shootingEntity;

    public EntityWhipTip(World world) {
        super(world);
        this.renderDistanceWeight = 10.0;
        this.setSize(this.tipWidth, this.tipHeight);
    }

    public EntityWhipTip(World world, double posX, double posY, double posZ) {
        super(world);
        this.renderDistanceWeight = 10.0;
        this.setSize(this.tipWidth, this.tipHeight);
        this.setPosition(posX, posY, posZ);
        this.yOffset = 0.0f;
    }

    public EntityWhipTip(World world, EntityLivingBase entityShooter, EntityLivingBase entityTarget, float p_i1755_4_, float p_i1755_5_) {
        super(world);
        this.renderDistanceWeight = 10.0;
        this.shootingEntity = entityShooter;
        this.setSize(this.tipWidth, this.tipHeight);
    }

    public EntityWhipTip(World world, Entity player, float strength) {
        super(world);
        this.renderDistanceWeight = 10.0;
        this.shootingEntity = player;
        this.setSize(this.tipWidth, this.tipHeight);
        this.setLocationAndAngles(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ, player.rotationYaw, player.rotationPitch);
        this.posX -= (double)(MathHelper.cos((float)(this.rotationYaw / 180.0f * (float)Math.PI)) * 0.16f);
        this.posY -= 0.6000000014901161;
        this.posZ -= (double)(MathHelper.sin((float)(this.rotationYaw / 180.0f * (float)Math.PI)) * 0.16f);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0f;
        this.motionX = -MathHelper.sin((float)(this.rotationYaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(this.rotationPitch / 180.0f * (float)Math.PI));
        this.motionZ = MathHelper.cos((float)(this.rotationYaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(this.rotationPitch / 180.0f * (float)Math.PI));
        this.motionY = -MathHelper.sin((float)(this.rotationPitch / 180.0f * (float)Math.PI));
        this.strength = strength * 1.5f;
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, this.strength, 1.0f);
    }

    public void setShootingEntity(Entity shooting) {
        this.shootingEntity = shooting;
    }

    public double getDistanceFromShooter() {
        return this.distanceFromShooter;
    }

    public void setReach(float reach) {
        this.whipReach = reach;
    }

    public float getReach() {
        return this.whipReach;
    }

    public void setWhipItem(ItemStack item) {
        this.whipItem = item;
    }

    public ItemStack getWhipItem() {
        return this.whipItem;
    }

    public List<Entity> getOwners() {
        return this.owners;
    }

    public void onUpdate() {
        EntityPlayer entityplayer;
        float f1;
        Block block;
        super.onUpdate();
        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            float f = MathHelper.sqrt_double((double)(this.motionX * this.motionX + this.motionZ * this.motionZ));
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, f) * 180.0 / Math.PI);
        }
        this.collisionBlock = block = this.worldObj.getBlock(this.positionX, this.positionY, this.positionZ);
        if (block.getMaterial() != Material.air) {
            block.setBlockBoundsBasedOnState((IBlockAccess)this.worldObj, this.positionX, this.positionY, this.positionZ);
            this.destReached = true;
        }
        ++this.ticksInAir;
        if (this.ticksInAir > 50) {
            this.setDead();
            return;
        }
        Vec3 vec31 = Vec3.createVectorHelper((double)this.posX, (double)this.posY, (double)this.posZ);
        Vec3 vec3 = Vec3.createVectorHelper((double)(this.posX + this.motionX), (double)(this.posY + this.motionY), (double)(this.posZ + this.motionZ));
        MovingObjectPosition movingobjectposition = this.worldObj.func_147447_a(vec31, vec3, false, true, false);
        vec31 = Vec3.createVectorHelper((double)this.posX, (double)this.posY, (double)this.posZ);
        vec3 = Vec3.createVectorHelper((double)(this.posX + this.motionX), (double)(this.posY + this.motionY), (double)(this.posZ + this.motionZ));
        if (movingobjectposition != null) {
            vec3 = Vec3.createVectorHelper((double)movingobjectposition.hitVec.xCoord, (double)movingobjectposition.hitVec.yCoord, (double)movingobjectposition.hitVec.zCoord);
        }
        Entity entity = null;
        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
        double d0 = 0.0;
        for (int i = 0; i < list.size(); ++i) {
            double d1;
            AxisAlignedBB axisalignedbb1;
            MovingObjectPosition movingobjectposition1;
            Entity entity1 = (Entity)list.get(i);
            if (entity1.isDead || !entity1.canBeCollidedWith() || entity1 == this.shootingEntity && this.ticksInAir < 5 || (movingobjectposition1 = (axisalignedbb1 = entity1.boundingBox.expand((double)(f1 = 0.3f), (double)f1, (double)f1)).calculateIntercept(vec31, vec3)) == null || !((d1 = vec31.distanceTo(movingobjectposition1.hitVec)) < d0) && d0 != 0.0) continue;
            entity = entity1;
            d0 = d1;
        }
        if (entity != null) {
            movingobjectposition = new MovingObjectPosition(entity);
        }
        if (movingobjectposition != null && movingobjectposition.entityHit != null && movingobjectposition.entityHit instanceof EntityPlayer && ((entityplayer = (EntityPlayer)movingobjectposition.entityHit) == (EntityPlayer)this.shootingEntity || entityplayer.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer && !((EntityPlayer)this.shootingEntity).canAttackPlayer(entityplayer))) {
            movingobjectposition = null;
        }
        if (movingobjectposition != null) {
            if (movingobjectposition.entityHit != null) {
                float f2 = MathHelper.sqrt_double((double)(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ));
                int k = MathHelper.ceiling_double_int((double)((double)f2 * this.damage));
                EntityDamageSourceIndirect damagesource = null;
                damagesource = this.shootingEntity == null ? new EntityDamageSourceIndirect("whip", (Entity)this, (Entity)this) : new EntityDamageSourceIndirect("whip", (Entity)this, this.shootingEntity);
                if (this.isBurning() && !(movingobjectposition.entityHit instanceof EntityEnderman)) {
                    movingobjectposition.entityHit.setFire(5);
                }
                if (movingobjectposition.entityHit.attackEntityFrom((DamageSource)damagesource, (float)k)) {
                    if (movingobjectposition.entityHit instanceof EntityLivingBase) {
                        float f4;
                        EntityLivingBase entitylivingbase = (EntityLivingBase)movingobjectposition.entityHit;
                        if (this.knockbackStrength > 0 && (f4 = MathHelper.sqrt_double((double)(this.motionX * this.motionX + this.motionZ * this.motionZ))) > 0.0f) {
                            movingobjectposition.entityHit.addVelocity(this.motionX * (double)this.knockbackStrength * (double)0.6f / (double)f4, 0.1, this.motionZ * (double)this.knockbackStrength * (double)0.6f / (double)f4);
                        }
                        if (this.shootingEntity != null && this.shootingEntity instanceof EntityLivingBase) {
                            EnchantmentHelper.func_151384_a((EntityLivingBase)entitylivingbase, (Entity)this.shootingEntity);
                            EnchantmentHelper.func_151385_b((EntityLivingBase)((EntityLivingBase)this.shootingEntity), (Entity)entitylivingbase);
                        }
                        if (this.shootingEntity != null && movingobjectposition.entityHit != this.shootingEntity && movingobjectposition.entityHit instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
                            ((EntityPlayerMP)this.shootingEntity).playerNetServerHandler.sendPacket((Packet)new S2BPacketChangeGameState(6, 0.0f));
                        }
                    }
                    if (!(movingobjectposition.entityHit instanceof EntityEnderman)) {
                        this.playSound("worldexplorer:pebble_hit_1", 1.0f, 1.2f / (this.rand.nextFloat() * 0.2f + 0.9f));
                    }
                }
            } else {
                this.collisionBlock = this.worldObj.getBlock(movingobjectposition.blockX, movingobjectposition.blockY, movingobjectposition.blockZ);
                if (this.collisionBlock.getMaterial() != Material.air) {
                    this.playSound("worldexplorer:pebble_hit_1", 1.0f, 1.2f / (this.rand.nextFloat() * 0.2f + 0.9f));
                    this.collisionBlock.onEntityCollidedWithBlock(this.worldObj, this.positionX, this.positionY, this.positionZ, (Entity)this);
                    this.destReached = true;
                }
            }
        }
        if (this.isCritical) {
            this.playSound("worldexplorer:whip_snap", 1.0f, 1.2f / (this.rand.nextFloat() * 0.2f + 0.9f));
            for (int p_i = 0; p_i < 15; ++p_i) {
                this.worldObj.spawnParticle("crit", this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ, -this.motionX, -this.motionY + 0.2, -this.motionZ);
            }
        }
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        float f2 = MathHelper.sqrt_double((double)(this.motionX * this.motionX + this.motionZ * this.motionZ));
        this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
        this.rotationPitch = (float)(Math.atan2(this.motionY, f2) * 180.0 / Math.PI);
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
        float f3 = 0.99f;
        f1 = 0.01f;
        if (this.isInWater()) {
            for (int l = 0; l < 4; ++l) {
                float f4 = 0.25f;
                this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double)f4, this.posY - this.motionY * (double)f4, this.posZ - this.motionZ * (double)f4, this.motionX, this.motionY, this.motionZ);
            }
            this.destReached = true;
        }
        if (this.isWet()) {
            this.extinguish();
        }
        if (this.shootingEntity != null) {
            double distance;
            this.distanceFromShooter = distance = Vec3.createVectorHelper((double)this.shootingEntity.posX, (double)this.shootingEntity.posY, (double)this.shootingEntity.posZ).distanceTo(Vec3.createVectorHelper((double)this.posX, (double)this.posY, (double)this.posZ));
            if (this.destReached) {
                if (this.whipItem != null) {
                    ((Whip)this.whipItem.getItem()).unleashed = false;
                }
                this.setDead();
                if (this.shootingEntity instanceof EntityPlayer) {
                    ((EntityPlayer)this.shootingEntity).stopUsingItem();
                }
            }
            if (distance >= (double)this.whipReach && !this.destReached) {
                this.isCritical = true;
                this.destReached = true;
            } else {
                this.isCritical = false;
            }
        } else {
            f3 = 0.99f;
            this.motionX *= (double)f3;
            this.motionY *= (double)f3;
            this.motionZ *= (double)f3;
            this.motionY -= (double)f1;
        }
        this.setPosition(this.posX, this.posY, this.posZ);
        this.func_145775_I();
    }

    protected void entityInit() {
        this.dataWatcher.addObject(16, (Object)0);
        this.dataWatcher.addObject(10, (Object)new ItemStack((Item)WEEquipmentItems.spyglass));
    }

    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
        this.positionX = tagCompound.getShort("xTile");
        this.positionY = tagCompound.getShort("yTile");
        this.positionZ = tagCompound.getShort("zTile");
        this.collisionBlock = Block.getBlockById((int)(tagCompound.getByte("inTile") & 0xFF));
        if (tagCompound.hasKey("damage", 99)) {
            this.damage = tagCompound.getDouble("damage");
        }
    }

    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        tagCompound.setShort("xTile", (short)this.positionX);
        tagCompound.setShort("yTile", (short)this.positionY);
        tagCompound.setShort("zTile", (short)this.positionZ);
        tagCompound.setByte("inTile", (byte)Block.getIdFromBlock((Block)this.collisionBlock));
        tagCompound.setDouble("damage", this.damage);
    }

    public void onCollideWithPlayer(EntityPlayer player) {
        if (this.ticksInAir <= 2 && this.shootingEntity == null && this.owners.size() == 0) {
            this.owners.add((Entity)player);
        }
        if (!(this.shootingEntity instanceof EntityPlayer)) {
            return;
        }
        if (!this.worldObj.isRemote && this.ticksInAir > 2 && player == (EntityPlayer)this.shootingEntity) {
            ((Whip)this.whipItem.getItem()).unleashed = false;
            this.setDead();
        }
    }

    public void setKnockbackStrength(int knockback) {
        this.knockbackStrength = knockback;
    }

    public void setThrowableHeading(double dirX, double dirY, double dirZ) {
        this.setThrowableHeading(dirX, dirY, dirZ, this.strength, 1.0f);
    }

    public void setThrowableHeading(double dirX, double dirY, double dirZ, float strength, float p_70186_8_) {
        float f2 = MathHelper.sqrt_double((double)(dirX * dirX + dirY * dirY + dirZ * dirZ));
        dirX /= (double)f2;
        dirY /= (double)f2;
        dirZ /= (double)f2;
        dirX += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * (double)0.0075f * (double)p_70186_8_;
        dirY += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * (double)0.0075f * (double)p_70186_8_;
        dirZ += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * (double)0.0075f * (double)p_70186_8_;
        this.motionX = dirX *= (double)strength;
        this.motionY = dirY *= (double)strength;
        this.motionZ = dirZ *= (double)strength;
        float f3 = MathHelper.sqrt_double((double)(dirX * dirX + dirZ * dirZ));
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(dirX, dirZ) * 180.0 / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(dirY, f3) * 180.0 / Math.PI);
    }
}

