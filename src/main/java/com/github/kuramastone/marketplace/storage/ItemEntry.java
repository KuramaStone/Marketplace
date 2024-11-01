package com.github.kuramastone.marketplace.storage;

import com.github.kuramastone.marketplace.player.PlayerProfile;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

/**
 * Stores Itemstacks as an entry in the {@link MarketplaceStorage}
 */
public class ItemEntry {

    private final UUID uuid;

    private final PlayerProfile profile;
    private final ItemStack itemstack;
    private double originalPrice;

    public ItemEntry(PlayerProfile profile, ItemStack itemstack, double originalPrice) {
        this.profile = profile;
        this.itemstack = itemstack;
        this.originalPrice = originalPrice;
        uuid = UUID.randomUUID();
    }

    public ItemEntry(UUID uuid, PlayerProfile profile, ItemStack itemstack, double originalPrice) {
        this.uuid = uuid;
        this.profile = profile;
        this.itemstack = itemstack;
        this.originalPrice = originalPrice;
    }

    /**
     * @return {@link PlayerProfile} who made this entry
     */
    public PlayerProfile getProfile() {
        return profile;
    }


    /**
     * @return {@link ItemStack} stored in entry
     */
    public ItemStack getItemstack() {
        return itemstack.clone();
    }

    /**
     * @return Asking price for this item by the player.
     */
    public double getOriginalPrice() {
        return originalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemEntry itemEntry = (ItemEntry) o;
        return Double.compare(originalPrice, itemEntry.originalPrice) == 0 && Objects.equals(uuid, itemEntry.uuid) && Objects.equals(profile, itemEntry.profile) && Objects.equals(itemstack, itemEntry.itemstack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, profile, itemstack, originalPrice);
    }

    public double getPrice(double currentDiscount) {
        return Math.max(0, (this.originalPrice) - (this.originalPrice * currentDiscount));
    }
}
