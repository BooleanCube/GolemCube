package bot.commands;

import bot.Command;
import bot.Main;
import bot.Tools;
import bot.database.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;

public class Reputation implements Command {
    static HashMap<Long, Long> timeout = new HashMap<>();

    @Override
    public String getCommand() {
        return "rep";
    }

    @Override
    public String getHelp() {
        return "Adds a reputation point to a user! Give reputation points to people you receive help from as a sign of appreaciation!\n" +
                "Usage: `" + Main.getPrefix() + getCommand() + " [user]`";
    }

    @Override
    public void run(List<String> args, MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        if (args.size() == 0) {
            Tools.wrongUsage(channel, this);
            return;
        }
        Member m = Tools.getEffectiveMember(event.getGuild(), String.join(" ", args));
        if (m == null) {
            channel.sendMessage("Could not find the member specified! Please try again with different parameters!").queue();
            return;
        }
        long userId = event.getAuthor().getIdLong();
        if (userId == m.getIdLong()) {
            channel.sendMessageEmbeds(new EmbedBuilder().setDescription("You can't give yourself a reputation point!").build()).queue();
            return;
        }
        Long time = timeout.get(userId);
        long currentTime = System.currentTimeMillis();
        if (time != null && time + 7200000 > currentTime) {
            channel.sendMessageEmbeds(new EmbedBuilder().setDescription("You can only use the `" + Main.getPrefix() + "rep` command once in 2 hours!\nWait for " + Tools.secondsToTime((time + 7200000 - currentTime) / 1000)).build()).queue();
            return;
        }
        int reps = Database.addReputation(m);
        if (reps == 25)
            event.getGuild().addRoleToMember(m, event.getGuild().getRolesByName("Helper", true).get(0)).queue();
        timeout.put(userId, currentTime);
        channel.sendMessageEmbeds(new EmbedBuilder()
                .setAuthor(m.getEffectiveName(), m.getUser().getAvatarUrl(), m.getUser().getEffectiveAvatarUrl())
                .addField("Reputation Points:", String.valueOf(reps), false)
                .addField("Point Added By:", event.getAuthor().getAsMention(), true)
                .build()
        ).flatMap(ms -> ms.addReaction("\uD83D\uDC4D")).queue();

    }
}
