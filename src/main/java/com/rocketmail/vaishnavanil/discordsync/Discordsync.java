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
            });

        } catch (Exception e) {
            getLogger().info("Unable to initiate bot!");
            e.printStackTrace();
        }
        getServer().getPluginManager().registerEvents(new MinecraftListener(),this);

    }

    @Override
    public void onDisable() {
        API.disconnect();
        API = null;
    }
}
