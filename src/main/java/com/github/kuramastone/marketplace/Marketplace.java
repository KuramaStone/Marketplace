package com.github.kuramastone.marketplace;

import com.github.kuramastone.marketplace.commands.BlackmarketCommand;
import com.github.kuramastone.marketplace.commands.MarketplaceCommand;
import com.github.kuramastone.marketplace.commands.SellCommand;
import com.github.kuramastone.marketplace.commands.TransactionHistoryCommand;
import com.github.kuramastone.marketplace.player.JoinQuitListener;
import com.github.kuramastone.marketplace.player.PlayerProfile;
import com.github.kuramastone.marketplace.storage.ItemEntry;
import com.github.kuramastone.marketplace.storage.ItemEntryData;
import com.github.kuramastone.marketplace.utils.DatabaseRefresher;
import com.github.kuramastone.marketplace.utils.VaultUtils;
import com.mongodb.MongoCommandException;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

public final class Marketplace extends JavaPlugin {

    public static Marketplace instance;
    public static Logger logger;

    private MarketplaceAPI api;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        if (!loadAPI()) {
            setEnabled(false);
            return;
        }

        registerListeners();
        registerCommands();
        registerSchedulers();
    }

    /**
     * Loads the api
     *
     * @return True if connected successfully
     */
    private boolean loadAPI() {
        try {
            VaultUtils.setupEconomy(this);
            api = new MarketplaceAPI();
            return true;
        }
        catch (MongoCommandException e) {
            logger.severe("Unable to connect to database. Verify database settings and reload the plugin.");
        }
        catch (RuntimeException e) {
            logger.severe("Unable to startup Marketplace.");
            e.printStackTrace();
        }

        return false;
    }

    private void registerListeners() {
        new JoinQuitListener(api).register(this);
    }

    private void registerSchedulers() {
        DatabaseRefresher.startTimer(this);
        //debugAddItems();
    }

    private void registerCommands() {
        new MarketplaceCommand(api, null, 0, "marketplace").register(this);
        new BlackmarketCommand(api, null, 0, "blackmarket").register(this);
        new SellCommand(api, null, 0, "sell").register(this);
        new TransactionHistoryCommand(api, null, 0, "transactions").register(this);
    }

    @Override
    public void onDisable() {
        if (api == null) {
            return;
        }

        api.uploadPlayersToDatabase();
        api.closeDatabase();
    }

    public MarketplaceAPI getAPI() {
        return api;
    }

    /**
     * Adds a random item to the market every few seconds. useful for testing
     */
    private void debugAddItems() {
        Random random = new Random();
        List<Material> validTypes = new ArrayList<>(new ArrayList<>(List.of(Material.values())).stream().filter(Material::isItem).toList());
        validTypes.removeIf(Material::isAir);
        getServer().getScheduler().runTaskTimer(this, () -> {
            PlayerProfile profile = api.getOrCreateProfile(UUID.fromString("3ea0aa33-e7d6-4f28-b2d9-6075bd5c60dc"));
            ItemStack item = new ItemStack(validTypes.get(random.nextInt(validTypes.size())));
            api.addItemToMarketplace(new ItemEntry(profile, new ItemEntryData(profile.getUUID(), item, 100, System.currentTimeMillis())));
        }, 0L, 40L);
    }
}
