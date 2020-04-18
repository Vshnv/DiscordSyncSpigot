package com.rocketmail.vaishnavanil.discsync.output;

import com.rocketmail.vaishnavanil.discsync.DiscordSync;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MinecraftOutput {
    String chatFormat;
    public MinecraftOutput(String format){
        this.chatFormat = format;
    }

    public void sendMessage(String player,String message){
        String msg = chatFormat.replaceAll("%user%",player).replaceAll("%message%",message);
        Bukkit.broadcastMessage(msg);
        DiscordSync.log(msg,false);
    }

}
