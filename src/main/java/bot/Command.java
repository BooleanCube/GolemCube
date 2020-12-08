package bot;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public interface Command {
    String getCommand();
    String getHelp();
    void run(List<String> args, GuildMessageReceivedEvent event);
}
