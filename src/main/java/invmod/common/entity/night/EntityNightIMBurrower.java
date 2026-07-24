package invmod.common.entity.night;

import invmod.common.entity.EntityIMBurrower;
import invmod.common.nexus.INexusAccess;
import net.minecraft.world.World;

public class EntityNightIMBurrower extends EntityIMBurrower implements INightInvasionMob {
    public EntityNightIMBurrower(World world) { super(world); }
    public EntityNightIMBurrower(World world, INexusAccess nexus) { super(world, nexus); }
}
