package zairus.worldexplorer.core.items;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import zairus.worldexplorer.core.WorldExplorer;

public class WorldExplorerItems {
    public static WEItem journal = null;
    public static WEItem needle = null;
    public static WEItem raregem = null;
    public static WEItem explorerbag = null;

    public static void init() {
        journal = new BookJournal().setCreativeTab(null);
        needle = new WEItem().setUnlocalizedName("needle").setTextureName("worldexplorer:needle").setCreativeTab(WorldExplorer.tabWorldExplorer);
        raregem = new WERareGem();
        explorerbag = new WEItem().setUnlocalizedName("bag").setTextureName("worldexplorer:bag").setCreativeTab(null);
    }

    public static void register() {
        GameRegistry.registerItem(needle, needle.getUnlocalizedName());
        GameRegistry.registerItem(raregem, raregem.getUnlocalizedName());
    }
}
