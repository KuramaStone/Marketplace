package com.github.kuramastone.marketplace.storage;

import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

public class ItemEntryData {
    /**
     * UUID of player who owns it
     */
    private final UUID sellerUUID;
    /**
     * UUID of this unique entry
     */
    private final UUID entryUUID;
    /**
     * ItemStack to sell
     */
    private final ItemStack itemstack;
    /**
     * Listed price by player
     */
    private final double originalPrice;
    /**
     * Listed time in milliseconds
     */
    private final long listTime;

    public ItemEntryData(UUID entryUUID, UUID sellerUUID, ItemStack itemstack, double originalPrice, long listTime) {
        this.sellerUUID = sellerUUID;
        this.entryUUID = entryUUID;
        this.itemstack = itemstack;
        this.originalPrice = originalPrice;
        this.listTime = listTime;
    }

    public ItemEntryData(UUID sellerUUID, ItemStack itemstack, double originalPrice, long listTime) {
        this(UUID.randomUUID(), sellerUUID,  itemstack, originalPrice, listTime);
    }

    /**
     * Get the UUID of player who owns this item
     * @return
     */
    public UUID getSellerUUID() {
        return sellerUUID;
    }

    /**
     * Get the UUID of player who owns this item
     * @return
     */
    public UUID getEntryUUID() {
        return entryUUID;
    }

    /**
     * Clone of itemstack that this represnts
     * @return
     */
    public ItemStack getItemstack() {
        return itemstack.clone();
    }

    /**
     * Listed sell price by player
     * @return
     */
    public double getOriginalPrice() {
        return originalPrice;
    }

    /**
     * Returns the time the seller listed this item
     * @return
     */
    public long getListTime() {
        return listTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemEntryData that = (ItemEntryData) o;
        return Objects.equals(entryUUID, that.entryUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entryUUID);
    }
}
