package com.rocketmail.vaishnavanil.discsync.listeners;

import com.rocketmail.vaishnavanil.discsync.DiscordSync;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MinecraftListener implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        if(DiscordSync.getDiscordApi()==null)return;
        if(e.getRecipients().size()<=1 && Bukkit.getOnlinePlayers().size() > 1)return;
        Player p = e.getPlayer();


        //Channel Checks  for VentureChat
        MineverseChatPlayer chatPlayer = MineverseChatAPI.getOnlineMineverseChatPlayer(p);
        ChatChannel channel = chatPlayer.getCurrentChannel();
        if (chatPlayer.isQuickChat()) channel = chatPlayer.getQuickChannel();
        if (channel == null) return;
        if (chatPlayer.isPartyChat() && !chatPlayer.isQuickChat()) return;
        if (e.getMessage().startsWith("@")) return;
        if (chatPlayer.isMuted(channel.getName())) return;
        if (channel.hasPermission() && !chatPlayer.getPlayer().hasPermission(channel.getPermission())) return;
        if(!channel.isDefaultchannel())return;
        //Channel Checks Complete


        DiscordSync.propagateMessageThroughDiscord(ChatColor.stripColor(p.getDisplayName()),e.getMessage());
    }


    @EventHandler
    public void advancement(PlayerAdvancementDoneEvent e){
        if(e.getPlayer().hasPermission("discordsync.silent"))return;
        if (e.getAdvancement() == null || e.getAdvancement().getKey().getKey().contains("recipe/") || e.getPlayer() == null) return;

        if(DiscordSync.getInstance().getServer().getOnlinePlayers().isEmpty())return;
        for(Player online:DiscordSync.getInstance().getServer().getOnlinePlayers())if(!online.canSee(e.getPlayer()))return;

        try {
            Object craftAdvancement = ((Object) e.getAdvancement()).getClass().getMethod("getHandle").invoke(e.getAdvancement());
            Object advancementDisplay = craftAdvancement.getClass().getMethod("c").invoke(craftAdvancement);
            boolean display = (boolean) advancementDisplay.getClass().getMethod("i").invoke(advancementDisplay);
            if (!display) return;
        } catch (NullPointerException ex) {
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        String rawAdvancementName = e.getAdvancement().getKey().getKey();
        String advancementName = Arrays.stream(rawAdvancementName.substring(rawAdvancementName.lastIndexOf("/") + 1).toLowerCase().split("_"))
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
                .collect(Collectors.joining(" "));

        DiscordSync.propagateInfoThroughDiscord(DiscordSync.getConfigData().getAdvancementFormat().replaceAll("%user%",e.getPlayer().getName()).replaceAll("%message%",advancementName),"",Color.YELLOW);
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        new BukkitRunnable(){

            @Override
            public void run() {
                DiscordSync.getInstance().updateChannelDescription();
            }
        }.runTaskLater(DiscordSync.getInstance(),20);

        if(e.getPlayer().hasPermission("discordsync.silent"))return;
        float play = ((float)e.getPlayer().getStatistic((Statistic.PLAY_ONE_MINUTE))/20f)/60f/60f;
        DiscordSync.propagateInfoThroughDiscord(":ballot_box_with_check: __"+ e.getPlayer().getName() +"__ joined!","Play time: " + String.format("%.2f", play) + "h\nPlayer kills: " + e.getPlayer().getStatistic((Statistic.PLAYER_KILLS)) + "\nDeaths: " + e.getPlayer().getStatistic((Statistic.DEATHS)) + "\nK/D ratio: " + String.format("%.2f", ((float) e.getPlayer().getStatistic(Statistic.PLAYER_KILLS)) / ((float) e.getPlayer().getStatistic((Statistic.DEATHS)))) + "\nMobs killed: " + e.getPlayer().getStatistic(Statistic.MOB_KILLS),Color.GREEN);

    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        new BukkitRunnable(){

            @Override
            public void run() {
                DiscordSync.getInstance().updateChannelDescription();
            }
        }.runTaskLater(DiscordSync.getInstance(),20);
        if(e.getPlayer().hasPermission("discordsync.silent"))return;
        DiscordSync.propagateInfoThroughDiscord("",e.getPlayer().getDisplayName() + " has left the game!",Color.BLUE);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        if(e.getEntity().hasPermission("discordsync.silent"))return;
        if(e.getEntity().hasMetadata("NPC"))return;
        DiscordSync.propagateInfoThroughDiscord(DiscordSync.getConfigData().getFormatDeath().replaceAll("%message%",e.getDeathMessage()).replaceAll("%user%",e.getEntity().getName()),"",Color.RED);
    }
}
