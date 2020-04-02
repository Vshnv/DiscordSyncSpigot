package com.rocketmail.vaishnavanil.discordsync;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.util.Arrays;

public final class Discordsync extends JavaPlugin {
    private static Discordsync instance;
    public static Discordsync getInstance(){
        return instance;
    }
    public static DiscordApi getDiscordAPI(){
        return getInstance().API;
    }


    private DiscordApi API;
    @Override
    public void onEnable() {
        instance = this;
        FileConfiguration config = getConfig();
        config.addDefault("channels", Arrays.asList(1234l,5678l));
        config.addDefault("token", "ADD_TOKEN_HERE");
        config.addDefault("format.Discord2Minecraft", "[DISCORD]&c%user%: &f%message%");
        config.addDefault("format.Discord2Discord", "[SYNC]%user%: %message%");
        config.addDefault("format.Minecraft2Discord", "[MINECRAFT]%user%: %message%");
        config.addDefault("format.alert.Dealth", "[DEATH] %message%");
        config.addDefault("format.alert.Join", "[JOIN] %message%");
        config.addDefault("format.alert.Quit", "[QUIT] %message%");
        config.addDefault("format.ChannelDesc", "%playercount% Player Online. PlayerList: %onlineplayers%");
        getConfig().options().copyDefaults(true);
        saveConfig();
        getLogger().info("Initiating Discord Bot!");
        String token = getConfig().getString("token");
        try {
            new DiscordApiBuilder().setToken(token).login().thenAccept(api -> {
                this.API = api;
                api.updateActivity("Connected! Starting...");
                api.updateActivity("Initiating Listeners...");

                api.addMessageCreateListener(new DiscordListener());

                api.updateActivity("Connected!");
                getLogger().info("Discord Bot Connected!");
                System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
                DiscordOutputHandler.getInstance().updateChannelDesc();
            });

        } catch (Exception e) {
            getLogger().info("Unable to initiate bot!");
            e.printStackTrace();
        }
        getServer().getPluginManager().registerEvents(new MinecraftListener(),this);
        getServer().getPluginCommand("dcast").setExecutor(new CommandDCast());

    }

    @Override
    public void onDisable() {
        DiscordOutputHandler.getInstance().topicOffline();
        API.disconnect();
        API = null;
    }
}
