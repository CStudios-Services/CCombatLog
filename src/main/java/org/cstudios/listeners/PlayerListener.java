package org.cstudios.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.cstudios.handlers.manager.CombatManager;
import org.cstudios.utils.Messages;
import org.cstudios.utils.config.model.YamlFile;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final CombatManager combat;
    private final YamlFile config;

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof Player damaged))
            return;

        Player damager = null;

        if (event.getDamager() instanceof Player)
            damager = (Player) event.getDamager();
        else if (event.getDamager() instanceof Projectile) {
            if (((Projectile) event.getDamager()).getShooter() instanceof Player)
                damager = (Player) ((Projectile) event.getDamager()).getShooter();
        }

        if (damager == null)
            return;

        if (!combat.isTagged(damaged))
            damaged.sendMessage(Messages.toComponent("COMBAT_TAG.TAGGED"));

        if (!combat.isTagged(damager))
            damager.sendMessage(Messages.toComponent("COMBAT_TAG.TAGGED"));

        combat.tag(damaged, damager);
    }

    @EventHandler
    public void onDamageByCrystals(EntityDamageByEntityEvent event) {

        if (!config.getConfig().getBoolean("COMBAT_TAG.TAG_ON_CRYSTAL_EXPLOSION"))
            return;

        if (event.isCancelled() || !(event.getEntity() instanceof Player damaged))
            return;

        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && event.getDamager() instanceof EnderCrystal) {
            if (!combat.isTagged(damaged))
                damaged.sendMessage(Messages.toComponent("COMBAT_TAG.TAGGED"));

            combat.tag(damaged);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (combat.isTagged(player)) {
            player.setHealth(0.0f);

            for (PotionEffect effect : player.getActivePotionEffects())
                player.removePotionEffect(effect.getType());

            combat.flush(player);
        }

    }

}