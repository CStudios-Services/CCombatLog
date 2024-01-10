package org.cstudios.handlers.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.cstudios.handlers.manager.CombatManager;

import java.util.UUID;

@RequiredArgsConstructor
public class CombatPlayer {

    private final CombatManager factory;

    @Getter
    private final UUID uuid;
    @Getter
    private long end;


    public Player player() {
        return Bukkit.getPlayer(uuid);
    }

    public void refresh() {
        end = System.currentTimeMillis() + factory.getTagDuration();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > end;
    }
}
