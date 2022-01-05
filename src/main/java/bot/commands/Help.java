package bot.commands;

import bot.Command;
import bot.Constants;
import bot.Manager;
import bot.Tools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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
    public void run(List<String> args, MessageReceivedEvent event) {
        if (args.size() > 1) {
            Tools.wrongUsage(event.getChannel(), this);
            return;
        }
        if (args.isEmpty()) {
            EmbedBuilder e = new EmbedBuilder()
                    .setTitle("GolemCube Help")
                    .setFooter("Use `" + Constants.PREFIX + getCommand() + " [command]` to get more info about a command!");
            StringBuilder desc = new StringBuilder();
            manager.getCommands().forEach(command -> desc.append("`").append(command.getCommand()).append("`, "));
            String commandList = desc.substring(0, desc.length()-2);
            e.addField("Commands", commandList, false);
            e.addField("Auto Moderation", "**Mass Mention Control**:\n" +
                    "> 10 pings in 5 seconds is results in a ban!\n" +
                    "**Message Spam Control**:\n" +
                    "> 9 messages in 3 seconds will result in a mute and a warning!\n" +
                    "**Link Control**:\n" +
                    "> You can't advertise discord servers in channels other than #self-promotion \n", false);
            event.getChannel().sendMessageEmbeds(e.build()).queue();
            return;
        }
        Command command = manager.getCommand(String.join("", args));
        if (command == null) {
            event.getChannel().sendMessage("The command `" + String.join("", args) + "` does not exist!\n" +
                    "Use `" + Constants.PREFIX + getCommand() + "` to get more information about me!").queue();
            return;
        }
        event.getChannel().sendMessage("Command help for `" + command.getCommand() + "`\n" +
                command.getHelp()).queue();
    }
}
