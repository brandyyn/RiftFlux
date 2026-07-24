package invmod.common;

import com.voidsrift.riftflux.ModConfig;
import gravestone.block.BlockGSGraveStone;
import invmod.common.entity.EntityIMBoulder;
import invmod.common.entity.EntityIMPrimedTNT;
import invmod.common.entity.night.INightInvasionMob;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.Explosion;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

/** One authority for Invasion mining and explosion block protection. */
public final class InvasionDestructionPolicy {
    private InvasionDestructionPolicy() {
    }

    public static boolean canMineBlock(Entity source, World world, int x, int y, int z) {
        if (source instanceof INightInvasionMob && !ModConfig.invasionNightMobsCanMineBlocks) {
            return false;
        }
        return !isProtectedBlock(world, x, y, z);
    }

    public static boolean canDirectlyDestroyBlock(Entity source, World world, int x, int y, int z) {
        if (source instanceof INightInvasionMob && !ModConfig.invasionNightMobsCanDirectlyDestroyBlocks) {
            return false;
        }
        return !isProtectedBlock(world, x, y, z);
    }

    private static boolean isProtectedBlock(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block instanceof BlockGSGraveStone) {
            return true;
        }

        Object registryName = Block.blockRegistry.getNameForObject(block);
        if (registryName == null || ModConfig.invasionBlockDestructionBlacklist == null) {
            return false;
        }

        String name = registryName.toString();
        for (String blockedName : ModConfig.invasionBlockDestructionBlacklist) {
            if (blockedName != null && name.equalsIgnoreCase(blockedName.trim())) {
                return true;
            }
        }
        return false;
    }

    public static void filterExplosionBlocks(World world, Explosion explosion) {
        if (!canExplosionDestroyBlocks(explosion.exploder)) {
            explosion.affectedBlockPositions.clear();
            return;
        }

        Iterator iterator = explosion.affectedBlockPositions.iterator();
        while (iterator.hasNext()) {
            ChunkPosition position = (ChunkPosition) iterator.next();
            if (isProtectedBlock(world, position.chunkPosX, position.chunkPosY, position.chunkPosZ)) {
                iterator.remove();
            }
        }
    }

    private static boolean canExplosionDestroyBlocks(Entity source) {
        if (source instanceof EntityIMBoulder) {
            if (((EntityIMBoulder) source).shootingEntity instanceof INightInvasionMob) {
                return ModConfig.invasionNightMobThrownExplosionsCanDestroyBlocks;
            }
            return true;
        }
        if (source instanceof EntityIMPrimedTNT) {
            if (((EntityIMPrimedTNT) source).shootingEntity instanceof INightInvasionMob) {
                return ModConfig.invasionNightMobThrownExplosionsCanDestroyBlocks;
            }
            return true;
        }
        if (source instanceof INightInvasionMob) {
            return ModConfig.invasionNightMobSelfExplosionsCanDestroyBlocks;
        }
        return true;
    }
}
