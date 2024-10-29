package com.github.kuramastone.marketplace.storage;

import com.github.kuramastone.marketplace.player.PlayerProfile;
import org.bukkit.inventory.ItemStack;

/**
 * Stores Itemstacks as an entry in the {@link MarketplaceStorage}
 */
public class ItemEntry {

    private final PlayerProfile profile;
    private final ItemStack itemstack;
    private double originalPrice;

    public ItemEntry(PlayerProfile profile, ItemStack itemstack) {
        this.profile = profile;
        this.itemstack = itemstack;
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

}
