/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.IEntityAdditionalSpawnData
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.entity.Entity
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.entity;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.nmccoy.legendgear.MiniParticle;
import net.nmccoy.legendgear.entity.EntitySpellEffect;

public class SpellDecorator
extends Entity
implements IEntityAdditionalSpawnData {
    public int spellType;
    public List<MiniParticle> particles;
    public double radius;
    public double power;
    public boolean isCrit;
    public int longLife = 0;
    public int maxAge = 200;

    public SpellDecorator(World world) {
        super(world);
        this.spellType = Integer.MIN_VALUE;
        this.ignoreFrustumCheck = true;
    }

    public SpellDecorator(EntitySpellEffect spell) {
        super(spell.worldObj);
        this.posX = spell.posX;
        this.posY = spell.posY;
        this.posZ = spell.posZ;
        this.radius = spell.radius;
        this.power = spell.power;
        this.isCrit = spell.isCrit;
        this.spellType = spell.spellType.ordinal();
        this.generateParticles();
        this.height = (float)this.radius * 2.0f;
        this.width = (float)this.radius * 2.0f;
        this.ignoreFrustumCheck = true;
    }

    protected void generateParticles() {
        MiniParticle p;
        int i;
        this.particles = new ArrayList<MiniParticle>();
        Random rand = this.worldObj.rand;
        if (this.spellType == EntitySpellEffect.SpellType.Twinkle.ordinal()) {
            for (i = 0; i < 50; ++i) {
                p = MiniParticle.NewRadialMiniParticle(rand, this.radius, 0.0, 0.0);
                p.maxLife = 5;
                p.hibernateTime = rand.nextInt(15);
                this.particles.add(p);
            }
        }
        if (this.spellType == EntitySpellEffect.SpellType.Fire1.ordinal()) {
            for (i = 0; i < 30; ++i) {
                p = MiniParticle.NewRadialMiniParticle(rand, this.radius * 0.75, 0.1, -0.002);
                p.maxLife = 15 + rand.nextInt(10);
                p.ay = 0.03;
                p.hibernateTime = rand.nextInt(5);
                p.drag = 0.8;
                p.uniqueness = rand.nextDouble();
                this.particles.add(p);
            }
        }
        if (this.spellType == EntitySpellEffect.SpellType.Lightning1.ordinal()) {
            for (i = 0; i < 30; ++i) {
                p = MiniParticle.NewRadialMiniParticle(rand, this.radius, 0.0, 0.0);
                p.uniqueness = rand.nextDouble();
                p.maxLife = 15;
                p.hibernateTime = rand.nextInt(5);
                this.particles.add(p);
            }
        }
        if (this.spellType == EntitySpellEffect.SpellType.Ice1.ordinal()) {
            for (i = 0; i < 20; ++i) {
                p = MiniParticle.NewRadialMiniParticle(rand, this.radius, 0.0, 0.0);
                p.maxLife = 15;
                p.hibernateTime = rand.nextInt(5);
                this.particles.add(p);
            }
        }
        if (this.spellType == EntitySpellEffect.SpellType.OrbExplosion.ordinal()) {
            if (this.worldObj.isRemote) {
                this.worldObj.spawnParticle("largeexplode", this.posX, this.posY, this.posZ, 1.0, 0.0, 0.0);
            }
            if (!this.worldObj.isRemote) {
                this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "random.explode", 4.0f, (1.0f + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2f) * 0.7f);
            }
        }
        if (this.spellType == EntitySpellEffect.SpellType.SprinkleStardust.ordinal()) {
            for (i = 0; i < 30; ++i) {
                p = MiniParticle.NewRadialMiniParticle(rand, this.radius, 0.0, 0.0);
                p.maxLife = 5;
                p.hibernateTime = rand.nextInt(8);
                this.particles.add(p);
            }
        }
        if (this.spellType == EntitySpellEffect.SpellType.ScytheWind.ordinal()) {
            for (i = 0; i < 50; ++i) {
                p = MiniParticle.NewRadialMiniParticle(rand, this.radius, 0.0, 0.0);
                p.maxLife = 20;
                this.particles.add(p);
            }
        }
        if (this.spellType == EntitySpellEffect.SpellType.Rayfire.ordinal()) {
            for (i = 0; i < 25; ++i) {
                double theta = (double)i * Math.PI * 2.0 / 25.0;
                double r = (double)i / 23.0 * this.radius;
                for (int j = 0; j < 3; ++j) {
                    MiniParticle p2 = MiniParticle.NewRadialMiniParticle(rand, this.radius, 0.0, 0.0);
                    p2.x = Math.cos(theta) * r;
                    p2.z = Math.sin(theta) * r;
                    p2.maxLife = 5;
                    p2.y = 0.0;
                    p2.hibernateTime = i / 2;
                    this.particles.add(p2);
                    theta += 2.0943951023931953;
                }
            }
        }
        if (this.spellType == EntitySpellEffect.SpellType.Exit.ordinal()) {
            for (i = 0; i < 20; ++i) {
                MiniParticle p3 = MiniParticle.NewRadialMiniParticle(rand, this.radius, 0.0, 0.0);
                p3.maxLife = 10;
                p3.hibernateTime = rand.nextInt(8);
                this.particles.add(p3);
            }
        }
    }

    public void onUpdate() {
        if (this.particles == null) {
            this.generateParticles();
        }
        if (this.worldObj.isRemote) {
            this.particles = MiniParticle.chewParticles(this.particles);
        }
        ++this.longLife;
        if (this.worldObj.isRemote && this.particles.size() == 0) {
            this.setDead();
        }
        if (this.longLife > this.maxAge) {
            this.setDead();
        }
    }

    protected void entityInit() {
    }

    protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
    }

    protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
    }

    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(this.spellType);
        buffer.writeDouble(this.radius);
        buffer.writeDouble(this.power);
        buffer.writeBoolean(this.isCrit);
    }

    public void readSpawnData(ByteBuf additionalData) {
        this.spellType = additionalData.readInt();
        this.radius = additionalData.readDouble();
        this.power = additionalData.readDouble();
        this.isCrit = additionalData.readBoolean();
        this.height = (float)this.radius * 2.0f;
        this.width = (float)this.radius * 2.0f;
    }
}

