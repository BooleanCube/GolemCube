package bot.listeners.moderation;

import bot.module.Module;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Duration;

public class LinkControl extends ModuleController {
    private final Duration delay = Duration.ofSeconds(5);

    public LinkControl() {
        super(Module.LINK_CONTROL);
    }

    public void check(MessageReceivedEvent event) {
        if (!event.getChannel().getName().contains("promotion") && !event.getMessage().getInvites().isEmpty()) {
            event.getMessage().delete().queue();
            event.getChannel()
                .sendMessage("No advertising in channels other than <#743858602997186612> please!")
                .flatMap(Message::delete)
                .delay(delay) // delay the delete for 5 Seconds
                .queue();
        }
    }
}
