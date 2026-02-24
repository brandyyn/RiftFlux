package com.voidsrift.riftflux.blessings;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

public class CommandCurrentBlessing extends CommandBase {
    @Override
    public String getCommandName() {
        return "currentBlessing";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/currentBlessing";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) {
            sender.addChatMessage(new ChatComponentText("This command can only be used by a player."));
            return;
        }
        EntityPlayer player = (EntityPlayer) sender;
        String blessing = BlessingHelper.getBlessing(player);
        if (blessing == null) {
            sender.addChatMessage(new ChatComponentText("You don't currently have a blessing."));
            return;
        }
        String desc = BlessingHelper.getDescription(blessing);
        String msg = desc.isEmpty()
                ? "Current Blessing - Blessing of the " + blessing + "."
                : "Current Blessing - Blessing of the " + blessing + ": " + desc;
        sender.addChatMessage(new ChatComponentText(msg));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
