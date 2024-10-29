package com.github.kuramastone.marketplace.storage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stores {@link ItemEntry}s for use in a marketplace gui.
 */
public class MarketplaceStorage {

    private Set<ItemEntry> itemEntries; // no duplicates allowed

    private double currentDiscount; //discount on all items

    public MarketplaceStorage(Set<ItemEntry> itemEntries, double currentDiscount) {
        this.itemEntries = itemEntries;
        this.currentDiscount = currentDiscount;
    }

    public void addItem(ItemEntry itemEntry) {
        itemEntries.add(itemEntry);
    }

    public void removeItem(ItemEntry itemEntry) {
        itemEntries.remove(itemEntry);
    }

    /**
     * @return The item's price after applying the discount
     */
    public double getPrice(ItemEntry itemEntry) {
        double original = itemEntry.getOriginalPrice();

        return original - (original * currentDiscount);
    }

    /**
     * @return Returns a copy of the {@link Set} of items in the marketplace
     */
    public Set<ItemEntry> getItemEntries() {
        return new HashSet<>(itemEntries);
    }

    /**
     * @return Returns the current discount for all items in this marketplace
     */
    public double getCurrentDiscount() {
        return currentDiscount;
    }

}