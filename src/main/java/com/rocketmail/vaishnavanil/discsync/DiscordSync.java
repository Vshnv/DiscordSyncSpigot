package com.rocketmail.vaishnavanil.discsync;

import com.rocketmail.vaishnavanil.discsync.listeners.MinecraftListener;
import com.rocketmail.vaishnavanil.discsync.output.DiscordOutput;
import com.rocketmail.vaishnavanil.discsync.output.MinecraftOutput;
import com.rocketmail.vaishnavanil.discsync.utils.ConfigData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;

import java.awt.*;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiscordSync extends JavaPlugin {
    private DiscordApi api;
    private ConfigData config;
    private static DiscordSync instance;
    public static DiscordSync getInstance(){
        return instance;
    }
    public static DiscordApi getDiscordApi(){
        return instance.api;
    }
    public static void disconnectBot(){
        if(instance.api==null)return;
        instance.api.disconnect();
        instance.api = null;
    }
    public static ConfigData getConfigData(){
        return instance.config;
    }
    public static void connectBot(String token){
        try {
            new DiscordApiBuilder().setToken(token).login().thenAccept(api -> {
                instance.api = api;
                api.updateActivity("Connected! Starting...");
                api.updateActivity("Initiating Listeners...");

                api.updateActivity("thewild.skaia.us");
                instance.config.addListeners();
                getInstance().getLogger().info("Discord Bot Connected!");
                System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
                //DiscordOutputHandler.getInstance().updateChannelDesc();

            });

        } catch (Exception e) {
            getInstance().getLogger().info("Unable to initiate bot!");
            e.printStackTrace();
        };
    }
    private static Logger logger;
    public static void log(String mes,boolean sync){
        if(logger==null)logger = DiscordSync.getInstance().getLogger();
        if(sync){
            new BukkitRunnable(){

                @Override
                public void run() {
                    logger.log(Level.INFO, mes);
                }
            }.runTask(DiscordSync.getInstance());
        }else {
            logger.log(Level.INFO, mes);
        }
    }

    private MinecraftOutput Mo;

    private HashMap<Long,DiscordOutput> outputMap = new HashMap<>();

    @Override
    public void onEnable(){
        instance = this;
        config = new ConfigData();
        config.setupDefault();
        config.loadConfiguration();
        Mo = new MinecraftOutput(config.getFormat2Minecraft());

        getServer().getPluginManager().registerEvents(new MinecraftListener(),this);
    }
    public void clearOutputMap(){
        outputMap.clear();
    }
    public void addDiscordOutputHandler(Long id,String hook){
        outputMap.put(id,new DiscordOutput(hook,String.valueOf(id)));
    }
    @Override
    public void onDisable(){
        disconnectBot();
    }

    public static MinecraftOutput getMCOutput(){
        return instance.Mo;
    }
    public static DiscordOutput getDiscOutput(Long ID){
        return instance.outputMap.get(ID);
    }
    public static void propagateMessageThroughDiscord(Long excludeID,String player,String message,String avatar){
        for(Long id:instance.outputMap.keySet()){
            if(id.equals(excludeID))continue;
            instance.outputMap.get(id).sendPlayerMessage(player,message,avatar);
        }
    }
    public static void propagateMessageThroughDiscord(String player,String message){
        for(DiscordOutput out:instance.outputMap.values()){
            out.sendPlayerMessage(player,message);
        }
    }
    public static void propagateInfoThroughDiscord(String title, String message, Color color){
        for(DiscordOutput out:instance.outputMap.values()){
            out.sendInfo(title,message,"https://media.discordapp.net/attachments/363682079545884672/670824484546281482/TheWild_copy.png",color);
        }
    }


    public void updateChannelDescription(){
        if(outputMap.isEmpty())return;
        for(Long chID:outputMap.keySet()){
            Optional<ServerTextChannel> schO = getDiscordApi().getServerTextChannelById(chID);
            if(!schO.isPresent())continue;
            ServerTextChannel sch = schO.get();
            sch.updateTopic(getConfigData().getChannelDescription()
                    .replaceAll("(?i)%playercount%", String.valueOf(DiscordSync.getInstance().getServer().getOnlinePlayers().size()))
                    .replaceAll("(?i)%onlineplayers%",getOnlinePlayers())
                    .replaceAll("(?i)%uniquecount%", String.valueOf(DiscordSync.getInstance().getServer().getOfflinePlayers().length)));
        }
    }
    private String getOnlinePlayers(){
        StringBuilder players = new StringBuilder();
        for(Player p:DiscordSync.getInstance().getServer().getOnlinePlayers()){
            players.append(p.getDisplayName());
            players.append(", ");
        }
        if(players.length() >=2)players.setLength(players.length()-2);
        return players.toString();
    }
}
