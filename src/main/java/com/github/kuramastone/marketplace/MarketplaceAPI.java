package com.github.kuramastone.marketplace;

import com.github.kuramastone.bUtilities.ComponentEditor;
import com.github.kuramastone.bUtilities.SimpleAPI;
import com.github.kuramastone.bUtilities.YamlConfig;
import com.github.kuramastone.marketplace.guis.GuiType;
import com.github.kuramastone.marketplace.player.PlayerProfile;
import com.github.kuramastone.marketplace.storage.BlackMarketSelection;
import com.github.kuramastone.marketplace.storage.ItemEntry;
import com.github.kuramastone.marketplace.storage.MarketplaceStorage;
import com.github.kuramastone.marketplace.utils.GuiManager;
import com.github.kuramastone.marketplace.utils.config.ConfigOptions;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.PagedGui;

import java.time.LocalDate;
import java.util.*;

public class MarketplaceAPI implements SimpleAPI {

    private Map<UUID, PlayerProfile> profileMap; // profiles by UUID

    private ConfigOptions configOptions; // config data
    private MarketplaceStorage marketplace;
    private MarketplaceStorage blackmarket;
    public GuiManager guiManager;

    public MarketplaceAPI() {
        profileMap = new HashMap<>();
        loadConfig();
        loadMarkets();
    }

    public PlayerProfile getOrCreateProfile(UUID uniqueId) {
        return profileMap.computeIfAbsent(uniqueId, PlayerProfile::new);
    }

    /**
     * Load markets from database
     */
    private void loadMarkets() {
        marketplace = new MarketplaceStorage(new HashSet<>(), 0.0);
        blackmarket = new MarketplaceStorage(new HashSet<>(), 0.5);
    }

    private void loadConfig() {
        configOptions = new ConfigOptions();

        YamlConfig.setLogger(Marketplace.logger);
        YamlConfig config = new YamlConfig(Marketplace.instance.getDataFolder(), "config.yml");
        config.saveAndLoadFromJar(getClass()); // TODO: Remove from release. This resets the config file each time.
        configOptions.loadConfig(this, config);
        loadGuis(config);
    }

    private void loadGuis(YamlConfig config) {
        guiManager = new GuiManager(this, config);
    }

    /**
     * Gets the message as stored in the config via key. Allows easy replacements.
     * @param key Key to search in the cached message storage. Should be found at messages.[key] in the config.yml
     * @param replacements Length must be a multiple of two. Will replace the first key with the second value
     * @throws IllegalArgumentException if replacements has a non-even length
     * @return
     */
    public ComponentEditor getMessage(String key, Object... replacements) {
        ComponentEditor edit = configOptions.messages.getOrDefault(key, new ComponentEditor(key)).copy();

        if (replacements.length % 2 != 0) {
            throw new IllegalArgumentException("A key was not provided with a replacement.");
        }

        for (int i = 0; i < replacements.length; i += 2) {
            edit = edit.replace(replacements[i + 0].toString(), replacements[i + 1].toString());
        }

        return edit;
    }

    public ConfigOptions getConfigOptions() {
        return configOptions;
    }

    public MarketplaceStorage getBlackmarket() {
        return blackmarket;
    }

    /**
     * Generates a seed for every day. Using that seed, it combs through current ItemEntries and adds them if the random value produced is below a threshold.
     */
    public void updateBlackMarket() {
        LocalDate today = LocalDate.now();
        // Convert the date to a long value to use as a seed
        long dailySeed =  12412L * today.toEpochDay();
        this.blackmarket.setEntries(BlackMarketSelection.createSelectionFor(dailySeed, configOptions.blackMarketUseRate, getMarketplace()));
    }

    public MarketplaceStorage getMarketplace() {
        return marketplace;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    /**
     * Lists item in marketplace, adds it to the player transaction history, and updates the black market
     */
    public void addItemToMarketplace(PlayerProfile profile, ItemEntry itemEntry) {
        profile.addNewTransaction(itemEntry); // add to player history
        getMarketplace().addItem(itemEntry); // add to market
        updateBlackMarket(); // potentially add to black market
    }

    /**
     * Removes money from purchasing player
     * @return True if successful. False if it was not found in database
     */
    public synchronized boolean handlePlayerPurchase(Player player, ItemEntry itemEntry, double currentDiscount) {
        removeItemFromMarkets(itemEntry);
        double price = itemEntry.getPrice(currentDiscount);

        double moneyToGiveSeller = itemEntry.getOriginalPrice(); // seller always receives full price
        double moneyToTakeFromBuyer = price;
        //TODO: Handle purchasing

        return true;
    }

    private synchronized Set<ItemEntry> downloadItemEntries() {
    }

    private synchronized void removeItemFromMarkets(ItemEntry itemEntry) {
        Set<ItemEntry> itemEntries = downloadItemEntries();
        getMarketplace().setEntries(itemEntries);
        getBlackmarket().filter(itemEntries);
        updatePlayerMarkets();
    }

    /**
     * Refresh every player's gui view
     */
    public void updatePlayerMarkets() {
        for (PlayerProfile profile : profileMap.values()) {
            if(!profile.isOnline()) {
                continue;
            }

            int page = 0;
            if(profile.getCurrentMarketGui() instanceof PagedGui<?> pagedGui) {
                page = pagedGui.getCurrentPage();
            }
            GuiType guiType = profile.getCurrentGuiType();

            if(guiType == GuiType.MARKETPLACE) {
                guiManager.showMarketplaceTo(profile.getPlayer(), page);
            }
            else if(guiType == GuiType.BLACKMARKET) {
                guiManager.showBlackmarketTo(profile.getPlayer(), page);
            }

        }
    }
}
