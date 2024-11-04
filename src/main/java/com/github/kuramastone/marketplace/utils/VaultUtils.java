package com.github.kuramastone.marketplace.utils;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class VaultUtils {

    private static Economy economy;

    public static void setupEconomy(JavaPlugin plugin) {
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
            return;
        }

        throw new RuntimeException("Unable to load Vault economy. Install Vault AND an Economy plugin, then restart.");
    }

    public static Economy getEconomy() {
        return economy;
    }

}
