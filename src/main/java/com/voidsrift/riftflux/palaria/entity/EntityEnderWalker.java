package com.voidsrift.riftflux.palaria.entity;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.palaria.PalariaMobContent;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityEnderWalker extends EntityMob {
    private int teleportDelay;
    private int stareTimer;

    public EntityEnderWalker(World world) {
        super(world);
        setSize(1.4F, 0.9F);
        experienceValue = 30;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(16, Byte.valueOf((byte) 0));
        dataWatcher.addObject(17, Byte.valueOf((byte) 0));
        dataWatcher.addObject(18, Byte.valueOf((byte) 0));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(ModConfig.palariaEnderWalkerMaxHealth);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3D);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(30.0D);
    }

    @Override
    protected Entity findPlayerToAttack() {
        EntityPlayer player = worldObj.getClosestVulnerablePlayerToEntity(this, 64.0D);
        if (player != null && shouldAttackPlayer(player)) {
            if (stareTimer == 0) {
                worldObj.playSoundAtEntity(player, "mob.endermen.stare", 1.0F, 1.0F);
            }
            if (++stareTimer == 5) {
                stareTimer = 0;
                setScreaming(true);
                return player;
            }
        } else {
            stareTimer = 0;
        }
        return null;
    }

    protected boolean shouldAttackPlayer(EntityPlayer player) {
        ItemStack helmet = player.inventory.armorInventory[3];
        if (helmet != null && helmet.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
            return false;
        }
        Vec3 look = player.getLook(1.0F).normalize();
        Vec3 toMob = Vec3.createVectorHelper(posX - player.posX, boundingBox.minY + (double) (height / 2.0F) - (player.posY + (double) player.getEyeHeight()), posZ - player.posZ);
        double distance = toMob.lengthVector();
        toMob = toMob.normalize();
        double dot = look.dotProduct(toMob);
        return dot > 1.0D - 0.025D / distance && player.canEntityBeSeen(this);
    }

    @Override
    public void onLivingUpdate() {
        if (isWet()) {
            attackEntityFrom(DamageSource.drown, 1.0F);
        }

        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(entityToAttack != null ? 6.5D : 0.3D);
        for (int i = 0; i < 2; ++i) {
            worldObj.spawnParticle("portal",
                    posX + (rand.nextDouble() - 0.5D) * (double) width,
                    posY + rand.nextDouble() * (double) height - 0.25D,
                    posZ + (rand.nextDouble() - 0.5D) * (double) width,
                    (rand.nextDouble() - 0.5D) * 2.0D,
                    -rand.nextDouble(),
                    (rand.nextDouble() - 0.5D) * 2.0D);
        }

        if (worldObj.isDaytime() && !worldObj.isRemote) {
            float brightness = getBrightness(1.0F);
            if (brightness > 0.5F && worldObj.canBlockSeeTheSky(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ))
                    && rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F) {
                entityToAttack = null;
                setScreaming(false);
                teleportRandomly();
            }
        }

        if (isWet() || isBurning()) {
            entityToAttack = null;
            setScreaming(false);
            teleportRandomly();
        }

        isJumping = false;
        if (entityToAttack != null) {
            faceEntity(entityToAttack, 100.0F, 100.0F);
        }
        if (!worldObj.isRemote && isEntityAlive()) {
            if (entityToAttack != null) {
                if (entityToAttack instanceof EntityPlayer && shouldAttackPlayer((EntityPlayer) entityToAttack)) {
                    moveStrafing = 0.0F;
                    moveForward = 0.0F;
                    getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(2.0D);
                    if (entityToAttack.getDistanceSqToEntity(this) < 16.0D) {
                        teleportRandomly();
                    }
                    teleportDelay = 0;
                } else if (entityToAttack.getDistanceSqToEntity(this) > 256.0D && teleportDelay++ >= 30 && teleportToEntity(entityToAttack)) {
                    teleportDelay = 0;
                }
            } else {
                setScreaming(false);
                teleportDelay = 0;
            }
        }
        super.onLivingUpdate();
    }

    protected boolean teleportRandomly() {
        double x = posX + (rand.nextDouble() - 0.5D) * 64.0D;
        double y = posY + (double) (rand.nextInt(64) - 32);
        double z = posZ + (rand.nextDouble() - 0.5D) * 64.0D;
        return teleportTo(x, y, z);
    }

    protected boolean teleportToEntity(Entity target) {
        Vec3 vector = Vec3.createVectorHelper(posX - target.posX, boundingBox.minY + (double) (height / 2.0F) - target.posY + (double) target.getEyeHeight(), posZ - target.posZ).normalize();
        double distance = 16.0D;
        double x = posX + (rand.nextDouble() - 0.5D) * 8.0D - vector.xCoord * distance;
        double y = posY + (double) (rand.nextInt(16) - 8) - vector.yCoord * distance;
        double z = posZ + (rand.nextDouble() - 0.5D) * 8.0D - vector.zCoord * distance;
        return teleportTo(x, y, z);
    }

    protected boolean teleportTo(double x, double y, double z) {
        double oldX = posX;
        double oldY = posY;
        double oldZ = posZ;
        posX = x;
        posY = y;
        posZ = z;
        boolean valid = false;
        int blockX = MathHelper.floor_double(posX);
        int blockY = MathHelper.floor_double(posY);
        int blockZ = MathHelper.floor_double(posZ);

        if (worldObj.blockExists(blockX, blockY, blockZ)) {
            boolean foundGround = false;
            while (!foundGround && blockY > 0) {
                Block below = worldObj.getBlock(blockX, blockY - 1, blockZ);
                if (below.getMaterial().blocksMovement()) {
                    foundGround = true;
                } else {
                    --posY;
                    --blockY;
                }
            }
            if (foundGround) {
                setPosition(posX, posY, posZ);
                valid = worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox);
            }
        }

        if (!valid) {
            setPosition(oldX, oldY, oldZ);
            return false;
        }

        for (int i = 0; i < 128; ++i) {
            double step = (double) i / 127.0D;
            float mx = (rand.nextFloat() - 0.5F) * 0.2F;
            float my = (rand.nextFloat() - 0.5F) * 0.2F;
            float mz = (rand.nextFloat() - 0.5F) * 0.2F;
            double px = oldX + (posX - oldX) * step + (rand.nextDouble() - 0.5D) * (double) width * 2.0D;
            double py = oldY + (posY - oldY) * step + rand.nextDouble() * (double) height;
            double pz = oldZ + (posZ - oldZ) * step + (rand.nextDouble() - 0.5D) * (double) width * 2.0D;
            worldObj.spawnParticle("portal", px, py, pz, mx, my, mz);
        }
        worldObj.playSoundEffect(oldX, oldY, oldZ, "mob.endermen.portal", 1.0F, 1.0F);
        playSound("mob.endermen.portal", 1.0F, 1.0F);
        return true;
    }

    @Override
    protected String getLivingSound() {
        return isScreaming() ? "mob.endermen.scream" : "mob.endermen.idle";
    }

    @Override
    protected String getHurtSound() {
        return "mob.endermen.hit";
    }

    @Override
    protected String getDeathSound() {
        return "mob.endermen.death";
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        int pearls = rand.nextInt(2) + rand.nextInt(1 + looting);
        int shards = rand.nextInt(3) + rand.nextInt(1 + looting);
        for (int i = 0; i < pearls; ++i) dropItem(Items.ender_pearl, 1);
        for (int i = 0; i < shards; ++i) dropItem(PalariaMobContent.endermiteShard, 1);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (isEntityInvulnerable()) {
            return false;
        }
        setScreaming(true);
        if (source instanceof EntityDamageSourceIndirect || source.isProjectile()) {
            for (int i = 0; i < 64; ++i) {
                if (teleportRandomly()) {
                    return true;
                }
            }
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }

    public boolean isScreaming() {
        return dataWatcher.getWatchableObjectByte(18) > 0;
    }

    public void setScreaming(boolean screaming) {
        dataWatcher.updateObject(18, Byte.valueOf((byte) (screaming ? 1 : 0)));
    }
}
