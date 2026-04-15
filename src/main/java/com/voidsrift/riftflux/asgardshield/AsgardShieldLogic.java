package com.voidsrift.riftflux.asgardshield;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.compat.BackhandCompat;
import com.voidsrift.riftflux.inventorypets.ItemInventoryShieldPet;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class AsgardShieldLogic {
    private AsgardShieldLogic() {
    }

    public static boolean isAsgardItem(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return false;
        }
        Item item = stack.getItem();
        return item instanceof ItemAsgardShield || item instanceof ItemAsgardGreatsword || item instanceof ItemInventoryShieldPet;
    }

    public static boolean isAsgardShield(ItemStack stack) {
        return stack != null && (stack.getItem() instanceof ItemAsgardShield || stack.getItem() instanceof ItemInventoryShieldPet);
    }

    public static boolean isAsgardGreatsword(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemAsgardGreatsword;
    }

    public static boolean isBlockingWithAsgardItem(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        return isAsgardItem(getActiveGuardStack(player));
    }

    public static ItemStack getActiveGuardStack(EntityPlayer player) {
        if (player == null) {
            return null;
        }

        ItemStack inUse = player.getItemInUse();
        if (isAsgardItem(inUse)) {
            return inUse;
        }

        if (isUsingOffhandAsgardItem(player)) {
            return BackhandCompat.getOffhandItem(player);
        }

        if (player.isUsingItem() && isAsgardItem(inUse)) {
            return inUse;
        }

        return null;
    }

    public static ItemStack getEquippedAsgardItem(EntityPlayer player) {
        if (player == null) {
            return null;
        }

        ItemStack active = getActiveGuardStack(player);
        if (isAsgardItem(active)) {
            return active;
        }

        ItemStack held = player.getHeldItem();
        if (isAsgardItem(held)) {
            return held;
        }

        ItemStack offhand = BackhandCompat.getOffhandItem(player);
        return isAsgardItem(offhand) ? offhand : null;
    }

    public static void stopGuardUse(EntityPlayer player, ItemStack guardStack) {
        if (player == null) {
            return;
        }

        player.stopUsingItem();

        if (!BackhandCompat.isAvailable()) {
            return;
        }

        ItemStack offhand = BackhandCompat.getOffhandItem(player);
        boolean usesOffhand = (guardStack != null && BackhandCompat.isOffhandStack(player, guardStack))
                || (offhand != null && isAsgardItem(offhand) && BackhandCompat.isOffhandItemInUse(player));
        if (usesOffhand) {
            BackhandCompat.setOffhandItemInUse(player, false);
        }
    }

    public static void clearStaleOffhandGuardUse(EntityPlayer player) {
        if (player == null || !BackhandCompat.isAvailable() || !BackhandCompat.isOffhandItemInUse(player)) {
            return;
        }

        ItemStack offhand = BackhandCompat.getOffhandItem(player);
        if (!isAsgardItem(offhand)) {
            return;
        }

        ItemStack inUse = player.getItemInUse();
        if (!isAsgardItem(inUse)) {
            BackhandCompat.setOffhandItemInUse(player, false);
        }
    }

    public static float getPassiveDamageMultiplier(ItemStack held) {
        if (held == null || held.getItem() == null) {
            return 1.0F;
        }
        if (held.getItem() instanceof ItemAsgardShield) {
            return ((ItemAsgardShield) held.getItem()).getDamageMultiplier();
        }
        if (held.getItem() instanceof ItemInventoryShieldPet) {
            return ((ItemInventoryShieldPet) held.getItem()).getDamageMultiplier();
        }
        if (held.getItem() instanceof ItemAsgardGreatsword) {
            return ((ItemAsgardGreatsword) held.getItem()).getDamageMultiplier();
        }
        return 1.0F;
    }

    public static boolean handleGuardHit(EntityPlayer player, DamageSource source, float incomingDamage) {
        if (!canResolveGuardHit(player, source)) {
            return false;
        }

        ItemStack held = getActiveGuardStack(player);
        int incoming = Math.max(1, MathHelper.ceiling_float_int(incomingDamage));
        incoming = absorbAuraDamage(player, incoming);
        if (incoming <= 0) {
            return true;
        }

        GuardResult result;
        if (held.getItem() instanceof ItemAsgardShield) {
            result = handleShieldGuard(player, (ItemAsgardShield) held.getItem(), held, source, incoming);
        } else if (held.getItem() instanceof ItemInventoryShieldPet) {
            result = handleShieldPetGuard(player, (ItemInventoryShieldPet) held.getItem(), held, source, incoming);
        } else {
            result = handleGreatswordGuard(player, (ItemAsgardGreatsword) held.getItem(), held, source, incoming);
        }

        if (result.allowDamageThrough) {
            return false;
        }

        if (result.itemDamage > 0 && held.stackSize > 0) {
            held.damageItem(result.itemDamage, player);
            if (held.stackSize <= 0) {
                destroyGuardItem(player, held);
            }
        }

        if (result.playGuardSound) {
            playGuardSound(player, result.soundProfile);
        }

        if (result.consumeProjectile && source.getSourceOfDamage() != null && source.isProjectile()) {
            source.getSourceOfDamage().setDead();
        }

        return true;
    }

    public static boolean canResolveGuardHit(EntityPlayer player, DamageSource source) {
        if (player == null || source == null || source.isUnblockable()) {
            return false;
        }

        ItemStack held = getActiveGuardStack(player);
        if (!isAsgardItem(held)) {
            return false;
        }

        if (held.getItem() instanceof ItemAsgardShield) {
            ItemAsgardShield shield = (ItemAsgardShield) held.getItem();
            Entity sourceEntity = source.getEntity();
            if (shield.getPerkId() == 6 && (sourceEntity instanceof EntityEnderman || sourceEntity instanceof EntityDragon)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isUsingOffhandAsgardItem(EntityPlayer player) {
        if (!BackhandCompat.isAvailable() || player == null) {
            return false;
        }

        ItemStack offhand = BackhandCompat.getOffhandItem(player);
        if (!isAsgardItem(offhand)) {
            return false;
        }

        if (BackhandCompat.isUsingOffhand(player) || BackhandCompat.isOffhandItemInUse(player)) {
            return true;
        }

        ItemStack inUse = player.getItemInUse();
        return inUse != null && BackhandCompat.isOffhandStack(player, inUse);
    }

    private static void destroyGuardItem(EntityPlayer player, ItemStack guardStack) {
        if (player == null || guardStack == null) {
            return;
        }

        if (BackhandCompat.isAvailable() && BackhandCompat.isOffhandStack(player, guardStack)) {
            int offhandSlot = BackhandCompat.getOffhandSlot(player);
            if (player.inventory != null
                    && player.inventory.mainInventory != null
                    && offhandSlot >= 0
                    && offhandSlot < player.inventory.mainInventory.length
                    && player.inventory.mainInventory[offhandSlot] == guardStack) {
                player.inventory.mainInventory[offhandSlot] = null;
                BackhandCompat.setOffhandItemInUse(player, false);
                return;
            }
        }

        if (player.getHeldItem() == guardStack) {
            player.destroyCurrentEquippedItem();
        }
    }

    private static GuardResult handleShieldGuard(EntityPlayer player,
                                                 ItemAsgardShield shieldItem,
                                                 ItemStack shieldStack,
                                                 DamageSource source,
                                                 int incomingDamage) {
        Random rand = player.getRNG();
        int perkChance = rand.nextInt(100) + 1;
        boolean gilded = shieldItem.isGilded();
        boolean allowExplosionPenalty = true;

        int durabilityDamage = Math.max(1, Math.round(incomingDamage * shieldItem.getDamageMultiplier()));
        GuardResult result = new GuardResult();
        result.soundProfile = shieldItem.getSoundProfile();
        result.consumeProjectile = true;

        durabilityDamage = applyEnchantMitigation(shieldStack, player, source, durabilityDamage);

        Entity sourceEntity = source.getEntity();

        switch (shieldItem.getPerkId()) {
            case 1: // Arrow Catch
                if ((perkChance >= 50 || gilded) && source.isProjectile() && sourceEntity instanceof EntityArrow) {
                    player.inventory.addItemStackToInventory(new ItemStack(Items.arrow));
                }
                if (source.isFireDamage()) {
                    durabilityDamage += 15;
                }
                break;

            case 2: // Fire Resistant
                if (source.isFireDamage() && gilded) {
                    durabilityDamage = Math.max(0, durabilityDamage / 2);
                }
                if (source.isExplosion()) {
                    durabilityDamage *= 3;
                }
                allowExplosionPenalty = false;
                break;

            case 3: // Explosive Resistant
                if (source.isExplosion() && gilded) {
                    durabilityDamage = Math.max(0, durabilityDamage / 2);
                }
                if (player.isInWater()) {
                    durabilityDamage += 15;
                }
                allowExplosionPenalty = false;
                break;

            case 4: // Projectile Reflect
                if (sourceEntity == null) {
                    durabilityDamage *= 2;
                    break;
                }
                if (perkChance >= (gilded ? 40 : 70)) {
                    reflectProjectile(player, sourceEntity, durabilityDamage);
                    durabilityDamage += 2;
                }
                break;

            case 5: // Fire Catch
                if (perkChance >= 50 || gilded) {
                    if (source.getSourceOfDamage() instanceof EntityFireball || sourceEntity instanceof EntityFireball) {
                        player.inventory.addItemStackToInventory(new ItemStack(Items.fire_charge));
                        player.worldObj.playSoundAtEntity(player, "fire.ignite", 1.0F, 1.0F);
                        player.worldObj.playAuxSFX(2004, MathHelper.floor_double(player.posX),
                                MathHelper.floor_double(player.posY + 0.5D), MathHelper.floor_double(player.posZ), 0);
                    }
                }
                player.addExhaustion(0.2F);
                break;

            case 6: // Teleport Displace
                if (sourceEntity instanceof EntityEnderman || sourceEntity instanceof EntityDragon) {
                    result.allowDamageThrough = true;
                    result.playGuardSound = false;
                    result.itemDamage = durabilityDamage;
                    return result;
                }
                if (sourceEntity instanceof EntityLivingBase && perkChance >= (gilded ? 60 : 80)) {
                    teleportRandomly((EntityLivingBase) sourceEntity, player.worldObj, rand);
                }
                break;

            case 7: // Fear
                if (sourceEntity != null && perkChance >= (gilded ? 60 : 85)) {
                    confuseNearby(sourceEntity, player);
                }
                if (perkChance >= 90) {
                    durabilityDamage *= gilded ? 2 : 3;
                }
                break;

            case 8: // Foulness
                if (sourceEntity instanceof EntityLivingBase && perkChance >= (gilded ? 90 : 80)) {
                    ((EntityLivingBase) sourceEntity).addPotionEffect(new PotionEffect(Potion.poison.id, 200, 0));
                }
                if (perkChance >= 95) {
                    player.addPotionEffect(new PotionEffect(Potion.poison.id, 200, 0));
                }
                break;

            case 9: // Soul Matrix
                if (AsgardShieldState.getLivingmetalAura(player) <= 0
                        && perkChance >= 90
                        && consumeMatrixCharge(player, true)) {
                    AsgardShieldState.setLivingmetalAura(player, gilded ? 30 : 20);
                    player.worldObj.playSoundAtEntity(player, "random.breath", 1.0F, 1.0F);
                }
                break;

            case 10: // Blood Matrix
                if (AsgardShieldState.getBiomassAura(player) <= 0
                        && perkChance >= 90
                        && consumeMatrixCharge(player, false)) {
                    AsgardShieldState.setBiomassAura(player, gilded ? 30 : 20);
                    player.worldObj.playSoundAtEntity(player, "random.breath", 1.0F, 1.0F);
                }
                break;

            default:
                break;
        }

        durabilityDamage *= explosionPenalty(source, allowExplosionPenalty);
        durabilityDamage = applyVitalityAndSanguinary(shieldStack, player, source, durabilityDamage);

        result.itemDamage = Math.max(0, durabilityDamage);
        result.playGuardSound = true;
        return result;
    }

    private static GuardResult handleShieldPetGuard(EntityPlayer player,
                                                    ItemInventoryShieldPet shieldPet,
                                                    ItemStack shieldStack,
                                                    DamageSource source,
                                                    int incomingDamage) {
        GuardResult result = new GuardResult();
        result.soundProfile = shieldPet.getSoundProfile();
        result.consumeProjectile = true;

        int durabilityDamage = Math.max(1, Math.round(incomingDamage * shieldPet.getDamageMultiplier()));
        durabilityDamage = applyEnchantMitigation(shieldStack, player, source, durabilityDamage);
        if (source.isExplosion() && shieldPet.isFluxVariant()) {
            durabilityDamage = Math.max(0, durabilityDamage / 2);
        }
        durabilityDamage = applyVitalityAndSanguinary(shieldStack, player, source, durabilityDamage);

        result.itemDamage = Math.max(0, durabilityDamage);
        result.playGuardSound = true;
        return result;
    }

    private static GuardResult handleGreatswordGuard(EntityPlayer player,
                                                     ItemAsgardGreatsword swordItem,
                                                     ItemStack swordStack,
                                                     DamageSource source,
                                                     int incomingDamage) {
        GuardResult result = new GuardResult();
        result.soundProfile = swordItem.getSoundProfile();
        result.consumeProjectile = true;

        int durabilityDamage = Math.max(1, Math.round(incomingDamage * swordItem.getDamageMultiplier()));
        durabilityDamage = applyEnchantMitigation(swordStack, player, source, durabilityDamage);
        durabilityDamage *= explosionPenalty(source, true);
        durabilityDamage = applyVitalityAndSanguinary(swordStack, player, source, durabilityDamage);

        result.itemDamage = Math.max(0, durabilityDamage);
        result.playGuardSound = true;
        return result;
    }

    private static int applyVitalityAndSanguinary(ItemStack stack, EntityPlayer player, DamageSource source, int durabilityDamage) {
        Random rand = player.getRNG();

        int vitalityLevel = getConfiguredEnchantLevel(ModConfig.asgardShieldHarkenVitalityAugmentId, stack);
        if (vitalityLevel > 0 && durabilityDamage > 0 && durabilityDamage <= player.getHealth()) {
            if (vitalityLevel * 5 >= rand.nextInt(100) + 1) {
                float saturation = player.getFoodStats().getSaturationLevel();
                if (saturation > 0.0F) {
                    player.getFoodStats().addExhaustion((durabilityDamage + 1) * saturation);
                } else {
                    player.getFoodStats().addExhaustion(durabilityDamage + 1);
                    durabilityDamage = 0;
                }
            }
        }

        int sanguinaryLevel = getConfiguredEnchantLevel(ModConfig.asgardShieldHarkenSanguinaryAugmentId, stack);
        if (sanguinaryLevel > 0 && player.getHealth() < player.getMaxHealth()) {
            if (sanguinaryLevel * 10 >= rand.nextInt(100) + 1) {
                player.heal(1.0F);
            }
        }

        return durabilityDamage;
    }

    private static int applyEnchantMitigation(ItemStack stack, EntityPlayer player, DamageSource source, int durabilityDamage) {
        Entity sourceEntity = source.getEntity();

        int level = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack);
        if (level > 0 && (source.isProjectile() || source.isFireDamage() || source.isExplosion())) {
            durabilityDamage = applyReductionPercent(durabilityDamage, level * 10);
        }

        level = EnchantmentHelper.getEnchantmentLevel(Enchantment.projectileProtection.effectId, stack);
        if (level > 0 && source.isProjectile()) {
            durabilityDamage = applyReductionPercent(durabilityDamage, level * 15);
        }

        level = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack);
        if (level > 0 && source.isFireDamage()) {
            durabilityDamage = applyReductionPercent(durabilityDamage, level * 15);
        }

        level = EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack);
        if (level > 0 && source.isExplosion()) {
            durabilityDamage = applyReductionPercent(durabilityDamage, level * 15);
        }

        level = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack);
        if (level > 0 && sourceEntity instanceof EntityLivingBase
                && ("mob".equals(source.getDamageType()) || "player".equals(source.getDamageType()))) {
            sourceEntity.attackEntityFrom(DamageSource.causeThornsDamage(player), level);
            sourceEntity.worldObj.playSoundAtEntity(sourceEntity, "damage.thorns", 0.5F, 1.0F);
            durabilityDamage += level;
        }

        level = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, stack);
        if (level > 0 && sourceEntity instanceof EntityLivingBase
                && ("mob".equals(source.getDamageType()) || "player".equals(source.getDamageType()))) {
            float yaw = player.rotationYaw * (float) Math.PI / 180.0F;
            float amount = (level + 1) * 0.5F;
            sourceEntity.addVelocity(-Math.sin(yaw) * amount, 0.1D, Math.cos(yaw) * amount);
        }

        int exudeLevel = getConfiguredEnchantLevel(ModConfig.asgardShieldHarkenExudeAugmentId, stack);
        if (exudeLevel > 0 && ("magic".equals(source.getDamageType()) || "wither".equals(source.getDamageType()))) {
            if (exudeLevel * 4 >= player.getRNG().nextInt(100) + 1) {
                durabilityDamage = 0;
            }
        }

        return Math.max(0, durabilityDamage);
    }

    private static int getConfiguredEnchantLevel(int enchantId, ItemStack stack) {
        if (stack == null || enchantId <= 0 || enchantId >= Enchantment.enchantmentsList.length) {
            return 0;
        }
        if (Enchantment.enchantmentsList[enchantId] == null) {
            return 0;
        }
        return EnchantmentHelper.getEnchantmentLevel(enchantId, stack);
    }

    private static int applyReductionPercent(int value, int percent) {
        if (value <= 0 || percent <= 0) {
            return value;
        }
        float scale = Math.max(0.0F, 1.0F - (percent * 0.01F));
        return Math.max(0, Math.round(value * scale));
    }

    private static int explosionPenalty(DamageSource source, boolean allow) {
        if (!allow) {
            return 1;
        }
        return source != null && source.isExplosion() ? 2 : 1;
    }

    private static void reflectProjectile(EntityPlayer player, Entity sourceEntity, int durabilityDamage) {
        World world = player.worldObj;
        if (world == null || sourceEntity == null) {
            return;
        }

        if (sourceEntity instanceof EntityArrow) {
            EntityArrow reflected = new EntityArrow(world, player, Math.max(0.8F, durabilityDamage / 3.0F));
            reflected.canBePickedUp = 0;
            world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F);
            if (!world.isRemote) {
                world.spawnEntityInWorld(reflected);
            }
            return;
        }

        if (sourceEntity instanceof EntityFireball) {
            double dx = sourceEntity.posX - player.posX;
            double dy = sourceEntity.boundingBox.minY + sourceEntity.height * 0.5D - (player.posY + player.getEyeHeight());
            double dz = sourceEntity.posZ - player.posZ;
            EntitySmallFireball fireball = new EntitySmallFireball(world, player, dx, dy, dz);
            fireball.posY = player.posY + player.getEyeHeight() + 0.5D;
            if (!world.isRemote) {
                world.spawnEntityInWorld(fireball);
            }
            sourceEntity.setDead();
        }
    }

    public static void spawnEnderFx(World world, double x, double y, double z, Random random) {
        if (world == null || random == null) {
            return;
        }
        for (int i = 0; i < 3; i++) {
            int signX = random.nextBoolean() ? 1 : -1;
            int signZ = random.nextBoolean() ? 1 : -1;
            double px = x + 0.5D + 0.25D * signX;
            double py = y + random.nextFloat();
            double pz = z + 0.5D + 0.25D * signZ;
            double vx = random.nextFloat() * signX;
            double vy = (random.nextFloat() - 0.5D) * 0.125D;
            double vz = random.nextFloat() * signZ;
            world.spawnParticle("portal", px, py, pz, vx, vy, vz);
        }
    }

    private static int absorbAuraDamage(EntityPlayer player, int incoming) {
        int livingAura = AsgardShieldState.getLivingmetalAura(player);
        if (livingAura > 0 && incoming > 0) {
            int absorbed = Math.min(livingAura, incoming);
            incoming -= absorbed;
            AsgardShieldState.setLivingmetalAura(player, livingAura - absorbed);
        }

        int biomassAura = AsgardShieldState.getBiomassAura(player);
        if (biomassAura > 0 && incoming > 0) {
            int absorbed = Math.min(biomassAura, incoming);
            incoming -= absorbed;
            AsgardShieldState.setBiomassAura(player, biomassAura - absorbed);
        }

        return Math.max(0, incoming);
    }

    private static boolean consumeMatrixCharge(EntityPlayer player, boolean soulMatrix) {
        if (player == null || player.inventory == null) {
            return false;
        }
        String[] candidates = soulMatrix
                ? new String[]{"item.HSSoulkeeper", "item.HSSoulVessel"}
                : new String[]{"item.HSBloodkeeper", "item.HSBloodVessel"};

        for (int slot = 0; slot < player.inventory.mainInventory.length; slot++) {
            ItemStack stack = player.inventory.mainInventory[slot];
            if (stack == null || stack.getItem() == null) {
                continue;
            }
            String name = stack.getItem().getUnlocalizedName();
            if (name == null) {
                continue;
            }
            boolean matches = false;
            for (String candidate : candidates) {
                if (candidate.equals(name)) {
                    matches = true;
                    break;
                }
            }
            if (!matches) {
                continue;
            }

            if (stack.isItemStackDamageable()) {
                stack.damageItem(1, player);
                if (stack.stackSize <= 0) {
                    Item replacement = GameRegistry.findItem("harkenscythe", soulMatrix ? "HSEssenceKeeper" : "HSEssenceVessel");
                    if (replacement != null) {
                        player.inventory.mainInventory[slot] = new ItemStack(replacement, 1, 0);
                    }
                }
            } else {
                stack.stackSize--;
                if (stack.stackSize <= 0) {
                    player.inventory.mainInventory[slot] = null;
                }
            }

            player.inventory.markDirty();
            return true;
        }
        return false;
    }

    private static void confuseNearby(Entity sourceEntity, EntityPlayer player) {
        if (sourceEntity == null || player == null || player.worldObj == null || player.worldObj.isRemote) {
            return;
        }

        AxisAlignedBB box = player.boundingBox.expand(10.0D, 10.0D, 10.0D);
        List<Entity> entities = player.worldObj.getEntitiesWithinAABB(Entity.class, box);
        List<EntityCreature> creatures = new ArrayList<EntityCreature>();
        for (Entity entity : entities) {
            if (!(entity instanceof EntityCreature) || entity == player) {
                continue;
            }
            creatures.add((EntityCreature) entity);
        }
        if (creatures.size() < 2) {
            return;
        }

        for (int i = 0; i < creatures.size(); i++) {
            EntityCreature creature = creatures.get(i);
            EntityLivingBase target = creatures.get(i == 0 ? creatures.size() - 1 : i - 1);
            creature.setAttackTarget(target);
        }
        player.worldObj.playSoundAtEntity(player, "mob.ghast.scream", 0.1F, 0.2F);
    }

    private static boolean teleportRandomly(EntityLivingBase entity, World world, Random random) {
        if (entity == null || world == null || random == null) {
            return false;
        }

        final double oldX = entity.posX;
        final double oldY = entity.posY;
        final double oldZ = entity.posZ;

        for (int tries = 0; tries < 16; tries++) {
            double x = oldX + (random.nextDouble() - 0.5D) * 24.0D;
            double y = oldY + (double) (random.nextInt(24) - 12);
            double z = oldZ + (random.nextDouble() - 0.5D) * 24.0D;
            if (tryTeleport(entity, world, x, y, z)) {
                world.playSoundEffect(oldX, oldY, oldZ, "mob.endermen.portal", 1.0F, 1.0F);
                world.playSoundAtEntity(entity, "mob.endermen.portal", 1.0F, 1.0F);
                spawnTeleportParticles(world, oldX, oldY, oldZ, entity.posX, entity.posY, entity.posZ, random);
                return true;
            }
        }

        entity.setPosition(oldX, oldY, oldZ);
        return false;
    }

    private static boolean tryTeleport(EntityLivingBase entity, World world, double x, double y, double z) {
        final double oldX = entity.posX;
        final double oldY = entity.posY;
        final double oldZ = entity.posZ;

        entity.setPosition(x, y, z);
        int bx = MathHelper.floor_double(entity.posX);
        int by = MathHelper.floor_double(entity.posY);
        int bz = MathHelper.floor_double(entity.posZ);

        if (!world.blockExists(bx, by, bz)) {
            entity.setPosition(oldX, oldY, oldZ);
            return false;
        }

        while (by > 0 && !world.getBlock(bx, by - 1, bz).getMaterial().blocksMovement()) {
            entity.setPosition(entity.posX, entity.posY - 1.0D, entity.posZ);
            by--;
        }

        if (!world.getCollidingBoundingBoxes(entity, entity.boundingBox).isEmpty() || world.isAnyLiquid(entity.boundingBox)) {
            entity.setPosition(oldX, oldY, oldZ);
            return false;
        }

        return true;
    }

    private static void spawnTeleportParticles(World world,
                                               double oldX,
                                               double oldY,
                                               double oldZ,
                                               double newX,
                                               double newY,
                                               double newZ,
                                               Random random) {
        int count = 128;
        for (int i = 0; i < count; i++) {
            double t = (double) i / (count - 1.0D);
            float vx = (random.nextFloat() - 0.5F) * 0.2F;
            float vy = (random.nextFloat() - 0.5F) * 0.2F;
            float vz = (random.nextFloat() - 0.5F) * 0.2F;
            double px = oldX + (newX - oldX) * t + (random.nextDouble() - 0.5D);
            double py = oldY + (newY - oldY) * t + random.nextDouble();
            double pz = oldZ + (newZ - oldZ) * t + (random.nextDouble() - 0.5D);
            world.spawnParticle("portal", px, py, pz, vx, vy, vz);
        }
    }

    private static void playGuardSound(EntityPlayer player, String soundProfile) {
        if (player == null || player.worldObj == null || soundProfile == null) {
            return;
        }
        String sound = "dig.stone";
        float volume = 0.4F;
        float pitch = 1.0F;

        if ("wood".equals(soundProfile)) {
            sound = "dig.wood";
            volume = 0.5F;
        } else if ("stone".equals(soundProfile)) {
            sound = "dig.stone";
            volume = 0.8F;
            pitch = 0.5F;
        } else if ("metal".equals(soundProfile)) {
            sound = "random.anvil_land";
            volume = 0.25F;
            pitch = 1.5F;
        } else if ("crystal".equals(soundProfile)) {
            sound = "random.glass";
            volume = 0.9F;
            pitch = 1.2F;
        } else if ("flesh".equals(soundProfile)) {
            sound = "mob.zombie.step";
            volume = 0.8F;
            pitch = 1.0F;
        } else if ("bone".equals(soundProfile)) {
            sound = "mob.skeleton.hurt";
            volume = 0.25F;
            pitch = 0.8F;
        }

        player.worldObj.playSoundAtEntity(player, sound, volume, pitch);
    }

    public static String getPerkName(int perkId) {
        switch (perkId) {
            case 1:
                return "Arrow Catch";
            case 2:
                return "Fire Resistant";
            case 3:
                return "Explosive Resistant";
            case 4:
                return "Projectile Reflect";
            case 5:
                return "Fire Catch";
            case 6:
                return "Teleport Displace";
            case 7:
                return "Fear";
            case 8:
                return "Foulness";
            case 9:
                return "Soul Matrix";
            case 10:
                return "Blood Matrix";
            default:
                return "---";
        }
    }

    public static String getPerkWeakness(int perkId) {
        switch (perkId) {
            case 1:
                return "Fire Damage";
            case 2:
                return "Explosive Damage";
            case 3:
                return "Water Damage";
            case 4:
                return "Reflected Damage";
            case 5:
                return "Fatigue";
            case 6:
                return "Ender Damage";
            case 7:
                return "Brittle Bones";
            case 8:
                return "Foulness";
            case 9:
                return "Soul Eater";
            case 10:
                return "Blood Drinker";
            default:
                return "---";
        }
    }

    private static final class GuardResult {
        private int itemDamage;
        private boolean allowDamageThrough;
        private boolean consumeProjectile;
        private boolean playGuardSound;
        private String soundProfile;
    }
}
