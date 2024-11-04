package com.github.kuramastone.marketplace.player;

import com.github.kuramastone.marketplace.guis.GuiType;
import com.github.kuramastone.marketplace.guis.MarketplaceGUI;
import com.github.kuramastone.marketplace.storage.ItemEntry;
import com.github.kuramastone.marketplace.storage.ItemEntryData;
import com.github.kuramastone.marketplace.utils.config.ItemConfig;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Used to store this plugin's information about this player.
 */
public class PlayerProfile {

    private final OfflinePlayer offlinePlayer;
    private @Nullable Player player;

    private final UUID uuid;
    private List<TransactionEntry> transactionHistory;

    // gui info
    private @Nullable MarketplaceGUI currentMarketGui;

    public PlayerProfile(UUID uuid) {
        this(uuid, new ArrayList<>());
    }

    public PlayerProfile(UUID uuid, List<TransactionEntry> transactionHistory) {
        this.uuid = uuid;
        this.transactionHistory = transactionHistory;
        offlinePlayer = Bukkit.getOfflinePlayer(uuid);
    }

    public boolean isOnline() {
        return offlinePlayer.isOnline();
    }

    /**
     * Load a cached instance of {@link Player} if they are online.
     */
    public Player getPlayer() {
        if(isOnline()) {
            player = offlinePlayer.getPlayer();
            return player;
        }

        player = null;
        return null;
    }

    /**
     * Add a unpurchased {@link TransactionEntry} to the player's history.
     * @param itemEntry
     */
    public TransactionEntry addNewTransaction(ItemEntry itemEntry, long listTime) {
        TransactionEntry te = new TransactionEntry(itemEntry.getData(), itemEntry.getOriginalPrice(), listTime);
        transactionHistory.add(te);
        return te;
    }

    /**
     * Mark the corresponding transaction with this {@link ItemEntry} as completed with the appropriate info
     */
    public void completeTransaction(ItemEntryData itemEntryData, UUID soldTo, double purchasePrice) {
        for (TransactionEntry transactionEntry : transactionHistory) {
            if(transactionEntry.getItemEntryData().equals(itemEntryData)) {
                transactionEntry.setPurchasePrice(purchasePrice);
                transactionEntry.setTimePurchased(System.currentTimeMillis());
                transactionEntry.setPurchasedBy(soldTo);
            }
        }
    }

    public void setCurrentMarketGui(MarketplaceGUI gui) {
        this.currentMarketGui = gui;
    }

    @Nullable
    public MarketplaceGUI getCurrentMarketGui() {
        return currentMarketGui;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public List<TransactionEntry> getTransactionHistory() {
        return transactionHistory;
    }

    public OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerProfile that = (PlayerProfile) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}
