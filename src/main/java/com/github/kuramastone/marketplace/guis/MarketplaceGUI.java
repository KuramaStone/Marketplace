package com.github.kuramastone.marketplace.guis;

import com.github.kuramastone.marketplace.MarketplaceAPI;
import com.github.kuramastone.marketplace.storage.ItemEntry;
import com.github.kuramastone.marketplace.storage.MarketplaceStorage;
import com.github.kuramastone.marketplace.utils.config.GuiConfig;
import com.github.kuramastone.marketplace.utils.config.ItemConfig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MarketplaceGUI {

    private MarketplaceAPI api;
    private GuiConfig guiInfo;

    public MarketplaceGUI(MarketplaceAPI api) {
        this.api = api;
        this.guiInfo = api.getConfigOptions().guiManager.getMarketplaceConfig();
    }

    /**
     * Build a unique gui for this player that shows the marketplace.
     * @param player
     */
    public void show(Player player) {

        PagedGui.Builder builder = PagedGui.items()
                .setStructure(guiInfo.getStructure());

        for (Map.Entry<Character, ItemConfig> set : guiInfo.getIngredients().entrySet()) {
            char c = set.getKey();
            ItemConfig ic = set.getValue();
            builder.addIngredient(c, ic.toItemStack());
        }

        List<ItemStack> list = new ArrayList<>();
        MarketplaceStorage market = api.getMarketplace();
        for (ItemEntry itemEntry : market.getItemEntries()) {
            list.add(itemEntry.getItemstack());
        }

        builder.addIngredient(guiInfo.getListCharacter(), Markers.CONTENT_LIST_SLOT_HORIZONTAL);
        builder.setContent(list);

        Window window = Window.single()
                .setViewer(player)
                .setTitle(guiInfo.getWindowName())
                .setGui(builder.build())
                .build();

        window.open();

    }

}
