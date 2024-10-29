package com.github.kuramastone.marketplace;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Marketplace extends JavaPlugin {

    public static Marketplace instance;
    public static Logger logger;

    private MarketplaceAPI api;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        api = new MarketplaceAPI();
    }

    @Override
    public void onDisable() {
    }

    public MarketplaceAPI getAPI() {
        return api;
    }
}
