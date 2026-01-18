package com.voidsrift.riftflux.tweaks.ladder;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.entity.living.LivingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import com.voidsrift.riftflux.riftflux;

/**
 * Lets players extend ladders downward by right-clicking an existing ladder with a ladder item.
 */
public class FloatingLadderEvents {

    /**
     * Thickness used for the "touching ladder" test.
     * Vanilla ladder plane thickness is 2/16 = 0.125.
     * We add a tiny epsilon so grazing contact still counts.
     */
    private static final double TOUCH_THICKNESS = 0.125D;
    private static final double TOUCH_EPSILON = 0.02D;

    // --- Climbing tweak ---
    // Vanilla ladder climbing relies on the entity being "in" the ladder block space.
    // For a hanging ladder with a centered collision plane, players may be adjacent to it
    // rather than inside it. This handler adds ladder-like movement when touching it.
    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event == null || event.entityLiving == null) return;

        final EntityLivingBase ent = event.entityLiving;
        final World world = ent.worldObj;
        if (world == null) return;

        // --- New behavior: require holding jump (space) to climb upward on ladders ---
        // This avoids "climbing" just because you're inside the ladder block space.
        if (ModConfig.enableHangingLadders) {
            // Only apply to players; mob pathing expects vanilla ladder behavior.
            if (!(ent instanceof EntityPlayer)) return;
            // Enforce this only on the client. Player input (SPACE) is a client concept,
            // and trying to gate ladder motion server-side causes desync and "can't climb" bugs.
            if (!world.isRemote) return;
            if (!ent.isOnLadder()) return;

            final boolean jumping = riftflux.proxy != null && riftflux.proxy.isJumpKeyDown();

            // Always reset fall distance when on ladders.
            ent.fallDistance = 0.0F;

            // Keep the vanilla ladder slide clamp.
            if (ent.motionY < -0.15D) {
                ent.motionY = -0.15D;
            }

            // Sneak to stick in place (vanilla behavior).
            if (ent.isSneaking()) {
                if (ent.motionY < 0.0D) ent.motionY = 0.0D;
                // If they're sneaking and not jumping, we definitely don't want upward motion.
                if (!jumping && ent.motionY > 0.0D) ent.motionY = 0.0D;
                return;
            }

            if (jumping) {
                // Holding jump ascends.
                if (ent.motionY < 0.2D) {
                    ent.motionY = 0.2D;
                }
            } else {
                // Not holding jump: cancel any upward motion the vanilla ladder code may have applied.
                if (ent.motionY > 0.0D) {
                    ent.motionY = 0.0D;
                }
            }
            return;
        }

        // --- Legacy behavior: allow climbing ladders from any side by "touching" the ladder plane ---
        if (!ModConfig.enableHangingLadders) return;

        // If vanilla already thinks we're on a ladder, let vanilla handle it.
        if (ent.isOnLadder()) return;

        // Quick reject: if they're not moving vertically and on ground, avoid work.
        if (ent.motionY >= 0.0D && ent.onGround) return;

        if (!isTouchingAnyLadder(ent)) return;

        // Emulate the core ladder physics from EntityLivingBase#moveEntityWithHeading.
        ent.fallDistance = 0.0F;

        // Damp horizontal drift so it feels like you're attached to the ladder.
        ent.motionX *= 0.15D;
        ent.motionZ *= 0.15D;

        // Limit slide speed downwards.
        if (ent.motionY < -0.15D) {
            ent.motionY = -0.15D;
        }

        // Sneak to "stick" in place like vanilla ladders.
        if (ent.isSneaking() && ent.motionY < 0.0D) {
            ent.motionY = 0.0D;
            return;
        }

        // Climb by pressing forward into the ladder (mirrors how players usually climb ladders).
        // moveForward is synced to the server for players.
        if (ent.moveForward > 0.0F) {
            if (ent.motionY < 0.2D) {
                ent.motionY = 0.2D;
            }
        } else if (ent.moveForward < 0.0F) {
            // Backing away acts like descending.
            if (ent.motionY > -0.15D) {
                ent.motionY = -0.15D;
            }
        }
    }

    private boolean isTouchingAnyLadder(EntityLivingBase ent) {
        if (ent == null || ent.boundingBox == null) return false;
        final World world = ent.worldObj;
        if (world == null) return false;

        // Expand slightly so "touching" includes near-contact.
        AxisAlignedBB bb = ent.boundingBox.expand(0.01D, 0.01D, 0.01D);

        int minX = (int) Math.floor(bb.minX);
        int maxX = (int) Math.floor(bb.maxX);
        int minY = (int) Math.floor(bb.minY);
        int maxY = (int) Math.floor(bb.maxY);
        int minZ = (int) Math.floor(bb.minZ);
        int maxZ = (int) Math.floor(bb.maxZ);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block b = world.getBlock(x, y, z);
                    if (!(b instanceof BlockLadder)) continue;

                    // IMPORTANT:
                    // Only count as "touching" when intersecting the actual ladder plane area (a thin slice),
                    // NOT the entire 1x1x1 block. Using the full block is what made it feel like the whole square
                    // (edges/corners) behaved like a ladder.
                    int meta = world.getBlockMetadata(x, y, z);
                    if (isTouchingLadderPlane(bb, x, y, z, meta, b instanceof BlockFloatingLadder)) return true;
                }
            }
        }
        return false;
    }

    private boolean isTouchingLadderPlane(AxisAlignedBB entityBB, int x, int y, int z, int meta, boolean isHangingLadder) {
        // For the hanging ladder we want climb-any-side (front/back), so include the opposite face too.
        // For vanilla ladders this behavior is configurable.
        // Consolidated config: when Hanging Ladders are enabled, we treat the opposite face as climbable too.
        // This preserves the "climb from either side" behavior for both Hanging Ladders and regular ladders.
        boolean includeOpposite = true;

        AxisAlignedBB plane = ladderPlaneAabb(x, y, z, meta);
        if (plane != null && entityBB.intersectsWith(plane)) return true;

        if (includeOpposite) {
            int opp = oppositeLadderMeta(meta);
            if (opp != meta) {
                AxisAlignedBB plane2 = ladderPlaneAabb(x, y, z, opp);
                if (plane2 != null && entityBB.intersectsWith(plane2)) return true;
            }
        }
        return false;
    }

    private AxisAlignedBB ladderPlaneAabb(int x, int y, int z, int meta) {
        // BlockLadder in 1.7.10 uses metadata 2..5 for orientation.
        // Vanilla visual plane thickness is 0.125. We expand slightly to make "touching" forgiving,
        // but still only near the ladder face.
        double t = TOUCH_THICKNESS + TOUCH_EPSILON;
        if (meta == 2) {
            // Plane on the north wall (at z+1)
            return AxisAlignedBB.getBoundingBox(x, y, z + 1.0D - t, x + 1.0D, y + 1.0D, z + 1.0D);
        } else if (meta == 3) {
            // Plane on the south wall (at z)
            return AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0D, y + 1.0D, z + t);
        } else if (meta == 4) {
            // Plane on the west wall (at x+1)
            return AxisAlignedBB.getBoundingBox(x + 1.0D - t, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
        } else if (meta == 5) {
            // Plane on the east wall (at x)
            return AxisAlignedBB.getBoundingBox(x, y, z, x + t, y + 1.0D, z + 1.0D);
        }
        // Unknown meta: don't treat as ladder touch.
        return null;
    }

    private int oppositeLadderMeta(int meta) {
        // Swap the two Z faces and the two X faces.
        if (meta == 2) return 3;
        if (meta == 3) return 2;
        if (meta == 4) return 5;
        if (meta == 5) return 4;
        return meta;
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!ModConfig.enableHangingLadders) return;
        if (event.action == Action.LEFT_CLICK_BLOCK) return;

        World world = event.world;
        if (world == null || world.isRemote) return;

        EntityPlayer player = event.entityPlayer;
        if (player == null) return;
        if (ModConfig.floatingLaddersRequireSneak && !player.isSneaking()) return;

        ItemStack stack = player.getHeldItem();
        if (stack == null || stack.getItem() != Item.getItemFromBlock(Blocks.ladder)) return;

        // Ray trace to the actually targeted ladder block (matches the original snippet)
        MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, false);
        if (mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;

        int x = mop.blockX;
        int y = mop.blockY;
        int z = mop.blockZ;

        Block target = world.getBlock(x, y, z);
        if (!(target instanceof BlockLadder)) return;

        // Respect vanilla edit permissions.
        if (!player.canPlayerEdit(x, y, z, mop.sideHit, stack)) return;

        // Walk downward to find the bottom-most ladder in this column.
        int scan = 0;
        int scanY = y;
        while (scan < ModConfig.floatingLaddersMaxScan) {
            Block b = world.getBlock(x, scanY, z);
            if (!(b instanceof BlockLadder)) break;
            scanY--;
            scan++;
            if (scanY <= 0) break;
        }

        // scanY is now the first non-ladder block below the column
        if (scan <= 0) return;

        int placeY = scanY; // this is the non-ladder position
        if (!world.isAirBlock(x, placeY, z)) return;

        // Match ladder orientation by copying metadata from the bottom-most ladder block (placeY + 1)
        int meta = world.getBlockMetadata(x, placeY + 1, z);

        // Cancel the default ladder placement so we don't place a normal ladder against the wall.
        event.setCanceled(true);

        // Place our floating ladder.
        if (!world.setBlock(x, placeY, z, RiftFluxLadderContent.floatingLadderBlock, meta, 3)) {
            return;
        }

        // Consume one ladder item (unless creative).
        if (!player.capabilities.isCreativeMode) {
            stack.stackSize--;
            if (stack.stackSize <= 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }
            player.inventory.markDirty();
        }

        // Small placement sound for feedback.
        world.playSoundEffect(x + 0.5D, placeY + 0.5D, z + 0.5D,
                Blocks.ladder.stepSound.getStepResourcePath(),
                (Blocks.ladder.stepSound.getVolume() + 1.0F) / 2.0F,
                Blocks.ladder.stepSound.getPitch() * 0.8F);
    }

    // Copied from vanilla Item with the same signature as in the provided snippet.
    protected MovingObjectPosition getMovingObjectPositionFromPlayer(World world, EntityPlayer player, boolean stopOnLiquid) {
        float f = 1.0F;
        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double px = player.prevPosX + (player.posX - player.prevPosX) * (double) f;
        double py = player.prevPosY + (player.posY - player.prevPosY) * (double) f + (double) player.getEyeHeight();
        double pz = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) f;

        Vec3 vec3 = Vec3.createVectorHelper(px, py, pz);
        float f3 = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f4 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f5 = -MathHelper.cos(-pitch * 0.017453292F);
        float f6 = MathHelper.sin(-pitch * 0.017453292F);
        float dx = f4 * f5;
        float dz = f3 * f5;

        double reach = 5.0D;
        if (player instanceof EntityPlayerMP) {
            reach = ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance();
        }

        Vec3 vec31 = vec3.addVector((double) dx * reach, (double) f6 * reach, (double) dz * reach);
        return world.func_147447_a(vec3, vec31, stopOnLiquid, !stopOnLiquid, false);
    }
}
