package bot.commands;

import bot.Command;
import bot.Main;
import bot.database.Database;
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
        return "Adds a reputation point to a user! Give reputation points to people you receive help from as a sign of appreaciation!\n" +
                "Usage: `" + Main.getPrefix() + getCommand() + " [user]`";
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
            if(timeout.containsKey(event.getMember().getIdLong()) && timeout.get(event.getMember().getIdLong()) + 7200000 > System.currentTimeMillis()) {
                event.getChannel().sendMessageEmbeds(new EmbedBuilder().setDescription("You can only use the `" + Main.getPrefix() + "rep` command once in 2 hours!\nWait for " + Tools.secondsToTime((timeout.get(event.getMember().getIdLong()) + 7200000 - System.currentTimeMillis())/1000)).build()).queue();
                return;
            } else timeout.remove(event.getMember().getIdLong());
            Database.addReputation(m);
            if(Database.getReputation(m) == 25) event.getGuild().addRoleToMember(m, event.getGuild().getRolesByName("Helper", true).get(0)).queue();
            timeout.put(event.getAuthor().getIdLong(), System.currentTimeMillis());
            event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                    .setAuthor(m.getEffectiveName(), m.getUser().getAvatarUrl(), m.getUser().getEffectiveAvatarUrl())
                    .addField("Reputation Points:", String.valueOf(Database.getReputation(m)), false)
                    .addField("Point Added By:", event.getMember().getAsMention(), true)
                    .build()
            ).queue(ms -> ms.addReaction("\uD83D\uDC4D").queue());
        } else Tools.wrongUsage(event.getChannel(), this);
    }
}
