package org.cstudios.utils.config;

import org.bukkit.plugin.Plugin;
import org.cstudios.utils.config.model.YamlFile;
import org.cstudios.utils.config.struct.Config;

import java.util.*;

public class Configuration {

    private final Map<Config, YamlFile> configurations = new EnumMap<>(Config.class);
    
    public Configuration(Plugin plugin) {
        Arrays.stream(Config.values()).forEach(config -> configurations.put(config, new YamlFile(plugin, config.getName())));
    }

    public void reload() {
        collect().forEach(YamlFile::reload);
    }

    public YamlFile find(Config config) {
        return configurations.get(config);
    }

    public Collection<YamlFile> collect() {
        return List.copyOf(configurations.values());
    }

}
