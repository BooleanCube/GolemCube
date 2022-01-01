package bot.commands;

import bot.Command;
import bot.Constants;
import bot.Tools;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Purge implements Command {

    @Override
    public String getCommand() {
        return "purge";
    }

    @Override
    public String getHelp() {
        return "Purges/Deletes the number of messages given!\n" +
                "Usage: `" + Constants.PREFIX + getCommand() + " [num]`";
    }

    @Override
    public void run(List<String> args, MessageReceivedEvent event) {
        if (args.size() == 1) {
            if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                event.getChannel().sendMessage("You don't have the **MESSAGE_MANAGE** permission!").queue();
                return;
            }
            event.getMessage().delete().complete();
            int num;
            try {
                num = Integer.parseInt(args.get(0));
            } catch (NumberFormatException nfe) {
                Tools.wrongUsage(event.getChannel(), this);
                return;
            }
            int currentNum = num / 100;
            if (currentNum == 0) {
                List<Message> msg = event.getChannel().getHistory().retrievePast(num).complete();
                event.getChannel().purgeMessages(msg);
                event.getChannel().sendMessage("Successfully purged `" + num + "` messages.").queue(m -> m.delete().queueAfter(3, TimeUnit.SECONDS));
                return;
            }
            try {
                for (int i = 0; i <= currentNum; i++) {
                    if (i == num) {
                        List<Message> msg = event.getChannel().getHistory().retrievePast(num).complete();
                        event.getChannel().purgeMessages(msg);
                        event.getChannel().sendMessage("Successfully purged `" + num + "` messages.").queue(m -> m.delete().queueAfter(3, TimeUnit.SECONDS));
                    } else {
                        List<Message> msg = event.getChannel().getHistory().retrievePast(100).complete();
                        event.getChannel().purgeMessages(msg);
                        num -= 100;
                    }
                }
            } catch (Exception e) {
                event.getChannel().sendMessage("I came across an error while purging! I deleted as many as I could!").queue();
            }
        } else {
            Tools.wrongUsage(event.getChannel(), this);
        }
    }

}