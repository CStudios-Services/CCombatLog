package org.cstudios.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.cstudios.CCombatLog;
import org.cstudios.utils.config.struct.Config;

import java.util.List;

public class Messages {

    public static String plainText(String path) {
        return CCombatLog.getInstance().getConfiguration().find(Config.LANGUAGE).getConfig().getString(path);
    }

    public static Component toComplexComponent(String messagePath, String hoverPath, String command, Object... params) {
        return
                Messages.toComponent(messagePath, params)
                        .hoverEvent(HoverEvent.showText(Messages.toComponent(hoverPath, params)))
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, command));
    }

    public static Component direct(Player player, String text, Object... params) {
        if (params.length % 2 == 1)
            throw new IllegalArgumentException("Params length should not be odd.");

        for (int i = 0; i < params.length; i += 2)
            text = text.replace("{" + params[i].toString() + "}", params[i + 1].toString());

        if (player != null)
            text = PlaceholderAPI.setPlaceholders(player, text);

        return MiniMessage.miniMessage().deserialize(text);
    }

    public static Component direct(String text, Object... params) {
        return direct(null, text, params);
    }

    public static Component fromSection(Player player, ConfigurationSection section, String path, Object... params) {
        if (params.length % 2 == 1)
            throw new IllegalArgumentException("Params length should not be odd.");

        Object object = section.get(path);
        String result;

        if (object instanceof List) result = String.join("\n", (List<String>) object);
        else result = object.toString();

        return direct(player, result, params);
    }

    public static Component fromSection(ConfigurationSection section, String path, Object... params) {
        return fromSection(null, section, path, params);
    }

    public static String convertColors(String s) {
        s = s.replaceAll("&0", "<black>")
                .replaceAll("&1", "<dark_blue>")
                .replaceAll("&2", "<dark_green>")
                .replaceAll("&3", "<dark_aqua>")
                .replaceAll("&4", "<dark_red>")
                .replaceAll("&5", "<dark_purple>")
                .replaceAll("&6", "<gold>")
                .replaceAll("&7", "<gray>")
                .replaceAll("&8", "<dark_gray>")
                .replaceAll("&9", "<blue>")
                .replaceAll("&a", "<green>")
                .replaceAll("&b", "<aqua>")
                .replaceAll("&c", "<red>")
                .replaceAll("&d", "<light_purple>")
                .replaceAll("&e", "<yellow>")
                .replaceAll("&f", "<white>")
                .replaceAll("&m", "<st>")
                .replaceAll("&k", "<obf>")
                .replaceAll("&o", "<italic>")
                .replaceAll("&l", "<bold>")
                .replaceAll("&r", "<reset>");

        return s;
    }

    public static Component toComponent(Player target, String path, Object... params) {
        if (params.length % 2 == 1)
            throw new IllegalArgumentException("Params length should not be odd.");

        FileConfiguration config = CCombatLog.getInstance().getConfiguration().find(Config.LANGUAGE).getConfig();
        Object object = config.get(path);

        if (object == null)
            return Component.empty();

        String result;

        if (config instanceof List) result = String.join("\n", (List<String>) config);
        else result = object.toString();

        return direct(target, result, params);
    }

    public static Component toComponent(String path, Object... params) {
        if (params.length % 2 == 1)
            throw new IllegalArgumentException("Params length should not be odd.");

        FileConfiguration storage = CCombatLog.getInstance().getConfiguration().find(Config.LANGUAGE).getConfig();
        Object config = storage.get(path);

        if (config == null)
            return Component.empty();

        String result;

        // Asserting List's generic type is 'String'
        if (config instanceof List) result = String.join("\n", (List<String>) config);
        else result = config.toString();

        // Don't send empty messages.
        if (result.isBlank() || result.isEmpty())
            return Component.empty();

        for (int i = 0; i < params.length; i += 2)
            result = result.replace("{" + params[i].toString() + "}", params[i + 1].toString());

        return MiniMessage.miniMessage().deserialize(result);
    }

    public static String formatTime(long totalSeconds) {
        long days = totalSeconds / (24 * 60 * 60);
        long hours = (totalSeconds / (60 * 60)) % 24;
        long minutes = (totalSeconds / 60) % 60;
        long seconds = totalSeconds % 60;

        return (days > 0 ? ((days >= 10)
           ? days
           : ("0" + days))
           + ":" : "") + (hours > 0 ? ((hours >= 10)
           ? hours
           : ("0" + hours))
           + ":" : "") + ((minutes >= 10)
           ? minutes
           : ("0" + minutes))
           + ":" + ((seconds >= 10)
           ? seconds
           : ("0" + seconds));
    }

    public static String formatFromSeconds(long durationSeconds) {
        // Compute the number of days, hours, minutes, and seconds
        long days = durationSeconds / (24 * 60 * 60);
        long hours = (durationSeconds / (60 * 60)) % 24;
        long minutes = (durationSeconds / 60) % 60;
        long seconds = durationSeconds % 60;

        // Adjust seconds, minutes, and hours if necessary
        if (seconds >= 60) {
            minutes += seconds / 60;
            seconds %= 60;
        }
        if (minutes >= 60) {
            hours += minutes / 60;
            minutes %= 60;
        }
        if (hours >= 24) {
            days += hours / 24;
            hours %= 24;
        }

        // Construct the formatted string
        StringBuilder formattedDuration = new StringBuilder();

        if (days > 0) {
            if (days == 1) {
                formattedDuration.append(days).append(" Giorno, ");
            } else {
                formattedDuration.append(days).append(" Giorni, ");
            }
        }
        if (hours > 0) {
            if (hours == 1) {
                formattedDuration.append(hours).append(" Ora, ");
            } else {
                formattedDuration.append(hours).append(" Ore, ");
            }
        }
        if (minutes > 0) {
            if (minutes == 1) {
                formattedDuration.append(minutes).append(" Minuto, ");
            } else {
                formattedDuration.append(minutes).append(" Minuti, ");
            }
        }

        if (seconds == 1) {
            formattedDuration.append(seconds).append(" Secondo");
        } else {
            formattedDuration.append(seconds).append(" Secondi");
        }

        return formattedDuration.toString();
    }
}
