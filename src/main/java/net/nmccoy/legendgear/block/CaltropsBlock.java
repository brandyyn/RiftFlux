/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.block.Block
 *  net.minecraft.block.Block$SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.DamageSource
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.block;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.blessings.BlessingHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.nmccoy.legendgear.LegendGear2;
import java.util.Set;

public class CaltropsBlock
extends Block {
    private static final String NBT_CALTROPS_COOLDOWN_UNTIL = "RiftFluxCaltropsCooldownUntil";
    private static final int PERSISTENT_CALTROPS_COOLDOWN_TICKS = 10;
    public static final double HITBOX_MIN = 0.1875D;
    public static final double HITBOX_MAX = 0.8125D;
    public static final double HITBOX_HEIGHT = 0.1875D;
    DamageSource caltropsDamage;
    public static float steppedOnDropChance = 0.5f;

    public CaltropsBlock() {
        super(Material.circuits);
        this.setCreativeTab(LegendGear2.legendgearTab);
        this.setBlockBounds((float)HITBOX_MIN, 0.0f, (float)HITBOX_MIN, (float)HITBOX_MAX, (float)HITBOX_HEIGHT, (float)HITBOX_MAX);
        this.setHardness(0.0f);
        this.setBlockName("caltrops");
        this.setBlockTextureName("legendgear:blockCaltrops");
        if (LegendGear2.CONFIG_CALTROPS_REQUIRE_PICKAXE_TO_DROP) {
            this.setHarvestLevel("pickaxe", 0);
        }
        this.caltropsDamage = new DamageSource("caltrops");
        this.caltropsDamage.setDamageBypassesArmor();
        Block.SoundType caltropsSound = new Block.SoundType("caltrops", 0.5f, 1.2f){

            @Override
            public String getBreakSound() {
                return "legendgear:caltropsland";
            }

            @Override
            public String getStepResourcePath() {
                return "legendgear:caltropsland";
            }

            @Override
            public String func_150496_b() {
                return "legendgear:caltropsland";
            }
        };
        this.setStepSound(caltropsSound);
    }

    public float getBlockHardness(World world, int x, int y, int z) {
        return LegendGear2.CONFIG_CALTROPS_IRON_BARS_MINING_SPEED ? 5.0F : super.getBlockHardness(world, x, y, z);
    }

    public boolean canHarvestBlock(EntityPlayer player, int meta) {
        if (!LegendGear2.CONFIG_CALTROPS_REQUIRE_PICKAXE_TO_DROP) {
            return true;
        }
        if (player == null) {
            return false;
        }

        ItemStack equipped = player.getCurrentEquippedItem();
        if (equipped == null || equipped.getItem() == null) {
            return false;
        }

        Set<String> toolClasses = equipped.getItem().getToolClasses(equipped);
        return toolClasses != null && toolClasses.contains("pickaxe");
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        return null;
    }

    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(x + HITBOX_MIN, y, z + HITBOX_MIN, x + HITBOX_MAX, y + HITBOX_HEIGHT, z + HITBOX_MAX);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        this.setBlockBounds((float)HITBOX_MIN, 0.0f, (float)HITBOX_MIN, (float)HITBOX_MAX, (float)HITBOX_HEIGHT, (float)HITBOX_MAX);
    }

    public void setBlockBoundsForItemRender() {
        this.setBlockBounds((float)HITBOX_MIN, 0.0f, (float)HITBOX_MIN, (float)HITBOX_MAX, (float)HITBOX_HEIGHT, (float)HITBOX_MAX);
    }

    public int getRenderType() {
        return 6;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    private boolean hasSupportForCaltrops(World world, int x, int y, int z) {
        Block support = world.getBlock(x, y, z);
        if (support == null
                || support.isAir((IBlockAccess) world, x, y, z)
                || support.getMaterial().isLiquid()
                || support.isReplaceable((IBlockAccess) world, x, y, z)) {
            return false;
        }
        return World.doesBlockHaveSolidTopSurface((IBlockAccess) world, x, y, z)
                || support.getCollisionBoundingBoxFromPool(world, x, y, z) != null;
    }

    public boolean canBlockStay(World par1World, int par2, int par3, int par4) {
        return this.hasSupportForCaltrops(par1World, par2, par3 - 1, par4);
    }

    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4) {
        if (!this.canBlockStay(par1World, par2, par3, par4)) {
            return false;
        }
        Block target = par1World.getBlock(par2, par3, par4);
        if (target.getMaterial().isLiquid()) {
            return false;
        }
        return target.isReplaceable((IBlockAccess)par1World, par2, par3, par4);
    }

    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block block) {
        if (!this.canBlockStay(par1World, par2, par3, par4)) {
            this.dropBlockAsItem(par1World, par2, par3, par4, 0, 0);
            par1World.setBlockToAir(par2, par3, par4);
        }
    }

    public void addRecipes() {
        GameRegistry.addRecipe((ItemStack)new ItemStack((Block)this, 4), (Object[])new Object[]{" X ", " X ", "X X", Character.valueOf('X'), Items.iron_ingot});
    }

    private boolean isImmuneToCaltrops(EntityLivingBase target) {
        if (!(target instanceof EntityPlayer) || !ModConfig.blessingsEnabled) {
            return false;
        }
        String blessing = BlessingHelper.getBlessing((EntityPlayer) target);
        return "Mechanic".equals(blessing) && BlessingHelper.isBlessingEnabled(blessing);
    }

    private float getConfiguredDamage(EntityLivingBase target) {
        if (target instanceof EntityPlayer) {
            return Math.max(0.0f, target.getMaxHealth() * (LegendGear2.CONFIG_CALTROPS_PLAYER_DAMAGE_PERCENT / 100.0f));
        }
        return Math.max(0.0f, LegendGear2.CONFIG_CALTROPS_MOB_DAMAGE);
    }

    private boolean isOnPersistentTriggerCooldown(World world, EntityLivingBase target) {
        if (world == null || target == null || LegendGear2.CONFIG_CALTROPS_BREAK_ON_TRIGGER) {
            return false;
        }
        NBTTagCompound data = target.getEntityData();
        return data != null && data.getLong(NBT_CALTROPS_COOLDOWN_UNTIL) > world.getTotalWorldTime();
    }

    private void markPersistentTriggerCooldown(World world, EntityLivingBase target) {
        if (world == null || target == null || LegendGear2.CONFIG_CALTROPS_BREAK_ON_TRIGGER) {
            return;
        }
        target.getEntityData().setLong(NBT_CALTROPS_COOLDOWN_UNTIL, world.getTotalWorldTime() + PERSISTENT_CALTROPS_COOLDOWN_TICKS);
    }

    private void playTriggerSound(World world, int x, int y, int z) {
        if (world == null || world.isRemote) {
            return;
        }
        world.playSoundEffect(
                (double)x + 0.5D,
                (double)y + 0.5D,
                (double)z + 0.5D,
                "legendgear:caltropsland",
                0.5F,
                world.rand.nextFloat() * 0.4F + 0.9F
        );
    }

    public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {
        if (par1World == null || par1World.isRemote || !(par5Entity instanceof EntityLivingBase) || par5Entity.isSneaking()) {
            return;
        }

        EntityLivingBase living = (EntityLivingBase) par5Entity;
        if (this.isImmuneToCaltrops(living)) {
            return;
        }
        if (this.isOnPersistentTriggerCooldown(par1World, living)) {
            return;
        }

        this.playTriggerSound(par1World, par2, par3, par4);

        float damage = this.getConfiguredDamage(living);
        if (damage <= 0.0f || !living.attackEntityFrom(this.caltropsDamage, damage)) {
            return;
        }

        LegendGear2.addConfiguredPotionEffect(living, LegendGear2.CONFIG_CALTROPS_SLOWNESS_POTION_ID, Potion.moveSlowdown, 100, 2, true);
        LegendGear2.applyJumpPenalty(living, 100, 2, true);
        if (LegendGear2.CONFIG_CALTROPS_BREAK_ON_TRIGGER) {
            par1World.setBlockToAir(par2, par3, par4);
        } else {
            this.markPersistentTriggerCooldown(par1World, living);
        }
        if (LegendGear2.CONFIG_CALTROPS_BREAK_ON_TRIGGER
                && LegendGear2.CONFIG_CALTROPS_TRIGGER_DROP_ENABLED
                && steppedOnDropChance > 0.0f) {
            this.dropBlockAsItemWithChance(par1World, par2, par3, par4, 0, steppedOnDropChance, 0);
        }
    }
}
