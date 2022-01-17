package bot.commands;

import bot.Command;
import bot.Main;
import bot.Tools;
import bot.module.Module;
import bot.module.ModuleManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Objects;

public class Settings implements Command {
    @Override
    public String getCommand() {
        return "settings";
    }

    @Override
    public String getHelp() {
        return "Shows all the settings for this guild!\nUsage: `" + Main.getPrefix() + getCommand() + "`";
    }

    @Override
    public void run(List<String> args, MessageReceivedEvent event) {
        if (!Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_SERVER)) return;
        if (args.size() != 0) {
            Tools.wrongUsage(event.getTextChannel(), this);
            return;
        }

        ModuleManager moduleManager = Main.getModuleManager();

        String[] modules = {"spam", "massmention", "link"};
        StringBuilder desc = new StringBuilder();
        for(String sModule : modules) {
            Module type = moduleManager.stringToModule(sModule);
            desc.append("`").append(sModule).append("`: ").append(moduleManager.isEnabled(type) ? "on\n" : "off\n");
        }
        event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                .setTitle(event.getGuild().getName() + "'s Settings")
                .setDescription(desc.toString())
                .build()
        ).queue();
    }
}
