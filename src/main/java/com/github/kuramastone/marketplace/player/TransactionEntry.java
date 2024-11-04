package com.github.kuramastone.marketplace.player;

import com.github.kuramastone.marketplace.storage.ItemEntry;
import com.github.kuramastone.marketplace.storage.ItemEntryData;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Used to track the transaction history of this particular {@link ItemEntry}
 */
public class TransactionEntry {

    private final ItemEntryData itemEntryData; // item
    private final double listPrice; // price the player listed this as
    private final long timeSubmitted; // time it was first sent to the market

    private @Nullable UUID purchasedBy;
    private double purchasePrice; // price that it was bought at
    private long timePurchased; // time that it was bought

    public TransactionEntry(ItemEntryData itemEntryData, double listPrice, long timeSubmitted) {
        this.itemEntryData = itemEntryData;
        this.listPrice = listPrice;
        this.timeSubmitted = timeSubmitted;
    }

    public TransactionEntry(ItemEntryData itemEntryData, double listPrice) {
        this.itemEntryData = itemEntryData;
        this.listPrice = listPrice;
        this.timeSubmitted = System.currentTimeMillis();
    }

    public TransactionEntry(ItemEntryData itemEntryData, double listPrice, long timeSubmitted, UUID purchasedBy, double purchasePrice, long timePurchased) {
        this.itemEntryData = itemEntryData;
        this.listPrice = listPrice;
        this.timeSubmitted = timeSubmitted;
        this.purchasedBy = purchasedBy;
        this.purchasePrice = purchasePrice;
        this.timePurchased = timePurchased;
    }

    public ItemEntryData getItemEntryData() {
        return itemEntryData;
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

    public boolean hasBeenSold() {
        return this.purchasedBy != null;
    }

}
