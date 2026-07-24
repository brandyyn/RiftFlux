package invmod.common.entity.night;

import invmod.common.entity.EntityIMImp;
import invmod.common.nexus.INexusAccess;
import net.minecraft.world.World;

public class EntityNightIMImp extends EntityIMImp implements INightInvasionMob {
    public EntityNightIMImp(World world) { super(world); }
    public EntityNightIMImp(World world, INexusAccess nexus) { super(world, nexus); }
}
