package gravestone.core.commands;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.command.CommandHandler;
import net.minecraft.server.MinecraftServer;

public class GSCommands {
   private static GSCommands instance;

   private GSCommands(FMLServerStartingEvent event) {
      instance = this;
      this.initCommands(event.getServer());
   }

   public static GSCommands getInstance(FMLServerStartingEvent event) {
      return instance == null ? new GSCommands(event) : instance;
   }

   private void initCommands(MinecraftServer server) {
      CommandHandler commandManager = (CommandHandler)server.getCommandManager();
      commandManager.registerCommand(new CommandStructuresGenerator());
      commandManager.registerCommand(new CommandCustomGraveItems());
   }
}
