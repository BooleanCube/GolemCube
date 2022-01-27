package bot.commands;

import bot.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("ConstantConditions")
public class Purge implements Command {
    @Override
    public CommandData getCommandData() {
        return new CommandData("purge", "Purges/Deletes the number of messages given!")
                .addOption(OptionType.INTEGER, "number", "Number of messages to delete", true);
    }

    @Override
    public void run(SlashCommandEvent event) {
        if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            event.reply("You don't have the **MESSAGE_MANAGE** permission!").setEphemeral(true).queue();
            return;
        }

        int num = Math.abs(Math.toIntExact(event.getOption("number").getAsLong()));

        int hundreds = num / 100;
        if (hundreds == 0) {
            event.getChannel().getHistory().retrievePast(num).queue(msgs -> {
                event.getChannel().purgeMessages(msgs);
                event.reply("Successfully purged `" + num + "` messages.")
                        .queue(m -> m.deleteOriginal().queueAfter(5, TimeUnit.SECONDS));
            });
            return;
        }

        AtomicInteger atomicNum = new AtomicInteger(num);
        try {
            for (int i = 0; i <= hundreds; i++) {
                if (atomicNum.get() < 100) {
                    event.getChannel().getHistory().retrievePast(atomicNum.get()).queue(msgs -> {
                        event.getChannel().purgeMessages(msgs);
                        event.getChannel().sendMessage("Successfully purged `" + num + "` messages.")
                                .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
                    });
                } else {
                    event.getChannel().getHistory().retrievePast(100).queue(msgs -> {
                        event.getChannel().purgeMessages(msgs);
                        atomicNum.getAndAdd(-100);
                    });
                }
            }
        } catch (Exception e) {
            event.reply("I came across an error while purging! I deleted as many as I could!")
                    .queue(m -> m.deleteOriginal().queueAfter(5, TimeUnit.SECONDS));
        }
    }
}