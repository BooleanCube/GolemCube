package bot.listeners;

import bot.SettingType;
import bot.Settings;
import bot.Tools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("ConstantConditions")
public class SpamControl extends ListenerAdapter {
    static ArrayList<String> blackListedChannels = new ArrayList<>();
    static ArrayList<String> blackListedMembers = new ArrayList<>();
    static ArrayList<String> blackListedRoles = new ArrayList<>();

    HashMap<Member, MessageHistory> messageTracking = new HashMap<>();

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (!Settings.isEnabled(SettingType.SpamControl)) return;

        Member member = event.getMember();

        if (blackListedChannels.contains(event.getChannel().getId())
                || blackListedMembers.contains(member.getId())
                || member.getRoles().stream().anyMatch(it -> blackListedRoles.contains(it.getId()))) {
            return;
        }

        // 9 messages in under 3 seconds then mute for 5 minutes and add a warning!
        int scmessages = 9;
        int scseconds = 3;

        if (!messageTracking.containsKey(member)) {
            messageTracking.put(member, new MessageHistory(1, System.currentTimeMillis()));
        } else {
            int msgNum = messageTracking.get(member).msgNum++;
            long lastTimeSent = messageTracking.get(member).lastTimeSent;
            if (msgNum == scmessages && System.currentTimeMillis() - lastTimeSent <= scseconds * 1000) {
                Tools.muteMember(member, event.getGuild(), 5, "Spamming");
                member.getUser().openPrivateChannel().queue(c -> {
                    c.sendMessageEmbeds(new EmbedBuilder().setDescription("You have been muted for **5 minutes** for spamming in a channel! You have also been given **1 warning**!").build()).queue();
                });
                event.getChannel().sendMessage("Please do not spam! You have been muted for `5 minutes`!").queue();
                if (Tools.memberToWarns.get(member).size() >= 4) {
                    event.getChannel().sendMessage("Kicked " + member.getAsMention() + " from the server because they exceeded `3 warnings`!").queue();
                    member.kick("Exceeded 3 warnings!").queue();
                }
            } else if (System.currentTimeMillis() - lastTimeSent > scseconds * 1000) {
                messageTracking.get(member).msgNum = 1;
                messageTracking.get(member).lastTimeSent = System.currentTimeMillis();
            }
            messageTracking.get(member).lastTimeSent = System.currentTimeMillis();
        }
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        //Add all of your blacklists here
        blackListedChannels.add("768793442632990721");
        blackListedChannels.add("741785877944074251");
        blackListedMembers.add(event.getJDA().getGuilds().get(0).getOwner().getId());
        blackListedRoles.add("773337238952083477");
    }
}

class MessageHistory {
    public MessageHistory(int msgNum, long lastTimeSent) {
        this.msgNum = msgNum;
        this.lastTimeSent = lastTimeSent;
    }

    int msgNum;
    long lastTimeSent;
}