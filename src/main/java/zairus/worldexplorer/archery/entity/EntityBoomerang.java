/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.Event
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IProjectile
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.item.EntityXPOrb
 *  net.minecraft.entity.monster.EntityEnderman
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Blocks
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
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.player.EntityItemPickupEvent
 */
package zairus.worldexplorer.archery.entity;

import cpw.mods.fml.common.eventhandler.Event;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import zairus.worldexplorer.archery.items.WEArcheryItems;
import zairus.worldexplorer.core.WorldExplorer;

public class EntityBoomerang
extends Entity
implements IProjectile {
    private int positionX = -1;
    private int positionY = -1;
    private int positionZ = -1;
    private Block collisionBlock;
    private int ticksInAir;
    private double damage = 2.5;
    private int knockbackStrength;
    private ItemStack thrownBoomerang;
    private float strength;
    private double boomerangReach = 16.0;
    private boolean destReached = false;
    private boolean canCollectItems = true;
    private List<ItemStack> inventory = new ArrayList<ItemStack>();
    private List<EntityXPOrb> orbsCollected = new ArrayList<EntityXPOrb>();
    private int capacity = 1;
    public Entity shootingEntity;

    public EntityBoomerang(World world) {
        super(world);
        this.renderDistanceWeight = 10.0;
        this.setSize(0.5f, 0.5f);
    }

    public EntityBoomerang(World world, double posX, double posY, double posZ) {
        super(world);
        this.renderDistanceWeight = 10.0;
        this.setSize(0.5f, 0.5f);
        this.setPosition(posX, posY, posZ);
        this.yOffset = 0.0f;
    }

    public EntityBoomerang(World world, EntityLivingBase entityShooter, EntityLivingBase entityTarget, float p_i1755_4_, float p_i1755_5_) {
        super(world);
        this.renderDistanceWeight = 10.0;
        this.shootingEntity = entityShooter;
        this.posY = entityShooter.posY + (double)entityShooter.getEyeHeight() - (double)0.1f;
        double d0 = entityTarget.posX - entityShooter.posX;
        double d1 = entityTarget.boundingBox.minY + (double)(entityTarget.height / 3.0f) - this.posY;
        double d2 = entityTarget.posZ - entityShooter.posZ;
        double d3 = MathHelper.sqrt_double((double)(d0 * d0 + d2 * d2));
        if (d3 >= 1.0E-7) {
            float f2 = (float)(Math.atan2(d2, d0) * 180.0 / Math.PI) - 90.0f;
            float f3 = (float)(-(Math.atan2(d1, d3) * 180.0 / Math.PI));
            double d4 = d0 / d3;
            double d5 = d2 / d3;
            this.setLocationAndAngles(entityShooter.posX + d4, this.posY, entityShooter.posZ + d5, f2, f3);
            this.yOffset = 0.0f;
            float f4 = (float)d3 * 0.2f;
            this.setThrowableHeading(d0, d1 + (double)f4, d2, p_i1755_4_, p_i1755_5_);
        }
    }

    public EntityBoomerang(World world, EntityLivingBase player, float p_i1756_3_) {
        super(world);
        this.renderDistanceWeight = 10.0;
        this.shootingEntity = player;
        this.setSize(0.5f, 0.5f);
        this.setLocationAndAngles(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ, player.rotationYaw, player.rotationPitch);
        this.posX -= (double)(MathHelper.cos((float)(this.rotationYaw / 180.0f * (float)Math.PI)) * 0.16f);
        this.posY -= 0.6000000014901161;
        this.posZ -= (double)(MathHelper.sin((float)(this.rotationYaw / 180.0f * (float)Math.PI)) * 0.16f);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0f;
        this.motionX = -MathHelper.sin((float)(this.rotationYaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(this.rotationPitch / 180.0f * (float)Math.PI));
        this.motionZ = MathHelper.cos((float)(this.rotationYaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(this.rotationPitch / 180.0f * (float)Math.PI));
        this.motionY = -MathHelper.sin((float)(this.rotationPitch / 180.0f * (float)Math.PI));
        this.strength = p_i1756_3_ * 1.5f;
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, this.strength, 1.0f);
    }

    private void returnToSender() {
        double d0 = this.shootingEntity.posX - this.posX;
        double d1 = this.shootingEntity.boundingBox.minY + (double)(this.shootingEntity.height / 3.0f) - this.posY;
        d1 -= 0.6;
        double d2 = this.shootingEntity.posZ - this.posZ;
        double d3 = MathHelper.sqrt_double((double)(d0 * d0 + d2 * d2));
        if (d3 >= 1.0E-7) {
            float f2 = (float)(Math.atan2(d2, d0) * 180.0 / Math.PI) - 90.0f;
            float f3 = (float)(-(Math.atan2(d1, d3) * 180.0 / Math.PI));
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, f2, f3);
            float f4 = (float)d3 * 0.2f;
            this.setThrowableHeading(d0, d1 + (double)f4, d2, this.strength, 1.0f);
        }
    }

    public void setReach(double reach) {
        this.boomerangReach = reach;
    }

    public void setCanCollectItems(boolean canCollect) {
        this.canCollectItems = canCollect;
    }

    public void setInventoryCapacity(int cap) {
        this.capacity = cap;
    }

    public List<ItemStack> getInventory() {
        return this.inventory;
    }

    public void setThrownBoomerang(ItemStack stack) {
        this.thrownBoomerang = stack;
    }

    public ItemStack getThrownBoomerang() {
        return this.thrownBoomerang;
    }

    protected void entityInit() {
        this.dataWatcher.addObject(16, (Object)0);
        this.dataWatcher.addObject(10, (Object)new ItemStack((Item)WEArcheryItems.boomerang));
    }

    public void setKnockbackStrength(int knockback) {
        this.knockbackStrength = knockback;
    }

    public void setThrowableHeading(double dirX, double dirY, double dirZ) {
        this.setThrowableHeading(dirX, dirY, dirZ, this.strength, 1.0f);
    }

    public void setThrowableHeading(double dirX, double dirY, double dirZ, float p_70186_7_, float p_70186_8_) {
        float f2 = MathHelper.sqrt_double((double)(dirX * dirX + dirY * dirY + dirZ * dirZ));
        dirX /= (double)f2;
        dirY /= (double)f2;
        dirZ /= (double)f2;
        dirX += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * (double)0.0075f * (double)p_70186_8_;
        dirY += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * (double)0.0075f * (double)p_70186_8_;
        dirZ += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * (double)0.0075f * (double)p_70186_8_;
        this.motionX = dirX *= (double)p_70186_7_;
        this.motionY = dirY *= (double)p_70186_7_;
        this.motionZ = dirZ *= (double)p_70186_7_;
        float f3 = MathHelper.sqrt_double((double)(dirX * dirX + dirZ * dirZ));
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(dirX, dirZ) * 180.0 / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(dirY, f3) * 180.0 / Math.PI);
    }

    public void onUpdate() {
        EntityPlayer entityplayer;
        float f1;
        int i;
        Block block;
        super.onUpdate();
        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            float f = MathHelper.sqrt_double((double)(this.motionX * this.motionX + this.motionZ * this.motionZ));
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, f) * 180.0 / Math.PI);
        }
        this.collisionBlock = block = this.worldObj.getBlock(this.positionX, this.positionY, this.positionZ);
        if (block.getMaterial() != Material.air) {
            boolean c_flag;
            block.setBlockBoundsBasedOnState((IBlockAccess)this.worldObj, this.positionX, this.positionY, this.positionZ);
            boolean bl = c_flag = block == Blocks.grass || block == Blocks.tallgrass;
            if (c_flag) {
                try {
                    for (int p_i = 0; p_i < (c_flag ? 35 : 15); ++p_i) {
                        this.worldObj.spawnParticle("blockcrack_" + Block.getIdFromBlock((Block)block) + "_0", (double)this.positionX, (double)this.positionY, (double)this.positionZ, 0.0, 0.0, 0.0);
                    }
                }
                catch (Exception e) {
                    WorldExplorer.log(e.getMessage());
                }
                if (block.stepSound.getBreakSound().length() > 0) {
                    this.playSound(block.stepSound.getBreakSound(), 1.0f, 1.2f / (this.rand.nextFloat() * 0.2f + 0.9f));
                }
                block.breakBlock(this.worldObj, this.positionX, this.positionY, this.positionZ, block, 1);
            } else {
                this.destReached = true;
            }
        }
        ++this.ticksInAir;
        if (this.ticksInAir > 100) {
            this.returnToPlayer();
            return;
        }
        if ((double)this.ticksInAir / 2.0 - Math.floor((double)this.ticksInAir / 2.0) == 0.0) {
            this.playSound("worldexplorer:boomerang_swoosh", 2.5f, 1.2f / (this.rand.nextFloat() * 0.2f + 0.9f));
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
        for (i = 0; i < list.size(); ++i) {
            double d1;
            AxisAlignedBB axisalignedbb1;
            MovingObjectPosition movingobjectposition1;
            Entity entity1 = (Entity)list.get(i);
            if (entity1 instanceof EntityXPOrb) {
                this.orbsCollected.add((EntityXPOrb)entity1);
            }
            if (entity1 instanceof EntityItem && this.canCollectItems && !this.isDead && this.inventory.size() < this.capacity && ((EntityItem)entity1).getEntityItem() != this.thrownBoomerang) {
                this.inventory.add(((EntityItem)entity1).getEntityItem().copy());
                if (!this.worldObj.isRemote) {
                    entity1.setDead();
                }
            }
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
                DamageSource damagesource = null;
                damagesource = this.shootingEntity == null ? new EntityDamageSourceIndirect("boomerang", (Entity)this, (Entity)this).setProjectile() : new EntityDamageSourceIndirect("boomerang", (Entity)this, this.shootingEntity).setProjectile();
                if (this.isBurning() && !(movingobjectposition.entityHit instanceof EntityEnderman)) {
                    movingobjectposition.entityHit.setFire(5);
                }
                if (movingobjectposition.entityHit.attackEntityFrom(damagesource, (float)k)) {
                    if (this.shootingEntity instanceof EntityLivingBase) {
                        boolean dFlag = true;
                        if (this.shootingEntity instanceof EntityPlayer && ((EntityPlayer)this.shootingEntity).capabilities.isCreativeMode) {
                            dFlag = false;
                        }
                        if (dFlag && this.thrownBoomerang.attemptDamageItem(1, ((EntityLivingBase)this.shootingEntity).getRNG()) && this.thrownBoomerang.getItemDamage() >= this.thrownBoomerang.getMaxDamage()) {
                            this.setDead();
                            this.playSound("random.break", 1.0f, 1.2f / (this.rand.nextFloat() * 0.2f + 0.9f));
                            return;
                        }
                    }
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
                        this.destReached = true;
                    }
                } else {
                    this.destReached = true;
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
        if (this.boomerangReach > 32.0) {
            for (i = 0; i < 2; ++i) {
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
            double distance = Vec3.createVectorHelper((double)this.shootingEntity.posX, (double)this.shootingEntity.posY, (double)this.shootingEntity.posZ).distanceTo(Vec3.createVectorHelper((double)this.posX, (double)this.posY, (double)this.posZ));
            if (distance >= this.boomerangReach && !this.destReached) {
                this.destReached = true;
            }
            if (this.destReached) {
                this.returnToSender();
            }
        } else {
            f3 = 0.99f;
            this.motionX *= (double)f3;
            this.motionY *= (double)f3;
            this.motionZ *= (double)f3;
            this.motionY -= (double)f1;
        }
        for (int o_i = 0; o_i < this.orbsCollected.size(); ++o_i) {
            this.orbsCollected.get(o_i).setPosition(this.posX, this.posY, this.posZ);
        }
        this.setPosition(this.posX, this.posY, this.posZ);
        this.func_145775_I();
    }

    public double getDamage() {
        return this.damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        this.positionX = tagCompound.getShort("xTile");
        this.positionY = tagCompound.getShort("yTile");
        this.positionZ = tagCompound.getShort("zTile");
        this.collisionBlock = Block.getBlockById((int)(tagCompound.getByte("inTile") & 0xFF));
        if (tagCompound.hasKey("damage", 99)) {
            this.damage = tagCompound.getDouble("damage");
        }
    }

    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        tagCompound.setShort("xTile", (short)this.positionX);
        tagCompound.setShort("yTile", (short)this.positionY);
        tagCompound.setShort("zTile", (short)this.positionZ);
        tagCompound.setByte("inTile", (byte)Block.getIdFromBlock((Block)this.collisionBlock));
        tagCompound.setDouble("damage", this.damage);
    }

    public void onCollideWithPlayer(EntityPlayer player) {
        if (!(this.shootingEntity instanceof EntityPlayer)) {
            return;
        }
        if (!this.worldObj.isRemote && this.thrownBoomerang != null && this.ticksInAir > 2 && player == (EntityPlayer)this.shootingEntity) {
            this.returnToPlayer(true);
        }
    }

    private void returnToPlayer() {
        this.returnToPlayer(false);
    }

    private void returnToPlayer(boolean catched) {
        if (this.isDead) {
            return;
        }
        if (this.thrownBoomerang == null) {
            if (!this.isDead) {
                this.setDead();
            }
            return;
        }
        if (this.thrownBoomerang.getItemDamage() >= this.thrownBoomerang.getMaxDamage()) {
            if (!this.isDead) {
                this.setDead();
            }
            return;
        }
        if (this.shootingEntity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)this.shootingEntity;
            this.canCollectItems = false;
            if (this.isBurning()) {
                player.setFire(3);
            }
            if (!player.inventory.addItemStackToInventory(this.thrownBoomerang)) {
                if (!this.worldObj.isRemote) {
                    this.worldObj.spawnEntityInWorld((Entity)new EntityItem(this.worldObj, catched ? this.posX : this.shootingEntity.posX, catched ? this.posY : this.shootingEntity.posY, catched ? this.posZ : this.shootingEntity.posZ, this.thrownBoomerang));
                }
            } else {
                EntityItemPickupEvent event = new EntityItemPickupEvent(player, new EntityItem(this.worldObj, catched ? this.posX : this.shootingEntity.posX, catched ? this.posY : this.shootingEntity.posY, catched ? this.posZ : this.shootingEntity.posZ, this.thrownBoomerang));
                MinecraftForge.EVENT_BUS.post((Event)event);
            }
            for (int i = 0; i < this.inventory.size(); ++i) {
                if (!player.inventory.addItemStackToInventory(this.inventory.get(i))) {
                    if (this.worldObj.isRemote) continue;
                    this.worldObj.spawnEntityInWorld((Entity)new EntityItem(this.worldObj, catched ? this.posX : this.shootingEntity.posX, catched ? this.posY : this.shootingEntity.posY, catched ? this.posZ : this.shootingEntity.posZ, this.inventory.get(i)));
                    continue;
                }
                EntityItemPickupEvent event = new EntityItemPickupEvent(player, new EntityItem(this.worldObj, catched ? this.posX : this.shootingEntity.posX, catched ? this.posY : this.shootingEntity.posY, catched ? this.posZ : this.shootingEntity.posZ, this.inventory.get(i)));
                MinecraftForge.EVENT_BUS.post((Event)event);
            }
            this.setDead();
            this.playSound("worldexplorer:boomerang_catch", 1.0f, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
            player.onItemPickup((Entity)this, 1);
        }
    }
}

