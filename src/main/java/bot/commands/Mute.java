package bot.commands;

import bot.Command;
import bot.Constants;
import bot.Tools;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class Mute implements Command {

    @Override
    public String getCommand() {
        return "mute";
    }

    @Override
    public String getHelp() {
        return "Mutes a Member!\n" +
                "Usage: `" + Constants.PREFIX + getCommand() + " <user> <time|seconds> <reason>`";
    }

    @Override
    public void run(List<String> args, GuildMessageReceivedEvent event) {
        final TextChannel channel = event.getChannel();
        final Message message = event.getMessage();
        final Member member = event.getMember();

        if (args.size() < 2 || message.getMentionedMembers().isEmpty()) {
            Tools.wrongUsage(channel, this);
            return;
        }

        final Member target = message.getMentionedMembers().get(0);

        if (target.getRoles().stream().anyMatch(it -> it.getIdLong() == 741287382757933206L)) {
            channel.sendMessage("The User is already Muted!").queue();
            return;
        }

        if (!member.hasPermission(Permission.MANAGE_SERVER)) {
            channel.sendMessage("You don't have Permission to Mute Members!").queue();
            return;
        }

        if (!event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.getChannel().sendMessage("I don't have the **MANAGE_ROLES** Permission!").queue();
            return;
        }

        if (!event.getGuild().getSelfMember().canInteract(target) || target.hasPermission(Permission.MANAGE_SERVER)) {
            event.getChannel().sendMessage("I can't mute that user! The user has a higher role or is a moderator!").queue();
            return;
        }

        if (!member.canInteract(target)) {
            event.getChannel().sendMessage("You can't mute that user! The user has a higher role or is a moderator!").queue();
            return;
        }

        int seconds = 0;
        String reason = "";
        try {
            seconds = Integer.parseInt(args.get(1));
        } catch (Exception ignored) {
        }
        try {
            if (seconds == 0) {
                reason = String.join(" ", args.subList(1, args.size()));
            } else {
                reason = String.join(" ", args.subList(2, args.size()));
            }
        } catch (Exception ignored) {
        }

        try {
            Tools.muteMember(target, event.getGuild(), seconds, reason);
            event.getChannel().sendMessage("Successfully muted " + target.getAsMention()).queue();
        } catch (Exception e) {
            event.getChannel().sendMessage("Could not mute " + target.getAsMention()).queue();
        }
    }
}
