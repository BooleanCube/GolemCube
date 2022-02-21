package bot.commands;

import bot.Command;
import bot.Manager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class Help implements Command {

    @Override
    public String getCategory() {
        return "Miscellaneous";
    }

    Manager manager;

    public Help(Manager m) {
        this.manager = m;
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("help", "Shows you a list of all the commands!");
    }

    @Override
    public void run(SlashCommandInteractionEvent event) {
        EmbedBuilder e = new EmbedBuilder()
                .setTitle("GolemCube Help");

        StringBuilder rep = new StringBuilder();
        StringBuilder auto = new StringBuilder();
        StringBuilder mod = new StringBuilder();
        StringBuilder misc = new StringBuilder();
        manager.getCommands("Moderation").forEach(command -> mod.append("`").append(command.getCommandData().getName()).append("`, "));
        manager.getCommands("Auto Moderation").forEach(command -> auto.append("`").append(command.getCommandData().getName()).append("`, "));
        manager.getCommands("Reputation").forEach(command -> rep.append("`").append(command.getCommandData().getName()).append("`, "));
        manager.getCommands("Miscellaneous").forEach(command -> misc.append("`").append(command.getCommandData().getName()).append("`, "));

        e.addField("Reputation", rep.substring(0, rep.length() - 2), false);
        e.addField("Auto Moderation", auto.substring(0, auto.length() - 2), false);
        e.addField("Moderation", mod.substring(0, mod.length() - 2), false);
        e.addField("Miscellaneous", misc.substring(0, misc.length() - 2), false);

        e.setThumbnail("https://cdn.discordapp.com/attachments/936805914667810817/936806049002946620/GolemCube.png");

        event.replyEmbeds(e.build()).queue();
    }
}
