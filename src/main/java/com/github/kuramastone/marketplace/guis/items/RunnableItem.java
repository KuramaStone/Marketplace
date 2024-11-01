package com.github.kuramastone.marketplace.guis.items;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

/**
 * Go back a page
 */
public class RunnableItem extends SimpleItem {

    private ClickRunnable clickRunnable;

    public RunnableItem(ItemProvider provider, ClickRunnable clickRunnable) {
        super(provider);
        this.clickRunnable = clickRunnable;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        clickRunnable.handleClick(clickType, player, event);
    }

    public interface ClickRunnable {
        void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event);
    }

}