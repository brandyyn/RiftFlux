package gravestone.core.commands;

import gravestone.core.logger.GSLogger;
import gravestone.structures.GSStructureGenerator;
import gravestone.structures.catacombs.CatacombsGenerator;
import gravestone.structures.graves.SingleGraveGenerator;
import gravestone.structures.memorials.MemorialGenerator;
import gravestone.structures.village.VillageCemeteryGenerator;
import gravestone.structures.village.VillageUndertakerGenerator;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;

public class CommandStructuresGenerator extends CommandBase {
   public String getCommandName() {
      return "generate";
   }

   public String getCommandUsage(ICommandSender icommandsender) {
      return "/" + this.getCommandName() + " <structure name> <x coordinate> <z coordinate>";
   }

   public void processCommand(ICommandSender icommandsender, String[] commandStr) {
      GSLogger.logInfo("Structure generation command recieved");
      if (commandStr[0].equals("catacombs")) {
         generateStructure(icommandsender.getEntityWorld(), commandStr[1], commandStr[2], CatacombsGenerator.getInstance());
      } else if (commandStr[0].equals("memorial")) {
         generateStructure(icommandsender.getEntityWorld(), commandStr[1], commandStr[2], MemorialGenerator.getInstance());
      } else if (commandStr[0].equals("grave")) {
         generateStructure(icommandsender.getEntityWorld(), commandStr[1], commandStr[2], SingleGraveGenerator.getInstance());
      } else if (commandStr[0].equals("cemetery")) {
         generateStructure(icommandsender.getEntityWorld(), commandStr[1], commandStr[2], VillageCemeteryGenerator.getInstance());
      } else if (commandStr[0].equals("undertaker")) {
         generateStructure(icommandsender.getEntityWorld(), commandStr[1], commandStr[2], VillageUndertakerGenerator.getInstance());
      } else {
         GSLogger.logError("Unknown structure type");
      }

   }

   private static void generateStructure(World world, String xStr, String zStr, GSStructureGenerator structure) {
      try {
         structure.generate(world, world.rand, Integer.parseInt(xStr), Integer.parseInt(zStr), 0.0D, true);
      } catch (NumberFormatException var5) {
         GSLogger.logError("Coordinate error");
         var5.printStackTrace();
      }

   }
}
