/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandBase
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.server.MinecraftServer
 */
package goki.stats;

import goki.stats.lib.Reference;
import goki.stats.stats.Stat;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class StatsCommand
extends CommandBase {
    public String getCommandName() {
        return "reloadGokiStats";
    }

    public void processCommand(ICommandSender icommandsender, String[] astring) {
        Reference.configuration.load();
        Stat.loadOptions(Reference.configuration);
        Stat.loadAllStatsFromConfiguration(Reference.configuration);
        if (icommandsender instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer)icommandsender;
        } else {
            MinecraftServer.getServer().logInfo("Reloaded gokiStats configuration file.");
        }
    }

    public String getCommandUsage(ICommandSender icommandsender) {
        return null;
    }

    public int compareTo(Object o) {
        return 0;
    }
}

