package com.github.kuramastone.marketplace.guis;

import com.github.kuramastone.bUtilities.ComponentEditor;
import com.github.kuramastone.marketplace.MarketplaceAPI;
import com.github.kuramastone.marketplace.guis.items.ItemEntryItem;
import com.github.kuramastone.marketplace.guis.items.RunnableItem;
import com.github.kuramastone.marketplace.player.PlayerProfile;
import com.github.kuramastone.marketplace.storage.ItemEntry;
import com.github.kuramastone.marketplace.storage.MarketplaceStorage;
import com.github.kuramastone.marketplace.utils.PurchaseResult;
import com.github.kuramastone.marketplace.utils.config.GuiConfig;
import com.github.kuramastone.marketplace.utils.config.ItemConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.window.Window;

import java.util.Map;

public class ConfirmationGUI {

    private MarketplaceAPI api;
    private GuiConfig guiInfo;
    private ItemEntry itemEntry; // item seeking confirmation to buy
    private double currentDiscount;
    private MarketplaceStorage marketplaceStorage;

    private boolean locked = false;

    public ConfirmationGUI(MarketplaceAPI api, MarketplaceStorage marketplaceStorage, GuiConfig guiInfo, ItemEntry itemEntry, double currentDiscount) {
        this.api = api;
        this.marketplaceStorage = marketplaceStorage;
        this.guiInfo = guiInfo;
        this.itemEntry = itemEntry;
        this.currentDiscount = currentDiscount;
    }

    /**
     * Build a unique gui for this player that shows the marketplace.
     *
     * @param player
     */
    public void show(Player player) {

        PagedGui.Builder<Item> builder = PagedGui.items()
                .setStructure(guiInfo.getStructure());

        // load ingredients
        for (Map.Entry<Character, ItemConfig> set : guiInfo.getIngredients().entrySet()) {
            char c = set.getKey();
            ItemConfig ic = set.getValue();
            String tag = ic.getTag();

            if ("ACCEPT".equals(tag)) {
                // accept transaction on click
                builder.addIngredient(c, new RunnableItem(new ItemWrapper(ic.toItemStack()), this::handleAccept));
            }
            else if ("DENY".equals(tag)) {
                // deny transaction on click
                builder.addIngredient(c, new RunnableItem(new ItemWrapper(ic.toItemStack()), this::handleDeny));
            }
            else if ("ITEM_TO_CONFIRM".equals(tag)) {
                // This is the item showcasing their purchase
                builder.addIngredient(c, new ItemEntryItem(marketplaceStorage, itemEntry, true, currentDiscount));
            }
            else {
                // default to a plain item
                builder.addIngredient(c, ic.toItemStack());
            }

        }

        Window window = Window.single()
                .setViewer(player)
                .setTitle(new AdventureComponentWrapper(ComponentEditor.decorateComponent(guiInfo.getWindowName())))
                .setGui(builder.build())
                .build();

        window.open();

    }

    private synchronized void handleAccept(ClickType clickType, Player player, InventoryClickEvent inventoryClickEvent) {
        if (locked)
            return;
        locked = true;

        PurchaseResult result = api.handlePlayerPurchase(player, itemEntry, currentDiscount);

        if (result == PurchaseResult.SUCCESS) {
            // return to previous gui
            showPreviousPage(player);
        }
        else {
            player.closeInventory();
            if (result == PurchaseResult.ALREADY_BOUGHT)
                player.sendMessage(api.getMessage("commands.already purchased").build());
            else if (result == PurchaseResult.NOT_ENOUGH_MONEY)
                player.sendMessage(api.getMessage("commands.not enough money").build());
        }


    }

    private synchronized void handleDeny(ClickType clickType, Player player, InventoryClickEvent inventoryClickEvent) {
        if (locked)
            return;
        locked = true;

        // return to previous gui
        showPreviousPage(player);
    }

    private void showPreviousPage(Player player) {
        PlayerProfile profile = api.getOrCreateProfile(player.getUniqueId());
        Gui gui = profile.getCurrentMarketGui().getGui();
        int page = 0;
        if (gui instanceof PagedGui<?>) {
            page = ((PagedGui<?>) gui).getCurrentPage();
        }
        if (profile.getCurrentMarketGui().getGuiType() == GuiType.MARKETPLACE) {
            api.getGuiManager().showMarketplaceTo(player, page);
        }
        else if (profile.getCurrentMarketGui().getGuiType() == GuiType.BLACKMARKET) {
            api.getGuiManager().showBlackmarketTo(player, page);
        }
    }

}
