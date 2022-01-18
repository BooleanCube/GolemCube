package bot;

import bot.config.Config;
import bot.config.ConfigManager;
import bot.config.ModuleConfig;
import bot.database.Database;
import bot.listeners.ReputationLeaderboardButtons;
import bot.listeners.Suggestions;
import bot.listeners.moderation.LinkControl;
import bot.listeners.moderation.MassMentionControl;
import bot.listeners.moderation.SpamControl;
import bot.module.ModuleManager;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    private static String PREFIX;
    private static String OWNER_ID;

    private static ModuleManager moduleManager;

    public static void main(String[] args) throws LoginException, InterruptedException {
        Path configPath = Paths.get("config"); // TODO: Set the language level to 17 pls xD

        // Bot Config
        ConfigManager<Config> configManager = ConfigManager.create(configPath, "config.yml", Config.class);
        // Module Config
        ConfigManager<ModuleConfig> moduleConfigManager = ConfigManager.create(configPath, "modules.yml", ModuleConfig.class);

        configManager.reloadConfig();
        moduleConfigManager.reloadConfig();

        // Create Reload Config Command if you want
        Config config = configManager.getConfigData();
        PREFIX = config.prefix();
        OWNER_ID = config.ownerID();

        moduleManager = new ModuleManager(moduleConfigManager);

        Database.setupDatabase(config.databaseURI());
        JDABuilder.createDefault(config.token())
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGES)
                .setActivity(Activity.watching("over this village! | " + PREFIX + "help"))
                .addEventListeners(
                        new Database(),
                        new Listener(),
                        new SpamControl(config),
                        new MassMentionControl(),
                        new LinkControl(),
                        new ReputationLeaderboardButtons(),
                        new Suggestions(config.suggestionChannelID())
                )
                .build().awaitReady();
    }

    public static String getPrefix() {
        return PREFIX;
    }

    public static String getOwnerId() {
        return OWNER_ID;
    }

    public static ModuleManager getModuleManager() {
        return moduleManager;
    }

}
