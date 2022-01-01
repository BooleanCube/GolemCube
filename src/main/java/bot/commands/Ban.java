package bot.commands;

import bot.Command;
import bot.Constants;
import bot.Tools;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class Ban implements Command {

    @Override
    public String getCommand() {
        return "ban";
    }

    @Override
    public String getHelp() {
        return "Bans a User from the Server!\n" +
                "Usage: `" + Constants.PREFIX + getCommand() + " <user> <time|days> <reason>`";
    }

    @Override
    public void run(List<String> args, MessageReceivedEvent event) {
        final MessageChannel channel = event.getChannel();
        final Message message = event.getMessage();
        final Member member = event.getMember();

        if (args.size() < 2 || message.getMentionedMembers().isEmpty()) {
            Tools.wrongUsage(channel, this);
            return;
        }

        final Member target = message.getMentionedMembers().get(0);

        if (!member.hasPermission(Permission.BAN_MEMBERS)) {
            channel.sendMessage("You don't have Permission to Ban Members!").queue();
            return;
        }

        if (!event.getGuild().getSelfMember().hasPermission(Permission.BAN_MEMBERS)) {
            event.getChannel().sendMessage("I don't have the **BAN_MEMBERS** Permission!").queue();
            return;
        }

        if (!event.getGuild().getSelfMember().canInteract(target) || target.hasPermission(Permission.BAN_MEMBERS)) {
            event.getChannel().sendMessage("I can't ban that user! The user has a higher role or is a moderator!").queue();
            return;
        }

        if (!member.canInteract(target)) {
            event.getChannel().sendMessage("You can't ban that user! The user has a higher role or is a moderator!").queue();
            return;
        }

        int days = 0;
        String reason = "";
        try {
            days = Integer.parseInt(args.get(1));
        } catch (Exception ignored) {
        }
        try {
            if (days == 0) {
                reason = String.join(" ", args.subList(1, args.size()));
            } else {
                reason = String.join(" ", args.subList(2, args.size()));
            }
        } catch (Exception ignored) {
        }

        try {
            target.ban(days, reason.equals("") ? null : reason).queue();
            event.getChannel().sendMessage("Successfully banned " + target.getAsMention()).queue();
        } catch (Exception e) {
            event.getChannel().sendMessage("Could not ban " + target.getAsMention()).queue();
        }

    }
}
