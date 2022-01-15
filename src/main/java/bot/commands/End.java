package bot.commands;

import bot.Command;
import bot.Main;
import bot.Tools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class End implements Command {
    @Override
    public String getCommand() {
        return "end";
    }

    @Override
    public String getHelp() {
        return "After helping somebody, use this command if you want to remind them to give you a reputation point!\n" +
                "Usage: `" + Main.getPrefix() + getCommand() + "`";
    }

    @Override
    public void run(List<String> args, MessageReceivedEvent event) {
        if (args.isEmpty()) {
            event.getChannel().sendMessageEmbeds(
                    new EmbedBuilder()
                            .setTitle("Done Recieving Help?")
                            .setDescription("Please use the `" + Main.getPrefix() + "rep` command to give a reputation point to your helper to show your appreciation to them!")
                            .build()
            ).queue();
        } else Tools.wrongUsage(event.getChannel(), this);
    }
}
