package bot.commands;

import bot.Command;
import bot.database.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class Points implements Command {

    @Override
    public String getCategory() {
        return "Reputation";
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("points", "Shows the user their reputation in the server.")
                .addOption(OptionType.USER, "user", "The user whose points you want.");
    }

    @Override
    public void run(SlashCommandInteractionEvent event) {
        Member m = event.getOption("user", event.getMember(), OptionMapping::getAsMember);

        int reputation = Database.getReputation(m);
        int reputationRank = Database.getReputationRank(m);

        event.replyEmbeds(
                new EmbedBuilder()
                        .setAuthor(m.getEffectiveName(), m.getUser().getAvatarUrl(), m.getUser().getEffectiveAvatarUrl())
                        .addField("Points:", String.valueOf(reputation), true)
                        .addField("Rank: ", String.valueOf(reputationRank), true)
                        .build()
        ).queue();
    }
}
