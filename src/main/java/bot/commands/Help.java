package bot.commands;

import bot.Command;
import bot.Manager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

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
    public CommandData getCommandData() {
        return new CommandData("help", "Shows you a list of all the commands!");
    }

    @Override
    public void run(SlashCommandEvent event) {
        EmbedBuilder e = new EmbedBuilder()
                .setTitle("GolemCube Help");

        StringBuilder rep = new StringBuilder();
        StringBuilder auto = new StringBuilder();
        StringBuilder mod = new StringBuilder();
        manager.getCommands("Moderation").forEach(command -> mod.append("`").append(command.getCommandData().getName()).append("`, "));
        manager.getCommands("Auto Moderation").forEach(command -> auto.append("`").append(command.getCommandData().getName()).append("`, "));
        manager.getCommands("Reputation").forEach(command -> rep.append("`").append(command.getCommandData().getName()).append("`, "));

        e.addField("Reputation", rep.substring(0, rep.length() - 2), false);
        e.addField("Auto Moderation", auto.substring(0, auto.length() - 2), false);
        e.addField("Moderation", mod.substring(0, mod.length() - 2), false);

        e.setThumbnail(event.getGuild().getSelfMember().getAvatarUrl());

        event.replyEmbeds(e.build()).queue();
    }
}
