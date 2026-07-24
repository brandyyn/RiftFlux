package invmod.common.entity.night;

import invmod.common.entity.EntityIMZombie;
import invmod.common.nexus.INexusAccess;
import net.minecraft.world.World;

public class EntityNightIMZombie extends EntityIMZombie implements INightInvasionMob {
    public EntityNightIMZombie(World world) { super(world); }
    public EntityNightIMZombie(World world, INexusAccess nexus) { super(world, nexus); }
}
