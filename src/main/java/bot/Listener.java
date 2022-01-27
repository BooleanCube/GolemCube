package bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class Listener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    public final Manager m;

    public Listener(JDA jda) {
        m = new Manager(jda);
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        LOGGER.info(event.getJDA().getSelfUser().getName() + " is online!");
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (event.getName().equals("shutdown")) {
            if (event.getUser().getId().equals(Main.getOwnerId())) {
                event.deferReply().queue();
                JDA jda = event.getJDA();
                jda.shutdown();
                jda.getHttpClient().connectionPool().evictAll();
                jda.getHttpClient().dispatcher().executorService().shutdown();
            } else {
                event.reply("You can't shutdown this bot.").setEphemeral(true).queue();
            }
            return;
        }

        m.run(event);
    }
}
