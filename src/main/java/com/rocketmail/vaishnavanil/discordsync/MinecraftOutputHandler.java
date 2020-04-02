package com.rocketmail.vaishnavanil.discordsync;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;

public class MinecraftOutputHandler {
    private static MinecraftOutputHandler instance;
    String d2mFormat;
    public static MinecraftOutputHandler getInstance() {
        if(instance == null)instance = new MinecraftOutputHandler();
        return instance;
    }
    private Server server;
    public MinecraftOutputHandler(){
        server = Discordsync.getInstance().getServer();
        loadFormats();
    }
    public void sendChatMessage(String message,String user) {
        message = ChatColor.stripColor(message);
        user = ChatColor.stripColor(user);
        server.broadcastMessage(applyPlaceholders(message,user));
        Discordsync.getInstance().getServer().getConsoleSender().sendMessage((applyPlaceholders(message,user)));
    }

    public void loadFormats(){
        FileConfiguration config = Discordsync.getInstance().getConfig();
        d2mFormat = ChatColor.translateAlternateColorCodes('&',config.getString("format.Discord2Minecraft"));
    }





    public String applyPlaceholders(String msg,String user){
        return d2mFormat
                .replaceAll("(?i)%message%",msg)
                .replaceAll("(?i)%user%",user);
    }

}
