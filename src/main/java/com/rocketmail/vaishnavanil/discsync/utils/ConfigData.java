package com.rocketmail.vaishnavanil.discsync.utils;

import com.rocketmail.vaishnavanil.discsync.DiscordSync;
import com.rocketmail.vaishnavanil.discsync.listeners.DiscordListener;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;

public class ConfigData {
    private String token;
    private String formatAdv, formatChDesc, formatDeath, format2Minecraft;
    private HashMap<String,String> discordChannelData = new HashMap<>();
    public void loadConfiguration(){
        FileConfiguration config = DiscordSync.getInstance().getConfig();
        token = config.getString("Token");
        formatAdv = config.getString("Format.Advancement");
        formatChDesc = config.getString("Format.ChannelDescription");
        formatDeath = config.getString("Format.DeathAlert");
        format2Minecraft = ChatColor.translateAlternateColorCodes('&',config.getString("Format.ToMinecraft"));
        for(String channel:config.getConfigurationSection("Channels").getKeys(false)){
            String hook = config.getString("Channels."+channel);
            if(channel.equalsIgnoreCase("1234")||channel.equalsIgnoreCase("5678"))continue;
            discordChannelData.put(channel,hook);
        }

        updateListeners();
        updateOutput();
    }

    private void updateOutput() {
        DiscordSync.getInstance().clearOutputMap();
        for(String key:discordChannelData.keySet()){
            String hook = discordChannelData.get(key);
            DiscordSync.getInstance().addDiscordOutputHandler(Long.valueOf(key),hook);
        }
    }

    private void updateListeners(){
        DiscordSync.disconnectBot();
        DiscordSync.connectBot(token);
    }

    public void setupDefault(){
        FileConfiguration config = DiscordSync.getInstance().getConfig();
        config.addDefault("Token","token here");
        config.addDefault("Format.Advancement",":trophy: *%user%* has gained the advancement __%message%__!");
        config.addDefault("Format.ChannelDescription","%playercount% Player Online. PlayerList: %onlineplayers%");
        config.addDefault("Format.DeathAlert",":skull: %message%");
        config.addDefault("Format.ToMinecraft","[DISCORD]&c%user%: &f%message%");
        config.addDefault("Channels.1234","http://HOOKLINK.link");
        config.addDefault("Channels.5678","http://HOOKLINK.link");
        config.options().copyDefaults(true);
        DiscordSync.getInstance().saveConfig();
    }


    public String getAdvancementFormat(){
        return formatAdv;
    }

    public String getChannelDescription(){
        return formatChDesc;
    }

    public String getFormatDeath(){
        return formatDeath;
    }

    public String getFormat2Minecraft(){
        return format2Minecraft;
    }

    public void addListeners() {
        for(String key:discordChannelData.keySet()){
            DiscordSync.getDiscordApi().addMessageCreateListener(new DiscordListener(Long.valueOf(key)));
        }
    }
}
