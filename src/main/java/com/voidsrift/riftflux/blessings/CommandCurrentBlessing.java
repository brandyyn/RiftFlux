package com.voidsrift.riftflux.blessings;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;

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
            sender.addChatMessage(new ChatComponentTranslation("blessing.riftflux.command.only_player"));
            return;
        }
        EntityPlayer player = (EntityPlayer) sender;
        String blessing = BlessingHelper.getBlessing(player);
        if (blessing == null) {
            sender.addChatMessage(new ChatComponentTranslation("blessing.riftflux.command.none"));
            return;
        }
        String title = BlessingHelper.getLocalizedTitle(blessing);
        String desc = BlessingHelper.getDescription(blessing);
        if (desc == null || desc.isEmpty()) {
            sender.addChatMessage(new ChatComponentTranslation("blessing.riftflux.command.current.no_desc", title));
            return;
        }
        sender.addChatMessage(new ChatComponentTranslation("blessing.riftflux.command.current.with_desc", title, desc));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
