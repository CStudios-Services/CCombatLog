package org.cstudios.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.cstudios.handlers.manager.CombatManager;
import org.cstudios.utils.Messages;
import org.cstudios.utils.config.model.YamlFile;

import java.util.Objects;

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

        if(!config.getConfig().getBoolean("COMBAT_TAG.KILL_TAGGED_ON_QUIT"))
            return;

        Player player = event.getPlayer();

        if (combat.isTagged(player)) {
            player.setHealth(0.0f);

            for (PotionEffect effect : player.getActivePotionEffects())
                player.removePotionEffect(effect.getType());

            combat.flush(player);
        }

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getPlayer();
        combat.flush(victim);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event)  {

        if(!config.getConfig().getBoolean("COMBAT_TAG.BLOCK_COMMANDS_ON_TAG"))
            return;

        Player player = event.getPlayer();

        if (combat.isTagged(player) && !player.hasPermission(Objects.requireNonNull(config.getConfig().getString("COMBAT_TAG.BLOCK_COMMANDS_ON_TAG.BYPASS_PERM")))) {

            if(config.getConfig().getStringList("COMBAT_TAG.BLOCK_COMMANDS_ON_TAG.PERMITTED_COMMANDS").contains(event.getMessage()))
                return;

            player.sendMessage(Messages.toComponent("COMBAT_TAG.RESTRICTED"));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void elytraToggle(EntityToggleGlideEvent event) {

        if(!config.getConfig().getBoolean("COMBAT_TAG.DISABLE_ELYTRA"))
           return;


        if (event.isCancelled() || !(event.getEntity() instanceof Player player))
            return;

        if (!combat.isTagged(player))
            return;

        player.sendMessage(Messages.toComponent("COMBAT_TAG.CANT_USE_ELYTRA"));
        player.setGliding(false);
        event.setCancelled(true);

        PlayerInventory inventory = player.getInventory();
        ItemStack chest = inventory.getChestplate();

        if (chest == null || chest.getType() != Material.ELYTRA)
            return;

        inventory.setChestplate(null);

        if (inventory.firstEmpty() == -1) {
            Location location = player.getLocation();
            location.getWorld().dropItemNaturally(location, chest);
            player.updateInventory();
            return;
        }

        inventory.addItem(chest);
    }


}
