package com.github.kuramastone.marketplace.utils;

import org.bukkit.ChatColor;

public class StringUtils {

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

}
