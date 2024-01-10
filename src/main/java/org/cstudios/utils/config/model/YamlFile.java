package org.cstudios.utils.config.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

@RequiredArgsConstructor
public class YamlFile {

    private final File file;
    @Getter private FileConfiguration config;

    public YamlFile(Plugin plugin, String name) {
        name = name + ".yml";
        file = new File(plugin.getDataFolder(), name);

        if (!file.exists())
            plugin.saveResource(name, false);

        reload();
    }

    public void save() {
        try {
            config.save(file);
        }catch (IOException ignored) {
            // Scrivi quello che cazzo ti pare
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

}
