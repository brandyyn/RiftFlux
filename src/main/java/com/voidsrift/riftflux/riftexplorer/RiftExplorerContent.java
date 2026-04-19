package com.voidsrift.riftflux.riftexplorer;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import zairus.worldexplorer.core.items.WEItem;

public final class RiftExplorerContent {
    private static Object worldExplorer;
    private static Object equipment;
    private static Object archery;
    private static Object proxy;
    private static boolean preInited;
    private static boolean initialized;
    private static boolean postInited;

    private RiftExplorerContent() {
    }

    public static boolean isEnabled() {
        return ModConfig.enableRiftExplorerModule;
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (!isEnabled() || preInited) {
            return;
        }
        preInited = true;

        try {
            Class<?> worldExplorerClass = Class.forName("zairus.worldexplorer.core.WorldExplorer");
            Class<?> equipmentClass = Class.forName("zairus.worldexplorer.equipment.Equipment");
            Class<?> archeryClass = Class.forName("zairus.worldexplorer.archery.Archery");

            worldExplorer = worldExplorerClass.newInstance();
            equipment = equipmentClass.newInstance();
            archery = archeryClass.newInstance();
            proxy = createProxy();

            setStaticField(worldExplorerClass, "instance", worldExplorer);
            setStaticField(worldExplorerClass, "proxy", proxy);
            setStaticField(equipmentClass, "instance", equipment);
            setStaticField(archeryClass, "instance", archery);

            invokeLifecycle(equipment, "preInit", FMLPreInitializationEvent.class, event);
            invokeLifecycle(archery, "preInit", FMLPreInitializationEvent.class, event);
            invokeLifecycle(worldExplorer, "preInit", FMLPreInitializationEvent.class, event);
        } catch (Exception e) {
            throw new RuntimeException("Failed to pre-initialize integrated Rift Explorer module", e);
        }
    }

    public static void init(FMLInitializationEvent event) {
        if (!isEnabled() || !preInited || initialized) {
            return;
        }
        initialized = true;

        try {
            invokeLifecycle(equipment, "init", FMLInitializationEvent.class, event);
            invokeLifecycle(archery, "init", FMLInitializationEvent.class, event);
            invokeStaticNoArg("zairus.worldexplorer.core.items.WorldExplorerItems", "register");
            invokeLifecycle(proxy, "init", FMLInitializationEvent.class, event);
            registerCoreRecipesWithoutStudyDesk();
            registerEventHandlers();
            applyRiftFluxAdjustments();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize integrated Rift Explorer module", e);
        }
    }

    public static void postInit(FMLPostInitializationEvent event) {
        if (!isEnabled() || !initialized || postInited) {
            return;
        }
        postInited = true;

        try {
            invokeLifecycle(proxy, "postInit", FMLPostInitializationEvent.class, event);
            Object packetPipeline = getStaticField(Class.forName("zairus.worldexplorer.core.WorldExplorer"), "packetPipeline");
            invokePrivateNoArg(packetPipeline, "postInitialise");
        } catch (Exception e) {
            throw new RuntimeException("Failed to post-initialize integrated Rift Explorer module", e);
        }
    }

    private static Object createProxy() throws Exception {
        String className = FMLCommonHandler.instance().getSide() == Side.CLIENT
                ? "zairus.worldexplorer.core.ClientProxy"
                : "zairus.worldexplorer.core.ServerProxy";
        return Class.forName(className).newInstance();
    }

    private static void registerEventHandlers() throws Exception {
        Object eventHandler = Class.forName("zairus.worldexplorer.core.event.WEEventHandler").newInstance();
        FMLCommonHandler.instance().bus().register(eventHandler);
        MinecraftForge.EVENT_BUS.register(eventHandler);
        MinecraftForge.TERRAIN_GEN_BUS.register(eventHandler);
    }

    private static void applyRiftFluxAdjustments() throws Exception {
        Item journal = getStaticItem("zairus.worldexplorer.core.items.WorldExplorerItems", "journal");
        Item explorerBag = getStaticItem("zairus.worldexplorer.core.items.WorldExplorerItems", "explorerbag");
        Item quiver = getStaticItem("zairus.worldexplorer.archery.items.WEArcheryItems", "quiver");

        removeRecipesByOutputItems(journal, explorerBag, quiver);

        Block studyDesk = (Block) getStaticField(Class.forName("zairus.worldexplorer.core.block.WorldExplorerBlocks"), "studydesk");
        if (studyDesk != null) {
            studyDesk.setCreativeTab(null);
            removeRecipesByOutputItem(Item.getItemFromBlock(studyDesk));
        }

        removeRiftExplorerCreativeTab();
        registerVanillaUpgradeRecipes();
    }

    private static void registerCoreRecipesWithoutStudyDesk() throws Exception {
        // RiftFlux removes the Study Desk completely, but the needle recipe is still required for archery progression.
        Item needle = getStaticItem("zairus.worldexplorer.core.items.WorldExplorerItems", "needle");
        if (needle != null) {
            GameRegistry.addShapelessRecipe(new ItemStack(needle, 6), Blocks.cactus);
        }
    }

    private static void removeRiftExplorerCreativeTab() throws Exception {
        setStaticField(Class.forName("zairus.worldexplorer.core.WorldExplorer"), "tabWorldExplorer", null);
        removeCreativeTabFromItems("zairus.worldexplorer.core.items.WorldExplorerItems");
        removeCreativeTabFromItems("zairus.worldexplorer.archery.items.WEArcheryItems");
        removeCreativeTabFromItems("zairus.worldexplorer.equipment.items.WEEquipmentItems");
    }

    private static void removeCreativeTabFromItems(String ownerClassName) throws Exception {
        Class<?> owner = Class.forName(ownerClassName);
        Field[] fields = owner.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (!Item.class.isAssignableFrom(field.getType())) {
                continue;
            }
            field.setAccessible(true);
            Object value = field.get(null);
            if (value instanceof Item) {
                ((Item) value).setCreativeTab(null);
            }
        }
    }

    private static Item getStaticItem(String ownerClassName, String fieldName) throws Exception {
        Object value = getStaticField(Class.forName(ownerClassName), fieldName);
        return value instanceof Item ? (Item) value : null;
    }

    private static void registerVanillaUpgradeRecipes() throws Exception {
        List<WEItem> candidates = new ArrayList<WEItem>();
        collectWeItemsFromClass("zairus.worldexplorer.archery.items.WEArcheryItems", candidates);
        collectWeItemsFromClass("zairus.worldexplorer.equipment.items.WEEquipmentItems", candidates);
        for (int i = 0; i < candidates.size(); i++) {
            WEItem weItem = candidates.get(i);
            if (weItem == null || !weItem.hasImprovements()) {
                continue;
            }
            List<WEItem.Improvement> improvements = weItem.getItemImprovements();
            if (improvements == null || improvements.isEmpty()) {
                continue;
            }
            for (int j = 0; j < improvements.size(); j++) {
                WEItem.Improvement imp = improvements.get(j);
                if (imp == null || imp.material == null || imp.improvementType == null) {
                    continue;
                }
                GameRegistry.addRecipe(new RecipeRiftExplorerUpgrade(
                        weItem,
                        imp.material,
                        imp.improvementType.getKey(),
                        imp.valuePerUnit,
                        imp.improvementType.getMaxValue()
                ));
            }
        }
    }

    private static void collectWeItemsFromClass(String ownerClassName, List<WEItem> out) throws Exception {
        Class<?> owner = Class.forName(ownerClassName);
        Field[] fields = owner.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (!WEItem.class.isAssignableFrom(field.getType())) {
                continue;
            }
            field.setAccessible(true);
            Object value = field.get(null);
            if (value instanceof WEItem) {
                out.add((WEItem) value);
            }
        }
    }

    private static void removeRecipesByOutputItems(Item... items) {
        if (items == null || items.length == 0) {
            return;
        }
        List recipes = CraftingManager.getInstance().getRecipeList();
        Iterator iterator = recipes.iterator();
        while (iterator.hasNext()) {
            Object recipeObj = iterator.next();
            if (!(recipeObj instanceof IRecipe)) {
                continue;
            }
            ItemStack output = ((IRecipe) recipeObj).getRecipeOutput();
            if (output == null || output.getItem() == null) {
                continue;
            }
            Item outputItem = output.getItem();
            for (int i = 0; i < items.length; i++) {
                Item removed = items[i];
                if (removed != null && removed == outputItem) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    private static void removeRecipesByOutputItem(Item item) {
        if (item == null) {
            return;
        }
        List recipes = CraftingManager.getInstance().getRecipeList();
        Iterator iterator = recipes.iterator();
        while (iterator.hasNext()) {
            Object recipeObj = iterator.next();
            if (!(recipeObj instanceof IRecipe)) {
                continue;
            }
            ItemStack output = ((IRecipe) recipeObj).getRecipeOutput();
            if (output != null && output.getItem() == item) {
                iterator.remove();
            }
        }
    }

    private static void invokeLifecycle(Object target, String name, Class<?> eventClass, Object event) throws Exception {
        Method method = target.getClass().getMethod(name, eventClass);
        method.invoke(target, event);
    }

    private static void invokeStaticNoArg(String className, String methodName) throws Exception {
        Method method = Class.forName(className).getMethod(methodName);
        method.invoke(null);
    }

    private static void invokePrivateNoArg(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }

    private static void setStaticField(Class<?> owner, String name, Object value) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.set(null, value);
    }

    private static Object getStaticField(Class<?> owner, String name) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(null);
    }
}
