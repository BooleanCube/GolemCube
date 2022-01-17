package bot.listeners;

import bot.Main;
import bot.database.Database;
import bot.database.ReputationsResult;
import bot.commands.ReputationLeaderboard;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ReputationLeaderboardButtons extends ListenerAdapter {
    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        final String[] id = event.getComponentId().split(":");
        String userId = id[0];
        String buttonId = id[1];

        if (!Objects.requireNonNull(event.getMember()).getId().equals(userId)) {
            event.reply("You are not the one who requested the leaderboard. Use `" + Main.getPrefix() + "leaderboard` to create a new one.")
                    .setEphemeral(true).queue();
        }

        final Message message = event.getMessage();
        String page = message.getButtons().stream()
                .filter(it -> it.getLabel().equals("Done"))
                .findAny().get().getId().split(":")[2];

        User user = event.getMember().getUser();

        switch (buttonId) {
            case "delete": {
                event.deferEdit().queue();
                message.delete().queue();
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
        page = next ? page + 1 : page - 1;

        ReputationsResult reputations = Database.getMemberReputationsWithUser(user);
        List<List<ReputationsResult.BMember>> guildsList = reputations.getMemberReputations();
        if (page == 0 || page == guildsList.size()) return;

        ReputationsResult.BMember member = reputations.getMember();
        EmbedBuilder embed = new EmbedBuilder().setDescription("```" + ReputationLeaderboard.getTable(guildsList.get(page - 1)) + "```")
                .addField("Your Rank", "`" + member.getRank() + ". " + member.getName() + " : " + member.getPoints() + "`", false);

        String userId = user.getId();

        msg.editMessageEmbeds(embed.build()).queue();
        msg.editMessageComponents(ActionRow.of(
                Button.primary(userId + ":previous", "Previous"),
                Button.success(userId + ":done" + page, Emoji.fromUnicode("✅")),
                Button.danger(userId + ":delete", Emoji.fromUnicode("\uD83D\uDDD1")),
                Button.primary(userId + ":next", "Next")
        )).queue();
    }

}
