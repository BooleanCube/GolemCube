package bot;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class Listener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    public final Manager m = new Manager();

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        LOGGER.info(event.getJDA().getSelfUser().getName() + " is online!");
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equalsIgnoreCase(Main.getPrefix() + "shutdown") && (event.getAuthor().getId().equals(Main.getOwnerId()))) {
            event.getJDA().shutdown();
            System.exit(0);
        }
        m.run(event);
    }

    @Override
    public void onShutdown(@Nonnull ShutdownEvent event) {
        Main.getModuleManager().writeConfig();
    }
}
