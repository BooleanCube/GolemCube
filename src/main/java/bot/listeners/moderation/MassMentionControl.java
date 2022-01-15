package bot.listeners.moderation;

import bot.Main;
import bot.Tools;
import bot.module.Module;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MassMentionControl extends ListenerAdapter {

    public HashMap<Member, MENTIONS> memberToMentions = new HashMap<>();

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (!Main.getModuleManager().isEnabled(Module.MASS_MENTION_CONTROL)) return;
        if (event.getMessage().getMentionedMembers().size() >= 1) {
            if (memberToMentions.containsKey(event.getMember())) {
                memberToMentions.get(event.getMember()).mentions += event.getMessage().getMentionedMembers().size();
                memberToMentions.get(event.getMember()).lastTimeMention = System.currentTimeMillis();
            } else memberToMentions.put(event.getMember(), new MENTIONS(1, System.currentTimeMillis()));
        }
        if (memberToMentions.containsKey(event.getMember()) && (System.currentTimeMillis() - memberToMentions.get(event.getMember()).lastTimeMention <= 5000) && memberToMentions.get(event.getMember()).mentions > 9) {
            event.getChannel().sendMessageEmbeds(new EmbedBuilder().setDescription("Please do not mass mention on this server! You have been muted for `2 days`!").build()).queue();
            Tools.muteMember(event.getMember(), event.getGuild(), TimeUnit.DAYS.toMinutes(2), "Mass Mention");
        }
    }

    class MENTIONS {
        public int mentions = 0;
        public long lastTimeMention = 0;

        public MENTIONS(int m, long lt) {
            mentions = m;
            lastTimeMention = lt;
        }
    }
}
