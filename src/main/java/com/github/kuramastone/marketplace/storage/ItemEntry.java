package com.github.kuramastone.marketplace.storage;

import com.github.kuramastone.marketplace.player.PlayerProfile;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

/**
 * Stores Itemstacks as an entry in the {@link MarketplaceStorage}
 */
public class ItemEntry {

    private final PlayerProfile profile;
    private final ItemEntryData entryData;

    public ItemEntry(PlayerProfile profile, ItemEntryData data) {
        this.profile = profile;
        entryData = data;
    }

    /**
     * @return {@link PlayerProfile} who made this entry
     */
    public PlayerProfile getProfile() {
        return profile;
    }


    /**
     * @return Clone of {@link ItemStack} stored in entry
     */
    public ItemStack getItemstack() {
        return entryData.getItemstack();
    }

    /**
     * @return Asking price for this item by the player.
     */
    public double getOriginalPrice() {
        return entryData.getOriginalPrice();
    }

    public double getPrice(double currentDiscount) {
        return Math.max(0, (getOriginalPrice()) - (getOriginalPrice() * currentDiscount));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemEntry itemEntry = (ItemEntry) o;
        return Objects.equals(profile, itemEntry.profile) && Objects.equals(entryData, itemEntry.entryData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profile, entryData);
    }

    public ItemEntryData getData() {
        return this.entryData;
    }
}
