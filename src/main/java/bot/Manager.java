package bot;

import bot.commands.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;

public class Manager {
    Logger LOGGER = LoggerFactory.getLogger(Manager.class);

    private final Map<String, Command> commands = new HashMap<>();

    Manager() {
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
    }

    private void addCommand(Command c) {
        if (!commands.containsKey(c.getCommand())) {
            commands.put(c.getCommand(), c);
            LOGGER.info("Added " + c.getCommand() + " command");
        }
    }

    public Collection<Command> getCommands() {
        return commands.values();
    }

    public Command getCommand(String commandName) {
        if (commandName == null) {
            return null;
        }
        return commands.get(commandName);
    }

    void run(MessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw();
        if (!msg.startsWith(Main.getPrefix())) return;

        final String[] split = msg.replaceFirst("(?i)" + Pattern.quote(Main.getPrefix()), "").split("\\s+");
        final String command = split[0].toLowerCase();
        if (commands.containsKey(command)) {
            final List<String> args = Arrays.asList(split).subList(1, split.length);
            commands.get(command).run(args, event);
        }
    }
}
