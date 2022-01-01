package bot.commands;

import bot.Command;
import bot.Constants;
import bot.Database;
import bot.Tools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
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
        return "Adds a reputation point to a user! Give reputation points to people you recieve help from as a sign of appreaciation!\n" +
                "Usage: `" + Constants.PREFIX + getCommand() + " [user]`";
    }

    @Override
    public void run(List<String> args, MessageReceivedEvent event) {
        if(args.size() >= 1) {
            Member m = Tools.getEffectiveMember(event.getGuild(), String.join(" ", args));
            if(m == null) {
                event.getChannel().sendMessage("Could not find the member specified! Please try again with different parameters!").queue();
                return;
            }
            if(event.getMember().getIdLong() == m.getIdLong()) {
                event.getChannel().sendMessageEmbeds(new EmbedBuilder().setDescription("You can't give yourself a reputation point!").build()).queue();
                return;
            }
            if(timeout.containsKey(event.getMember().getIdLong()) && timeout.get(event.getMember().getIdLong()) + 3600000 > System.currentTimeMillis()) {
                event.getChannel().sendMessageEmbeds(new EmbedBuilder().setDescription("You can only use the `" + Constants.PREFIX + "rep` command once an hour!\nWait for " + Tools.secondsToTime((timeout.get(event.getMember().getIdLong()) + 3600000 - System.currentTimeMillis())/1000)).build()).queue();
                return;
            } else timeout.remove(event.getMember().getIdLong());
            Database.addReputation(m);
            if(Database.getReputationPoints(m) == 25) event.getGuild().addRoleToMember(m, event.getGuild().getRolesByName("Helper", true).get(0)).queue();
            timeout.put(event.getAuthor().getIdLong(), System.currentTimeMillis());
            event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                    .setAuthor(m.getEffectiveName(), m.getUser().getAvatarUrl(), m.getUser().getEffectiveAvatarUrl())
                    .addField("Reputation Points:", String.valueOf(Database.getReputationPoints(m)), false)
                    .addField("Point Added By:", event.getMember().getAsMention(), true)
                    .build()
            ).queue(ms -> ms.addReaction("\uD83D\uDC4D").queue());
        } else Tools.wrongUsage(event.getChannel(), this);
    }
}
