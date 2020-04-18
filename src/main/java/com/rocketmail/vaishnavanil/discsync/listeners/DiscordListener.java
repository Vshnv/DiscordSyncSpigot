package com.rocketmail.vaishnavanil.discsync.listeners;

import com.rocketmail.vaishnavanil.discsync.DiscordSync;
import com.rocketmail.vaishnavanil.discsync.output.DiscordOutput;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class DiscordListener implements MessageCreateListener {
    long channelID;
    public DiscordListener(long channelID){
        this.channelID = channelID;
    }
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if(event.getChannel().getId() != channelID)return;
        MessageAuthor author = event.getMessageAuthor();
        if(author.isBotUser())return;
        if(author.isWebhook())return;

        DiscordSync.getMCOutput().sendMessage(author.getDisplayName(),event.getReadableMessageContent());//Sending message to minecraft

        DiscordSync.propagateMessageThroughDiscord(channelID,author.getDisplayName()+" ឵឵ ",event.getReadableMessageContent(),author.getAvatar().getUrl().toString());//Sending message to other discord channels

    }
}
