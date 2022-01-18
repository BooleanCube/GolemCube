package bot.listeners.moderation;

import bot.Tools;
import bot.config.Config;
import bot.module.Module;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class SpamControl extends ModuleController {
    static List<String> blackListedChannels;
    static List<String> blackListedMembers;
    static List<String> blackListedRoles;
    private final HashMap<String, MessageHistory> messageTracking = new HashMap<>();

    public SpamControl(Config config) {
        super(Module.SPAM_CONTROL);
        blackListedChannels = config.channelSpamBlacklist();
        blackListedMembers = config.memberSpamBlacklist();
        blackListedRoles = config.roleSpamBlacklist();
//        if(blackListedChannels == null) blackListedChannels = new ArrayList<>();
//        if(blackListedMembers == null) blackListedMembers = new ArrayList<>();
//        if(blackListedRoles == null) blackListedRoles = new ArrayList<>();
    }

    @Override
    public void check(MessageReceivedEvent event) {
        Member member = event.getMember();
        String id = member.getId();

        if (blackListedChannels.contains(event.getChannel().getId())
            || blackListedMembers.contains(id)
            || member.getRoles().stream().map(Role::getId).anyMatch(blackListedRoles::contains)) {
            return;
        }

        // 9 messages in under 3 seconds then mute for 5 minutes and add a warning!
        int scmessages = 5;
        int scseconds = 2;

        MessageHistory history = messageTracking.get(id);
        long time = System.currentTimeMillis();
        if (history == null) {
            messageTracking.put(id, new MessageHistory(1, time));
            return;
        }
        history.msgNum++;
        if (history.msgNum == scmessages && time - history.lastTimeSent <= scseconds * 1000) {
            MessageEmbed embed = new EmbedBuilder()
                .setDescription("You have been muted for **5 minutes** for spamming in a channel! You have also been given **1 warning**!")
                .setFooter(member.getEffectiveName())
                .build();
            member.getUser()
                .openPrivateChannel()
                .flatMap(c -> c.sendMessageEmbeds(embed))
                .onErrorFlatMap(e -> event.getChannel().sendMessageEmbeds(embed))
                .queue();
            event.getChannel().sendMessage("Please do not spam, " + event.getAuthor().getAsMention() + "! You have been muted for `5 minutes` and given `1 warning`!").queue();
            if (Tools.getWarns(member.getId()).size() >= 4) {
                event.getChannel().sendMessage("Kicked " + member.getAsMention() + " from the server because they exceeded `3 warnings`!").queue();
                member.kick("Exceeded 3 warnings!").queue();
            }
            Tools.muteMember(member, 5, "Spamming");
        } else if (time - history.lastTimeSent > scseconds * 1000) {
            history.msgNum = 1;
            history.lastTimeSent = time;
        }
    }

    // TODO: is this necessary? We have the constructor to add blacklists
    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        //Add all of your blacklists here
    }
}
