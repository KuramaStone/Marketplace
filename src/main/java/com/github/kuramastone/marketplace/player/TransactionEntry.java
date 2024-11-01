package com.github.kuramastone.marketplace.player;

import com.github.kuramastone.marketplace.storage.ItemEntry;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Used to track the transaction history of this particular {@link ItemEntry}
 */
public class TransactionEntry {

    private final ItemEntry itemEntry; // item
    private final double listPrice; // price the player listed this as
    private final long timeSubmitted; // time it was first sent to the market

    private @Nullable UUID purchasedBy;
    private double purchasePrice; // price that it was bought at
    private long timePurchased; // time that it was bought

    public TransactionEntry(ItemEntry itemEntry, double listPrice, long timeSubmitted) {
        this.itemEntry = itemEntry;
        this.listPrice = listPrice;
        this.timeSubmitted = timeSubmitted;
    }

    public TransactionEntry(ItemEntry itemEntry, double listPrice) {
        this.itemEntry = itemEntry;
        this.listPrice = listPrice;
        this.timeSubmitted = System.currentTimeMillis();
    }

    public TransactionEntry(ItemEntry itemEntry, double listPrice, long timeSubmitted, UUID purchasedBy, double purchasePrice, long timePurchased) {
        this.itemEntry = itemEntry;
        this.listPrice = listPrice;
        this.timeSubmitted = timeSubmitted;
        this.purchasedBy = purchasedBy;
        this.purchasePrice = purchasePrice;
        this.timePurchased = timePurchased;
    }

    public ItemEntry getItemEntry() {
        return itemEntry;
    }

    public double getListPrice() {
        return listPrice;
    }

    public long getTimeSubmitted() {
        return timeSubmitted;
    }

    public UUID getPurchasedBy() {
        return purchasedBy;
    }

    public void setPurchasedBy(UUID purchasedBy) {
        this.purchasedBy = purchasedBy;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public long getTimePurchased() {
        return timePurchased;
    }

    public void setTimePurchased(long timePurchased) {
        this.timePurchased = timePurchased;
    }
}
