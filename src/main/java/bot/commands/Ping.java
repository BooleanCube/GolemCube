package bot.commands;

import bot.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Ping implements Command {

    @Override
    public String getCategory() {
        return "Miscellaneous";
    }

    @Override
    public CommandData getCommandData() {
        return new CommandData("ping", "Gives you the gateway and rest ping of the bot.");
    }

    @Override
    public void run(SlashCommandEvent event) {
        event.replyEmbeds(new EmbedBuilder()
                .setTitle("Pong!")
                .addField("Gateway Ping", event.getJDA().getGatewayPing() + "ms", true)
                .addField("Rest Ping", event.getJDA().getRestPing().complete() + "ms", true)
                .build()
        ).queue();
    }
}
