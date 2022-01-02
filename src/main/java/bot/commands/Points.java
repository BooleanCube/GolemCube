package bot.commands;

import bot.Command;
import bot.Constants;
import bot.Database;
import bot.Tools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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
    public void run(List<String> args, MessageReceivedEvent event) {
        Member m;
        if (args.size() >= 1)
            m = Tools.getEffectiveMember(event.getGuild(), String.join(" ", args));
        else
            m = event.getMember();

        event.getChannel().sendMessageEmbeds(
                new EmbedBuilder()
                        .setAuthor(m.getEffectiveName(), m.getUser().getAvatarUrl(), m.getUser().getEffectiveAvatarUrl())
                        .addField("Points:", String.valueOf(Database.getReputation(m)), true)
                        .addField("Rank: ", String.valueOf(Database.getReputationRank(event.getGuild(), m)), true)
                        .build()
        ).queue();
    }
}
