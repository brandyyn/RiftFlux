/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Ints
 *  net.minecraft.command.ICommand
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.ChatComponentText
 *  net.minecraft.util.IChatComponent
 */
package de.rinonline.korinrpg.Commands;

import com.google.common.primitives.Ints;
import de.rinonline.korinrpg.Helper.NBT.RINPlayer2;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class CommandSubtracStamina
implements ICommand {
    public int compareTo(Object arg0) {
        return 0;
    }

    public String getCommandName() {
        return "subtract Stamina";
    }

    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "subtract Stamina of a Player";
    }

    public List getCommandAliases() {
        ArrayList<String> commandAliases = new ArrayList<String>();
        commandAliases.add("subtractstamina");
        commandAliases.add("ssubtract");
        return commandAliases;
    }

    public void processCommand(ICommandSender p_71515_1_, String[] p_71515_2_) {
        if (p_71515_1_.getEntityWorld().getPlayerEntityByName(p_71515_2_[0]) instanceof EntityPlayer) {
            Integer intValue = Ints.tryParse((String)p_71515_2_[1]);
            if (intValue != null) {
                RINPlayer2.get(p_71515_1_.getEntityWorld().getPlayerEntityByName(p_71515_2_[0])).subtracttamina(intValue);
                p_71515_1_.getEntityWorld().getPlayerEntityByName(p_71515_2_[0]).addChatMessage((IChatComponent)new ChatComponentText("Subtract Stamina"));
            }
        } else {
            p_71515_1_.addChatMessage((IChatComponent)new ChatComponentText("Format Error: /subtractstamina playername amount"));
        }
    }

    public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
        return true;
    }

    public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
        return null;
    }

    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }
}

