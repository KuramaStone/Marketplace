package com.github.kuramastone.marketplace;

import com.github.kuramastone.marketplace.commands.BlackmarketCommand;
import com.github.kuramastone.marketplace.commands.MarketplaceCommand;
import com.github.kuramastone.marketplace.commands.SellCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.error.Mark;

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

        registerCommands();
    }

    private void registerCommands() {
        new MarketplaceCommand(api, null, 0, "marketplace").register(this);
        new BlackmarketCommand(api, null, 0, "blackmarket").register(this);
        new SellCommand(api, null, 0, "sell").register(this);
    }

    @Override
    public void onDisable() {
    }

    public MarketplaceAPI getAPI() {
        return api;
    }
}
