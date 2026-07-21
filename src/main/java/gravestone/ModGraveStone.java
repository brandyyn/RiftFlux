package gravestone;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import gravestone.config.GraveStoneConfig;
import gravestone.core.GSBlock;
import gravestone.core.GSEntity;
import gravestone.core.GSItem;
import gravestone.core.GSMessageHandler;
import gravestone.core.GSPotion;
import gravestone.core.GSRecipes;
import gravestone.core.GSStructures;
import gravestone.core.GSTabs;
import gravestone.core.GSTileEntity;
import gravestone.core.commands.GSCommands;
import gravestone.core.compatibility.GSCompatibility;
import gravestone.core.event.GSEventHandlerNetwork;
import gravestone.core.event.GSEventsHandler;
import gravestone.core.proxy.CommonProxy;
import net.minecraftforge.common.MinecraftForge;

public class ModGraveStone {
   public static ModGraveStone instance;
   public static CommonProxy proxy;

   public ModGraveStone() {
      instance = this;
   }

   public void preInit(FMLPreInitializationEvent event) {
      GraveStoneConfig.getInstance(event.getModConfigurationDirectory().getAbsolutePath() + "/GraveStoneMod/");
      GSStructures.preInit();
      GSMessageHandler.init();
   }

   public void load(FMLInitializationEvent event) {
      MinecraftForge.EVENT_BUS.register(new GSEventsHandler());
      FMLCommonHandler.instance().bus().register(new GSEventHandlerNetwork());
      proxy.registerHandlers();
      GSTabs.registration();
      GSBlock.registration();
      GSItem.registration();
      GSRecipes.registration();
      GSTileEntity.registration();
      GSStructures.getInstance();
      GSEntity.getInstance();
      GSPotion.init();
      proxy.registerRenderers();
   }

   public void postInit(FMLPostInitializationEvent event) {
      GSCompatibility.getInstance().checkMods();
      GSRecipes.registerSwordGravestoneRecipes();
   }

   public void serverStarting(FMLServerStartingEvent event) {
      GSCommands.getInstance(event);
   }
}
