package org.cstudios;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.cstudios.handlers.manager.CombatManager;
import org.cstudios.listeners.PlayerListener;
import org.cstudios.utils.config.Configuration;
import org.cstudios.utils.config.struct.Config;

public final class CCombatLog extends JavaPlugin {

    @Getter
    private static CCombatLog instance;
    @Getter
    private Configuration configuration;

    @Getter
    private CombatManager combatManager;

    @Override
    public void onEnable() {

        instance = this;

        getLogger().info(" ");
        getLogger().info("CCombatLog Enabling - CStudios");
        getLogger().info(" ");

        getLogger().info("Loading modules...");
        initHandlers();

        getLogger().info("Registering listeners...");
        registerListeners();
    }

    @Override
    public void onDisable() {
        this.combatManager.end();
    }

    private void initHandlers() {
        this.configuration = new Configuration(instance);
        this.combatManager = new CombatManager(configuration.find(Config.CONFIG));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this.combatManager, configuration.find(Config.CONFIG)), this);
    }
}
