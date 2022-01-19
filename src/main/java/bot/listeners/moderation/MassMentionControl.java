package bot.listeners.moderation;

import bot.Tools;
import bot.module.Module;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ConstantConditions")
public class MassMentionControl extends ModuleController {
    private final HashMap<String, Mentions> memberToMentions = new HashMap<>();
    private final long muteTime = TimeUnit.DAYS.toMinutes(2);

    public MassMentionControl() {
        super(Module.LINK_CONTROL);
    }

    @Override
    public void check(MessageReceivedEvent event) {
        long time = System.currentTimeMillis();
        int mentionCount = event.getMessage().getMentionedMembers().size();
        if (mentionCount >= 1) {
            Mentions mentions = memberToMentions.computeIfAbsent(event.getMember().getId(), id -> new Mentions(0, time));
            mentions.mentions += mentionCount;
            if (time - mentions.lastTimeMention <= 5000 && mentions.mentions > 9) {
                event.getChannel().sendMessageEmbeds(new EmbedBuilder().setDescription("Please do not mass mention on this server! You have been muted for `2 days`!").build()).queue();
                Tools.muteMember(event.getMember(), muteTime, "Mass Mention");
            }
            mentions.lastTimeMention = time;
        }
    }

    static class Mentions {
        public int mentions;
        public long lastTimeMention;

        public Mentions(int m, long lt) {
            mentions = m;
            lastTimeMention = lt;
        }
    }
}
