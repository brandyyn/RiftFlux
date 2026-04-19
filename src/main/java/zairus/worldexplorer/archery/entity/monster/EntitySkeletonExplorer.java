package zairus.worldexplorer.archery.entity.monster;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import zairus.worldexplorer.archery.entity.EntityPebble;
import zairus.worldexplorer.archery.items.WEArcheryItems;

public class EntitySkeletonExplorer extends EntitySkeleton {
    public EntitySkeletonExplorer(World world) {
        super(world);
        this.tasks.addTask(7, (EntityAIBase) new EntityAIWatchClosest(this, EntityCreeper.class, 8.0f));
        this.targetTasks.addTask(1, (EntityAIBase) new EntityAINearestAttackableTarget(this, EntityCreeper.class, 0, true));
    }

    protected void addRandomArmor() {
        super.addRandomArmor();
        this.setCurrentItemOrArmor(0, new ItemStack(WEArcheryItems.slingshot));
    }

    public void setCombatTask() {
    }

    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        EntityPebble entityPebble = new EntityPebble(this.worldObj, this, target, 1.6f, 14 - this.worldObj.difficultySetting.getDifficultyId() * 4);
        int power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, this.getHeldItem());
        int punch = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, this.getHeldItem());
        entityPebble.setDamage((double) (distanceFactor * 2.0f) + this.rand.nextGaussian() * 0.25D + (double) ((float) this.worldObj.difficultySetting.getDifficultyId() * 0.11f));
        if (power > 0) {
            entityPebble.setDamage(entityPebble.getDamage() + (double) power * 0.5D + 0.5D);
        }
        if (punch > 0) {
            entityPebble.setKnockbackStrength(punch);
        }
        if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, this.getHeldItem()) > 0 || this.getSkeletonType() == 1) {
            entityPebble.setFire(100);
        }
        this.playSound("worldexplorer:slingshot_release_1", 1.0f, 1.0f / (this.getRNG().nextFloat() * 0.4f + 0.8f));
        this.worldObj.spawnEntityInWorld(entityPebble);
    }

    protected Item getDropItem() {
        if (ModConfig.riftExplorerSlingshotUsesCobblestoneAmmo) {
            return Item.getItemFromBlock(Blocks.cobblestone);
        }
        return WEArcheryItems.pebble;
    }

    protected void dropFewItems(boolean recentlyHit, int looting) {
        int j;
        int k;
        if (this.getSkeletonType() == 1) {
            j = this.rand.nextInt(3 + looting) - 1;
            for (k = 0; k < j; ++k) {
                this.dropItem(Items.coal, 1);
            }
        } else {
            j = this.rand.nextInt(3 + looting);
            Item ammoDrop = ModConfig.riftExplorerSlingshotUsesCobblestoneAmmo ? Item.getItemFromBlock(Blocks.cobblestone) : WEArcheryItems.pebble;
            for (k = 0; k < j; ++k) {
                this.dropItem(ammoDrop, 1);
            }
        }

        j = this.rand.nextInt(3 + looting);
        for (k = 0; k < j; ++k) {
            this.dropItem(Items.bone, 1);
        }
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
    }

    protected void entityInit() {
        super.entityInit();
    }

    protected String getLivingSound() {
        return "mob.skeleton.say";
    }

    protected String getHurtSound() {
        return "mob.skeleton.hurt";
    }

    protected String getDeathSound() {
        return "mob.skeleton.death";
    }

    protected void func_145780_a(int x, int y, int z, Block block) {
        this.playSound("mob.skeleton.step", 0.15f, 1.0f);
    }
}
