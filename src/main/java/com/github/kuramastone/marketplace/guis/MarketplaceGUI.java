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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MarketplaceGUI {

    private final MarketplaceAPI api;
    private final GuiType guiType;
    private final MarketplaceStorage marketplaceStorage;
    private final GuiConfig guiInfo;
    private @Nullable PagedGui<Item> gui;

    public MarketplaceGUI(MarketplaceAPI api, GuiType guiType, MarketplaceStorage marketplaceStorage, GuiConfig guiInfo) {
        this.api = api;
        this.guiType = guiType;
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


            if ("BACK".equals(tag)) {
                // use back page item
                builder.addIngredient(c, new BackItem(ic.toItemStack()));
            }
            else if ("NEXT".equals(tag)) {
                // use back page item
                builder.addIngredient(c, new ForwardItem(ic.toItemStack()));
            }
            else {
                // default to a plain item
                builder.addIngredient(c, ic.toItemStack());
            }

        }

        // load contents
        builder.addIngredient(guiInfo.getListCharacter(), Markers.CONTENT_LIST_SLOT_HORIZONTAL);
        builder.setContent(createContentList());

        this.gui = builder.build();
        gui.setPage(page);
        api.getOrCreateProfile(player.getUniqueId()).setCurrentMarketGui(this);

        Window window = Window.single()
                .setViewer(player)
                .setTitle(new AdventureComponentWrapper(ComponentEditor.decorateComponent(guiInfo.getWindowName())))
                .setGui(gui)
                .build();

        window.open();

    }

    @Nullable
    public PagedGui<Item> getGui() {
        return gui;
    }

    public GuiType getGuiType() {
        return guiType;
    }

    public List<Item> createContentList() {
        List<ItemEntry> listEntries = new ArrayList<>(marketplaceStorage.getItemEntries());

        // sorted list of entries. Newest first
        listEntries.sort((c1, c2) -> (int) Math.signum((int) (c2.getData().getListTime() - c1.getData().getListTime())));

        List<Item> list = new ArrayList<>();
        for (ItemEntry itemEntry : listEntries) {
            list.add(new ItemEntryItem(marketplaceStorage, itemEntry, marketplaceStorage.getCurrentDiscount()));
        }

        return list;
    }

}
