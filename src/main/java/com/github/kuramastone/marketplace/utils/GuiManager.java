package com.github.kuramastone.marketplace.utils;

import com.github.kuramastone.bUtilities.YamlConfig;
import com.github.kuramastone.marketplace.MarketplaceAPI;
import com.github.kuramastone.marketplace.guis.ConfirmationGUI;
import com.github.kuramastone.marketplace.guis.GuiType;
import com.github.kuramastone.marketplace.guis.MarketplaceGUI;
import com.github.kuramastone.marketplace.player.PlayerProfile;
import com.github.kuramastone.marketplace.storage.ItemEntry;
import com.github.kuramastone.marketplace.storage.MarketplaceStorage;
import com.github.kuramastone.marketplace.utils.config.GuiConfig;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.PagedGui;

public class GuiManager {

    private final MarketplaceAPI api;

    @YamlConfig.YamlObject("guis.marketplace")
    private GuiConfig marketplaceConfig;
    @YamlConfig.YamlObject("guis.blackmarket")
    private GuiConfig blackmarketConfig;
    @YamlConfig.YamlObject("guis.confirmation")
    private GuiConfig confirmationConfig;

    public GuiManager(MarketplaceAPI api, YamlConfig config) {
        this.api = api;
        YamlConfig.loadFromYaml(this, config);
    }

    public void showMarketplaceTo(Player player) {
        showMarketplaceTo(player, 0);
    }

    public void showMarketplaceTo(Player player, int page) {
        MarketplaceGUI gui = new MarketplaceGUI(api, GuiType.MARKETPLACE, api.getMarketplace(), marketplaceConfig);
        gui.show(player, page);
    }

    public void showBlackmarketTo(Player player) {
        showBlackmarketTo(player, 0);
    }

    public void showBlackmarketTo(Player player, int page) {
        MarketplaceGUI gui = new MarketplaceGUI(api, GuiType.BLACKMARKET, api.getBlackmarket(), blackmarketConfig);
        gui.show(player, page);
    }

    public void showConfirmationTo(MarketplaceStorage marketplaceStorage, Player player, ItemEntry itemToBuy, double currentDiscount) {
        ConfirmationGUI gui = new ConfirmationGUI(api, marketplaceStorage, confirmationConfig, itemToBuy, currentDiscount);
        gui.show(player);
    }

    public GuiConfig getMarketplaceConfig() {
        return marketplaceConfig;
    }

    public GuiConfig getBlackmarketConfig() {
        return blackmarketConfig;
    }

    public GuiConfig getConfirmationConfig() {
        return confirmationConfig;
    }

    public void updateProfileMarket(PlayerProfile profile) {
        MarketplaceGUI gui = profile.getCurrentMarketGui();
        if (gui != null) {
            if (gui.getGui() != null) {
                gui.getGui().setContent(gui.createContentList());
            }
        }
    }
}


















