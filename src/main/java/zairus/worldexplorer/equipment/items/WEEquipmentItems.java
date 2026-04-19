package zairus.worldexplorer.equipment.items;

import zairus.worldexplorer.core.WorldExplorer;
import zairus.worldexplorer.core.items.WEItem;

public class WEEquipmentItems {
    public static WEItem spyglass = null;

    public static void init() {
        spyglass = new SpyGlass();
    }

    public static void register() {
        // Wooden Handle is intentionally removed in RiftFlux.
    }
}
