/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  cpw.mods.fml.common.FMLLog
 *  org.apache.commons.lang3.mutable.MutableDouble
 *  org.apache.commons.lang3.mutable.MutableFloat
 *  org.apache.logging.log4j.Level
 */
package de.sanandrew.mods.claysoldiers.entity;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import cpw.mods.fml.common.FMLLog;
import de.sanandrew.core.manpack.util.EnumNbtTypes;
import de.sanandrew.core.manpack.util.javatuples.Pair;
import de.sanandrew.core.manpack.util.javatuples.Triplet;
import de.sanandrew.mods.claysoldiers.entity.SoldierCloakHelper;
import de.sanandrew.mods.claysoldiers.entity.mount.IMount;
import de.sanandrew.mods.claysoldiers.entity.projectile.ISoldierProjectile;
import de.sanandrew.mods.claysoldiers.network.PacketManager;
import de.sanandrew.mods.claysoldiers.network.ParticlePacketSender;
import de.sanandrew.mods.claysoldiers.util.BugfixHelper;
import de.sanandrew.mods.claysoldiers.util.ClaySoldiersMod;
import de.sanandrew.mods.claysoldiers.util.IDisruptable;
import de.sanandrew.mods.claysoldiers.util.ModConfig;
import de.sanandrew.mods.claysoldiers.util.RegistryItems;
import de.sanandrew.mods.claysoldiers.util.soldier.ClaymanTeam;
import de.sanandrew.mods.claysoldiers.util.soldier.EnumMethodState;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.ASoldierEffect;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.SoldierEffectInst;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.SoldierEffects;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.ASoldierUpgrade;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgradeInst;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgrades;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.logging.log4j.Level;

public class EntityClayMan
extends EntityCreature
implements IDisruptable {
    private static final int DW_TEAM = 20;
    private static final int DW_MISC_COLOR = 21;
    private static final int DW_IS_TEXTURE_RARE_OR_UNIQUE = 22;
    private static final int DW_TEXTURE_INDEX = 23;
    public final SoldierCloakHelper cloakHelper = new SoldierCloakHelper();
    public ItemStack dollItem = null;
    public Triplet<Double, Double, Double> knockBack = Triplet.with(0.7, 0.7, 0.7);
    public boolean canMove = true;
    public boolean nexusSpawn = false;
    private final Map<ASoldierUpgrade, SoldierUpgradeInst> p_upgrades = new ConcurrentHashMap<ASoldierUpgrade, SoldierUpgradeInst>();
    private final Map<ASoldierEffect, SoldierEffectInst> p_effects = new ConcurrentHashMap<ASoldierEffect, SoldierEffectInst>();
    private final long[] p_upgradeRenderFlags = new long[2];
    private final long[] p_effectRenderFlags = new long[2];
    private Entity p_targetFollow = null;
    private Collection p_entitiesInRange;

    public EntityClayMan(World world) {
        super(world);
        this.stepHeight = 0.1f;
        this.renderDistanceWeight = 5.0;
        this.setSize(0.17f, 0.4f);
        this.yOffset = 0.01f;
        this.ignoreFrustumCheck = true;
        this.jumpMovementFactor = 0.2f;
    }

    public EntityClayMan(World world, String team) {
        this(world);
        this.dataWatcher.updateObject(20, team);
        this.setupTexture(this.rand.nextInt(8196) == 0, false);
    }

    @Override
    public void moveEntity(double motionX, double motionY, double motionZ) {
        if (this.canMove) {
            super.moveEntity(motionX, motionY, motionZ);
        } else {
            super.moveEntity(0.0, motionY > 0.0 ? motionY / 2.0 : motionY, 0.0);
        }
    }

    @Override
    public void disrupt() {
        if (!this.getCustomNameTag().startsWith("[UNDISRUPTABLE]")) {
            this.attackEntityFrom(IDisruptable.DISRUPT_DAMAGE, 99999.0f);
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.cloakHelper.onUpdate(this.posX, this.posY, this.posZ);
        if (!this.canMove) {
            this.motionX = 0.0;
            this.motionZ = 0.0;
            this.isJumping = false;
        }
        this.canMove = true;
        Iterator<Map.Entry<ASoldierUpgrade, SoldierUpgradeInst>> iterUpgrades = this.p_upgrades.entrySet().iterator();
        while (iterUpgrades.hasNext()) {
            SoldierUpgradeInst upg;
            if (!this.worldObj.isRemote) {
                upg = iterUpgrades.next().getValue();
                if (!upg.getUpgrade().onUpdate(this, upg)) continue;
                iterUpgrades.remove();
                continue;
            }
            upg = iterUpgrades.next().getValue();
            upg.getUpgrade().onClientUpdate(this, upg);
        }
        Iterator<Map.Entry<ASoldierEffect, SoldierEffectInst>> iterEffects = this.p_effects.entrySet().iterator();
        while (iterEffects.hasNext()) {
            SoldierEffectInst upg;
            if (!this.worldObj.isRemote) {
                upg = iterEffects.next().getValue();
                if (!upg.getEffect().onUpdate(this, upg)) continue;
                iterEffects.remove();
                continue;
            }
            upg = iterEffects.next().getValue();
            upg.getEffect().onClientUpdate(this, upg);
        }
        if (this.ticksExisted % 5 == 0) {
            this.updateUpgradeEffectRenders();
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setString("team", this.getClayTeam());
        nbt.setByte("miscColor", this.dataWatcher.getWatchableObjectByte(21));
        nbt.setByte("isRareOrUnique", this.dataWatcher.getWatchableObjectByte(22));
        nbt.setInteger("textureIndex", this.dataWatcher.getWatchableObjectInt(23));
        nbt.setBoolean("canMove", this.canMove);
        nbt.setBoolean("nexusSpawned", this.nexusSpawn);
        if (this.dollItem != null) {
            NBTTagCompound stackNbt = new NBTTagCompound();
            this.dollItem.writeToNBT(stackNbt);
            nbt.setTag("dollItem", stackNbt);
        }
        NBTTagList upgNbtList = new NBTTagList();
        for (SoldierUpgradeInst upg : this.p_upgrades.values()) {
            NBTTagCompound savedUpg = new NBTTagCompound();
            savedUpg.setString("name", SoldierUpgrades.getName(upg.getUpgrade()));
            savedUpg.setTag("data", upg.getNbtTag());
            NBTTagCompound upgItem = upg.saveStoredItemToNBT();
            if (upgItem != null) {
                savedUpg.setTag("item", upgItem);
            }
            upgNbtList.appendTag(savedUpg);
        }
        nbt.setTag("upgrade", upgNbtList);
        NBTTagList effNbtList = new NBTTagList();
        for (SoldierEffectInst eff : this.p_effects.values()) {
            NBTTagCompound savedEff = new NBTTagCompound();
            savedEff.setString("name", SoldierEffects.getEffectName(eff.getEffect()));
            savedEff.setTag("data", eff.getNbtTag());
            effNbtList.appendTag(savedEff);
        }
        nbt.setTag("effect", effNbtList);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.dataWatcher.updateObject(20, nbt.getString("team"));
        this.dataWatcher.updateObject(21, nbt.getByte("miscColor"));
        this.dataWatcher.updateObject(22, nbt.getByte("isRareOrUnique"));
        this.dataWatcher.updateObject(23, nbt.getInteger("textureIndex"));
        this.canMove = nbt.getBoolean("canMove");
        this.nexusSpawn = nbt.getBoolean("nexusSpawned");
        if (nbt.hasKey("dollItem")) {
            this.dollItem = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("dollItem"));
        }
        NBTTagList upgNbtList = nbt.getTagList("upgrade", EnumNbtTypes.NBT_COMPOUND.ordinal());
        for (int i = 0; i < upgNbtList.tagCount(); ++i) {
            NBTTagCompound savedUpg = upgNbtList.getCompoundTagAt(i);
            SoldierUpgradeInst upgInst = new SoldierUpgradeInst(SoldierUpgrades.getUpgrade(savedUpg.getString("name")));
            upgInst.setNbtTag(savedUpg.getCompoundTag("data"));
            if (savedUpg.hasKey("item")) {
                upgInst.readStoredItemFromNBT(savedUpg.getCompoundTag("item"));
            }
            this.p_upgrades.put(upgInst.getUpgrade(), upgInst);
        }
        NBTTagList effNbtList = nbt.getTagList("effect", EnumNbtTypes.NBT_COMPOUND.ordinal());
        for (int i = 0; i < effNbtList.tagCount(); ++i) {
            NBTTagCompound savedEff = effNbtList.getCompoundTagAt(i);
            SoldierEffectInst effInst = new SoldierEffectInst(SoldierEffects.getEffect(savedEff.getString("name")));
            effInst.setNbtTag(savedEff.getCompoundTag("data"));
            this.p_effects.put(effInst.getEffect(), effInst);
        }
    }

    @Override
    public Entity getEntityToAttack() {
        return this.entityToAttack;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (!(source.getEntity() instanceof EntityPlayer) && source != IDisruptable.DISRUPT_DAMAGE) {
            if (this.ridingEntity != null && this.rand.nextInt(4) == 0) {
                this.ridingEntity.attackEntityFrom(source, damage);
                return false;
            }
        } else {
            damage = 10000.0f;
        }
        if (!this.worldObj.isRemote) {
            for (Map.Entry<ASoldierUpgrade, SoldierUpgradeInst> upgrade : this.p_upgrades.entrySet()) {
                SoldierUpgradeInst upg = upgrade.getValue();
                MutableFloat newDamage = new MutableFloat(damage);
                if (upg.getUpgrade().onSoldierHurt(this, upg, source, newDamage)) {
                    return false;
                }
                damage = newDamage.floatValue();
            }
        }
        return super.attackEntityFrom(source, damage);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (!this.worldObj.isRemote) {
            if (damageSource.isFireDamage() && this.dollItem != null) {
                ItemStack brickItem = new ItemStack(RegistryItems.dollBrick, this.dollItem.stackSize);
                brickItem.setTagCompound(this.dollItem.getTagCompound());
                this.dollItem = brickItem;
            }
            ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
            if (!this.nexusSpawn) {
                if (this.dollItem != null) {
                    drops.add(this.dollItem.copy());
                }
                for (SoldierUpgradeInst upg : this.p_upgrades.values()) {
                    upg.getUpgrade().onItemDrop(this, upg, drops);
                }
                drops.removeAll(Collections.singleton(null));
                for (ItemStack drop : drops) {
                    this.entityDropItem(drop, 0.0f);
                }
            }
            for (SoldierUpgradeInst upg : this.p_upgrades.values()) {
                upg.getUpgrade().onSoldierDeath(this, upg, damageSource);
            }
            for (SoldierEffectInst eff : this.p_effects.values()) {
                eff.getEffect().onSoldierDeath(this, eff, damageSource);
            }
        }
    }

    @Override
    public void knockBack(Entity par1Entity, float par2, double par3, double par5) {
        if (!this.canMove) {
            return;
        }
        super.knockBack(par1Entity, par2, par3, par5);
        this.motionX *= this.knockBack.getValue0().doubleValue();
        this.motionY *= this.knockBack.getValue1().doubleValue();
        this.motionZ *= this.knockBack.getValue2().doubleValue();
    }

    @Override
    public float getAIMoveSpeed() {
        MutableFloat speed = new MutableFloat(0.5f);
        for (SoldierUpgradeInst upg : this.p_upgrades.values()) {
            upg.getUpgrade().getAiMoveSpeed(this, upg, speed);
        }
        for (SoldierEffectInst eff : this.p_effects.values()) {
            eff.getEffect().getAiMoveSpeed(this, eff, speed);
        }
        return speed.floatValue();
    }

    @Override
    public boolean canEntityBeSeen(Entity target) {
        return this.worldObj.func_147447_a(Vec3.createVectorHelper(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ), Vec3.createVectorHelper(target.posX, target.posY + (double)target.getEyeHeight(), target.posZ), false, true, false) == null;
    }

    @Override
    public boolean canBePushed() {
        return this.canMove;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(ModConfig.soldierBaseHealth);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(20, ClaymanTeam.NULL_TEAM.getTeamName());
        this.dataWatcher.addObject(21, (byte)15);
        this.dataWatcher.addObject(22, (byte)0);
        this.dataWatcher.addObject(23, 0);
    }

    @Override
    protected String getLivingSound() {
        return null;
    }

    @Override
    protected String getHurtSound() {
        return ModConfig.useOldHurtSound ? "claysoldiers:mob.soldier.hurt" : "dig.gravel";
    }

    @Override
    protected String getDeathSound() {
        return "step.gravel";
    }

    @Override
    protected void onDeathUpdate() {
        this.deathTime = 20;
        this.setDead();
        ParticlePacketSender.sendSoldierDeathFx(this.posX, this.posY, this.posZ, this.dimension, this.getClayTeam());
    }

    @Override
    protected boolean isAIEnabled() {
        return false;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    protected boolean interact(EntityPlayer player) {
        if (this.worldObj.isRemote) {
            ClaySoldiersMod.proxy.switchClayCam(true, this);
        }
        return super.interact(player);
    }

    @Override
    protected void updateEntityActionState() {
        block33: {
            block35: {
                block36: {
                    if (!this.hasPath()) {
                        if (this.entityToAttack != null) {
                            this.setPathToEntity(BugfixHelper.getPathEntityToEntity(this.worldObj, this, this.entityToAttack, 16.0f, true, false, false, true));
                        } else if (this.p_targetFollow != null) {
                            this.setPathToEntity(BugfixHelper.getPathEntityToEntity(this.worldObj, this, this.p_targetFollow, 16.0f, true, false, false, true));
                        } else if ((this.rand.nextInt(180) == 0 || this.rand.nextInt(120) == 0 || this.fleeingTick > 0) && this.entityAge < 100) {
                            this.updateWanderPath();
                        }
                    }
                    super.updateEntityActionState();
                    if (this.worldObj.isRemote) break block33;
                    this.p_entitiesInRange = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getTargetArea());
                    if (this.entityToAttack != null) break block35;
                    if (this.rand.nextInt(4) == 0 || this.p_targetFollow != null) break block36;
                    Collection<EntityClayMan> claymen = this.getSoldiersInRange();
                    for (EntityClayMan entityClayMan : claymen) {
                        if (entityClayMan.isDead || this.rand.nextInt(3) != 0 || !this.checkIfValidTarget(entityClayMan)) continue;
                        this.entityToAttack = entityClayMan;
                        break block33;
                    }
                    break block33;
                }
                if (this.p_targetFollow == null) {
                    Collection<EntityItem> items = this.getItemsInRange();
                    block1: for (EntityItem entityItem : items) {
                        ASoldierUpgrade upgrade;
                        if (!this.canEntityBeSeen(entityItem) || (upgrade = SoldierUpgrades.getUpgrade(entityItem.getEntityItem())) == null || this.hasUpgrade(upgrade) || !upgrade.canBePickedUp(this, entityItem.getEntityItem(), null)) continue;
                        for (SoldierUpgradeInst upgradeInst : this.p_upgrades.values()) {
                            if (upgrade != upgradeInst.getUpgrade() && upgrade.canBePickedUp(this, entityItem.getEntityItem(), upgradeInst.getUpgrade())) continue;
                            continue block1;
                        }
                        this.p_targetFollow = entityItem;
                        break;
                    }
                } else {
                    if (this.p_targetFollow.isDead) {
                        this.p_targetFollow = null;
                    } else if (!this.canEntityBeSeen(this.p_targetFollow)) {
                        this.p_targetFollow = null;
                    }
                    if (this.p_targetFollow instanceof EntityItem && this.p_targetFollow.getDistanceToEntity(this) < 0.5f) {
                        EntityItem itemEntity = (EntityItem)this.p_targetFollow;
                        ASoldierUpgrade upgrade = SoldierUpgrades.getUpgrade(itemEntity.getEntityItem());
                        if (upgrade != null) {
                            this.addUpgrade(upgrade, itemEntity.getEntityItem());
                            if (itemEntity.getEntityItem().stackSize <= 0) {
                                itemEntity.setDead();
                            }
                            this.p_targetFollow = null;
                        }
                    } else if (this.p_targetFollow instanceof IMount) {
                        if (this.p_targetFollow.riddenByEntity != null) {
                            this.p_targetFollow = null;
                        } else if ((double)this.p_targetFollow.getDistanceToEntity(this) < 0.5) {
                            this.mountEntity(this.p_targetFollow);
                            this.p_targetFollow = null;
                        }
                    }
                }
                if (this.p_targetFollow != null || this.ridingEntity != null) break block33;
                Collection<IMount> mounts = this.getMountsInRange();
                for (IMount iMount : mounts) {
                    EntityLivingBase slyfox = (EntityLivingBase)((Object)iMount);
                    if (this.rand.nextInt(4) != 0 || !this.canEntityBeSeen(slyfox) || slyfox.riddenByEntity != null) continue;
                    this.p_targetFollow = slyfox;
                    break block33;
                }
                break block33;
            }
            if (this.entityToAttack.isDead
                    || !this.canEntityBeSeen(this.entityToAttack)
                    || !(this.entityToAttack instanceof EntityClayMan || this.entityToAttack instanceof EntityPlayer)
                    || this.entityToAttack instanceof EntityClayMan && !this.checkIfValidTarget((EntityClayMan)this.entityToAttack)) {
                this.entityToAttack = null;
            } else if (this.attackTime == 0) {
                this.attackTime = 5;
                MutableFloat atkRng = new MutableFloat(this.riddenByEntity != null ? 0.6f : 0.7f);
                for (SoldierUpgradeInst soldierUpgradeInst : this.p_upgrades.values()) {
                    soldierUpgradeInst.getUpgrade().getAttackRange(this, soldierUpgradeInst, this.entityToAttack, atkRng);
                }
                if (this.getDistanceToEntity(this.entityToAttack) < atkRng.floatValue() && this.entityToAttack instanceof EntityLivingBase && !this.entityToAttack.isEntityInvulnerable()) {
                    EntityLivingBase target = (EntityLivingBase)this.entityToAttack;
                    if (target.hurtTime == 0) {
                        MutableFloat mutableFloat = new MutableFloat(ModConfig.soldierBaseDamage);
                        if (target instanceof EntityClayMan) {
                            EntityClayMan soldierTarget = (EntityClayMan)target;
                            soldierTarget.knockBack = Triplet.with(0.8, 0.8, 0.8);
                            for (SoldierUpgradeInst upg : this.p_upgrades.values()) {
                                upg.getUpgrade().onSoldierAttack(this, upg, soldierTarget, mutableFloat);
                            }
                        }
                        if (target.attackEntityFrom(DamageSource.causeMobDamage(this), mutableFloat.getValue().floatValue()) && target instanceof EntityClayMan) {
                            for (SoldierUpgradeInst upg : this.p_upgrades.values()) {
                                upg.getUpgrade().onSoldierDamage(this, upg, (EntityClayMan)target);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void updateWanderPath() {
        this.worldObj.theProfiler.startSection("stroll");
        boolean blockFound = false;
        int x = -1;
        int y = -1;
        int z = -1;
        float maxPathWeight = -99999.0f;
        for (int i = 0; i < 10; ++i) {
            int currZ;
            int currY;
            int currX = MathHelper.floor_double(this.posX + (double)this.rand.nextInt(13) - 6.0);
            float pathWeight = this.getBlockPathWeight(currX, currY = MathHelper.floor_double(this.posY + (double)this.rand.nextInt(7) - 3.0), currZ = MathHelper.floor_double(this.posZ + (double)this.rand.nextInt(13) - 6.0));
            if (!(pathWeight > maxPathWeight)) continue;
            maxPathWeight = pathWeight;
            x = currX;
            y = currY;
            z = currZ;
            blockFound = true;
        }
        if (blockFound) {
            this.setPathToEntity(BugfixHelper.getEntityPathToXYZ(this.worldObj, this, x, y, z, 10.0f, true, false, false, true));
        }
        this.worldObj.theProfiler.endSection();
    }

    public void setupTexture(boolean isRare, boolean isUnique) {
        ClaymanTeam team = ClaymanTeam.getTeam(this.getClayTeam());
        if (isUnique && team.getUniqueTextures().length > 0) {
            this.dataWatcher.updateObject(22, (byte)2);
            this.dataWatcher.updateObject(23, this.rand.nextInt(team.getUniqueTextures().length));
        } else if (isRare && team.getRareTextures().length > 0) {
            this.dataWatcher.updateObject(22, (byte)1);
            this.dataWatcher.updateObject(23, this.rand.nextInt(team.getRareTextures().length));
        } else {
            this.dataWatcher.updateObject(22, (byte)0);
            this.dataWatcher.updateObject(23, this.rand.nextInt(team.getDefaultTextures().length));
        }
    }

    public boolean isJumping() {
        return this.isJumping;
    }

    public double getLookRangeRad() {
        MutableDouble radiusMT = new MutableDouble(8.0);
        for (SoldierUpgradeInst upg : this.p_upgrades.values()) {
            upg.getUpgrade().getLookRange(this, upg, radiusMT);
        }
        return radiusMT.getValue();
    }

    public Collection<EntityClayMan> getSoldiersInRange() {
        return Collections2.transform((Collection)Collections2.filter((Collection)this.p_entitiesInRange, (Predicate)Predicates.instanceOf(EntityClayMan.class)), (Function)new Function<Object, EntityClayMan>(){

            public EntityClayMan apply(Object input) {
                return (EntityClayMan)input;
            }
        });
    }

    public Collection<EntityItem> getItemsInRange() {
        return Collections2.transform((Collection)Collections2.filter((Collection)this.p_entitiesInRange, (Predicate)Predicates.instanceOf(EntityItem.class)), (Function)new Function<Object, EntityItem>(){

            public EntityItem apply(Object input) {
                return (EntityItem)input;
            }
        });
    }

    public Collection<IMount> getMountsInRange() {
        return Collections2.transform((Collection)Collections2.filter((Collection)this.p_entitiesInRange, (Predicate)Predicates.instanceOf(IMount.class)), (Function)new Function<Object, IMount>(){

            public IMount apply(Object input) {
                return (IMount)input;
            }
        });
    }

    public String getClayTeam() {
        return this.dataWatcher.getWatchableObjectString(20);
    }

    public ResourceLocation getTexture() {
        if (this.dataWatcher.getWatchableObjectByte(22) == 2) {
            return ClaymanTeam.getTeam(this.dataWatcher.getWatchableObjectString(20)).getUniqueTextures()[this.dataWatcher.getWatchableObjectInt(23)];
        }
        if (this.dataWatcher.getWatchableObjectByte(22) == 1) {
            return ClaymanTeam.getTeam(this.dataWatcher.getWatchableObjectString(20)).getRareTextures()[this.dataWatcher.getWatchableObjectInt(23)];
        }
        return ClaymanTeam.getTeam(this.dataWatcher.getWatchableObjectString(20)).getDefaultTextures()[this.dataWatcher.getWatchableObjectInt(23)];
    }

    public void updateUpgradeEffectRenders() {
        if (this.worldObj.isRemote) {
            long dwValue;
            int renderStorageDw;
            long renderFlag;
            for (Byte by : SoldierUpgrades.getRegisteredRenderIds()) {
                renderFlag = 1L << by % 64;
                renderStorageDw = by / 64;
                dwValue = this.p_upgradeRenderFlags[renderStorageDw];
                ASoldierUpgrade upgrade = SoldierUpgrades.getUpgrade(by.byteValue());
                if ((dwValue & renderFlag) == renderFlag) {
                    if (this.p_upgrades.containsKey(upgrade)) continue;
                    this.p_upgrades.put(upgrade, new SoldierUpgradeInst(upgrade));
                    continue;
                }
                this.p_upgrades.remove(upgrade);
            }
            for (byte by : SoldierEffects.getRegisteredRenderIds()) {
                renderFlag = 1L << by % 64;
                renderStorageDw = by / 64;
                dwValue = this.p_effectRenderFlags[renderStorageDw];
                ASoldierEffect effect = SoldierEffects.getEffect(by);
                if ((dwValue & renderFlag) == renderFlag) {
                    if (this.p_effects.containsKey(effect)) continue;
                    SoldierEffectInst effectInst = new SoldierEffectInst(effect);
                    this.p_effects.put(effect, effectInst);
                    effect.onConstruct(this, effectInst);
                    continue;
                }
                this.p_effects.remove(effect);
            }
        } else {
            this.p_upgradeRenderFlags[0] = 0L;
            this.p_upgradeRenderFlags[1] = 0L;
            this.p_effectRenderFlags[0] = 0L;
            this.p_effectRenderFlags[1] = 0L;
            for (SoldierUpgradeInst soldierUpgradeInst : this.p_upgrades.values()) {
                byte renderId = SoldierUpgrades.getRenderId(soldierUpgradeInst.getUpgrade());
                if (renderId < 0) continue;
                long l = 1L << renderId % 64;
                int renderStorageDw = renderId / 64;
                long prevDwValue = this.p_upgradeRenderFlags[renderStorageDw];
                this.p_upgradeRenderFlags[renderStorageDw] = prevDwValue | l;
            }
            ArrayList<Pair<Byte, NBTTagCompound>> effectNbtToClt = new ArrayList<Pair<Byte, NBTTagCompound>>();
            for (SoldierEffectInst effInst : this.p_effects.values()) {
                byte by = SoldierEffects.getRenderId(effInst.getEffect());
                if (by < 0) continue;
                long renderFlag = 1L << by % 64;
                int renderStorageDw = by / 64;
                long prevDwValue = this.p_effectRenderFlags[renderStorageDw];
                this.p_effectRenderFlags[renderStorageDw] = prevDwValue | renderFlag;
                if (!effInst.getEffect().shouldNbtSyncToClient(this, effInst)) continue;
                effectNbtToClt.add(Pair.with(by, effInst.getNbtTag()));
            }
            ArrayList<Pair<Byte, NBTTagCompound>> arrayList = new ArrayList<Pair<Byte, NBTTagCompound>>();
            for (SoldierUpgradeInst soldierUpgradeInst : this.p_upgrades.values()) {
                byte renderId = SoldierUpgrades.getRenderId(soldierUpgradeInst.getUpgrade());
                if (renderId < 0) continue;
                long renderFlag = 1L << renderId % 64;
                int renderStorageDw = renderId / 64;
                long prevDwValue = this.p_upgradeRenderFlags[renderStorageDw];
                this.p_upgradeRenderFlags[renderStorageDw] = prevDwValue | renderFlag;
                if (!soldierUpgradeInst.getUpgrade().shouldNbtSyncToClient(this, soldierUpgradeInst)) continue;
                arrayList.add(Pair.with(renderId, soldierUpgradeInst.getNbtTag()));
            }
            PacketManager.sendToAllAround((short)0, this.dimension, this.posX, this.posY, this.posZ, 64.0, Triplet.with(this.getEntityId(), this.p_upgradeRenderFlags, this.p_effectRenderFlags));
            for (Pair pair : effectNbtToClt) {
                PacketManager.sendToAllAround((short)2, this.dimension, this.posX, this.posY, this.posZ, 64.0, Triplet.with(this.getEntityId(), pair.getValue0(), pair.getValue1()));
            }
            for (Pair pair : arrayList) {
                PacketManager.sendToAllAround((short)3, this.dimension, this.posX, this.posY, this.posZ, 64.0, Triplet.with(this.getEntityId(), pair.getValue0(), pair.getValue1()));
            }
        }
    }

    public void onProjectileHit(ISoldierProjectile<? extends EntityThrowable> projectile, MovingObjectPosition movObjPos) {
        for (SoldierUpgradeInst upg : this.p_upgrades.values()) {
            upg.getUpgrade().onProjectileHit(this, upg, movObjPos, projectile);
        }
    }

    public boolean canTargetSoldier(EntityClayMan target) {
        return this.canTargetSoldier(target, true);
    }

    public boolean canTargetSoldier(EntityClayMan target, boolean withUpgradeCheck) {
        if (this.entityToAttack == null || this.entityToAttack.isDead) {
            if (withUpgradeCheck && !this.checkIfValidTarget(target)) {
                return false;
            }
            this.entityToAttack = target;
            return true;
        }
        return false;
    }

    public boolean hasUpgrade(ASoldierUpgrade upgrade) {
        return this.p_upgrades.containsKey(upgrade);
    }

    public boolean hasUpgrade(String upgradeName) {
        return this.hasUpgrade(SoldierUpgrades.getUpgrade(upgradeName));
    }

    public boolean hasUpgrade(Class upgradeClass) {
        for (ASoldierUpgrade upgrade : this.p_upgrades.keySet()) {
            if (!upgradeClass.isInstance(upgrade)) continue;
            return true;
        }
        return false;
    }

    public ASoldierUpgrade[] getAvailableUpgrades() {
        return this.p_upgrades.keySet().toArray(new ASoldierUpgrade[this.p_upgrades.size()]);
    }

    public SoldierUpgradeInst getUpgrade(ASoldierUpgrade upgrade) {
        if (this.hasUpgrade(upgrade)) {
            return this.p_upgrades.get(upgrade);
        }
        return null;
    }

    public void removeEffect(ASoldierEffect effect) {
        if (this.hasEffect(effect)) {
            this.p_effects.remove(effect);
        }
    }

    public Entity getTargetFollowing() {
        return this.p_targetFollow;
    }

    public void setTargetFollowing(Entity entity) {
        this.p_targetFollow = entity;
    }

    public SoldierUpgradeInst addUpgrade(ASoldierUpgrade upgrade) {
        return this.addUpgrade(upgrade, null);
    }

    public SoldierUpgradeInst addUpgrade(ASoldierUpgrade upgrade, ItemStack stack) {
        if (!this.hasUpgrade(upgrade)) {
            SoldierUpgradeInst upgradeInst = new SoldierUpgradeInst(upgrade);
            upgrade.onConstruct(this, upgradeInst);
            if (stack != null) {
                upgrade.onPickup(this, upgradeInst, stack);
            }
            for (SoldierUpgradeInst inst : this.p_upgrades.values()) {
                inst.getUpgrade().onUpgradeAdded(this, inst, upgradeInst);
            }
            this.p_upgrades.put(upgrade, upgradeInst);
            return upgradeInst;
        }
        return null;
    }

    public int getMiscColor() {
        return ItemDye.field_150922_c[this.getMiscColorIndex()];
    }

    public int getMiscColorIndex() {
        return this.dataWatcher.getWatchableObjectByte(21);
    }

    public void setMiscColorIndex(int colorIndex) {
        if (colorIndex >= 0 && colorIndex < ItemDye.field_150922_c.length) {
            this.dataWatcher.updateObject(21, (byte)colorIndex);
        }
    }

    public SoldierEffectInst addEffect(ASoldierEffect effect) {
        if (!this.hasEffect(effect)) {
            SoldierEffectInst effectInst = new SoldierEffectInst(effect);
            effect.onConstruct(this, effectInst);
            for (SoldierEffectInst existEffect : this.p_effects.values()) {
                if (effect.isCompatibleWith(this, effectInst, existEffect)) continue;
                return null;
            }
            this.p_effects.put(effect, effectInst);
            return effectInst;
        }
        return null;
    }

    public SoldierEffectInst getEffect(ASoldierEffect effect) {
        if (this.hasEffect(effect)) {
            return this.p_effects.get(effect);
        }
        return null;
    }

    public SoldierEffectInst getEffect(String effectName) {
        return this.getEffect(SoldierEffects.getEffect(effectName));
    }

    public void applyRenderFlags(long ... flags) {
        this.p_upgradeRenderFlags[0] = flags[0];
        this.p_upgradeRenderFlags[1] = flags[1];
        this.p_effectRenderFlags[0] = flags[2];
        this.p_effectRenderFlags[1] = flags[3];
    }

    public boolean hasEffect(ASoldierEffect effect) {
        return this.p_effects.containsKey(effect);
    }

    public boolean hasEffect(String effectName) {
        return this.hasEffect(SoldierEffects.getEffect(effectName));
    }

    public void throwSomethingAtEnemy(EntityLivingBase entity, Class<? extends ISoldierProjectile<? extends EntityThrowable>> projClass, boolean homing) {
        double d = entity.posX - this.posX;
        double d1 = entity.posZ - this.posZ;
        try {
            ISoldierProjectile<? extends EntityThrowable> projectile = projClass.getConstructor(World.class, EntityLivingBase.class).newInstance(this.worldObj, this);
            projectile.initProjectile(entity, homing, this.getClayTeam());
            EntityThrowable throwable = projectile.getProjectileEntity();
            throwable.posY += 0.1;
            double d2 = entity.posY + (double)entity.getEyeHeight() - 0.10000000298023223 - throwable.posY;
            float f1 = MathHelper.sqrt_double(d * d + d1 * d1) * 0.2f;
            this.worldObj.spawnEntityInWorld(throwable);
            throwable.setThrowableHeading(d, d2 + (double)f1, d1, 0.6f, 12.0f);
            this.attackTime = 30;
            this.hasAttacked = true;
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            FMLLog.log((String)"ClaySoldiers", (Level)Level.ERROR, (String)"%1$s cannot be instantiated! %1$s is not thrown to target!", (Object[])new Object[]{projClass.getName()});
            e.printStackTrace();
        }
    }

    private boolean checkIfValidTarget(EntityClayMan target) {
        EnumMethodState result;
        for (SoldierEffectInst eff : this.p_effects.values()) {
            result = eff.getEffect().onTargeting(this, eff, target);
            if (result == EnumMethodState.DENY) {
                return false;
            }
            if (result != EnumMethodState.ALLOW) continue;
            return true;
        }
        for (SoldierUpgradeInst upg : this.p_upgrades.values()) {
            result = upg.getUpgrade().onTargeting(this, upg, target);
            if (result == EnumMethodState.DENY) {
                return false;
            }
            if (result != EnumMethodState.ALLOW) continue;
            return true;
        }
        for (SoldierUpgradeInst upg : target.p_upgrades.values()) {
            result = upg.getUpgrade().onBeingTargeted(target, upg, this);
            if (result == EnumMethodState.DENY) {
                return false;
            }
            if (result != EnumMethodState.ALLOW) continue;
            return true;
        }
        return !target.getClayTeam().equals(this.getClayTeam()) && this.canEntityBeSeen(target);
    }

    private AxisAlignedBB getTargetArea() {
        double radius = this.getLookRangeRad();
        return AxisAlignedBB.getBoundingBox(this.posX - radius, this.posY - radius, this.posZ - radius, this.posX + radius, this.posY + radius, this.posZ + radius);
    }
}
