/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.IEntityAdditionalSpawnData
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityTracker
 *  net.minecraft.entity.item.EntityXPOrb
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.server.S0DPacketCollectItem
 *  net.minecraft.stats.StatBase
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.Vec3
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 */
package net.nmccoy.legendgear.entity;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.stats.StatBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.PlayerStarstatsExtension;
import net.nmccoy.legendgear.entity.EntitySpellEffect;

public class EntityFallingStar
extends Entity
implements IEntityAdditionalSpawnData {
    public boolean impact = false;
    public int dwindle_timer;
    public boolean special;
    public static int DWINDLE_TIME = 440;
    public List<ItemStack> contents;
    private final Set<Integer> hitEntityIds;

    public EntityFallingStar(World par1World) {
        super(par1World);
        this.noClip = false;
        this.setSize(0.5f, 1.0f);
        this.yOffset = 0.5f;
        this.dwindle_timer = DWINDLE_TIME = LegendGear2.starFadeTime;
        this.renderDistanceWeight = 10.0;
        this.contents = new ArrayList<ItemStack>();
        this.hitEntityIds = new HashSet<Integer>();
    }

    public void addItem(ItemStack stack) {
        if (stack != null) {
            this.contents.add(stack);
        }
    }

    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {
        if (!this.impact) {
            return;
        }
        ItemStack prize = this.special ? new ItemStack((Item)LegendGear2.starDust, 1, 0) : new ItemStack((Item)LegendGear2.starDust, 1, 1);
        if (!this.worldObj.isRemote && par1EntityPlayer.inventory.addItemStackToInventory(prize)) {
            int var2;
            this.playSound("random.pop", 0.2f, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
            par1EntityPlayer.worldObj.playSoundAtEntity((Entity)par1EntityPlayer, "legendgear:starcaught", 2.0f, 1.0f);
            par1EntityPlayer.addStat((StatBase)LegendGear2.achievementStarGet, 1);
            for (int xp = 15; xp > 0; xp -= var2) {
                var2 = 1;
                this.worldObj.spawnEntityInWorld((Entity)new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, var2));
            }
            PlayerStarstatsExtension.get(par1EntityPlayer).setMana(0.0f);
            if (!this.isDead && !this.worldObj.isRemote) {
                EntityTracker entitytracker = ((WorldServer)this.worldObj).getEntityTracker();
                entitytracker.func_151248_b((Entity)this, (Packet)new S0DPacketCollectItem(this.getEntityId(), par1EntityPlayer.getEntityId()));
            }
            this.setDead();
        }
    }

    public EntityFallingStar(EntityPlayer player) {
        this(player.worldObj);
        double theta = this.rand.nextDouble() * Math.PI * 2.0;
        double r = 48.0;
        this.posX = player.posX + Math.cos(theta) * r;
        this.posY = player.posY + 150.0;
        this.posZ = player.posZ + Math.sin(theta) * r;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.motionY = 0.0;
        this.motionX = this.rand.nextGaussian() * 0.0;
        this.motionZ = this.rand.nextGaussian() * 0.0;
    }

    public EntityFallingStar(EntityPlayer player, List<ItemStack> items) {
        this(player);
        this.contents.addAll(items);
        this.special = true;
    }

    public void onUpdate() {
        AxisAlignedBB axisalignedbb;
        double prevX = this.posX;
        double prevY = this.posY;
        double prevZ = this.posZ;
        this.motionY -= 0.05;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        if (!this.worldObj.isRemote && !this.impact) {
            this.damageEntitiesAlongPath(prevX, prevY, prevZ);
        }
        if (this.ticksExisted == 2 && !this.worldObj.isRemote && !this.impact) {
            this.worldObj.playSoundAtEntity((Entity)this, "legendgear:starfall", 100.0f, 1.0f);
        }
        if (this.worldObj.isAABBInMaterial(axisalignedbb = AxisAlignedBB.getBoundingBox((double)this.boundingBox.minX, (double)(this.boundingBox.minY - 0.5), (double)this.boundingBox.minZ, (double)this.boundingBox.maxX, (double)this.boundingBox.minY, (double)this.boundingBox.maxZ), Material.water)) {
            if (!this.impact && !this.worldObj.isRemote) {
                if (!this.special) {
                    this.worldObj.createExplosion((Entity)this, this.posX, this.posY, this.posZ, 5.0f, false);
                }
                this.applyImpactAreaDamage();
                this.setSize(0.5f, 0.5f);
                this.worldObj.playSoundAtEntity((Entity)this, "game.neutral.swim.splash", 10.0f, 1.0f);
            }
            this.impact = true;
            this.motionY += 0.2;
            this.motionY *= 0.9;
        }
        if (this.isCollided && !this.impact) {
            if (!this.worldObj.isRemote) {
                if (!this.special) {
                    this.worldObj.createExplosion((Entity)this, this.posX, this.posY, this.posZ, 5.0f, false);
                } else {
                    this.worldObj.playSoundAtEntity((Entity)this, "legendgear:starAppear", 1.0f, 1.0f);
                    this.worldObj.createExplosion((Entity)this, this.posX, this.posY, this.posZ, 0.5f, false);
                }
                this.applyImpactAreaDamage();
                this.setSize(0.5f, 0.5f);
                if (!this.special) {
                    Vec3 pos = Vec3.createVectorHelper((double)this.posX, (double)this.posY, (double)this.posZ);
                    this.worldObj.spawnEntityInWorld((Entity)new EntitySpellEffect(this.worldObj, EntitySpellEffect.SpellType.StarImpact, null, pos, 2.0, 0.0, false));
                }
            }
            this.impact = true;
        }
        if (!this.worldObj.isRemote && this.impact && !this.contents.isEmpty() && this.special) {
            int var2;
            for (ItemStack stack : this.contents) {
                this.entityDropItem(stack, 0.0f);
            }
            this.contents.clear();
            for (int xp = 7; xp > 0; xp -= var2) {
                var2 = 1;
                this.worldObj.spawnEntityInWorld((Entity)new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, var2));
            }
            this.setDead();
        }
        if (this.worldObj.isRemote) {
            LegendGear2.proxy.addSparkleParticle(this.worldObj, this.posX - this.motionX, this.posY - this.motionY, this.posZ - this.motionZ, this.rand.nextGaussian() * 0.2, this.rand.nextGaussian() * 0.2, this.rand.nextGaussian() * 0.2, 4.0f * (float)this.dwindle_timer / (float)DWINDLE_TIME);
        } else if (this.impact && this.dwindle_timer % 25 == 0) {
            this.worldObj.playSoundAtEntity((Entity)this, "legendgear:twinkle", 2.0f, 1.0f);
        }
        if (this.impact && --this.dwindle_timer <= 0) {
            if (!this.worldObj.isRemote) {
                int var2;
                if (!this.special) {
                    this.dropItem(LegendGear2.starDust, 1);
                }
                for (int xp = 5; xp > 0; xp -= var2) {
                    var2 = 1;
                    this.worldObj.spawnEntityInWorld((Entity)new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, var2));
                }
            }
            this.setDead();
        }
        super.onUpdate();
    }

    private static float getConfiguredStarDamage() {
        if (!LegendGear2.CONFIG_FALLING_STAR_DAMAGE_ENABLED) {
            return 0.0f;
        }
        return Math.max(0.0f, LegendGear2.CONFIG_FALLING_STAR_DAMAGE);
    }

    private DamageSource getStarDamageSource() {
        return new DamageSource("legendgear.fallingStar");
    }

    private void damageEntitiesAlongPath(double prevX, double prevY, double prevZ) {
        float damage = getConfiguredStarDamage();
        if (damage <= 0.0f) {
            return;
        }

        double minX = Math.min(prevX, this.posX) - 1.0;
        double minY = Math.min(prevY, this.posY) - 1.0;
        double minZ = Math.min(prevZ, this.posZ) - 1.0;
        double maxX = Math.max(prevX, this.posX) + 1.0;
        double maxY = Math.max(prevY, this.posY) + 1.0;
        double maxZ = Math.max(prevZ, this.posZ) + 1.0;
        AxisAlignedBB sweep = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);

        List entities = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)this, sweep);
        for (Object obj : entities) {
            if (!(obj instanceof EntityLivingBase)) {
                continue;
            }
            EntityLivingBase living = (EntityLivingBase)obj;
            if (living.isDead || !this.hitEntityIds.add(living.getEntityId())) {
                continue;
            }
            living.attackEntityFrom(this.getStarDamageSource(), damage);
        }
    }

    private void applyImpactAreaDamage() {
        float damage = getConfiguredStarDamage();
        if (damage <= 0.0f) {
            return;
        }

        AxisAlignedBB area = AxisAlignedBB.getBoundingBox(
                this.posX - 1.5,
                this.posY - 1.5,
                this.posZ - 1.5,
                this.posX + 1.5,
                this.posY + 1.5,
                this.posZ + 1.5
        );

        List entities = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)this, area);
        for (Object obj : entities) {
            if (!(obj instanceof EntityLivingBase)) {
                continue;
            }
            EntityLivingBase living = (EntityLivingBase)obj;
            if (living.isDead || !this.hitEntityIds.add(living.getEntityId())) {
                continue;
            }
            living.attackEntityFrom(this.getStarDamageSource(), damage);
        }
    }

    protected void entityInit() {
    }

    protected void readEntityFromNBT(NBTTagCompound var1) {
        this.impact = var1.getBoolean("impact");
        this.dwindle_timer = var1.getInteger("dwindle");
        this.special = var1.getBoolean("special");
        NBTTagList items = var1.getTagList("items", 10);
        for (int i = 0; i < items.tagCount(); ++i) {
            this.contents.add(ItemStack.loadItemStackFromNBT((NBTTagCompound)items.getCompoundTagAt(i)));
        }
    }

    protected void writeEntityToNBT(NBTTagCompound var1) {
        var1.setBoolean("impact", this.impact);
        var1.setInteger("dwindle", this.dwindle_timer);
        var1.setBoolean("special", this.special);
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < this.contents.size(); ++i) {
            ItemStack stack = this.contents.get(i);
            NBTTagCompound itemTag = new NBTTagCompound();
            stack.writeToNBT(itemTag);
            list.appendTag((NBTBase)itemTag);
        }
        var1.setTag("items", (NBTBase)list);
    }

    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeBoolean(this.impact);
        buffer.writeInt(this.dwindle_timer);
        buffer.writeBoolean(this.special);
    }

    public void readSpawnData(ByteBuf buffer) {
        this.impact = buffer.readBoolean();
        this.dwindle_timer = buffer.readInt();
        this.special = buffer.readBoolean();
    }
}
