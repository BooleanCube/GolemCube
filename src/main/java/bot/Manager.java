package bot;

import bot.commands.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"ConstantConditions"})
public class Manager {
    private final Logger LOGGER = LoggerFactory.getLogger(Manager.class);
    private final Map<String, Command> commands = new HashMap<>();
    private final Map<String, String> commandIds = new HashMap<>();

    Manager(JDA jda) {
        //commands
        addCommand(new Ping());
        addCommand(new Help(this));
        addCommand(new Warn());
        addCommand(new Kick());
        addCommand(new Ban());
        addCommand(new Mute());
        addCommand(new Unmute());
        addCommand(new Purge());
        addCommand(new Reputation());
        addCommand(new End());
        addCommand(new Points());
        addCommand(new ReputationLeaderboard());
        addCommand(new Toggle());
        addCommand(new Settings());

        List<CommandData> commands = this.commands.values().stream().map(Command::getCommandData).collect(Collectors.toList());
        commands.add(new CommandData("shutdown", "Shuts down the bot.").setDefaultEnabled(false));

        Guild guild = jda.getGuildById(Main.getMainServerId());

        // Update Commands only in our guild
        jda.updateCommands().addCommands(commands).queue(e -> {
            e.forEach(it -> commandIds.put(it.getName(), it.getId()));

            guild.updateCommandPrivilegesById(commandIds.get("shutdown"),
                    CommandPrivilege.enableUser(Main.getOwnerId())).queue();
        });
    }

    private void addCommand(Command c) {
        String name = c.getCommandData().getName();

        if (!commands.containsKey(name)) {
            commands.put(name, c);
            LOGGER.info("Added " + name + " command");
        }
    }

    public Collection<Command> getCommands() {
        return commands.values();
    }

    public Collection<Command> getCommands(String category) {
        return commands.values().stream().filter(cmd -> cmd.getCategory().equalsIgnoreCase(category)).collect(Collectors.toList());
    }

    void run(SlashCommandEvent event) {
        commands.get(event.getName()).run(event);
    }
}
