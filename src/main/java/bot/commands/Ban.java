package bot.commands;

import bot.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

@SuppressWarnings("ConstantConditions")
public class Ban implements Command {
    @Override
    public CommandData getCommandData() {
        return new CommandData("ban", "Bans a User from the Server!")
                .addOption(OptionType.USER, "user", "The user to be banned.", true)
                .addOption(OptionType.INTEGER, "deldays", "The history of messages, in days, that will be deleted.")
                .addOption(OptionType.STRING, "reason", "The reason for this action.")
                .setDefaultEnabled(false);
    }

    @Override
    public void run(SlashCommandEvent event) {
        Member member = event.getMember();

        if (!member.hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("You don't have Permission to Ban Members!").setEphemeral(true).queue();
            return;
        }

        if (!event.getGuild().getSelfMember().hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("I don't have the **BAN_MEMBERS** Permission!").setEphemeral(true).queue();
            return;
        }

        Member target = event.getOption("user").getAsMember();

        if (!event.getGuild().getSelfMember().canInteract(target) || target.hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("I can't ban that user! The user has a higher role or is a moderator!").setEphemeral(true).queue();
            return;
        }

        if (!member.canInteract(target)) {
            event.reply("You can't ban that user! The user has a higher role or is a moderator!").setEphemeral(true).queue();
            return;
        }

        OptionMapping delDays = event.getOption("deldays");
        OptionMapping reason = event.getOption("reason");

        int delDaysAsInt = Math.abs(Math.toIntExact(delDays == null ? 0 : delDays.getAsLong()));

        target.ban(delDaysAsInt, reason == null ? null : reason.getAsString()).queue();
        event.reply("Successfully banned " + target.getAsMention()).queue();
    }
}
