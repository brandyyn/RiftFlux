package com.voidsrift.riftflux.chester;

import java.lang.ref.WeakReference;
import java.lang.ref.ReferenceQueue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public final class ChesterBinding {
    private static final String TAG_UUID = "ChesterUUID";
    private static final String TAG_NAME = "ChesterName";
    private static final ReferenceQueue<EntityChester> COLLECTED_CHESTERS =
            new ReferenceQueue<EntityChester>();
    private static final ConcurrentHashMap<UUID, ChesterReference> SERVER_CHESTERS =
            new ConcurrentHashMap<UUID, ChesterReference>();

    private ChesterBinding() {
    }

    public static boolean isEyeBone(ItemStack stack) {
        return stack != null && stack.getItem() == ChesterContent.eyeBone;
    }

    public static EntityChester ensureChester(ItemStack stack, EntityPlayer player) {
        if (!isEyeBone(stack) || player == null || player.worldObj.isRemote) {
            return null;
        }

        UUID boundId = getBoundId(stack);
        if (boundId != null) {
            EntityChester existing = findRegistered(player.worldObj, boundId);
            if (existing != null) {
                existing.markStaffHeldBy(player);
                updateBoundName(stack, existing.getCommandSenderName());
            }
            return existing;
        }

        EntityChester chester = new EntityChester(player.worldObj);
        placeNearPlayer(chester, player);
        chester.setTamed(true);
        chester.func_152115_b(player.getUniqueID().toString());
        chester.setHealth(chester.getMaxHealth());
        if (!player.worldObj.spawnEntityInWorld(chester)) {
            return null;
        }

        register(chester);
        bind(stack, chester);
        chester.markStaffHeldBy(player);
        player.worldObj.setEntityState(chester, (byte) 7);
        return chester;
    }

    public static boolean isBoundTo(ItemStack stack, UUID entityId) {
        UUID boundId = getBoundId(stack);
        return boundId != null && boundId.equals(entityId);
    }

    public static void register(EntityChester chester) {
        purgeCollectedChesters();
        if (chester != null && chester.worldObj != null && !chester.worldObj.isRemote) {
            UUID id = chester.getUniqueID();
            SERVER_CHESTERS.put(id, new ChesterReference(id, chester));
        }
    }

    public static void unregister(EntityChester chester) {
        if (chester == null || chester.worldObj == null || chester.worldObj.isRemote) {
            return;
        }
        ChesterReference reference = SERVER_CHESTERS.get(chester.getUniqueID());
        if (reference != null && reference.get() == chester) {
            SERVER_CHESTERS.remove(chester.getUniqueID(), reference);
        }
        purgeCollectedChesters();
    }

    public static EntityChester findBoundChester(ItemStack stack, World world) {
        UUID id = getBoundId(stack);
        return id == null ? null : findRegistered(world, id);
    }

    public static UUID getBoundChesterId(ItemStack stack) {
        return getBoundId(stack);
    }

    public static EntityChester findBoundChester(UUID id, World world) {
        return id == null ? null : findRegistered(world, id);
    }

    public static void updateGroundAnchor(ItemStack stack, World world, double x, double y, double z) {
        EntityChester chester = findBoundChester(stack, world);
        if (chester != null) {
            chester.setStaffAnchor(x, y, z);
        }
    }

    public static String getBoundName(ItemStack stack) {
        if (!isEyeBone(stack) || !stack.hasTagCompound()) {
            return null;
        }
        String name = stack.getTagCompound().getString(TAG_NAME);
        return name.isEmpty() ? null : name;
    }

    public static void updateBoundName(ItemStack stack, String name) {
        if (!isEyeBone(stack) || name == null) {
            return;
        }
        getOrCreateTag(stack).setString(TAG_NAME, name);
    }

    private static void bind(ItemStack stack, EntityChester chester) {
        NBTTagCompound tag = getOrCreateTag(stack);
        tag.setString(TAG_UUID, chester.getUniqueID().toString());
        tag.setString(TAG_NAME, chester.getCommandSenderName());
        stack.setItemDamage(0);
    }

    private static UUID getBoundId(ItemStack stack) {
        if (!isEyeBone(stack) || !stack.hasTagCompound()) {
            return null;
        }
        String value = stack.getTagCompound().getString(TAG_UUID);
        if (value.isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static EntityChester findRegistered(World world, UUID entityId) {
        purgeCollectedChesters();
        ChesterReference reference = SERVER_CHESTERS.get(entityId);
        EntityChester chester = reference == null ? null : reference.get();
        if (chester == null || chester.isDead || chester.worldObj != world) {
            if (reference != null) {
                SERVER_CHESTERS.remove(entityId, reference);
            }
            return null;
        }
        return chester;
    }

    private static void purgeCollectedChesters() {
        ChesterReference reference;
        while ((reference = (ChesterReference) COLLECTED_CHESTERS.poll()) != null) {
            SERVER_CHESTERS.remove(reference.id, reference);
        }
    }

    private static final class ChesterReference extends WeakReference<EntityChester> {
        private final UUID id;

        private ChesterReference(UUID id, EntityChester chester) {
            super(chester, COLLECTED_CHESTERS);
            this.id = id;
        }
    }

    private static NBTTagCompound getOrCreateTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    private static void placeNearPlayer(EntityChester chester, EntityPlayer player) {
        double baseAngle = Math.toRadians(player.rotationYaw + 180.0F);
        for (int attempt = 0; attempt < 12; attempt++) {
            double angle = baseAngle + attempt * Math.PI * 2.0D / 12.0D;
            double radius = 5.0D + attempt % 3;
            double x = player.posX + Math.sin(angle) * radius;
            double z = player.posZ - Math.cos(angle) * radius;
            int groundY = MathHelper.floor_double(player.posY) - 1;
            if (!player.worldObj.getBlock(
                    MathHelper.floor_double(x),
                    groundY,
                    MathHelper.floor_double(z)
            ).getMaterial().blocksMovement()) {
                continue;
            }
            chester.setLocationAndAngles(x, groundY + 1.0D, z, player.rotationYaw, 0.0F);
            if (player.worldObj.getCollidingBoundingBoxes(chester, chester.boundingBox).isEmpty()
                    && !player.worldObj.isAnyLiquid(chester.boundingBox)) {
                return;
            }
        }

        double fallbackX = player.posX + Math.sin(baseAngle) * 3.0D;
        double fallbackZ = player.posZ - Math.cos(baseAngle) * 3.0D;
        chester.setLocationAndAngles(fallbackX, player.posY, fallbackZ, player.rotationYaw, 0.0F);
    }
}
