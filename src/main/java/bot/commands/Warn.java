package bot.commands;

import bot.Command;
import bot.Main;
import bot.Tools;
import bot.Warning;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class Warn implements Command {
    @Override
    public String getCommand() {
        return "warn";
    }

    @Override
    public String getHelp() {
        return "Warns a member!\n" +
            "Usage: `" + Main.getPrefix() + getCommand() + " <member> <reason>`";
    }

    @Override
    public void run(List<String> args, MessageReceivedEvent event) {
        if (event.getMember().getRoles().contains(event.getGuild().getRoleById(773337238952083477L))) {
            try {
                String id = Long.getLong(args.get(0).replaceAll("[<@!>]", "")).toString(); //checks to see if the first argument is a ping or a number(id)
                List<Warning> warns = Tools.getWarns(id);
                String reason = args.size() == 1 ? "No reason provided!" : String.join(" ", args.subList(1, args.size()));
                warns.add(new Warning(reason, System.currentTimeMillis()));
                event.getChannel().sendMessage("Successfully warned <@" + id + "> for `" + reason + "`").queue();
                if (warns.size() >= 4) {
                    event.getChannel().sendMessage("Banned <@" + id + "> from the server because they exceeded `3 warnings`!").queue();
                    event.getGuild().ban(id, 7, "Exceeded 3 warnings!").queue();
                }
            } catch (Exception e) {
                Tools.wrongUsage(event.getChannel(), this);
                e.printStackTrace();
            }
        } else {
            event.getChannel().sendMessage("You do not have the permission to use this command!").queue();
        }
    }
}
