package bot.listeners.moderation;

import bot.Main;
import bot.module.Module;
import bot.module.ModuleManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

//TODO: Still thinking on the name
public abstract class ModuleController extends ListenerAdapter {
    private final Module module;
    private final ModuleManager moduleManager = Main.getModuleManager();

    public ModuleController(Module module) {
        this.module = module;
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (moduleManager.isEnabled(module) && !event.getAuthor().isBot() && event.isFromGuild()) {
            check(event);
        }
    }

    public abstract void check(MessageReceivedEvent event);
}
