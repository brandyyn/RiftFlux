package invmod.common.entity.night;

import invmod.common.entity.EntityIMPigEngy;
import invmod.common.nexus.INexusAccess;
import net.minecraft.world.World;

public class EntityNightIMPigEngy extends EntityIMPigEngy implements INightInvasionMob {
    public EntityNightIMPigEngy(World world) { super(world); }
    public EntityNightIMPigEngy(World world, INexusAccess nexus) { super(world, nexus); }
}
