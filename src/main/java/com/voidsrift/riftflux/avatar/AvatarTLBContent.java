package com.voidsrift.riftflux.avatar;

import com.voidsrift.riftflux.avatar.appa.EntityBison;
import com.voidsrift.riftflux.avatar.appa.ItemAppaSpawnEgg;
import com.voidsrift.riftflux.avatar.appa.ModelFlyingBison;
import com.voidsrift.riftflux.avatar.appa.RenderBison;
import com.voidsrift.riftflux.avatar.appa.AppaClientEvents;
import com.voidsrift.riftflux.avatar.glider.EntityGlider;
import com.voidsrift.riftflux.avatar.glider.GliderClientEvents;
import com.voidsrift.riftflux.avatar.glider.GliderPlayerRenderHandler;
import com.voidsrift.riftflux.avatar.glider.ItemGlider;
import com.voidsrift.riftflux.avatar.glider.RenderGliderActive;
import com.voidsrift.riftflux.avatar.glider.RenderGliderInHand;
import com.voidsrift.riftflux.riftflux;
import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.init.Items;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public final class AvatarTLBContent {
    public static final ItemGlider[] GLIDERS = new ItemGlider[16];
    public static ItemAppaSpawnEgg APPA_SPAWN_EGG;

    private static final String[] GLIDER_COLOR_NAMES = {
            "white",
            "orange",
            "magenta",
            "light_blue",
            "yellow",
            "light_green",
            "pink",
            "grey",
            "light_grey",
            "cyan",
            "purple",
            "blue",
            "brown",
            "green",
            "red",
            "black"
    };

    private static final int[] DYE_TO_GLIDER = {
            15, // black
            14, // red
            13, // green
            12, // brown
            11, // blue
            10, // purple
            9,  // cyan
            8,  // light gray
            7,  // gray
            6,  // pink
            5,  // lime
            4,  // yellow
            3,  // light blue
            2,  // magenta
            1,  // orange
            0   // white
    };

    private static boolean initialized;

    private AvatarTLBContent() {
    }

    public static void preInit() {
        if (initialized) {
            return;
        }
        initialized = true;

        registerItems();
        registerEntities();
    }

    public static void initClient() {
        if (FMLCommonHandler.instance().getSide() != Side.CLIENT) {
            return;
        }

        RenderingRegistry.registerEntityRenderingHandler(EntityGlider.class, new RenderGliderActive());
        RenderingRegistry.registerEntityRenderingHandler(EntityBison.class, new RenderBison(new ModelFlyingBison(), 1.0f));

        RenderGliderInHand gliderRenderer = new RenderGliderInHand();
        for (ItemGlider glider : GLIDERS) {
            if (glider != null) {
                MinecraftForgeClient.registerItemRenderer(glider, gliderRenderer);
            }
        }

        MinecraftForge.EVENT_BUS.register(new GliderClientEvents());
        FMLCommonHandler.instance().bus().register(new GliderClientEvents());
        MinecraftForge.EVENT_BUS.register(new GliderPlayerRenderHandler());
        FMLCommonHandler.instance().bus().register(new AppaClientEvents());
    }

    private static void registerItems() {
        for (int i = 0; i < GLIDERS.length; i++) {
            String color = GLIDER_COLOR_NAMES[i];
            ItemGlider glider = new ItemGlider(i);
            glider.setUnlocalizedName("glider_" + color);
            GameRegistry.registerItem(glider, "glider_" + color);
            GLIDERS[i] = glider;
        }
        if (ModConfig.gliderDyeRecipes) {
            registerGliderDyeRecipes();
        }
        APPA_SPAWN_EGG = new ItemAppaSpawnEgg(0xEDEDDF, 14071663);
        APPA_SPAWN_EGG.setUnlocalizedName("appa_spawn_egg");
        GameRegistry.registerItem(APPA_SPAWN_EGG, "appa_spawn_egg");
    }

    private static void registerEntities() {
        int id = 50;
        EntityRegistry.registerModEntity(EntityGlider.class, "Glider", id++, riftflux.instance, 80, 1, true);
        EntityRegistry.registerModEntity(EntityBison.class, "Bison", id++, riftflux.instance, 80, 3, true);
        registerEntityEgg(EntityBison.class, 0xEDEDDF, 14071663);
    }

    private static void registerEntityEgg(Class<? extends Entity> entityClass, int primaryColor, int secondaryColor) {
        int entityId = EntityRegistry.findGlobalUniqueEntityId();
        EntityRegistry.registerGlobalEntityID(entityClass, entityClass.getSimpleName(), entityId);
        EntityList.entityEggs.put(entityId, new EntityList.EntityEggInfo(entityId, primaryColor, secondaryColor));
    }

    private static void registerGliderDyeRecipes() {
        for (ItemGlider glider : GLIDERS) {
            if (glider != null) {
                OreDictionary.registerOre("riftfluxGlider", glider);
            }
        }

        for (int dyeColor = 0; dyeColor < DYE_TO_GLIDER.length; dyeColor++) {
            int gliderColor = DYE_TO_GLIDER[dyeColor];
            ItemGlider glider = GLIDERS[gliderColor];
            if (glider == null) {
                continue;
            }
            GameRegistry.addRecipe(new ShapelessOreRecipe(
                    new ItemStack(glider, 1, 0),
                    new ItemStack(Items.dye, 1, dyeColor),
                    "riftfluxGlider"
            ));
        }
    }
}
