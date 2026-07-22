package com.voidsrift.riftflux.hotsprings;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.ConfiguredPotionEffectHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import java.util.Random;
import java.util.List;

public final class BlockSpringWater extends BlockFluidClassic {
    private IIcon stillIcon;
    private IIcon flowingIcon;
    private String[] cachedEffectEntries;
    private List<PotionEffect> cachedEffects;

    public BlockSpringWater(Fluid fluid) {
        super(fluid, Material.water);
        setBlockName("riftflux.spring_water");
        setLightOpacity(3);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (!world.isRemote && entity instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) entity;
            List<PotionEffect> effects = getConfiguredEffects();
            for (PotionEffect effect : effects) {
                living.addPotionEffect(new PotionEffect(
                        effect.getPotionID(),
                        effect.getDuration(),
                        effect.getAmplifier(),
                        true
                ));
            }
        }
    }

    private List<PotionEffect> getConfiguredEffects() {
        String[] entries = ModConfig.hotSpringsPotionEffects;
        if (entries != cachedEffectEntries || cachedEffects == null) {
            cachedEffectEntries = entries;
            cachedEffects = ConfiguredPotionEffectHelper.parseEffects(entries);
        }
        return cachedEffects;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
        super.randomDisplayTick(world, x, y, z, random);
        int particleCount = ModConfig.hotSpringsSteamParticlesPerDisplayTick;
        float chancePercent = ModConfig.hotSpringsSteamParticleChancePercent;
        double renderDistance = ModConfig.hotSpringsSteamParticleRenderDistance;
        if (particleCount <= 0
                || chancePercent <= 0.0F
                || renderDistance <= 0.0D
                || !world.isAirBlock(x, y + 1, z)
                || world.getClosestPlayer(x + 0.5D, y + 1.0D, z + 0.5D, renderDistance) == null
                || random.nextFloat() * 100.0F >= chancePercent) {
            return;
        }

        for (int i = 0; i < particleCount; i++) {
            world.spawnParticle(
                    "cloud",
                    x + random.nextFloat(),
                    y + 1.0D,
                    z + random.nextFloat(),
                    0.0D,
                    0.015D,
                    0.0D
            );
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        stillIcon = register.registerIcon("riftflux:spring_water_still");
        flowingIcon = register.registerIcon("riftflux:spring_water_flowing");
        getFluid().setIcons(stillIcon, flowingIcon);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata) {
        return side == 0 || side == 1 ? stillIcon : flowingIcon;
    }
}
