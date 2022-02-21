package bot.commands;

import bot.Command;
import bot.Tools;
import bot.database.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.HashMap;

@SuppressWarnings("ConstantConditions")
public class Reputation implements Command {

    @Override
    public String getCategory() {
        return "Reputation";
    }

    private static final HashMap<Long, Long> timeout = new HashMap<>();

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("rep", "Adds a reputation point to a user! Give reputation points to people as a sign of appreciation!")
                .addOption(OptionType.USER, "user", "The user who helped you ;)", true);
    }

    @Override
    public void run(SlashCommandInteractionEvent event) {
        Member m = event.getOption("user").getAsMember();
        long userId = event.getMember().getIdLong();
        if (userId == m.getIdLong()) {
            event.reply("You can't give yourself a reputation point!").setEphemeral(true).queue();
            return;
        }

        Long time = timeout.get(userId);
        long currentTime = System.currentTimeMillis();
        if (time != null && time + 7200000 > currentTime) {
            event.reply("You can only use the `/rep` command once in 2 hours!\n" +
                            "Wait for " + Tools.secondsToTime((time + 7200000 - currentTime) / 1000))
                    .setEphemeral(true).queue();
            return;
        }
        int reps = Database.addReputation(m);
        if (reps == 25)
            event.getGuild().addRoleToMember(m, event.getGuild().getRolesByName("Helper", true).get(0)).queue();

        timeout.put(userId, currentTime);
        event.replyEmbeds(new EmbedBuilder()
                .setAuthor(m.getEffectiveName(), m.getUser().getAvatarUrl(), m.getUser().getEffectiveAvatarUrl())
                .addField("Reputation Points:", String.valueOf(reps), false)
                .addField("Point Added By:", event.getMember().getAsMention(), true)
                .build()
        ).queue();
    }
}
