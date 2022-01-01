package bot;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class Listener extends ListenerAdapter {
    Logger LOGGER = LoggerFactory.getLogger(Listener.class);

    public final Manager m = new Manager();

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        LOGGER.info(event.getJDA().getSelfUser().getName() + " is online!");
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equalsIgnoreCase(Constants.PREFIX + "shutdown") && (event.getAuthor().getIdLong() == Constants.OWNERID)) {
            event.getJDA().shutdown();
            System.exit(0);
        }
        m.run(event);
    }
}
