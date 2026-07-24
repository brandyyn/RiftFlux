package invmod.common.entity.night;

import invmod.common.entity.EntityIMSkeleton;
import invmod.common.nexus.INexusAccess;
import net.minecraft.world.World;

public class EntityNightIMSkeleton extends EntityIMSkeleton implements INightInvasionMob {
    public EntityNightIMSkeleton(World world) { super(world); }
    public EntityNightIMSkeleton(World world, INexusAccess nexus) { super(world, nexus); }
}
