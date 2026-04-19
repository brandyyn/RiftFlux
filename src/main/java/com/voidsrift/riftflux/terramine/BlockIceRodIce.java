package com.voidsrift.riftflux.terramine;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class BlockIceRodIce extends Block {
    private static final Map<String, CrackState> CRACK_STATES = new HashMap<String, CrackState>();
    private static long lastPruneTick = Long.MIN_VALUE;

    @SideOnly(Side.CLIENT)
    private IIcon normalIcon;

    public BlockIceRodIce() {
        super(Material.ice);
        this.setBlockName("magic_ice_temp");
        this.setBlockTextureName("riftflux:magic_ice");
        this.setHardness(0.4F);
        this.setStepSound(soundTypeGlass);
        this.setLightOpacity(0);
        this.setCreativeTab(null);
        this.setTickRandomly(false);
        this.slipperiness = 0.98F;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.normalIcon = iconRegister.registerIcon("riftflux:magic_ice");
        this.blockIcon = this.normalIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return this.normalIcon;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        if (!world.isRemote) {
            initializeCrackState(world, x, y, z);
        }
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        if (world.isRemote) {
            return;
        }

        if (world.getBlock(x, y, z) != this) {
            removeCrackState(world, x, y, z);
            return;
        }

        CrackState state = getOrCreateCrackState(world, x, y, z);
        long now = world.getTotalWorldTime();

        if (now >= state.expireTick) {
            clearCrackProgress(world, x, y, z);
            removeCrackState(world, x, y, z);
            world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(TerrariaContent.magicIceBlock));
            world.setBlockToAir(x, y, z);
            return;
        }

        if (now < state.crackStartTick) {
            int wait = (int) Math.max(1L, state.crackStartTick - now);
            world.scheduleBlockUpdate(x, y, z, this, wait);
            return;
        }

        long crackDuration = Math.max(1L, state.expireTick - state.crackStartTick);
        long elapsed = now - state.crackStartTick;
        int progress = (int) Math.min(9L, (elapsed * 10L) / crackDuration);
        setCrackProgress(world, x, y, z, progress);
        world.scheduleBlockUpdate(x, y, z, this, 1);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        if (!world.isRemote) {
            clearCrackProgress(world, x, y, z);
            removeCrackState(world, x, y, z);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        if (ModConfig.magicIceRequireSilkTouch) {
            return null;
        }
        return Item.getItemFromBlock(TerrariaContent.magicIceBlock);
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    protected boolean canSilkHarvest() {
        return true;
    }

    @Override
    protected ItemStack createStackedBlock(int meta) {
        return new ItemStack(TerrariaContent.magicIceBlock, 1, 0);
    }

    private static void initializeCrackState(World world, int x, int y, int z) {
        pruneExpiredStates(world);
        clearCrackProgress(world, x, y, z);
        long now = world.getTotalWorldTime();
        int total = TerrariaContent.getIceRodLifetimeTicks();
        int crackDuration = Math.max(1, total / 3);
        int crackStartDelay = Math.max(1, total - crackDuration);
        CrackState state = new CrackState(now + crackStartDelay, now + total);
        CRACK_STATES.put(getStateKey(world, x, y, z), state);
        world.scheduleBlockUpdate(x, y, z, world.getBlock(x, y, z), crackStartDelay);
    }

    private static CrackState getOrCreateCrackState(World world, int x, int y, int z) {
        pruneExpiredStates(world);
        String key = getStateKey(world, x, y, z);
        CrackState state = CRACK_STATES.get(key);
        if (state == null) {
            long now = world.getTotalWorldTime();
            int total = TerrariaContent.getIceRodLifetimeTicks();
            int crackDuration = Math.max(1, total / 3);
            int crackStartDelay = Math.max(1, total - crackDuration);
            state = new CrackState(now + crackStartDelay, now + total);
            CRACK_STATES.put(key, state);
        }
        return state;
    }

    private static void removeCrackState(World world, int x, int y, int z) {
        CRACK_STATES.remove(getStateKey(world, x, y, z));
    }

    public static void clearCrackStatesForWorld(World world) {
        if (world == null || world.provider == null) {
            return;
        }
        String prefix = world.provider.dimensionId + ":";
        Iterator<Map.Entry<String, CrackState>> iterator = CRACK_STATES.entrySet().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getKey().startsWith(prefix)) {
                iterator.remove();
            }
        }
    }

    private static void pruneExpiredStates(World world) {
        if (world == null || world.provider == null || CRACK_STATES.isEmpty()) {
            return;
        }
        long now = world.getTotalWorldTime();
        if (lastPruneTick != Long.MIN_VALUE && now >= lastPruneTick && now - lastPruneTick < 200L) {
            return;
        }
        lastPruneTick = now;
        String prefix = world.provider.dimensionId + ":";
        Iterator<Map.Entry<String, CrackState>> iterator = CRACK_STATES.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, CrackState> entry = iterator.next();
            if (entry.getKey().startsWith(prefix) && entry.getValue().expireTick + 200L < now) {
                iterator.remove();
            }
        }
    }

    private static void setCrackProgress(World world, int x, int y, int z, int progress) {
        world.destroyBlockInWorldPartially(getCrackId(x, y, z), x, y, z, progress);
    }

    private static void clearCrackProgress(World world, int x, int y, int z) {
        world.destroyBlockInWorldPartially(getCrackId(x, y, z), x, y, z, -1);
    }

    private static int getCrackId(int x, int y, int z) {
        int h = 0x5F000000;
        h ^= (x * 73428767);
        h ^= (y * 912931);
        h ^= (z * 4382897);
        return h;
    }

    private static String getStateKey(World world, int x, int y, int z) {
        int dim = world.provider != null ? world.provider.dimensionId : 0;
        return dim + ":" + x + ":" + y + ":" + z;
    }

    private static final class CrackState {
        private final long crackStartTick;
        private final long expireTick;

        private CrackState(long crackStartTick, long expireTick) {
            this.crackStartTick = crackStartTick;
            this.expireTick = expireTick;
        }
    }
}
