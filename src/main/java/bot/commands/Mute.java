package bot.commands;

import bot.Command;
import bot.Main;
import bot.Tools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class Mute implements Command {

    @Override
    public String getCommand() {
        return "mute";
    }

    @Override
    public String getHelp() {
        return "Mutes a Member!\n" +
                "Usage: `" + Main.getPrefix() + getCommand() + " <user> <time|seconds> <reason>`";
    }

    @Override
    public void run(List<String> args, MessageReceivedEvent event) {
        final MessageChannel channel = event.getChannel();
        final Message message = event.getMessage();
        final Member member = event.getMember();
        if (message.getMentionedMembers().isEmpty()) {
            Tools.wrongUsage(channel, this);
            return;
        }
        final Member target = message.getMentionedMembers().get(0);
        if (target.getRoles().stream().anyMatch(it -> it.getIdLong() == 741287382757933206L)) {
            channel.sendMessageEmbeds(new EmbedBuilder().setDescription("The user is already in timeout!").build()).queue();
            return;
        }

        if (!member.hasPermission(Permission.MODERATE_MEMBERS)) {
            channel.sendMessageEmbeds(new EmbedBuilder().setDescription("You don't have Permission to Timeout Members!").build()).queue();
            return;
        }
        if (!event.getGuild().getSelfMember().hasPermission(Permission.MODERATE_MEMBERS)) {
            event.getChannel().sendMessageEmbeds(new EmbedBuilder().setDescription("I don't have the **Timeout Members** Permission!").build()).queue();
            return;
        }
        if (!event.getGuild().getSelfMember().canInteract(target) || target.hasPermission(Permission.MODERATE_MEMBERS)) {
            event.getChannel().sendMessageEmbeds(new EmbedBuilder().setDescription("I can't timeout that user! The user has a higher role or is a moderator!").build()).queue();
            return;
        }
        if (!member.canInteract(target)) {
            event.getChannel().sendMessageEmbeds(new EmbedBuilder().setDescription("You can't timeout that user! The user has a higher role or is a moderator!").build()).queue();
            return;
        }
        int minutes = 0;
        String reason = "unkown";
        try {
            minutes = Integer.parseInt(args.get(1));
        } catch (Exception ignored) {}
        try {
            if (minutes == 0) reason = String.join(" ", args.subList(1, args.size()));
            else reason = String.join(" ", args.subList(2, args.size()));
        } catch (Exception ignored) {}
        try {
            Tools.muteMember(target, event.getGuild(), minutes, reason);
            event.getChannel().sendMessageEmbeds(new EmbedBuilder().setDescription("Successfully sent " + target.getAsMention() + " into timeout!").build()).queue();
        } catch (Exception e) {
            event.getChannel().sendMessageEmbeds(new EmbedBuilder().setDescription("Could not send " + target.getAsMention() + " into timeout!").build()).queue();
        }
    }
}
