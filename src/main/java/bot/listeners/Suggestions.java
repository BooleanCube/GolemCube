package bot.listeners;

import bot.Main;
import bot.module.Module;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Suggestions extends ListenerAdapter {

    private final String channelID;

    public Suggestions(String channelID) {
        this.channelID = channelID;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!Main.getModuleManager().isEnabled(Module.SUGGESTION_LISTENER)) return;
        if (event.getAuthor().isBot() || event.getMessage().getContentRaw().toLowerCase().startsWith(Main.getPrefix()))
            return;

        if (event.getChannel().getId().equals(channelID)) {
            event.getMessage().delete().queue();
            event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                    .setAuthor(Objects.requireNonNull(event.getMember()).getEffectiveName(), event.getAuthor().getAvatarUrl(), event.getAuthor().getEffectiveAvatarUrl())
                    .setDescription(event.getMessage().getContentRaw())
                    .build()
            ).queue(m -> {
                m.addReaction("✅").queue();
                m.addReaction("❌").queue();
            });
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (Objects.requireNonNull(event.getMember()).getUser().isBot()) return;
        if (event.getChannel().getIdLong() == 901832454904614953L) {
            if (event.getReaction().retrieveUsers().complete().size() < 10) return;
            if (event.getReaction().getReactionEmote().getEmoji().equalsIgnoreCase("✅")) {
                Message msg = event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete();
                msg.clearReactions().queue();
                msg.editMessageEmbeds(
                        new EmbedBuilder()
                                .setAuthor(Objects.requireNonNull(msg.getEmbeds().get(0).getAuthor()).getName(), Objects.requireNonNull(msg.getEmbeds().get(0).getAuthor()).getUrl(), msg.getEmbeds().get(0).getAuthor().getIconUrl())
                                .setDescription(msg.getEmbeds().get(0).getDescription())
                                .setFooter("Suggestion sent!")
                                .build()
                ).queue();
                TextChannel trending = event.getGuild().getTextChannelsByName("trending-suggestions", true).get(0);
                trending.sendMessageEmbeds(
                        new EmbedBuilder()
                                .setAuthor(Objects.requireNonNull(msg.getEmbeds().get(0).getAuthor()).getName(), Objects.requireNonNull(msg.getEmbeds().get(0).getAuthor()).getUrl(), msg.getEmbeds().get(0).getAuthor().getIconUrl())
                                .addField("Description", msg.getEmbeds().get(0).getDescription(), false)
                                .addField("Link", "https://discord.com/channels/" + event.getGuild().getId() + "/" + event.getChannel().getId() + "/" + event.getMessageId(), false)
                                .build()
                ).queue();
            } else if (event.getReaction().getReactionEmote().getEmoji().equalsIgnoreCase("❌"))
                event.getChannel().deleteMessageById(event.getMessageIdLong()).queue();
        }
    }
}
