package bot.listeners;

import bot.SettingType;
import bot.Settings;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class LinkControl extends ListenerAdapter {

    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(!Settings.isEnabled(SettingType.LinkControl)) return;
        if(!event.getChannel().getName().contains("promotion")) {
            String msg = event.getMessage().getContentRaw();
            if(msg.contains("discord.gg/")) {
                event.getMessage().delete().queue();
                event.getChannel().sendMessage("No advertising in channels other than <#743858602997186612> please!").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            }
        }
    }
}
