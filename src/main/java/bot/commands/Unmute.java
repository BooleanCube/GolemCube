package bot.commands;

import bot.Command;
import bot.Tools;
import net.dv8tion.jda.api.EmbedBuilder;
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

        if (target.isTimedOut()) {
            channel.sendMessageEmbeds(new EmbedBuilder().setDescription("The user is in timeout!").build()).queue();
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
        if (!event.getGuild().getSelfMember().canInteract(target)) {
            event.getChannel().sendMessageEmbeds(new EmbedBuilder().setDescription("I can't unmute that user! The user has a higher role!").build()).queue();
            return;
        }
        if (!member.canInteract(target)) {
            event.getChannel().sendMessageEmbeds(new EmbedBuilder().setDescription("You can't timeout that user! The user has a higher role!").build()).queue();
            return;
        }

        try {
            Tools.unmuteMember(target);
            event.getChannel().sendMessage("Successfully unmuted " + target.getAsMention()).queue();
        } catch (Exception e) {
            event.getChannel().sendMessage("Could not unmute " + target.getAsMention()).queue();
        }
    }
}
