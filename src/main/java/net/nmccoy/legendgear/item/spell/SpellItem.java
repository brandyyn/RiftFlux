/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.stats.StatBase
 *  net.minecraft.util.MovingObjectPosition
 *  net.minecraft.util.Vec3
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.item.spell;

import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import com.voidsrift.riftflux.compat.BackhandCompat;
import net.nmccoy.legendgear.CustomAttributes;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.PlayerStarstatsExtension;
import net.nmccoy.legendgear.entity.EntitySpellEffect;
import net.nmccoy.legendgear.item.LGItem;
import net.nmccoy.legendgear.item.MagicRing;
import net.nmccoy.legendgear.magic.IMana;

public class SpellItem
extends LGItem
implements IMana {
    protected double baseCastRange;
    protected double baseCastRadius;
    protected double baseCastTime;
    protected double baseCritBonus;
    protected double baseArcanePower;
    protected float baseStaminaCost;
    protected int baseMeleeDamage;
    protected EntitySpellEffect.SpellType spellType;
    protected boolean isStaff = false;
    protected boolean isScroll = false;
    protected boolean isTome = false;
    public boolean hitsWater = false;
    public boolean hideIdleReticle = false;
    public static final int CRIT_WINDOW = 5;
    public static final UUID weaponPowerUUID = UUID.fromString("1556de23-3415-43b9-8b3b-1706b408759b");
    public static final UUID weaponRangeUUID = UUID.fromString("3f067f1d-4f88-456c-a45c-973870817514");
    public static final UUID weaponRadiusUUID = UUID.fromString("9150e1fb-32ea-4ca1-9e83-fd2e7473e95d");
    public static final UUID weaponSurgeUUID = UUID.fromString("b0ce01be-bd12-4864-890f-50b20969b188");

    public boolean isDamageable() {
        return true;
    }

    public EntitySpellEffect.SpellType getSpell(ItemStack stack) {
        return this.spellType;
    }

    public int getMaxDamage() {
        return super.getMaxDamage();
    }

    @Override
    public float getManaCost() {
        return this.baseStaminaCost;
    }

    public SpellItem() {
        this.setUnlocalizedName("genericSpellItem");
        this.setMaxStackSize(1);
        this.baseArcanePower = 8.0;
        this.baseMeleeDamage = 3;
        this.baseCastRadius = 3.0;
        this.baseCastRange = 7.0;
        this.baseCastTime = 0.75;
        this.baseStaminaCost = 4.0f;
    }

    public int getItemEnchantability() {
        return 1;
    }

    public boolean isFull3D() {
        return this.isStaff;
    }

    public boolean shouldRotateAroundWhenRendering() {
        return false;
    }

    public Multimap getAttributeModifiers(ItemStack stack) {
        Multimap map = super.getAttributeModifiers(stack);
        map.put((Object)CustomAttributes.arcanePower.getAttributeUnlocalizedName(), (Object)new AttributeModifier(weaponPowerUUID, "Weapon Power", this.getBasePower(stack), 0));
        map.put((Object)CustomAttributes.spellRange.getAttributeUnlocalizedName(), (Object)new AttributeModifier(weaponRangeUUID, "Weapon Range", this.getBaseRange(stack), 0));
        map.put((Object)CustomAttributes.spellRadius.getAttributeUnlocalizedName(), (Object)new AttributeModifier(weaponRadiusUUID, "Weapon Radius", this.getBaseRadius(stack), 0));
        map.put((Object)CustomAttributes.spellSurgePower.getAttributeUnlocalizedName(), (Object)new AttributeModifier(weaponSurgeUUID, "Weapon Surge", this.getBaseCritBonus(stack), 0));
        map.put((Object)SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), (Object)new AttributeModifier(field_111210_e, "Weapon modifier", (double)this.getSmackDamage(stack), 0));
        return map;
    }

    protected double getBaseCastTime(ItemStack stack) {
        return this.baseCastTime;
    }

    protected double getBaseCritBonus(ItemStack stack) {
        return this.baseCritBonus;
    }

    protected double getBasePower(ItemStack stack) {
        return this.baseArcanePower;
    }

    protected double getBaseRange(ItemStack stack) {
        int reach = EnchantmentHelper.getEnchantmentLevel((int)LegendGear2.enchSpellReachID, (ItemStack)stack);
        return this.baseCastRange * (1.0 + 0.15 * (double)reach);
    }

    protected double getBaseRadius(ItemStack stack) {
        int spread = EnchantmentHelper.getEnchantmentLevel((int)LegendGear2.enchSpellSpreadID, (ItemStack)stack);
        return this.baseCastRadius * (1.0 + 0.15 * (double)spread);
    }

    protected int getSmackDamage(ItemStack stack) {
        return this.baseMeleeDamage;
    }

    public int getMaxItemUseDuration(ItemStack p_77626_1_) {
        return 65535;
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        if (this.isScroll) {
            return EnumAction.block;
        }
        return EnumAction.bow;
    }

    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int count) {
        if (BackhandCompat.isAvailable() && BackhandCompat.isOffhandStack(player, stack)) {
            BackhandCompat.setOffhandItemInUse(player, false);
        }
        int totalTicks;
        int ticks = this.getMaxItemUseDuration(stack) - count;
        if (ticks >= (totalTicks = this.getCastingTicks(stack, player))) {
            boolean crit = false;
            if (ticks - totalTicks <= 5) {
                crit = true;
            }
            this.castSpell(player, stack, crit);
        }
    }

    public void castSpell(EntityPlayer player, ItemStack stack, boolean crit) {
        double range = this.getEffectiveCastRange(stack, player);
        double radius = this.getEffectiveCastRadius(stack, player);
        double power = this.getEffectivePower(stack, player);
        double critBonus = this.getEffectiveCritBonus(stack, player);
        if (this.isStaff) {
            player.addStat((StatBase)LegendGear2.achievementMage, 1);
        }
        if (this.isTome) {
            player.addStat((StatBase)LegendGear2.achievementReadSpellbook, 1);
        }
        if (crit) {
            power += critBonus;
            player.worldObj.playSoundAtEntity((Entity)player, "legendgear:spellcrit", 0.3f, 1.0f);
        }
        Vec3 from = Vec3.createVectorHelper((double)player.posX, (double)(player.posY + (double)player.eyeHeight), (double)player.posZ);
        Vec3 castPoint = SpellItem.getRayTargetResult(from, player.getLookVec(), range, player.worldObj, this.hitsWater);
        if (!player.capabilities.isCreativeMode) {
            if (this.isStaff) {
                if (stack.isItemStackDamageable()) {
                    stack.damageItem(1, (EntityLivingBase)player);
                }
            } else if (stack.getMaxDamage() > 0) {
                stack.damageItem(1, (EntityLivingBase)player);
            } else {
                --stack.stackSize;
            }
            if (stack.stackSize <= 0) {
                player.destroyCurrentEquippedItem();
            }
        }
        this.GenerateSpell(player, this.getSpell(stack), castPoint, radius, power, crit);
        player.addExhaustion(0.2f);
        PlayerStarstatsExtension pse = PlayerStarstatsExtension.get(player);
        float multiplier = 1.0f;
        if (MagicRing.PlayerWears(player, MagicRing.RingType.MAGE_RING)) {
            multiplier = MagicRing.PlayerWears(player, MagicRing.RingType.RESONANCE_RING) ? MagicRing.MAGE_AND_RESONANCE_FACTOR : MagicRing.MAGE_RING_FACTOR;
        }
        pse.expendMana(stack, this.baseStaminaCost * multiplier);
        player.swingItem();
    }

    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase wielder) {
        if (!this.isStaff) {
            return true;
        }
        if (!wielder.worldObj.isRemote) {
            boolean flag;
            boolean bl = flag = wielder.fallDistance > 0.0f && !wielder.onGround && !wielder.isOnLadder() && !wielder.isInWater() && !wielder.isPotionActive(Potion.blindness) && wielder.ridingEntity == null;
            if (flag && this.isStaff && wielder instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer)wielder;
                stack.damageItem(1, wielder);
                this.GenerateSpell(
                        player,
                        this.spellType,
                        Vec3.createVectorHelper((double)target.posX, (double)(target.posY + (double)(target.height / 2.0f)), (double)target.posZ),
                        this.getEffectiveCastRadius(stack, player) / 2.0,
                        this.getEffectivePower(stack, player) / 2.0,
                        false
                );
            }
        }
        stack.damageItem(2, wielder);
        return true;
    }

    public void GenerateSpell(EntityPlayer caster, EntitySpellEffect.SpellType id, Vec3 location, double radius, double power, boolean critical) {
        if (!caster.worldObj.isRemote) {
            caster.worldObj.spawnEntityInWorld((Entity)new EntitySpellEffect(caster.worldObj, id, caster, location, radius, power, critical));
        }
    }

    public static Vec3 getRayTargetResult(Vec3 from, Vec3 direction, double maxDistance, World world, boolean water) {
        Vec3 hitPos;
        Vec3 castDestination = from.addVector(direction.xCoord * maxDistance, direction.yCoord * maxDistance, direction.zCoord * maxDistance);
        Vec3 copyFrom = Vec3.createVectorHelper((double)from.xCoord, (double)from.yCoord, (double)from.zCoord);
        MovingObjectPosition mop = world.rayTraceBlocks(copyFrom, castDestination, water);
        if (mop != null && (hitPos = mop.hitVec).distanceTo(from) < maxDistance) {
            return hitPos;
        }
        direction = direction.normalize();
        direction.xCoord *= maxDistance;
        direction.yCoord *= maxDistance;
        direction.zCoord *= maxDistance;
        return from.addVector(direction.xCoord, direction.yCoord, direction.zCoord);
    }

    public float getCastingProgress(ItemStack stack, EntityPlayer player, float subtick) {
        return Math.min(((float)player.getItemInUseDuration() + subtick) / (float)this.getCastingTicks(stack, player), 1.0f);
    }

    public int getCastingTicks(ItemStack stack, EntityPlayer player) {
        double time = this.getBaseCastTime(stack);
        int fatigue = PlayerStarstatsExtension.get(player).fatigueLevel();
        if (fatigue == 1) {
            time *= 2.0;
        }
        if (fatigue == 2) {
            time *= 4.0;
        }
        return (int)(time * 20.0);
    }

    public static double getPlayerPower(EntityPlayer player) {
        return player.getAttributeMap().getAttributeInstance(CustomAttributes.arcanePower).getAttributeValue();
    }

    public static double getPlayerCastRange(EntityPlayer player) {
        return player.getAttributeMap().getAttributeInstance(CustomAttributes.spellRange).getAttributeValue();
    }

    public static double getPlayerCastRadius(EntityPlayer player) {
        return player.getAttributeMap().getAttributeInstance(CustomAttributes.spellRadius).getAttributeValue();
    }

    public static double getPlayerCritBonus(EntityPlayer player) {
        return player.getAttributeMap().getAttributeInstance(CustomAttributes.spellSurgePower).getAttributeValue();
    }

    public double getEffectivePower(ItemStack stack, EntityPlayer player) {
        return this.resolveEffectiveAttributeValue(player, CustomAttributes.arcanePower, weaponPowerUUID, this.getBasePower(stack));
    }

    public double getEffectiveCastRange(ItemStack stack, EntityPlayer player) {
        return this.resolveEffectiveAttributeValue(player, CustomAttributes.spellRange, weaponRangeUUID, this.getBaseRange(stack));
    }

    public double getEffectiveCastRadius(ItemStack stack, EntityPlayer player) {
        return this.resolveEffectiveAttributeValue(player, CustomAttributes.spellRadius, weaponRadiusUUID, this.getBaseRadius(stack));
    }

    public double getEffectiveCritBonus(ItemStack stack, EntityPlayer player) {
        return this.resolveEffectiveAttributeValue(player, CustomAttributes.spellSurgePower, weaponSurgeUUID, this.getBaseCritBonus(stack));
    }

    private double resolveEffectiveAttributeValue(EntityPlayer player, IAttribute attribute, UUID modifierId, double desiredModifier) {
        if (player == null || attribute == null) {
            return desiredModifier;
        }

        IAttributeInstance instance = player.getAttributeMap().getAttributeInstance(attribute);
        if (instance == null) {
            return desiredModifier;
        }

        double value = instance.getAttributeValue();
        AttributeModifier currentModifier = instance.getModifier(modifierId);
        if (currentModifier != null) {
            value -= currentModifier.getAmount();
        }
        return value + desiredModifier;
    }

    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        int ticks = this.getMaxItemUseDuration(stack) - count;
        int cast_time = this.getCastingTicks(stack, player);
        if (!player.worldObj.isRemote) {
            if (ticks == cast_time / 3) {
                player.worldObj.playSoundAtEntity((Entity)player, "legendgear:highcharge", 0.3f, 0.5f);
            }
            if (ticks == cast_time * 2 / 3) {
                player.worldObj.playSoundAtEntity((Entity)player, "legendgear:highcharge", 0.3f, 0.75f);
            }
            if (ticks == cast_time) {
                player.worldObj.playSoundAtEntity((Entity)player, "legendgear:highcharge", 0.3f, 1.0f);
            }
        }
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (BackhandCompat.isAvailable() && BackhandCompat.isOffhandStack(player, stack)) {
            BackhandCompat.setOffhandItemInUse(player, true);
        }
        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        super.addInformation(stack, player, list, p_77624_4_);
    }
}
