package com.github.kuramastone.marketplace.guis.items;

import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

/**
 * Go forward a page
 */
public class ForwardItem extends PageItem {

    private ItemStack item;

    public ForwardItem(ItemStack item) {
        super(true);
        this.item = item;
    }

    @Override
    public ItemProvider getItemProvider(PagedGui<?> gui) {
        ItemBuilder builder = new ItemBuilder(item)
                .setDisplayName("Next page")
                .addLoreLines(gui.hasNextPage()
                        ? "Go to page " + (gui.getCurrentPage() + 2) + "/" + gui.getPageAmount()
                        : "There are no more pages");

        return builder;
    }

}