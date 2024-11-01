package com.github.kuramastone.marketplace.guis.items;

import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

/**
 * Go forward a page
 */
public class ForwardItem extends PageItem {

    private ItemBuilder builder;

    public ForwardItem(ItemBuilder builder) {
        super(true);
        this.builder = builder;
    }

    @Override
    public ItemProvider getItemProvider(PagedGui<?> gui) {
        builder.setDisplayName("Next page")
            .addLoreLines(gui.hasNextPage()
                ? "Go to page " + (gui.getCurrentPage() + 2) + "/" + gui.getPageAmount()
                : "There are no more pages");

        return builder;
    }

}