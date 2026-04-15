package com.voidsrift.riftflux.palaria.entity;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.palaria.PalariaMobDrops;
import com.voidsrift.riftflux.palaria.PalariaMobContent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.world.World;

public class EntityRaptorChicken extends EntityAbstractRaptorChicken {
    public int timeUntilNextEgg = rand.nextInt(12000) + 12000;

    public EntityRaptorChicken(World world) {
        super(world);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(ModConfig.palariaRaptorChickenMaxHealth);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(15.0D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!isChild() && !worldObj.isRemote && --timeUntilNextEgg <= 0) {
            playSound("mob.chicken.plop", 1.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
            dropItem(Items.egg, 1);
            timeUntilNextEgg = rand.nextInt(12000) + 12000;
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        return super.attackEntityAsMob(target);
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        int feathers = rand.nextInt(3) + rand.nextInt(1 + looting);
        for (int i = 0; i < feathers; ++i) dropItem(Items.feather, 1);
        PalariaMobDrops.dropChance(this, PalariaMobContent.raptorClaw, 0.05F, 1);
        dropItem(isBurning() ? Items.cooked_chicken : Items.chicken, 1);
    }
}
