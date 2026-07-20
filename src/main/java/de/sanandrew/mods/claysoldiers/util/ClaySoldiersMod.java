package de.sanandrew.mods.claysoldiers.util;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import de.sanandrew.core.manpack.mod.ModCntManPack;
import de.sanandrew.core.manpack.network.NetworkManager;
import de.sanandrew.mods.claysoldiers.dispenser.BehaviorDisruptorDispenseItem;
import de.sanandrew.mods.claysoldiers.dispenser.BehaviorSoldierDispenseItem;
import de.sanandrew.mods.claysoldiers.network.PacketManager;
import de.sanandrew.mods.claysoldiers.util.soldier.ClaymanTeam;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.SoldierEffects;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgrades;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.misc.UpgradeFood;
import de.sanandrew.mods.claysoldiers.world.gen.WorldGenerator;
import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;

/**
 * Internal compatibility holder and lifecycle for Clay Soldiers code bundled
 * as part of RiftFlux. This is deliberately not a separate FML mod.
 */
public final class ClaySoldiersMod {
    public static final String MOD_ID = "claysoldiers";
    public static final String VERSION = "2.0.0-beta.2-riftflux";
    public static final String MOD_LOG = "ClaySoldiers";
    public static final String MOD_CHANNEL = "ClaySoldiersNWCH";
    public static final EventBus EVENT_BUS = new EventBus();

    public static CommonProxy proxy;

    public static CreativeTabs clayTab;

    private static final ClaySoldiersMod EVENT_HANDLER = new ClaySoldiersMod();
    private static boolean initialized;

    private ClaySoldiersMod() {
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (!com.voidsrift.riftflux.ModConfig.enableClaySoldiersModule) {
            return;
        }

        proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT
                ? new de.sanandrew.mods.claysoldiers.client.util.ClientProxy()
                : new CommonProxy();
        ModConfig.syncConfig();
        clayTab = new CreativeTabClaySoldiers();
        RegistryItems.initialize();
        RegistryBlocks.initialize();
        ClaymanTeam.initialize();
        UpgradeFood.excludeFood((ItemFood) Items.potato);
        UpgradeFood.excludeFood((ItemFood) Items.carrot);
        GameRegistry.registerWorldGenerator((IWorldGenerator) new WorldGenerator(), 100);
        PacketManager.registerPackets();
        initialized = true;
    }

    public static void init(FMLInitializationEvent event) {
        if (!initialized) {
            return;
        }

        SoldierEffects.initialize();
        SoldierUpgrades.initialize();
        RegistryRecipes.initialize();
        FMLCommonHandler.instance().bus().register(EVENT_HANDLER);
        proxy.modInit();
        RegistryEntities.registerEntities(com.voidsrift.riftflux.riftflux.instance);

        BlockDispenser.dispenseBehaviorRegistry.putObject(
                RegistryItems.dollSoldier,
                new BehaviorSoldierDispenseItem()
        );
        BlockDispenser.dispenseBehaviorRegistry.putObject(
                RegistryItems.disruptor,
                new BehaviorDisruptorDispenseItem()
        );
        BlockDispenser.dispenseBehaviorRegistry.putObject(
                RegistryItems.disruptorHardened,
                new BehaviorDisruptorDispenseItem()
        );

        ModCntManPack.proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT
                ? new de.sanandrew.core.manpack.mod.client.ClientProxy()
                : new de.sanandrew.core.manpack.mod.CommonProxy();
        NetworkManager.initPacketHandler();
    }

    public static void postInit(FMLPostInitializationEvent event) {
        if (initialized) {
            SoldierUpgrades.logUpgradeCount();
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if ("riftflux".equals(event.modID)) {
            ModConfig.syncConfig();
        }
    }
}
