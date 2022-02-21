package bot.commands;

import bot.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class End implements Command {

    @Override
    public String getCategory() {
        return "Reputation";
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("end", "After helping somebody, use this command if you want to remind them to give you a reputation point!");
    }

    @Override
    public void run(SlashCommandInteractionEvent event) {
        event.replyEmbeds(
                new EmbedBuilder()
                        .setTitle("Done Receiving Help?")
                        .setDescription("Please use the `/rep` command to give a reputation point to your helper to show your appreciation to them!")
                        .build()
        ).queue();
    }
}
