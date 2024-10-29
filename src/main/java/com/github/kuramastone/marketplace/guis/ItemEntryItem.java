package com.github.kuramastone.marketplace.guis;

import com.github.kuramastone.marketplace.storage.ItemEntry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.SimpleItem;

public class ItemEntryItem extends SimpleItem {

    private ItemEntry itemEntry;

    public ItemEntryItem(ItemEntry itemEntry) {
        super(new EntryProvider(itemEntry));
        this.itemEntry = itemEntry;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        super.handleClick(clickType, player, event);
    }

    private static class EntryProvider implements ItemProvider {

        private ItemEntry itemEntry;

        public EntryProvider(ItemEntry itemEntry) {
            this.itemEntry = itemEntry;
        }

        @Override
        public @NotNull ItemStack get(String lang) {
            ItemStack base = itemEntry.getItemstack();
            // TODO: add lore and whatever later
            return base;
        }

    }

}
