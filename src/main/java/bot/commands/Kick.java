package bot.commands;

import bot.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@SuppressWarnings("ConstantConditions")
public class Kick implements Command {

    @Override
    public String getCategory() {
        return "Moderation";
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("kick", "Kicks a User from the Server!")
                .addOption(OptionType.USER, "user", "The user to be kicked.", true)
                .addOption(OptionType.STRING, "reason", "The reason for this action.");
    }

    @Override
    public void run(SlashCommandInteractionEvent event) {
        Member member = event.getMember();

        if (!member.hasPermission(Permission.KICK_MEMBERS)) {
            event.reply("You don't have Permission to Kick Members!").setEphemeral(true).queue();
            return;
        }

        if (!event.getGuild().getSelfMember().hasPermission(Permission.KICK_MEMBERS)) {
            event.reply("I don't have the **KICK_MEMBERS** Permission!").setEphemeral(true).queue();
            return;
        }

        Member target = event.getOption("user").getAsMember();

        if (!event.getGuild().getSelfMember().canInteract(target) || target.hasPermission(Permission.KICK_MEMBERS)) {
            event.reply("I can't kick that user! The user has a higher role or is a moderator!").setEphemeral(true).queue();
            return;
        }

        if (!member.canInteract(target)) {
            event.reply("You can't kick that user! The user has a higher role or is a moderator!").setEphemeral(true).queue();
            return;
        }

        String reason = event.getOption("reason", null, OptionMapping::getAsString);

        target.kick(reason).queue();
        event.reply("Successfully kicked " + target.getUser().getAsTag() + " from the server!").queue();
    }
}
