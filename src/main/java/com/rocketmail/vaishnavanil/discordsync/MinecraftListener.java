package com.rocketmail.vaishnavanil.discordsync;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MinecraftListener implements Listener {


    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        if(Discordsync.getDiscordAPI() == null)return;
        Player p = e.getPlayer();
        String user = "";
        if(p.hasPermission("discordsync.staff")){
            user = "**" + ChatColor.stripColor(p.getDisplayName()) + "**";
        }else {
            user =  ChatColor.stripColor(p.getDisplayName()) ;
        }

        DiscordOutputHandler.getInstance().sendToDiscord(e.getMessage(),user);
    }

    @EventHandler
    public void onChat(PlayerJoinEvent e){
        DiscordOutputHandler.getInstance().sendEventAlert(e.getJoinMessage());
    }
    @EventHandler
    public void onChat(PlayerQuitEvent e){
        DiscordOutputHandler.getInstance().sendEventAlert(e.getQuitMessage());
    }
    @EventHandler
    public void onChat(PlayerDeathEvent e){
        DiscordOutputHandler.getInstance().sendEventAlert(e.getDeathMessage());
    }
}
