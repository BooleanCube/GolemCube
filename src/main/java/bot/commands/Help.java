package bot.commands;

import bot.Command;
import bot.Manager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Help implements Command {
    Manager manager;

    public Help(Manager m) {
        this.manager = m;
    }

    @Override
    public CommandData getCommandData() {
        return new CommandData("help", "Shows you a list of all the commands!");
    }

    @Override
    public void run(SlashCommandEvent event) {
        EmbedBuilder e = new EmbedBuilder()
                .setTitle("GolemCube Help");

        StringBuilder desc = new StringBuilder();
        manager.getCommands().forEach(command -> desc.append("`").append(command.getCommandData().getName()).append("`, "));
        String commandList = desc.substring(0, desc.length() - 2);

        e.addField("Commands", commandList, false);
        event.replyEmbeds(e.build()).queue();

        // I guess this would be unnecessary if we are using slash commands
        /*
        Command command = manager.getCommand(String.join("", args));
        if (command == null) {
            event.getChannel().sendMessage("The command `" + String.join("", args) + "` does not exist!\n" +
                    "Use `" + Main.getPrefix() + getCommand() + "` to get more information about me!").queue();
            return;
        }
        event.getChannel().sendMessage("Command help for `" + command.getCommand() + "`\n" +
                command.getHelp()).queue();*/
    }
}
