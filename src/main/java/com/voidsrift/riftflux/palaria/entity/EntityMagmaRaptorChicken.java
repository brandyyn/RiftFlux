package com.voidsrift.riftflux.palaria.entity;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.palaria.PalariaMobDrops;
import com.voidsrift.riftflux.palaria.PalariaMobContent;
import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityMagmaRaptorChicken extends EntityAbstractRaptorChicken {
    public EntityMagmaRaptorChicken(World world) {
        super(world);
        isImmuneToFire = true;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(16, Byte.valueOf((byte) 0));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(ModConfig.palariaMagmaRaptorChickenMaxHealth);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(50.0D);
    }

    @Override
    public void onLivingUpdate() {
        if (isWet()) {
            attackEntityFrom(DamageSource.drown, 1.0F);
        }
        if (rand.nextInt(24) == 0) {
            worldObj.playSoundEffect(posX + 0.5D, posY + 0.5D, posZ + 0.5D, "fire.fire", 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F);
        }
        for (int i = 0; i < 2; ++i) {
            worldObj.spawnParticle("largesmoke", posX + (rand.nextDouble() - 0.5D) * (double) width,
                    posY + rand.nextDouble() * (double) height,
                    posZ + (rand.nextDouble() - 0.5D) * (double) width, 0.0D, 0.0D, 0.0D);
        }
        if (!worldObj.isRemote && ModConfig.palariaMagmaRaptorChickenPlaceFire) {
            for (int i = 0; i < 4; ++i) {
                int x = MathHelper.floor_double(posX + (double) ((float) (i % 2 * 2 - 1) * 0.25F));
                int y = MathHelper.floor_double(posY);
                int z = MathHelper.floor_double(posZ + (double) ((float) (i / 2 % 2 * 2 - 1) * 0.25F));
                if (worldObj.isAirBlock(x, y, z) && Blocks.fire.canPlaceBlockAt(worldObj, x, y, z)) {
                    worldObj.setBlock(x, y, z, Blocks.fire);
                }
            }
        }
        super.onLivingUpdate();
    }

    @Override
    protected String getDeathSound() {
        return "mob.blaze.death";
    }

    @Override
    protected void func_145780_a(int x, int y, int z, Block block) {
        playSound("mob.chicken.step", 0.15F, 1.0F);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        int powder = rand.nextInt(3) + rand.nextInt(1 + looting);
        int rods = rand.nextInt(2);
        for (int i = 0; i < powder; ++i) dropItem(Items.blaze_powder, 1);
        for (int i = 0; i < rods; ++i) dropItem(Items.blaze_rod, 1);
        for (int i = 0; i < powder; ++i) dropItem(Items.cooked_chicken, 1);
        PalariaMobDrops.dropChance(this, PalariaMobContent.raptorClaw, 0.05F, 1);
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        return 0xF000F0;
    }

    @Override
    public float getBrightness(float partialTicks) {
        return 1.0F;
    }

    @Override
    protected boolean isValidLightLevel() {
        return true;
    }

    @Override
    public boolean getCanSpawnHere() {
        return worldObj != null && worldObj.provider != null && worldObj.provider.dimensionId == -1 && super.getCanSpawnHere();
    }
}
