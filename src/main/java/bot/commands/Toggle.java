package bot.commands;

import bot.Command;
import bot.Main;
import bot.module.Module;
import bot.module.ModuleManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.stream.Collectors;

import static net.dv8tion.jda.api.interactions.commands.Command.Choice;

@SuppressWarnings({"ConstantConditions"})
public class Toggle implements Command {

    @Override
    public String getCategory() {
        return "Auto Moderation";
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("toggle", "Toggles a module.")
                .addOptions(
                        new OptionData(OptionType.STRING, "module", "The module you want to toggle.", true)
                                .addChoices(Arrays.stream(Module.values())
                                        .map(it -> new Choice(it.getName(), it.getName()))
                                        .collect(Collectors.toList()))
                );
    }

    @Override
    public void run(SlashCommandInteractionEvent event) {
        if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) return;

        String module = event.getOption("module").getAsString();
        ModuleManager moduleManager = Main.getModuleManager();

        Module type = moduleManager.stringToModule(module);
        if (type == null) {
            event.reply(module + " is not a valid setting to toggle!").setEphemeral(true).queue();
            return;
        }

        moduleManager.toggle(type);
        String status = moduleManager.isEnabled(type) ? "on" : "off";
        event.replyEmbeds(new EmbedBuilder()
                .setDescription(type.getName() + " was toggled `" + status + "`")
                .build()
        ).queue();
        moduleManager.writeConfig();
    }
}
