package org.cstudios.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Strings {

    private static final Pattern COLOR_PATTERN = Pattern.compile("<#([0-9A-Fa-f]{6})>([^<]+)</#([0-9A-Fa-f]{6})>");
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(Locale.ENGLISH);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0");

    private Strings() {
        throw new AssertionError("Cannot instantiate utils class.");
    }

    public static String timer(long end) {
        return DECIMAL_FORMAT.format(Math.max(0.0, (end - System.currentTimeMillis()) / 1000.0));
    }

    public static String replaceAll(String string, String target, String replacement) {
        if (target.length() != replacement.length())
            return string;

        char[]
           targetArray = target.toCharArray(),
           replaceArray = replacement.toCharArray();

        for (int i = 0; i < target.length(); i++)
            string = string.replace(targetArray[i], replaceArray[i]);

        return string;
    }

    public static String colorShade(String str, Color first, Color second) {
        if (first.equals(second))
            return str;

        StringBuilder builder = new StringBuilder();
        char[] chars = str.toCharArray();
        int max = chars.length;

        for (int scale = 0; scale < max; scale++) {
            double
               weight1 = (double) (max - (scale + 1)) / max,
               based = 1.0 - weight1;

            int
               red = (int) (first.getRed() * weight1 + second.getRed() * based),
               green = (int) (first.getGreen() * weight1 + second.getGreen() * based),
               blue = (int) (first.getBlue() * weight1 + second.getBlue() * based);

            red = Math.max(0, Math.min(255, red));
            green = Math.max(0, Math.min(255, green));
            blue = Math.max(0, Math.min(255, blue));

            String color = colorToHex(new Color(red, green, blue));
            builder.append("<").append(color).append(">").append(chars[scale]).append("</").append(color).append(">");
        }

        return builder.toString();
    }

    private static String colorToHex(Color color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    public static String shade(String string) {
        StringBuilder builder = new StringBuilder();
        Matcher matcher = COLOR_PATTERN.matcher(string);
        boolean hasDone = false;

        while (matcher.find()) {
            if (!hasDone)
                builder.append(string, 0, matcher.start());

            hasDone = true;

            System.out.println(string.substring(matcher.start(), matcher.end()));

            String
               startColorHex = matcher.group(1),
               endColorHex = matcher.group(3),
               enclosedText = matcher.group(2);

            Color
               firstColor = Color.decode("#" + startColorHex),
               secondColor = Color.decode("#" + endColorHex);

            if (firstColor.equals(secondColor)) {
                builder.append("<#").append(startColorHex).append(">").append(enclosedText).append("</#").append(endColorHex).append(">");
                continue;
            }

            builder.append(colorShade(enclosedText, firstColor, secondColor));
        }

        return hasDone ? builder.toString() : string;
    }

    public static String capitalize(String str) {
        char[] chars = str.toCharArray();
        Character previous = null;

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            if (previous == null || previous == ' ') {
                previous = c;
                chars[i] = String.valueOf(c).toUpperCase().charAt(0);
                continue;
            }

            previous = c;
        }

        return String.valueOf(chars);
    }

    public static String material(Material material) {
        return Strings.capitalize(material.name().toLowerCase().replace("_", " "));
    }

    @Deprecated
    public static String colors(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static Boolean forceBoolean(String str) {
        try {
            return Boolean.parseBoolean(str);
        }catch (Exception ignored) {
            return null;
        }
    }

    public static Long forceLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Integer forceInteger(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static String formatBalance(long value) {
        return (value == 0 ? "0" : NUMBER_FORMAT.format(value).replace(",", "."));
    }

    public static String formatDollars(long value) {
        return "$" + (value == 0 ? "0" : NUMBER_FORMAT.format(value));
    }

}
