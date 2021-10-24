package bot.listeners;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class LinkControl extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if(!event.getChannel().getName().contains("promotion")) {
            String msg = event.getMessage().getContentRaw();
            if(msg.contains("discord.gg/")) {
                event.getMessage().delete().queue();
                event.getChannel().sendMessage("No advertising in channels other than <#743858602997186612> please!").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            }
        }
    }
}