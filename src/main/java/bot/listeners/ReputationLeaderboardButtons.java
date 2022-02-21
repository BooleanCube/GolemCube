package bot.listeners;

import bot.commands.ReputationLeaderboard;
import bot.database.Database;
import bot.database.ReputationsResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings({"ConstantConditions", "OptionalGetWithoutIsPresent"})
public class ReputationLeaderboardButtons extends ListenerAdapter {
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (!event.isFromGuild()) return;

        String[] id = event.getComponentId().split(":");

        if (!event.getMember().getId().equals(id[0])) {
            event.reply("You are not the one who requested the leaderboard. Use `/lead` to create a new one.")
                    .setEphemeral(true).queue();
        }

        Message message = event.getMessage();
        String page = message.getButtons().stream()
                .filter(it -> it.getId().contains("done"))
                .findAny().get().getId().split(":")[2];

        User user = event.getMember().getUser();

        switch (id[1]) {
            case "delete": {
                message.delete().queue();
                event.deferEdit().queue();
            }
            break;
            case "done": {
                final Button[] disabledButton = message.getButtons().stream().map(Button::asDisabled).toArray(Button[]::new);
                event.deferEdit().setActionRow(disabledButton).queue();
            }
            break;
            case "previous": {
                event.deferEdit().queue();
                editMessage(message, false, user, Integer.parseInt(page));
            }
            break;
            case "next": {
                event.deferEdit().queue();
                editMessage(message, true, user, Integer.parseInt(page));
            }
            break;
        }
    }

    private void editMessage(Message msg, boolean next, User user, int page) {
        ReputationsResult reputations = Database.getMemberReputationsWithUser(user);
        List<List<ReputationsResult.BMember>> guildsList = reputations.getMemberReputations();

        if (page == 0 || page == guildsList.size()) return;

        System.out.println("Current Page: " + page);
        page = next ? page + 1 : page - 1;

        System.out.println(page);

        ReputationsResult.BMember member = reputations.getMember();
        EmbedBuilder embed = new EmbedBuilder().setDescription("```" + ReputationLeaderboard.getTable(guildsList.get(page - 1)) + "```")
                .addField("Your Rank", "`" + member.getRank() + ". " + member.getName() + " : " + member.getPoints() + "`", false);

        String userId = user.getId();

        msg.editMessageEmbeds(embed.build()).queue();
        msg.editMessageComponents(ActionRow.of(
                Button.primary(userId + ":previous", "Previous"),
                Button.success(userId + ":done:" + page, Emoji.fromUnicode("✅")),
                Button.danger(userId + ":delete:" + msg.getId(), Emoji.fromUnicode("\uD83D\uDDD1")),
                Button.primary(userId + ":next", "Next")
        )).queue();
    }

}
