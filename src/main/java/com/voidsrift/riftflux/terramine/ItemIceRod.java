package com.voidsrift.riftflux.terramine;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.avatar.glider.ItemGlider;
import com.voidsrift.riftflux.compat.BackhandCompat;
import com.voidsrift.riftflux.legendgear.LegendGearContent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.nmccoy.legendgear.PlayerStarstatsExtension;

public class ItemIceRod extends Item {
    private static final int AIR_SEARCH_RADIUS = 1;

    public ItemIceRod() {
        this.setMaxStackSize(1);
        this.setMaxDamage(Math.max(0, ModConfig.iceRodDurability));
        this.setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabCombat);
        this.setTextureName("riftflux:ice_rod");
        this.setFull3D();
    }

    @Override
    public boolean isFull3D() {
        return true;
    }

    @Override
    public boolean shouldRotateAroundWhenRendering() {
        return false;
    }

    @Override
    public int getMaxDamage() {
        return getConfiguredDurability();
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return getConfiguredDurability();
    }

    @Override
    public boolean isDamageable() {
        return getConfiguredDurability() > 0 && !this.getHasSubtypes();
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return getConfiguredDurability() > 0 && super.isDamaged(stack);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        if (getConfiguredDurability() > 0) {
            super.setDamage(stack, damage);
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player == null) {
            return stack;
        }
        if (shouldIgnoreOffhandActivation(stack, player)) {
            return stack;
        }
        if (world.isRemote) {
            return stack;
        }

        boolean useLegendGearMana = shouldUseLegendGearMana(player);
        if (useLegendGearMana && !hasEnoughLegendGearMana(player)) {
            return stack;
        }

        int[] placePos = findPlacementForPlayer(world, player);
        if (placePos == null) {
            return stack;
        }

        if (world.setBlock(placePos[0], placePos[1], placePos[2], TerrariaContent.iceRodBlock, 0, 3)) {
            world.playAuxSFX(2001, placePos[0], placePos[1], placePos[2], Block.getIdFromBlock(TerrariaContent.magicIceBlock));
            if (!player.capabilities.isCreativeMode && getConfiguredDurability() > 0) {
                stack.damageItem(1, player);
            }
            if (useLegendGearMana) {
                spendLegendGearMana(stack, player);
            }
        }

        return stack;
    }

    private static int getConfiguredDurability() {
        return Math.max(0, ModConfig.iceRodDurability);
    }

    private static double getConfiguredSpawnDistance() {
        return Math.max(1.0D, (double) ModConfig.iceRodSpawnDistance);
    }

    private static float getConfiguredLegendGearManaCost() {
        return Math.max(0.0F, ModConfig.iceRodLegendGearManaCost);
    }

    public int[] findPlacementForPlayer(World world, EntityPlayer player) {
        AimContext context = createAimContext(world, player, -1.0F);
        if (context == null) {
            return null;
        }
        int[] target = findAimTarget(world, context);
        if (target == null) {
            return null;
        }
        return findPlacementInCubeRadius(
                world,
                target[0],
                target[1],
                target[2],
                context.look,
                context.eyeX,
                context.eyeY,
                context.eyeZ
        );
    }

    public int[] findPlacementForPlayer(World world, EntityPlayer player, float partialTicks) {
        AimContext context = createAimContext(world, player, partialTicks);
        if (context == null) {
            return null;
        }
        int[] target = findAimTarget(world, context);
        if (target == null) {
            return null;
        }
        return findPlacementInCubeRadius(
                world,
                target[0],
                target[1],
                target[2],
                context.look,
                context.eyeX,
                context.eyeY,
                context.eyeZ
        );
    }

    private int[] findAimTarget(World world, AimContext context) {
        if (world == null || context == null) {
            return null;
        }
        int x;
        int y;
        int z;

        MovingObjectPosition hit = context.hit;
        if (hit != null && hit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            int tx = hit.blockX;
            int ty = hit.blockY;
            int tz = hit.blockZ;
            Block targeted = world.getBlock(tx, ty, tz);

            if (targeted != null && targeted.isReplaceable(world, tx, ty, tz)) {
                x = tx;
                y = ty;
                z = tz;
            } else {
                int[] adjacent = findPreferredAdjacentPlacement(
                        world,
                        tx,
                        ty,
                        tz,
                        hit,
                        hit.sideHit,
                        context.look,
                        context.eyeX,
                        context.eyeY,
                        context.eyeZ
                );
                if (adjacent != null) {
                    return adjacent;
                }
                ForgeDirection side = ForgeDirection.getOrientation(hit.sideHit);
                x = tx + side.offsetX;
                y = ty + side.offsetY;
                z = tz + side.offsetZ;
            }
        } else {
            Vec3 look = context.look;
            if (look == null) {
                return null;
            }

            double distance = getConfiguredSpawnDistance();
            x = MathHelper.floor_double(context.baseX + look.xCoord * distance);
            y = MathHelper.floor_double(context.eyeY + look.yCoord * distance);
            z = MathHelper.floor_double(context.baseZ + look.zCoord * distance);
        }

        return new int[] {x, y, z};
    }

    private static int[] findPreferredAdjacentPlacement(
            World world,
            int tx,
            int ty,
            int tz,
            MovingObjectPosition hit,
            int preferredSideHit,
            Vec3 look,
            double eyeX,
            double eyeY,
            double eyeZ
    ) {
        if (world == null) {
            return null;
        }

        ForgeDirection preferred = ForgeDirection.getOrientation(preferredSideHit);
        if (preferred != null && preferred != ForgeDirection.UNKNOWN) {
            int px = tx + preferred.offsetX;
            int py = ty + preferred.offsetY;
            int pz = tz + preferred.offsetZ;
            if (canPlaceAt(world, px, py, pz)) {
                return new int[] {px, py, pz};
            }
        }

        ForgeDirection[] sides = new ForgeDirection[] {
                ForgeDirection.DOWN, ForgeDirection.UP,
                ForgeDirection.NORTH, ForgeDirection.SOUTH,
                ForgeDirection.WEST, ForgeDirection.EAST
        };

        for (ForgeDirection side : sides) {
            int x = tx + side.offsetX;
            int y = ty + side.offsetY;
            int z = tz + side.offsetZ;
            if (canPlaceAt(world, x, y, z)) {
                return new int[] {x, y, z};
            }
        }

        return null;
    }

    private static boolean shouldUseLegendGearMana(EntityPlayer player) {
        return player != null
                && !player.capabilities.isCreativeMode
                && ModConfig.iceRodUseLegendGearMana
                && LegendGearContent.isEnabled();
    }

    private static boolean hasEnoughLegendGearMana(EntityPlayer player) {
        if (player == null || player.capabilities.isCreativeMode) {
            return true;
        }
        float manaCost = getConfiguredLegendGearManaCost();
        if (manaCost <= 0.0F) {
            return true;
        }
        PlayerStarstatsExtension pse = PlayerStarstatsExtension.get(player);
        if (pse == null) {
            return true;
        }
        return PlayerStarstatsExtension.availableMana(player) + 1.0e-4f >= manaCost;
    }

    private static void spendLegendGearMana(ItemStack stack, EntityPlayer player) {
        if (player == null || player.capabilities.isCreativeMode) {
            return;
        }
        float manaCost = getConfiguredLegendGearManaCost();
        if (manaCost <= 0.0F) {
            return;
        }
        PlayerStarstatsExtension pse = PlayerStarstatsExtension.get(player);
        if (pse == null) {
            return;
        }
        pse.expendMana(stack, manaCost);
    }

    private static boolean shouldIgnoreOffhandActivation(ItemStack stack, EntityPlayer player) {
        if (player == null || stack == null || !BackhandCompat.isAvailable()) {
            return false;
        }
        ItemStack mainhand = BackhandCompat.getMainhandItem(player);
        if (isIceRodOrGlider(mainhand) && mainhand.getItem() != stack.getItem()) {
            return true;
        }
        if (!BackhandCompat.isOffhandStack(player, stack)) {
            return false;
        }
        return shouldBlockOffhandByMainhand(player);
    }

    public static boolean canUseFromOffhand(ItemStack stack, EntityPlayer player) {
        if (!BackhandCompat.isAvailable() || player == null || stack == null) {
            return true;
        }
        if (!BackhandCompat.isOffhandStack(player, stack)) {
            return true;
        }
        return !shouldBlockOffhandByMainhand(player);
    }

    private static boolean shouldBlockOffhandByMainhand(EntityPlayer player) {
        if (BackhandCompat.isMainhandUsingItem(player)) {
            return true;
        }
        ItemStack mainhand = BackhandCompat.getMainhandItem(player);
        return isIceRodOrGlider(mainhand) || BackhandCompat.mainhandConsumesRightClick(mainhand);
    }

    private static int[] findPlacementInCubeRadius(
            World world,
            int centerX,
            int centerY,
            int centerZ,
            Vec3 look,
            double eyeX,
            double eyeY,
            double eyeZ
    ) {
        if (world == null) {
            return null;
        }
        for (int radius = 0; radius <= AIR_SEARCH_RADIUS; radius++) {
            int[] yOffsets = orderedOffsets(radius, look == null ? 0.0D : look.yCoord);
            int[] zOffsets = orderedOffsets(radius, look == null ? 0.0D : look.zCoord);
            int[] xOffsets = orderedOffsets(radius, look == null ? 0.0D : look.xCoord);
            for (int dy : yOffsets) {
                for (int dz : zOffsets) {
                    for (int dx : xOffsets) {
                        int r = Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz)));
                        if (r != radius) {
                            continue;
                        }
                        int x = centerX + dx;
                        int y = centerY + dy;
                        int z = centerZ + dz;
                        if (canPlaceAt(world, x, y, z)) {
                            return new int[] {x, y, z};
                        }
                    }
                }
            }
        }

        return null;
    }

    private static int[] orderedOffsets(int radius, double direction) {
        if (radius <= 0) {
            return new int[] {0};
        }
        int[] offsets = new int[radius * 2 + 1];
        offsets[0] = 0;
        int index = 1;
        boolean positiveFirst = direction >= 0.0D;
        for (int step = 1; step <= radius; step++) {
            if (positiveFirst) {
                offsets[index++] = step;
                offsets[index++] = -step;
            } else {
                offsets[index++] = -step;
                offsets[index++] = step;
            }
        }
        return offsets;
    }

    private static boolean canPlaceAt(World world, int x, int y, int z) {
        if (world == null) {
            return false;
        }
        if (y < 1 || y >= world.getActualHeight()) {
            return false;
        }
        if (!world.blockExists(x, y, z)) {
            return false;
        }
        Block existing = world.getBlock(x, y, z);
        return world.isAirBlock(x, y, z) || (existing != null && existing.isReplaceable(world, x, y, z));
    }

    private static boolean isIceRodOrGlider(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return false;
        }
        Item item = stack.getItem();
        return item instanceof ItemIceRod || item instanceof ItemGlider;
    }

    private AimContext createAimContext(World world, EntityPlayer player, float partialTicks) {
        if (world == null || player == null) {
            return null;
        }

        if (partialTicks >= 0.0F) {
            double interpX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
            double interpY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
            double interpZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;
            float interpPitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
            float interpYaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;
            Vec3 look = getLookVector(interpYaw, interpPitch);
            if (look == null) {
                return null;
            }

            double eyeX = interpX;
            double eyeY = interpY + player.getEyeHeight();
            double eyeZ = interpZ;
            double distance = getConfiguredSpawnDistance();
            Vec3 start = Vec3.createVectorHelper(eyeX, eyeY, eyeZ);
            Vec3 end = start.addVector(look.xCoord * distance, look.yCoord * distance, look.zCoord * distance);
            MovingObjectPosition hit = world.rayTraceBlocks(start, end, false);
            return new AimContext(hit, look, eyeX, eyeY, eyeZ, interpX, interpY, interpZ);
        }

        Vec3 look = getLookVector(player.rotationYaw, player.rotationPitch);
        if (look == null) {
            return null;
        }
        double baseX = player.posX;
        double baseY = player.posY;
        double baseZ = player.posZ;
        double eyeY = baseY + player.getEyeHeight();
        Vec3 start = Vec3.createVectorHelper(baseX, eyeY, baseZ);
        double distance = getConfiguredSpawnDistance();
        Vec3 end = start.addVector(look.xCoord * distance, look.yCoord * distance, look.zCoord * distance);
        MovingObjectPosition hit = world.rayTraceBlocks(start, end, false);
        return new AimContext(hit, look, baseX, eyeY, baseZ, baseX, baseY, baseZ);
    }

    private static Vec3 getLookVector(float yaw, float pitch) {
        float yawRad = -yaw * 0.017453292F - (float) Math.PI;
        float pitchRad = -pitch * 0.017453292F;
        float cosYaw = MathHelper.cos(yawRad);
        float sinYaw = MathHelper.sin(yawRad);
        float cosPitch = -MathHelper.cos(pitchRad);
        float sinPitch = MathHelper.sin(pitchRad);
        return Vec3.createVectorHelper(sinYaw * cosPitch, sinPitch, cosYaw * cosPitch);
    }

    private static final class AimContext {
        private final MovingObjectPosition hit;
        private final Vec3 look;
        private final double eyeX;
        private final double eyeY;
        private final double eyeZ;
        private final double baseX;
        private final double baseY;
        private final double baseZ;

        private AimContext(
                MovingObjectPosition hit,
                Vec3 look,
                double eyeX,
                double eyeY,
                double eyeZ,
                double baseX,
                double baseY,
                double baseZ
        ) {
            this.hit = hit;
            this.look = look;
            this.eyeX = eyeX;
            this.eyeY = eyeY;
            this.eyeZ = eyeZ;
            this.baseX = baseX;
            this.baseY = baseY;
            this.baseZ = baseZ;
        }
    }
}
