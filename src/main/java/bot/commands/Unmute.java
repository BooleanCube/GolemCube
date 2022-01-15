package bot.commands;

import bot.Command;
import bot.Tools;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class Unmute implements Command {

    @Override
    public String getCommand() {
        return "unmute";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public void run(List<String> args, MessageReceivedEvent event) {
        final MessageChannel channel = event.getChannel();
        final Message message = event.getMessage();
        final Member member = event.getMember();

        if (args.size() < 1 || message.getMentionedMembers().isEmpty()) {
            Tools.wrongUsage(channel, this);
            return;
        }

        final Member target = message.getMentionedMembers().get(0);

        if (target.getRoles().stream().anyMatch(it -> it.getIdLong() == 741287382757933206L)) {
            channel.sendMessage("The User is already Muted!").queue();
            return;
        }

        if (!member.hasPermission(Permission.MODERATE_MEMBERS)) {
            channel.sendMessage("You don't have Permission to Mute Members!").queue();
            return;
        }

        if (!event.getGuild().getSelfMember().hasPermission(Permission.MODERATE_MEMBERS)) {
            event.getChannel().sendMessage("I don't have the **MANAGE_ROLES** Permission!").queue();
            return;
        }

        if (!event.getGuild().getSelfMember().canInteract(target) || target.hasPermission(Permission.MODERATE_MEMBERS)) {
            event.getChannel().sendMessage("I can't mute that user! The user has a higher role or is a moderator!").queue();
            return;
        }

        if (!member.canInteract(target)) {
            event.getChannel().sendMessage("You can't mute that user! The user has a higher role or is a moderator!").queue();
            return;
        }

        try {
            Tools.unmuteMember(target, event.getGuild());
            event.getChannel().sendMessage("Successfully unmuted " + target.getAsMention()).queue();
        } catch (Exception e) {
            event.getChannel().sendMessage("Could not unmute " + target.getAsMention()).queue();
        }
    }
}
