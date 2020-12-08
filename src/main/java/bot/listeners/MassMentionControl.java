package bot.listeners;

import bot.Tools;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class MassMentionControl extends ListenerAdapter {

    public HashMap<Member, MENTIONS> memberToMentions = new HashMap<>();

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if(event.getMessage().getMentionedMembers().size() >= 1) {
            if(memberToMentions.containsKey(event.getMember())) {
                memberToMentions.get(event.getMember()).mentions += event.getMessage().getMentionedMembers().size();
                memberToMentions.get(event.getMember()).lastTimeMention = System.currentTimeMillis();
            } else {
                memberToMentions.put(event.getMember(), new MENTIONS(event.getMessage().getMentionedMembers().size(), System.currentTimeMillis()));
            }
        }
        if(event.getMessage().getMentionedMembers().size() > 3) {
            event.getMessage().delete().queue();
            event.getChannel().sendMessage("You have been warned for mass mention!").queue();
        }
        if((System.currentTimeMillis()-memberToMentions.get(event.getMember()).lastTimeMention <= 5000) && memberToMentions.get(event.getMember()).mentions > 3) {
            event.getChannel().sendMessage("Please do not mass mention on this server! You have been muted for `2 hours`!").queue();
            Tools.muteMember(event.getMember(), event.getGuild(), 7200, "Mass Mention");
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
