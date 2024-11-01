package com.github.kuramastone.marketplace.player;

import com.github.kuramastone.marketplace.guis.GuiType;
import com.github.kuramastone.marketplace.storage.ItemEntry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Used to store this plugin's information about this player.
 */
public class PlayerProfile {

    private final UUID uuid;
    private final OfflinePlayer offlinePlayer;
    private @Nullable Player player;

    private List<TransactionEntry> transactionHistory;

    // gui info
    private @Nullable Gui currentMarketGui;
    private @Nullable GuiType currentGuiType; // used to know what gui to reopen if a player goes back to it

    public PlayerProfile(UUID uuid) {
        this.uuid = uuid;
        offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        transactionHistory = new ArrayList<>();
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
    public void addNewTransaction(ItemEntry itemEntry) {
        transactionHistory.add(new TransactionEntry(itemEntry, itemEntry.getOriginalPrice(), System.currentTimeMillis()));
    }

    /**
     * Mark the corresponding transaction with this {@link ItemEntry} as completed with the appropriate info
     */
    public void completeTransaction(ItemEntry itemEntry, UUID soldTo, double purchasePrice) {
        for (TransactionEntry transactionEntry : transactionHistory) {
            if(transactionEntry.getItemEntry().equals(itemEntry)) {
                transactionEntry.setPurchasePrice(purchasePrice);
                transactionEntry.setTimePurchased(System.currentTimeMillis());
                transactionEntry.setPurchasedBy(soldTo);
            }
        }
    }

    public void setCurrentGuiType(@Nullable GuiType lastGuiTypeOpened) {
        this.currentGuiType = lastGuiTypeOpened;
    }

    @Nullable
    public GuiType getCurrentGuiType() {
        return currentGuiType;
    }

    public void setCurrentMarketGui(Gui gui) {
        this.currentMarketGui = gui;
    }

    @Nullable
    public Gui getCurrentMarketGui() {
        return currentMarketGui;
    }

    public UUID getUUID() {
        return this.uuid;
    }
}
