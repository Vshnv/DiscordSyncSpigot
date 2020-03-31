package com.rocketmail.vaishnavanil.discordsync;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;
import org.bukkit.scheduler.BukkitRunnable;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;

import java.util.*;

public class DiscordOutputHandler {
    private static DiscordOutputHandler instance;
    public static DiscordOutputHandler getInstance(){
        if(instance == null) instance = new DiscordOutputHandler();
        return instance;
    }
    public DiscordOutputHandler(){
        loadChannels();
        new BukkitRunnable(){

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
        }.runTaskTimer(Discordsync.getInstance(),100,100);
    }

    public void loadChannels(){
        listeningChannels.clear();
        FileConfiguration config = Discordsync.getInstance().getConfig();
        List<Long> chIDs = (List<Long>) config.getList("channels");
        for(long l:chIDs){
            Optional<TextChannel> c = Discordsync.getDiscordAPI().getTextChannelById(l);
            if(c.isPresent()){
                listeningChannels.add(c.get());
                if(!buffer.containsKey(c.get().getId())){
                    buffer.put(c.get().getId(),new LinkedList<>());
                }
            }else{
                Discordsync.getInstance().getLogger().info("Could not connect to channel with ID: " + l);
            }
        }
    }

    List<TextChannel> listeningChannels = new ArrayList<>();
    HashMap<Long,LinkedList<String>> buffer = new HashMap<>();

    public void spreadMessaageToOtherListeners(Channel from, String message, User user){
        if(listeningChannels.size() <= 1)return;

        for(TextChannel ch:listeningChannels){
            if(ch==from)continue;
            addToBuffer(ch.getId(),"[CHAT-SYNC] " + user.getName() + ": " + message);
            //buffer.get(ch.getId()).addLast("[CHAT-SYNC] " + user.getName() + ": " + message);
        }
    }

    public void sendToDiscord(String message,String user){
        for(TextChannel ch:listeningChannels){
            addToBuffer(ch.getId(),"[MINECRAFT] " + user + ": " + message);
            //buffer.get(ch.getId()).addLast("[MINECRAFT] " + user + ": " + message);
        }
    }

    public void sendEventAlert(String alert){
        for(TextChannel ch:listeningChannels){
            addToBuffer(ch.getId(),"**" + alert+"**");
            //buffer.get(ch.getId()).addLast("**" + alert+"**");
        }
    }

    private void addToBuffer(Long key,String msg){
        LinkedList<String> q = buffer.get(key);
        if(q==null)return;
        q.addLast(msg);
        buffer.put(key,q);
    }
}
