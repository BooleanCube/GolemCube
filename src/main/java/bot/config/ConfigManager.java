package bot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.arim.dazzleconf.ConfigurationFactory;
import space.arim.dazzleconf.ConfigurationOptions;
import space.arim.dazzleconf.error.ConfigFormatSyntaxException;
import space.arim.dazzleconf.error.InvalidConfigException;
import space.arim.dazzleconf.ext.snakeyaml.CommentMode;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlConfigurationFactory;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlOptions;
import space.arim.dazzleconf.helper.ConfigurationHelper;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

public final class ConfigManager<C> {

    private final ConfigurationHelper<C> configHelper;
    private final Path filePath;
    private volatile C configData;

    private final Logger LOGGER = LoggerFactory.getLogger(ConfigManager.class);

    private ConfigManager(ConfigurationHelper<C> configHelper, Path filePath) {
        this.configHelper = configHelper;
        this.filePath = filePath;
    }

    public static <C> ConfigManager<C> create(Path configFolder, String fileName, Class<C> configClass) {
        SnakeYamlOptions yamlOptions = new SnakeYamlOptions.Builder()
                .commentMode(CommentMode.alternativeWriter())
                .build();

        ConfigurationOptions configOptions = new ConfigurationOptions.Builder()
                .sorter(new AnnotationBasedSorter()).build();

        ConfigurationFactory<C> configFactory = SnakeYamlConfigurationFactory.create(
                configClass,
                configOptions,
                yamlOptions);
        return new ConfigManager<>(new ConfigurationHelper<>(configFolder, fileName, configFactory), configFolder.resolve(fileName));
    }

    public void reloadConfig() {
        try {
            configData = configHelper.reloadConfigData();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        } catch (ConfigFormatSyntaxException ex) {
            configData = configHelper.getFactory().loadDefaults();
            System.err.println("The yaml syntax in your configuration is invalid. "
                    + "Check your YAML syntax with a tool such as https://yaml-online-parser.appspot.com/");
            ex.printStackTrace();
        } catch (InvalidConfigException ex) {
            configData = configHelper.getFactory().loadDefaults();
            System.err.println("One of the values in your configuration is not valid. "
                    + "Check to make sure you have specified the right data types.");
            ex.printStackTrace();
        }
    }

    public C getConfigData() {
        C configData = this.configData;
        if (configData == null) {
            throw new IllegalStateException("Configuration has not been loaded yet");
        }
        return configData;
    }

    public void writeConfig(C configData) {
        try (final FileOutputStream output = new FileOutputStream(filePath.toFile())) {
            configHelper.getFactory().write(configData, output);
        } catch (IOException e) {
            LOGGER.error("Something went wrong when writing Config", e);
        }
    }

}