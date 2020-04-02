package com.rocketmail.vaishnavanil.discordsync;

import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class MinecraftListener implements Listener {


    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        if(Discordsync.getDiscordAPI() == null)return;
        Player p = e.getPlayer();

        MineverseChatPlayer chatPlayer = MineverseChatAPI.getOnlineMineverseChatPlayer(p);
        ChatChannel channel = chatPlayer.getCurrentChannel();
        if (chatPlayer.isQuickChat()) channel = chatPlayer.getQuickChannel();

        if (channel == null) return;
        if (chatPlayer.isPartyChat() && !chatPlayer.isQuickChat()) return;

        if (e.getMessage().startsWith("@")) return;

        if (chatPlayer.isMuted(channel.getName())) return;

       if (channel.hasPermission() && !chatPlayer.getPlayer().hasPermission(channel.getPermission())) return;
        if(!channel.isDefaultchannel())return;
        String user = "";
        if(p.hasPermission("discordsync.staff")){
            user = "**" + ChatColor.stripColor(p.getDisplayName()) + "**";
        }else {
            user =  ChatColor.stripColor(p.getDisplayName()) ;
        }

        DiscordOutputHandler.getInstance().sendToDiscord(e.getMessage(),user);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        new BukkitRunnable(){

            @Override
            public void run() {
                DiscordOutputHandler.getInstance().updateChannelDesc();
            }
        }.runTaskLater(Discordsync.getInstance(),20);

        if(e.getPlayer().hasPermission("discordsync.silent"))return;
        DiscordOutputHandler.getInstance().sendEventAlert(ChatColor.stripColor(e.getJoinMessage()),e.getPlayer().getDisplayName(),DiscordOutputHandler.alertJoin);
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        new BukkitRunnable(){

            @Override
            public void run() {
                DiscordOutputHandler.getInstance().updateChannelDesc();
            }
        }.runTaskLater(Discordsync.getInstance(),20);
        if(e.getPlayer().hasPermission("discordsync.silent"))return;
        DiscordOutputHandler.getInstance().sendEventAlert(ChatColor.stripColor(e.getQuitMessage()),e.getPlayer().getDisplayName(),DiscordOutputHandler.alertQuit);
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        if(e.getEntity().hasPermission("discordsync.silent"))return;
        if(e.getEntity().hasMetadata("NPC"))return;
        DiscordOutputHandler.getInstance().sendEventAlert(ChatColor.stripColor(e.getDeathMessage()),e.getEntity().getDisplayName(),DiscordOutputHandler.alertDeath);
    }



}
