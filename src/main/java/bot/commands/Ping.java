package bot.commands;

import bot.Command;
import bot.Main;
import bot.Tools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class Ping implements Command {
    @Override
    public String getCommand() {
        return "ping";
    }

    @Override
    public String getHelp() {
        return "Gives you the gateway and rest ping of the bot.\n" +
                "Usage: `" + Main.getPrefix() + getCommand() + "`";
    }

    @Override
    public void run(List<String> args, MessageReceivedEvent event) {
        if (args.isEmpty()) {
            event.getChannel().sendMessageEmbeds(new EmbedBuilder()
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
