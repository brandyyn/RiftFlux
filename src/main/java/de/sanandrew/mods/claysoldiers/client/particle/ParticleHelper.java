/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.core.manpack.util.javatuples.Quartet;
import de.sanandrew.core.manpack.util.javatuples.Sextet;
import de.sanandrew.core.manpack.util.javatuples.Triplet;
import de.sanandrew.core.manpack.util.javatuples.Tuple;
import de.sanandrew.mods.claysoldiers.client.particle.ParticleBunnyDeath;
import de.sanandrew.mods.claysoldiers.client.particle.ParticleHorseDeath;
import de.sanandrew.mods.claysoldiers.client.particle.ParticleNexusFX;
import de.sanandrew.mods.claysoldiers.client.particle.ParticleSoldierDeath;
import de.sanandrew.mods.claysoldiers.client.particle.ParticleTurtleDeath;
import de.sanandrew.mods.claysoldiers.network.packet.EnumParticleFx;
import de.sanandrew.mods.claysoldiers.util.mount.EnumBunnyType;
import de.sanandrew.mods.claysoldiers.util.mount.EnumHorseType;
import de.sanandrew.mods.claysoldiers.util.mount.EnumTurtleType;
import de.sanandrew.mods.claysoldiers.util.soldier.ClaymanTeam;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.client.particle.EntityCritFX;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

@SideOnly(value=Side.CLIENT)
public final class ParticleHelper {
    public static void spawnParticles(EnumParticleFx particleId, Tuple particleData) {
        Minecraft mc = Minecraft.getMinecraft();
        switch (particleId) {
            case FX_BREAK: {
                ParticleHelper.spawnBreakFx((Quartet)particleData, mc);
                break;
            }
            case FX_CRIT: {
                ParticleHelper.spawnCritFx((Triplet)particleData, mc);
                break;
            }
            case FX_SOLDIER_DEATH: {
                ParticleHelper.spawnSoldierDeathFx((Quartet)particleData, mc);
                break;
            }
            case FX_HORSE_DEATH: {
                ParticleHelper.spawnHorseDeathFx((Quartet)particleData, mc);
                break;
            }
            case FX_BUNNY_DEATH: {
                ParticleHelper.spawnBunnyDeathFx((Quartet)particleData, mc);
                break;
            }
            case FX_TURTLE_DEATH: {
                ParticleHelper.spawnTurtleDeathFx((Quartet)particleData, mc);
                break;
            }
            case FX_DIGGING: {
                ParticleHelper.spawnDiggingFx((Quartet)particleData, mc);
                break;
            }
            case FX_SPELL: {
                ParticleHelper.spawnSpellFx((Sextet)particleData, mc);
                break;
            }
            case FX_NEXUS: {
                ParticleHelper.spawnNexusFx((Sextet)particleData, mc);
                break;
            }
            case FX_SHOCKWAVE: {
                ParticleHelper.spawnShockwaveFx((Triplet)particleData, mc);
                break;
            }
            case FX_MAGMAFUSE: {
                ParticleHelper.spawnMagmafuseFx((Triplet)particleData, mc);
            }
        }
    }

    public static void spawnBreakFx(Quartet<Double, Double, Double, String> data, Minecraft mc) {
        Item item = (Item)Item.itemRegistry.getObject(data.getValue3());
        for (int i = 0; i < 5; ++i) {
            EntityBreakingFX fx = new EntityBreakingFX(mc.theWorld, data.getValue0(), data.getValue1(), data.getValue2(), item);
            mc.effectRenderer.addEffect(fx);
        }
    }

    public static void spawnCritFx(Triplet<Double, Double, Double> data, Minecraft mc) {
        for (int i = 0; i < 10; ++i) {
            double motX = SAPUtils.RNG.nextDouble() - 0.5;
            double motY = SAPUtils.RNG.nextDouble() * 0.5;
            double motZ = SAPUtils.RNG.nextDouble() - 0.5;
            EntityCritFX fx = new EntityCritFX(mc.theWorld, data.getValue0(), data.getValue1(), data.getValue2(), motX, motY, motZ);
            mc.effectRenderer.addEffect(fx);
        }
    }

    public static void spawnSoldierDeathFx(Quartet<Double, Double, Double, String> data, Minecraft mc) {
        ClaymanTeam team = ClaymanTeam.getTeam(data.getValue3());
        for (int i = 0; i < 10; ++i) {
            ParticleSoldierDeath fx = new ParticleSoldierDeath((World)mc.theWorld, (double)data.getValue0(), (double)data.getValue1(), (double)data.getValue2(), team);
            mc.effectRenderer.addEffect(fx);
        }
    }

    public static void spawnHorseDeathFx(Quartet<Double, Double, Double, Byte> data, Minecraft mc) {
        EnumHorseType type = EnumHorseType.VALUES[data.getValue3()];
        for (int i = 0; i < 5; ++i) {
            ParticleHorseDeath fx = new ParticleHorseDeath((World)mc.theWorld, (double)data.getValue0(), (double)data.getValue1(), (double)data.getValue2(), type);
            mc.effectRenderer.addEffect(fx);
        }
    }

    public static void spawnBunnyDeathFx(Quartet<Double, Double, Double, Byte> data, Minecraft mc) {
        EnumBunnyType type = EnumBunnyType.VALUES[data.getValue3()];
        for (int i = 0; i < 5; ++i) {
            ParticleBunnyDeath fx = new ParticleBunnyDeath((World)mc.theWorld, (double)data.getValue0(), (double)data.getValue1(), (double)data.getValue2(), type);
            mc.effectRenderer.addEffect(fx);
        }
    }

    public static void spawnTurtleDeathFx(Quartet<Double, Double, Double, Byte> data, Minecraft mc) {
        EnumTurtleType type = EnumTurtleType.VALUES[data.getValue3()];
        for (int i = 0; i < 5; ++i) {
            ParticleTurtleDeath fx = new ParticleTurtleDeath((World)mc.theWorld, (double)data.getValue0(), (double)data.getValue1(), (double)data.getValue2(), type);
            mc.effectRenderer.addEffect(fx);
        }
    }

    public static void spawnDiggingFx(Quartet<Double, Double, Double, String> data, Minecraft mc) {
        Block block = (Block)Block.blockRegistry.getObject(data.getValue3());
        for (int i = 0; i < 8; ++i) {
            EntityDiggingFX fx = new EntityDiggingFX(mc.theWorld, data.getValue0(), data.getValue1(), data.getValue2(), SAPUtils.RNG.nextGaussian() * 0.15, SAPUtils.RNG.nextDouble() * 0.2, SAPUtils.RNG.nextGaussian() * 0.15, block, 0);
            mc.effectRenderer.addEffect(fx);
        }
    }

    public static void spawnSpellFx(Sextet<Double, Double, Double, Double, Double, Double> data, Minecraft mc) {
        for (int i = 0; i < 4; ++i) {
            EntitySpellParticleFX fx = new EntitySpellParticleFX(mc.theWorld, data.getValue0(), data.getValue1() - SAPUtils.RNG.nextDouble() * 0.2, data.getValue2(), data.getValue3(), data.getValue4(), data.getValue5());
            mc.effectRenderer.addEffect(fx);
        }
    }

    public static void spawnNexusFx(Sextet<Double, Double, Double, Float, Float, Float> data, Minecraft mc) {
        ParticleNexusFX fx = new ParticleNexusFX(mc.theWorld, data.getValue0() + (double)0.2f + SAPUtils.RNG.nextDouble() * (double)0.6f, data.getValue1(), data.getValue2() + (double)0.2f + SAPUtils.RNG.nextDouble() * (double)0.6f, 0.1f + SAPUtils.RNG.nextFloat() * 0.2f, data.getValue3().floatValue(), data.getValue4().floatValue(), data.getValue5().floatValue());
        fx.motionY = 0.02f;
        mc.effectRenderer.addEffect(fx);
    }

    public static void spawnShockwaveFx(Triplet particleData, Minecraft mc) {
        int blockZ;
        int blockY;
        double x = (Double)particleData.getValue0();
        double y = (Double)particleData.getValue1();
        double z = (Double)particleData.getValue2();
        int blockX = MathHelper.floor_double(x);
        Block block = mc.theWorld.getBlock(blockX, blockY = MathHelper.floor_double(y), blockZ = MathHelper.floor_double(z));
        if (block.getMaterial() != Material.air) {
            double radius = 0.5;
            int particleCount = (int)(150.0 * radius);
            for (int i2 = 0; i2 < particleCount; ++i2) {
                float rad = MathHelper.randomFloatClamp(SAPUtils.RNG, 0.0f, (float)Math.PI * 2);
                double multi = MathHelper.randomFloatClamp(SAPUtils.RNG, 0.75f, 1.0f);
                double partY = 0.2 + radius / 100.0;
                double partX = (double)(MathHelper.cos(rad) * 0.2f) * multi * multi * (radius + 0.2);
                double partZ = (double)(MathHelper.sin(rad) * 0.2f) * multi * multi * (radius + 0.2);
                mc.theWorld.spawnParticle("blockdust_" + Block.getIdFromBlock(block) + '_' + mc.theWorld.getBlockMetadata(blockX, blockY, blockZ), x + 0.5, y + 1.0, z + 0.5, partX, partY, partZ);
            }
        }
    }

    public static void spawnMagmafuseFx(Triplet<Double, Double, Double> data, Minecraft mc) {
        float green;
        float red = (float)SAPUtils.RNG.nextInt(2) - 0.001f;
        if (red + (green = (float)SAPUtils.RNG.nextInt(2)) <= 0.5f) {
            if (SAPUtils.RNG.nextBoolean()) {
                green = 1.0f;
            } else {
                red = 1.0f;
            }
        }
        EntityReddustFX fx = new EntityReddustFX((World)mc.theWorld, (double)data.getValue0(), (double)data.getValue1(), (double)data.getValue2(), red, green, 0.0f);
        mc.effectRenderer.addEffect(fx);
    }
}

