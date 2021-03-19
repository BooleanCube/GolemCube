package bot.commands;

import bot.Command;
import bot.Constants;
import bot.Tools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class Ping implements Command {

    @Override
    public String getCommand() {
        return "ping";
    }

    @Override
    public String getHelp() {
        return "Gives you the gateway and rest ping of the bot.\n" +
                "Usage: `" + Constants.PREFIX + getCommand() + "`";
    }

    @Override
    public void run(List<String> args, GuildMessageReceivedEvent event) {
        if (args.isEmpty()) {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setTitle("Ping!")
                    .addField("Gateway Ping", event.getJDA().getGatewayPing() + "ms", true)
                    .addField("Rest Ping", event.getJDA().getRestPing().complete() + "ms", true)
                    .build()
            ).queue();
        } else {
            Tools.wrongUsage(event.getChannel(), this);
        }
    }

}
