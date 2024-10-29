package com.github.kuramastone.marketplace.utils;

import com.github.kuramastone.bUtilities.YamlConfig;
import com.github.kuramastone.marketplace.utils.config.GuiConfig;

public class GuiManager {

    @YamlConfig.YamlObject
    private GuiConfig marketplaceConfig;
    @YamlConfig.YamlObject
    private GuiConfig blackmarketConfig;
    @YamlConfig.YamlObject
    private GuiConfig confirmationConfig;

    public void load(YamlConfig config) {
        YamlConfig.loadFromYaml(this, config.getSection("guis"));
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
}
