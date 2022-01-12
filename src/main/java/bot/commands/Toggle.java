package bot.commands;

import bot.Command;
import bot.Main;
import bot.Tools;
import bot.module.Module;
import bot.module.ModuleManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class Toggle implements Command {
    @Override
    public String getCommand() {
        return "toggle";
    }

    @Override
    public String getHelp() {
        return "Enables a setting in the automoderator discord bot!\nUsage: `" + Main.getPrefix() + getCommand() + " [feature]`";
    }

    @Override
    public void run(List<String> args, MessageReceivedEvent event) {
        if (!Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_SERVER)) return;
        if (args.size() != 1) {
            Tools.wrongUsage(event.getTextChannel(), this);
            return;
        }

        ModuleManager moduleManager = Main.getModuleManager();

        String moduleByUser = String.join(" ", args);
        Module type = moduleManager.stringToModule(moduleByUser);
        if (type == null) {
            event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                    .setDescription(moduleByUser + " is not a valid setting to toggle!")
                    .setColor(Color.red)
                    .build()
            ).queue();
            return;
        }
        moduleManager.toggle(type);
        String status = moduleManager.isEnabled(type) ? "on" : "off";
        event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                .setDescription(type.getName() + " was toggled `" + status + "`")
                .build()
        ).queue();
    }
}
