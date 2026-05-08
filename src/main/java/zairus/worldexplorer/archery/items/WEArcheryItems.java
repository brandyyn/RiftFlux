package zairus.worldexplorer.archery.items;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import zairus.worldexplorer.core.WorldExplorer;
import zairus.worldexplorer.core.items.WEItem;

public class WEArcheryItems {
    public static WEItem slingshot = null;
    public static WEItem boomerang = null;
    public static WEItem longbow = null;
    public static WEItem blowpipe = null;
    public static WEItem quiver = null;
    public static WEItem dart = null;
    public static WEItem specialarrow = null;
    public static WEItem pebble = null;
    public static WEItem captured_ender_chest = null;
    public static WEItem longbow_handle = null;
    public static WEItem longbow_string = null;
    public static Item slingshot_skeleton_spawn_egg = null;

    public static void init() {
        dart = new Dart();
        specialarrow = new SpecialArrow();
        pebble = new WEItem().setUnlocalizedName("pebble").setTextureName("worldexplorer:pebble").setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabCombat).setMaxStackSize(16);
        captured_ender_chest = new CapturedEnderChestItem();
        quiver = new Quiver().setCreativeTab(null);
        longbow_handle = new WEItem().setUnlocalizedName("longbow_handle").setTextureName("worldexplorer:longbow_handle_standby").setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabMaterials);
        longbow_string = new WEItem().setUnlocalizedName("longbow_string").setTextureName("worldexplorer:longbow_string_pulling_0").setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabMaterials);
        slingshot_skeleton_spawn_egg = new ItemSlingshotSkeletonSpawnEgg();
        slingshot = new Slingshot();
        boomerang = new Boomerang();
        longbow = new LongBow();
        blowpipe = new BlowPipe();
    }

    public static void register() {
        GameRegistry.registerItem(pebble, pebble.getUnlocalizedName());
        GameRegistry.registerItem(captured_ender_chest, captured_ender_chest.getUnlocalizedName());
        GameRegistry.registerItem(dart, dart.getUnlocalizedName());
        GameRegistry.registerItem(specialarrow, specialarrow.getUnlocalizedName());
        GameRegistry.registerItem(longbow_handle, longbow_handle.getUnlocalizedName());
        GameRegistry.registerItem(longbow_string, longbow_string.getUnlocalizedName());
        GameRegistry.registerItem(slingshot_skeleton_spawn_egg, "slingshot_skeleton_spawn_egg");
        GameRegistry.registerItem(slingshot, slingshot.getUnlocalizedName());
        GameRegistry.registerItem(boomerang, boomerang.getUnlocalizedName());
        GameRegistry.registerItem(longbow, longbow.getUnlocalizedName());
        GameRegistry.registerItem(blowpipe, blowpipe.getUnlocalizedName());

        ChestGenHooks.addItem("villageBlacksmith", new WeightedRandomChestContent(new ItemStack(slingshot), 1, 2, 5));
        ChestGenHooks.addItem("pyramidDesertyChest", new WeightedRandomChestContent(new ItemStack(slingshot), 1, 2, 5));
        ChestGenHooks.addItem("pyramidJungleChest", new WeightedRandomChestContent(new ItemStack(slingshot), 1, 2, 5));
        ChestGenHooks.addItem("pyramidDesertyChest", new WeightedRandomChestContent(new ItemStack(boomerang), 1, 1, 2));
        ChestGenHooks.addItem("pyramidJungleChest", new WeightedRandomChestContent(new ItemStack(boomerang), 1, 1, 2));
        ChestGenHooks.addItem("dungeonChest", new WeightedRandomChestContent(new ItemStack(boomerang), 1, 1, 2));
        ChestGenHooks.addItem("strongholdCorridor", new WeightedRandomChestContent(new ItemStack(boomerang), 1, 1, 2));

        if (ModConfig.riftExplorerPebblesDropFromGrass && SlingshotAmmoHelper.isPebbleAllowed()) {
            MinecraftForge.addGrassSeed(new ItemStack(pebble), 4);
        }
        MinecraftForge.addGrassSeed(new ItemStack(Items.stick), 4);
    }
}
