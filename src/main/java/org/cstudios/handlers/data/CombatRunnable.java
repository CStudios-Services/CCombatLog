package org.cstudios.handlers.data;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.cstudios.handlers.manager.CombatManager;
import org.cstudios.utils.Messages;
import org.cstudios.utils.Strings;

@RequiredArgsConstructor
public class CombatRunnable implements Runnable {
    private final CombatManager factory;

    @Override
    public void run() {
        for (CombatPlayer player : factory.collect()) {
            Player bukkit = player.player();

            if (player.isExpired()) {
                factory.flush(bukkit);
                bukkit.sendMessage(Messages.toComponent("COMBAT_TAG.UNTAGGED"));
                bukkit.sendActionBar(Component.empty());
                continue;
            }

            bukkit.sendActionBar(Messages.toComponent("COMBAT_TAG.ACTION_BAR", "time_left", Strings.timer(factory.search(bukkit).map(CombatPlayer::getEnd).orElse(0L))));
        }
    }
}
