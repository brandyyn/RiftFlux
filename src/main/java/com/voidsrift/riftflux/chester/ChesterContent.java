package com.voidsrift.riftflux.chester;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.entity.RiftFluxEntityRegistry;
import com.voidsrift.riftflux.riftflux;
import com.voidsrift.riftflux.chester.client.ModelChester;
import com.voidsrift.riftflux.chester.client.RenderChester;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public final class ChesterContent {
    public static final String MODID = "chester";
    public static final int GUI_ID = 13000;

    public static Item eyeBone;
    public static Item chesterSpawnEgg;

    private static boolean preInited;
    private static boolean initialized;
    private static boolean clientInited;

    private ChesterContent() {
    }

    public static boolean isEnabled() {
        return ModConfig.enableChesterModule;
    }

    public static void preInit() {
        if (preInited) {
            return;
        }
        preInited = true;
        if (!isEnabled()) {
            return;
        }

        eyeBone = new ItemEyeBone()
                .setCreativeTab(CreativeTabs.tabMisc)
                .setTextureName(MODID + ":eyebone")
                .setUnlocalizedName("eyebone");
        chesterSpawnEgg = new ItemChesterSpawnEgg();
        GameRegistry.registerItem(eyeBone, "eyebone", MODID);
        GameRegistry.registerItem(chesterSpawnEgg, "chester_spawn_egg");

        RiftFluxEntityRegistry.registerModEntity(
                EntityChester.class,
                "Chester",
                riftflux.instance,
                80,
                1,
                true
        );
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        if (!isEnabled()) {
            return;
        }
    }

    public static void initClient() {
        if (clientInited) {
            return;
        }
        clientInited = true;
        if (!isEnabled()) {
            return;
        }

        RenderingRegistry.registerEntityRenderingHandler(
                EntityChester.class,
                new RenderChester(new ModelChester(), 1.0F)
        );
    }
}
