package invmod.common.nexus;

import invmod.common.mod_Invasion;
import invmod.common.entity.EntityIMBurrower;
import invmod.common.entity.EntityIMCreeper;
import invmod.common.entity.EntityIMImp;
import invmod.common.entity.EntityIMLiving;
import invmod.common.entity.EntityIMPigEngy;
import invmod.common.entity.EntityIMSkeleton;
import invmod.common.entity.EntityIMSpider;
import invmod.common.entity.EntityIMThrower;
import invmod.common.entity.EntityIMZombie;
import invmod.common.entity.EntityIMZombiePigman;
import invmod.common.entity.night.EntityNightIMBurrower;
import invmod.common.entity.night.EntityNightIMCreeper;
import invmod.common.entity.night.EntityNightIMImp;
import invmod.common.entity.night.EntityNightIMPigEngy;
import invmod.common.entity.night.EntityNightIMSkeleton;
import invmod.common.entity.night.EntityNightIMSpider;
import invmod.common.entity.night.EntityNightIMThrower;
import invmod.common.entity.night.EntityNightIMZombie;
import invmod.common.entity.night.EntityNightIMZombiePigman;
import net.minecraft.world.World;

public class MobBuilder 
{
	public EntityIMLiving createMobFromConstruct(EntityConstruct mobConstruct, World world, INexusAccess nexus)
	{
		return createMobFromConstruct(mobConstruct, world, nexus, false);
	}

	public EntityIMLiving createNightMobFromConstruct(EntityConstruct mobConstruct, World world)
	{
		return createMobFromConstruct(mobConstruct, world, null, true);
	}

	private EntityIMLiving createMobFromConstruct(EntityConstruct mobConstruct, World world, INexusAccess nexus, boolean nightMob)
  {
    EntityIMLiving mob = null;
    switch (mobConstruct.getMobType())
    {
    case ZOMBIE:
      EntityIMZombie zombie = nightMob ? new EntityNightIMZombie(world, nexus) : new EntityIMZombie(world, nexus);
      zombie.setTexture(mobConstruct.getTexture());
      zombie.setFlavour(mobConstruct.getFlavour());
      zombie.setTier(mobConstruct.getTier());
      mob = zombie;
      break;
    case ZOMBIEPIGMAN:
        EntityIMZombiePigman zombiePigman = nightMob ? new EntityNightIMZombiePigman(world, nexus) : new EntityIMZombiePigman(world, nexus);
        zombiePigman.setTexture(mobConstruct.getTexture());
        zombiePigman.setTier(mobConstruct.getTier());
        mob = zombiePigman;
        break;
    case SPIDER:
      EntityIMSpider spider = nightMob ? new EntityNightIMSpider(world, nexus) : new EntityIMSpider(world, nexus);
      spider.setTexture(mobConstruct.getTexture());
      spider.setFlavour(mobConstruct.getFlavour());
      spider.setTier(mobConstruct.getTier());
      mob = spider;
      break;
    case SKELETON:
      EntityIMSkeleton skeleton = nightMob ? new EntityNightIMSkeleton(world, nexus) : new EntityIMSkeleton(world, nexus);
      mob = skeleton;
      break;
    case PIG_ENGINEER:
      EntityIMPigEngy pigEngy = nightMob ? new EntityNightIMPigEngy(world, nexus) : new EntityIMPigEngy(world, nexus);
      mob = pigEngy;
      break;
    case THROWER:
      EntityIMThrower thrower = nightMob ? new EntityNightIMThrower(world, nexus) : new EntityIMThrower(world, nexus);
      thrower.setTexture(mobConstruct.getTier());
      thrower.setTier(mobConstruct.getTier());
      mob = thrower;
      break;
    case BURROWER:
      EntityIMBurrower burrower = nightMob ? new EntityNightIMBurrower(world, nexus) : new EntityIMBurrower(world, nexus);
      mob = burrower;
      break;
    case CREEPER:
      EntityIMCreeper creeper = nightMob ? new EntityNightIMCreeper(world, nexus) : new EntityIMCreeper(world, nexus);
      mob = creeper;
      break;
      
    case IMP:
        EntityIMImp imp = nightMob ? new EntityNightIMImp(world, nexus) : new EntityIMImp(world, nexus);
        mob = imp;
        break;
    default:
      mod_Invasion.log("Missing mob type in MobBuilder: " + mobConstruct.getMobType());
      mob = null;
    }
    
    return mob;
  }
}
