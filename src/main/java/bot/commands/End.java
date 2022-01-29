package bot.commands;

import bot.Command;
import bot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class End implements Command {

    @Override
    public String getCategory() {
        return "Reputation";
    }
    
    @Override
    public CommandData getCommandData() {
        return new CommandData("end", "After helping somebody, use this command if you want to remind them to give you a reputation point!");
    }

    @Override
    public void run(SlashCommandEvent event) {
        event.replyEmbeds(
                new EmbedBuilder()
                        .setTitle("Done Receiving Help?")
                        .setDescription("Please use the `/rep` command to give a reputation point to your helper to show your appreciation to them!")
                        .build()
        ).queue();
    }
}
