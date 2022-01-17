package bot.listeners.moderation;

import bot.Main;
import bot.Tools;
import bot.module.Module;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ConstantConditions")
public class MassMentionControl extends ListenerAdapter {
    public HashMap<String, MENTIONS> memberToMentions = new HashMap<>();

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (!Main.getModuleManager().isEnabled(Module.MASS_MENTION_CONTROL)) return;
        if (event.getAuthor().isBot()) return;
        if (!event.isFromGuild()) return;
        if (event.getMessage().getMentionedMembers().size() >= 1) {
            if (memberToMentions.containsKey(event.getMember().getId())) {
                memberToMentions.get(event.getMember().getId()).mentions += event.getMessage().getMentionedMembers().size();
                memberToMentions.get(event.getMember().getId()).lastTimeMention = System.currentTimeMillis();
            } else memberToMentions.put(event.getMember().getId(), new MENTIONS(1, System.currentTimeMillis()));
        }
        if (memberToMentions.containsKey(event.getMember().getId()) &&
                (System.currentTimeMillis() - memberToMentions.get(event.getMember().getId()).lastTimeMention <= 5000) &&
                memberToMentions.get(event.getMember().getId()).mentions > 9) {
            event.getChannel().sendMessageEmbeds(new EmbedBuilder().setDescription("Please do not mass mention on this server! You have been muted for `2 days`!").build()).queue();
            Tools.muteMember(event.getMember(), event.getGuild(), TimeUnit.DAYS.toMinutes(2), "Mass Mention");
        }
    }

    static class MENTIONS {
        public int mentions;
        public long lastTimeMention;

        public MENTIONS(int m, long lt) {
            mentions = m;
            lastTimeMention = lt;
        }
    }
}
