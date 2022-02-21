package bot.commands;

import bot.Command;
import bot.Tools;
import bot.Warning;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.List;

public class Warn implements Command {

    @Override
    public String getCategory() {
        return "Moderation";
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("warn", "Warns a member!")
                .addOption(OptionType.USER, "user", "The user to be warned", true)
                .addOption(OptionType.STRING, "reason", "The reason for this action.");
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void run(SlashCommandInteractionEvent event) {
        if (event.getMember().getRoles().stream().anyMatch(it -> it.getId().equals("773337238952083477"))) {
            try {
                String id = event.getOption("user").getAsMember().getId();
                List<Warning> warns = Tools.getWarns(id);

                String reason = event.getOption("reason", "No reason provided!", OptionMapping::getAsString);
                warns.add(new Warning(reason, System.currentTimeMillis()));
                event.reply("Successfully warned <@" + id + "> for `" + reason + "`").queue();
                if (warns.size() > 3) {
                    event.getGuild().ban(id, 7, "Exceeded 3 warnings!").queue(v ->
                            event.reply("Banned <@" + id + "> from the server because they exceeded `3 warnings`!").queue());
                }
            } catch (Exception e) {
                Tools.wrongUsage(event, this);
                e.printStackTrace();
            }
        } else {
            event.reply("You do not have the permission to use this command!").setEphemeral(true).queue();
        }
    }
}
