package bot.commands;

import bot.Command;
import bot.Tools;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

@SuppressWarnings("ConstantConditions")
public class Unmute implements Command {

    @Override
    public CommandData getCommandData() {
        return new CommandData("unmute", "Unmutes a Member.")
                .addOption(OptionType.USER, "user", "User to be muted.", true);
    }

    @Override
    public void run(SlashCommandEvent event) {
        Member member = event.getMember();
        Member target = event.getOption("user").getAsMember();

        if (!target.isTimedOut()) {
            event.reply("The user is not in timeout!").setEphemeral(true).queue();
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
        if (!event.getGuild().getSelfMember().canInteract(target)) {
            event.reply("I can't unmute that user! The user has a higher role!").setEphemeral(true).queue();
            return;
        }
        if (!member.canInteract(target)) {
            event.reply("You can't timeout that user! The user has a higher role!").setEphemeral(true).queue();
            return;
        }

        Tools.unmuteMember(target);
        event.getChannel().sendMessage("Successfully unmuted " + target.getAsMention()).queue();
    }
}
