package bot.module;

import bot.config.ConfigManager;
import bot.config.ModuleConfig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ModuleManager {
    private final ConfigManager<ModuleConfig> configManager;
    private final Map<Integer, Boolean> modules = new HashMap<>();

    public ModuleManager(ConfigManager<ModuleConfig> moduleConfigManager) {
        ModuleConfig config = moduleConfigManager.getConfigData();
        modules.put(1, config.spamControl());
        modules.put(2, config.massMentionControl());
        modules.put(3, config.linkControl());
        modules.put(4, config.suggestionListener());

        configManager = moduleConfigManager;
    }

    public void toggle(Module module) {
        int id = module.getID();
        modules.put(id, !modules.get(id));
    }

    public boolean isEnabled(Module module) {
        return modules.getOrDefault(module.getID(), false);
    }

    public Module stringToModule(String name) {
        return Arrays.stream(Module.values())
                .filter(it -> Arrays.stream(it.getAliases())
                        .anyMatch(a -> a.equalsIgnoreCase(name)) ||
                        it.getName().equalsIgnoreCase(name))
                .findAny().orElse(null);
    }

    public void writeConfig() {
        configManager.writeConfig(new ModuleConfig() {
            @Override
            public boolean spamControl() {
                return modules.get(1);
            }

            @Override
            public boolean massMentionControl() {
                return modules.get(2);
            }

            @Override
            public boolean linkControl() {
                return modules.get(3);
            }

            @Override
            public boolean suggestionListener() {
                return modules.get(4);
            }
        });
    }
}
