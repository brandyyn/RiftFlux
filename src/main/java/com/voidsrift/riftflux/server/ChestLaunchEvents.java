package com.voidsrift.riftflux.server;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public final class ChestLaunchEvents {

    // Fire once per open (player, windowId) to avoid double impulses
    private final Map<UUID, Integer> lastWinId = new HashMap<UUID, Integer>();

    @SubscribeEvent
    public void onOpen(PlayerOpenContainerEvent e) {
        if (!ModConfig.enableChestLaunch) return;

        final EntityPlayer p = e.entityPlayer;
        if (p == null) return;

        final World w = p.worldObj;
        if (w == null || w.isRemote) return; // server only

        final Container c = p.openContainer;
        if (c == null || c.inventorySlots == null) return;

        // Debounce: one launch per windowId per player
        final UUID pid = p.getUniqueID();
        final Integer last = lastWinId.get(pid);
        if (last != null && last == c.windowId) return;
        lastWinId.put(pid, c.windowId);

        // Resolve the TE backing this container (vanilla & modded)
        final TileEntity te = resolveTileFromContainer(c);
        if (te == null) return;

        // NOTE: we DO NOT skip vanilla chests anymore — everything goes through here
        final int x = te.xCoord, y = te.yCoord, z = te.zCoord;
        final Block b = w.getBlock(x, y, z);
        if (b == null || !looksLikeChest(b, te)) return;

        // Figure facing the same way for all chests
        Integer facing = resolveFacingModded(w, x, y, z, b, te);
        if (facing == null) facing = resolveFacingGeneric(w, x, y, z, b, te);
        if (facing == null) return;

        // EXACT same impulse formula you use in the mixin:
        final double H = ModConfig.chestLaunchHorizontal;
        final double U = ModConfig.chestLaunchUpward;

        final double[] dir = awayFrom(facing);

        // Only launch the opener, and only when actually standing on the chest block
        if (isStandingOn(x, y, z, p)) {
            p.addVelocity(dir[0] * H, U, dir[1] * H);
            p.velocityChanged = true;
        }
    }

    // ---------- facing resolution (modded first, then generic) ----------

    private static Integer resolveFacingModded(World w, int x, int y, int z, Block b, TileEntity te) {
        final String bc = b.getClass().getName().toLowerCase(Locale.ROOT);
        final String tc = te.getClass().getName().toLowerCase(Locale.ROOT);

        // IronChests
        if (bc.contains("ironchest") || tc.contains("ironchest")) {
            Integer f = readFacingFromNames(te,
                    new String[]{"facing","facingDirection","direction","orientation","rotation"},
                    new String[]{"getFacing","getFacingDirection","getDirection","getOrientation","getRotation"});
            if (f != null) return f;
            return readFacingFromNames(b,
                    new String[]{"facing","facingDirection","direction","orientation","rotation"},
                    new String[]{"getFacing","getFacingDirection","getDirection","getOrientation","getRotation"});
        }

        // BetterStorage
        if (bc.contains("betterstorage") || tc.contains("betterstorage")) {
            Integer f = readFacingFromNames(te,
                    new String[]{"orientation","facing","direction"},
                    new String[]{"getOrientation","getFacing","getDirection"});
            if (f != null) return f;
            return readFacingFromNames(b,
                    new String[]{"orientation","facing","direction"},
                    new String[]{"getOrientation","getFacing","getDirection"});
        }

        return null;
    }

    private static Integer resolveFacingGeneric(World w, int x, int y, int z, Block b, TileEntity te) {
        // Try block metadata in vanilla 2..5 (N,S,W,E)
        int meta = w.getBlockMetadata(x, y, z);
        if (meta >= 2 && meta <= 5) return meta;

        // Try reflection on TE then Block
        Integer f = readFacingFromNames(te,
                new String[]{"facing","facingDirection","direction","orientation","rotation"},
                new String[]{"getFacing","getFacingDirection","getDirection","getOrientation","getRotation"});
        if (f != null) return f;

        f = readFacingFromNames(b,
                new String[]{"facing","facingDirection","direction","orientation","rotation"},
                new String[]{"getFacing","getFacingDirection","getDirection","getOrientation","getRotation"});
        if (f != null) return f;

        // Fallback: some mods encode NESW as 0..3
        return mapNESW(meta & 3);
    }

    private static Integer readFacingFromNames(Object obj, String[] fields, String[] getters) {
        if (obj == null) return null;

        for (String n : fields) {
            try {
                Field f = obj.getClass().getDeclaredField(n);
                f.setAccessible(true);
                Integer v = normalizeFacingValue(f.get(obj));
                if (v != null) return v;
            } catch (Throwable ignored) {}
        }
        for (String n : getters) {
            try {
                Method m = obj.getClass().getMethod(n);
                m.setAccessible(true);
                Integer v = normalizeFacingValue(m.invoke(obj));
                if (v != null) return v;
            } catch (Throwable ignored) {}
        }
        return null;
    }

    private static Integer normalizeFacingValue(Object v) {
        if (v == null) return null;

        if (v instanceof Number) {
            int n = ((Number) v).intValue();
            if (n >= 2 && n <= 5) return n;     // vanilla mapping
            return mapNESW(n & 3);              // NESW→2..5 mapping
        }

        String type = v.getClass().getName();
        String name = v.toString().toUpperCase(Locale.ROOT);

        if ("net.minecraftforge.common.util.ForgeDirection".equals(type)) {
            try {
                Method m = v.getClass().getMethod("ordinal");
                int ord = ((Number)m.invoke(v)).intValue();
                if (ord >= 2 && ord <= 5) return ord;
                return mapNESW(ord & 3);
            } catch (Throwable ignored) {}
        }

        if (v instanceof Enum) {
            if (name.contains("NORTH")) return 2;
            if (name.contains("SOUTH")) return 3;
            if (name.contains("WEST"))  return 4;
            if (name.contains("EAST"))  return 5;
        }
        return null;
    }

    private static Integer mapNESW(int n) {
        switch (n) {
            case 0: return 2; // NORTH
            case 1: return 5; // EAST
            case 2: return 3; // SOUTH
            case 3: return 4; // WEST
            default: return null;
        }
    }

    // Direction vector away from the chest’s front (same as before)
    private static double[] awayFrom(int f) {
        switch (f) {
            case 2: return new double[]{ 0D, +1D}; // facing NORTH(−Z) → push +Z
            case 3: return new double[]{ 0D, -1D}; // facing SOUTH(+Z) → push −Z
            case 4: return new double[]{+1D,  0D}; // facing WEST (−X) → push +X
            case 5: return new double[]{-1D,  0D}; // facing EAST (+X) → push −X
            default:return new double[]{ 0D, +1D};
        }
    }

    private static boolean looksLikeChest(Block b, TileEntity te) {
        final String bu = String.valueOf(b.getUnlocalizedName()).toLowerCase(Locale.ROOT);
        final String bc = b.getClass().getName().toLowerCase(Locale.ROOT);
        final String tc = te.getClass().getName().toLowerCase(Locale.ROOT);
        return (b instanceof BlockChest)
                || bu.contains("chest") || bc.contains("chest") || tc.contains("chest")
                || bu.contains("storage") || bc.contains("storage");
    }

    private static boolean isStandingOn(int x, int y, int z, EntityPlayer e) {
        if (e == null || e.boundingBox == null) return false;
        int bx = MathHelper.floor_double(e.posX);
        int by = MathHelper.floor_double(e.boundingBox.minY - 1.0E-3D);
        int bz = MathHelper.floor_double(e.posZ);
        return (bx == x && by == y && bz == z) && (e.onGround || Math.abs(e.motionY) < 0.05D);
    }

    // ---- TE resolve helpers ----

    private static TileEntity resolveTileFromContainer(Container c) {
        // Vanilla ContainerChest fast path
        try {
            if ("ContainerChest".equals(c.getClass().getSimpleName())) {
                Field fLower = c.getClass().getDeclaredField("lowerChestInventory");
                fLower.setAccessible(true);
                Object inv = fLower.get(c);
                TileEntity te = unwrapInventoryToTile(inv);
                if (te != null) return te;
            }
        } catch (Throwable ignored) {}

        // Generic: scan fields for a TE or IInventory that wraps a TE
        try {
            for (Field f : c.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                Object v = f.get(c);
                if (v instanceof TileEntity) return (TileEntity) v;
                if (v instanceof IInventory) {
                    TileEntity te = unwrapInventoryToTile(v);
                    if (te != null) return te;
                }
            }
        } catch (Throwable ignored) {}
        return null;
    }

    private static TileEntity unwrapInventoryToTile(Object invObj) {
        if (!(invObj instanceof IInventory)) return null;
        if (invObj instanceof TileEntity) return (TileEntity) invObj;

        if (invObj instanceof InventoryLargeChest) {
            try {
                Field fLeft  = InventoryLargeChest.class.getDeclaredField("field_70477_b");
                Field fRight = InventoryLargeChest.class.getDeclaredField("field_70478_c");
                fLeft.setAccessible(true); fRight.setAccessible(true);
                Object left = fLeft.get(invObj), right = fRight.get(invObj);
                TileEntity te = unwrapInventoryToTile(left);
                if (te != null) return te;
                return unwrapInventoryToTile(right);
            } catch (Throwable ignored) {}
        }

        try {
            for (Field f : invObj.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                Object v = f.get(invObj);
                if (v instanceof TileEntity) return (TileEntity) v;
                if (v instanceof IInventory) {
                    TileEntity te = unwrapInventoryToTile(v);
                    if (te != null) return te;
                }
            }
        } catch (Throwable ignored) {}
        return null;
    }
}
