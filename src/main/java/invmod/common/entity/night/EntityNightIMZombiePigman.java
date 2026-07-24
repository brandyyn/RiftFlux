package invmod.common.entity.night;

import invmod.common.entity.EntityIMZombiePigman;
import invmod.common.nexus.INexusAccess;
import net.minecraft.world.World;

public class EntityNightIMZombiePigman extends EntityIMZombiePigman implements INightInvasionMob {
    public EntityNightIMZombiePigman(World world) { super(world); }
    public EntityNightIMZombiePigman(World world, INexusAccess nexus) { super(world, nexus); }
}
