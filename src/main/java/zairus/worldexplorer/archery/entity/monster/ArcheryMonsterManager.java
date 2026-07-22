package zairus.worldexplorer.archery.entity.monster;

import zairus.worldexplorer.archery.Archery;
import zairus.worldexplorer.core.IWEAddonMonsterManager;
import zairus.worldexplorer.core.entity.WEEntityRegistry;

public class ArcheryMonsterManager implements IWEAddonMonsterManager {
    @Override
    public void registerMobs() {
        WEEntityRegistry.registerEntity(
                EntitySkeletonExplorer.class,
                "slingshot_skeleton",
                Archery.instance,
                64,
                1,
                true,
                0xFFFFFF,
                0x00FF00
        );
    }
}
