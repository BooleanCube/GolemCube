package bot.commands;

import bot.Command;
import bot.Constants;
import bot.Tools;
import bot.Warning;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class Warn implements Command {
    @Override
    public String getCommand() {
        return "warn";
    }

    @Override
    public String getHelp() {
        return "Warns a member!\n" +
                "Usage: `" + Constants.PREFIX + getCommand() + " <member> <reason>`";
    }

    @Override
    public void run(List<String> args, MessageReceivedEvent event) {
        if (event.getMember().getRoles().contains(event.getGuild().getRoleById(773337238952083477L))) {
            try {
                long id = Long.parseLong(args.get(0).replaceAll("<@", "").replaceAll(">", "").replaceAll("!", "")); //checks to see if the first argument is a ping or a number(id)
                ArrayList<Warning> n = new ArrayList<>();
                if (args.size() == 1) {
                    n.add(new Warning(String.join(" ", "No reason provided!"), System.currentTimeMillis()));
                    Tools.memberToWarns.putIfAbsent(event.getGuild().getMemberById(id), n);
                    if (Tools.memberToWarns.containsKey(event.getGuild().getMemberById(id)))
                        Tools.memberToWarns.get(event.getGuild().getMemberById(id)).add(new Warning("No reason provided!", System.currentTimeMillis()));
                    event.getChannel().sendMessage("Successfully warned " + event.getGuild().getMemberById(id).getAsMention() + " for `" + "No reason provided!" + "`").queue();
                    if (Tools.memberToWarns.get(event.getGuild().getMemberById(id)).size() >= 4) {
                        event.getChannel().sendMessage("Banned " + event.getGuild().getMemberById(id).getAsMention() + " from the server because they exceeded `3 warnings`!").queue();
                        event.getGuild().getMemberById(id).ban(7, "Exceeded 3 warnings!").queue();
                    }
                } else {
                    n.add(new Warning(String.join(" ", args.subList(1, args.size())), System.currentTimeMillis()));
                    Tools.memberToWarns.putIfAbsent(event.getGuild().getMemberById(id), n);
                    if (Tools.memberToWarns.containsKey(event.getGuild().getMemberById(id)))
                        Tools.memberToWarns.get(event.getGuild().getMemberById(id)).add(new Warning(String.join(" ", args.subList(1, args.size())), System.currentTimeMillis()));
                    event.getChannel().sendMessage("Successfully warned " + event.getGuild().getMemberById(id).getAsMention() + " for `" + String.join(" ", args.subList(1, args.size())) + "`").queue();
                    if (Tools.memberToWarns.get(event.getGuild().getMemberById(id)).size() >= 3) {
                        event.getChannel().sendMessage("Banned " + event.getGuild().getMemberById(id).getAsMention() + " from the server because they exceeded `3 warnings`!").queue();
                        event.getGuild().getMemberById(id).ban(7, "Exceeded 3 warnings!").queue();
                    }
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
