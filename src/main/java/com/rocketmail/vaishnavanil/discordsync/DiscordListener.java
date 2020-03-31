package com.rocketmail.vaishnavanil.discordsync;

import org.bukkit.configuration.file.FileConfiguration;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DiscordListener implements MessageCreateListener {

    public DiscordListener(){
        loadChannels();
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

    @Override
    public void onMessageCreate(MessageCreateEvent event) {

        if(!listeningChannels.contains(event.getMessage().getChannel()))return;   //Ignore messages not from listening channels
        Optional<User> userP = event.getMessage().getUserAuthor();
        if(!userP.isPresent())return; // If user doesnt exist Ignore
        User u = userP.get();
        if(u.isBot())return; // If user is a bot Ignore


        MinecraftOutputHandler.getInstance().sendChatMessage(event.getMessage().getReadableContent(),u.getName()); // Output to Minecraft server
        DiscordOutputHandler.getInstance().spreadMessaageToOtherListeners(event.getChannel(),event.getMessage().getReadableContent(),u);

    }


}
