package org.cstudios.handlers.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.cstudios.handlers.data.CombatPlayer;
import org.cstudios.handlers.data.CombatRunnable;
import org.cstudios.utils.Schedule;
import org.cstudios.utils.config.model.YamlFile;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CombatManager {

    private final Map<UUID, CombatPlayer> combatPlayers = new ConcurrentHashMap<>();
    private final FileConfiguration config;
    private final BukkitTask combatTask;

    public CombatManager(YamlFile config) {
        this.config = config.getConfig();
        this.combatTask = Schedule.timer(true, new CombatRunnable(this), 1L, 1L);
    }

    public void end() {
        if(combatTask != null && !combatTask.isCancelled())
            combatTask.cancel();
    }

    public void tag(Player... players) {
        for (Player player : players)
            insert(player).refresh();
    }

    public Optional<CombatPlayer> search(Player player) {
        return Optional.ofNullable(combatPlayers.get(player.getUniqueId()));
    }

    public boolean isTagged(Player player) {
        Optional<CombatPlayer> search = search(player);
        return search.isPresent() && !search.get().isExpired();
    }

    public void delete(UUID uniqueId) {
        combatPlayers.remove(uniqueId);
    }

    public long getTagDuration() {
        return config.getInt("COMBAT_TAG.TIME") * 1000L;
    }

    public void flush(Player... players) {
        for (Player player : players) {

            if(player == null || !player.isOnline())
                continue;

            delete(player.getUniqueId());
        }
    }

    public Collection<CombatPlayer> collect() {
        return combatPlayers.values();
    }

    public CombatPlayer insert(Player player) {
        UUID uniqueId = player.getUniqueId();
        return combatPlayers.computeIfAbsent(uniqueId, k -> new CombatPlayer(this, uniqueId));
    }
}

