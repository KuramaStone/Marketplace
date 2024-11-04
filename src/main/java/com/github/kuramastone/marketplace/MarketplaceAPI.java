package com.github.kuramastone.marketplace;

import com.github.kuramastone.bUtilities.ComponentEditor;
import com.github.kuramastone.bUtilities.SimpleAPI;
import com.github.kuramastone.bUtilities.YamlConfig;
import com.github.kuramastone.marketplace.database.ItemService;
import com.github.kuramastone.marketplace.database.MongoService;
import com.github.kuramastone.marketplace.database.PlayerService;
import com.github.kuramastone.marketplace.player.PlayerProfile;
import com.github.kuramastone.marketplace.storage.BlackMarketSelection;
import com.github.kuramastone.marketplace.storage.ItemEntry;
import com.github.kuramastone.marketplace.storage.ItemEntryData;
import com.github.kuramastone.marketplace.storage.MarketplaceStorage;
import com.github.kuramastone.marketplace.utils.*;
import com.github.kuramastone.marketplace.utils.config.ConfigOptions;
import com.mongodb.MongoCommandException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;

public class MarketplaceAPI implements SimpleAPI {

    private Map<UUID, PlayerProfile> profileMap; // profiles by UUID

    private ConfigOptions configOptions; // config data
    private MarketplaceStorage marketplace; // local cache of data for the marketplace
    private MarketplaceStorage blackmarket; // local cache of data for the blackmarket
    public GuiManager guiManager; // manage guis

    private MongoService mongoService; // maintains the connection to the database
    private ItemService itemService; // manager for items
    private PlayerService playerService; // manger for player profiles

    public MarketplaceAPI() throws MongoCommandException {
        profileMap = new HashMap<>();
        loadConfig();
        loadDatabase();
        downloadPlayerProfiles();
        loadMarkets();
    }

    private void loadDatabase() throws MongoCommandException {
        String connectionString = configOptions.database.getConnectionString();
        String dbName = configOptions.database.getDatabaseName();
        this.mongoService = new MongoService(connectionString, dbName);
        mongoService.connectOrThrow();

        this.itemService = new ItemService(mongoService);
        this.playerService = new PlayerService(mongoService);
    }

    public PlayerProfile getOrCreateProfile(UUID uniqueId) {
        if (this.profileMap.containsKey(uniqueId))
            return profileMap.get(uniqueId);


        // check database
        PlayerProfile profile = playerService.downloadProfileByUUID(uniqueId);

        if (profile != null)
            return profile;

        profile = profileMap.computeIfAbsent(uniqueId, PlayerProfile::new);
        playerService.uploadProfile(profile);

        return profile;
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
     *
     * @param key          Key to search in the cached message storage. Should be found at messages.[key] in the config.yml
     * @param replacements Length must be a multiple of two. Will replace the first key with the second value
     * @return
     * @throws IllegalArgumentException if replacements has a non-even length
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
     * Generates a seed for every day. Using that seed, it combs through current ItemEntries in the marketplace cache
     * and adds them if the random value produced is below a threshold.
     */
    public void updateBlackMarket() {
        LocalDate today = LocalDate.now();
        // Convert the date to a long value to use as a seed
        long dailySeed = 12412L * today.toEpochDay();
        this.blackmarket.setEntries(BlackMarketSelection.createSelectionFor(dailySeed, configOptions.blackMarketUseRate, this.marketplace.getItemEntries()));
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
    public void addItemToMarketplace(ItemEntry itemEntry) {
        itemService.uploadItem(itemEntry.getData());
        getMarketplace().addItem(itemEntry); // add to market
        updateBlackMarket(); // potentially add to black market
        updatePlayerMarkets(); // update player guis
    }

    /**
     * Downloads all market data from the database and refreshes the markets
     *
     * @return
     */
    public synchronized Set<ItemEntry> refreshMarketsFromDatabase() {
        Set<ItemEntryData> dataSet = itemService.downloadAvailableItems();

        Set<ItemEntry> entries = new HashSet<>();
        for (ItemEntryData data : dataSet) {
            entries.add(new ItemEntry(getOrCreateProfile(data.getSellerUUID()), data));
        }

        marketplace.setEntries(entries);
        updateBlackMarket();

        return entries;
    }

    /**
     * Removes money from purchasing player
     *
     * @return True if successful. False if it was not found in database
     */
    public synchronized PurchaseResult handlePlayerPurchase(Player player, ItemEntry itemEntry, double currentDiscount) {

        double price = itemEntry.getPrice(currentDiscount);
        double moneyToTakeFromBuyer = price;
        double moneyToGiveSeller = itemEntry.getOriginalPrice(); // seller always receives full price

        OfflinePlayer buyer = player;
        OfflinePlayer seller = Bukkit.getOfflinePlayer(itemEntry.getData().getSellerUUID());

        boolean hasEnoughMoney = VaultUtils.getEconomy().getBalance(buyer) >= moneyToTakeFromBuyer;
        if (!hasEnoughMoney) {
            return PurchaseResult.NOT_ENOUGH_MONEY;
        }

        // if the purchase can proceed, buy the item
        boolean isValidOnDatabase = itemService.buyItem(itemEntry.getData().getEntryUUID());

        if (!isValidOnDatabase) {
            return PurchaseResult.ALREADY_BOUGHT;
        }
        removeItemFromMarkets(itemEntry);
        ItemStack item = itemEntry.getItemstack();

        // handle money
        VaultUtils.getEconomy().withdrawPlayer(buyer, moneyToTakeFromBuyer);
        VaultUtils.getEconomy().depositPlayer(seller, moneyToGiveSeller);
        getProfile(itemEntry.getData().getSellerUUID()).completeTransaction(itemEntry.getData(), player.getUniqueId(), moneyToGiveSeller);

        // give item to purchasing player
        ItemUtils.giveOrDropItem(player, item);

        // handle discord webhook
        if(configOptions.discordConfig.enabled) {
            // build message to send the discord webhook
            String messageToSend =
                    configOptions.discordConfig.discordWebhookContent
                            .replace("{seller}", seller.getName())
                            .replace("{buyer}", buyer.getName())
                            .replace("{material}", ItemUtils.getItemStackName(item))
                            .replace("{count}", String.valueOf(item.getAmount()))
                            .replace("{price}", NumberFormat.getInstance().format(moneyToTakeFromBuyer));


            try {
                DiscordWebhook.sendMessage(configOptions.discordConfig, messageToSend);
            }
            catch (IOException e) {
                configOptions.discordConfig.enabled = false;
                Marketplace.logger.severe("Unable to send discord webhook. Disabling future attempts to send.");
                e.printStackTrace();
            }
        }

        return PurchaseResult.SUCCESS;
    }

    private synchronized void removeItemFromMarkets(ItemEntry itemEntry) {
        removeItemEntryFromDatabase(itemEntry);

        boolean r1 = getMarketplace().remove(itemEntry);
        boolean r2 = getBlackmarket().remove(itemEntry);
        updatePlayerMarkets();
    }

    /**
     * Remove the item from the database
     *
     * @param itemEntry
     */
    private void removeItemEntryFromDatabase(ItemEntry itemEntry) {
        itemService.removeItem(itemEntry.getData());
    }

    /**
     * Refresh every player's gui view
     */
    public void updatePlayerMarkets() {
        for (PlayerProfile profile : profileMap.values()) {
            if (!profile.isOnline()) {
                continue;
            }

            guiManager.updateProfileMarket(profile);
        }
    }

    public void closeDatabase() {
        mongoService.close();
    }

    public PlayerProfile getProfile(UUID target) {
        return this.profileMap.getOrDefault(target, null);
    }

    public void uploadPlayersToDatabase() {
        for (PlayerProfile profile : this.profileMap.values()) {
            playerService.uploadProfile(profile);
        }
    }

    public void downloadPlayerProfiles() {
        this.profileMap.clear();
        for (PlayerProfile profile : playerService.downloadProfiles()) {
            this.profileMap.put(profile.getUUID(), profile);
        }
    }

    public void uploadPlayerProfile(PlayerProfile profile) {
        this.playerService.uploadProfile(profile);
    }
}
