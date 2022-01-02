package bot.commands;

import bot.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.List;

public class ReputationLeaderboard implements Command {
    @Override
    public String getCommand() {
        return "lead";
    }

    @Override
    public String getHelp() {
        return "Shows a list of 10 server members with the highest reputation\n" +
                "Usage: `" + Constants.PREFIX + getCommand() + "` <page-number(optional)>";
    }

    @Override
    public void run(List<String> args, MessageReceivedEvent event) {
        User author = event.getAuthor();
        String userId = author.getId();

        try {

            int page = args.isEmpty() ? 1 : Integer.parseInt(args.get(0));
            ReputationsResult reputations = Database.getMemberReputationsWithUser(author);
            ReputationsResult.BMember member = reputations.getMember();

            EmbedBuilder embed = new EmbedBuilder().setDescription("```" + getTable(reputations.getMemberReputations().get(page - 1)) + "```")
                    .addField("Your Rank", "`" + member.getRank() + ". " + member.getName() + " : " + member.getPoints() + "`", false);

            event.getChannel().sendMessageEmbeds(embed.build())
                    .setActionRow(
                            Button.primary(userId + ":previous", "Previous"),
                            Button.success(userId + ":done:" + page, "Done"),
                            Button.primary(userId + ":next", "Next"),
                            Button.danger(userId + ":delete", "Delete")
                    ).queue();
        } catch (NumberFormatException e) {
            Tools.wrongUsage(event.getChannel(), this);
        } catch (IndexOutOfBoundsException e) {
            event.getChannel().sendMessage("No members on the page").queue();
        }
    }

    public static String getTable(List<ReputationsResult.BMember> memberList) {
        StringBuilder table = new StringBuilder();
        final int nameSize = memberList.stream()
                .mapToInt(it -> Math.min(it.getName().length(), 22))
                .max()
                .orElse(0);
        final int pointSize = memberList.stream()
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

        for (final ReputationsResult.BMember member : memberList) {
            String name = member.getName();
            table.append(String.format(rowFormat, member.getRank() + ".", name.substring(0, Math.min(22, name.length())), member.getPoints()));
        }

        table.append(String.format(rowFormat, "", "", "", "").replaceFirst("║", "╚")
                .replaceFirst("║", "╩").replaceFirst("║", "╩")
                .replaceFirst("║", "╝").replaceAll(" ", "═"));

        return table.toString();
    }
}
