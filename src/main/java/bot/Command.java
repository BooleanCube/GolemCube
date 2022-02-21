package bot;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface Command {
    SlashCommandData getCommandData();
    String getCategory();
    void run(SlashCommandInteractionEvent event);
}
