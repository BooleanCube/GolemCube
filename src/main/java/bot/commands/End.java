package bot.commands;

import bot.Command;
import bot.Constants;
import bot.Tools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class End implements Command {
    @Override
    public String getCommand() {
        return "end";
    }

    @Override
    public String getHelp() {
        return "After helping somebody, use this command if you want to remind them to give you a reputation point!\n" +
                "Usage: `" + Constants.PREFIX + getCommand() + "`";
    }

    @Override
    public void run(List<String> args, GuildMessageReceivedEvent event) {
        if(args.isEmpty()) {
            event.getChannel().sendMessage(
                    new EmbedBuilder()
                            .setTitle("Done Recieving Help?")
                            .setDescription("Please use the `" + Constants.PREFIX + "rep` command to give a reputation point to your helper to show your appreciation to them!")
                            .build()
            ).queue();
        } else Tools.wrongUsage(event.getChannel(), this);
    }
}
