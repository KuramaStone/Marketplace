package com.github.kuramastone.marketplace.guis;

import com.github.kuramastone.bUtilities.ComponentEditor;
import com.github.kuramastone.marketplace.MarketplaceAPI;
import com.github.kuramastone.marketplace.guis.items.BackItem;
import com.github.kuramastone.marketplace.guis.items.ForwardItem;
import com.github.kuramastone.marketplace.guis.items.ItemEntryItem;
import com.github.kuramastone.marketplace.storage.ItemEntry;
import com.github.kuramastone.marketplace.storage.MarketplaceStorage;
import com.github.kuramastone.marketplace.utils.config.GuiConfig;
import com.github.kuramastone.marketplace.utils.config.ItemConfig;
import org.bukkit.entity.Player;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MarketplaceGUI {

    private MarketplaceAPI api;
    private MarketplaceStorage marketplaceStorage;
    private GuiConfig guiInfo;

    public MarketplaceGUI(MarketplaceAPI api, MarketplaceStorage marketplaceStorage, GuiConfig guiInfo) {
        this.api = api;
        this.marketplaceStorage = marketplaceStorage;
        this.guiInfo = guiInfo;
    }

    /**
     * Build a unique gui for this player that shows the marketplace.
     *
     * @param player
     * @param page
     */
    public void show(Player player, int page) {

        PagedGui.Builder<Item> builder = PagedGui.items()
                .setStructure(guiInfo.getStructure());

        // load ingredients
        for (Map.Entry<Character, ItemConfig> set : guiInfo.getIngredients().entrySet()) {
            char c = set.getKey();
            ItemConfig ic = set.getValue();
            String tag = ic.getTag();


            if("BACK".equals(tag)) {
                // use back page item
                builder.addIngredient(c, new BackItem(new ItemBuilder(ic.toItemStack())));
            }
            else if("NEXT".equals(tag)) {
                // use back page item
                builder.addIngredient(c, new ForwardItem(new ItemBuilder(ic.toItemStack())));
            }
            else {
                // default to a plain item
                builder.addIngredient(c, ic.toItemStack());
            }

        }

        // load contents
        List<Item> list = new ArrayList<>();
        for (ItemEntry itemEntry : marketplaceStorage.getItemEntries()) {
            list.add(new ItemEntryItem(marketplaceStorage, itemEntry, marketplaceStorage.getCurrentDiscount()));
        }

        builder.addIngredient(guiInfo.getListCharacter(), Markers.CONTENT_LIST_SLOT_HORIZONTAL);
        builder.setContent(list);

        PagedGui gui = builder.build();
        gui.setPage(page);
        api.getOrCreateProfile(player.getUniqueId()).setCurrentMarketGui(gui);

        Window window = Window.single()
                .setViewer(player)
                .setTitle(new AdventureComponentWrapper(ComponentEditor.decorateComponent(guiInfo.getWindowName())))
                .setGui(gui)
                .build();

        window.open();

    }

}
