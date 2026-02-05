package makamys.satchels;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.voidsrift.riftflux.riftflux;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import makamys.satchels.Packets.HandlerOpenContainer;
import makamys.satchels.Packets.HandlerSyncEquipment;
import makamys.satchels.Packets.MessageOpenContainer;
import makamys.satchels.Packets.MessageSyncEquipment;
import makamys.satchels.inventory.ContainerSatchels;
import makamys.satchels.proxy.SatchelsProxyClient;
import makamys.satchels.proxy.SatchelsProxyCommon;

public class Satchels
{   
    public static final String MODID = "satchels";
    public static final String VERSION = "@VERSION@";
    
    public static SatchelsProxyCommon proxy;
    
    public static SimpleNetworkWrapper networkWrapper;
    
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static void preInit(FMLPreInitializationEvent event) {
        SatchelsItems.init();
        ConfigSatchels.init();
        makamys.satchels.compat.BaublesCompat.registerSlots();
    }
    
    public static void init(FMLInitializationEvent event)
    {
        if (ConfigSatchels.enablePouchUpgradeLoot && ConfigSatchels.pouchUpgradeWeight > 0) {
            ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(SatchelsItems.pouch_upgrade), 1, 1, ConfigSatchels.pouchUpgradeWeight));
        }
        
        proxy = createProxy();
        MinecraftForge.EVENT_BUS.register(proxy);
        FMLCommonHandler.instance().bus().register(proxy);
        proxy.init();
        
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        networkWrapper.registerMessage(HandlerOpenContainer.class, MessageOpenContainer.class, 0, Side.SERVER);
        networkWrapper.registerMessage(HandlerSyncEquipment.class, MessageSyncEquipment.class, 1, Side.CLIENT);
    }
    
    public static void postInit(FMLPostInitializationEvent event) {
        SatchelsItems.postInit();
        ConfigSatchels.reparse();
        proxy.postInit();
    }

    private static SatchelsProxyCommon createProxy() {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            return new SatchelsProxyClient();
        }
        return new SatchelsProxyCommon();
    }
    
    public static void postPlayerConstructor(EntityPlayer player) {
        player.openContainer = player.inventoryContainer = new ContainerSatchels(player); 
    }

    public static void onGameTypeChanged(WorldSettings.GameType gameType, EntityPlayer player) {
        if(gameType.isCreative()) {
            ((ContainerSatchels)player.inventoryContainer).redoSlots(false);
        } else {
            ((ContainerSatchels)player.inventoryContainer).redoSlots(true);
        }
    }
}
