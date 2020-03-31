package com.rocketmail.vaishnavanil.discordsync;

import org.bukkit.ChatColor;
import org.bukkit.Server;

public class MinecraftOutputHandler {
    private static MinecraftOutputHandler instance;

    public static MinecraftOutputHandler getInstance() {
        if(instance == null)instance = new MinecraftOutputHandler();
        return instance;
    }
    private Server server;
    public MinecraftOutputHandler(){
        server = Discordsync.getInstance().getServer();
    }
    public void sendChatMessage(String message,String user) {

        server.broadcastMessage(ChatColor.GOLD + "[Discord]" + user+": " + message);

    }

}
