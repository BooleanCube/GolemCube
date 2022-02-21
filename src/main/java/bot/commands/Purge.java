package bot.commands;

import bot.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("ConstantConditions")
public class Purge implements Command {

    @Override
    public String getCategory() {
        return "Moderation";
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("purge", "Purges/Deletes the number of messages given!")
                .addOption(OptionType.INTEGER, "number", "Number of messages to delete", true);
    }

    @Override
    public void run(SlashCommandInteractionEvent event) {
        if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            event.reply("You don't have the **MESSAGE_MANAGE** permission!").setEphemeral(true).queue();
            return;
        }

        int num = Math.abs(Math.toIntExact(event.getOption("number").getAsLong()));

        int hundreds = num / 100;
        if (hundreds == 0) {
            event.getChannel().getHistory().retrievePast(num).queue(msgs -> {
                event.getChannel().purgeMessages(msgs);
                event.reply("Successfully purged `" + num + "` messages.").setEphemeral(true).queue();
            });
            return;
        }

        AtomicInteger atomicNum = new AtomicInteger(num);
        try {
            for (int i = 0; i <= hundreds; i++) {
                if (atomicNum.get() < 100) {
                    event.getChannel().getHistory().retrievePast(atomicNum.get()).queue(msgs -> {
                        event.getChannel().purgeMessages(msgs);
                        event.reply("Successfully purged `" + num + "` messages.").setEphemeral(true).queue();
                    });
                } else {
                    event.getChannel().getHistory().retrievePast(100).queue(msgs -> {
                        event.getChannel().purgeMessages(msgs);
                        atomicNum.getAndAdd(-100);
                    });
                }
            }
        } catch (Exception e) {
            event.reply("I came across an error while purging! I deleted as many as I could!").setEphemeral(true).queue();
        }
    }
}
