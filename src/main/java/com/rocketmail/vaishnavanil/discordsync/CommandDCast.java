package com.rocketmail.vaishnavanil.discordsync;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandDCast implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!sender.hasPermission("discordsync.dcast"))return true;
        StringBuilder message = new StringBuilder();
        for(String s:args){
            message.append(s);
            message.append(" ");
        }
        message.trimToSize();
        DiscordOutputHandler.getInstance().sendDCast(message.toString());
        return false;
    }
}
