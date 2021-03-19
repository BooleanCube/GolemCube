package bot.commands;

import bot.Command;
import bot.Constants;
import bot.Manager;
import bot.Tools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class Help implements Command {
    Manager manager;
    public Help(Manager m) {
        this.manager = m;
    }
    @Override
    public String getCommand() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "Shows you a list of all the commands!\n" +
                "Usage: `" + Constants.PREFIX + getCommand() + " <command(optional)>`";
    }

    @Override
    public void run(List<String> args, GuildMessageReceivedEvent event) {
        if(args.size() > 1) {
            Tools.wrongUsage(event.getChannel(), this);
            return;
        }
        if(args.isEmpty()) {
            EmbedBuilder e = new EmbedBuilder()
                    .setTitle("A list of all my commands:");
            StringBuilder desc = e.getDescriptionBuilder();
            manager.getCommands().forEach(command -> {
                desc.append("`").append(command.getCommand()).append("`\n");
            });
            event.getChannel().sendMessage(e.build()).queue();
            return;
        }
        Command command = manager.getCommand(String.join("", args));
        if(command == null) {
            event.getChannel().sendMessage("The command `" + String.join("", args) + "` does not exist!\n" +
                    "Use `" + Constants.PREFIX + command.getCommand() + "` for a list of all my commands!").queue();
            return;
        }
        event.getChannel().sendMessage("Command help for `" + command.getCommand() + "`\n" +
                command.getHelp()).queue();
    }
}
