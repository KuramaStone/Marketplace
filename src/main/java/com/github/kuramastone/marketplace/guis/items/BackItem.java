package com.github.kuramastone.marketplace.guis.items;

import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

/**
 * Go back a page
 */
public class BackItem extends PageItem {

    private ItemStack item;

    public BackItem(ItemStack item) {
        super(false);
        this.item = item;
    }

    @Override
    public ItemProvider getItemProvider(PagedGui<?> gui) {
        ItemBuilder builder = new ItemBuilder(item)
                .setDisplayName("Previous page")
                .addLoreLines(gui.hasPreviousPage()
                        ? "Go to page " + gui.getCurrentPage() + "/" + gui.getPageAmount()
                        : "You can't go further back");

        return builder;
    }

}