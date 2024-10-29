package com.github.kuramastone.marketplace;

import com.github.kuramastone.bUtilities.ComponentEditor;
import com.github.kuramastone.bUtilities.SimpleAPI;
import com.github.kuramastone.bUtilities.YamlConfig;
import com.github.kuramastone.marketplace.storage.MarketplaceStorage;
import com.github.kuramastone.marketplace.utils.GuiManager;
import com.github.kuramastone.marketplace.utils.config.ConfigOptions;

import java.util.HashSet;
import java.util.Map;

public class MarketplaceAPI implements SimpleAPI {

    private ConfigOptions configOptions; // config data
    private MarketplaceStorage marketplace;
    private MarketplaceStorage blackmarket;

    public MarketplaceAPI() {
        loadConfig();
        loadMarkets();
    }

    /**
     * Load markets from database
     */
    private void loadMarkets() {
        marketplace = new MarketplaceStorage(new HashSet<>(), 0.0);
        blackmarket = new MarketplaceStorage(new HashSet<>(), 0.5);
    }

    private void loadConfig() {
        configOptions = new ConfigOptions();

        YamlConfig config = new YamlConfig(Marketplace.instance.getDataFolder(), "config.yml");
        configOptions.loadConfig(config);
    }

    /**
     * Gets the message as stored in the config via key. Allows easy replacements.
     * @param key Key to search in the cached message storage. Should be found at messages.[key] in the config.yml
     * @param replacements Length must be a multiple of two. Will replace the first key with the second value
     * @throws IllegalArgumentException if replacements has a non-even length
     * @return
     */
    public ComponentEditor getMessage(String key, Object... replacements) {
        ComponentEditor edit = configOptions.messages.getOrDefault(key, new ComponentEditor(key)).copy();

        if (replacements.length % 2 != 0) {
            throw new IllegalArgumentException("A key was not provided with a replacement.");
        }

        for (int i = 0; i < replacements.length; i += 2) {
            edit = edit.replace(replacements[i + 0].toString(), replacements[i + 1].toString());
        }

        return edit;
    }

    public ConfigOptions getConfigOptions() {
        return configOptions;
    }

    public MarketplaceStorage getBlackmarket() {
        return blackmarket;
    }

    public MarketplaceStorage getMarketplace() {
        return marketplace;
    }
}
