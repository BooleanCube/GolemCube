package bot.commands;

import bot.Command;
import bot.Tools;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

@SuppressWarnings("ConstantConditions")
public class Mute implements Command {

    @Override
    public CommandData getCommandData() {
        return new CommandData("mute", "Mutes a Member using Timeout!")
                .addOption(OptionType.USER, "user", "User to be muted.", true)
                .addOption(OptionType.INTEGER, "minutes", "Duration for mute in minutes.")
                .addOption(OptionType.STRING, "reason", "The reason for this action.");
    }

    @Override
    public void run(SlashCommandEvent event) {
        Member member = event.getMember();
        Member target = event.getOption("user").getAsMember();

        if (target.isTimedOut()) {
            event.reply("The user is already in timeout!").setEphemeral(true).queue();
            return;
        }

        if (!member.hasPermission(Permission.MODERATE_MEMBERS)) {
            event.reply("You don't have Permission to Timeout Members!").setEphemeral(true).queue();
            return;
        }
        if (!event.getGuild().getSelfMember().hasPermission(Permission.MODERATE_MEMBERS)) {
            event.reply("I don't have the **Timeout Members** Permission!").setEphemeral(true).queue();
            return;
        }

        if (!event.getGuild().getSelfMember().canInteract(target) || target.hasPermission(Permission.MODERATE_MEMBERS)) {
            event.reply("I can't timeout that user! The user has a higher role or is a moderator!").setEphemeral(true).queue();
            return;
        }
        if (!member.canInteract(target)) {
            event.reply("You can't timeout that user! The user has a higher role or is a moderator!").setEphemeral(true).queue();
            return;
        }

        OptionMapping minutes = event.getOption("minutes");
        OptionMapping reason = event.getOption("reason");

        int minutesAsInt = minutes == null ? 1440 : Math.abs(Math.toIntExact(minutes.getAsLong()));

        Tools.muteMember(target, minutesAsInt, reason == null ? "Unknown." : reason.getAsString());
        event.reply("Successfully sent " + target.getAsMention() + " into timeout!").queue();
    }
}
