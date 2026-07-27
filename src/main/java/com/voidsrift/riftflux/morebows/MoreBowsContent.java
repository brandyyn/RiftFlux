package com.voidsrift.riftflux.morebows;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.entity.RiftFluxEntityRegistry;
import com.voidsrift.riftflux.riftflux;
import com.voidsrift.riftflux.util.PrefixedConfiguration;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public final class MoreBowsContent {
    private static boolean preInited;

    private MoreBowsContent() {
    }

    public static boolean isEnabled() {
        return ModConfig.enableMoreBowsModule;
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (!isEnabled() || preInited) {
            return;
        }
        preInited = true;

        try {
            Class<?> moreBowsClass = Class.forName("iDiamondhunter.morebows.MoreBows");
            Object proxy = createProxy(moreBowsClass);
            seedStaticModFields(moreBowsClass, proxy);
            loadOriginalConfig(moreBowsClass, event);
            registerContent(moreBowsClass);
            registerEvents(proxy);
            if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
                registerClientRenderers(moreBowsClass);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize integrated MoreBows module", e);
        }
    }

    private static Object createProxy(Class<?> moreBowsClass) throws Exception {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            return Class.forName("iDiamondhunter.morebows.Client").newInstance();
        }
        return moreBowsClass.newInstance();
    }

    private static void seedStaticModFields(Class<?> moreBowsClass, Object proxy) throws Exception {
        Field[] fields = moreBowsClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (!Modifier.isStatic(field.getModifiers()) || !field.getType().isInstance(proxy)) {
                continue;
            }
            field.setAccessible(true);
            field.set(null, proxy);
        }
    }

    private static void loadOriginalConfig(Class<?> moreBowsClass, FMLPreInitializationEvent event) throws Exception {
        Configuration backingConfiguration = ModConfig.config;
        if (backingConfiguration == null) {
            throw new IllegalStateException("RiftFlux config is not initialized before MoreBows integration");
        }
        Configuration configuration = new PrefixedConfiguration(backingConfiguration, "morebows");
        setStaticFieldByType(moreBowsClass, Configuration.class, configuration);
        configuration.load();
        Method method = moreBowsClass.getDeclaredMethod("b");
        method.setAccessible(true);
        method.invoke(null);
    }

    @SuppressWarnings("unchecked")
    private static void registerContent(Class<?> moreBowsClass) throws Exception {
        Item diamondBow = getStaticFieldAnyName(moreBowsClass, Item.class, "var_net_minecraft_item_Item_a", "a");
        Item enderBow = getStaticFieldAnyName(moreBowsClass, Item.class, "var_net_minecraft_item_Item_b", "b");
        Item flameBow = getStaticFieldAnyName(moreBowsClass, Item.class, "var_net_minecraft_item_Item_c", "c");
        Item frostBow = getStaticFieldAnyName(moreBowsClass, Item.class, "d");
        Item goldBow = getStaticFieldAnyName(moreBowsClass, Item.class, "e");
        Item ironBow = getStaticFieldAnyName(moreBowsClass, Item.class, "f");
        Item multiBow = getStaticFieldAnyName(moreBowsClass, Item.class, "g");
        Item stoneBow = getStaticFieldAnyName(moreBowsClass, Item.class, "h");

        GameRegistry.registerItem(diamondBow, "DiamondBow");
        GameRegistry.registerItem(enderBow, "EnderBow");
        GameRegistry.registerItem(flameBow, "FlameBow");
        GameRegistry.registerItem(frostBow, "FrostBow");
        GameRegistry.registerItem(goldBow, "GoldBow");
        GameRegistry.registerItem(ironBow, "IronBow");
        GameRegistry.registerItem(multiBow, "MultiBow");
        GameRegistry.registerItem(stoneBow, "StoneBow");

        addRecipes(diamondBow, enderBow, flameBow, frostBow, goldBow, ironBow, multiBow, stoneBow);
        registerOreDictionary(diamondBow, enderBow, flameBow, frostBow, goldBow, ironBow, multiBow, stoneBow);

        RiftFluxEntityRegistry.registerModEntity(
                (Class<? extends Entity>) Class.forName("iDiamondhunter.morebows.d"),
                "MoreBowsArrowSpawner",
                riftflux.instance,
                -1,
                Integer.MAX_VALUE,
                false
        );
        RiftFluxEntityRegistry.registerModEntity(
                (Class<? extends Entity>) Class.forName("iDiamondhunter.morebows.e"),
                "MoreBowsCustomArrow",
                riftflux.instance,
                64,
                20,
                true
        );
    }

    private static void addRecipes(
            Item diamondBow,
            Item enderBow,
            Item flameBow,
            Item frostBow,
            Item goldBow,
            Item ironBow,
            Item multiBow,
            Item stoneBow
    ) {
        GameRegistry.addRecipe((IRecipe) new ShapedOreRecipe(Blocks.dispenser, new Object[]{
                "AAA", "ABA", "ACA", 'A', "cobblestone", 'B', "bow", 'C', "dustRedstone"
        }));
        GameRegistry.addRecipe((IRecipe) new ShapedOreRecipe(diamondBow, new Object[]{
                " DC", "ABC", " DC", 'C', "string", 'D', "gemDiamond", 'A', "ingotIron", 'B', "bow"
        }));
        GameRegistry.addRecipe((IRecipe) new ShapedOreRecipe(enderBow, new Object[]{
                "CD", "AB", "CD", 'C', "ingotGold", 'D', "pearlEnder", 'B', "bowIron", 'A', "pearlEnderEye"
        }));
        GameRegistry.addRecipe((IRecipe) new ShapedOreRecipe(flameBow, new Object[]{
                "CD", "AB", "CD", 'A', "ingotGold", 'D', "rodBlaze", 'B', "bowIron", 'C', "netherrack"
        }));
        GameRegistry.addRecipe((IRecipe) new ShapedOreRecipe(frostBow, new Object[]{
                " DC", "ABC", " DC", 'C', "string", 'D', "ice", 'A', "snowball", 'B', "bowIron"
        }));
        GameRegistry.addRecipe((IRecipe) new ShapedOreRecipe(goldBow, new Object[]{
                " AC", "ABC", " AC", 'C', "string", 'A', "ingotGold", 'B', "bow"
        }));
        GameRegistry.addRecipe((IRecipe) new ShapedOreRecipe(ironBow, new Object[]{
                " AC", "ABC", " AC", 'C', "string", 'A', "ingotIron", 'B', "bow"
        }));
        GameRegistry.addRecipe((IRecipe) new ShapedOreRecipe(multiBow, new Object[]{
                " DC", "A C", " DC", 'C', "string", 'A', "ingotIron", 'D', "bowIron"
        }));
        GameRegistry.addRecipe((IRecipe) new ShapedOreRecipe(stoneBow, new Object[]{
                " DC", "ABC", " DC", 'A', "stickWood", 'C', "string", 'D', "stone", 'B', Items.bow
        }));
    }

    private static void registerOreDictionary(
            Item diamondBow,
            Item enderBow,
            Item flameBow,
            Item frostBow,
            Item goldBow,
            Item ironBow,
            Item multiBow,
            Item stoneBow
    ) {
        OreDictionary.registerOre("bow", new ItemStack(diamondBow, 1, Short.MAX_VALUE));
        OreDictionary.registerOre("bow", new ItemStack(enderBow, 1, Short.MAX_VALUE));
        OreDictionary.registerOre("bow", new ItemStack(flameBow, 1, Short.MAX_VALUE));
        OreDictionary.registerOre("bow", new ItemStack(frostBow, 1, Short.MAX_VALUE));
        OreDictionary.registerOre("bow", new ItemStack(goldBow, 1, Short.MAX_VALUE));
        OreDictionary.registerOre("bow", new ItemStack(ironBow, 1, Short.MAX_VALUE));
        OreDictionary.registerOre("bow", new ItemStack(Items.bow, 1, Short.MAX_VALUE));
        OreDictionary.registerOre("bow", new ItemStack(multiBow, 1, Short.MAX_VALUE));
        OreDictionary.registerOre("bow", new ItemStack(stoneBow, 1, Short.MAX_VALUE));
        OreDictionary.registerOre("bowDiamond", new ItemStack(diamondBow, 1, Short.MAX_VALUE));
        OreDictionary.registerOre("bowGold", new ItemStack(goldBow, 1, Short.MAX_VALUE));
        OreDictionary.registerOre("bowIron", new ItemStack(ironBow, 1, Short.MAX_VALUE));
        OreDictionary.registerOre("ice", Blocks.ice);
        OreDictionary.registerOre("ice", Blocks.packed_ice);
        OreDictionary.registerOre("netherrack", Blocks.netherrack);
        OreDictionary.registerOre("pearlEnder", Items.ender_pearl);
        OreDictionary.registerOre("pearlEnderEye", Items.ender_eye);
        OreDictionary.registerOre("rodBlaze", Items.blaze_rod);
        OreDictionary.registerOre("snowball", Items.snowball);
        OreDictionary.registerOre("string", Items.string);
    }

    private static void registerEvents(Object proxy) {
        MinecraftForge.EVENT_BUS.register(proxy);
        FMLCommonHandler.instance().bus().register(proxy);
    }

    private static void registerClientRenderers(Class<?> moreBowsClass) throws Exception {
        Class<?> rendererClass = Class.forName("iDiamondhunter.morebows.c");
        Class<?> customArrowClass = Class.forName("iDiamondhunter.morebows.e");
        Object renderer = rendererClass.newInstance();
        Class<?> renderingRegistryClass = Class.forName("cpw.mods.fml.client.registry.RenderingRegistry");
        Method entityRendererMethod = findMethod(renderingRegistryClass, "registerEntityRenderingHandler", 2);
        entityRendererMethod.invoke(null, customArrowClass, renderer);

        Class<?> minecraftForgeClientClass = Class.forName("net.minecraftforge.client.MinecraftForgeClient");
        Method itemRendererMethod = findMethod(minecraftForgeClientClass, "registerItemRenderer", 2);
        Item[] bows = new Item[] {
                getStaticFieldAnyName(moreBowsClass, Item.class, "var_net_minecraft_item_Item_a", "a"),
                getStaticFieldAnyName(moreBowsClass, Item.class, "var_net_minecraft_item_Item_b", "b"),
                getStaticFieldAnyName(moreBowsClass, Item.class, "var_net_minecraft_item_Item_c", "c"),
                getStaticFieldAnyName(moreBowsClass, Item.class, "d"),
                getStaticFieldAnyName(moreBowsClass, Item.class, "e"),
                getStaticFieldAnyName(moreBowsClass, Item.class, "f"),
                getStaticFieldAnyName(moreBowsClass, Item.class, "g"),
                getStaticFieldAnyName(moreBowsClass, Item.class, "h")
        };
        for (int i = 0; i < bows.length; i++) {
            itemRendererMethod.invoke(null, bows[i], renderer);
        }
    }

    private static Method findMethod(Class<?> owner, String name, int parameterCount) {
        Method[] methods = owner.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (name.equals(method.getName()) && method.getParameterTypes().length == parameterCount) {
                return method;
            }
        }
        throw new IllegalArgumentException("Missing method " + owner.getName() + "." + name);
    }

    private static <T> T getStaticField(Class<?> owner, String name, Class<T> type) throws Exception {
        Field[] fields = owner.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (!name.equals(field.getName()) || !Modifier.isStatic(field.getModifiers()) || !type.equals(field.getType())) {
                continue;
            }
            field.setAccessible(true);
            return type.cast(field.get(null));
        }
        throw new IllegalArgumentException("Missing static field " + owner.getName() + "." + name + " of type " + type.getName());
    }

    private static <T> T getStaticFieldAnyName(Class<?> owner, Class<T> type, String... names) throws Exception {
        Exception last = null;
        for (int i = 0; i < names.length; i++) {
            try {
                return getStaticField(owner, names[i], type);
            } catch (Exception e) {
                last = e;
            }
        }
        throw new IllegalArgumentException(
                "Missing static field on " + owner.getName() + " with any of names " + java.util.Arrays.toString(names) +
                        " of type " + type.getName(),
                last
        );
    }

    private static void setStaticFieldByType(Class<?> owner, Class<?> type, Object value) throws Exception {
        Field[] fields = owner.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (!Modifier.isStatic(field.getModifiers()) || !type.equals(field.getType())) {
                continue;
            }
            field.setAccessible(true);
            field.set(null, value);
            return;
        }
        throw new IllegalArgumentException("Missing static field on " + owner.getName() + " of type " + type.getName());
    }
}
