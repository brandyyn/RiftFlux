package invmod.common.entity.night;

import invmod.common.entity.EntityIMThrower;
import invmod.common.nexus.INexusAccess;
import net.minecraft.world.World;

public class EntityNightIMThrower extends EntityIMThrower implements INightInvasionMob {
    public EntityNightIMThrower(World world) { super(world); }
    public EntityNightIMThrower(World world, INexusAccess nexus) { super(world, nexus); }
}
