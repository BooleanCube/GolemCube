package bot.listeners;

import bot.BMember;
import bot.Database;
import bot.commands.ReputationLeaderboard;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReputationLeaderboardButtons extends ListenerAdapter {
    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        final String[] id = event.getComponentId().split(":");
        String userId = id[0];
        String buttonId = id[1];

        if (!event.getMember().getId().equals(userId)) {
            event.reply("You are not the one who requested the leaderboard. Use `g!leaderboard` to create a new one.")
                    .setEphemeral(true).queue();
        }

        final Message message = event.getMessage();
        String page = message.getButtons().stream()
                .filter(it -> it.getLabel().equals("Done"))
                .findAny().get().getId().split(":")[2];

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
            case "next": {
                event.deferEdit().queue();
                editMessage(message, true, userId, Integer.parseInt(page));
            }
            break;
            case "previous": {
                event.deferEdit().queue();
                editMessage(message, false, userId, Integer.parseInt(page));
            }
            break;
        }
    }

    private void editMessage(Message msg, boolean next, String userId, int page) {
        page = next ? page + 1 : page - 1;

        final List<List<BMember>> guildsList = Database.getMemberReputations();
        if (page == 0 || page == guildsList.size()) return;

        EmbedBuilder embed = new EmbedBuilder().setDescription("```" + ReputationLeaderboard.getTable(guildsList.get(page - 1)) + "```");

        msg.editMessageEmbeds(embed.build()).queue();
        msg.editMessageComponents(ActionRow.of(
                Button.primary(userId + ":previous", "Previous"),
                Button.success(userId + ":done:" + page, "Done"),
                Button.primary(userId + ":next", "Next"),
                Button.danger(userId + ":delete", "Delete")
        )).queue();
    }

}
