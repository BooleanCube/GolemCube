package bot.listeners;

import bot.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class Suggestions extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if(event.getAuthor().isBot() || event.getMessage().getContentRaw().toLowerCase().startsWith(Constants.PREFIX)) return;
        if(event.getChannel().getIdLong() == 901832454904614953L) {
            event.getMessage().delete().queue();
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setAuthor(event.getMember().getEffectiveName(), event.getAuthor().getAvatarUrl(), event.getAuthor().getEffectiveAvatarUrl())
                    .setDescription(event.getMessage().getContentRaw())
                    .build()
            ).queue(m -> {
                m.addReaction("✅").queue();
                m.addReaction("❌").queue();
            });
        }
    }

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        if(event.getMember().getUser().isBot()) return;
        if(event.getChannel().getIdLong() == 901832454904614953L) {
            if(event.getReaction().retrieveUsers().complete().size() < 10) return;
            if(event.getReaction().getReactionEmote().getEmoji().equalsIgnoreCase("✅")) {
                Message msg = event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete();
                msg.clearReactions().queue();
                msg.editMessage(
                        new EmbedBuilder()
                                .setAuthor(msg.getEmbeds().get(0).getAuthor().getName(), msg.getEmbeds().get(0).getAuthor().getUrl(), msg.getEmbeds().get(0).getAuthor().getIconUrl())
                                .setDescription(msg.getEmbeds().get(0).getDescription())
                                .setFooter("Suggestion sent!")
                                .build()
                ).queue();
                TextChannel trending = event.getGuild().getTextChannelsByName("trending-suggestions", true).get(0);
                trending.sendMessage(
                        new EmbedBuilder()
                                .setAuthor(msg.getEmbeds().get(0).getAuthor().getName(), msg.getEmbeds().get(0).getAuthor().getUrl(), msg.getEmbeds().get(0).getAuthor().getIconUrl())
                                .addField("Description", msg.getEmbeds().get(0).getDescription(), false)
                                .addField("Link", "https://discord.com/channels/" + event.getGuild().getId() + "/" + event.getChannel().getId() + "/" + event.getMessageId(), false)
                                .build()
                ).queue();
            }
            else if(event.getReaction().getReactionEmote().getEmoji().equalsIgnoreCase("❌"))
                event.getChannel().deleteMessageById(event.getMessageIdLong()).queue();
        }
    }
}
