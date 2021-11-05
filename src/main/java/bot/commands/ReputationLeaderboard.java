package bot.commands;

import bot.Command;
import bot.Constants;
import bot.Database;
import bot.Tools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class ReputationLeaderboard implements Command {
    @Override
    public String getCommand() {
        return "reps";
    }

    @Override
    public String getHelp() {
        return "Shows a list of 10 server members with the highest reputation\n" +
                "Usage: `" + Constants.PREFIX + getCommand() + "`";
    }

    @Override
    public void run(List<String> args, GuildMessageReceivedEvent event) {
        if(args.isEmpty()) {
            event.getChannel().sendMessage(
                    new EmbedBuilder()
                            .setDescription(Database.getReputationLB(event.getGuild(), event.getMember()))
                            .build()
            ).queue();
        } else Tools.wrongUsage(event.getChannel(), this);
    }
}
