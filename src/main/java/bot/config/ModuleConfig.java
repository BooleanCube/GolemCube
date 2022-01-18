package bot.config;

import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;

public interface ModuleConfig {
    @ConfKey("spam-control")
    @ConfDefault.DefaultBoolean(true)
    boolean spamControl();

    @ConfKey("mass-mention-control")
    @ConfDefault.DefaultBoolean(true)
    boolean massMentionControl();

    @ConfKey("link-control")
    @ConfDefault.DefaultBoolean(true)
    boolean linkControl();

    @ConfKey("suggestionListener")
    @ConfDefault.DefaultBoolean(true)
    boolean suggestionListener();
}
