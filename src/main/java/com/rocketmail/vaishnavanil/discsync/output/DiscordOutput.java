package com.rocketmail.vaishnavanil.discsync.output;

import com.rocketmail.vaishnavanil.discsync.utils.DiscordWebhook;
import org.bukkit.entity.Player;

import java.awt.*;
import java.io.IOException;

public class DiscordOutput {
    String hookURL;
    String chID;
    public DiscordOutput(String hookURL,String channelID){
        this.hookURL = hookURL;
        this.chID = channelID;
    }

    public void sendInfo(String title, Color color){
        DiscordWebhook webHook = new DiscordWebhook(hookURL);
        sendInfo(title,"","",color);
    }
    public void sendInfo(String title,String content, Color color){
        DiscordWebhook webHook = new DiscordWebhook(hookURL);
        sendInfo(title,content,"",color);
    }
    public void sendInfo(String title,String content, String avatar, Color color){
        DiscordWebhook webHook = new DiscordWebhook(hookURL);
        webHook.setUsername("TheWild Towny");
        DiscordWebhook.EmbedObject eo = new DiscordWebhook.EmbedObject();
        eo.setTitle(title);
        eo.setColor(color);
        if(content.contains("\n")){
            String[] kv = content.split("\n");
            for(String pair:kv){
                String[] sp = pair.split(": ");
                eo.addField(sp[0]+": ",sp[1],true);
            }
        }else{
            eo.setDescription(content);
        }
        webHook.addEmbed(eo);

        webHook.setAvatarUrl(avatar);
        try {
            webHook.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendInfoSimple(String content, String avatar){
        DiscordWebhook webHook = new DiscordWebhook(hookURL);
        webHook.setUsername("TheWild Towny");
        webHook.setContent(content);
        webHook.setAvatarUrl(avatar);
        try {
            webHook.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPlayerMessage(Player player, String message){
        DiscordWebhook webHook = new DiscordWebhook(hookURL);
        webHook.setUsername(player.getName());
        webHook.setAvatarUrl("https://mc-heads.net/avatar/"+player.getName()+"/100");
        webHook.setContent(message);
        try {
            webHook.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendPlayerMessage(String player, String message){
        DiscordWebhook webHook = new DiscordWebhook(hookURL);
        webHook.setUsername(player);
        webHook.setAvatarUrl("https://mc-heads.net/avatar/"+player+"/100");
        webHook.setContent(message);
        try {
            webHook.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendPlayerMessage(String player, String message,String avatar){
        DiscordWebhook webHook = new DiscordWebhook(hookURL);
        webHook.setUsername(player);
        webHook.setAvatarUrl(avatar);
        webHook.setContent(message);
        try {
            webHook.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
