package com.rocketmail.vaishnavanil.discordsync;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;

import java.util.*;

public class DiscordOutputHandler {
    private static DiscordOutputHandler instance;
    public static DiscordOutputHandler getInstance(){
        if(instance == null) instance = new DiscordOutputHandler();
        return instance;
    }

    static String d2dFormat;
    static String m2dFormat;

    static String alertDeath,alertJoin,alertQuit;
    static String descFormat;
    public DiscordOutputHandler(){
        loadChannels();
        loadFormats();
        /*new BukkitRunnable(){

            @Override
            public void run() {
                if(listeningChannels.isEmpty())return;
                if(Discordsync.getDiscordAPI() == null){
                    this.cancel();
                    return;
                }
               for(TextChannel ch:listeningChannels){
                   int i = 0;
                   LinkedList<String> bff = buffer.get(ch.getId());
                   StringBuilder message = new StringBuilder();
                   while(!bff.isEmpty()){
                       message.append(bff.pollFirst() + "\n");
                       i++;
                       if(i>15){
                           i=0;
                           ch.sendMessage(message.toString().trim());
                           message.delete(0,message.length());
                       }
                   }
                   if(message.length()>1){
                       ch.sendMessage(message.toString().trim());
                       message.delete(0,message.length());
                   }
                   buffer.put(ch.getId(),bff);
               }
            }
        }.runTaskTimer(Discordsync.getInstance(),100,100);*/
    }
    public void loadFormats(){
        FileConfiguration config = Discordsync.getInstance().getConfig();
        d2dFormat = (config.getString("format.Discord2Discord"));
        m2dFormat = (config.getString("format.Minecraft2Discord"));
        alertDeath = (config.getString("format.alert.Dealth"));
        alertJoin = (config.getString("format.alert.Join"));
        alertQuit = (config.getString("format.alert.Quit"));
        descFormat = (config.getString("format.ChannelDesc"));
    }
    public void loadChannels(){
        listeningChannels.clear();
        FileConfiguration config = Discordsync.getInstance().getConfig();
        List<Long> chIDs = (List<Long>) config.getList("channels");
        for(long l:chIDs){
            Optional<TextChannel> c = Discordsync.getDiscordAPI().getTextChannelById(l);
            if(c.isPresent()){
                listeningChannels.add(c.get());
            }else{
                Discordsync.getInstance().getLogger().info("Could not connect to channel with ID: " + l);
            }
        }
    }

    List<TextChannel> listeningChannels = new ArrayList<>();

    public void spreadMessaageToOtherListeners(Channel from, String message, User user){
        if(listeningChannels.size() <= 1)return;

        for(TextChannel ch:listeningChannels){
            if(ch==from)continue;
            ch.sendMessage(applyPlaceholders(message,user.getName(),d2dFormat));
            //addToBuffer(ch.getId(),"[CHAT-SYNC] " + user.getName() + ": " + message);
            //buffer.get(ch.getId()).addLast("[CHAT-SYNC] " + user.getName() + ": " + message);
        }
    }

    public void sendToDiscord(String message,String user){
        for(TextChannel ch:listeningChannels){
            ch.sendMessage(applyPlaceholders(message,user,m2dFormat));
            //addToBuffer(ch.getId(),"[MINECRAFT] " + user + ": " + message);
            //buffer.get(ch.getId()).addLast("[MINECRAFT] " + user + ": " + message);
        }
    }

    public void sendEventAlert(String alert,String user, String format){
        for(TextChannel ch:listeningChannels){
            ch.sendMessage(applyPlaceholders(alert,user,format));
            //buffer.get(ch.getId()).addLast("**" + alert+"**");
        }
    }
    public void sendDCast(String dcast){
        for(TextChannel ch:listeningChannels){
            ch.sendMessage(dcast);
            //buffer.get(ch.getId()).addLast("**" + alert+"**");
        }
    }



    public String applyPlaceholders(String msg,String user,String format){
        return format
                .replaceAll("(?i)%message%",msg)
                .replaceAll("(?i)%user%",user);
    }


    public void updateChannelDesc(){
        if(listeningChannels.size() == 0)return;
        for(TextChannel ch:listeningChannels){
            Optional<ServerTextChannel> schO = ch.asServerTextChannel();
            if(!schO.isPresent())continue;
            ServerTextChannel sch = schO.get();
            sch.updateTopic(descFormat
                    .replaceAll("(?i)%playercount%", String.valueOf(Discordsync.getInstance().getServer().getOnlinePlayers().size()))
                    .replaceAll("(?i)%onlineplayers%",getOnlinePlayers()));
        }
    }
    public void topicOffline(){
        if(listeningChannels.size() == 0)return;
        for(TextChannel ch:listeningChannels){
            Optional<ServerTextChannel> schO = ch.asServerTextChannel();
            if(!schO.isPresent())continue;
            ServerTextChannel sch = schO.get();
            sch.updateTopic("Server currently offline!");
        }
    }


    private String getOnlinePlayers(){
        StringBuilder players = new StringBuilder();
        for(Player p:Discordsync.getInstance().getServer().getOnlinePlayers()){
            players.append(p.getDisplayName());
            players.append(", ");
        }
        if(players.length() >=2)players.setLength(players.length()-2);
        return players.toString();
    }
}
