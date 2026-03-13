/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Vec3
 */
package net.nmccoy.legendgear;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.util.Vec3;

public class MiniParticle {
    public double x;
    public double y;
    public double z;
    public double vx;
    public double vy;
    public double vz;
    public double ax;
    public double ay;
    public double az;
    public int hibernateTime;
    public int maxLife = 20;
    public int lifeTicks;
    public double age;
    public double size = 1.0;
    public double drag = 1.0;
    public double uniqueness;

    public MiniParticle() {
    }

    public MiniParticle(double ix, double iy, double iz) {
        this.x = ix;
        this.y = iy;
        this.z = iz;
    }

    public MiniParticle(double ix, double iy, double iz, double ivx, double ivy, double ivz) {
        this(ix, iy, iz);
        this.vx = ivx;
        this.vy = ivy;
        this.vz = ivz;
    }

    public MiniParticle(double ix, double iy, double iz, double ivx, double ivy, double ivz, double iax, double iay, double iaz) {
        this(ix, iy, iz, ivx, ivy, ivz);
        this.ax = iax;
        this.ay = iay;
        this.az = iaz;
    }

    public static MiniParticle NewRadialMiniParticle(Random rand, double spread, double velScale, double accScale) {
        double gx = rand.nextGaussian();
        double gy = rand.nextGaussian();
        double gz = rand.nextGaussian();
        Vec3 outward = Vec3.createVectorHelper((double)gx, (double)gy, (double)gz).normalize();
        gx = outward.xCoord * rand.nextDouble() * spread;
        gy = outward.yCoord * rand.nextDouble() * spread;
        gz = outward.zCoord * rand.nextDouble() * spread;
        MiniParticle p = new MiniParticle();
        p.x = gx;
        p.y = gy;
        p.z = gz;
        p.vx = gx * velScale;
        p.vy = gy * velScale;
        p.vz = gz * velScale;
        p.ax = gx * accScale;
        p.ay = gy * accScale;
        p.az = gz * accScale;
        p.uniqueness = rand.nextDouble();
        return p;
    }

    public String toString() {
        return "MiniParticle: hibernate " + this.hibernateTime + ", lifeTicks " + this.lifeTicks + ", maxLife " + this.maxLife;
    }

    public boolean tick() {
        if (this.hibernateTime-- > 0) {
            return true;
        }
        this.vx += this.ax;
        this.vy += this.ay;
        this.vz += this.az;
        this.vx *= this.drag;
        this.vy *= this.drag;
        this.vz *= this.drag;
        this.x += this.vx;
        this.y += this.vy;
        this.z += this.vz;
        ++this.lifeTicks;
        this.age = 1.0 * (double)this.lifeTicks / (double)this.maxLife;
        return this.age < 1.0;
    }

    public static List<MiniParticle> chewParticles(List<MiniParticle> input) {
        ArrayList<MiniParticle> output = new ArrayList<MiniParticle>(input.size());
        for (MiniParticle p : input) {
            if (!p.tick()) continue;
            output.add(p);
        }
        return output;
    }
}

