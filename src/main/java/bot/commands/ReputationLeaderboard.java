package bot.commands;

import bot.Command;
import bot.database.Database;
import bot.database.ReputationsResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;

public class ReputationLeaderboard implements Command {

    @Override
    public String getCategory() {
        return "Reputation";
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("leaderboard", "Shows a list of 10 server members with the highest reputation.")
                .addOptions(
                        new OptionData(OptionType.INTEGER, "page", "The page number of the leaderboard.").setMinValue(0)
                );
    }

    @Override
    public void run(SlashCommandInteractionEvent event) {
        User author = event.getUser();
        String userId = author.getId();

        int page = event.getOption("page", 1, OptionMapping::getAsInt);

        ReputationsResult reputations = Database.getMemberReputationsWithUser(author);
        ReputationsResult.BMember bMember = reputations.getMember();

        List<ReputationsResult.BMember> memberList = reputations.getMemberReputations().get(page - 1);

        if (memberList == null) {
            event.reply("No members on the page").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder().setDescription("```" + getTable(memberList) + "```")
                .addField("Your Rank", "`" + bMember.getRank() + ". " + bMember.getName() + " : " + bMember.getPoints() + "`", false);

        event.replyEmbeds(embed.build())
                .addActionRow(
                        Button.primary(userId + ":previous", "Previous"),
                        Button.success(userId + ":done:" + page, Emoji.fromUnicode("✅")),
                        Button.danger(userId + ":delete", Emoji.fromUnicode("\uD83D\uDDD1")),
                        Button.primary(userId + ":next", "Next")
                ).queue();
    }

    public static String getTable(List<ReputationsResult.BMember> memberList) {
        StringBuilder table = new StringBuilder();
        int nameSize = memberList.stream()
                .mapToInt(it -> Math.min(it.getName().length(), 22))
                .max()
                .orElse(0);
        int pointSize = memberList.stream()
                .mapToInt(it -> String.valueOf(it.getPoints()).length())
                .max()
                .orElse(0);

        String rowFormat = "║%-" + (Math.max(4, String.valueOf(memberList.get(memberList.size() - 1).getRank()).length()) + 1) + "s" +
                "║%-" + (Math.max(nameSize, 5) + 1) + "s" +
                "║%-" + (Math.max(pointSize, 6) + 1) + "s║%n";
        String divider = String.format(rowFormat, "", "", "", "").replaceAll(" ", "═");

        table.append(String.format(rowFormat, "", "", "", "").replaceFirst("║", "╔")
                .replaceFirst("║", "╦").replaceFirst("║", "╦")
                .replaceFirst("║", "╗").replaceAll(" ", "═"));
        table.append(String.format(rowFormat, "Rank ", "Name", "Points"));
        table.append(divider);

        for (ReputationsResult.BMember member : memberList) {
            String name = member.getName();
            table.append(String.format(rowFormat, member.getRank() + ".", name.substring(0, Math.min(22, name.length())), member.getPoints()));
        }

        table.append(String.format(rowFormat, "", "", "", "").replaceFirst("║", "╚")
                .replaceFirst("║", "╩").replaceFirst("║", "╩")
                .replaceFirst("║", "╝").replaceAll(" ", "═"));

        return table.toString();
    }
}
