/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.Mod
 *  cpw.mods.fml.common.Mod$EventHandler
 *  cpw.mods.fml.common.SidedProxy
 *  cpw.mods.fml.common.event.FMLInitializationEvent
 *  cpw.mods.fml.common.event.FMLPostInitializationEvent
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  cpw.mods.fml.common.registry.EntityRegistry
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraftforge.common.MinecraftForge
 */
package inurosen.healaltar.common;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import inurosen.healaltar.common.core.CommonProxy;
import inurosen.healaltar.common.core.EventHandler;
import inurosen.healaltar.common.items.SoulHeart;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

public class HealingAltar {
    public static final String NAME = "Healing Altar";
    public static final String MODID = "HealingAltar";
    public static final String VERSION = "1.2.1";
    public static Item soulHeart;
    public static CommonProxy proxy;

    public void preInit(FMLPreInitializationEvent event) {
    }

    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register((Object)new EventHandler());
        soulHeart = new SoulHeart();
        GameRegistry.registerItem((Item)soulHeart, (String)"soulHeart");
    }

    public void postInit(FMLPostInitializationEvent event) {
        proxy.registerRenderers();
    }
}
