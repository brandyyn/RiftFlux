package invmod.common.entity.night;

import invmod.common.entity.EntityIMSpider;
import invmod.common.nexus.INexusAccess;
import net.minecraft.world.World;

public class EntityNightIMSpider extends EntityIMSpider implements INightInvasionMob {
    public EntityNightIMSpider(World world) { super(world); }
    public EntityNightIMSpider(World world, INexusAccess nexus) { super(world, nexus); }
}
