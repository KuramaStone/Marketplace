package com.github.kuramastone.marketplace.guis.items;

import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

/**
 * Go back a page
 */
public class BackItem extends PageItem {

    private ItemBuilder builder;

    public BackItem(ItemBuilder builder) {
        super(false);
        this.builder = builder;
    }

    @Override
    public ItemProvider getItemProvider(PagedGui<?> gui) {
        builder.setDisplayName("Previous page")
                .addLoreLines(gui.hasPreviousPage()
                        ? "Go to page " + gui.getCurrentPage() + "/" + gui.getPageAmount()
                        : "You can't go further back");

        return builder;
    }

}