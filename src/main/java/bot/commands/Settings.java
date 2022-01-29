package bot.commands;

import bot.Command;
import bot.Main;
import bot.module.Module;
import bot.module.ModuleManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

@SuppressWarnings("ConstantConditions")
public class Settings implements Command {

    @Override
    public String getCategory() {
        return "Auto Moderation";
    }

    @Override
    public CommandData getCommandData() {
        return new CommandData("settings", "Shows all the settings for this guild!");
    }

    @Override
    public void run(SlashCommandEvent event) {
        if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) return;

        ModuleManager moduleManager = Main.getModuleManager();

        String[] modules = {"spam", "massmention", "link", "suggestion"};
        StringBuilder desc = new StringBuilder();
        for (String sModule : modules) {
            Module type = moduleManager.stringToModule(sModule);
            desc.append("`").append(sModule).append("`: ").append(moduleManager.isEnabled(type) ? "on\n" : "off\n");
        }

        event.replyEmbeds(new EmbedBuilder()
                .setTitle(event.getGuild().getName() + "'s Settings")
                .setDescription(desc.toString())
                .build()
        ).queue();
    }
}
