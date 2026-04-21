/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
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
package zairus.worldexplorer.archery.entity;

import com.voidsrift.riftflux.ModConfig;
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
import zairus.worldexplorer.archery.items.SpecialArrow;
import zairus.worldexplorer.archery.items.WEArcheryItems;

public class EntitySpecialArrow
extends Entity
implements IProjectile, IThrowableEntity {
    public int canBePickedUp;
    public int arrowShake;
    public Entity shootingEntity;
    public ItemStack arrowStack;
    private int blockX = -1;
    private int blockY = -1;
    private int blockZ = -1;
    private Block collisionBlock;
    private int inData;
    private boolean inGround;
    private int ticksInGround;
    private int ticksInAir;
    private double damage = 2.0;
    private int knockbackStrength;

    public EntitySpecialArrow(World world) {
        super(world);
        this.arrowStack = new ItemStack((Item)WEArcheryItems.specialarrow, 1, 0);
        this.dataWatcher.updateObject(17, (Object)0);
        this.renderDistanceWeight = 10.0;
        this.setSize(0.5f, 0.5f);
    }

    public EntitySpecialArrow(World world, double x, double y, double z) {
        super(world);
        this.arrowStack = new ItemStack((Item)WEArcheryItems.specialarrow, 1, 0);
        this.dataWatcher.updateObject(17, (Object)0);
        this.renderDistanceWeight = 10.0;
        this.setSize(0.5f, 0.5f);
        this.setPosition(x, y, z);
        this.yOffset = 0.0f;
    }

    public EntitySpecialArrow(World world, EntityLivingBase shooter, EntityLivingBase target, float p_i1755_4_, float p_i1755_5_, ItemStack ammo) {
        super(world);
        this.setSize(0.5f, 0.5f);
        this.setArrowStack(ammo);
        this.renderDistanceWeight = 10.0;
        this.shootingEntity = shooter;
        if (shooter instanceof EntityPlayer) {
            this.canBePickedUp = 1;
        }
        this.posY = shooter.posY + (double)shooter.getEyeHeight() - (double)0.1f;
        double d0 = target.posX - shooter.posX;
        double d1 = target.boundingBox.minY + (double)(target.height / 3.0f) - this.posY;
        double d2 = target.posZ - shooter.posZ;
        double d3 = MathHelper.sqrt_double((double)(d0 * d0 + d2 * d2));
        if (d3 >= 1.0E-7) {
            float f2 = (float)(Math.atan2(d2, d0) * 180.0 / Math.PI) - 90.0f;
            float f3 = (float)(-(Math.atan2(d1, d3) * 180.0 / Math.PI));
            double d4 = d0 / d3;
            double d5 = d2 / d3;
            this.setLocationAndAngles(shooter.posX + d4, this.posY, shooter.posZ + d5, f2, f3);
            this.yOffset = 0.0f;
            float f4 = (float)d3 * 0.2f;
            this.setThrowableHeading(d0, d1 + (double)f4, d2, p_i1755_4_ * SpecialArrow.getSpeedMultiplier(this.arrowStack.getItemDamage()), p_i1755_5_);
        }
    }

    public EntitySpecialArrow(World world, EntityLivingBase shooter, float power, ItemStack ammo) {
        super(world);
        this.setArrowStack(ammo);
        this.renderDistanceWeight = 10.0;
        this.shootingEntity = shooter;
        if (shooter instanceof EntityPlayer) {
            this.canBePickedUp = 1;
        }
        this.setSize(0.5f, 0.5f);
        this.setLocationAndAngles(shooter.posX, shooter.posY + (double)shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        this.posX -= (double)(MathHelper.cos((float)(this.rotationYaw / 180.0f * (float)Math.PI)) * 0.16f);
        this.posY -= (double)0.1f;
        this.posZ -= (double)(MathHelper.sin((float)(this.rotationYaw / 180.0f * (float)Math.PI)) * 0.16f);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0f;
        this.motionX = -MathHelper.sin((float)(this.rotationYaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(this.rotationPitch / 180.0f * (float)Math.PI));
        this.motionZ = MathHelper.cos((float)(this.rotationYaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(this.rotationPitch / 180.0f * (float)Math.PI));
        this.motionY = -MathHelper.sin((float)(this.rotationPitch / 180.0f * (float)Math.PI));
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, power * 1.5f * SpecialArrow.getSpeedMultiplier(this.arrowStack.getItemDamage()), 1.0f);
    }

    protected void entityInit() {
        this.dataWatcher.addObject(16, (Object)Byte.valueOf((byte)0));
        this.dataWatcher.addObject(17, (Object)0);
    }

    public void setThrowableHeading(double p_70186_1_, double p_70186_3_, double p_70186_5_, float p_70186_7_, float p_70186_8_) {
        float f2 = MathHelper.sqrt_double((double)(p_70186_1_ * p_70186_1_ + p_70186_3_ * p_70186_3_ + p_70186_5_ * p_70186_5_));
        if (f2 < 1.0E-7f) {
            return;
        }
        p_70186_1_ /= (double)f2;
        p_70186_3_ /= (double)f2;
        p_70186_5_ /= (double)f2;
        float inaccuracy = ModConfig.riftExplorerSpecialArrowInaccuracy;
        p_70186_1_ += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * (double)0.0075f * (double)inaccuracy;
        p_70186_3_ += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * (double)0.0075f * (double)inaccuracy;
        p_70186_5_ += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * (double)0.0075f * (double)inaccuracy;
        this.motionX = p_70186_1_ *= (double)p_70186_7_;
        this.motionY = p_70186_3_ *= (double)p_70186_7_;
        this.motionZ = p_70186_5_ *= (double)p_70186_7_;
        float f3 = MathHelper.sqrt_double((double)(p_70186_1_ * p_70186_1_ + p_70186_5_ * p_70186_5_));
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(p_70186_1_, p_70186_5_) * 180.0 / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(p_70186_3_, f3) * 180.0 / Math.PI);
        this.ticksInGround = 0;
    }

    @SideOnly(value=Side.CLIENT)
    public void setPositionAndRotation2(double p_70056_1_, double p_70056_3_, double p_70056_5_, float p_70056_7_, float p_70056_8_, int p_70056_9_) {
        this.setPosition(p_70056_1_, p_70056_3_, p_70056_5_);
        this.setRotation(p_70056_7_, p_70056_8_);
    }

    @SideOnly(value=Side.CLIENT)
    public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
        this.motionX = p_70016_1_;
        this.motionY = p_70016_3_;
        this.motionZ = p_70016_5_;
        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            float f = MathHelper.sqrt_double((double)(p_70016_1_ * p_70016_1_ + p_70016_5_ * p_70016_5_));
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(p_70016_1_, p_70016_5_) * 180.0 / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(p_70016_3_, f) * 180.0 / Math.PI);
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.ticksInGround = 0;
        }
    }

    public void onUpdate() {
        Block block;
        super.onUpdate();
        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            float f = MathHelper.sqrt_double((double)(this.motionX * this.motionX + this.motionZ * this.motionZ));
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, f) * 180.0 / Math.PI);
        }
        if ((block = this.worldObj.getBlock(this.blockX, this.blockY, this.blockZ)).getMaterial() != Material.air) {
            block.setBlockBoundsBasedOnState((IBlockAccess)this.worldObj, this.blockX, this.blockY, this.blockZ);
            AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(this.worldObj, this.blockX, this.blockY, this.blockZ);
            if (axisalignedbb != null && axisalignedbb.isVecInside(Vec3.createVectorHelper((double)this.posX, (double)this.posY, (double)this.posZ))) {
                this.inGround = true;
            }
        }
        if (this.arrowShake > 0) {
            --this.arrowShake;
        }
        if (this.inGround) {
            int j = this.worldObj.getBlockMetadata(this.blockX, this.blockY, this.blockZ);
            if (block == this.collisionBlock && j == this.inData) {
                ++this.ticksInGround;
                if (this.ticksInGround == 1200) {
                    this.setDead();
                }
            } else {
                this.inGround = false;
                this.motionX *= (double)(this.rand.nextFloat() * 0.2f);
                this.motionY *= (double)(this.rand.nextFloat() * 0.2f);
                this.motionZ *= (double)(this.rand.nextFloat() * 0.2f);
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            }
        } else {
            float f1;
            int i;
            ++this.ticksInAir;
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
            for (i = 0; i < list.size(); ++i) {
                double d1;
                AxisAlignedBB axisalignedbb1;
                MovingObjectPosition movingobjectposition1;
                Entity entity1 = (Entity)list.get(i);
                if (!entity1.canBeCollidedWith() || entity1 == this.shootingEntity && this.ticksInAir < 5 || (movingobjectposition1 = (axisalignedbb1 = entity1.boundingBox.expand((double)(f1 = 0.3f), (double)f1, (double)f1)).calculateIntercept(vec31, vec3)) == null || !((d1 = vec31.distanceTo(movingobjectposition1.hitVec)) < d0) && d0 != 0.0) continue;
                entity = entity1;
                d0 = d1;
            }
            if (entity != null) {
                movingobjectposition = new MovingObjectPosition(entity);
            }
            if (movingobjectposition != null && movingobjectposition.entityHit != null && movingobjectposition.entityHit instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer)movingobjectposition.entityHit;
                if (entityplayer.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer && !((EntityPlayer)this.shootingEntity).canAttackPlayer(entityplayer)) {
                    movingobjectposition = null;
                }
            }
            if (movingobjectposition != null) {
                if (movingobjectposition.entityHit != null) {
                    float f2 = MathHelper.sqrt_double((double)(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ));
                    int k = MathHelper.ceiling_double_int((double)((double)f2 * this.damage));
                    if (this.getIsCritical()) {
                        k += this.rand.nextInt(k / 2 + 2);
                    }
                    DamageSource damagesource = null;
                    damagesource = this.shootingEntity == null ? new EntityDamageSourceIndirect("specialarrow", (Entity)this, (Entity)this).setProjectile() : new EntityDamageSourceIndirect("specialarrow", (Entity)this, this.shootingEntity).setProjectile();
                    if (this.isBurning() && !(movingobjectposition.entityHit instanceof EntityEnderman)) {
                        movingobjectposition.entityHit.setFire(5);
                    }
                    if (movingobjectposition.entityHit.attackEntityFrom(damagesource, (float)(k = k + SpecialArrow.getDamageBonus(this.arrowStack.getItemDamage())))) {
                        if (movingobjectposition.entityHit instanceof EntityLivingBase) {
                            float f4;
                            EntityLivingBase entitylivingbase = (EntityLivingBase)movingobjectposition.entityHit;
                            if (!this.worldObj.isRemote) {
                                entitylivingbase.setArrowCountInEntity(entitylivingbase.getArrowCountInEntity() + 1);
                            }
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
                        this.playSound("random.bowhit", 1.0f, 1.2f / (this.rand.nextFloat() * 0.2f + 0.9f));
                        if (!(movingobjectposition.entityHit instanceof EntityEnderman)) {
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
                } else {
                    this.blockX = movingobjectposition.blockX;
                    this.blockY = movingobjectposition.blockY;
                    this.blockZ = movingobjectposition.blockZ;
                    this.collisionBlock = this.worldObj.getBlock(this.blockX, this.blockY, this.blockZ);
                    this.inData = this.worldObj.getBlockMetadata(this.blockX, this.blockY, this.blockZ);
                    this.motionX = (float)(movingobjectposition.hitVec.xCoord - this.posX);
                    this.motionY = (float)(movingobjectposition.hitVec.yCoord - this.posY);
                    this.motionZ = (float)(movingobjectposition.hitVec.zCoord - this.posZ);
                    float f2 = MathHelper.sqrt_double((double)(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ));
                    this.posX -= this.motionX / (double)f2 * (double)0.05f;
                    this.posY -= this.motionY / (double)f2 * (double)0.05f;
                    this.posZ -= this.motionZ / (double)f2 * (double)0.05f;
                    this.playSound("random.bowhit", 1.0f, 1.2f / (this.rand.nextFloat() * 0.2f + 0.9f));
                    this.inGround = true;
                    this.arrowShake = 7;
                    this.setIsCritical(false);
                    if (this.collisionBlock.getMaterial() != Material.air) {
                        this.collisionBlock.onEntityCollidedWithBlock(this.worldObj, this.blockX, this.blockY, this.blockZ, (Entity)this);
                    }
                }
            }
            if (this.getIsCritical()) {
                for (i = 0; i < 4; ++i) {
                    this.worldObj.spawnParticle("crit", this.posX + this.motionX * (double)i / 4.0, this.posY + this.motionY * (double)i / 4.0, this.posZ + this.motionZ * (double)i / 4.0, -this.motionX, -this.motionY + 0.2, -this.motionZ);
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
            f1 = 0.05f;
            if (this.isInWater()) {
                for (int l = 0; l < 4; ++l) {
                    float f4 = 0.25f;
                    this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double)f4, this.posY - this.motionY * (double)f4, this.posZ - this.motionZ * (double)f4, this.motionX, this.motionY, this.motionZ);
                }
                f3 = 0.8f;
            }
            if (this.isWet()) {
                this.extinguish();
            }
            this.motionX *= (double)f3;
            this.motionY *= (double)f3;
            this.motionZ *= (double)f3;
            this.motionY -= (double)f1;
            this.setPosition(this.posX, this.posY, this.posZ);
            this.func_145775_I();
        }
    }

    public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        p_70014_1_.setShort("xTile", (short)this.blockX);
        p_70014_1_.setShort("yTile", (short)this.blockY);
        p_70014_1_.setShort("zTile", (short)this.blockZ);
        p_70014_1_.setShort("life", (short)this.ticksInGround);
        p_70014_1_.setByte("inTile", (byte)Block.getIdFromBlock((Block)this.collisionBlock));
        p_70014_1_.setByte("inData", (byte)this.inData);
        p_70014_1_.setByte("shake", (byte)this.arrowShake);
        p_70014_1_.setByte("inGround", (byte)(this.inGround ? 1 : 0));
        p_70014_1_.setByte("pickup", (byte)this.canBePickedUp);
        p_70014_1_.setDouble("damage", this.damage);
        p_70014_1_.setInteger("arrowType", this.arrowStack.getItemDamage());
    }

    public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        this.blockX = p_70037_1_.getShort("xTile");
        this.blockY = p_70037_1_.getShort("yTile");
        this.blockZ = p_70037_1_.getShort("zTile");
        this.ticksInGround = p_70037_1_.getShort("life");
        this.collisionBlock = Block.getBlockById((int)(p_70037_1_.getByte("inTile") & 0xFF));
        this.inData = p_70037_1_.getByte("inData") & 0xFF;
        this.arrowShake = p_70037_1_.getByte("shake") & 0xFF;
        this.inGround = p_70037_1_.getByte("inGround") == 1;
        this.arrowStack = new ItemStack((Item)WEArcheryItems.specialarrow, 1, this.normalizeArrowType(p_70037_1_.getInteger("arrowType")));
        this.dataWatcher.updateObject(17, (Object)this.arrowStack.getItemDamage());
        if (p_70037_1_.hasKey("damage", 99)) {
            this.damage = p_70037_1_.getDouble("damage");
        }
        if (p_70037_1_.hasKey("pickup", 99)) {
            this.canBePickedUp = p_70037_1_.getByte("pickup");
        } else if (p_70037_1_.hasKey("player", 99)) {
            this.canBePickedUp = p_70037_1_.getBoolean("player") ? 1 : 0;
        }
    }

    public void onCollideWithPlayer(EntityPlayer player) {
        if (!this.worldObj.isRemote && this.inGround && this.arrowShake <= 0) {
            boolean flag;
            boolean bl = flag = this.canBePickedUp == 1 || this.canBePickedUp == 2 && player.capabilities.isCreativeMode;
            if (this.canBePickedUp == 1 && !player.inventory.addItemStackToInventory(new ItemStack(this.arrowStack.getItem(), 1, this.arrowStack.getItemDamage()))) {
                flag = false;
            }
            if (flag) {
                this.playSound("random.pop", 0.2f, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
                player.onItemPickup((Entity)this, 1);
                this.setDead();
            }
        }
    }

    protected boolean canTriggerWalking() {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public float getShadowSize() {
        return 0.0f;
    }

    public void setDamage(double p_70239_1_) {
        this.damage = p_70239_1_;
    }

    public double getDamage() {
        return this.damage;
    }

    public void setKnockbackStrength(int p_70240_1_) {
        this.knockbackStrength = p_70240_1_;
    }

    public boolean canAttackWithItem() {
        return false;
    }

    public void setIsCritical(boolean p_70243_1_) {
        byte b0 = this.dataWatcher.getWatchableObjectByte(16);
        if (p_70243_1_) {
            this.dataWatcher.updateObject(16, (Object)((byte)(b0 | 1)));
        } else {
            this.dataWatcher.updateObject(16, (Object)((byte)(b0 & 0xFFFFFFFE)));
        }
    }

    public boolean getIsCritical() {
        byte b0 = this.dataWatcher.getWatchableObjectByte(16);
        return (b0 & 1) != 0;
    }

    public int getArrowType() {
        return this.normalizeArrowType(this.dataWatcher.getWatchableObjectInt(17));
    }

    private void setArrowStack(ItemStack ammo) {
        if (ammo == null || ammo.getItem() == null || ammo.getItem() != WEArcheryItems.specialarrow) {
            this.arrowStack = new ItemStack((Item)WEArcheryItems.specialarrow, 1, 0);
            this.dataWatcher.updateObject(17, (Object)0);
            return;
        }
        this.arrowStack = ammo.copy();
        this.arrowStack.stackSize = 1;
        int arrowType = this.normalizeArrowType(this.arrowStack.getItemDamage());
        this.arrowStack.setItemDamage(arrowType);
        this.dataWatcher.updateObject(17, (Object)arrowType);
    }

    private int normalizeArrowType(int type) {
        return SpecialArrow.normalizeArrowType(type);
    }

    @Override
    public Entity getThrower() {
        return this.shootingEntity;
    }

    @Override
    public void setThrower(Entity entity) {
        this.shootingEntity = entity;
    }
}
