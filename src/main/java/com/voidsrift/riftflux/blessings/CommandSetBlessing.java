package com.voidsrift.riftflux.blessings;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.net.MsgSyncBlessing;
import com.voidsrift.riftflux.net.RFNetwork;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CommandSetBlessing extends CommandBase {
    @Override
    public String getCommandName() {
        return "setBlessing";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/setBlessing <blessing|random|none>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) {
            sender.addChatMessage(new ChatComponentTranslation("blessing.riftflux.command.only_player"));
            return;
        }

        EntityPlayer player = (EntityPlayer) sender;
        if (!ModConfig.blessingsEnabled) {
            player.addChatComponentMessage(new ChatComponentTranslation("blessing.riftflux.command.set.disabled_module"));
            return;
        }

        if (args == null || args.length != 1) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        String raw = args[0] == null ? "" : args[0].trim();
        if (raw.isEmpty()) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        String normalized = raw.toLowerCase(Locale.ROOT);
        if ("none".equals(normalized) || "clear".equals(normalized)) {
            BlessingHelper.clearBlessing(player);
            BlessingHelper.clearBlessingSource(player);
            BlessingHelper.resetBlessingState(player);
            syncBlessing(player);
            player.addChatComponentMessage(new ChatComponentTranslation("blessing.riftflux.command.set.cleared"));
            return;
        }

        String blessing;
        if ("random".equals(normalized)) {
            blessing = BlessingHelper.getRandomBlessing(player.getRNG(), true);
            if (blessing == null || blessing.isEmpty()) {
                player.addChatComponentMessage(new ChatComponentTranslation("blessing.riftflux.command.set.no_available"));
                return;
            }
        } else {
            blessing = resolveBlessingName(raw);
            if (blessing == null) {
                player.addChatComponentMessage(new ChatComponentTranslation("blessing.riftflux.command.set.unknown", raw));
                return;
            }
        }

        if (!BlessingHelper.isBlessingEnabled(blessing)) {
            player.addChatComponentMessage(new ChatComponentTranslation(
                    "blessing.riftflux.command.set.disabled_blessing",
                    BlessingHelper.getDisplayName(blessing)
            ));
            return;
        }

        BlessingHelper.setBlessing(player, blessing);
        BlessingHelper.clearBlessingSource(player);
        BlessingHelper.resetBlessingState(player);
        BlessingHelper.ensurePersistedBlessing(player);
        syncBlessing(player);
        player.addChatComponentMessage(new ChatComponentTranslation(
                "blessing.riftflux.command.set.success",
                BlessingHelper.getLocalizedTitle(blessing)
        ));
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args == null || args.length != 1) {
            return null;
        }
        List<String> options = new ArrayList<String>();
        options.add("random");
        options.add("none");
        for (String blessing : BlessingHelper.BLESSINGS) {
            options.add(blessing.toLowerCase(Locale.ROOT));
        }
        return getListOfStringsMatchingLastWord(args, options.toArray(new String[0]));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    private static String resolveBlessingName(String raw) {
        if (raw == null) {
            return null;
        }
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        if ("loner".equals(normalized)) {
            normalized = "rogue";
        }
        for (String candidate : BlessingHelper.BLESSINGS) {
            if (candidate != null && normalized.equals(candidate.toLowerCase(Locale.ROOT))) {
                return candidate;
            }
        }
        return null;
    }

    private static void syncBlessing(EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP)) {
            return;
        }
        if (RFNetwork.CH == null) {
            return;
        }
        RFNetwork.CH.sendTo(new MsgSyncBlessing(player), (EntityPlayerMP) player);
    }
}
