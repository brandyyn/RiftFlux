package invmod.common.entity.night;

import com.voidsrift.riftflux.ModConfig;
import invmod.common.entity.EntityIMCreeper;
import invmod.common.mod_Invasion;
import invmod.common.nexus.INexusAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityNightIMCreeper extends EntityIMCreeper implements INightInvasionMob {
    public EntityNightIMCreeper(World world) { super(world); }
    public EntityNightIMCreeper(World world, INexusAccess nexus) { super(world, nexus); }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        super.dropFewItems(recentlyHit, looting);
        if (ModConfig.invasionNightMobsDropSmallRemnants && this.rand.nextInt(4) == 0) {
            entityDropItem(new ItemStack(mod_Invasion.itemSmallRemnants, 1), 0.0F);
        }
    }
}
