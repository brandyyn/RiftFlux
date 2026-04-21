package zairus.worldexplorer.archery.entity.monster;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import zairus.worldexplorer.archery.entity.EntityPebble;
import zairus.worldexplorer.archery.items.SlingshotAmmoHelper;
import zairus.worldexplorer.archery.items.WEArcheryItems;

public class EntitySkeletonExplorer extends EntitySkeleton {
    private final EntityAIArrowAttack slingshotAttackAI = new EntityAIArrowAttack(this, 1.0D, 20, 60, 15.0F);
    private ItemStack slingshotAmmo;

    public EntitySkeletonExplorer(World world) {
        super(world);
        this.ensureSlingshotAttackTask();
        this.tasks.addTask(7, (EntityAIBase) new EntityAIWatchClosest(this, EntityCreeper.class, 8.0f));
        this.targetTasks.addTask(1, (EntityAIBase) new EntityAINearestAttackableTarget(this, EntityCreeper.class, 0, true));
    }

    protected void addRandomArmor() {
        super.addRandomArmor();
        this.setCurrentItemOrArmor(0, new ItemStack(WEArcheryItems.slingshot));
        this.setSlingshotAmmo(SlingshotAmmoHelper.randomMobAmmo(this.rand));
        this.ensureSlingshotAttackTask();
    }

    public void setCombatTask() {
        this.ensureSlingshotAttackTask();
    }

    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        EntityPebble entityPebble = new EntityPebble(this.worldObj, this, target, 1.6f, 14 - this.worldObj.difficultySetting.getDifficultyId() * 4);
        ItemStack ammoStack = this.getSlingshotAmmo();
        entityPebble.setPickupStack(ammoStack);
        entityPebble.canBePickedUp = 0;
        SlingshotAmmoHelper.SpecialAmmoBehavior behavior = SlingshotAmmoHelper.getSpecialBehavior(ammoStack);
        if (behavior != null) {
            entityPebble.setDamage(behavior.getDamage());
            entityPebble.setFixedDamage(true);
            entityPebble.setSpecialKnockbackStrength(behavior.getKnockbackStrength());
            entityPebble.setExplosionStrength(behavior.getExplosionStrength());
        }
        int power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, this.getHeldItem());
        int punch = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, this.getHeldItem());
        if (behavior == null) {
            entityPebble.setDamage((double) (distanceFactor * 2.0f) + this.rand.nextGaussian() * 0.25D + (double) ((float) this.worldObj.difficultySetting.getDifficultyId() * 0.11f));
        }
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
        ItemStack ammoDrop = this.getSlingshotAmmo();
        return ammoDrop == null || ammoDrop.getItem() == null ? WEArcheryItems.pebble : ammoDrop.getItem();
    }

    protected void dropFewItems(boolean recentlyHit, int looting) {
        int j;
        int k;
        if (this.getSkeletonType() == 1) {
            j = this.rand.nextInt(3 + looting) - 1;
            for (k = 0; k < j; ++k) {
                this.dropItem(Items.coal, 1);
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

    public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
        IEntityLivingData spawned = super.onSpawnWithEgg(data);
        if (this.getHeldItem() == null || this.getHeldItem().getItem() != WEArcheryItems.slingshot) {
            this.setCurrentItemOrArmor(0, new ItemStack(WEArcheryItems.slingshot));
        }
        if (!SlingshotAmmoHelper.isMobUsableAmmo(this.slingshotAmmo)) {
            this.setSlingshotAmmo(SlingshotAmmoHelper.randomMobAmmo(this.rand));
        }
        this.ensureSlingshotAttackTask();
        return spawned;
    }

    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        if (this.slingshotAmmo != null && this.slingshotAmmo.getItem() != null) {
            NBTTagCompound ammoTag = new NBTTagCompound();
            this.slingshotAmmo.writeToNBT(ammoTag);
            tag.setTag("SlingshotAmmo", ammoTag);
        }
    }

    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        if (tag.hasKey("SlingshotAmmo", 10)) {
            this.setSlingshotAmmo(ItemStack.loadItemStackFromNBT(tag.getCompoundTag("SlingshotAmmo")));
        } else {
            this.setSlingshotAmmo(SlingshotAmmoHelper.randomMobAmmo(this.rand));
        }
        if (this.getHeldItem() == null || this.getHeldItem().getItem() != WEArcheryItems.slingshot) {
            this.setCurrentItemOrArmor(0, new ItemStack(WEArcheryItems.slingshot));
        }
        this.ensureSlingshotAttackTask();
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

    public String getCommandSenderName() {
        return StatCollector.translateToLocal("entity.riftflux_slingshot_skeleton.name");
    }

    @Override
    public void setCurrentItemOrArmor(int slot, ItemStack stack) {
        super.setCurrentItemOrArmor(slot, stack);
        if (slot == 0) {
            this.ensureSlingshotAttackTask();
        }
    }

    private void setSlingshotAmmo(ItemStack ammo) {
        if (ammo == null || ammo.getItem() == null) {
            this.slingshotAmmo = null;
            return;
        }
        this.slingshotAmmo = ammo.copy();
        this.slingshotAmmo.stackSize = 1;
    }

    private ItemStack getSlingshotAmmo() {
        if (!SlingshotAmmoHelper.isMobUsableAmmo(this.slingshotAmmo)) {
            this.setSlingshotAmmo(SlingshotAmmoHelper.randomMobAmmo(this.rand));
        }
        return this.slingshotAmmo == null ? null : this.slingshotAmmo.copy();
    }

    private void ensureSlingshotAttackTask() {
        if (this.slingshotAttackAI == null) {
            return;
        }
        this.tasks.removeTask(this.slingshotAttackAI);
        this.tasks.addTask(4, this.slingshotAttackAI);
    }
}
