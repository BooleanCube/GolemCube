package bot.commands;

import bot.Command;
import bot.Constants;
import bot.Database;
import bot.Tools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class Points implements Command {
    @Override
    public String getCommand() {
        return "points";
    }

    @Override
    public String getHelp() {
        return "Shows the user their reputation in the server!\n" +
                "Usage: `" + Constants.PREFIX + getCommand() + " [user(optional)]`";
    }

    @Override
    public void run(List<String> args, GuildMessageReceivedEvent event) {
        if(args.size() >= 1) {
            Member m = Tools.getEffectiveMember(event.getGuild(), String.join(" ", args));
            event.getChannel().sendMessage(
                    new EmbedBuilder()
                            .setAuthor(m.getEffectiveName(), m.getUser().getAvatarUrl(), m.getUser().getEffectiveAvatarUrl())
                            .addField("Points:", String.valueOf(Database.getReputationPoints(m)), true)
                            .addField("Rank: ", String.valueOf(Database.getReputationRank(event.getGuild(), m)), true)
                            .build()
            ).queue();
        } else {
            Member m = event.getMember();
            event.getChannel().sendMessage(
                    new EmbedBuilder()
                            .setAuthor(m.getEffectiveName(), m.getUser().getAvatarUrl(), m.getUser().getEffectiveAvatarUrl())
                            .addField("Points:", String.valueOf(Database.getReputationPoints(m)), true)
                            .addField("Rank: ", String.valueOf(Database.getReputationRank(event.getGuild(), m)), true)
                            .build()
            ).queue();
        }
    }
}
