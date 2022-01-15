package bot.commands;

import bot.Command;
import bot.Main;
import bot.Tools;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class Kick implements Command {

    @Override
    public String getCommand() {
        return "kick";
    }

    @Override
    public String getHelp() {
        return "Kicks a User from the Server!\n" +
                "Usage: `" + Main.getPrefix() + getCommand() + " <user> <reason>`";
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

        if (!member.hasPermission(Permission.KICK_MEMBERS)) {
            channel.sendMessage("You don't have Permission to Kick Members!").queue();
            return;
        }

        if (!event.getGuild().getSelfMember().hasPermission(Permission.KICK_MEMBERS)) {
            event.getChannel().sendMessage("I don't have the **KICK_MEMBERS** Permission!").queue();
            return;
        }

        if (!event.getGuild().getSelfMember().canInteract(target) || target.hasPermission(Permission.KICK_MEMBERS)) {
            event.getChannel().sendMessage("I can't kick that user! The user has a higher role or is a moderator!").queue();
            return;
        }

        if (!member.canInteract(target)) {
            event.getChannel().sendMessage("You can't kick that user! The user has a higher role or is a moderator!").queue();
            return;
        }

        target.kick(args.size() == 1 ? "" : String.join(" ", args.subList(1, args.size()))).queue();
        event.getChannel().sendMessage("Successfully kicked " + target.getUser().getAsTag() + " from the server!").queue();
    }


}
