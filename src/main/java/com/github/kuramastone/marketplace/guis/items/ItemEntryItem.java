package com.github.kuramastone.marketplace.guis.items;

import com.github.kuramastone.bUtilities.ComponentEditor;
import com.github.kuramastone.marketplace.Marketplace;
import com.github.kuramastone.marketplace.storage.ItemEntry;
import com.github.kuramastone.marketplace.storage.MarketplaceStorage;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.SimpleItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an ItemEntry from {@link com.github.kuramastone.marketplace.storage.MarketplaceStorage} as a display item
 */
public class ItemEntryItem extends SimpleItem {

    private MarketplaceStorage marketplaceStorage;
    private ItemEntry itemEntry;
    private double currentDiscount;
    private boolean ignoreClicks;

    public ItemEntryItem(MarketplaceStorage marketplaceStorage, ItemEntry itemEntry, double currentDiscount) {
        this(marketplaceStorage, itemEntry, false, currentDiscount);
    }

    public ItemEntryItem(MarketplaceStorage marketplaceStorage, ItemEntry itemEntry, boolean ignoreClicks, double currentDiscount) {
        super(new EntryProvider(itemEntry, currentDiscount));
        this.itemEntry = itemEntry;
        this.ignoreClicks = ignoreClicks;
        this.currentDiscount = currentDiscount;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (ignoreClicks) {
            return;
        }

        if (itemEntry.getProfile().getUUID().equals(player.getUniqueId())) {
            // cant purchase your own items
            //return;
        }

        Marketplace.instance.getAPI()
                .getGuiManager().showConfirmationTo(marketplaceStorage, player, itemEntry, currentDiscount);

        super.handleClick(clickType, player, event);
    }

    private static class EntryProvider implements ItemProvider {

        private ItemEntry itemEntry;
        private double currentDiscount;

        public EntryProvider(ItemEntry itemEntry, double currentDiscount) {
            this.itemEntry = itemEntry;
            this.currentDiscount = currentDiscount;
        }

        @Override
        public @NotNull ItemStack get(String lang) {
            ItemStack base = itemEntry.getItemstack();

            List<Component> header = new ArrayList<>();
            List<Component> originalLore = base.lore();
            List<Component> fullLore = new ArrayList<>();

            double price = itemEntry.getPrice(currentDiscount);
            for (String line : Marketplace.instance.getAPI().getConfigOptions().marketplaceItemHeaders) {
                header.add(ComponentEditor.decorateComponent(line.replace("{amount}", "%.2f".formatted(price))));
            }

            fullLore.addAll(header);
            if (originalLore != null)
                fullLore.addAll(originalLore);

            base.lore(fullLore);

            return base;
        }

    }

}
