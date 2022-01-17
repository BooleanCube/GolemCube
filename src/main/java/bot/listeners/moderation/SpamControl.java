package bot.listeners.moderation;

import bot.Main;
import bot.Tools;
import bot.config.Config;
import bot.module.Module;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class SpamControl extends ListenerAdapter {
    static List<String> blackListedChannels;
    static List<String> blackListedMembers;
    static List<String> blackListedRoles;

    HashMap<Member, MessageHistory> messageTracking = new HashMap<>();

    public SpamControl(Config config) {
        blackListedChannels = config.channelSpamBlacklist();
        blackListedMembers = config.memberSpamBlacklist();
        blackListedRoles = config.roleSpamBlacklist();
//        if(blackListedChannels == null) blackListedChannels = new ArrayList<>();
//        if(blackListedMembers == null) blackListedMembers = new ArrayList<>();
//        if(blackListedRoles == null) blackListedRoles = new ArrayList<>();
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (!Main.getModuleManager().isEnabled(Module.SPAM_CONTROL)) return;
        if (event.getAuthor().isBot()) return;
        if(!event.isFromGuild()) return;
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
                member.getUser().openPrivateChannel().queue(c -> {
                    MessageEmbed embed = new EmbedBuilder().setDescription("You have been muted for **5 minutes** for spamming in a channel! You have also been given **1 warning**!").setFooter(member.getEffectiveName()).build();
                    c.sendMessageEmbeds(embed).queue(m -> {
                            },
                            e -> event.getChannel().sendMessageEmbeds(embed).queue()
                    );
                });
                event.getChannel().sendMessage("Please do not spam! You have been muted for `5 minutes` and given `1 warning`!").queue();
                if (Tools.memberToWarns.computeIfAbsent(member, (m) -> new ArrayList<>()).size() >= 4) {
                    event.getChannel().sendMessage("Kicked " + member.getAsMention() + " from the server because they exceeded `3 warnings`!").queue();
                    member.kick("Exceeded 3 warnings!").queue();
                }
                Tools.muteMember(member, event.getGuild(), 5, "Spamming");
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
    }
}
