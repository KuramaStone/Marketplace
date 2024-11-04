package com.github.kuramastone.marketplace.utils;

import com.github.kuramastone.marketplace.Marketplace;
import com.github.kuramastone.marketplace.MarketplaceAPI;
import org.bukkit.scheduler.BukkitTask;

public class DatabaseRefresher {
    public static void startTimer(Marketplace marketplace) {
        MarketplaceAPI api = marketplace.getAPI();
        marketplace.getServer().getScheduler().runTaskTimer(marketplace, DatabaseRefresher::refreshFromDatabase,
                0L, api.getConfigOptions().database.getRefreshRate());
    }

    private static void refreshFromDatabase(BukkitTask object) {
        MarketplaceAPI api = Marketplace.instance.getAPI();
        api.refreshMarketsFromDatabase();
    }
}
